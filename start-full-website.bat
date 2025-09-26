@echo off
echo Starting Banking System Services...
echo.

echo Step 1: Starting Credit Score Service (Port 8083)...
cd /d "%~dp0credit-score-service"
start "Credit Score Service" cmd /k "mvn spring-boot:run"
echo Credit Score Service starting in background...
echo.

echo Step 2: Waiting 30 seconds for Credit Score Service to initialize...
timeout /t 30 /nobreak >nul
echo.

echo Step 3: Starting Main Banking System (Port 8082)...
cd /d "%~dp0"
start "Banking System" cmd /k "mvn spring-boot:run"
echo Main Banking System starting in background...
echo.

echo Both services are starting up...
echo.
echo Services will be available at:
echo - Banking System: http://localhost:8082/login
echo - Credit Score Service: http://localhost:8083/actuator/health
echo.
echo Wait about 1-2 minutes for both services to fully start.
echo.
echo Press any key to open the banking system in your default browser...
pause >nul

start http://localhost:8082/login

echo.
echo Services are running! 
echo To stop services, close the command windows or press Ctrl+C in each.
echo.
pause