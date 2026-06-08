# Guía Paso a Paso para Probar Todos los CRUDs en Postman

Esta guía contiene los endpoints y payloads (en formato JSON `snake_case`) ordenados secuencialmente para que puedas probar las operaciones **GET (Listar y por ID), POST (Crear), PUT (Actualizar) y DELETE (Eliminar)** de todas las tablas del sistema.

> [!IMPORTANT]
> **Autenticación (JWT Bearer Token):**
> 1. Primero ejecuta la petición de **Login** (Paso 0) para obtener el token.
> 2. Copia el token de la respuesta.
> 3. En Postman, para el resto de peticiones, ve a la pestaña **Authorization**, selecciona **Bearer Token** y pega el token allí.
>
> **IDs dinámicos (UUIDs):**
> Cuando creas un registro (`POST`), el servidor te devolverá su ID (por ejemplo `"id": "3a2b1c..."`). Copia ese ID y úsalo para las peticiones de `GET por ID`, `PUT`, `DELETE` y para asociarlo como clave foránea en otras tablas.

---

## 0. Autenticación (Login)
*Ruta pública para obtener el token de acceso.*
* **POST** `http://localhost:8080/api/v1/auth/login`
* **JSON Body:**
```json
{
  "nombre": "root",
  "password": "Abc123**"
}
```
* **Qué hacer:** Copia el valor del campo `token` de la respuesta y configúralo como Bearer Token en Postman.

---

## 1. CRUD de Empresa (`empresa`)

* **POST (Crear):** `http://localhost:8080/api/v1/empresas`
  ```json
  {
    "nombre": "Estilo y Elegancia",
    "razon_social": "Estilo y Elegancia S.R.L.",
    "nit": "987654321",
    "telefono": "71234567",
    "email": "contacto@estilo.com",
    "activo": true
  }
  ```
  *(Copia el `id` devuelto para los siguientes pasos como `ID_EMPRESA`)*

* **GET (Listar todas):** `http://localhost:8080/api/v1/empresas`
* **GET (Obtener por ID):** `http://localhost:8080/api/v1/empresas/ID_EMPRESA`
* **PUT (Actualizar):** `http://localhost:8080/api/v1/empresas/ID_EMPRESA`
  ```json
  {
    "nombre": "Estilo y Elegancia Modificado",
    "razon_social": "Estilo y Elegancia S.R.L. Modificada",
    "nit": "987654321",
    "telefono": "71234567",
    "email": "contacto@estilo.com",
    "activo": true
  }
  ```
* **DELETE (Eliminar):** `http://localhost:8080/api/v1/empresas/ID_EMPRESA`

---

## 2. CRUD de Usuario (`usuario`)

* **POST (Crear):** `http://localhost:8080/api/v1/usuarios`
  ```json
  {
    "empresa_id": "ID_EMPRESA",
    "nombre": "Carlos",
    "apellido": "Barbero",
    "email": "carlos.barbero@estilo.com",
    "password": "Password123*",
    "rol": "ROLE_EMPLEADO",
    "activo": true
  }
  ```

* **GET (Listar todos):** `http://localhost:8080/api/v1/usuarios`
  *(Copia el `id` de Carlos de la lista como `ID_USUARIO`)*

* **GET (Obtener por ID):** `http://localhost:8080/api/v1/usuarios/ID_USUARIO`
* **PUT (Actualizar):** `http://localhost:8080/api/v1/usuarios/ID_USUARIO`
  ```json
  {
    "empresa_id": "ID_EMPRESA",
    "nombre": "Carlos Modificado",
    "apellido": "Barbero",
    "email": "carlos.barbero@estilo.com",
    "password": "Password123*",
    "rol": "ROLE_EMPLEADO",
    "activo": true
  }
  ```
* **DELETE (Eliminar):** `http://localhost:8080/api/v1/usuarios/ID_USUARIO`

---

## 3. CRUD de Sucursal (`sucursal`)

