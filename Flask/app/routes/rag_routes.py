from flask import (
    Blueprint,
    request,
    jsonify
)

from app.utils.security import (
    require_internal_key
)

from app.schemas.rag_schema import (
    RagSchema
)

from app.services.rag_service import (
    RagService
)


rag_bp = Blueprint(
    "rag",
    __name__
)


@rag_bp.route(
    "/query",
    methods=["POST"]
)
@require_internal_key
def ask_question():

    try:

        data = request.get_json()

        print("====================================")
        print("FLASK RAG /QUERY CALLED")
        print("Received data:", data)
        print("====================================")

        rag_data = RagSchema.validate(data)

        allowed_document_ids = rag_data[
            "allowed_document_ids"
        ]

        print(
            "Authorized documents:",
            allowed_document_ids
        )

        rag_service = RagService()

        result = rag_service.ask(
            question=rag_data["question"],
            allowed_document_ids=allowed_document_ids
        )

        return jsonify(result), 200

    except ValueError as error:

        return jsonify({
            "error": str(error)
        }), 400

    except Exception as error:

        print(
            "FLASK RAG ERROR:",
            error
        )

        return jsonify({
            "error": str(error)
        }), 500