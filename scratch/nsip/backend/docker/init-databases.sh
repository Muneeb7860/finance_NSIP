#!/bin/bash
# Creates a separate database for each microservice inside the shared Postgres instance.
# This enforces logical isolation (Database-per-Service pattern) without running 10 Postgres containers.
#
# FLAW #18 FIX: Each service now has its own database. Schema migrations in one
# service cannot accidentally break another service's tables.

set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER nsip_user WITH PASSWORD 'nsip_password';

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

    GRANT ALL PRIVILEGES ON DATABASE nsip_auth TO nsip_user;
    GRANT ALL PRIVILEGES ON DATABASE nsip_claims TO nsip_user;
    GRANT ALL PRIVILEGES ON DATABASE nsip_contributions TO nsip_user;
    GRANT ALL PRIVILEGES ON DATABASE nsip_education TO nsip_user;
    GRANT ALL PRIVILEGES ON DATABASE nsip_events TO nsip_user;
    GRANT ALL PRIVILEGES ON DATABASE nsip_rewards TO nsip_user;
    GRANT ALL PRIVILEGES ON DATABASE nsip_saga TO nsip_user;
    GRANT ALL PRIVILEGES ON DATABASE nsip_payments TO nsip_user;
    GRANT ALL PRIVILEGES ON DATABASE nsip_reviews TO nsip_user;
    GRANT ALL PRIVILEGES ON DATABASE nsip_notifications TO nsip_user;
EOSQL

echo "✅ All 10 per-service databases created successfully."
