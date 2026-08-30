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

from app.repositories.vector_repository import (
    VectorRepository
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
        if not data:
            return jsonify({"error": "No JSON payload provided"}), 400

        document_data = DocumentSchema.validate(data)

        result = IngestionService.ingest_document(document_data)

        return jsonify({
            "message": "Document processed successfully",
            "document_id": result["document_id"],
            "chunk_count": result["chunk_count"]
        }), 200

    except ValueError as error:
        return jsonify({
            "error": str(error)
        }), 400

    except Exception as error:
        return jsonify({
            "error": str(error)
        }), 500


@document_bp.route(
    "/<int:document_id>",
    methods=["DELETE"]
)
@require_internal_key
def delete_document_vectors(document_id):
    try:
        VectorRepository.delete_document(document_id)
        return jsonify({
            "message": f"Vectors for document {document_id} deleted successfully",
            "document_id": document_id
        }), 200
    except Exception as error:
        return jsonify({
            "error": str(error)
        }), 500