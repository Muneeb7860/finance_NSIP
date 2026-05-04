# NSIP - National Social Insurance Platform

A production-ready, microservices-based social insurance platform built with Spring Boot 3.2.5, JDK 17, and Hexagonal Architecture.

## 🏗 Architecture

```mermaid
graph TD
    Client[Web/Mobile Client] --> Gateway[API Gateway :8080]
    
    subgraph "Backend Services"
        Gateway --> Auth[Auth Service :8081]
        Gateway --> Claim[Claim Service :8082]
        Gateway --> Contrib[Contribution Service :8083]
        Gateway --> Educ[Education Service :8084]
        Gateway --> Event[Event Service :8085]
        Gateway --> Rewards[Rewards Service :8086]
        Gateway --> Saga[Saga Orchestrator :8087]
        Gateway --> Payment[Payment Service :8088]
        Gateway --> Review[Review Service :8089]
        Gateway --> Notif[Notification Engine :8090]
    end
    
    subgraph "Infrastructure"
        Auth --> DB[(PostgreSQL)]
        Claim --> DB
        Event --> Kafka{Kafka}
        Notif --> Kafka
        Gateway --> Redis[(Redis - Rate Limiting)]
    end
```

## 🛡 Hardening Features
- **Distributed Tracing**: Automatic `X-Correlation-ID` propagation across all services.
- **API Resilience**: Circuit Breakers (Resilience4j) and Redis-based Rate Limiting at the Gateway.
- **Hexagonal Architecture**: Strict separation of domain logic from infrastructure adapters.
- **Container Health**: Integrated Docker Health Checks for self-healing orchestration.
- **Security**: Centralized Global Exception Handling and restricted CORS policies.

## 🚀 Getting Started

### Prerequisites
- JDK 17
- Maven 3.9+
- Docker & Docker Compose

### Quick Start
1. **Build the Platform**:
   ```bash
   mvn clean install -DskipTests
   ```
2. **Launch Infrastructure**:
   ```bash
   docker-compose up -d
   ```
3. **Run Services Locally**:
   Use the provided script:
   ```bash
   ./run-locally.sh
   ```

## 📂 Repository Structure
- `/backend`: Java microservices and shared `nsip-common` library.
- `/frontend-web`: Material UI React portal.
- `/frontend-mobile`: Flutter-based mobile application.
- `/k8s`: Kubernetes manifests for production deployment.
- `/legacy`: Archive of previous platform iterations.

## 📝 License
Proprietary - National Social Insurance Authority.
