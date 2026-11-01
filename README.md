# Transferencias App

Aplicación para gestionar transferencias de dinero entre cuentas bancarias. El
proyecto está organizado en un backend REST con Spring Boot y un frontend
ejecutable con React, TypeScript y Vite.

## Estado actual

El backend está funcional para:

- Persistir cuentas y transacciones en PostgreSQL.
- Procesar transferencias de forma transaccional.
- Validar cuentas, monto y saldo disponible.
- Revertir transferencias completadas.
- Generar un código QR en Base64 para cada transferencia exitosa.
- Exponer la API documentada mediante Swagger/OpenAPI.
- Permitir solicitudes CORS desde `http://localhost:3000`.

El frontend cuenta con una aplicación Vite ejecutable, formulario de
transferencias, selectores dinámicos de cuentas, historial de transacciones y
estilos responsive.

## Stack tecnológico

### Backend

- Java 21.
- Spring Boot 4.1.2.
- Spring Web MVC.
- Spring Data JPA.
- Spring Security.
- Bean Validation.
- PostgreSQL.
- SpringDoc OpenAPI 3.0.0.
- ZXing Core y JavaSE 3.5.3 para generación de códigos QR.
- Maven Wrapper.

### Frontend

- React 19.
- TypeScript.
- Axios para comunicación HTTP.
- `qrcode.react` para renderizar códigos QR.
- Vite 8 como entorno de desarrollo y compilación.
- Node.js 22 como entorno recomendado.

## Arquitectura

El backend utiliza una arquitectura por capas:

```text
proyecto/
└── src/
    ├── main/
    │   ├── java/com/tdea/proyecto/
    │   │   ├── config/
    │   │   ├── controller/
    │   │   ├── dto/
    │   │   ├── entity/
    │   │   ├── exception/
    │   │   ├── repository/
    │   │   ├── service/
    │   │   └── ProyectoApplication.java
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/tdea/proyecto/
```

### Responsabilidades

- `controller`: expone endpoints REST, valida requests y devuelve respuestas
  HTTP.
- `service`: contiene las reglas de negocio, coordina transferencias y
  transacciones.
- `repository`: interfaces Spring Data JPA para acceso a PostgreSQL.
- `entity`: entidades persistentes JPA.
- `dto`: contratos de entrada y salida de la API implementados con Java
  `record`.
- `exception`: excepciones de dominio para cuentas, saldo y transacciones.
- `config`: configuración de seguridad y CORS.

El frontend sigue una separación por responsabilidades:

```text
frontend/
├── index.html
├── vite.config.ts
├── package.json
├── .env.example
└── src/
    ├── App.tsx
    ├── App.css
    ├── main.tsx
    ├── components/
    │   ├── TransactionHistory/
    │   └── TransferForm/
    ├── services/
    │   └── TransactionService.ts
    ├── types/
    │   └── transaction.ts
    └── *.d.ts
```

## Modelo de datos

### Account

Representa una cuenta bancaria:

- `id`: identificador autogenerado.
- `accountNumber`: número único y obligatorio.
- `ownerName`: nombre del titular.
- `balance`: saldo usando `BigDecimal`.

### Transaction

Representa una transferencia:

- `id`: identificador autogenerado.
- `sourceAccount`: cuenta de origen.
- `destinationAccount`: cuenta de destino.
- `amount`: monto transferido.
- `transactionDate`: fecha y hora.
- `status`: `COMPLETED`, `FAILED` o `PENDING`.

Las relaciones con `Account` usan `ManyToOne` con carga `LAZY`. El esquema se
actualiza automáticamente durante el desarrollo mediante
`spring.jpa.hibernate.ddl-auto=update`.

## Configuración del backend

La configuración se encuentra en
[`proyecto/src/main/resources/application.properties`](proyecto/src/main/resources/application.properties).

Valores principales:

```properties
spring.application.name=transferencias-app
spring.datasource.url=jdbc:postgresql://localhost:5432/transferencias_db
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false
```

