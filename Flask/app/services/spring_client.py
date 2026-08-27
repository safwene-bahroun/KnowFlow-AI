import requests

from app.config import Config


class SpringClient:

    @staticmethod
    def save_chunks(
        document_id,
        chunks
    ):

        url = (
            f"{Config.SPRING_BOOT_URL}"
            f"/api/internal/documents/"
            f"{document_id}/chunks"
        )

        headers = {
            "Content-Type": "application/json",
            "X-Internal-API-Key": Config.INTERNAL_API_KEY
        }

        response = requests.post(
            url,
            json={
                "chunks": chunks
            },
            headers=headers,
            timeout=60
        )

        response.raise_for_status()

        return response.json()


    @staticmethod
    def update_document_status(
        document_id,
        status
    ):

        url = (
            f"{Config.SPRING_BOOT_URL}"
            f"/api/internal/documents/"
            f"{document_id}/status"
        )

        headers = {
            "Content-Type": "application/json",
            "X-Internal-API-Key": Config.INTERNAL_API_KEY
        }

        response = requests.put(
            url,
            json={
                "status": status
            },
            headers=headers,
            timeout=30
        )

        response.raise_for_status()

        return response.json()