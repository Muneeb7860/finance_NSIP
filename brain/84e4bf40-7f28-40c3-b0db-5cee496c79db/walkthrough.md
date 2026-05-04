# NSIP Stabilization Walkthrough

We have successfully stabilized the NSIP platform. The primary blocker was a **port conflict** where a native PostgreSQL instance on the host machine was intercepting traffic meant for the Dockerized database.

## Key Changes

### 1. Infrastructure (PostgreSQL)
- **Port Migration**: Docker PostgreSQL now maps to host port `5433`.
- **Authentication**: Reverted to standard password authentication using `nsip_user` / `nsip_password`.
- **Initialization**: Automatic creation of all 10 service databases via `init-databases.sh`.

### 2. Backend Fixes
- **Port Standardization**: All `application.properties` updated to target `127.0.0.1:5433`.
- **Payment Service**: Marked `StripeAdapter` as `@Primary` to resolve bean ambiguity.
- **Education Service**: Renamed conflicting `/api/v1/learning/courses` endpoint in `LearningController` to `/api/v1/learning/list`.
- **Contribution Service**: Added missing `createdAt` field and fixed JPQL query syntax.

### 3. Frontend
- Launched on `http://localhost:5175/` (Vite).

## Verification Results

| Service | Status | Log Confirmation |
|---------|--------|------------------|
| Auth | ✅ Up | `Started AuthServiceApplication` |
| Claim | ✅ Up | `Started ClaimServiceApplication` |
| Contribution| ✅ Up | `Started ContributionServiceApplication` |
| Education | ✅ Up | `Started EducationServiceApplication` |
| Event | ✅ Up | `Started EventServiceApplication` |
| Notification| ✅ Up | `Started NotificationEngineApplication` |
| Payment | ✅ Up | `Started PaymentServiceApplication` |
| Review | ✅ Up | `Started ReviewServiceApplication` |
| Rewards | ✅ Up | `Started RewardsServiceApplication` |
| Saga | ✅ Up | `Started SagaOrchestratorApplication` |

## How to Run Locally

If you wish to run the code on your own machine:

1. **Infrastructure**:
   ```bash
   cd backend
   docker-compose up -d
   ```
2. **Backend**:
   ```bash
   cd backend
   mvn clean install -DskipTests
   # Start services (using the script provided in the root)
   ```
3. **Frontend**:
   ```bash
   cd frontend-web
   npm install
   npm run dev
   ```
