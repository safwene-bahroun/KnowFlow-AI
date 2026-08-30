package tn.knowflowai.backend.Service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import tn.knowflowai.backend.DTO.DocumentIngestRequest;
import tn.knowflowai.backend.DTO.FlaskRagRequest;
import tn.knowflowai.backend.DTO.FlaskRagResponse;

@Service
public class FlaskRagClient {

    private final RestTemplate restTemplate;
    private final String flaskRagUrl;
    private final String flaskIngestUrl;
    private final String internalApiKey;

    public FlaskRagClient(
            RestTemplate restTemplate,
            @Value("${flask.rag.url:http://localhost:5001/api/rag/query}")
            String flaskRagUrl,
            @Value("${flask.ingest.url:http://localhost:5001/api/documents/ingest}")
            String flaskIngestUrl,
            @Value("${app.internal-api-key:KNOWFLOW_SECRET_KEY}")
            String internalApiKey
    ) {
        this.restTemplate = restTemplate;
        this.flaskRagUrl = flaskRagUrl;
        this.flaskIngestUrl = flaskIngestUrl;
        this.internalApiKey = internalApiKey;
    }

    public FlaskRagResponse ask(
            String question,
            List<Long> allowedDocumentIds
    ) {
        FlaskRagRequest request = new FlaskRagRequest(question, allowedDocumentIds);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Internal-API-Key", internalApiKey);

        HttpEntity<FlaskRagRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<FlaskRagResponse> response = restTemplate.postForEntity(
                flaskRagUrl,
                entity,
                FlaskRagResponse.class
        );

        if (response.getBody() == null) {
            throw new RuntimeException("Flask RAG returned an empty response");
        }

        return response.getBody();
    }

    public Map<String, Object> ingestDocument(DocumentIngestRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Internal-API-Key", internalApiKey);

            HttpEntity<DocumentIngestRequest> entity = new HttpEntity<>(request, headers);

            @SuppressWarnings("unchecked")
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    flaskIngestUrl,
                    entity,
                    Map.class
            );

            return (Map<String, Object>) response.getBody();
        } catch (Exception e) {
            System.err.println("Failed to trigger Flask ingestion for document " + request.getDocumentId() + ": " + e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }

    public void deleteDocumentVectors(Long documentId) {
        if (documentId == null) return;
        try {
            String baseUrl = flaskIngestUrl.replace("/ingest", "");
            String deleteUrl = baseUrl + "/" + documentId;

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Internal-API-Key", internalApiKey);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            restTemplate.exchange(deleteUrl, HttpMethod.DELETE, entity, Void.class);
        } catch (Exception e) {
            System.err.println("Failed to delete Flask vectors for document " + documentId + ": " + e.getMessage());
        }
    }
}