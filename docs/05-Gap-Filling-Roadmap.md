# 05. Gap Filling Roadmap

This roadmap outlines the steps required to move from the current **Stabilized Production** state to a **Fully Mature Government-Scale Platform**.

## 🚀 Phase 1: Operational Excellence (Current - 1 Month)
- [ ] **Monitoring & Alerting**: Deploy Prometheus/Grafana and set up Azure Monitor alerts for high CPU/Memory.
- [ ] **SSL/TLS**: Implement `cert-manager` for automatic SSL termination on the Ingress.
- [ ] **Autoscaling**: Configure Horizontal Pod Autoscaler (HPA) for the API Gateway and Auth Service.

## 🛡️ Phase 2: Security Hardening (1 - 3 Months)
- [ ] **Azure Key Vault**: Move all Kubernetes Secrets to Azure Key Vault with CSI driver integration.
- [ ] **Network Policies**: Implement K8s NetworkPolicies to restrict pod-to-pod communication to only necessary routes.
- [ ] **WAF Integration**: Front the Ingress with Azure Application Gateway + WAF for DDoS protection.

## 📈 Phase 3: Business Maturity (3+ Months)
- [ ] **Advanced Analytics**: Integrate Azure Synapse/Data Factory to pipe Kafka events into a data warehouse for government reporting.
- [ ] **Mobile App Release**: Finalize and deploy the React Native mobile application to iOS/Android stores.
- [ ] **Disaster Recovery**: Implement Geo-Replication for the PostgreSQL database and Redis cache.
