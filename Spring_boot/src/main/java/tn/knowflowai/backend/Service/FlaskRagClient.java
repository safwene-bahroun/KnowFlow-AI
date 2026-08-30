package tn.knowflowai.backend.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import tn.knowflowai.backend.DTO.FlaskRagRequest;
import tn.knowflowai.backend.DTO.FlaskRagResponse;

@Service
public class FlaskRagClient {

    private final RestTemplate restTemplate;

    private final String flaskRagUrl;

    private final String internalApiKey;

    public FlaskRagClient(

            RestTemplate restTemplate,

            @Value("${flask.rag.url}")
            String flaskRagUrl,

            @Value("${app.internal-api-key}")
            String internalApiKey

    ) {

        this.restTemplate =
                restTemplate;

        this.flaskRagUrl =
                flaskRagUrl;

        this.internalApiKey =
                internalApiKey;
    }

    public FlaskRagResponse ask(

            String question,

            List<Long>
                    allowedDocumentIds

    ) {

        FlaskRagRequest request =

                new FlaskRagRequest(

                        question,

                        allowedDocumentIds
                );


        HttpHeaders headers =

                new HttpHeaders();

        headers.setContentType(

                MediaType.APPLICATION_JSON
        );

        headers.set(

                "X-Internal-API-Key",

                internalApiKey
        );


        HttpEntity<FlaskRagRequest> entity =

                new HttpEntity<>(

                        request,

                        headers
                );


        ResponseEntity<FlaskRagResponse> response =

                restTemplate.postForEntity(

                        flaskRagUrl,

                        entity,

                        FlaskRagResponse.class
                );


        if (

                response.getBody() == null

        ) {

            throw new RuntimeException(

                    "Flask RAG returned an empty response"
            );
        }


        return response.getBody();
    }
}