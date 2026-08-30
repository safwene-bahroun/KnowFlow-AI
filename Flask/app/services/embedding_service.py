from langchain_huggingface import HuggingFaceEmbeddings

from app.config import Config


class EmbeddingService:

    _embeddings = None

    @classmethod
    def get_embeddings(cls):

        if cls._embeddings is None:

            cls._embeddings = HuggingFaceEmbeddings(
                model_name=Config.EMBEDDING_MODEL,
                model_kwargs={
                    "device": "cuda"
                },
                encode_kwargs={
                    "normalize_embeddings": True
                }
            )

        return cls._embeddings