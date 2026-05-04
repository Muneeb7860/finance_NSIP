# National Social Insurance Platform (NSIP)

A production-ready, event-driven microservices platform for national social insurance management.

## 🚀 Quick Start (Local)

To run the entire platform (10 microservices + Frontend + Infrastructure) locally, ensure you have **Docker** and **Node.js** installed, then run:

```bash
./run-locally.sh
```

This script will:
1. Build the **Material UI (MUI)** based frontend.
2. Build all 10 Java microservices using the **Maven multi-module** Docker build.
3. Start isolated Postgres instances, Kafka, Zookeeper, and Redis.
4. Inject correlation IDs for Splunk/Dynatrace observability.

## 🌐 Access Points

| Component | URL |
|-----------|-----|
| **Contributor Portal** | [http://localhost:5173](http://localhost:5173) |
| **API Gateway** | [http://localhost:8081](http://localhost:8081) |
| **Swagger Docs** | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) |

## 🛠 Tech Stack

- **Backend**: Java 17, Spring Boot 3.2.5, **Maven Multi-Module**
- **Frontend**: React 18, **Material UI (MUI)**
- **Database**: PostgreSQL (Isolated per-service databases)
- **Observability**: Splunk (JSON logs), Dynatrace (OpenTelemetry), Correlation ID Tracing
- **Messaging**: Apache Kafka (Event-driven architecture)
- **Orchestration**: Saga Pattern (persistent state)

## 📂 Project Structure

- `/backend`: 10 microservices, **Root Dockerfile**, and infrastructure configs.
- `/frontend-web`: React-based portal using **MUI**.
- `/docs`: BRD, PRD, HLD, LLD, **Apigee**, and **MCP** documentation.
- `/k8s`: Kubernetes manifests for production deployment.

## 📋 Monitoring & Logs

To view logs for all services:
```bash
cd backend
docker-compose logs -f
```

To stop everything:
```bash
cd backend
docker-compose down
```
