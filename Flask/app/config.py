import os

from dotenv import load_dotenv


load_dotenv()


class Config:

    HOST = os.getenv(
        "HOST",
        "0.0.0.0"
    )

    PORT = int(
        os.getenv(
            "PORT",
            5001
        )
    )

    CHROMA_PERSIST_DIRECTORY = os.getenv(
        "CHROMA_PERSIST_DIRECTORY",
        "chroma_db"
    )

    CHROMA_COLLECTION_NAME = os.getenv(
        "CHROMA_COLLECTION_NAME",
        "knowflow_documents"
    )

    EMBEDDING_MODEL = os.getenv(
        "EMBEDDING_MODEL",
        "sentence-transformers/all-MiniLM-L6-v2"
    )

    OLLAMA_BASE_URL = os.getenv(
        "OLLAMA_BASE_URL",
        "http://localhost:11434"
    )

    OLLAMA_MODEL = os.getenv(
        "OLLAMA_MODEL",
        "llama3.2"
    )

    SPRING_BOOT_URL = os.getenv(
        "SPRING_BOOT_URL",
        "http://localhost:3000"
    )

    INTERNAL_API_KEY = os.getenv(
        "INTERNAL_API_KEY"
    )