# 06. LLD: Infrastructure & Secret Design

This document details the low-level configuration of the Azure environment and Kubernetes security.

## 1. Kubernetes Namespace Structure
| Namespace | Role |
|:---|:---|
| `nsip` | Main application namespace for all microservices. |
| `ingress-nginx` | Ingress controller for routing external traffic. |

## 2. Shared Configuration (ConfigMap)
The `nsip-azure-config` ConfigMap provides centralized environment variables:
- **DB URLs**: Points to the Azure PostgreSQL Flexible Server endpoint.
- **Kafka Bootstrap**: Points to the Azure Event Hubs endpoint (Port 9093).
- **JAVA_OPTS**: Tuned for container memory limits (`-XX:MaxRAMPercentage=75.0`).

## 3. Secret Management
Secrets are managed in the `nsip-azure-secrets` object:
- **`SPRING_DATASOURCE_PASSWORD`**: Database admin credentials.
- **`SPRING_REDIS_PASSWORD`**: Redis primary access key.
- **`EVENT_HUBS_CONNECTION_STRING`**: Shared Access Signature (SAS) for Kafka communication.

## 4. Resource Allocation (Resource Quotas)
Each deployment is configured with `limits` and `requests` to ensure cluster stability:
- **Standard**: 256Mi Memory / 200m CPU.
- **Heavy (Gateway/Auth)**: 512Mi Memory / 500m CPU.
