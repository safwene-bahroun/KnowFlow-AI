# KnowFlow AI

KnowFlow AI is an enterprise knowledge platform that combines role-based document access, retrieval-augmented generation (RAG), and an AI chat assistant. Employees and managers can ask questions about documents they are authorized to access. Administrators manage users, departments, documents, feedback, and notifications.

## Architecture

The repository contains three applications:

- `Angular/`: Angular 20 frontend with role-based dashboards for admins, employees, and managers.
- `Spring_boot/`: Spring Boot 3.5 REST API, PostgreSQL persistence, JWT authentication, document management, conversations, feedback, and notifications.
- `Flask/`: Flask RAG service for document ingestion, embeddings, vector search, and LLM responses.

Typical request flow:

```text
Angular -> Spring Boot API -> Flask RAG service
                    |
                    -> PostgreSQL
                    -> Chroma vector store (Flask)
```

## Main Features

- JWT authentication and role-based access control
- Document upload, update, visibility rules, and ingestion
- RAG-powered chat with conversation history
- Employee and manager feedback submission
- Admin feedback inbox and feedback notifications
- Notifications for document changes and new feedback
- Admin management of users, departments, and documents

## Requirements

- Node.js and npm
- Java 21
- Maven
- Python 3.10 or newer
- PostgreSQL
- Ollama or another configured LLM provider used by the Flask service

## Configuration

Create the required environment values before starting Spring Boot:

- `DB_PASSWORD`: PostgreSQL password
- `JWT_SECRET`: JWT signing secret
- `MAIL_PASSWORD`: SMTP password, if password reset email is enabled

Spring Boot expects PostgreSQL at:

```text
jdbc:postgresql://localhost:5432/knowflow
```

The default service ports are:

- Angular: `4200`
- Spring Boot: `3000`
- Flask: `5001`

## Run Locally

Start the Flask service:

```bash
cd Flask
python -m pip install -r requirements.txt
python run.py
```

Start the Spring Boot API in another terminal:

```bash
cd Spring_boot
mvn spring-boot:run
```

Start the Angular frontend in another terminal:

```bash
cd Angular
npm install
npm start
```

Open `http://localhost:4200/` in a browser.

## Build and Test

Angular production build:

```bash
cd Angular
npm run build
```

Angular unit tests:

```bash
cd Angular
npm test
```

Spring Boot tests:

```bash
cd Spring_boot
mvn test
```

## API Areas

The Spring Boot API includes these main areas:

- `/api/auth`: registration, login, and password recovery
- `/api/admin/documents`: administrator document management
- `/api/documents`: employee document access
- `/api/chat`: questions, conversations, and conversation messages
- `/api/feedback`: feedback submission and admin feedback management
- `/api/notifications`: authenticated notification inbox

## Project Notes

- Do not commit passwords, JWT secrets, SMTP credentials, or database credentials.
- PostgreSQL schema updates are managed by Hibernate and application startup migrations.
- The Flask service uses the local Chroma database and configured embedding/LLM services.
- The Angular API URLs currently target `localhost`; update the services when deploying to another environment.