No se deben publicar credenciales reales en el repositorio. Para otros
entornos, configura el usuario y la contraseña mediante variables de entorno o
un mecanismo seguro de configuración.

## Requisitos previos

- JDK 21.
- PostgreSQL ejecutándose en `localhost:5432`.
- Base de datos `transferencias_db`.
- Usuario de PostgreSQL con permisos sobre la base de datos.
- Node.js 22 para trabajar con el frontend.

Crear la base de datos desde pgAdmin o `psql`:

```sql
CREATE DATABASE transferencias_db;
```

Las tablas `accounts` y `transactions` se crean o actualizan mediante JPA al
iniciar el backend.

## Ejecución del backend

Desde la carpeta `proyecto/`:

### Validar configuración

Windows:

```powershell
.\mvnw.cmd validate
```

Linux/macOS:

```bash
./mvnw validate
```

### Compilar

Windows:

```powershell
.\mvnw.cmd compile
```

Linux/macOS:

```bash
./mvnw compile
```

### Ejecutar pruebas

Windows:

```powershell
.\mvnw.cmd test
```

Linux/macOS:

```bash
./mvnw test
```

### Iniciar la aplicación

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

El backend queda disponible en `http://localhost:8080`.

## Swagger y OpenAPI

Con el backend iniciado, abre:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Documento OpenAPI: `http://localhost:8080/v3/api-docs`

Spring Security protege los endpoints de negocio. Durante el desarrollo,
Spring Boot puede mostrar en la consola una contraseña generada para el usuario
`user`. Swagger y el documento OpenAPI están permitidos explícitamente por la
configuración de seguridad.

## API de transferencias

### GET `/transactions`

Obtiene el historial completo.

Respuesta esperada:

```json
[
  {
    "id": 1,
    "sourceAccountId": 1,
    "destinationAccountId": 2,
    "amount": 25000.00,
    "transactionDate": "2026-10-31T23:40:00",
    "status": "COMPLETED",
    "qrCodeBase64": "data:image/png;base64,..."
  }
]
```

### GET `/accounts`

Obtiene las cuentas disponibles para realizar transferencias.

Respuesta:

```json
[
  {
    "id": 1,
    "accountNumber": "ACC-001",
    "ownerName": "Juan Perez",
    "balance": 100000.00
  }
]
```

### POST `/transactions`

Procesa una transferencia y devuelve el QR generado.

Request:

```json
{
  "sourceAccountId": 1,
  "destinationAccountId": 2,
  "amount": 25000.00
}
```

Respuesta exitosa: `201 Created`.

Reglas aplicadas:

- Ambas cuentas deben existir.
- La cuenta de origen debe tener saldo suficiente.
- La cuenta de origen y destino deben ser diferentes.
- El monto debe ser mayor que cero.
- El débito y el crédito se confirman juntos mediante `@Transactional`.

### DELETE `/transactions/{id}`

Revierte una transferencia completada y restaura los saldos.

Ejemplo:

```text
DELETE http://localhost:8080/transactions/1
```

Respuesta exitosa: `204 No Content`.

### Errores principales

- `400 Bad Request`: saldo insuficiente, cuentas iguales, estado no reversible
  o datos de entrada inválidos.
- `404 Not Found`: cuenta o transacción inexistente.
- `401 Unauthorized` y `403 Forbidden`: pueden aparecer en rutas protegidas
  adicionales si se incorporan reglas de autenticación.

## Datos de prueba

Después de iniciar el backend y permitir que JPA cree las tablas, inserta dos
cuentas desde pgAdmin:

```sql
INSERT INTO accounts (account_number, owner_name, balance)
VALUES
    ('ACC-001', 'Juan Perez', 100000.00),
    ('ACC-002', 'Maria Gomez', 50000.00);
```

Consulta los identificadores reales:

```sql
SELECT id, account_number, owner_name, balance
FROM accounts
ORDER BY id;
```

Usa esos IDs en Swagger. Para verificar los saldos:

