# Banking Account Service

Microservicio para la gestión de cuentas bancarias: creación de cuentas, consignaciones, retiros y consulta de saldo.

## Tecnologías

- Java 17
        - Spring Boot 4.1.0
        - Spring Data JPA
- PostgreSQL 16
        - Maven
- Lombok
- Spring Security + JWT

## Prerrequisitos

- Java 17 (Eclipse Temurin recomendado — https://adoptium.net)
        - Maven (o usar el wrapper `mvnw` incluido, no requiere instalación aparte)
- Docker Desktop (https://www.docker.com/products/docker-desktop)
        - Git

        Verificar instalación:
        ```bash
        java -version
        mvn -version
        docker --version
        git --version
        ```

        ## 1. Clonar el repositorio

        ```bash
        git clone https://github.com/NikolasSantofimio/banking-account-service.git
        cd banking-account-service
        ```

        ## 2. Levantar PostgreSQL con Docker

        El proyecto usa un contenedor de PostgreSQL en lugar de una instalación local.

        **Verificar si Docker Desktop está corriendo:**
        ```bash
        docker --version
        ```

        **Crear y levantar el contenedor** (primera vez):
        ```bash
docker run --name banking-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=banking -p 5432:5432 -d postgres:16
        ```

        **Si el contenedor ya existe pero está apagado** (en ejecuciones posteriores):
        ```bash
docker start banking-postgres
```

        **Verificar que quedó corriendo:**
        ```bash
docker ps
```
Debe aparecer `banking-postgres` con estado `Up` y el puerto `0.0.0.0:5432->5432/tcp`.

        **Detener el contenedor** (cuando termines de trabajar):
        ```bash
docker stop banking-postgres
```

        ### Solución de problemas comunes

| Problema | Causa | Solución |
        |---|---|---|
        | `port is already allocated` | Ya hay algo usando el puerto 5432 | Detén el otro proceso o cambia el mapeo a `-p 5433:5432` y actualiza `application.properties` |
        | `Connection refused: getsockopt` al correr la app | El contenedor no está corriendo | Ejecuta `docker start banking-postgres` |
        | `docker: command not found` | Docker Desktop no está instalado o no está en el PATH | Reinstala Docker Desktop y reinicia la terminal |
        | El contenedor no aparece ni con `docker ps -a` | Falló la creación silenciosamente | Corre el `docker run` de nuevo y copia el mensaje de error completo |

        ## 3. Configurar variables de entorno (opcional)

Por defecto, `application.properties` ya trae valores de desarrollo (usuario/clave `postgres`/`postgres`, JWT con secreto de ejemplo). Para producción, sobreescribe con variables de entorno:

        ```bash
export DB_URL=jdbc:postgresql://localhost:5432/banking
export DB_USER=postgres
export DB_PASSWORD=postgres
export JWT_SECRET=<Colocar valor secreto>
        ```

        ## 4. Compilar y correr la aplicación

**Con el wrapper de Maven (recomendado, no requiere Maven instalado):**
        ```bash
        ./mvnw clean install
        ./mvnw spring-boot:run
```
En Windows (PowerShell):
        ```powershell
        .\mvnw.cmd clean install
        .\mvnw.cmd spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`.

        ## 5. Probar los endpoints

### 5.1. Obtener token JWT
```bash
curl -X POST http://localhost:8080/auth/token \
        -H "Content-Type: application/json" \
        -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
        ```
Copia el `token` de la respuesta para usarlo en los siguientes requests.

### 5.2. Crear cuenta
```bash
curl -X POST http://localhost:8080/accounts \
        -H "Authorization: Bearer <TOKEN>" \
        -H "Content-Type: application/json" \
        -d "{\"accountHolder\":\"Saulo Santofimio\"}"
        ```

        ### 5.3. Depositar
```bash
curl -X POST http://localhost:8080/accounts/1/deposit \
        -H "Authorization: Bearer <TOKEN>" \
        -H "Content-Type: application/json" \
        -d "{\"amount\":100000}"
        ```

        ### 5.4. Retirar
```bash
curl -X POST http://localhost:8080/accounts/1/withdraw \
        -H "Authorization: Bearer <TOKEN>" \
        -H "Content-Type: application/json" \
        -d "{\"amount\":30000}"
        ```

        ### 5.5. Consultar saldo
```bash
curl http://localhost:8080/accounts/1/balance \
        -H "Authorization: Bearer <TOKEN>"
        ```

        ## 6. Correr las pruebas unitarias

```bash
        ./mvnw test
```

        ## Estructura del proyecto

```
src/main/java/com/bankingtest/accountservice/
        ├── controller/     # Endpoints REST
├── service/        # Lógica de negocio
├── repository/     # Spring Data JPA
├── model/          # Entidades JPA
├── dto/            # Request/Response
├── exception/       # Excepciones + manejo global de errores
└── security/        # JWT (filtro, config, utilidades)
```

        ## CORS

Por defecto el backend solo acepta requests desde `http://localhost:5173` (el frontend React en desarrollo). Está configurado en `SecurityConfig.java`. Si despliegas el frontend en otro dominio, actualiza el origen permitido ahí (o parametrízalo vía variable de entorno antes de ir a producción).

        ## Repositorio del frontend

El cliente React vive en un repositorio separado: `https://github.com/NikolasSantofimio/banking-account-ui` (ver su propio README para instrucciones de instalación y ejecución).

