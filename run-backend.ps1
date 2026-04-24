# PowerShell script to run the Smart Campus Spring Boot backend
Write-Host "Starting Smart Campus Backend..." -ForegroundColor Cyan

$backendDir = Join-Path $PSScriptRoot "backend"

if (-not (Test-Path $backendDir)) {
    Write-Host "ERROR: backend directory not found at $backendDir" -ForegroundColor Red
    exit 1
}

Set-Location $backendDir

# Check for mvnw
if (Test-Path ".\mvnw.cmd") {
    Write-Host "Using Maven Wrapper..." -ForegroundColor Green
    .\mvnw.cmd spring-boot:run
} elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
    Write-Host "Using system Maven..." -ForegroundColor Green
    mvn spring-boot:run
} else {
    Write-Host "Maven not found! Trying to run JAR directly..." -ForegroundColor Yellow
    $jar = Get-ChildItem -Path "target" -Filter "*.jar" -Exclude "*sources*" | Select-Object -First 1
    if ($jar) {
        Write-Host "Running JAR: $($jar.FullName)" -ForegroundColor Green
        java -jar $jar.FullName
    } else {
        Write-Host "ERROR: No JAR found. Please install Maven and run 'mvn package' first." -ForegroundColor Red
        exit 1
    }
}
