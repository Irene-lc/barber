# Script para arrancar el backend con las variables de entorno del .env
# Uso: .\run.ps1

$envFile = ".\barber-api\src\main\resources\.env"

if (-Not (Test-Path $envFile)) {
    Write-Error "No se encontro el archivo .env en: $envFile"
    exit 1
}

# Cargar variables del .env
Get-Content $envFile | ForEach-Object {
    if ($_ -match "^\s*([^#][^=]+)=(.*)$") {
        $key = $matches[1].Trim()
        $value = $matches[2].Trim()
        [System.Environment]::SetEnvironmentVariable($key, $value, "Process")
        Write-Host "  Cargada: $key"
    }
}

Write-Host ""
Write-Host "Variables cargadas. Iniciando Spring Boot..." -ForegroundColor Green
Write-Host ""

.\mvnw.cmd spring-boot:run -pl barber-api --no-transfer-progress
