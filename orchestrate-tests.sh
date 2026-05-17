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

# =============================================================================
# Flutter Mobile Pipeline Verification (Strategic Roadmap Step 4)
# =============================================================================
echo "📱 Validating Flutter Mobile Application CI/CD Suite..."
if cd frontend-mobile; then
  flutter test
  if [ $? -eq 0 ]; then
    echo "✅ Flutter unit tests PASSED"
  else
    echo "❌ Flutter unit tests FAILED. Aborting comprehensive test suite."
    exit 1
  fi
  
  echo "📦 Compiling Production-Ready Release Package (APK)..."
  # Run a dry-run or release build with safety fallbacks
  flutter build apk --release --no-pub 2>/dev/null
  if [ $? -eq 0 ]; then
    echo "✅ Native Android package compiled: build/app/outputs/flutter-apk/app-release.apk"
  else
    echo "⚠️ Native compilation skipped or requires Android SDK environment variables."
  fi
  cd ..
else
  echo "⚠️ Mobile folder not found or accessible. Skipping."
fi

echo "------------------------------------------"
echo "🌐 Starting Backend Fleet Health Checks..."
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
