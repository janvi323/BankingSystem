#!/bin/bash

# Credit Score Microservice Test Script
# This script tests the two contrasting customer profiles using the credit score microservice API

echo "=============================================================="
echo "🎯 CREDIT SCORE MICROSERVICE TEST"
echo "=============================================================="

# Check if the credit score service is running
echo "📡 Checking if Credit Score Service is running on port 8083..."
if curl -s http://localhost:8083/actuator/health > /dev/null; then
    echo "✅ Credit Score Service is running!"
else
    echo "❌ Credit Score Service is not running on port 8083"
    echo "Please start the service first: cd credit-score-service && mvn spring-boot:run"
    exit 1
fi

echo ""
echo "🧪 Testing Credit Score Calculations..."
echo "=============================================================="

# Test Profile 1: High Debt Customer (Expected Low Score)
echo ""
echo "📉 PROFILE 1: HIGH DEBT CUSTOMER"
echo "Expected: Low Credit Score (Poor Grade)"
echo "--------------------------------------------------------------"

curl -X POST http://localhost:8083/api/credit-scores/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1001,
    "customerName": "Sarah Wilson",
    "customerEmail": "sarah.wilson@example.com",
    "income": 35000.0,
    "debtToIncomeRatio": 0.85,
    "paymentHistoryScore": 45,
    "creditUtilizationRatio": 0.95,
    "creditAgeMonths": 18,
    "numberOfAccounts": 8
  }' | jq '.'

echo ""
echo ""

# Test Profile 2: Low Debt Customer (Expected High Score)
echo "📈 PROFILE 2: LOW DEBT CUSTOMER"
echo "Expected: High Credit Score (Excellent Grade)"
echo "--------------------------------------------------------------"

curl -X POST http://localhost:8083/api/credit-scores/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1002,
    "customerName": "Michael Johnson",
    "customerEmail": "michael.johnson@example.com",
    "income": 95000.0,
    "debtToIncomeRatio": 0.15,
    "paymentHistoryScore": 98,
    "creditUtilizationRatio": 0.05,
    "creditAgeMonths": 84,
    "numberOfAccounts": 4
  }' | jq '.'

echo ""
echo ""
echo "=============================================================="
echo "📊 RETRIEVING CALCULATED SCORES"
echo "=============================================================="

# Get calculated scores
echo ""
echo "🔍 Sarah Wilson's Credit Score:"
curl -s http://localhost:8083/api/credit-scores/customer/1001 | jq '.'

echo ""
echo "🔍 Michael Johnson's Credit Score:"
curl -s http://localhost:8083/api/credit-scores/customer/1002 | jq '.'

echo ""
echo ""
echo "=============================================================="
echo "📈 COMPARISON SUMMARY"
echo "=============================================================="

# Get both scores for comparison
echo ""
echo "📊 Side-by-side Comparison:"
echo ""
echo "Sarah Wilson (High Debt Profile):"
curl -s http://localhost:8083/api/credit-scores/customer/1001 | jq -r '"  Score: \(.creditScore) | Grade: \(.scoreGrade) | Income: ₹\(.income) | Debt Ratio: \(.debtToIncomeRatio * 100)%"'

echo ""
echo "Michael Johnson (Low Debt Profile):"
curl -s http://localhost:8083/api/credit-scores/customer/1002 | jq -r '"  Score: \(.creditScore) | Grade: \(.scoreGrade) | Income: ₹\(.income) | Debt Ratio: \(.debtToIncomeRatio * 100)%"'

echo ""
echo "=============================================================="
echo "✅ TESTING COMPLETE"
echo "=============================================================="
echo ""
echo "🎯 The credit scores should demonstrate:"
echo "   • Sarah Wilson: Low score due to high debt burden"
echo "   • Michael Johnson: High score due to excellent financial profile"
echo ""
echo "📝 All calculations are performed by the Credit Score Microservice API"