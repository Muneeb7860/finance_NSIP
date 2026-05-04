# High-Level Design (HLD)
## National Social Insurance Platform (NSIP)

**Version:** 2.0 | **Date:** May 2026

---

## 1. System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐  │
│  │ Flutter App   │  │ React Web    │  │ React Web (Admin/Employer)│  │
│  │ (Contributor) │  │ (Contributor)│  │ (Back Office + Business) │  │
│  └──────┬───────┘  └──────┬───────┘  └───────────┬──────────────┘  │
└─────────┼──────────────────┼─────────────────────┼──────────────────┘
          │                  │                     │
          ▼                  ▼                     ▼
┌─────────────────────────────────────────────────────────────────────┐
│                     API GATEWAY LAYER                                │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  Spring Cloud Gateway / Apigee                              │    │
│  │  • JWT Validation  • RBAC  • Rate Limiting  • CORS          │    │
│  └──────────────────────────┬──────────────────────────────────┘    │
└─────────────────────────────┼──────────────────────────────────────┘
                              │
┌─────────────────────────────┼──────────────────────────────────────┐
│                    MICROSERVICE LAYER                               │
│                              │                                      │
│  ┌──────────┐  ┌──────────┐ │ ┌──────────┐  ┌──────────────────┐  │
│  │  Auth    │  │  Claims  │ │ │Contribu- │  │   Education      │  │
│  │  Service │  │  Service │ │ │  tions   │  │   Service        │  │
│  │  :8081   │  │  :8082   │ │ │  :8083   │  │   :8084          │  │
│  │          │  │          │ │ │          │  │ • LMS + Quiz     │  │
│  │ • JWT    │  │ • Loans  │ │ │ • 4%     │  │ • Streaks        │  │
│  │ • BCrypt │  │ • Claims │ │ │   deduct │  │ • Certificates   │  │
│  └────┬─────┘  └────┬─────┘ │ └────┬─────┘  │ • Advisors       │  │
│       │              │       │      │        │ • Wellness        │  │
│  ┌────┴─────┐  ┌────┴─────┐ │ ┌────┴─────┐  └────┬─────────────┘  │
│  │  Event   │  │ Payment  │ │ │ Rewards  │       │                 │
│  │  Service │  │ Service  │ │ │ Service  │       │                 │
│  │  :8085   │  │  :8088   │ │ │  :8086   │       │                 │
│  │ • 3-Layer│  │ • Wallet │ │ │ • Points │       │                 │
│  │  Approve │  │ • Stripe │ │ │ • Ledger │       │                 │
│  │ • RSVP   │  │ • R4j    │ │ │ • Kafka  │       │                 │
│  └──────────┘  └──────────┘ │ └──────────┘       │                 │
│                              │                    │                 │
│  ┌──────────────────────┐   │ ┌──────────────────┴───┐             │
│  │  Saga Orchestrator   │   │ │  Notification Engine  │             │
│  │  :8087               │   │ │  :8090                │             │
│  │  • Persistent state  │   │ │  • Kafka consumer     │             │
│  │  • Compensations     │   │ │  • Email/SMS/WhatsApp │             │
│  └──────────────────────┘   │ └──────────────────────┘             │
│                              │                                      │
│  ┌──────────┐               │                                      │
│  │  Review  │               │                                      │
│  │  Service │               │                                      │
│  │  :8089   │               │                                      │
│  └──────────┘               │                                      │
└─────────────────────────────┼──────────────────────────────────────┘
                              │
┌─────────────────────────────┼──────────────────────────────────────┐
│                    DATA LAYER                                       │
│                              │                                      │
│  ┌───────────────────────────┴─────────────────────────────────┐   │
│  │          PostgreSQL 15 (Shared Instance, Logical DBs)       │   │
│  │  nsip_auth | nsip_claims | nsip_contributions | nsip_events │   │
│  │  nsip_education | nsip_rewards | nsip_payments | nsip_saga  │   │
│  │  nsip_reviews | nsip_notifications                          │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
│  ┌────────────────┐  ┌────────────────────────────────────────┐    │
│  │  Redis 7       │  │  Apache Kafka (Confluent 7.3)          │    │
│  │  • Session     │  │  Topics:                                │    │
│  │    cache       │  │  • loan.requested                       │    │
│  │  • Rate limit  │  │  • contribution.command/event.*          │    │
│  └────────────────┘  │  • payment.command/event.*               │    │
│                      │  • gamification.events                   │    │
│                      │  • notification.command.send              │    │
│                      └────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 2. Service Inventory

