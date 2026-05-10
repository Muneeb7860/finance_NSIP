# 07. LLD Diagrams (Cloud Architecture)

This document visualizes the production-ready infrastructure and service orchestration on Azure.

## 1. Cloud Infrastructure Overview (Azure)

```mermaid
graph TD
    User([Citizen/Employer]) --> Ingress[Nginx Ingress Controller]
    
    subgraph "Azure AKS Cluster"
        Ingress --> Gateway[API Gateway]
        Gateway --> Auth[Auth Service]
        Gateway --> Services[Microservices Stack]
        
        Services --> Saga[Saga Orchestrator]
        Saga --> Kafka((Azure Event Hubs))
    end
    
    subgraph "Managed Data Layer"
        Services --> DB[(Azure PostgreSQL)]
        Auth --> Redis[(Azure Redis Cache)]
    end
    
    subgraph "Security & Identity"
        Auth --> Entra[Azure Entra ID / SP]
    end
```

## 2. CI/CD Pipeline (GitHub Actions)

```mermaid
graph LR
    Push[Git Push] --> GH[GitHub Actions]
    GH --> Parallel[Parallel Build Matrix]
    
    subgraph "Parallel Builders"
        Parallel --> B1[Frontend]
        Parallel --> B2[Auth]
        Parallel --> B3[Claims]
        Parallel --> B4[...]
    end
    
    B1 & B2 & B3 & B4 --> ACR{Azure Container Registry}
    ACR --> AKS[AKS Deployment]
```

## 3. Data Flow: Loan Request (Saga Pattern)

```mermaid
sequenceDiagram
    participant User
    participant Gateway
    participant ClaimService
    participant Saga
    participant Kafka
    participant Payment

    User->>Gateway: POST /api/v1/claims/loan
    Gateway->>ClaimService: Route Request
    ClaimService->>Kafka: Emit 'loan.requested'
    Kafka->>Saga: Consume Event
    Saga->>Payment: Trigger Disbursement
    Payment-->>Saga: Disbursement Success
    Saga->>Kafka: Emit 'loan.completed'
    Kafka->>ClaimService: Finalize Status
```