```sql
SELECT id, account_number, balance
FROM accounts
ORDER BY id;
```

Para consultar las transferencias:

```sql
SELECT *
FROM transactions
ORDER BY id;
```

## Frontend

La URL del backend se configura mediante Vite:

```env
VITE_BACKEND_URL=http://localhost:8080
```

El ejemplo se encuentra en
[`frontend/.env.example`](frontend/.env.example). Copia ese archivo como
`.env` dentro de `frontend/` y ajusta el valor si el backend se ejecuta en otro
host o puerto.

### Servicio HTTP

[`TransactionService.ts`](frontend/src/services/TransactionService.ts) expone:

- `getAllAccounts()`.
- `getAllTransactions()`.
- `createTransaction(request)`.
- `revertTransaction(transactionId)`.

### Componentes

- `TransactionHistory`: muestra el historial en una tabla, permite actualizarlo
  y revertir transferencias completadas.
- `TransferForm`: carga las cuentas desde `GET /accounts`, muestra selectores
  para origen y destino, procesa el monto y muestra el código QR devuelto por el
  backend.
- `App`: compone el formulario y el historial dentro del layout principal.

La aplicación se monta desde `main.tsx` con `React.StrictMode`. El archivo
`vite.config.ts` configura el plugin de React y fuerza el puerto `3000`.

### Configuración del frontend

Variables locales en `frontend/.env`:

```env
VITE_BACKEND_URL=http://localhost:8080
VITE_API_USER=user
VITE_API_PASSWORD=REPLACE_WITH_GENERATED_SPRING_PASSWORD
```

El archivo `.env` está excluido por `frontend/.gitignore`. No publiques
credenciales reales en el repositorio.

Importación de ejemplo:

```tsx
import { TransactionHistory } from "./components/TransactionHistory/TransactionHistory";
import { TransferForm } from "./components/TransferForm/TransferForm";

export default function App() {
  return (
    <>
      <TransferForm />
      <TransactionHistory />
    </>
  );
}
```

El backend permite CORS exclusivamente desde `http://localhost:3000`. Si el
frontend se sirve desde otro puerto, actualiza `CorsConfig` antes de probar la
comunicación entre aplicaciones.

## Seguridad y configuración CORS

La configuración actual:

- Deshabilita CSRF para facilitar el consumo de la API REST durante desarrollo.
- Permite Swagger y OpenAPI.
- Permite `/accounts` y las operaciones de `/transactions` para facilitar el
  consumo desde el frontend durante desarrollo.
- Permite solicitudes desde `http://localhost:3000`.
- Permite `GET`, `POST`, `PUT`, `DELETE` y `OPTIONS`.
- Permite todos los headers CORS.

Para producción se debe reemplazar la configuración de desarrollo por
autenticación explícita, secretos externos, HTTPS, una política CORS limitada y
un esquema de migraciones versionadas.

## Verificación rápida

Backend:

```powershell
cd proyecto
.\mvnw.cmd test
```

Frontend:

```powershell
cd frontend
npm install
npm run build
```

Para iniciar el frontend en desarrollo:

```powershell
cd frontend
npm run dev
```

La aplicación queda disponible en `http://localhost:3000`.

El chequeo TypeScript se incluye automáticamente en `npm run build` y también
puede ejecutarse con:

```powershell
cd frontend
.\node_modules\.bin\tsc.cmd --noEmit
```

## Convenciones de desarrollo

- Usar Java 21 estrictamente.
- Mantener nombres de código en inglés.
- Usar `record` para DTOs.
- Documentar métodos públicos con Javadoc.
- Usar `BigDecimal` para valores monetarios.
- Mantener los controladores delgados y concentrar reglas en servicios.
- Usar inyección por constructor.
- Añadir o actualizar pruebas para cada comportamiento nuevo.
- Ejecutar `.\mvnw.cmd test` antes de finalizar cambios del backend.

Las reglas completas para GitHub Copilot están en
[`.github/copilot-instructions.md`](.github/copilot-instructions.md).
