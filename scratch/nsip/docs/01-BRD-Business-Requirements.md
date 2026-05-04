# Business Requirements Document (BRD)
## National Social Insurance Platform (NSIP)

**Version:** 2.0 | **Date:** May 2026 | **Classification:** Confidential

---

## 1. Executive Summary

NSIP is a national-scale social insurance platform that manages mandatory employer–employee contribution programs across the country. The platform provides digital insurance, financial planning, education, wellness, and event management services to three user personas: Contributors (Customers), Business Owners (Employers), and Back Office Administrators.

---

## 2. Business Objectives

| # | Objective | KPI |
|---|-----------|-----|
| BO-1 | Digitize national social insurance contributions | 100% digital payroll processing within 12 months |
| BO-2 | Enable financial literacy among contributors | 80% of users complete ≥1 course within 6 months |
| BO-3 | Provide self-service loan and claim management | 90% reduction in manual claim processing |
| BO-4 | Support employer-organized community events | 500+ approved events per quarter |
| BO-5 | Deliver wellness and chronic care programs | 10,000 chronic care enrollments within 12 months |
| BO-6 | Ensure regulatory compliance and audit trails | Zero compliance violations; full transaction auditability |

---

## 3. Stakeholders

| Role | Description | Platform Access |
|------|-------------|----------------|
| **Contributor (Customer)** | Employee who receives 4% salary deduction into insurance fund | Mobile app (Flutter) + Web |
| **Business Owner (Employer)** | Organization that uploads payroll and proposes events | Web portal |
| **Back Office Admin** | NSIP staff who approve claims, events, and manage the platform | Web portal (elevated privileges) |
| **Financial Advisor** | Registered expert who offers paid sessions to contributors | Web portal (self-managed) |

---

## 4. Business Rules

### 4.1 Contributions
- Mandatory 4% salary deduction per employee per month
- Employer uploads payroll CSV; system auto-calculates deductions
- 3-year minimum vesting period before funds can be claimed

### 4.2 Claims & Loans
- Personal loan capped at 30% of vested savings (max SAR 45,000)
- Emergency relief: requires supporting documentation
- All claims go through saga-orchestrated workflow (lock funds → disburse → notify)

### 4.3 Event Approvals
- Business owners propose events (Ramadan, 5K runs, meetups, etc.)
- 3-layer mandatory approval: L1 Reviewer → L2 Manager → L3 Director
- Only `LIVE` events are visible to contributors
- Every approval/rejection is audited with timestamp and reason

### 4.4 Gamification & Points
- Course completion: 50–150 pts (1st attempt = 100%, 2nd = 50%, 3rd+ = 25%)
- Quiz bonus: +1 pt per % above passing score
- Weekly streak (≥4 active days): +100 pts
- Monthly streak (4 consecutive weekly streaks): +500 pts
- Advisor session: -1,000 pts (refunded on cancellation)
- Every user must be able to earn ≥1,000 pts through normal usage

### 4.5 Wellness
- Fitness tips are content-managed by admins
- Chronic care programs require enrollment; track home visits and teleconsults
- Programs are covered under insurance plan

---

## 5. Regulatory & Compliance

| Requirement | Implementation |
|-------------|---------------|
| Data Privacy | BCrypt-12 password hashing; JWT with HS256; HTTPS-only |
| Audit Trail | Immutable `EventApproval`, `WalletTransaction`, `ActivityLog` tables |
| Financial Regulation | Saga-based fund locking prevents double-spend; pessimistic locking on contributions |
| Uptime SLA | Circuit breaker on payment gateway (95% uptime guarantee) |

---

## 6. Success Criteria

1. All 10 microservices deployable via Docker Compose in <5 minutes
2. Full e2e claim-to-disbursement saga completes in <3 seconds
3. Zero data inconsistency across services under concurrent load
4. 3-layer event approval workflow fully auditable
5. Contributor achieves 1,000 points within 2 weeks of active usage
