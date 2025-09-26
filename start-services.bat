@echo off
echo ============================================
echo    Banking System with Credit Score Service
echo ============================================
echo.

echo Cleaning and building all services...
call mvn clean install -q
if %errorlevel% neq 0 (
    echo ❌ Build failed for main Banking System
    pause
    exit /b 1
)

cd credit-score-service
call mvn clean install -q
if %errorlevel% neq 0 (
    echo ❌ Build failed for Credit Score Service
    pause
    exit /b 1
)
cd ..

echo ✅ Build successful for all services
echo.
echo Choose how to start the services:
echo 1. Start Credit Score Service only (Port 8083)
echo 2. Start Banking System only (Port 8082) 
echo 3. Start both services (Recommended)
echo 4. Exit
echo.
set /p choice="Enter your choice (1-4): "

if "%choice%"=="1" goto credit_only
if "%choice%"=="2" goto banking_only  
if "%choice%"=="3" goto both_services
if "%choice%"=="4" goto exit
goto invalid

:credit_only
echo Starting Credit Score Service on port 8083...
cd credit-score-service
call mvn spring-boot:run
goto end

:banking_only
echo Starting Banking System on port 8082...
call mvn spring-boot:run
goto end

:both_services
echo Starting both services...
echo ⚠️  Note: This will open two terminal windows
echo Press any key to continue...
pause >nul
start "Credit Score Service" cmd /k "cd credit-score-service && mvn spring-boot:run"
timeout /t 3 >nul
echo Starting Banking System in 5 seconds...
timeout /t 5 >nul
call mvn spring-boot:run
goto end

:invalid
echo Invalid choice. Please try again.
pause
goto end

:exit
echo Goodbye!
goto end

:end
echo.
echo Press any key to exit...
pause >nul