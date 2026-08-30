import os
import tempfile
import requests
from typing import Dict, Any
from langchain_core.documents import Document

from app.config import Config
from app.services.embedding_service import EmbeddingService
from app.services.spring_client import SpringClient
from app.repositories.vector_repository import VectorRepository


class IngestionService:

    @staticmethod
    def _resolve_file_path(url: str) -> str:
        """
        Resolves the local file path from URL or downloads it to a temp file.
        """
        clean_url = url.strip()
        filename = os.path.basename(clean_url)

        # Check local directories first
        local_candidates = [
            clean_url,
            os.path.join(os.getcwd(), clean_url.lstrip("/\\")),
            os.path.join(os.getcwd(), "..", "Spring_boot", clean_url.lstrip("/\\")),
            os.path.join(os.getcwd(), "..", "Spring_boot", "files", filename),
            os.path.join("c:\\Users\\GIGABYTE\\Desktop\\KnowFlow_AI\\Spring_boot\\files", filename)
        ]

        for candidate in local_candidates:
            if os.path.exists(candidate) and os.path.isfile(candidate):
                return candidate

        # If not found locally, download from Spring Boot static URL
        download_url = clean_url if clean_url.startswith("http") else f"{Config.SPRING_BOOT_URL}{clean_url if clean_url.startswith('/') else '/' + clean_url}"
        print(f"Downloading file from {download_url}...")
        
        response = requests.get(download_url, timeout=60)
        response.raise_for_status()

        suffix = os.path.splitext(filename)[1] or ".bin"
        with tempfile.NamedTemporaryFile(delete=False, suffix=suffix) as temp_file:
            temp_file.write(response.content)
            return temp_file.name

    @classmethod
    def ingest_document(cls, document_data: Dict[str, Any]) -> Dict[str, Any]:
        document_id = int(document_data["document_id"])
        url = document_data["url"]
        name = document_data.get("name", "Document")
        dept_id = document_data.get("department_id")
        visibility = document_data.get("visibility", "")

        temp_downloaded_path = None

        try:
            # 1. Update status to PROCESSING in Spring Boot
            try:
                SpringClient.update_document_status(document_id, "PROCESSING")
            except Exception as e:
                print(f"Warning: Could not update status to PROCESSING: {e}")

            # 2. Resolve file path
            file_path = cls._resolve_file_path(url)
            if "tmp" in file_path.lower() or "temp" in file_path.lower():
                temp_downloaded_path = file_path

            # 3. Extract text
            loaded_docs = EmbeddingService.load_document(file_path)
            if not loaded_docs:
                raise ValueError("No text content could be extracted from the document")

            # 4. Split into chunks
            split_chunks = EmbeddingService.split_documents(loaded_docs)
            if not split_chunks:
                raise ValueError("Document splitting resulted in zero chunks")

            # 5. Prepare chunks for Spring Boot PostgreSQL
            chunks_for_spring = []
            vector_docs = []

            for index, chunk in enumerate(split_chunks):
                content = chunk.page_content.strip()
                if not content:
                    continue

                token_count = len(content.split())

                chunks_for_spring.append({
                    "chunkIndex": index,
                    "content": content,
                    "tokenCount": token_count
                })

                chunk_metadata = {
                    "document_id": document_id,
                    "document_name": name,
                    "chunk_index": index,
                    "department_id": int(dept_id) if dept_id is not None else 0,
                    "visibility": str(visibility or "")
                }

                vector_docs.append(Document(
                    page_content=content,
                    metadata=chunk_metadata
                ))

            # 6. Save chunks in PostgreSQL
            if chunks_for_spring:
                SpringClient.save_chunks(document_id, chunks_for_spring)

            # 7. Delete previous vector embeddings if any, then add new ones
            VectorRepository.delete_document(document_id)
            if vector_docs:
                VectorRepository.add_documents(vector_docs)

            # 8. Update status to PROCESSED in Spring Boot
            SpringClient.update_document_status(document_id, "PROCESSED")

            return {
                "success": True,
                "document_id": document_id,
                "chunk_count": len(vector_docs)
            }

        except Exception as error:
            print(f"Error ingesting document {document_id}: {error}")
            try:
                SpringClient.update_document_status(document_id, "FAILED")
            except Exception:
                pass
            raise error

        finally:
            if temp_downloaded_path and os.path.exists(temp_downloaded_path):
                try:
                    os.remove(temp_downloaded_path)
                except Exception:
                    pass