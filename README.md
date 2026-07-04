# Jobtastic

Jobtastic is a full-stack job application tracker that helps users manage their job search in one place. Users can sign up, log in, and keep track of applications with status updates, notes, dates, and locations.

## Features

- User authentication and authorization
- Secure login/signup flow with JWT-based auth
- Dashboard for tracking job applications
- Add, edit, search, and delete applications
- Status tracking for application progress
- Profile management
- REST API with Swagger documentation

## Tech Stack

### Frontend
- React
- TypeScript
- Vite
- React Router
- Axios
- Tailwind CSS

### Backend
- Java 25
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway migrations
- JWT authentication
- OpenAPI / Swagger

## Project Structure

```text
backend/   # Spring Boot API and database layer
frontend/  # React + TypeScript client application
```

## Prerequisites

- Java 25 or newer
- Maven or the included Maven wrapper
- Node.js and npm
- PostgreSQL database

## Environment Variables

Set the following environment variables before running the backend:

```bash
DB_URL=jdbc:postgresql://localhost:5432/jobtastic
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
JWT_SECRET_KEY=your_jwt_secret
SUPPORT_EMAIL=your_email@example.com
APP_PASSWORD=your_email_app_password
```

## Running the Backend

```bash
cd backend
./mvnw spring-boot:run
```

The API will be available at:
- http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

## Running the Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend will be available at:
- http://localhost:5173

## Testing

Backend tests can be run with:

```bash
cd backend
./mvnw test
```

## API Overview

The backend exposes endpoints for:
- authentication and registration
- user profile management
- job application CRUD operations