# Credit Score Microservice Test Script (PowerShell)
# This script tests the two contrasting customer profiles using the credit score microservice API

Write-Host "==============================================================" -ForegroundColor Blue
Write-Host "🎯 CREDIT SCORE MICROSERVICE TEST" -ForegroundColor Yellow
Write-Host "==============================================================" -ForegroundColor Blue

# Check if the credit score service is running
Write-Host "📡 Checking if Credit Score Service is running on port 8083..." -ForegroundColor Cyan

try {
    $healthCheck = Invoke-RestMethod -Uri "http://localhost:8083/actuator/health" -Method Get -TimeoutSec 5
    Write-Host "✅ Credit Score Service is running!" -ForegroundColor Green
} catch {
    Write-Host "❌ Credit Score Service is not running on port 8083" -ForegroundColor Red
    Write-Host "Please start the service first: cd credit-score-service && mvn spring-boot:run" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "🧪 Testing Credit Score Calculations..." -ForegroundColor Yellow
Write-Host "==============================================================" -ForegroundColor Blue

# Test Profile 1: High Debt Customer (Expected Low Score)
Write-Host ""
Write-Host "📉 PROFILE 1: HIGH DEBT CUSTOMER" -ForegroundColor Red
Write-Host "Expected: Low Credit Score (Poor Grade)" -ForegroundColor Gray
Write-Host "--------------------------------------------------------------" -ForegroundColor Gray

$highDebtProfile = @{
    customerId = 1001
    customerName = "Sarah Wilson"
    customerEmail = "sarah.wilson@example.com"
    income = 35000.0
    debtToIncomeRatio = 0.85
    paymentHistoryScore = 45
    creditUtilizationRatio = 0.95
    creditAgeMonths = 18
    numberOfAccounts = 8
} | ConvertTo-Json

try {
    $result1 = Invoke-RestMethod -Uri "http://localhost:8083/api/credit-scores/calculate" -Method Post -Body $highDebtProfile -ContentType "application/json"
    Write-Host "Customer: $($result1.customerName)" -ForegroundColor White
    Write-Host "Income: ₹$($result1.income)" -ForegroundColor White
    Write-Host "Debt-to-Income Ratio: $([math]::Round($result1.debtToIncomeRatio * 100, 1))%" -ForegroundColor White
    Write-Host "Payment History Score: $($result1.paymentHistoryScore)/100" -ForegroundColor White
    Write-Host "Credit Utilization: $([math]::Round($result1.creditUtilizationRatio * 100, 1))%" -ForegroundColor White
    Write-Host "Credit Age: $($result1.creditAgeMonths) months" -ForegroundColor White
    Write-Host "Number of Accounts: $($result1.numberOfAccounts)" -ForegroundColor White
    Write-Host "🎯 CALCULATED CREDIT SCORE: $($result1.creditScore) ($($result1.scoreGrade))" -ForegroundColor Yellow
} catch {
    Write-Host "❌ Error calculating credit score for Sarah Wilson: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test Profile 2: Low Debt Customer (Expected High Score)
Write-Host "📈 PROFILE 2: LOW DEBT CUSTOMER" -ForegroundColor Green
Write-Host "Expected: High Credit Score (Excellent Grade)" -ForegroundColor Gray
Write-Host "--------------------------------------------------------------" -ForegroundColor Gray

$lowDebtProfile = @{
    customerId = 1002
    customerName = "Michael Johnson"
    customerEmail = "michael.johnson@example.com"
    income = 95000.0
    debtToIncomeRatio = 0.15
    paymentHistoryScore = 98
    creditUtilizationRatio = 0.05
    creditAgeMonths = 84
    numberOfAccounts = 4
} | ConvertTo-Json

try {
    $result2 = Invoke-RestMethod -Uri "http://localhost:8083/api/credit-scores/calculate" -Method Post -Body $lowDebtProfile -ContentType "application/json"
    Write-Host "Customer: $($result2.customerName)" -ForegroundColor White
    Write-Host "Income: ₹$($result2.income)" -ForegroundColor White
    Write-Host "Debt-to-Income Ratio: $([math]::Round($result2.debtToIncomeRatio * 100, 1))%" -ForegroundColor White
    Write-Host "Payment History Score: $($result2.paymentHistoryScore)/100" -ForegroundColor White
    Write-Host "Credit Utilization: $([math]::Round($result2.creditUtilizationRatio * 100, 1))%" -ForegroundColor White
    Write-Host "Credit Age: $($result2.creditAgeMonths) months" -ForegroundColor White
    Write-Host "Number of Accounts: $($result2.numberOfAccounts)" -ForegroundColor White
    Write-Host "🎯 CALCULATED CREDIT SCORE: $($result2.creditScore) ($($result2.scoreGrade))" -ForegroundColor Yellow
} catch {
    Write-Host "❌ Error calculating credit score for Michael Johnson: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host ""
Write-Host "==============================================================" -ForegroundColor Blue
Write-Host "📊 RETRIEVING CALCULATED SCORES" -ForegroundColor Yellow
Write-Host "==============================================================" -ForegroundColor Blue

# Get calculated scores
Write-Host ""
Write-Host "🔍 Sarah Wilson's Credit Score:" -ForegroundColor Cyan
try {
    $sarah = Invoke-RestMethod -Uri "http://localhost:8083/api/credit-scores/customer/1001" -Method Get
    $sarah | ConvertTo-Json -Depth 3 | Write-Host -ForegroundColor White
} catch {
    Write-Host "❌ Could not retrieve score for Sarah Wilson" -ForegroundColor Red
}

Write-Host ""
Write-Host "🔍 Michael Johnson's Credit Score:" -ForegroundColor Cyan
try {
    $michael = Invoke-RestMethod -Uri "http://localhost:8083/api/credit-scores/customer/1002" -Method Get
    $michael | ConvertTo-Json -Depth 3 | Write-Host -ForegroundColor White
} catch {
    Write-Host "❌ Could not retrieve score for Michael Johnson" -ForegroundColor Red
}

Write-Host ""
Write-Host ""
Write-Host "==============================================================" -ForegroundColor Blue
Write-Host "📈 COMPARISON SUMMARY" -ForegroundColor Yellow
Write-Host "==============================================================" -ForegroundColor Blue

Write-Host ""
Write-Host "📊 Side-by-side Comparison:" -ForegroundColor Cyan
Write-Host ""

if ($result1) {
    Write-Host "Sarah Wilson (High Debt Profile):" -ForegroundColor Red
    Write-Host "  Score: $($result1.creditScore) | Grade: $($result1.scoreGrade) | Income: ₹$($result1.income) | Debt Ratio: $([math]::Round($result1.debtToIncomeRatio * 100, 1))%" -ForegroundColor White
}

if ($result2) {
    Write-Host ""
    Write-Host "Michael Johnson (Low Debt Profile):" -ForegroundColor Green
    Write-Host "  Score: $($result2.creditScore) | Grade: $($result2.scoreGrade) | Income: ₹$($result2.income) | Debt Ratio: $([math]::Round($result2.debtToIncomeRatio * 100, 1))%" -ForegroundColor White
}

Write-Host ""
Write-Host "==============================================================" -ForegroundColor Blue
Write-Host "✅ TESTING COMPLETE" -ForegroundColor Green
Write-Host "==============================================================" -ForegroundColor Blue
Write-Host ""
Write-Host "🎯 The credit scores should demonstrate:" -ForegroundColor Yellow
Write-Host "   • Sarah Wilson: Low score due to high debt burden" -ForegroundColor Red
Write-Host "   • Michael Johnson: High score due to excellent financial profile" -ForegroundColor Green
Write-Host ""
Write-Host "📝 All calculations are performed by the Credit Score Microservice API" -ForegroundColor Cyan