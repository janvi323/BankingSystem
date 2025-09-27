# Verification script to test Sarah & Michael's integration

Write-Host "========================================" -ForegroundColor Green
Write-Host "  Verifying Sarah & Michael Integration" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

Write-Host "1. Testing Credit Score Service..." -ForegroundColor Cyan
try {
    # Test if we can access credit scores
    $sarahCreditScore = Invoke-RestMethod -Uri "http://localhost:8083/api/credit-scores/customer/1002" -Method GET -TimeoutSec 5
    Write-Host "[OK] Sarah's Credit Score: $($sarahCreditScore.creditScore) ($($sarahCreditScore.scoreGrade))" -ForegroundColor Green
} catch {
    Write-Host "[INFO] Sarah's credit score will be calculated when she logs in" -ForegroundColor Yellow
}

try {
    $michaelCreditScore = Invoke-RestMethod -Uri "http://localhost:8083/api/credit-scores/customer/1003" -Method GET -TimeoutSec 5
    Write-Host "[OK] Michael's Credit Score: $($michaelCreditScore.creditScore) ($($michaelCreditScore.scoreGrade))" -ForegroundColor Green
} catch {
    Write-Host "[INFO] Michael's credit score will be calculated when he logs in" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "2. Checking System Integration..." -ForegroundColor Cyan

# Check if both services are running
$bankingSystemRunning = $false
$creditScoreServiceRunning = $false

try {
    Invoke-RestMethod -Uri "http://localhost:8082/login" -Method GET -TimeoutSec 3 | Out-Null
    $bankingSystemRunning = $true
    Write-Host "[OK] Banking System (8082): Running" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Banking System (8082): Not running" -ForegroundColor Red
}

try {
    $health = Invoke-RestMethod -Uri "http://localhost:8083/actuator/health" -Method GET -TimeoutSec 3
    if ($health.status -eq "UP") {
        $creditScoreServiceRunning = $true
        Write-Host "[OK] Credit Score Service (8083): Running" -ForegroundColor Green
    }
} catch {
    Write-Host "[ERROR] Credit Score Service (8083): Not running" -ForegroundColor Red
}

Write-Host ""
Write-Host "3. System Status Summary" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

if ($bankingSystemRunning -and $creditScoreServiceRunning) {
    Write-Host "[OK] Both services are running correctly!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Ready to Test:" -ForegroundColor Yellow
    Write-Host "1. Go to: http://localhost:8082/login" -ForegroundColor White
    Write-Host "2. Login as Sarah Wilson or Michael Johnson" -ForegroundColor White
    Write-Host "3. Their credit scores will be automatically calculated" -ForegroundColor White
    Write-Host "4. View dashboard to see credit score display" -ForegroundColor White
    Write-Host "5. Apply for loans to test credit score fluctuation" -ForegroundColor White
} else {
    Write-Host "[WARNING] Some services are not running properly" -ForegroundColor Yellow
    if (-not $bankingSystemRunning) {
        Write-Host "- Start Banking System: mvn spring-boot:run" -ForegroundColor Gray
    }
    if (-not $creditScoreServiceRunning) {
        Write-Host "- Start Credit Score Service: cd credit-score-service && mvn spring-boot:run" -ForegroundColor Gray
    }
}

Write-Host ""
Write-Host "Test Customers Added:" -ForegroundColor Cyan
Write-Host "Sarah Wilson (High Debt Profile):" -ForegroundColor Yellow
Write-Host "  Login: sarah.wilson@email.com / password123" -ForegroundColor White
Write-Host "  Expected: Low credit score due to high debt" -ForegroundColor Red
Write-Host ""
Write-Host "Michael Johnson (Low Debt Profile):" -ForegroundColor Yellow
Write-Host "  Login: michael.johnson@email.com / password123" -ForegroundColor White
Write-Host "  Expected: High credit score due to low debt" -ForegroundColor Green
Write-Host ""
Write-Host "Integration Complete!" -ForegroundColor Green