* **POST (Crear):** `http://localhost:8080/api/v1/sucursales`
  ```json
  {
    "nombre": "Sucursal Norte",
    "direccion": "Av. Banzer Km 5",
    "telefono": "78912345",
    "empresa_id": "ID_EMPRESA"
  }
  ```
  *(Copia el `id` devuelto como `ID_SUCURSAL`)*

* **GET (Listar todas):** `http://localhost:8080/api/v1/sucursales`
* **GET (Obtener por ID):** `http://localhost:8080/api/v1/sucursales/ID_SUCURSAL`
* **PUT (Actualizar):** `http://localhost:8080/api/v1/sucursales/ID_SUCURSAL`
  ```json
  {
    "nombre": "Sucursal Norte Modificada",
    "direccion": "Av. Banzer Km 5 y 3er Anillo",
    "telefono": "78912345",
    "empresa_id": "ID_EMPRESA"
  }
  ```
* **DELETE (Eliminar):** `http://localhost:8080/api/v1/sucursales/ID_SUCURSAL`

---

## 4. CRUD de Especie (`especie`)

* **POST (Crear):** `http://localhost:8080/api/v1/especies`
  ```json
  {
    "nombre": "Perro",
    "activo": true
  }
  ```
  *(Copia el `id` devuelto como `ID_ESPECIE`)*

* **GET (Listar todas):** `http://localhost:8080/api/v1/especies`
* **GET (Obtener por ID):** `http://localhost:8080/api/v1/especies/ID_ESPECIE`
* **PUT (Actualizar):** `http://localhost:8080/api/v1/especies/ID_ESPECIE`
  ```json
  {
    "nombre": "Perro Modificado",
    "activo": true
  }
  ```
* **DELETE (Eliminar):** `http://localhost:8080/api/v1/especies/ID_ESPECIE`

---

## 5. CRUD de Raza (`raza`)

* **POST (Crear):** `http://localhost:8080/api/v1/razas`
  ```json
  {
    "nombre": "Golden Retriever",
    "especie_id": "ID_ESPECIE",
    "activo": true
  }
  ```
  *(Copia el `id` devuelto como `ID_RAZA`)*

* **GET (Listar todas):** `http://localhost:8080/api/v1/razas`
* **GET (Obtener por ID):** `http://localhost:8080/api/v1/razas/ID_RAZA`
* **PUT (Actualizar):** `http://localhost:8080/api/v1/razas/ID_RAZA`
  ```json
  {
    "nombre": "Golden Retriever Modificado",
    "especie_id": "ID_ESPECIE",
    "activo": true
  }
  ```
* **DELETE (Eliminar):** `http://localhost:8080/api/v1/razas/ID_RAZA`

---

## 6. CRUD de Cliente (`cliente`)

* **POST (Crear):** `http://localhost:8080/api/v1/clientes`
  ```json
  {
    "empresa_id": "ID_EMPRESA",
    "nombre": "Maria Gomez",
    "telefono": "70090010",
    "email": "maria.gomez@gmail.com",
    "documento": "9876543",
    "notas": "Cliente VIP, prefiere te en lugar de café",
    "activo": true
  }
  ```
  *(Copia el `id` devuelto como `ID_CLIENTE`)*

* **GET (Listar todos):** `http://localhost:8080/api/v1/clientes`
* **GET (Obtener por ID):** `http://localhost:8080/api/v1/clientes/ID_CLIENTE`
* **PUT (Actualizar):** `http://localhost:8080/api/v1/clientes/ID_CLIENTE`
  ```json
  {
    "empresa_id": "ID_EMPRESA",
    "nombre": "Maria Gomez Modificada",
    "telefono": "70090010",
    "email": "maria.gomez@gmail.com",
    "documento": "9876543",
    "notas": "Cliente VIP, prefiere te en lugar de café",
    "activo": true
  }
  ```
* **DELETE (Eliminar):** `http://localhost:8080/api/v1/clientes/ID_CLIENTE`

---

## 7. CRUD de Mascota (`mascota`)

