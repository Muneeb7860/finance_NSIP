# Product Requirements Document (PRD)
## National Social Insurance Platform (NSIP)

**Version:** 2.0 | **Date:** May 2026

---

## 1. Product Overview

NSIP is a multi-tenant, event-driven microservices platform serving 3 user types across web and mobile. It handles financial contributions, claims, education, events, wellness, and advisor sessions with full gamification.

---

## 2. User Personas & Journeys

### 2.1 Contributor (Customer)
```
Register → View Portfolio → Apply for Loan → Track Saga Status
         → Complete Courses → Take Quizzes → Earn Points → Book Advisor Session
         → Use BMC/EMF → Build Streaks → Earn Certificates
         → Browse Events → RSVP to LIVE Events
         → Enroll in Chronic Care Programs
         → Top-up Digital Wallet
```

### 2.2 Business Owner (Employer)
```
Login → Upload Payroll CSV → Pay Contributions via Gateway
      → Propose Event → Track Approval Status (DRAFT → L1 → L2 → LIVE)
```

### 2.3 Back Office Admin
```
Login → Approve/Reject Claims (with saga metrics)
      → Approve/Reject Events (3-layer pipeline)
      → Manage Wellness Content
```

### 2.4 Financial Advisor
```
Register Profile → Set Specialty & Bio → View Schedule
                 → Accept/Reschedule/Cancel Sessions
                 → View Average Rating & Reviews
```

---

## 3. Feature Catalog

### 3.1 Core Features

| Feature | Service | Status |
|---------|---------|--------|
| User Registration & Login (JWT + BCrypt) | auth-service | ✅ |
| Contribution Tracking (4% deduction) | contribution-service | ✅ |
| Pension Estimation | contribution-service | ✅ |
| Claim Submission & Approval | claim-service | ✅ |
| Personal Loan (30% cap, vesting check) | claim-service + saga | ✅ |
| Loan Repayment (installment tracking) | payment-service | ✅ |

### 3.2 Financial Features

| Feature | Service | Status |
|---------|---------|--------|
| Digital Wallet (balance, top-up, auto-debit) | payment-service | ✅ |
| Payment Gateway (Stripe, Resilience4j) | payment-service | ✅ |
| Budget Management Calculator (BMC) | Frontend | ✅ |
| Emergency Fund Calculator (EMF) | Frontend | ✅ |

### 3.3 Education & Gamification

| Feature | Service | Status |
|---------|---------|--------|
| LMS Course Catalog | education-service | ✅ |
| Quiz with Retry + Diminishing Points | education-service | ✅ |
| Certificate Issuance (≥90% score) | education-service | ✅ |
| Weekly Streak (≥4 days/week) | education-service | ✅ |
| Monthly Streak (4 weekly streaks) | education-service | ✅ |
| Activity Logging (11 activity types) | education-service | ✅ |
| Centralised Points System | rewards-service | ✅ |

### 3.4 Advisor Booking

| Feature | Service | Status |
|---------|---------|--------|
| Advisor Self-Registration | education-service | ✅ |
| Session Booking (-1,000 pts) | education-service | ✅ |
| Session Cancellation (full refund) | education-service | ✅ |
| Session Rescheduling | education-service | ✅ |
| Post-Session Review (1-5 stars) | education-service | ✅ |
| Denormalized Average Rating | education-service | ✅ |

### 3.5 Events

| Feature | Service | Status |
|---------|---------|--------|
| Event Proposal by Employers | event-service | ✅ |
| 3-Layer Approval (L1→L2→L3→LIVE) | event-service | ✅ |
| RSVP Guard (LIVE only) | event-service | ✅ |
| Approval Audit Trail | event-service | ✅ |

### 3.6 Wellness

| Feature | Service | Status |
|---------|---------|--------|
| Fitness Tips & Suggestions | education-service | ✅ |
| Chronic Disease Home Assistance | education-service | ✅ |
| Program Enrollment Tracking | education-service | ✅ |

### 3.7 Platform

| Feature | Service | Status |
|---------|---------|--------|
| Notification Engine (Kafka-driven) | notification-engine | ✅ |
| Omni-Channel Support (WhatsApp, SMS, Email) | Frontend | ✅ |
| Saga Orchestration (persistent state) | saga-orchestrator | ✅ |

---

## 4. Point Economics Model

```
EARNING:
  6 courses × 100 pts avg           = 600 pts
  Quiz bonus (30 pts avg per course) = 180 pts
  2 weekly streaks                   = 200 pts
  Event attendance (2 events)        = 100 pts
  ─────────────────────────────────────────────
  Total after ~2 weeks              = 1,080 pts ✅ (exceeds 1,000 threshold)

SPENDING:
  Advisor session                   = -1,000 pts
  Session cancellation              = +1,000 pts (full refund)
```

---

## 5. Non-Functional Requirements

| Requirement | Target |
|-------------|--------|
| Availability | 99.9% uptime (circuit breaker on payment gateway) |
| Latency | <200ms API response (p95) |
| Scalability | Horizontal via Kubernetes StatefulSets |
| Security | BCrypt-12, JWT HS256, RBAC, HTTPS-only |
| Audit | Immutable ledgers for all financial transactions |
| Observability | Structured logging via SLF4J, Kafka event tracing |
