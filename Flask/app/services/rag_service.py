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
        """
        Main entry point for KnowFlow AI.

        There are three possible situations:

        1. User has NO authorized documents:
           allowed_document_ids = []

           -> Normal LLM
           -> No ChromaDB search
           -> No document context
           -> No sources

        2. User has authorized documents AND
           relevant information is found:

           -> RAG
           -> ChromaDB search
           -> LLM receives document context
           -> Return answer + sources

        3. User has authorized documents BUT
           no relevant information is found:

           -> DO NOT use normal LLM
           -> Return "information not found"
           -> No sources
        """

        # ==================================================
        # 1. VALIDATE QUESTION
        # ==================================================

        if question is None:
            raise ValueError(
                "Question cannot be empty"
            )

        question = question.strip()

        if not question:
            raise ValueError(
                "Question cannot be empty"
            )

        # ==================================================
        # 2. NORMALIZE AUTHORIZED DOCUMENT IDS
        # ==================================================

        if allowed_document_ids is None:
            allowed_document_ids = []

        allowed_document_ids = list(
            allowed_document_ids
        )

        print("====================================")
        print("RAG SERVICE")
        print("Question:", question)
        print(
            "Authorized document IDs:",
            allowed_document_ids
        )
        print("====================================")

        # ==================================================
        # 3. NO AUTHORIZED DOCUMENTS
        # ==================================================
        #
        # The user has no documents available.
        #
        # This is NOT an authorization error.
        #
        # The user can still use the LLM normally.
        #
        # IMPORTANT:
        # We do NOT search ChromaDB.
        # ==================================================

        if not allowed_document_ids:

            print(
                "MODE: NORMAL LLM"
            )

            print(
                "User has no authorized documents."
            )

            print(
                "Skipping vector search."
            )

            llm = LLMService.get_llm()

            response = llm.invoke(
                question
            )

            return {

                "answer":
                    response.content,

                "sources":
                    []
            }

        # ==================================================
        # 4. RAG MODE
        # ==================================================
        #
        # The user has authorized documents.
        #
        # Search ONLY those documents.
        # ==================================================

        print(
            "MODE: RAG"
        )

        print(
            "Searching authorized documents..."
        )

        relevant_documents = (
            VectorRepository.similarity_search(

                question=question,

                allowed_document_ids=
                    allowed_document_ids,

                k=5
            )
        )

        # ==================================================
        # 5. NO RELEVANT DOCUMENT FOUND
        # ==================================================
        #
        # IMPORTANT SECURITY RULE:
        #
        # We DO NOT fall back to the normal LLM here.
        #
        # Why?
        #
        # Example:
        #
        # User asks:
        # "What is our company's salary policy?"
        #
        # The user has documents, but none of the
        # authorized documents contain that information.
        #
        # The general LLM must NOT invent an answer.
        # ==================================================

        if not relevant_documents:

            print(
                "NO RELEVANT DOCUMENT FOUND"
            )

            print(
                "Normal LLM fallback is DISABLED."
            )

            answer = (
                "I couldn't find relevant information "
                "about this question in the documents "
                "you are authorized to access."
            )

            return {

                "answer":
                    answer,

                "sources":
                    []
            }

        # ==================================================
        # 6. RELEVANT DOCUMENTS FOUND
        # ==================================================

        print(
            "Relevant documents found:",
            len(relevant_documents)
        )

        # ==================================================
        # 7. BUILD CONTEXT
        # ==================================================

        context = self.build_context(
            relevant_documents
        )

        print(
            "Document context successfully built."
        )

        # ==================================================
        # 8. GET LLM
        # ==================================================

        llm = LLMService.get_llm()

        # ==================================================
        # 9. CREATE RAG CHAIN
        # ==================================================

        chain = (
            RAG_PROMPT
            | llm
        )

        # ==================================================
        # 10. INVOKE RAG CHAIN
        # ==================================================

        response = chain.invoke({

            "context":
                context,

            "question":
                question
        })

        # ==================================================
        # 11. BUILD SOURCES
        # ==================================================

        sources = self.build_sources(
            relevant_documents
        )

        print(
            "Sources:",
            sources
        )

        # ==================================================
        # 12. RETURN RAG RESPONSE
        # ==================================================

        return {

            "answer":
                response.content,

            "sources":
                sources
        }

    # ======================================================
    # BUILD CONTEXT
    # ======================================================

    def build_context(
        self,
        documents
    ):
        """
        Convert retrieved vector documents
        into the context sent to the LLM.
        """

        context_parts = []

        for document in documents:

            # ----------------------------------------------
            # Document ID
            # ----------------------------------------------

            document_id = (
                document.metadata.get(
                    "document_id"
                )
            )

            # ----------------------------------------------
            # Document name
            # ----------------------------------------------

            document_name = (
                document.metadata.get(
                    "document_name"
                )
            )

            # ----------------------------------------------
            # Chunk index
            # ----------------------------------------------

            chunk_index = (
                document.metadata.get(
                    "chunk_index"
                )
            )

            # ----------------------------------------------
            # Page content
            # ----------------------------------------------

            content = (
                document.page_content
            )

            # ----------------------------------------------
            # Build context block
            # ----------------------------------------------

            context_parts.append(

                f"""
SOURCE:
Document ID: {document_id}
Document Name: {document_name}
Chunk Index: {chunk_index}

CONTENT:
{content}
"""
            )

        return "\n\n".join(
            context_parts
        )

    # ======================================================
    # BUILD SOURCES
    # ======================================================

    def build_sources(
        self,
        documents
    ):
        """
        Build a unique list of source documents.

        Several chunks can belong to the same document.

        Example:

        Chunk 1 -> document 10
        Chunk 2 -> document 10
        Chunk 3 -> document 15

        Result:

        [
            {
                "document_id": 10,
                "document_name": "file1.pdf"
            },
            {
                "document_id": 15,
                "document_name": "file2.pdf"
            }
        ]
        """

        unique_sources = {}

        for document in documents:

            # ----------------------------------------------
            # Document ID
            # ----------------------------------------------

            document_id = (
                document.metadata.get(
                    "document_id"
                )
            )

            # ----------------------------------------------
            # Ignore invalid document IDs
            # ----------------------------------------------

            if document_id is None:
                continue

            # ----------------------------------------------
            # Document name
            # ----------------------------------------------

            document_name = (
                document.metadata.get(
                    "document_name"
                )
            )

            # ----------------------------------------------
            # Add only once
            # ----------------------------------------------

            if document_id not in unique_sources:

                unique_sources[
                    document_id
                ] = {

                    "document_id":
                        document_id,

                    "document_name":
                        document_name
                }

        return list(
            unique_sources.values()
        )