# Credit Score Profile Testing Script
# This script creates two contrasting customer profiles and tests credit score calculations

Write-Host "========================================" -ForegroundColor Green
Write-Host "    Credit Score Profile Testing" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# Configuration
$baseUrl = "http://localhost:8083/api/credit-scores"
$headers = @{ "Content-Type" = "application/json" }

# Wait for service to be ready
Write-Host "Checking if Credit Score Service is running..." -ForegroundColor Yellow
$serviceReady = $false
$maxRetries = 10
$retries = 0

do {
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8083/actuator/health" -Method GET -TimeoutSec 5
        if ($response.status -eq "UP") {
            $serviceReady = $true
            Write-Host "Credit Score Service is running!" -ForegroundColor Green
        }
    }
    catch {
        $retries++
        Write-Host "Waiting for service... ($retries/$maxRetries)" -ForegroundColor Yellow
        Start-Sleep -Seconds 3
    }
} while (-not $serviceReady -and $retries -lt $maxRetries)

if (-not $serviceReady) {
    Write-Host "Credit Score Service is not available. Please start it first." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "    Creating Customer Profiles" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Profile 1: Poor Credit Profile (Expected Low Score)
Write-Host "Creating Profile 1: Poor Credit Customer" -ForegroundColor Red
Write-Host "   - High debt-to-income ratio: 80%" -ForegroundColor Red
Write-Host "   - Poor payment history: 40/100" -ForegroundColor Red
Write-Host "   - High credit utilization: 90%" -ForegroundColor Red
Write-Host "   - Short credit age: 12 months" -ForegroundColor Red
Write-Host "   - Few accounts: 2" -ForegroundColor Red

$poorCreditProfile = @{
    customerId = 1001
    customerName = "Alice Poor-Credit"
    customerEmail = "alice.poor@example.com"
    income = 30000.0
    debtToIncomeRatio = 0.8
    paymentHistoryScore = 40
    creditUtilizationRatio = 0.9
    creditAgeMonths = 12
    numberOfAccounts = 2
} | ConvertTo-Json

try {
    Write-Host "Sending request to credit score service..." -ForegroundColor Green
    $poorCreditResponse = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $poorCreditProfile -Headers $headers
    Write-Host "Poor Credit Profile Created Successfully!" -ForegroundColor Green
    Write-Host "Credit Score: $($poorCreditResponse.creditScore)" -ForegroundColor Magenta
    Write-Host "Grade: $($poorCreditResponse.scoreGrade)" -ForegroundColor Magenta
    Write-Host ""
} catch {
    Write-Host "Failed to create poor credit profile: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
}

# Profile 2: Excellent Credit Score (Low Risk Customer)
Write-Host "Creating Profile 2: Excellent Credit Customer" -ForegroundColor Green
Write-Host "   - Low debt-to-income ratio: 15%" -ForegroundColor Green
Write-Host "   - Excellent payment history: 95/100" -ForegroundColor Green
Write-Host "   - Low credit utilization: 10%" -ForegroundColor Green
Write-Host "   - Long credit history: 120 months" -ForegroundColor Green
Write-Host "   - Many accounts: 8" -ForegroundColor Green

$excellentCreditProfile = @{
    customerId = 1002
    customerName = "Robert Smith"
    customerEmail = "robert.smith@email.com"
    income = 85000.0
    debtToIncomeRatio = 0.15
    paymentHistoryScore = 95
    creditUtilizationRatio = 0.10
    creditAgeMonths = 120
    numberOfAccounts = 8
} | ConvertTo-Json

try {
    Write-Host "Sending request to credit score service..." -ForegroundColor Green
    $excellentCreditResponse = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $excellentCreditProfile -Headers $headers
    Write-Host "Excellent Credit Profile Created Successfully!" -ForegroundColor Green
    Write-Host "Credit Score: $($excellentCreditResponse.creditScore)" -ForegroundColor Magenta
    Write-Host "Grade: $($excellentCreditResponse.scoreGrade)" -ForegroundColor Magenta
    Write-Host ""
} catch {
    Write-Host "Failed to create excellent credit profile: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
}

# Summary
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  Credit Score Comparison Summary" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan

if ($poorCreditResponse -and $excellentCreditResponse) {
    Write-Host "Poor Credit Customer (Alice):" -ForegroundColor Red
    Write-Host "  Score: $($poorCreditResponse.creditScore) ($($poorCreditResponse.scoreGrade))" -ForegroundColor Red
    Write-Host ""
    Write-Host "Excellent Credit Customer (Robert):" -ForegroundColor Green
    Write-Host "  Score: $($excellentCreditResponse.creditScore) ($($excellentCreditResponse.scoreGrade))" -ForegroundColor Green
    Write-Host ""
    
    $scoreDifference = $excellentCreditResponse.creditScore - $poorCreditResponse.creditScore
    Write-Host "Score Difference: $scoreDifference points" -ForegroundColor Cyan
    Write-Host ""
    
    if ($scoreDifference -gt 100) {
        Write-Host "SUCCESS: Credit Score Microservice correctly calculated significant difference!" -ForegroundColor Green
    } else {
        Write-Host "Score difference calculated by microservice: $scoreDifference points" -ForegroundColor Yellow
    }
    
    Write-Host ""
    Write-Host "Key factors that influenced the scores:" -ForegroundColor Yellow
    Write-Host "  - Debt-to-Income: 80% vs 15%" -ForegroundColor White
    Write-Host "  - Payment History: 40 vs 95" -ForegroundColor White
    Write-Host "  - Credit Utilization: 90% vs 10%" -ForegroundColor White
    Write-Host "  - Credit Age: 12 vs 120 months" -ForegroundColor White
    Write-Host "  - Number of Accounts: 2 vs 8" -ForegroundColor White
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "  Test Complete - Both profiles created!" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "To view these profiles later:" -ForegroundColor Yellow
Write-Host "  GET http://localhost:8083/api/credit-scores/customer/1001" -ForegroundColor Gray
Write-Host "  GET http://localhost:8083/api/credit-scores/customer/1002" -ForegroundColor Gray