* **POST (Crear):** `http://localhost:8080/api/v1/mascotas`
  ```json
  {
    "nombre": "Firulais",
    "edad": 3,
    "cliente_id": "ID_CLIENTE",
    "raza_id": "ID_RAZA",
    "activo": true
  }
  ```
  *(Copia el `id` devuelto como `ID_MASCOTA`)*

* **GET (Listar todas):** `http://localhost:8080/api/v1/mascotas`
* **GET (Obtener por ID):** `http://localhost:8080/api/v1/mascotas/ID_MASCOTA`
* **PUT (Actualizar):** `http://localhost:8080/api/v1/mascotas/ID_MASCOTA`
  ```json
  {
    "nombre": "Firulais Modificado",
    "edad": 4,
    "cliente_id": "ID_CLIENTE",
    "raza_id": "ID_RAZA",
    "activo": true
  }
  ```
* **DELETE (Eliminar):** `http://localhost:8080/api/v1/mascotas/ID_MASCOTA`

---

## 8. CRUD de Empleado (`empleado`)

* **POST (Crear):** `http://localhost:8080/api/v1/empleados`
  ```json
  {
    "nombre": "Carlos Alberto",
    "cargo": "BARBERO",
    "telefono": "76543210",
    "email": "carlos.barbero@gmail.com",
    "especialidad": "Cortes Degradados y Estética Canina",
    "foto_url": "http://example.com/carlos.jpg",
    "empresa_id": "ID_EMPRESA",
    "usuario_id": "ID_USUARIO",
    "disponible": true,
    "activo": true
  }
  ```
  *(Copia el `id` devuelto como `ID_EMPLEADO`)*

* **GET (Listar todos):** `http://localhost:8080/api/v1/empleados`
* **GET (Obtener por ID):** `http://localhost:8080/api/v1/empleados/ID_EMPLEADO`
* **PUT (Actualizar):** `http://localhost:8080/api/v1/empleados/ID_EMPLEADO`
  ```json
  {
    "nombre": "Carlos Alberto Modificado",
    "cargo": "BARBERO",
    "telefono": "76543210",
    "email": "carlos.barbero@gmail.com",
    "especialidad": "Degradados, Barba y Estética Canina",
    "foto_url": "http://example.com/carlos.jpg",
    "empresa_id": "ID_EMPRESA",
    "usuario_id": "ID_USUARIO",
    "disponible": true,
    "activo": true
  }
  ```
* **DELETE (Eliminar):** `http://localhost:8080/api/v1/empleados/ID_EMPLEADO`

---

## 9. CRUD de Empleado-Sucursal (`empleado_sucursal`)
*Establece qué empleado trabaja en qué sucursal.*

* **POST (Crear/Asignar):** `http://localhost:8080/api/v1/empleados-sucursales`
  ```json
  {
    "empleado_id": "ID_EMPLEADO",
    "sucursal_id": "ID_SUCURSAL",
    "activo": true
  }
  ```
  *(Copia el `id` devuelto como `ID_EMPLEADO_SUCURSAL`)*

* **GET (Listar todas):** `http://localhost:8080/api/v1/empleados-sucursales`
* **GET (Obtener por ID):** `http://localhost:8080/api/v1/empleados-sucursales/ID_EMPLEADO_SUCURSAL`
* **PUT (Actualizar):** `http://localhost:8080/api/v1/empleados-sucursales/ID_EMPLEADO_SUCURSAL`
  ```json
  {
    "empleado_id": "ID_EMPLEADO",
    "sucursal_id": "ID_SUCURSAL",
    "activo": false
  }
  ```
* **DELETE (Eliminar):** `http://localhost:8080/api/v1/empleados-sucursales/ID_EMPLEADO_SUCURSAL`

---

## 10. CRUD de Horario Empleado (`horario_empleado`)

* **POST (Crear):** `http://localhost:8080/api/v1/horarios-empleados`
  ```json
  {
    "empleado_id": "ID_EMPLEADO",
    "sucursal_id": "ID_SUCURSAL",
    "dia_semana": "LUNES",
    "hora_inicio": "09:00:00",
    "hora_fin": "18:00:00",
    "activo": true
  }
  ```
  *(Copia el `id` devuelto como `ID_HORARIO`)*

