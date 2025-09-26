@echo off
echo ============================================================
echo Starting Banking System with Credit Score Fluctuation
echo ============================================================

echo.
echo Starting Credit Score Service on port 8083...
echo.
start "Credit Score Service" cmd /c "cd /d "D:\MyProjects\BankingSystem\credit-score-service" && mvn spring-boot:run"

echo Waiting for Credit Score Service to start...
timeout /t 30 /nobreak

echo.
echo Starting Main Banking System on port 8082...
echo.
start "Banking System" cmd /c "cd /d "D:\MyProjects\BankingSystem" && mvn spring-boot:run"

echo.
echo ============================================================
echo Both services are starting...
echo - Credit Score Service: http://localhost:8083
echo - Banking System: http://localhost:8082
echo ============================================================
echo.
echo Press any key to close this window...
pause