#!/usr/bin/env pwsh
# Simple One-Click Run Script for 2ndCafe

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "2ndCafe - One-Click Run" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if Maven is installed
$mvnTest = mvn --version 2>&1 | Select-String "Apache Maven"
if (-not $mvnTest) {
    Write-Host "ERROR: Maven is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install Maven and try again." -ForegroundColor Yellow
    exit 1
}

Write-Host "✓ Maven found" -ForegroundColor Green
Write-Host ""

# Navigate to project directory
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

Write-Host "Building and running application..." -ForegroundColor Yellow
Write-Host ""

# Clean and compile
Write-Host "Step 1: Building project..." -ForegroundColor Gray
mvn clean compile -q

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed!" -ForegroundColor Red
    exit 1
}

Write-Host "✓ Build successful" -ForegroundColor Green
Write-Host ""

# Run the application
Write-Host "Step 2: Starting application..." -ForegroundColor Gray
Write-Host ""
Write-Host "LOGIN CREDENTIALS:" -ForegroundColor Yellow
Write-Host "  Admin:    admin / Admin123" -ForegroundColor Green
Write-Host "  Cashier:  cashier / Cash123" -ForegroundColor Green
Write-Host "  Customer: customer / Cust123" -ForegroundColor Green
Write-Host ""

mvn javafx:run

Write-Host ""
Write-Host "Application closed." -ForegroundColor Gray

