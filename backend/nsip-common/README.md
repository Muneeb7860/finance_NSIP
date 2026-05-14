# NSIP Common Shared Library

This library contains the shared business logic, standards, and constants for the **National Social Insurance Platform (NSIP)**.

## 🏛️ Architecture Role
In our Hexagonal Architecture, this module serves as the "Global Infrastructure" that all microservices import. It ensures consistency in:
1. **Business Rules**: Centralized constants for deduction rates, loan caps, and gamification points.
2. **DTOs**: Shared message schemas for Kafka communication (Events & Commands).
3. **Exceptions**: Standardized error handling (e.g., `DomainException`).
4. **Security**: Shared JWT utilities and request correlation tracking.

## 📊 Centralized Business Rules (BRD v2.0)
To update national insurance policies, modify the following constants in `CommonConstants.java`:

- `CONTRIBUTION_RATE`: Mandatory salary deduction (Default: 4%)
- `LOAN_CAP_PERCENT`: Maximum loan based on vested savings (Default: 30%)
- `LOAN_HARD_MAX_SAR`: National hard cap for personal loans (Default: SAR 45,000)
- `VESTING_YEARS`: Minimum contribution period for claims (Default: 3 years)

## 🛠️ Usage
Add the following dependency to your microservice's `pom.xml`:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>nsip-common</artifactId>
    <version>1.0.0</version>
</dependency>
```
