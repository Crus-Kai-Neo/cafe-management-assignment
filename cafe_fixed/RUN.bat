@echo off
REM Simple One-Click Run Script for 2ndCafe (Windows Batch)
REM This script will build and run the application

echo ========================================
echo 2ndCafe - One-Click Run
echo ========================================
echo.

REM Check if Maven is installed
mvn --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven and try again.
    echo Visit: https://maven.apache.org/
    pause
    exit /b 1
)

echo Maven found
echo.

REM Get the directory where this script is located
cd /d "%~dp0"

echo Building and running application...
echo.

REM Clean and compile
echo Step 1: Building project...
call mvn clean compile -q

if errorlevel 1 (
    echo Build failed!
    pause
    exit /b 1
)

echo Build successful
echo.

REM Run the application
echo Step 2: Starting application...
echo.
echo LOGIN CREDENTIALS:
echo   Admin:    admin / Admin123
echo   Cashier:  cashier / Cash123
echo   Customer: customer / Cust123
echo.

call mvn javafx:run

echo.
echo Application closed.
pause

