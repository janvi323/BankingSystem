# Script to add Sarah Wilson and Michael Johnson using form submission
# This method uses the working form-based registration

Write-Host "========================================" -ForegroundColor Green
Write-Host "  Adding Sarah & Michael via Form Submission" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

Write-Host "Checking if main banking system is running..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8082/login" -Method GET -TimeoutSec 5
    Write-Host "[OK] Main banking system is running" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Main banking system is not available" -ForegroundColor Red
    Write-Host "Please start the main banking system on port 8082" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "1. Adding Sarah Wilson (High Debt Profile)..." -ForegroundColor Cyan

# Sarah Wilson form data
$sarahFormData = @{
    name = "Sarah Wilson"
    email = "sarah.wilson@email.com"
    password = "password123"
    phone = "555-0101"
    address = "123 High Street, City"
    role = "CUSTOMER"
    income = 35000
    debtToIncomeRatio = 85
    paymentHistoryScore = 45
    creditUtilizationRatio = 95
    creditAgeMonths = 18
    numberOfAccounts = 8
}

try {
    $sarahResult = Invoke-RestMethod -Uri "http://localhost:8082/perform_register" -Method POST -Body $sarahFormData
    Write-Host "[OK] Sarah Wilson registered successfully!" -ForegroundColor Green
} catch {
    $errorMessage = $_.Exception.Message
    if ($errorMessage -like "*302*" -or $errorMessage -like "*redirect*") {
        Write-Host "[OK] Sarah Wilson registered (redirected to login)" -ForegroundColor Green
    } elseif ($errorMessage -like "*already exists*" -or $errorMessage -like "*duplicate*") {
        Write-Host "[WARNING] Sarah Wilson already exists in the system" -ForegroundColor Yellow
    } else {
        Write-Host "[ERROR] Failed to register Sarah Wilson: $errorMessage" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "2. Adding Michael Johnson (Low Debt Profile)..." -ForegroundColor Cyan

# Michael Johnson form data
$michaelFormData = @{
    name = "Michael Johnson"
    email = "michael.johnson@email.com"
    password = "password123"
    phone = "555-0102"
    address = "456 Low Street, City"
    role = "CUSTOMER"
    income = 95000
    debtToIncomeRatio = 15
    paymentHistoryScore = 98
    creditUtilizationRatio = 5
    creditAgeMonths = 84
    numberOfAccounts = 4
}

try {
    $michaelResult = Invoke-RestMethod -Uri "http://localhost:8082/perform_register" -Method POST -Body $michaelFormData
    Write-Host "[OK] Michael Johnson registered successfully!" -ForegroundColor Green
} catch {
    $errorMessage = $_.Exception.Message
    if ($errorMessage -like "*302*" -or $errorMessage -like "*redirect*") {
        Write-Host "[OK] Michael Johnson registered (redirected to login)" -ForegroundColor Green
    } elseif ($errorMessage -like "*already exists*" -or $errorMessage -like "*duplicate*") {
        Write-Host "[WARNING] Michael Johnson already exists in the system" -ForegroundColor Yellow
    } else {
        Write-Host "[ERROR] Failed to register Michael Johnson: $errorMessage" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "3. Starting Credit Score Service..." -ForegroundColor Cyan
Write-Host "Starting credit score service to calculate their scores..." -ForegroundColor Yellow

# Start credit score service
try {
    Start-Process cmd -ArgumentList "/c", "cd /d D:\MyProjects\BankingSystem\credit-score-service && mvn spring-boot:run" -WindowStyle Minimized
    Write-Host "[OK] Credit score service is starting..." -ForegroundColor Green
    Write-Host "Waiting for service to be ready..." -ForegroundColor Yellow
    
    # Wait for the service to start
    $serviceReady = $false
    $maxRetries = 30
    $retries = 0
    
    do {
        Start-Sleep -Seconds 2
        try {
            $healthCheck = Invoke-RestMethod -Uri "http://localhost:8083/actuator/health" -Method GET -TimeoutSec 3
            if ($healthCheck.status -eq "UP") {
                $serviceReady = $true
                Write-Host "[OK] Credit score service is ready!" -ForegroundColor Green
            }
        } catch {
            $retries++
            Write-Host "Waiting... ($retries/$maxRetries)" -ForegroundColor Gray
        }
    } while (-not $serviceReady -and $retries -lt $maxRetries)
    
    if (-not $serviceReady) {
        Write-Host "[WARNING] Credit score service taking longer than expected" -ForegroundColor Yellow
        Write-Host "Please wait a moment and check manually" -ForegroundColor Yellow
    }
} catch {
    Write-Host "[WARNING] Could not start credit score service automatically" -ForegroundColor Yellow
    Write-Host "Please start it manually: cd credit-score-service && mvn spring-boot:run" -ForegroundColor Gray
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Registration Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Login Details:" -ForegroundColor Cyan
Write-Host "Sarah Wilson:" -ForegroundColor Yellow
Write-Host "  Email: sarah.wilson@email.com" -ForegroundColor White
Write-Host "  Password: password123" -ForegroundColor White
Write-Host "  Profile: High debt (85% DTI, 95% utilization)" -ForegroundColor Red
Write-Host ""
Write-Host "Michael Johnson:" -ForegroundColor Yellow
Write-Host "  Email: michael.johnson@email.com" -ForegroundColor White
Write-Host "  Password: password123" -ForegroundColor White
Write-Host "  Profile: Low debt (15% DTI, 5% utilization)" -ForegroundColor Green
Write-Host ""
Write-Host "Next Steps:" -ForegroundColor Cyan
Write-Host "1. Login at: http://localhost:8082/login" -ForegroundColor White
Write-Host "2. Their credit scores will be calculated automatically" -ForegroundColor White
Write-Host "3. View credit scores in customer dashboard" -ForegroundColor White
Write-Host "4. Apply for loans to test credit score fluctuation" -ForegroundColor White