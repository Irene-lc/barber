# Script para arrancar el backend con las variables de entorno locales
# Uso: .\run.ps1

$envFile = ".\barber-api\src\main\resources\local.env"

if (-Not (Test-Path $envFile)) {
    Write-Error "No se encontro el archivo local.env en: $envFile"
    exit 1
}

# Cargar variables del local.env
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

.\mvnw.cmd install -DskipTests --no-transfer-progress
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

.\mvnw.cmd spring-boot:run -pl barber-api --no-transfer-progress
