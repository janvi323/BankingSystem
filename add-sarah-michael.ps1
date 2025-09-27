# Script to add Sarah Wilson and Michael Johnson to the main banking system
# This ensures they can log in and use all banking features

Write-Host "========================================" -ForegroundColor Green
Write-Host "  Adding Sarah & Michael to Main System" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

$baseUrl = "http://localhost:8082/api/auth"
$headers = @{ "Content-Type" = "application/json" }

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

# Sarah Wilson - High debt profile (matches credit score service data)
$sarahProfile = @{
    name = "Sarah Wilson"
    email = "sarah.wilson@email.com"
    password = "password123"
    phone = "555-0101"
    address = "123 High Street, City"
    role = "CUSTOMER"
    # Financial information matching credit score service
    income = 35000.0
    debtToIncomeRatio = 85.0  # Will be converted to 0.85 in controller
    paymentHistoryScore = 45
    creditUtilizationRatio = 95.0  # Will be converted to 0.95 in controller
    creditAgeMonths = 18
    numberOfAccounts = 8
} | ConvertTo-Json

try {
    $sarahResult = Invoke-RestMethod -Uri "$baseUrl/register" -Method POST -Body $sarahProfile -Headers $headers -TimeoutSec 10
    Write-Host "[OK] Sarah Wilson registered successfully!" -ForegroundColor Green
    Write-Host "   Email: sarah.wilson@email.com" -ForegroundColor Gray
    Write-Host "   Password: password123" -ForegroundColor Gray
    Write-Host "   Expected Credit Score: ~451 (Poor)" -ForegroundColor Red
} catch {
    $errorMessage = $_.Exception.Message
    if ($errorMessage -like "*already exists*" -or $errorMessage -like "*duplicate*") {
        Write-Host "[WARNING] Sarah Wilson already exists in the system" -ForegroundColor Yellow
    } else {
        Write-Host "[ERROR] Failed to register Sarah Wilson: $errorMessage" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "2. Adding Michael Johnson (Low Debt Profile)..." -ForegroundColor Cyan

# Michael Johnson - Low debt profile (matches credit score service data)
$michaelProfile = @{
    name = "Michael Johnson"
    email = "michael.johnson@email.com"
    password = "password123"
    phone = "555-0102"
    address = "456 Low Street, City"
    role = "CUSTOMER"
    # Financial information matching credit score service
    income = 95000.0
    debtToIncomeRatio = 15.0  # Will be converted to 0.15 in controller
    paymentHistoryScore = 98
    creditUtilizationRatio = 5.0  # Will be converted to 0.05 in controller
    creditAgeMonths = 84
    numberOfAccounts = 4
} | ConvertTo-Json

try {
    $michaelResult = Invoke-RestMethod -Uri "$baseUrl/register" -Method POST -Body $michaelProfile -Headers $headers -TimeoutSec 10
    Write-Host "[OK] Michael Johnson registered successfully!" -ForegroundColor Green
    Write-Host "   Email: michael.johnson@email.com" -ForegroundColor Gray
    Write-Host "   Password: password123" -ForegroundColor Gray
    Write-Host "   Expected Credit Score: ~764 (Very Good)" -ForegroundColor Green
} catch {
    $errorMessage = $_.Exception.Message
    if ($errorMessage -like "*already exists*" -or $errorMessage -like "*duplicate*") {
        Write-Host "[WARNING] Michael Johnson already exists in the system" -ForegroundColor Yellow
    } else {
        Write-Host "[ERROR] Failed to register Michael Johnson: $errorMessage" -ForegroundColor Red
    }
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
Write-Host "  Profile: High debt, expected low credit score" -ForegroundColor Red
Write-Host ""
Write-Host "Michael Johnson:" -ForegroundColor Yellow
Write-Host "  Email: michael.johnson@email.com" -ForegroundColor White
Write-Host "  Password: password123" -ForegroundColor White
Write-Host "  Profile: Low debt, expected high credit score" -ForegroundColor Green
Write-Host ""
Write-Host "Next Steps:" -ForegroundColor Cyan
Write-Host "1. Start credit score service (port 8083)" -ForegroundColor White
Write-Host "2. Login as Sarah or Michael at: http://localhost:8082/login" -ForegroundColor White
Write-Host "3. Their credit scores will be automatically calculated" -ForegroundColor White
Write-Host "4. Apply for loans to test credit score fluctuation" -ForegroundColor White