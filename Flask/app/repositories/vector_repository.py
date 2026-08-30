from typing import List, Optional
from langchain_chroma import Chroma
from langchain_core.documents import Document

from app.config import Config
from app.services.embedding_service import EmbeddingService


class VectorRepository:

    _vector_store = None

    @classmethod
    def get_vector_store(cls) -> Chroma:
        if cls._vector_store is None:
            embeddings = EmbeddingService.get_embeddings()

            cls._vector_store = Chroma(
                collection_name=Config.CHROMA_COLLECTION_NAME,
                persist_directory=Config.CHROMA_PERSIST_DIRECTORY,
                embedding_function=embeddings
            )

        return cls._vector_store

    @classmethod
    def add_documents(cls, documents: List[Document], ids: Optional[List[str]] = None):
        vector_store = cls.get_vector_store()
        if not documents:
            return

        if ids is None:
            ids = [
                f"doc_{doc.metadata.get('document_id', 0)}_chunk_{doc.metadata.get('chunk_index', i)}"
                for i, doc in enumerate(documents)
            ]

        vector_store.add_documents(
            documents=documents,
            ids=ids
        )

    @classmethod
    def delete_document(cls, document_id: int):
        vector_store = cls.get_vector_store()
        try:
            results = vector_store.get(
                where={"document_id": int(document_id)}
            )
            ids = results.get("ids", [])
            if ids:
                vector_store.delete(ids=ids)
        except Exception as e:
            print(f"Error deleting vector document {document_id}: {e}")

    @classmethod
    def similarity_search(cls, question: str, allowed_document_ids: List[int], k: int = 5) -> List[Document]:
        vector_store = cls.get_vector_store()

        if not allowed_document_ids:
            return []

        doc_ids = [int(x) for x in allowed_document_ids]

        filter_dict = (
            {"document_id": {"$in": doc_ids}}
            if len(doc_ids) > 1
            else {"document_id": doc_ids[0]}
        )

        try:
            return vector_store.similarity_search(
                query=question,
                k=k,
                filter=filter_dict
            )
        except Exception as e:
            print(f"Chroma similarity search error: {e}")
            return []