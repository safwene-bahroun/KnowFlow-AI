from app.services.embedding_service import EmbeddingService
from app.services.spring_client import SpringClient
from app.repositories.vector_repository import VectorRepository


class IngestionService:

    @staticmethod
    def ingest_document(
        document_id,
        file_path,
        metadata
    ):

        try:

            # 1. Extract document text
            documents = (
                EmbeddingService
                .load_document(
                    file_path
                )
            )


            # 2. Split document
            chunks = (
                EmbeddingService
                .split_documents(
                    documents
                )
            )


            # 3. Prepare chunks for Spring Boot
            chunks_for_spring = []

            for index, chunk in enumerate(chunks):

                content = (
                    chunk.page_content
                )

                token_count = len(
                    content.split()
                )


                chunks_for_spring.append(
                    {
                        "chunkIndex": index,
                        "content": content,
                        "tokenCount": token_count
                    }
                )


            # 4. Save chunks in PostgreSQL
            SpringClient.save_chunks(
                document_id,
                chunks_for_spring
            )


            # 5. Save embeddings in ChromaDB
            VectorRepository.add_documents(
                document_id,
                chunks,
                metadata
            )


            # 6. Update status
            SpringClient.update_document_status(
                document_id,
                "PROCESSED"
            )


            return {
                "success": True,
                "documentId": document_id,
                "chunkCount": len(chunks)
            }


        except Exception as error:

            try:

                SpringClient.update_document_status(
                    document_id,
                    "FAILED"
                )

            except Exception:
                pass


            raise error