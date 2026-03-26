#!/usr/bin/env pwsh
# Simple compile test script
Set-Location "C:\Users\user\IdeaProjects\2ndcafe"

Write-Host "Building the project with Maven..." -ForegroundColor Cyan
mvn clean compile -DskipTests

if ($LASTEXITCODE -eq 0) {
    Write-Host "Build successful!" -ForegroundColor Green
    Write-Host "Now packaging the application..." -ForegroundColor Cyan
    mvn package -DskipTests
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Package successful!" -ForegroundColor Green
        Write-Host "You can now run: mvn javafx:run" -ForegroundColor Yellow
    } else {
        Write-Host "Package failed!" -ForegroundColor Red
    }
} else {
    Write-Host "Build failed!" -ForegroundColor Red
    Write-Host "Please check the errors above." -ForegroundColor Red
}

