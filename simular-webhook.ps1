param (
    [Parameter(Mandatory=$true)]
    [string]$transaccionExternaId,

    [string]$url = "http://localhost:8080/api/v1/stereum"
)

$secretKey = "601d71d4-cf46-44ab-a637-56bf5cc63640"

$timestamp = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds()

$body = "{`"notification_type`":`"transaction`",`"id`":`"$(New-Guid)`",`"transaction`":{`"id`":`"$transaccionExternaId`",`"status`":`"COMPLETED`",`"amount`":60,`"currency`":`"USDT`",`"network`":`"POLYGON`",`"country`":`"BO`",`"amount_received`":60,`"fee`":0,`"idempotency_key`":`"$(New-Guid)`",`"on_main_net`":false,`"created_date`":$($timestamp * 1000)},`"timestamp`":$($timestamp * 1000)}"

$hmac = New-Object System.Security.Cryptography.HMACSHA256
$hmac.Key = [System.Text.Encoding]::UTF8.GetBytes($secretKey)
$hashBytes = $hmac.ComputeHash([System.Text.Encoding]::UTF8.GetBytes($body))
$signature = [BitConverter]::ToString($hashBytes).Replace("-","").ToLower()

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Simulando Webhook de Stereum..." -ForegroundColor Yellow
Write-Host "Transaccion ID : $transaccionExternaId"
Write-Host "URL destino    : $url"
Write-Host "========================================" -ForegroundColor Cyan

$headers = @{
    "Content-Type" = "application/json"
    "X-Signature"  = $signature
    "X-Timestamp"  = $timestamp.ToString()
}

try {
    $response = Invoke-RestMethod -Uri $url -Method Post -Headers $headers -Body $body
    Write-Host "Exito! El webhook fue procesado correctamente." -ForegroundColor Green
} catch {
    Write-Host "Error al enviar el Webhook:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
}
