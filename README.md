# NSIP - National Social Insurance Platform

A production-ready, microservices-based social insurance platform built with Spring Boot 3.2.5, JDK 17, and Hexagonal Architecture. Now enhanced with **Multimodal AI Capabilities** and **Azure AKS Deployment**.

## 🏗 Architecture

```mermaid
graph TD
    Client[Web/Mobile Client] --> Gateway[API Gateway :8080]
    
    subgraph "AI & Real-time"
        Client <--> LiveKit[LiveKit WebRTC]
        LiveKit <--> Gemini[Gemini 1.5 Flash]
    end

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
    
    subgraph "Infrastructure (Azure)"
        AKS[Azure Kubernetes Service]
        Auth --> DB[(Azure Postgres v4)]
        Gateway --> Redis[(Azure Redis v4)]
        ACR[(Azure Container Registry)]
    end
```

## 🛡 Hardening Features
- **Multimodal AI Assistant**: Integrated LiveKit + Gemini 1.5 Flash for real-time Voice, Video, and Text interaction.
- **Azure Hardened Infrastructure**: Deployed on AKS in `southeastasia` with managed Postgres and Redis.
- **Distributed Tracing**: Automatic `X-Correlation-ID` propagation across all services.
- **API Resilience**: Circuit Breakers (Resilience4j) and Redis-based Rate Limiting at the Gateway.
- **Hexagonal Architecture**: Strict separation of domain logic from infrastructure adapters.

## 🚀 Getting Started

### Prerequisites
- JDK 17 & Maven 3.9+
- Docker & Azure CLI
- LiveKit Cloud Account & Google AI Studio Key

### Azure Deployment (CI/CD)
1. **Provision Infrastructure**:
   ```bash
   cd azure/terraform
   terraform init && terraform apply
   ```
2. **Trigger Workflow**: Push to `main` to trigger `.github/workflows/deploy-azure.yml`.

### Local Development
1. **Build the Platform**: `mvn clean install -DskipTests`
2. **Run Infrastructure**: `docker-compose up -d`
3. **Run Services**: `./run-locally.sh`

## 📂 Repository Structure
- `/backend`: Java microservices and `nsip-common`.
- `/frontend-web`: Material UI React portal with LiveKit Assistant.
- `/azure`: Terraform scripts and Cloud configuration.
- `/k8s`: Kubernetes manifests for Production (v4).

## 📝 License
Proprietary - National Social Insurance Authority.
