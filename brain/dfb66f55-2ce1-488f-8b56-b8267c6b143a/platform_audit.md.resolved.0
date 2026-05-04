# NSIP Platform Audit: PO & Architect Review

**Auditor:** Platform Review Board  
**Date:** May 1, 2026  
**Scope:** All 10 Java microservices, React Web, Flutter Mobile, DevOps  
**Codebase:** 1,833 lines Java · 51 source files · 10 microservices

---

## Executive Summary

The platform demonstrates strong architectural vision — microservices, event-driven Saga, multi-role access, and omnichannel support are all correctly designed at the container level. However, a deep code review reveals **28 critical and high-severity issues** that must be resolved before any staging deployment. The most urgent gaps are in **security**, **data integrity**, and **test coverage**.

---

## 🔴 CRITICAL: Security Vulnerabilities (Must Fix Before Any Deployment)

### FLAW 1: Passwords Stored in Plaintext
> [!CAUTION]
> [AuthService.java L34](file:///Users/muneeb/.gemini/antigravity/scratch/nsip/backend/auth-service/src/main/java/com/example/auth_service/service/AuthService.java#L34): `user.setPasswordHash(rawPassword)` stores the raw password directly. This is a **data breach liability**.

**Fix:** Integrate `BCryptPasswordEncoder` from Spring Security.

### FLAW 2: No JWT Signature or Validation
> [!CAUTION]
> [AuthService.java L54](file:///Users/muneeb/.gemini/antigravity/scratch/nsip/backend/auth-service/src/main/java/com/example/auth_service/service/AuthService.java#L54): The JWT token is a hardcoded string concatenation: `"eyJ..." + userId + role`. This is **not a real JWT** — it has no signature, no expiry, and no validation. Any user can forge any role.

**Fix:** Use `io.jsonwebtoken:jjwt` library with HMAC-SHA256 signing and expiry claims.

### FLAW 3: No Authentication/Authorization on ANY Endpoint
> [!CAUTION]
> Every single REST controller across all 9 services has **zero authentication**. There are no `@PreAuthorize`, no JWT filters, no Spring Security configuration. The Admin claim approval endpoint is as open as the public course listing.

**Fix:** Add Spring Security with JWT filter chain to every service. Implement role-based access (`ADMIN`-only for approvals, `CUSTOMER`-only for claims).

### FLAW 4: Secrets in K8s Manifests
> [!WARNING]
> [config.yaml](file:///Users/muneeb/.gemini/antigravity/scratch/nsip/k8s/config.yaml): Database passwords and API keys are committed as plaintext `stringData` in the K8s Secret manifest. This will be visible in version control.

**Fix:** Use a secrets manager (AWS Secrets Manager, HashiCorp Vault) or at minimum use `kubectl create secret` imperatively and add the file to `.gitignore`.

---

## 🟠 HIGH: Data Integrity & Race Conditions

### FLAW 5: Points Deduction Race Condition
> [!WARNING]
> [RewardsService.java L67-77](file:///Users/muneeb/.gemini/antigravity/scratch/nsip/backend/rewards-service/src/main/java/com/example/rewards_service/service/RewardsService.java#L67-L77): The `bookSession()` method reads the balance, checks if >= 1000, then writes the deduction in **two separate database calls with no transaction lock**. If a user sends two simultaneous requests, both could pass the balance check and double-spend their points.

**Fix:** Wrap in `@Transactional` with a pessimistic lock (`SELECT ... FOR UPDATE`) on the user's ledger aggregate, or use an optimistic locking strategy.

### FLAW 6: Saga Has No State Persistence
> [!WARNING]
> [LoanSagaManager.java](file:///Users/muneeb/.gemini/antigravity/scratch/nsip/backend/saga-orchestrator/src/main/java/com/example/saga_orchestrator/service/LoanSagaManager.java): The Saga orchestrator is completely **stateless**. There is no `SagaState` table tracking which step each transaction is on. If the orchestrator pod restarts mid-saga, the transaction is permanently lost — funds could be locked forever with no disbursement and no rollback.

**Fix:** Add a `saga_state` table with columns: `sagaId`, `claimId`, `currentStep`, `status`, `createdAt`. Update state at each step transition.

### FLAW 7: Fund Lock is Simulated, Not Real
> [!WARNING]
> [FundLockService.java L32-42](file:///Users/muneeb/.gemini/antigravity/scratch/nsip/backend/contribution-service/src/main/java/com/example/contribution_service/service/FundLockService.java#L32-L42): The vesting date is hardcoded (`2020-01-01`) and total savings is hardcoded (`150000`). There is no actual database query. The fund lock doesn't modify any database record, so the "lock" doesn't actually prevent concurrent withdrawals.

**Fix:** Query the actual contribution history from the DB, calculate real totals, and create a `fund_locks` table with an idempotency key.

### FLAW 8: Kafka Payloads are Raw Strings, Not Typed
> [!WARNING]
> Every Kafka message is built with `String.format()` manually. This is fragile — a typo in the JSON key will silently break the consumer. The `handleGamificationEvent()` in RewardsService [L35-40](file:///Users/muneeb/.gemini/antigravity/scratch/nsip/backend/rewards-service/src/main/java/com/example/rewards_service/service/RewardsService.java#L35-L40) doesn't even parse the payload — it just logs it.

**Fix:** Create shared DTO/Event classes (e.g., `LoanRequestedEvent`, `CourseCompletedEvent`) and use Jackson `ObjectMapper` for serialization/deserialization. Consider Avro schemas with Schema Registry.

---

## 🟡 MEDIUM: Business Logic Gaps

### FLAW 9: No Loan Repayment Logic
**PO Question:** The user confirmed this is a **loan** (not a withdrawal). But there is zero repayment logic anywhere. How does the user pay back? What's the interest rate? What's the repayment schedule? What happens on default?

### FLAW 10: No Loan Amount Validation Against Credit Limit
The claim submission endpoint accepts any `amount` for a `PERSONAL_LOAN`. There is no validation that the requested amount is within the 30% personal limit (SAR 45,000). A user could request SAR 1,000,000.

### FLAW 11: No Cancellation Window for Advisor Sessions
Users can cancel sessions at any time and get a full refund. There should be a cancellation policy (e.g., no refund within 24 hours of the session).

### FLAW 12: No Duplicate RSVP Prevention
The event RSVP endpoint has no check for whether a user has already RSVP'd. A user could RSVP 100 times.

### FLAW 13: No Course Completion Tracking
The education service has a `LearningGamificationController` that awards points, but there's no `UserProgress` entity or tracking table. A user could call the `/complete` endpoint repeatedly to farm infinite points.

### FLAW 14: Pension Estimation is Static
The frontend shows "SAR 8,450" as a hardcoded value. There is no backend endpoint or formula that calculates estimated pension based on actual contribution history, age, and years of service.

---

## 🔵 ARCHITECTURE: Structural Issues

### FLAW 15: No Service Discovery
With 10 microservices, there is no service discovery mechanism (Eureka, Consul, or K8s DNS). Services can't find each other dynamically.

**Note:** In K8s, ClusterIP services provide basic DNS. But for local Docker Compose development, inter-service HTTP calls would fail.

### FLAW 16: No API Gateway (Apigee is Not Implemented)
The PRD specifies Apigee for API security and rate limiting. There is no Apigee proxy configuration, no API keys, and no rate limiting on any endpoint. The K8s Ingress does basic routing but provides none of Apigee's features.

### FLAW 17: No Circuit Breaker or Retry Logic
If the payment gateway is down, the Saga simply fails. There's no retry mechanism, no circuit breaker (Resilience4j), and no dead letter queue for failed Kafka messages.

### FLAW 18: Single Postgres Database
All 10 microservices share ONE Postgres database (`nsip_db`). This defeats the purpose of microservices — a schema migration in one service could break another. Each service should own its own database (Database-per-Service pattern).

### FLAW 19: No application.properties Configuration
Every service's `application.properties` only contains `spring.application.name=xxx`. There are no database connection strings, no Kafka configs, no server port configs. The services won't start without proper configuration.

### FLAW 20: No Swagger/OpenAPI Implementation
The PRD required Swagger documentation. No service has `springdoc-openapi` dependency or `@Operation` annotations. The Swagger UI mentioned in early C4 diagrams was never implemented.

---

## 🧪 TESTING: Zero Coverage

### FLAW 21: No Unit Tests Written
> [!CAUTION]
> All 10 test files are auto-generated Spring Boot context loaders (`@SpringBootTest` with empty body). There are **zero** actual unit tests across the entire platform. For a government financial system handling loans and insurance, this is unacceptable.

**Minimum required:**
- `ContributionCalculatorServiceTest` — validate 4% deduction with edge cases (zero salary, negative, decimal precision)
- `RewardsServiceTest` — test point deduction atomicity, insufficient balance, double-booking
- `LoanSagaManagerTest` — test each saga step independently with mock Kafka
- `ClaimServiceTest` — validate claim type routing to Saga

### FLAW 22: No Integration Tests
No tests verify the Kafka topic wiring (e.g., does `claim-service` publishing to `loan.requested` actually get consumed by `saga-orchestrator`?).

---

## 🎨 FRONTEND: UX & Data Gaps

### FLAW 23: All Data is Hardcoded (React + Flutter)
Every number on screen (SAR 150,000, 850 pts, contribution history) is hardcoded in the JSX/Dart. There are **zero API calls** to any backend service. The frontend is a static mockup.

### FLAW 24: No Error Handling or Loading States
Neither the React nor Flutter app has loading spinners, error boundaries, or offline handling.

### FLAW 25: No Form Validation
The loan application, registration, and calculator inputs have no validation. A user can submit an empty form or enter negative numbers.

### FLAW 26: No Routing in React
The React app uses `useState` for tab switching instead of `react-router-dom`. Users can't share links to specific pages, and the browser back button doesn't work.

### FLAW 27: Flutter Has No API Service Layer
There's no `http` package, no API client, no state management (Provider/Riverpod/Bloc). The app is purely a UI shell.

---

## 📋 PO Questions (Business Decisions Needed)

| # | Question | Impact |
|---|---|---|
| 1 | **Loan Interest Rate:** What is the interest rate on personal loans? Is it 0% (Islamic finance compliant) or market rate? | Affects repayment calculation engine |
| 2 | **Loan Repayment Schedule:** Monthly installments deducted from salary? Lump sum? How many months? | Requires new `Repayment` entity and scheduled jobs |
| 3 | **Default Handling:** What happens if a user defaults on loan repayment? Pension reduction? Legal action? | Affects claim lifecycle states |
| 4 | **Employer vs Employee Contribution:** Is the 4% split between employer and employee (e.g., 2%+2%) or fully employee-paid? | Affects contribution calculation |
| 5 | **Multi-Currency:** Will this system only handle SAR, or will it need to support transfers in USD/EUR for expatriates? | Affects payment entity schema |
| 6 | **KYC/AML Compliance:** For a government financial platform, where is the Know Your Customer and Anti-Money Laundering verification? | Requires integration with National ID verification API |
| 7 | **Audit Trail:** There is no audit log. For a government system, every state change (claim approval, fund lock, payment) must have an immutable audit trail. Who approved what and when? | Requires `audit_log` table with before/after snapshots |
| 8 | **Data Residency:** Government data must stay within national borders. Which cloud region will be used? Is there a data residency policy? | Affects K8s cluster region selection |
| 9 | **Accessibility (a11y):** The React and Flutter UIs have no ARIA labels, no screen reader support, no keyboard navigation. Government platforms are legally required to be accessible. | Requires WCAG 2.1 AA compliance pass |
| 10 | **Downtime & SLA:** What is the target uptime SLA? 99.9% (8.7 hrs/year downtime) or 99.99%? | Affects replica count, multi-region strategy |

---

## Prioritized Remediation Roadmap

| Priority | Category | Flaws | Effort |
|---|---|---|---|
| 🔴 P0 | Security | #1, #2, #3, #4 | 2-3 days |
| 🔴 P0 | Data Integrity | #5, #6, #7 | 2-3 days |
| 🟠 P1 | Kafka Contracts | #8 | 1-2 days |
| 🟠 P1 | Config & Infra | #18, #19, #20 | 1 day |
| 🟡 P2 | Business Logic | #9, #10, #11, #12, #13, #14 | 3-4 days |
| 🟡 P2 | Architecture | #15, #16, #17 | 2-3 days |
| 🔵 P3 | Testing | #21, #22 | 3-5 days |
| 🔵 P3 | Frontend Wiring | #23, #24, #25, #26, #27 | 3-4 days |

**Total estimated remediation: 17-25 engineering days**