* **GET (Listar todos):** `http://localhost:8080/api/v1/horarios-empleados`
* **GET (Obtener por ID):** `http://localhost:8080/api/v1/horarios-empleados/ID_HORARIO`
* **PUT (Actualizar):** `http://localhost:8080/api/v1/horarios-empleados/ID_HORARIO`
  ```json
  {
    "empleado_id": "ID_EMPLEADO",
    "sucursal_id": "ID_SUCURSAL",
    "dia_semana": "LUNES",
    "hora_inicio": "10:00:00",
    "hora_fin": "19:00:00",
    "activo": true
  }
  ```
* **DELETE (Eliminar):** `http://localhost:8080/api/v1/horarios-empleados/ID_HORARIO`

---

## 11. CRUD de Servicio (`servicio`)

* **POST (Crear):** `http://localhost:8080/api/v1/servicios`
  ```json
  {
    "nombre": "Corte Clasico Premium",
    "descripcion": "Corte a maquina y tijera con lavado",
    "precio": 50.00,
    "duracion": 45,
    "destinatario": "HUMANO",
    "categoria": "BARBERIA",
    "empresa_id": "ID_EMPRESA"
  }
  ```
  *(Copia el `id` devuelto como `ID_SERVICIO`)*

* **GET (Listar todos):** `http://localhost:8080/api/v1/servicios`
* **GET (Obtener por ID):** `http://localhost:8080/api/v1/servicios/ID_SERVICIO`
* **PUT (Actualizar):** `http://localhost:8080/api/v1/servicios/ID_SERVICIO`
  ```json
  {
    "nombre": "Corte Clasico Premium Modificado",
    "descripcion": "Corte con maquina, toalla caliente y lavado profundo",
    "precio": 55.00,
    "duracion": 45,
    "destinatario": "HUMANO",
    "categoria": "BARBERIA",
    "empresa_id": "ID_EMPRESA"
  }
  ```
* **DELETE (Eliminar):** `http://localhost:8080/api/v1/servicios/ID_SERVICIO`

---

## 12. CRUD de Producto (`producto`)

* **POST (Crear):** `http://localhost:8080/api/v1/productos`
  ```json
  {
    "nombre": "Cera Moldeadora Mate",
    "descripcion": "Fijación fuerte, acabado mate",
    "precio": 15.00,
    "empresa_id": "ID_EMPRESA",
    "activo": true
  }
  ```
  *(Copia el `id` devuelto como `ID_PRODUCTO`)*

* **GET (Listar todos):** `http://localhost:8080/api/v1/productos`
* **GET (Obtener por ID):** `http://localhost:8080/api/v1/productos/ID_PRODUCTO`
* **PUT (Actualizar):** `http://localhost:8080/api/v1/productos/ID_PRODUCTO`
  ```json
  {
    "nombre": "Cera Moldeadora Mate Modificada",
    "descripcion": "Fijación ultra fuerte con agradable aroma",
    "precio": 18.00,
    "empresa_id": "ID_EMPRESA",
    "activo": true
  }
  ```
* **DELETE (Eliminar):** `http://localhost:8080/api/v1/productos/ID_PRODUCTO`

---

## 13. CRUD de Inventario (`inventario_sucursal`)

* **POST (Crear/Registrar Stock):** `http://localhost:8080/api/v1/inventario`
  ```json
  {
    "producto_id": "ID_PRODUCTO",
    "sucursal_id": "ID_SUCURSAL",
    "stock_actual": 50,
    "stock_minimo": 5,
    "activo": true
  }
  ```
  *(Copia el `id` devuelto como `ID_INVENTARIO`)*

* **GET (Listar todo):** `http://localhost:8080/api/v1/inventario`
* **GET (Obtener por ID):** `http://localhost:8080/api/v1/inventario/ID_INVENTARIO`
* **PUT (Actualizar):** `http://localhost:8080/api/v1/inventario/ID_INVENTARIO`
  ```json
  {
    "producto_id": "ID_PRODUCTO",
    "sucursal_id": "ID_SUCURSAL",
    "stock_actual": 60,
    "stock_minimo": 5,
    "activo": true
  }
  ```
