# Distributed Architecture & Omnichannel Expansion Plan

## Goal Description
Evolve the platform's architecture to support enterprise-grade reliability and communication. This involves implementing the **Saga Design Pattern** for distributed transactions, integrating a **Payment Gateway** for seamless contributions/loan disbursements, expanding to an **Omnichannel Notification System**, and introducing a **Review & Metrics Engine** for all features.

## Proposed Architecture Changes

### 1. The Saga Pattern (Distributed Transactions)
*   **Orchestration Saga:** We will implement an Orchestration-based Saga Pattern to ensure data consistency across our microservices without traditional 2-Phase Commits. 
*   **Saga Orchestrator Service:** A new microservice responsible for managing distributed workflows. 
    *   *Example Workflow (Loan Disbursement):* User requests loan -> Orchestrator tells `claim-service` to approve -> Orchestrator tells `contribution-service` to lock funds -> Orchestrator tells `payment-service` to disburse funds. If payment fails, it sends a compensating transaction to unlock the funds in the `contribution-service`.

### 2. Payment Gateway Integration
*   **Payment Service:** Scaffold a new `payment-service` microservice.
*   **Integration:** Connect with an external payment gateway to handle auto-debits for monthly contributions and direct deposits for loan/claim payouts.

### 3. Omnichannel Notification Engine
*   Upgrade the `notification-engine` from just Email to a full Omnichannel dispatcher.
*   Integrate APIs for: **WhatsApp Business**, **Instagram Direct**, **X (Twitter) DM API**, **Snapchat**, and **SMS**. Users can specify their preferred channel for critical alerts.

### 4. Review & Metrics Engine
*   **Review Service:** Scaffold a new `review-service` to handle user feedback.
*   Users can leave 1-5 star ratings and text reviews on any platform feature (e.g., rate a course in the LMS, rate the loan application process).
*   Metrics will be aggregated for the Admin Dashboard to track user satisfaction.

## Target User Flow
1. **Loan Application (Saga in Action):** A user applies for an emergency loan. The Saga Orchestrator coordinates the approval, locks the 70% emergency credit, and talks to the Payment Gateway to send the money.
2. **Omnichannel Alerts:** The user receives a WhatsApp message and an Email confirming the money is in their bank account.
3. **Review:** A week later, they get an SMS asking them to review the loan application process. They rate it 5 stars inside the React App.

---

> [!IMPORTANT]
> ## User Review Required
> This phase upgrades the platform from a standard microservices app into a **Distributed Event-Driven Enterprise System**. 
> 
> Please review the proposed Saga Orchestration and Omnichannel architecture. If approved, I will map the Orchestrator, Payment, and Review services into our C4 diagrams and begin scaffolding!

> [!NOTE]
> ## Open Questions
> 1. **Saga Framework:** Should we build the Saga Orchestrator manually using native Spring Boot / Kafka topics, or should we integrate an enterprise workflow engine like **Camunda** or **Temporal**?
> 2. **Payment Gateway:** Do you have a specific payment provider in mind (e.g., Stripe, PayPal, or Moyasar/Tap Payments for the GCC region)?