| # | Service | Port | Responsibility | DB | LOC |
|---|---------|------|---------------|-----|-----|
| 1 | auth-service | 8081 | Registration, JWT, BCrypt, RBAC | nsip_auth | 352 |
| 2 | claim-service | 8082 | Claims, personal loans, vesting check | nsip_claims | 391 |
| 3 | contribution-service | 8083 | Salary deductions, pension estimation, fund lock/unlock | nsip_contributions | 586 |
| 4 | education-service | 8084 | LMS, quizzes, streaks, certificates, advisors, wellness | nsip_education | 1,660 |
| 5 | event-service | 8085 | Event proposals, 3-layer approval, RSVP | nsip_events | 852 |
| 6 | rewards-service | 8086 | Points ledger, gamification, advisor sessions | nsip_rewards | 341 |
| 7 | saga-orchestrator | 8087 | Loan disbursement saga with persistent state | nsip_saga | 460 |
| 8 | payment-service | 8088 | Stripe gateway, wallet, loan repayments, circuit breaker | nsip_payments | 575 |
| 9 | review-service | 8089 | Post-claim satisfaction reviews | nsip_reviews | 161 |
| 10 | notification-engine | 8090 | Kafka-driven email/SMS/WhatsApp dispatcher | — | 105 |

**Total: 114 Java files · 5,483 lines**

---

## 3. Communication Patterns

### 3.1 Synchronous (REST)
- Client → API Gateway → Service (all user-facing operations)
- Request/Response with proper HTTP status codes

### 3.2 Asynchronous (Kafka)
- **Saga Commands**: `loan.requested` → `contribution.command.lock_funds` → `payment.command.disburse`
- **Gamification Events**: `gamification.events` (points, streaks, course completions)
- **Notifications**: `notification.command.send` (dispatched to email/SMS/WhatsApp)

### 3.3 Event Flow: Loan Disbursement Saga
```
claim-service                saga-orchestrator          contribution-service       payment-service
     │                              │                          │                        │
     │ ──loan.requested──►          │                          │                        │
     │                    persist   │                          │                        │
     │                    state     │                          │                        │
     │                              │ ─contribution.cmd.lock─► │                        │
     │                              │                          │ lock funds             │
     │                              │ ◄─funds_locked──         │                        │
     │                              │ ─payment.cmd.disburse──────────────────────────►  │
     │                              │                          │               gateway  │
     │                              │ ◄─payment.event.disbursed─────────────────────    │
     │ ◄─claim.cmd.complete─        │                          │                        │
```

---

## 4. Security Architecture

| Layer | Mechanism |
|-------|-----------|
| Transport | HTTPS/TLS 1.3 (enforced at ingress) |
| Authentication | JWT (HS256, 24h expiry) |
| Password Storage | BCrypt with cost factor 12 |
| Authorization | Spring Security `@PreAuthorize` with role claims |
| API Protection | Rate limiting per-client (X-API-Key) |
| Data Isolation | Logical database-per-service (shared Postgres instance) |
| Audit | Immutable ledger tables for all financial transactions |

---

## 5. Resilience Patterns

| Pattern | Where | Config |
|---------|-------|--------|
| **Circuit Breaker** | payment-service → Stripe | Opens after 5 failures/60s; 30s recovery |
| **Retry** | payment-service → Stripe | 3 attempts; exponential backoff (500ms, 1s, 2s) |
| **Saga + Compensation** | saga-orchestrator | Persistent state; fund unlock on payment failure |
| **Pessimistic Locking** | contribution-service | `@Lock(PESSIMISTIC_WRITE)` on fund operations |

---

## 6. Deployment Architecture

```
┌─────────────────────────────────────────┐
│            Kubernetes Cluster            │
│  ┌─────────────────────────────────┐    │
│  │  Namespace: nsip                │    │
│  │                                 │    │
│  │  10 × Deployment (1 per svc)   │    │
│  │  10 × ClusterIP Service        │    │
│  │   1 × Ingress (NGINX)          │    │
│  │   1 × ConfigMap (secrets)      │    │
│  │   1 × StatefulSet (Postgres)   │    │
│  │   1 × StatefulSet (Kafka)      │    │
│  │   1 × StatefulSet (Redis)      │    │
│  └─────────────────────────────────┘    │
│                                         │
│  CI/CD: GitHub Actions                  │
│  Registry: Container Registry           │
└─────────────────────────────────────────┘
```
