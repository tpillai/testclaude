# Todo App

A full-stack Todo application built with:

- **Backend** — Spring Boot 3, Java 17, Spring Data JPA, PostgreSQL
- **Frontend** — React 18, Vite, served via Nginx
- **Infrastructure** — Docker, Docker Compose

---

## Project Structure

```
testclaude/
├── docker-compose.yml
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       └── main/java/com/example/todo/
│           ├── TodoApplication.java
│           ├── config/CorsConfig.java
│           ├── controller/TodoController.java
│           ├── model/Todo.java
│           └── repository/TodoRepository.java
└── frontend/
    ├── Dockerfile
    ├── nginx.conf
    ├── vite.config.js
    └── src/
        ├── main.jsx
        ├── App.jsx
        └── App.css
```

---

## REST API Endpoints

| Method | Endpoint       | Description         |
|--------|----------------|---------------------|
| GET    | `/todos`       | Get all todos       |
| GET    | `/todos/{id}`  | Get todo by ID      |
| POST   | `/todos`       | Create a new todo   |
| PUT    | `/todos/{id}`  | Update a todo       |
| DELETE | `/todos/{id}`  | Delete a todo       |

---

## Running with Docker Compose (Recommended)

### Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running

### Start the application

```bash
git clone <your-repo-url>
cd testclaude

docker compose up --build
```

Open your browser at: **http://localhost**

### Stop the application

```bash
docker compose down
```

To also remove the database volume (all data):

```bash
docker compose down -v
```

---

## Running Locally (Without Docker)

### Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 18+
- PostgreSQL running locally

### Backend

```bash
cd backend
mvn spring-boot:run
```

The backend starts at `http://localhost:8080`.

By default it connects to `jdbc:postgresql://localhost:5432/tododb` with username/password `postgres`.  
Override with environment variables:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tododb
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend starts at `http://localhost:5173`.  
Vite proxies `/api` calls to the backend automatically.

---

## Running Tests

```bash
cd backend
mvn test
```

Tests use an in-memory H2 database — no PostgreSQL required.

---

## Building Docker Images Manually

### Backend

```bash
# Build
cd backend
docker build -t todo-backend:1.0 .

# Tag for Docker Hub
docker tag todo-backend:1.0 <your-dockerhub-username>/todo-backend:1.0

# Push to Docker Hub
docker login
docker push <your-dockerhub-username>/todo-backend:1.0
```

### Frontend

```bash
# Generate package-lock.json first (only needed once)
cd frontend
npm install

# Build
docker build -t todo-frontend:1.0 .

# Tag for Docker Hub
docker tag todo-frontend:1.0 <your-dockerhub-username>/todo-frontend:1.0

# Push to Docker Hub
docker push <your-dockerhub-username>/todo-frontend:1.0
```

---

## Environment Variables

| Variable                    | Default                              | Description              |
|-----------------------------|--------------------------------------|--------------------------|
| `SPRING_DATASOURCE_URL`     | `jdbc:postgresql://localhost:5432/tododb` | PostgreSQL JDBC URL |
| `SPRING_DATASOURCE_USERNAME`| `postgres`                           | Database username        |
| `SPRING_DATASOURCE_PASSWORD`| `postgres`                           | Database password        |

---

## Architecture

```
Browser
  │
  ▼
Frontend (Nginx :80)
  │  serves React app
  │  proxies /api/* → backend:8080/*
  ▼
Backend (Spring Boot :8080)
  │
  ▼
PostgreSQL (:5432)
```
