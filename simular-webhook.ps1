param (
    [Parameter(Mandatory=$true)]
    [string]$transaccionExternaId
)

# Llave que Stereum usa para firmar (la corta, que descubrimos antes)
$apiKey = "6ea740c3-6db9-44ce-beda-c8e9a3038ed0"

# Obtener timestamp exacto en UTC (evita bugs de zona horaria de PowerShell)
$timestamp = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds()

# El body simulando un pago exitoso (COMPLETED)
$body = @"
{
  "notification_type": "PAYMENT",
  "id": "$(New-Guid)",
  "transaction": {
    "id": "$transaccionExternaId",
    "status": "COMPLETED",
    "amount": "10.00",
    "currency": "USDT",
    "network": "POLYGON",
    "country": "BO",
    "amount_received": "10.00",
    "on_main_net": false,
    "fee": "0",
    "idempotency_key": "$(New-Guid)",
    "created_date": $timestamp
  },
  "timestamp": $timestamp
}
"@

# Remover saltos de línea extra para la firma (algunos parsers son sensibles a esto)
$bodyCompact = $body -replace '\s+', '' -replace '","', '", "' -replace '":"', '": "'
$bodyCompact = $body

# Generar HMAC SHA256
$hmac = New-Object System.Security.Cryptography.HMACSHA256
$hmac.Key = [System.Text.Encoding]::UTF8.GetBytes($apiKey)
$hashBytes = $hmac.ComputeHash([System.Text.Encoding]::UTF8.GetBytes($body))
$signature = [BitConverter]::ToString($hashBytes).Replace("-","").ToLower()

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Simulando Webhook de Stereum..." -ForegroundColor Yellow
Write-Host "Transaccion ID : $transaccionExternaId"
Write-Host "========================================" -ForegroundColor Cyan

# Enviar la petición simulada al localhost
$headers = @{
    "Content-Type" = "application/json"
    "X-Signature" = $signature
    "X-Timestamp" = $timestamp.ToString()
    "ngrok-skip-browser-warning" = "1"
    "User-Agent" = "PostmanRuntime/7.32.3"
}

try {
    $response = Invoke-RestMethod -Uri "https://salutary-straggler-shortwave.ngrok-free.dev" -Method Post -Headers $headers -Body $body
    Write-Host "¡Exito! El webhook fue enviado y procesado por Spring Boot." -ForegroundColor Green
    Write-Host "Revisa tu base de datos o Postman para ver el estado 'PAGADO'." -ForegroundColor Green
} catch {
    Write-Host "Hubo un error al enviar el Webhook:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
}
