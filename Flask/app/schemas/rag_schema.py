class RagSchema:

    @staticmethod
    def validate(data):

        if "question" not in data:

            raise ValueError(
                "Question is required"
            )

        if "allowed_document_ids" not in data:

            raise ValueError(
                "allowed_document_ids is required"
            )

        return {
            "question": data["question"],

            "allowed_document_ids": [
                int(document_id)
                for document_id in
                data["allowed_document_ids"]
            ]
        }