* **DELETE (Eliminar):** `http://localhost:8080/api/v1/inventario/ID_INVENTARIO`

---

## 14. CRUD de Combo Servicio (`combo_servicio` y `combo_servicio_detalle`)

* **POST (Crear Combo con sus detalles):** `http://localhost:8080/api/v1/combos`
  ```json
  {
    "empresa_id": "ID_EMPRESA",
    "nombre": "Combo Barba & Corte",
    "descripcion": "Corte de pelo más perfilado de barba",
    "precio": 60.00,
    "duracion_minutos": 60,
    "activo": true,
    "detalles": [
      {
        "servicio_id": "ID_SERVICIO",
        "orden_ejecucion": 1
      }
    ]
  }
  ```
  *(Copia el `id` devuelto como `ID_COMBO`)*

* **GET (Listar todos):** `http://localhost:8080/api/v1/combos`
* **GET (Obtener por ID):** `http://localhost:8080/api/v1/combos/ID_COMBO`
* **PUT (Actualizar):** `http://localhost:8080/api/v1/combos/ID_COMBO`
  ```json
  {
    "empresa_id": "ID_EMPRESA",
    "nombre": "Combo Barba & Corte Modificado",
    "descripcion": "Corte premium y afeitado de barba clásico",
    "precio": 65.00,
    "duracion_minutos": 60,
    "activo": true,
    "detalles": [
      {
        "servicio_id": "ID_SERVICIO",
        "orden_ejecucion": 1
      }
    ]
  }
  ```
* **DELETE (Eliminar):** `http://localhost:8080/api/v1/combos/ID_COMBO`

---

## 15. CRUD de Venta (`venta` y `venta_detalle`)

* **POST (Crear Venta):** `http://localhost:8080/api/v1/ventas`
  *(Esto creará la venta, los detalles en cascada y **reducirá automáticamente 2 unidades del stock** del producto).*
  ```json
  {
    "sucursal_id": "ID_SUCURSAL",
    "cliente_id": "ID_CLIENTE",
    "descuento": 2.00,
    "notas": "Venta combinada de servicio y producto",
    "detalles": [
      {
        "tipo_item": "PRODUCTO",
        "producto_id": "ID_PRODUCTO",
        "cantidad": 2,
        "precio_unitario": 15.00,
        "descuento": 0.00,
        "notas": "2 ceras moldeadoras",
        "empleado_id": "ID_EMPLEADO"
      },
      {
        "tipo_item": "SERVICIO",
        "servicio_id": "ID_SERVICIO",
        "cantidad": 1,
        "precio_unitario": 50.00,
        "descuento": 0.00,
        "notas": "1 corte de pelo",
        "empleado_id": "ID_EMPLEADO"
      }
    ]
  }
  ```
  *(Copia el `id` devuelto como `ID_VENTA`)*

* **GET (Listar todas):** `http://localhost:8080/api/v1/ventas`
* **GET (Obtener por ID):** `http://localhost:8080/api/v1/ventas/ID_VENTA`
* **PUT (Actualizar Venta):** `http://localhost:8080/api/v1/ventas/ID_VENTA`
  *(Revertirá automáticamente el stock del producto modificado y aplicará el nuevo).*
  ```json
  {
    "sucursal_id": "ID_SUCURSAL",
    "cliente_id": "ID_CLIENTE",
    "descuento": 0.00,
    "notas": "Venta modificada a solo servicio de corte",
    "detalles": [
      {
        "tipo_item": "SERVICIO",
        "servicio_id": "ID_SERVICIO",
        "cantidad": 1,
        "precio_unitario": 50.00,
        "descuento": 0.00,
        "notas": "1 corte de pelo",
        "empleado_id": "ID_EMPLEADO"
      }
    ]
  }
  ```
