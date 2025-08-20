# Library API

Java 17 • Spring Boot 3.5 • Maven • H2 (dev)

A simple REST API that manages **books**. Built with a clean layering style (Controller → Service → Repository → Entity/DTO) plus validation and global error handling. Local development uses an in‑memory H2 database; a production profile with PostgreSQL + Docker/CI/CD will be added next.

---

## ✨ Features

- REST endpoints for **Book** CRUD
- Input **validation** (Jakarta Validation)
- **Global** exception handling with JSON errors
- **Spring Data JPA** for persistence
- **H2** in‑memory DB for local dev (no setup)
- Ready for Docker/CI/CD (next steps)

---

## 🧱 Tech Stack

- **Java**: 17
- **Spring Boot**: 3.5.x
- **Build**: Maven
- **DB (dev)**: H2 in‑memory (PostgreSQL mode)

---

## 🚀 Quick Start

```bash
# 1) run the app (dev profile / H2)
mvn spring-boot:run

# Base URL
http://localhost:8080
```

If you see the string `Library API is running` at `/`, the app is up.

> **Port busy?** Stop the old run (Ctrl+C) or start on another port:
> `mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"`

---

## ⚙️ Configuration (dev)

`src/main/resources/application.properties`

```properties
server.port=8080
spring.datasource.url=jdbc:h2:mem:libdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
spring.h2.console.path=/h2
```

### H2 Console

- URL: `http://localhost:8080/h2`
- JDBC URL: `jdbc:h2:mem:libdb`
- User: `sa`, Password: _(empty)_

> **Note:** The DB is in memory; it resets each time the app restarts.

---

## 📦 Project Structure (high level)

```
src/main/java/com/example/library_api/
 ├─ LibraryApiApplication.java          # app entry (@SpringBootApplication)
 ├─ RootController.java                 # optional hello at '/'
 ├─ common/
 │   └─ GlobalExceptionHandler.java     # consistent JSON errors
 └─ book/
     ├─ Book.java                       # @Entity
     ├─ BookRepository.java             # extends JpaRepository
     ├─ BookRequest.java                # DTO + validation
     ├─ BookService.java                # business logic (@Service)
     └─ BookController.java             # REST endpoints (@RestController)
```

---

## 🔌 API Endpoints

Base: `http://localhost:8080`

| Method | Path              | Body (JSON)                                      |  Success  |
| -----: | ----------------- | ------------------------------------------------ | :-------: |
|   POST | `/api/books`      | `{ "title", "author", "isbn", "yearPublished" }` |    201    |
|    GET | `/api/books`      | —                                                |    200    |
|    GET | `/api/books/{id}` | —                                                | 200 / 404 |
|    PUT | `/api/books/{id}` | Partial/Full `{ "title", ... }`                  |    200    |
| DELETE | `/api/books/{id}` | —                                                |    204    |

### Sample Requests

**Create**

```bash
curl -X POST http://localhost:8080/api/books \
 -H "Content-Type: application/json" \
 -d '{
      "title":"Clean Code",
      "author":"Robert C. Martin",
      "isbn":"978-0132350884",
      "yearPublished":2008
     }'
```

**List**

```bash
curl http://localhost:8080/api/books
```

### Validation & Errors

- Missing/blank fields → `400 { "error": "VALIDATION_ERROR", "fields": { ... } }`
- Duplicate ISBN → `400 { "error": "BAD_REQUEST", ... }`
- Not found → `404 { "error": "NOT_FOUND" }`

---

## 🧪 Postman Collection

Import the collection from `docs/postman/Library-API.postman_collection.json` and set the collection variable:

- `baseUrl = http://localhost:8080`

The collection includes: Create, List, Get by ID, Update, Delete, and an Invalid Create (validation demo).

---

## 🛠️ Build

```bash
# compile & run tests
mvn clean test

# package the jar
mvn -DskipTests package
# run the packaged jar
java -jar target/library-api-0.0.1-SNAPSHOT.jar
```

---

## 🧭 How it works (one line)

**HTTP → Controller → Service → Repository (JPA/Hibernate) → H2 → JSON back**

- `@RestController` maps routes → calls `BookService`
- `BookService` enforces rules (e.g., unique ISBN) and uses `BookRepository`
- `BookRepository extends JpaRepository` provides CRUD without boilerplate
- `@RestControllerAdvice` centralizes JSON error responses

---

## ☸️ Deploying the API to GKE (dev/H2)

The app has been containerized and deployed to **Google Kubernetes Engine** with a `LoadBalancer` service.  
This runs the app with the **dev (H2)** profile inside the container.

### 1) Container image

```bash
# build locally (optional)
docker build -t library-api:dev .

# run locally (optional)
docker run --rm -p 8080:8080 library-api:dev
```

### 2) Create / connect to a GKE cluster

```bash
gcloud config set project <YOUR_PROJECT_ID>
```

# Autopilot (used here) in us-central1

```bash
gcloud container clusters create-auto library-api --region us-central1
```

# wire kubectl to the cluster

```bash
gcloud container clusters get-credentials library-api --region us-central1
kubectl config current-context
```

### 3) Apply Kubernetes manifests

k8s/library-api.yaml defines Namespace, Deployment, and Service:

```bash
apiVersion: v1
kind: Namespace
metadata:
  name: library
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: library-api
  namespace: library
spec:
  replicas: 1
  selector:
    matchLabels:
      app: library-api
  template:
    metadata:
      labels:
        app: library-api
    spec:
      containers:
        - name: api
          image: ghcr.io/<your-username>/library-api-spring-boot:latest
          imagePullPolicy: Always
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "dev" # H2 in-memory
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: /
              port: 8080
            initialDelaySeconds: 10
            periodSeconds: 5
          livenessProbe:
            httpGet:
              path: /
              port: 8080
            initialDelaySeconds: 20
            periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: library-api
  namespace: library
spec:
  type: LoadBalancer
  selector:
    app: library-api
  ports:
    - name: http
      port: 80
      targetPort: 8080
```

Deploy:

```bash
kubectl apply -f k8s/library-api.yaml
kubectl rollout status deploy/library-api -n library --timeout=180s
```

## 4) Get a public IP & test

```bash
kubectl get svc library-api -n library -w   # wait until EXTERNAL-IP shows up

# once you have an IP:
EXT=<EXTERNAL_IP>
curl -i http://$EXT/
curl -s http://$EXT/api/books
curl -s -X POST http://$EXT/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Clean Code","author":"Robert C. Martin","isbn":"978-0132350884","yearPublished":2008}'
curl -s http://$EXT/api/books
```

# once you have an IP:

```bash
EXT=<EXTERNAL_IP>
curl -i http://$EXT/
curl -s http://$EXT/api/books
curl -s -X POST http://$EXT/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Clean Code","author":"Robert C. Martin","isbn":"978-0132350884","yearPublished":2008}'
curl -s http://$EXT/api/books
```

---

## ▶️ Next Steps (coming up)

- Add **production profile** (PostgreSQL)
- **Dockerfile** (multi‑stage) and optional `docker compose` with Postgres
- **GitHub Actions** to build & push container
- **GKE** manifests and deployment

---

## Troubleshooting

- **Port 8080 already in use**: stop the previous run (Ctrl+C), or run on another port `--server.port=8081`.
- **“No plugin found for prefix 'spring-boot'”**: ensure you run Maven **in the folder with `pom.xml`** or use the wrapper `./mvnw spring-boot:run`.
- **Old Maven mirrors**: if you ever see requests to a private Nexus, remove `~/.m2/settings.xml` or point Maven to a clean settings file.
