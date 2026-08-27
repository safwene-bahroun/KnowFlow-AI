from functools import wraps

from flask import request, jsonify

from app.config import Config


def require_internal_key(function):

    @wraps(function)
    def decorated(*args, **kwargs):

        api_key = request.headers.get(
            "X-Internal-API-Key"
        )

        if not api_key:

            return jsonify({
                "error": "Missing API key"
            }), 401

        if api_key != Config.INTERNAL_API_KEY:

            return jsonify({
                "error": "Invalid API key"
            }), 403

        return function(*args, **kwargs)

    return decorated