# Low-Level Design (LLD) - API Specifications

This document outlines the core RESTful APIs exposed by the Spring Boot microservices through the API Gateway.

## 1. Auth Service
Handles authentication and authorization.

*   `POST /api/v1/auth/login`
    *   **Payload:** `{ "national_id": "1234567890", "password": "..." }`
    *   **Response:** `200 OK` with JWT Token and User Info.
*   `GET /api/v1/auth/me`
    *   **Headers:** `Authorization: Bearer <token>`
    *   **Response:** User profile details (cached in Redis).

## 2. Claim Service
Handles submission and tracking of social insurance claims.

*   `POST /api/v1/claims`
    *   **Payload:** `{ "claim_type": "Retirement", "description": "Applying for standard retirement..." }`
    *   **Response:** `202 Accepted` (Produces `ClaimSubmittedEvent` to Kafka).
*   `GET /api/v1/claims/user/{user_id}`
    *   **Response:** List of claims for a specific user.
*   `PUT /api/v1/claims/{claim_id}/status` *(Admin Only)*
    *   **Payload:** `{ "status": "Approved", "reviewer_id": "..." }`
    *   **Response:** `200 OK` (Produces `ClaimStatusUpdatedEvent` to Kafka).

## 3. Contribution Service
Handles employer payroll and monthly social insurance deductions.

*   `GET /api/v1/employers/{employer_id}/employees`
    *   **Response:** List of active employments under this employer.
*   `POST /api/v1/employers/{employer_id}/payroll/upload`
    *   **Payload:** Multipart form data (CSV/Excel of salaries).
    *   **Response:** `202 Accepted` (Background task processes the file and calculates contributions).
*   `GET /api/v1/contributions?employer_id={employer_id}&month={YYYY-MM}`
    *   **Response:** List of generated contributions/invoices for the month.
*   `POST /api/v1/contributions/pay`
    *   **Payload:** `{ "employer_id": "...", "amount": 50000 }`
    *   **Response:** Integrates with Payment Gateway and updates status.

## 4. Asynchronous Events (Kafka Topics)
*   `claim.submitted`: Consumed by Notification Service to send "Claim Received" SMS.
*   `claim.status.updated`: Consumed by Notification Service.
*   `payroll.processed`: Consumed by Notification Service to alert employers that invoices are ready.
