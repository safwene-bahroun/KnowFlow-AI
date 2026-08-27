from app.repositories.vector_repository import (
    VectorRepository
)

from app.services.llm_service import (
    LLMService
)

from app.prompts.rag_prompt import (
    RAG_PROMPT
)


class RagService:

    def ask(
        self,
        question,
        allowed_document_ids
    ):

        relevant_documents = (
            VectorRepository
            .similarity_search(
                question=question,

                allowed_document_ids=
                allowed_document_ids,

                k=5
            )
        )

        if not relevant_documents:

            return {
                "answer":
                "I could not find relevant information "
                "in the documents you are authorized "
                "to access.",

                "sources": []
            }

        context = self.build_context(
            relevant_documents
        )

        llm = (
            LLMService.get_llm()
        )

        chain = (
            RAG_PROMPT
            | llm
        )

        response = chain.invoke({

            "context": context,

            "question": question
        })

        sources = self.build_sources(
            relevant_documents
        )

        return {

            "answer":
            response.content,

            "sources":
            sources
        }


    def build_context(
        self,
        documents
    ):

        context_parts = []

        for document in documents:

            document_id = (
                document.metadata.get(
                    "document_id"
                )
            )

            document_name = (
                document.metadata.get(
                    "document_name"
                )
            )

            chunk_index = (
                document.metadata.get(
                    "chunk_index"
                )
            )

            context_parts.append(
                f"""
SOURCE:
Document ID: {document_id}
Document Name: {document_name}
Chunk Index: {chunk_index}

CONTENT:
{document.page_content}
"""
            )

        return "\n\n".join(
            context_parts
        )


    def build_sources(
        self,
        documents
    ):

        unique_sources = {}

        for document in documents:

            document_id = (
                document.metadata.get(
                    "document_id"
                )
            )

            if document_id not in unique_sources:

                unique_sources[
                    document_id
                ] = {

                    "document_id":
                    document_id,

                    "document_name":
                    document.metadata.get(
                        "document_name"
                    )
                }

        return list(
            unique_sources.values()
        )