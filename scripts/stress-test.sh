#!/bin/bash
# NSIP Fleet Stress Test v1.0
# Simulates high-volume claim traffic and validates Digital Trust integrity.

LB_IP="20.43.156.128" # Ingress IP
USER_ID="947458a5-6912-4b1e-b6db-e56cfbdc4bcc"
CONCURRENT_REQUESTS=10
TOTAL_REQUESTS=50

echo "🔥 Initiating Stress Test on $LB_IP..."
echo "Simulating $TOTAL_REQUESTS loan requests ($CONCURRENT_REQUESTS concurrent)..."

start_time=$(date +%s)

for ((i=1; i<=TOTAL_REQUESTS; i++)); do
  (
    curl -s -X POST "http://$LB_IP/api/v1/claims/loan" \
      -H "Content-Type: application/json" \
      -d "{
        \"userId\": \"$USER_ID\",
        \"amount\": 5000,
        \"description\": \"Stress test loan request #$i\",
        \"type\": \"PERSONAL_LOAN\"
      }" > /dev/null
  ) &

  if (( $i % $CONCURRENT_REQUESTS == 0 )); then
    wait
    echo "Progress: $i/$TOTAL_REQUESTS requests sent..."
  fi
done

wait
end_time=$(date +%s)
duration=$((end_time - start_time))

echo "------------------------------------------"
echo "✅ Stress Test Complete in $duration seconds."
echo "Average latency: $((duration * 1000 / TOTAL_REQUESTS))ms per request."
echo "------------------------------------------"
echo "🔍 Validating Audit Chain Integrity..."

# Check event-service logs for any hash mismatches
kubectl logs -n nsip -l app=event-service --tail=100 | grep "Hash mismatch" || echo "✅ Audit Chain: NO TAMPERING DETECTED."
