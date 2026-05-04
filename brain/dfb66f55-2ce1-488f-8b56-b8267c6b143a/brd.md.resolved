# Business Requirements Document (BRD)

**Project:** National Social Insurance Platform (NSIP)
**Prepared By:** Business Analyst (BA) / Business Owner (BO)

## 1. Executive Summary
This document outlines the business needs for the NSIP. The platform will serve as the central hub for managing social insurance, including retirement, health, job loss, accident coverage, and end-of-life support for the workforce.

## 2. Business Scope & Objectives
**Scope:** Development of a comprehensive mobile and web application backed by a robust microservices architecture.
**Objectives:**
- Automate the end-to-end lifecycle of a contributor from employment to retirement.
- Ensure compliance with national labor and social insurance laws.
- Provide a scalable infrastructure capable of handling high transaction volumes (contributions, claim processing).

## 3. Functional Requirements

### FR1: User Management & Authentication
- **FR1.1:** The system shall authenticate users via a National ID/Iqama number and password, with OTP via SMS/Email (2FA).
- **FR1.2:** The system shall support Role-Based Access Control (RBAC) for Beneficiaries, Employers, and Admins.

### FR2: Contribution Management (Employers)
- **FR2.1:** Employers shall be able to upload monthly payroll files to auto-calculate insurance premiums.
- **FR2.2:** The system shall integrate with national banking gateways for automated monthly deductions.

### FR3: Benefits & Claims Processing
- **FR3.1 - Retirement:** The system shall automatically notify users when they are eligible for retirement based on age and contribution months.
- **FR3.2 - Job Loss:** Unemployed users shall be able to file a job loss claim. The system must verify employment status with the Ministry of Labor before approving.
- **FR3.3 - Accidental Coverage:** Users shall be able to upload medical reports and police reports for workplace accidents.
- **FR3.4 - End of Life:** Next-of-kin shall be able to submit death certificates to trigger survivor pension calculations.

### FR4: Notifications & Asynchronous Processing
- **FR4.1:** The system shall notify users of claim status changes (Submitted, Under Review, Approved, Rejected) via Push Notification, SMS, and Email. (Handled via Kafka).

## 4. Non-Functional Requirements

### NFR1: Performance & Caching
- **NFR1.1:** Frequently accessed data (e.g., user profiles, hospital networks) must be cached using **Redis** to reduce database load.

### NFR2: Data Integrity & Storage
- **NFR2.1:** All transactional data (contributions, claims) must be stored in a relational database (**PostgreSQL**) with strict ACID properties.

### NFR3: Event-Driven Architecture
- **NFR3.1:** High-volume operations like monthly payroll processing across all employers must use **Apache Kafka** to distribute the workload and prevent system timeouts.

## 5. Business Rules
- **BR1:** Retirement requires a minimum of 120 months of contributions and a minimum age of 60 (configurable).
- **BR2:** Job loss support is only applicable if the termination was not due to disciplinary action.
- **BR3:** Accident coverage applies only if the accident occurred during working hours or during the commute to/from the workplace.
