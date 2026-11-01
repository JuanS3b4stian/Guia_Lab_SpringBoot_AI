# Project Development Instructions

Estas instrucciones son obligatorias para todo el desarrollo del backend de este
workspace. GitHub Copilot debe leerlas y aplicarlas antes de proponer o modificar
código.

## Alcance tecnológico

- El backend está ubicado en `proyecto/`.
- Usar Java 21 de forma estricta.
- Usar Spring Boot y sus starters ya definidos en `proyecto/pom.xml`.
- Usar Maven Wrapper para compilar, probar y ejecutar el proyecto:
  - Windows: `.\mvnw.cmd`
  - Linux/macOS: `./mvnw`
- No introducir APIs, sintaxis o dependencias incompatibles con Java 21.
- Preferir APIs estándar de Java 21, `java.time`, colecciones inmutables,
  `Optional` cuando corresponda y `record` para datos inmutables.

## Convención obligatoria de nombres

Todos los nombres de código deben estar en inglés, sin excepciones:

- Clases: `PascalCase`, por ejemplo `TransferService`.
- Interfaces: `PascalCase`, por ejemplo `AccountRepository`.
- Métodos: `camelCase`, por ejemplo `createTransfer`.
- Atributos y variables: `camelCase`, por ejemplo `sourceAccount`.
- Constantes: `UPPER_SNAKE_CASE`, por ejemplo `MAX_TRANSFER_AMOUNT`.
- Paquetes: minúsculas, por ejemplo `com.tdea.proyecto.service`.
- Los nombres de endpoints, parámetros JSON, campos de entidades y DTOs también
  deben estar en inglés.
- No crear clases, métodos, atributos, variables ni paquetes con nombres en
  español.

## Documentación y Javadoc

- Todo método público debe tener Javadoc claro, actualizado y escrito en
  inglés.
- La documentación debe explicar el propósito, parámetros, valor retornado,
  excepciones relevantes y efectos secundarios cuando existan.
- Los métodos sobrescritos pueden usar `{@inheritDoc}` cuando la documentación
  heredada sea suficiente.
- Las clases públicas que representen contratos de aplicación, servicios,
  controladores o componentes de infraestructura deben documentar su propósito.
- Mantener la documentación sincronizada con el comportamiento real.

Ejemplo:

```java
/**
 * Transfers funds between two bank accounts.
 *
 * @param request transfer data validated by the application layer
 * @return the created transfer response
 * @throws InsufficientFundsException when the source account lacks funds
 */
public TransferResponse createTransfer(CreateTransferRequest request) {
    // ...
}
```

## DTOs

- Todos los Data Transfer Objects deben implementarse exclusivamente como
  `record` de Java.
- No crear DTOs mutables con clases tradicionales, getters/setters o Lombok.
- Separar los DTOs de entrada y salida cuando tengan responsabilidades
  diferentes; por ejemplo, `CreateTransferRequest` y `TransferResponse`.
- Aplicar validación Bean Validation directamente sobre los componentes del
  record cuando corresponda.
- No exponer entidades JPA directamente desde los controladores.
- Mapear explícitamente entre entidades y DTOs mediante mappers o métodos
  dedicados.

Ejemplo:

```java
/**
 * Request used to create a money transfer.
 *
 * @param sourceAccountId identifier of the account to debit
 * @param destinationAccountId identifier of the account to credit
 * @param amount amount to transfer
 */
public record CreateTransferRequest(
        @NotNull Long sourceAccountId,
        @NotNull Long destinationAccountId,
        @NotNull @Positive BigDecimal amount) {
}
```

## Estructura del proyecto

Ubicar cada componente en el paquete correspondiente bajo
`proyecto/src/main/java/com/tdea/proyecto/`:

```text
proyecto/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/tdea/proyecto/
    │   │   ├── ProyectoApplication.java
    │   │   ├── config/
    │   │   ├── controllers/
    │   │   ├── dtos/
    │   │   ├── entities/
    │   │   ├── exceptions/
    │   │   ├── mappers/
    │   │   ├── repositories/
    │   │   └── services/
    │   └── resources/
    │       ├── application.properties
    │       ├── static/
    │       └── templates/
    └── test/
        └── java/com/tdea/proyecto/
```

### Responsabilidades por paquete

- `controllers/`: controladores REST; reciben requests, validan entradas,
  delegan en servicios y devuelven DTOs. No contener lógica de negocio.
- `services/`: casos de uso y reglas de negocio. Coordinar transacciones y
  validar invariantes del dominio.
- `repositories/`: interfaces de acceso a datos basadas en Spring Data JPA.
  No colocar reglas de negocio aquí.
- `entities/`: entidades persistentes JPA y sus relaciones. No usarlas como
  contratos de la API.
- `dtos/`: records de entrada, salida y respuesta de la API.
- `mappers/`: conversión explícita entre entidades y DTOs.
- `config/`: configuración de Spring, seguridad, OpenAPI, CORS y beans.
- `exceptions/`: excepciones de dominio y manejo global de errores.
- `resources/`: configuración y recursos no Java.
- `src/test/`: pruebas unitarias, de integración y de controladores, respetando
  la misma organización de paquetes del código productivo.

## Spring Boot y diseño de la API

- Usar inyección por constructor; no usar `@Autowired` sobre atributos.
- Mantener los controladores delgados y delegar la lógica a servicios.
- Usar `@Transactional` en el límite del caso de uso, normalmente en métodos
  de servicio que modifican el estado.
- En transferencias, asegurar atomicidad: el débito y el crédito deben
  confirmarse juntos o revertirse juntos.
- Usar `BigDecimal` para importes monetarios; nunca `double` o `float`.
- Validar saldo, cuentas de origen y destino, moneda y monto antes de persistir.
- Evitar transferencias hacia la misma cuenta salvo que el caso de negocio lo
  permita explícitamente.
- Definir estados de transferencia mediante tipos seguros, preferiblemente
  `enum`.
- Usar códigos HTTP y respuestas de error consistentes.
- Centralizar el tratamiento de errores con `@RestControllerAdvice`.
- No devolver stack traces, credenciales, datos sensibles ni detalles internos
  en respuestas HTTP.
- Documentar los endpoints públicos con SpringDoc/OpenAPI cuando se creen o
  modifiquen.

## Persistencia y seguridad

- Diseñar entidades JPA con identificadores, restricciones y relaciones
  explícitas.
- Evitar `FetchType.EAGER` salvo justificación documentada.
- Controlar problemas de N+1 con consultas apropiadas y revisar el rendimiento.
- Usar migraciones versionadas si se incorpora una herramienta de migración.
- Nunca incluir credenciales de PostgreSQL, secretos o tokens en el código,
  `application.properties` versionado o archivos de instrucciones.
- Leer configuración sensible desde variables de entorno o un mecanismo seguro
  de configuración.
- No registrar contraseñas, tokens, códigos QR privados ni información
  financiera completa.

## Calidad y verificación

- Antes de finalizar un cambio, ejecutar como mínimo:
  `.\mvnw.cmd test` desde `proyecto/` en Windows.
- Para una validación rápida de configuración usar
  `.\mvnw.cmd validate`.
- Añadir o actualizar pruebas para cada comportamiento nuevo o modificado.
- Probar casos exitosos, entradas inválidas, errores de negocio y límites
  monetarios relevantes.
- Mantener cambios pequeños, coherentes y compatibles con la arquitectura.
- No introducir dependencias nuevas si una dependencia existente resuelve el
  requisito.
- No ignorar errores de compilación, pruebas fallidas o advertencias relevantes.
