# Guía de Integración y Pruebas - Stereum Pay Webhook

Esta guía explica paso a paso cómo arrancar el proyecto, configurar las variables de entorno y probar el flujo de pagos con Stereum (tanto simulado como real).

---

## 1. Configuración Inicial (¡Muy Importante!)

El proyecto ahora requiere variables de entorno obligatorias para conectarse a la Base de Datos y a Stereum. Sin ellas, la aplicación **no arrancará** (dará error de Hibernate/Dialecto).

### Opción A: Usar IntelliJ IDEA (Recomendado)
1. Ve a la configuración de arranque de Spring Boot (`Run` -> `Edit Configurations`).
2. En la sección **Environment variables**, pega lo siguiente:
   ```text
   USER_DB=postgres;PASSWORD_DB=tu_password_aqui;BARBER_SECRET_KEY=pQKOkAZ8j59Kb6QJC+LHL7viCOUvfhBZ7PjGeIznKbY=;STEREUM_API_KEY=6ea740c3-6db9-44ce-beda-c8e9a3038ed0;STEREUM_SECRET_KEY=33397e9e999d450693fb2b32e9bb5dbe75e804da685d42b0bbf3a5248e8435b0d720506cea6a4594890b424d27a420726e23548f158b48109fb7feb14afcea77
   ```
   *(Asegúrate de cambiar `tu_password_aqui` por la contraseña de tu PostgreSQL local).*
3. Dale a **Apply** y arranca el proyecto normalmente con el botón de Play ▶️.

### Opción B: Usar el script de PowerShell
Si prefieres arrancar por consola, asegúrate de tener el archivo `.env` creado en `barber-api/src/main/resources/.env`. Luego simplemente ejecuta desde la raíz del proyecto:
```powershell
.\run.ps1
```

---

## 2. Cómo Probar un Pago (Simulación Local SIN dinero real)

Para desarrollar el frontend o probar la lógica interna sin tener que usar criptomonedas reales, tenemos un script simulador.

### Paso 2.1 - Generar el QR
En Postman, con tu token de login (Bearer), crea un pago:
* **Método:** `POST http://localhost:8080/api/v1/pagos/generar-qr`
* **Body:** `{"ventaId": "ID_DE_VENTA_VALIDA"}`

Te devolverá algo así:
```json
{
  "pago_id": "7425738e-...",
  "payment_link": "https://...",
  "qr_base64": "..."
}
```

### Paso 2.2 - Obtener el ID de Stereum
Copia el `pago_id` del paso anterior y haz una consulta GET:
* **Método:** `GET http://localhost:8080/api/v1/pagos/7425738e-...`

En la respuesta, busca el campo **`transaccion_externa_id`** y cópialo. (Ej: `077b8ce7-9461...`). ¡No confundir con el `pago_id`!

### Paso 2.3 - Ejecutar el Simulador de Webhook
Abre la terminal de PowerShell (puedes usar la pestaña "Terminal" de IntelliJ) y ejecuta el script de simulación pasando el ID que acabas de copiar:
```powershell
.\simular-webhook.ps1 -transaccionExternaId "077b8ce7-9461-..."
```
Verás un mensaje en verde de `¡Exito!`. El backend ha recibido una notificación firmada criptográficamente simulando que Stereum procesó el pago.

### Paso 2.4 - Verificar el cambio
Vuelve a hacer el **GET** del Paso 2.2 en Postman. El campo `"estado_pago"` ahora debería decir `"PAGADO"`.

---

## 3. Cómo Probar Pagos Reales (Con Criptomonedas y ngrok)

Cuando quieras probar el flujo real donde el usuario escanea el QR y paga de verdad, necesitas exponer tu `localhost` a internet para que Stereum pueda notificar a tu PC.

1. Descarga **ngrok** (ngrok.com) y pon tu token de cuenta.
2. Abre una terminal y expón el puerto de Spring Boot:
   ```powershell
   ngrok http 8080
   ```
3. Ngrok te dará una URL (ej: `https://a1b2c3d4.ngrok-free.dev`). Copia esa URL.
4. Ve al **Dashboard de Stereum** -> Webhooks. Configura la URL agregando la ruta de nuestra API:
   ```text
   https://a1b2c3d4.ngrok-free.dev/api/v1/stereum
   ```
5. Genera un pago desde tu aplicación/Postman, escanea el QR con tu wallet y págalo.
6. A los pocos segundos, verás en tu consola de IntelliJ cómo llega la petición automáticamente y el pago cambia a `PAGADO`.