* **DELETE (Eliminar Venta):** `http://localhost:8080/api/v1/ventas/ID_VENTA`
  *(El sistema revertirá el stock de los productos vendidos antes de borrar la venta).*

---

## 16. CRUD de Pago (`pago`)

* **POST (Generar Cobro QR por Stereum Pay):** `http://localhost:8080/api/v1/pagos/generar-qr`
  ```json
  {
    "venta_id": "ID_VENTA"
  }
  ```

* **POST (Crear Pago Manual / Efectivo):** `http://localhost:8080/api/v1/pagos`
  ```json
  {
    "venta_id": "ID_VENTA",
    "monto": 78.00,
    "metodo_pago": "EFECTIVO",
    "estado_pago": "PAGADO",
    "transaccion_externa_id": "EF-MANUAL-1"
  }
  ```
  *(Copia el `id` devuelto como `ID_PAGO`)*

* **GET (Listar todos los pagos):** `http://localhost:8080/api/v1/pagos`
* **GET (Obtener por ID):** `http://localhost:8080/api/v1/pagos/ID_PAGO`
* **PUT (Actualizar Pago):** `http://localhost:8080/api/v1/pagos/ID_PAGO`
  ```json
  {
    "venta_id": "ID_VENTA",
    "monto": 78.00,
    "metodo_pago": "QR",
    "estado_pago": "PAGADO",
    "transaccion_externa_id": "QR-TRANSFER-1"
  }
  ```
* **DELETE (Eliminar Pago):** `http://localhost:8080/api/v1/pagos/ID_PAGO`

---

## 17. CRUD de Agenda Evento (`agenda_evento`, `agenda_evento_detalle` y `agenda_evento_empleado`)
*Para agendar turnos o citas.*

* **POST (Crear Cita):** `http://localhost:8080/api/v1/agenda-eventos`
  ```json
  {
    "sucursal_id": "ID_SUCURSAL",
    "tipo_evento": "CITA",
    "inicio": "2026-06-03T10:00:00Z",
    "fin": "2026-06-03T11:00:00Z",
    "notas": "Cita para corte y revisión de mascota",
    "cliente_id": "ID_CLIENTE",
    "mascota_id": "ID_MASCOTA",
    "motivo": "Baño y corte de pelo",
    "detalles": [
      {
        "servicio_id": "ID_SERVICIO",
        "duracion_estimada_minutos": 60,
        "precio_acordado": 50.00,
        "notas": "Servicio de corte pactado"
      }
    ],
    "empleados": [
      {
        "empleado_id": "ID_EMPLEADO",
        "rol_en_evento": "RESPONSABLE"
      }
    ]
  }
  ```
  *(Copia el `agenda_evento_id` devuelto como `ID_CITA`)*

* **GET (Listar todas las citas):** `http://localhost:8080/api/v1/agenda-eventos`
* **GET (Obtener por ID):** `http://localhost:8080/api/v1/agenda-eventos/ID_CITA`
* **PUT (Actualizar Cita):** `http://localhost:8080/api/v1/agenda-eventos/ID_CITA`
  ```json
  {
    "sucursal_id": "ID_SUCURSAL",
    "tipo_evento": "CITA",
    "inicio": "2026-06-03T14:00:00Z",
    "fin": "2026-06-03T15:00:00Z",
    "notas": "Cita movida a la tarde por el cliente",
    "cliente_id": "ID_CLIENTE",
    "mascota_id": "ID_MASCOTA",
    "motivo": "Baño y corte de pelo",
    "detalles": [
      {
        "servicio_id": "ID_SERVICIO",
        "duracion_estimada_minutos": 60,
        "precio_acordado": 50.00,
        "notas": "Servicio de corte pactado"
      }
    ],
    "empleados": [
      {
        "empleado_id": "ID_EMPLEADO",
        "rol_en_evento": "RESPONSABLE"
      }
    ]
  }
  ```
* **DELETE (Eliminar Cita/Evento):** `http://localhost:8080/api/v1/agenda-eventos/ID_CITA`
