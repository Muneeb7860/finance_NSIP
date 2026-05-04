#!/bin/bash

# National Social Insurance Platform (NSIP) - Local Run Script
# ==========================================================

echo "🚀 Starting NSIP Platform Local Setup..."

# 1. Check for Prerequisites
command -v docker >/dev/null 2>&1 || { echo "❌ Docker is not installed. Please install Docker and try again."; exit 1; }
command -v npm >/dev/null 2>&1 || { echo "❌ NPM is not installed. Please install Node.js and try again."; exit 1; }

# 2. Build Frontend (MUI)
echo "📦 Building Frontend Web (Material UI)..."
cd frontend-web
if [ ! -d "node_modules" ]; then
    npm install
fi
npm run build
cd ..

# 3. Build & Start Backend (Maven)
echo "🐘 Building and Starting Backend Microservices (Maven + Docker)..."
cd backend
docker-compose up --build -d

# 4. Wait for Infrastructure
echo "⏳ Waiting for databases and Kafka to initialize..."
sleep 15

# 5. Summary
echo "=========================================================="
echo "✅ NSIP Platform is now running!"
echo ""
echo "🌐 Portals:"
echo "   - Contributor Portal: http://localhost:5173"
echo "   - Back Office API:    http://localhost:8080 (Gateway)"
echo ""
echo "📊 Infrastructure:"
echo "   - Postgres: localhost:5432"
echo "   - Kafka:    localhost:9092"
echo "   - Redis:    localhost:6379"
echo ""
echo "📝 To see logs: cd backend && docker-compose logs -f"
echo "🛑 To stop:    cd backend && docker-compose down"
echo "=========================================================="
