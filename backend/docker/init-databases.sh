#!/bin/bash
# Creates a separate database for each microservice inside the shared Postgres instance.
# This enforces logical isolation (Database-per-Service pattern) without running 10 Postgres containers.
#
# FLAW #18 FIX: Each service now has its own database. Schema migrations in one
# service cannot accidentally break another service's tables.

set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER auth_user WITH PASSWORD 'auth_pass';
    CREATE USER claim_user WITH PASSWORD 'claim_pass';
    CREATE USER contribution_user WITH PASSWORD 'contribution_pass';
    CREATE USER education_user WITH PASSWORD 'education_pass';
    CREATE USER event_user WITH PASSWORD 'event_pass';
    CREATE USER rewards_user WITH PASSWORD 'rewards_pass';
    CREATE USER saga_user WITH PASSWORD 'saga_pass';
    CREATE USER payment_user WITH PASSWORD 'payment_pass';
    CREATE USER review_user WITH PASSWORD 'review_pass';
    CREATE USER notification_user WITH PASSWORD 'notification_pass';

    CREATE DATABASE nsip_auth;
    CREATE DATABASE nsip_claims;
    CREATE DATABASE nsip_contributions;
    CREATE DATABASE nsip_education;
    CREATE DATABASE nsip_events;
    CREATE DATABASE nsip_rewards;
    CREATE DATABASE nsip_saga;
    CREATE DATABASE nsip_payments;
    CREATE DATABASE nsip_reviews;
    CREATE DATABASE nsip_notifications;

    GRANT ALL PRIVILEGES ON DATABASE nsip_auth TO auth_user;
    GRANT ALL PRIVILEGES ON DATABASE nsip_claims TO claim_user;
    GRANT ALL PRIVILEGES ON DATABASE nsip_contributions TO contribution_user;
    GRANT ALL PRIVILEGES ON DATABASE nsip_education TO education_user;
    GRANT ALL PRIVILEGES ON DATABASE nsip_events TO event_user;
    GRANT ALL PRIVILEGES ON DATABASE nsip_rewards TO rewards_user;
    GRANT ALL PRIVILEGES ON DATABASE nsip_saga TO saga_user;
    GRANT ALL PRIVILEGES ON DATABASE nsip_payments TO payment_user;
    GRANT ALL PRIVILEGES ON DATABASE nsip_reviews TO review_user;
    GRANT ALL PRIVILEGES ON DATABASE nsip_notifications TO notification_user;
EOSQL

echo "✅ All 10 per-service databases created successfully."
