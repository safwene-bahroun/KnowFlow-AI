from flask import (
    Blueprint,
    request,
    jsonify
)

from app.utils.security import (
    require_internal_key
)

from app.schemas.document_schema import (
    DocumentSchema
)

from app.services.ingestion_service import (
    IngestionService
)

from app.services.spring_client import (
    SpringClient
)


document_bp = Blueprint(
    "documents",
    __name__
)


@document_bp.route(
    "/ingest",
    methods=["POST"]
)
@require_internal_key
def ingest_document():

    try:

        data = request.get_json()

        document_data = (
            DocumentSchema.validate(
                data
            )
        )

        ingestion_service = (
            IngestionService()
        )

        SpringClient.update_document_status(
            document_data[
                "document_id"
            ],
            "PROCESSING"
        )

        result = (
            ingestion_service
            .process_document(
                document_data
            )
        )

        SpringClient.save_chunks(
            document_data[
                "document_id"
            ],
            result["chunks"]
        )

        SpringClient.update_document_status(
            document_data[
                "document_id"
            ],
            "PROCESSED"
        )

        return jsonify({
            "message":
            "Document processed successfully",

            "document_id":
            result["document_id"],

            "chunk_count":
            len(
                result["chunks"]
            )
        }), 200

    except Exception as error:

        return jsonify({
            "error":
            str(error)
        }), 500