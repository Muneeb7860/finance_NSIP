#!/bin/bash
# NSIP Fleet Comprehensive Health Check
# This script verifies the readiness of all 12 microservices.

NAMESPACE="nsip"
SERVICES=(
  "api-gateway"
  "auth-service"
  "claim-service"
  "contribution-service"
  "education-service"
  "event-service"
  "notification-engine"
  "payment-service"
  "review-service"
  "rewards-service"
  "saga-orchestrator"
  "ai-agent"
)

echo "🚀 Starting NSIP Comprehensive Fleet Test..."
echo "------------------------------------------"

for SVC in "${SERVICES[@]}"; do
  echo -n "Checking $SVC... "
  STATUS=$(kubectl get pods -n $NAMESPACE -l app=$SVC -o jsonpath='{.items[*].status.containerStatuses[*].ready}' | grep -o "true" | wc -l | xargs)
  
  if [ "$STATUS" -ge 1 ]; then
    echo "✅ READY ($STATUS containers)"
  else
    echo "❌ NOT READY"
  fi
done

echo "------------------------------------------"
echo "🌐 Testing Ingress Connectivity..."
LB_IP=$(kubectl get svc api-gateway -n $NAMESPACE -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
if [ -z "$LB_IP" ]; then
  LB_IP="20.43.156.128" # Fallback to known IP
fi

HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://$LB_IP/actuator/health)
if [ "$HTTP_CODE" == "200" ]; then
  echo "✅ Ingress Gateway is UP (HTTP 200)"
else
  echo "❌ Ingress Gateway returned HTTP $HTTP_CODE"
fi

echo "🏁 Test Complete."
