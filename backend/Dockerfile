FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Layer 1: Cache parent + child POMs (only invalidated when POMs change)
COPY pom.xml .
COPY auth-service/pom.xml auth-service/
COPY claim-service/pom.xml claim-service/
COPY contribution-service/pom.xml contribution-service/
COPY education-service/pom.xml education-service/
COPY event-service/pom.xml event-service/
COPY notification-engine/pom.xml notification-engine/
COPY payment-service/pom.xml payment-service/
COPY review-service/pom.xml review-service/
COPY rewards-service/pom.xml rewards-service/
COPY saga-orchestrator/pom.xml saga-orchestrator/
COPY nsip-common/pom.xml nsip-common/
COPY api-gateway/pom.xml api-gateway/

# Layer 2: Resolve all deps offline (cached unless POMs change)
RUN mvn dependency:go-offline -B -q

# Layer 3: Build the specific service
COPY . .
ARG SERVICE_NAME
RUN mvn clean package -pl ${SERVICE_NAME} -am -DskipTests -B -q

# ── Runtime: JRE (Multi-arch support for Apple Silicon) ──────────────────────────
FROM eclipse-temurin:17-jre
WORKDIR /app

# Security: run as non-root (Debian syntax)
RUN groupadd -r nsip && useradd -r -g nsip nsip
USER nsip

ARG SERVICE_NAME
COPY --from=build /app/${SERVICE_NAME}/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
