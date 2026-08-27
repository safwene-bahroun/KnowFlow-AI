from langchain_ollama import ChatOllama

from app.config import Config


class LLMService:

    _llm = None

    @classmethod
    def get_llm(cls):

        if cls._llm is None:

            cls._llm = ChatOllama(

                model=
                Config.OLLAMA_MODEL,

                base_url=
                Config.OLLAMA_BASE_URL,

                temperature=0
            )

        return cls._llm