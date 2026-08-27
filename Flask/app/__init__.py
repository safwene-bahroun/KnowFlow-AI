from flask import Flask
from flask_cors import CORS

from app.config import Config


def create_app():

    app = Flask(__name__)

    app.config.from_object(Config)

    CORS(
        app,
        resources={
            r"/api/*": {
                "origins": "*"
            }
        }
    )

    from app.routes.health_routes import health_bp
    from app.routes.document_routes import document_bp
    from app.routes.rag_routes import rag_bp

    app.register_blueprint(
        health_bp,
        url_prefix="/api"
    )

    app.register_blueprint(
        document_bp,
        url_prefix="/api/documents"
    )

    app.register_blueprint(
        rag_bp,
        url_prefix="/api/rag"
    )

    return app