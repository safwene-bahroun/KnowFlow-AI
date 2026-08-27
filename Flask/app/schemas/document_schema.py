class DocumentSchema:

    @staticmethod
    def validate(data):

        required_fields = [
            "document_id",
            "name",
            "url"
        ]

        for field in required_fields:

            if field not in data:

                raise ValueError(
                    f"Missing required field: {field}"
                )

        return {
            "document_id": int(
                data["document_id"]
            ),

            "name": data["name"],

            "url": data["url"],

            "mime_type": data.get(
                "mime_type"
            ),

            "description": data.get(
                "description"
            ),

            "author": data.get(
                "author"
            ),

            "visibility": data.get(
                "visibility"
            ),

            "department_id": data.get(
                "department_id"
            ),

            "created_by_id": data.get(
                "created_by_id"
            )
        }