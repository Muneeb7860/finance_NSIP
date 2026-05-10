# 10. Current Problems & Outstanding Items

This document identifies remaining technical debt, performance bottlenecks, and monitoring gaps in the NSIP platform.

## 1. High Priority (Blockers)
- [ ] **None**: The platform is currently operational and passing health checks in AKS.

## 2. Medium Priority (Optimization)
| Item | Description | Impact |
|:---|:---|:---:|
| **Cold Starts** | Java microservices have ~30s startup time. | Slow horizontal scaling. |
| **Log Centralization** | Pod logs are only accessible via `kubectl`. | Hard to debug production issues. |
| **Dapr Component Polish** | Local state stores need to be switched to Azure CosmosDB in production. | Reliability. |

## 3. Low Priority (Technical Debt)
- **Shared Library Versioning**: `nsip-common` is currently copied/linked. Should be moved to a private Maven repository.
- **Frontend Environment Variables**: Currently uses `.env` files. Should move to dynamic runtime injection.
- **SSL Termination**: Ingress uses HTTP. Needs cert-manager integration for Let's Encrypt.

## 4. Monitoring Gaps
- **Prometheus/Grafana**: Not yet deployed to the `nsip` namespace.
- **Distributed Tracing**: Zipkin/Jaeger needs configuration for cross-service visualization.
