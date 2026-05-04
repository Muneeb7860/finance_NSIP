# Architecture High-Level Design (C4 Model)

This document contains the System Context (Level 1) and Container (Level 2) diagrams for the National Social Insurance Platform, updated to include the Financial Learning & Gamification expansion.

## Level 1: System Context Diagram

```mermaid
C4Context
title System Context diagram for National Social Insurance Platform

Person(beneficiary, "Beneficiary", "Citizen or Resident using the platform")
Person(employer, "Employer", "Company representative managing payroll")
Person(admin, "Gov Admin", "Processes claims and monitors the system")
Person(advisor, "Financial Advisor", "Hosts 1-on-1 financial sessions")

System(nsip, "National Social Insurance Platform", "Allows users to view benefits, employers to pay contributions, admins to process claims, and beneficiaries to learn and earn rewards.")

System_Ext(bank, "National Payment Gateway", "Processes contribution payments")
System_Ext(identity, "National Identity Service", "SSO Authentication (e.g., Absher)")
System_Ext(labor, "Ministry of Labor", "Validates employment status & contracts")
System_Ext(video_host, "Free Video Host", "YouTube/Vimeo for LMS video streams")

Rel(beneficiary, nsip, "Views benefits, takes courses, books sessions")
Rel(employer, nsip, "Uploads payroll, pays contributions")
Rel(admin, nsip, "Approves claims, views reports")
Rel(advisor, nsip, "Manages advisory sessions")

Rel(nsip, bank, "Initiates payments")
Rel(nsip, identity, "Authenticates users")
Rel(nsip, labor, "Verifies employment data")
Rel(nsip, video_host, "Embeds video streams into LMS")
```

## Level 2: Container Diagram

```mermaid
C4Container
title Container diagram for NSIP

Person(beneficiary, "Beneficiary")
Person(employer, "Employer")

System_Boundary(c1, "National Social Insurance Platform") {
    Container(mobile_app, "Mobile App", "Flutter", "Provides core functionality to beneficiaries.")
    Container(web_app, "Web App", "React", "Provides full functionality to employers, admins, and LMS.")
    
    Container(apigee, "Apigee API Gateway", "Google Cloud Apigee", "Enterprise API Gateway for security & rate limiting.")
    
    Container(auth_service, "Auth Service", "Spring Boot", "Handles SSO, JWT generation.")
    Container(claim_service, "Claim Service", "Spring Boot", "Manages lifecycle of all insurance claims.")
    Container(contribution_service, "Contribution Service", "Spring Boot", "Manages employer payrolls and payments.")
    
    %% NEW EXPANSION SERVICES
    Container(education_service, "Education/LMS Service", "Spring Boot", "Manages courses, videos, interactive gamification.")
    Container(event_service, "Event Service", "Spring Boot", "Physical/digital event ticketing and check-ins.")
    Container(rewards_service, "Rewards & Scheduling", "Spring Boot", "Points ledger & financial advisor booking.")
    Container(notification_engine, "Notification Engine", "Spring Boot + JavaMailSender", "Sends Email/SMS.")
    
    ContainerDb(db_postgres, "Primary Database", "PostgreSQL", "Stores all structured data.")
    ContainerDb(cache_redis, "Distributed Cache", "Redis", "Caches frequent reads.")
    ContainerQueue(kafka, "Message Broker", "Apache Kafka", "Handles async events (CourseCompleted, EventAttended).")
}

Rel(beneficiary, mobile_app, "Uses")
Rel(employer, web_app, "Uses")

Rel(mobile_app, apigee, "Makes API calls to", "JSON/HTTPS")
Rel(web_app, apigee, "Makes API calls to", "JSON/HTTPS")

Rel(apigee, education_service, "Routes to")
Rel(apigee, event_service, "Routes to")
Rel(apigee, rewards_service, "Routes to")
Rel(apigee, contribution_service, "Routes to")

Rel(education_service, db_postgres, "Reads/Writes")
Rel(rewards_service, db_postgres, "Reads/Writes")
Rel(event_service, db_postgres, "Reads/Writes")

Rel(education_service, kafka, "Publishes 'CourseCompleted' to")
Rel(event_service, kafka, "Publishes 'EventAttended' to")
Rel(kafka, rewards_service, "Consumes events to award points")
Rel(kafka, notification_engine, "Consumes events to send emails")
```
