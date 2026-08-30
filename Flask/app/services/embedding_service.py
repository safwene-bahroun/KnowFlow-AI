import os
import torch
from langchain_huggingface import HuggingFaceEmbeddings
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_core.documents import Document
from langchain_community.document_loaders import PyPDFLoader, TextLoader, Docx2txtLoader

from app.config import Config


class EmbeddingService:

    _embeddings = None

    @classmethod
    def get_embeddings(cls):
        if cls._embeddings is None:
            device = "cuda" if torch.cuda.is_available() else "cpu"
            cls._embeddings = HuggingFaceEmbeddings(
                model_name=Config.EMBEDDING_MODEL,
                model_kwargs={
                    "device": device
                },
                encode_kwargs={
                    "normalize_embeddings": True
                }
            )
        return cls._embeddings

    @classmethod
    def load_document(cls, file_path: str):
        """
        Loads document content from file_path and returns a list of Langchain Document objects.
        Supports PDF, DOCX, TXT, CSV, MD, etc.
        """
        if not os.path.exists(file_path):
            raise FileNotFoundError(f"File not found: {file_path}")

        ext = os.path.splitext(file_path)[1].lower()

        try:
            if ext == ".pdf":
                loader = PyPDFLoader(file_path)
                return loader.load()
            elif ext in [".docx", ".doc"]:
                loader = Docx2txtLoader(file_path)
                return loader.load()
            elif ext in [".txt", ".md", ".csv", ".json"]:
                loader = TextLoader(file_path, encoding="utf-8")
                return loader.load()
            else:
                # Fallback: read as plain text
                try:
                    with open(file_path, "r", encoding="utf-8") as f:
                        text = f.read()
                except UnicodeDecodeError:
                    with open(file_path, "r", encoding="latin-1") as f:
                        text = f.read()
                return [Document(page_content=text, metadata={"source": file_path})]
        except Exception as e:
            # Secondary fallback using raw text extraction
            try:
                if ext == ".pdf":
                    import pypdf
                    reader = pypdf.PdfReader(file_path)
                    pages_text = []
                    for i, page in enumerate(reader.pages):
                        t = page.extract_text()
                        if t:
                            pages_text.append(Document(page_content=t, metadata={"source": file_path, "page": i}))
                    if pages_text:
                        return pages_text
                elif ext in [".docx", ".doc"]:
                    import docx2txt
                    text = docx2txt.process(file_path)
                    if text:
                        return [Document(page_content=text, metadata={"source": file_path})]
            except Exception:
                pass
            raise RuntimeError(f"Failed to load document from {file_path}: {e}")

    @classmethod
    def split_documents(cls, documents, chunk_size=1000, chunk_overlap=200):
        """
        Splits list of Documents into chunks.
        """
        text_splitter = RecursiveCharacterTextSplitter(
            chunk_size=chunk_size,
            chunk_overlap=chunk_overlap,
            length_function=len,
            is_separator_regex=False
        )
        return text_splitter.split_documents(documents)