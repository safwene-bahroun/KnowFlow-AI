from langchain_chroma import Chroma

from app.config import Config
from app.services.embedding_service import EmbeddingService


class VectorRepository:

    _vector_store = None

    @classmethod
    def get_vector_store(cls):

        if cls._vector_store is None:

            embeddings = (
                EmbeddingService
                .get_embeddings()
            )

            cls._vector_store = Chroma(
                collection_name=
                Config.CHROMA_COLLECTION_NAME,

                persist_directory=
                Config.CHROMA_PERSIST_DIRECTORY,

                embedding_function=
                embeddings
            )

        return cls._vector_store


    @classmethod
    def add_documents(
        cls,
        documents,
        ids
    ):

        vector_store = (
            cls.get_vector_store()
        )

        vector_store.add_documents(
            documents=documents,
            ids=ids
        )


    @classmethod
    def delete_document(
        cls,
        document_id
    ):

        vector_store = (
            cls.get_vector_store()
        )

        results = vector_store.get(
            where={
                "document_id": document_id
            }
        )

        ids = results.get(
            "ids",
            []
        )

        if ids:

            vector_store.delete(
                ids=ids
            )


    @classmethod
    def similarity_search(
        cls,
        question,
        allowed_document_ids,
        k=5
    ):

        vector_store = (
            cls.get_vector_store()
        )

        results = vector_store.get(
            where={
                "document_id": {
                    "$in": allowed_document_ids
                }
            }
        )

        candidate_ids = results.get(
            "ids",
            []
        )

        if not candidate_ids:

            return []

        documents = vector_store.get(
            ids=candidate_ids,
            include=[
                "documents",
                "metadatas"
            ]
        )

        from langchain_core.documents import Document

        candidates = []

        for index, content in enumerate(
            documents["documents"]
        ):

            candidates.append(
                Document(
                    page_content=content,

                    metadata=
                    documents["metadatas"][index]
                )
            )

        embeddings = (
            EmbeddingService
            .get_embeddings()
        )

        query_vector = (
            embeddings.embed_query(
                question
            )
        )

        scored_documents = []

        for document in candidates:

            document_vector = (
                embeddings.embed_query(
                    document.page_content
                )
            )

            score = sum(
                a * b
                for a, b
                in zip(
                    query_vector,
                    document_vector
                )
            )

            scored_documents.append(
                (
                    score,
                    document
                )
            )

        scored_documents.sort(
            key=lambda item: item[0],
            reverse=True
        )

        return [
            item[1]
            for item in
            scored_documents[:k]
        ]