# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 0.0.1   | :white_check_mark: |

## Reporting a Vulnerability

The National Social Insurance Platform (NSIP) team takes the security of our services seriously. If you believe you have found a security vulnerability, please report it to us via the following process:

1. **Email**: security@nsip.gov.example
2. **Details**: Please include a detailed description of the vulnerability, steps to reproduce, and the potential impact.
3. **Response**: We will acknowledge receipt of your report within 48 hours and provide a timeline for resolution.

## Hardening Standards
- **Secrets Management**: Never commit `.env` files or hardcoded credentials. Use Kubernetes Secrets or AWS Secrets Manager in production.
- **Dependency Scanning**: All PRs are scanned for vulnerable dependencies using GitHub Dependency Graph.
- **Least Privilege**: Microservices should only have access to their own database schemas.
