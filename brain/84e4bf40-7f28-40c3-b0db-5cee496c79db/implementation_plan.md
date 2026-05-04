# Implementation Plan - NSIP Infrastructure Stabilization

We have identified a critical conflict: a native PostgreSQL instance is running on the host Mac (port 5432), which prevented the microservices from connecting to the Dockerized databases.

## Proposed Changes

### Infrastructure
- **PostgreSQL**: Map Docker port `5433` to container `5432`.
- **Database Init**: Use `docker/init-databases.sh` to create `nsip_user` and all required databases.

### Backend Services
- **Standardize JDBC URL**: Update all microservices to use `jdbc:postgresql://127.0.0.1:5433/nsip_<service>`.
- **Standardize Credentials**: Use `nsip_user` / `nsip_password`.

### Frontend
- **Vite Setup**: Ensure the proxy is configured to hit the local microservices.
- **Run Locally**: Use `npm run dev`.

## Execution Steps
1. Update `docker-compose.yml` with port 5433.
2. Update all `application.properties` / `application.yml` files.
3. Perform a clean Maven build of the entire project.
4. Start infrastructure (Postgres, Kafka, Redis).
5. Start microservices in the background.
6. Start the frontend.

## Verification Plan
- **Backend**: Check logs for successful Hibernate bootstrapping and HikariCP connection.
- **Frontend**: Verify login and dashboard functionality in the browser.
