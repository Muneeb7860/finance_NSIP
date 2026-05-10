# 09. Code Fixes Applied

This document tracks the critical fixes and stabilization efforts applied to the NSIP platform to ensure production readiness on Azure.

## 1. Infrastructure Stabilization (Terraform)
| Issue | Fix Applied | Status |
|:---|:---|:---:|
| **Missing Message Broker** | Provisioned **Azure Event Hubs** (Standard SKU) with Kafka support. | ✅ |
| **Database Firewall Blocking** | Added temporary and permanent firewall rules to allow AKS traffic to PostgreSQL. | ✅ |
| **ACR Pull Permissions** | Attached ACR to AKS using `az aks update --attach-acr`. | ✅ |

## 2. Deployment & CI/CD Pipeline (GitHub Actions)
| Issue | Fix Applied | Status |
|:---|:---|:---:|
| **Architecture Mismatch** | Switched from local builds (ARM64) to cloud-based GitHub Runners (AMD64). | ✅ |
| **Sequential Build Bottleneck** | Implemented **Matrix Strategy** to build 13 services in parallel. | ✅ |
| **Auth Failures (OIDC vs Secret)** | Reverted to stable `creds` JSON method and added `id-token` permissions. | ✅ |
| **Build Slowness** | Enabled **Docker Buildx GHA caching** (`type=gha`) for Maven and Node layers. | ✅ |
| **Missing Frontend Image** | Created a multi-stage `Dockerfile` for the `frontend-web` Vite application. | ✅ |

## 3. Application Hardening
| Issue | Fix Applied | Status |
|:---|:---|:---:|
| **CORS Policy** | Hardened `api-gateway` to allow explicit production origins (`nsip-web.azurewebsites.net`). | ✅ |
| **Secret Management** | Moved hardcoded DB/Redis passwords to Kubernetes Secrets (`nsip-azure-secrets`). | ✅ |
| **LiveKit Integration** | Finalized `LiveKitController` in `auth-service` for secure AI agent token generation. | ✅ |

## 4. Known Issues Resolved
- [x] **Exec Format Error**: Resolved by switching to AMD64 runners.
- [x] **401 Unauthorized (ACR)**: Resolved by adding `docker/login-action`.
- [x] **PostgreSQL Connection Timeout**: Resolved by updating database firewall rules.
