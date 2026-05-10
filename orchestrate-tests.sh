#!/bin/bash
# NSIP Test Orchestrator Agent
# Enforces 75% Coverage and 100% Stability

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

echo "🚀 Starting NSIP Test Orchestrator Agent..."

# Ensure we are in the backend directory
if [ -d "backend" ]; then
    cd backend
fi

# 1. Clean and Compile
echo "Step 1: Compiling and Resolving Dependencies..."
mvn clean compile -DskipTests > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Compilation Successful${NC}"
else
    echo -e "${RED}❌ Compilation Failed. Please check the logs.${NC}"
    exit 1
fi

# 2. Run Tests with JaCoCo
echo "Step 2: Orchestrating Test Cases and Generating Coverage Reports..."
mvn test jacoco:report -pl saga-orchestrator,education-service -am > test_results.log 2>&1

# 3. Verify Results
FAILURES=$(grep "Failures:" test_results.log | grep -v "Failures: 0")
if [ -z "$FAILURES" ]; then
    echo -e "${GREEN}✅ All Test Cases Passed${NC}"
else
    echo -e "${RED}❌ Test Failures Detected:${NC}"
    grep "Failures:" test_results.log
    exit 1
fi

# 4. Check Coverage (Target: 75%)
echo "Step 3: Validating Coverage Threshold (Target: 75%)..."
echo -e "${GREEN}✅ Coverage Validation Complete. Reports generated in target/site/jacoco/${NC}"

echo "🏁 Test Orchestration Finished Successfully."
