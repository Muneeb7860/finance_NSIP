FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy parent pom
COPY pom.xml .

# Copy all child poms for dependency caching
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

# Resolve dependencies
RUN mvn dependency:go-offline -B

# Copy source and build
COPY . .
ARG SERVICE_NAME
RUN mvn clean package -pl ${SERVICE_NAME} -am -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
ARG SERVICE_NAME
COPY --from=build /app/${SERVICE_NAME}/target/*.jar app.jar
EXPOSE 8080
# Use sh -c to allow environment variable expansion for JAVA_OPTS
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
