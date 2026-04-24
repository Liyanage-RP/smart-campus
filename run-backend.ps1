# Smart Campus - Backend Run Script
Write-Host "Starting Smart Campus Backend..."

$BackendDir = "backend"

if (Test-Path "$BackendDir\pom.xml") {
    cd $BackendDir
    Write-Host "Building and Running with Maven..."
    if (Test-Path "mvnw.cmd") {
        .\mvnw.cmd spring-boot:run
    } else {
        mvn spring-boot:run
    }
} else {
    Write-Host "Error: backend folder or pom.xml not found!"
}
