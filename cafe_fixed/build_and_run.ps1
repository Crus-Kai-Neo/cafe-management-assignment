#!/usr/bin/env pwsh
# Comprehensive Build and Debug Script for 2ndCafe Project

$ErrorActionPreference = "Continue"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "2ndCafe - Build & Debug Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check prerequisites
Write-Host "Checking Prerequisites..." -ForegroundColor Yellow
Write-Host ""

# Check Java
Write-Host "Checking Java installation..." -ForegroundColor Gray
$javaVersion = java -version 2>&1 | Select-String "version"
if ($javaVersion) {
    Write-Host "✓ Java found: $javaVersion" -ForegroundColor Green
} else {
    Write-Host "✗ Java not found. Please install Java 21+" -ForegroundColor Red
    exit 1
}

# Check Maven
Write-Host "Checking Maven installation..." -ForegroundColor Gray
$mvnVersion = mvn --version 2>&1 | Select-String "Apache Maven"
if ($mvnVersion) {
    Write-Host "✓ Maven found" -ForegroundColor Green
} else {
    Write-Host "✗ Maven not found. Please install Maven 3.8+" -ForegroundColor Red
    exit 1
}

# Check MySQL
Write-Host "Checking MySQL connection..." -ForegroundColor Gray
$dbHost = if ($env:DB_HOST) { $env:DB_HOST } else { "localhost" }
$dbUser = if ($env:DB_USER) { $env:DB_USER } else { "root" }
$dbPassword = if ($env:DB_PASSWORD) { $env:DB_PASSWORD } else { "" }
$mysqlTest = mysql -h $dbHost -u $dbUser --password=$dbPassword -e "SELECT 1" 2>&1
if ($?) {
    Write-Host "✓ MySQL connection successful" -ForegroundColor Green
} else {
    Write-Host "⚠ MySQL connection might have issues. Check credentials in DatabaseConfig.java" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Building Project" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Set-Location "C:\Users\user\Downloads\2ndcafe_fixed\cafe_fixed"

# Step 1: Clean
Write-Host "Step 1: Cleaning project..." -ForegroundColor Yellow
mvn clean 2>&1 | Out-Null
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Clean successful" -ForegroundColor Green
} else {
    Write-Host "✗ Clean failed" -ForegroundColor Red
}

# Step 2: Compile
Write-Host ""
Write-Host "Step 2: Compiling source code..." -ForegroundColor Yellow
mvn compile -DskipTests
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Compilation successful" -ForegroundColor Green
} else {
    Write-Host "✗ Compilation failed - see errors above" -ForegroundColor Red
    exit 1
}

# Step 3: Package
Write-Host ""
Write-Host "Step 3: Packaging application..." -ForegroundColor Yellow
mvn package -DskipTests
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Packaging successful" -ForegroundColor Green
} else {
    Write-Host "⚠ Packaging had issues but build may still work" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Build Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Next Steps:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Ensure MySQL is running with the correct database setup:"
Write-Host "   mysql -u root -p < src/db/schema.sql"
Write-Host ""
Write-Host "2. Run the application:"
Write-Host "   mvn javafx:run"
Write-Host ""
Write-Host "3. Demo Login Credentials:"
Write-Host "   Admin:    admin / Admin123"
Write-Host "   Cashier:  cashier / Cash123"
Write-Host "   Customer: customer / Cust123"
Write-Host ""

# Optional: Ask to run the application
Write-Host ""
Read-Host "Press Enter to continue or Ctrl+C to exit"
Write-Host ""
Write-Host "Starting application..." -ForegroundColor Cyan
mvn javafx:run

