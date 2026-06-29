# DevJobs 🚀

API REST para conectar **desarrolladores** con **empresas** tecnológicas.  
Registrate, creá tu empresa, publicá ofertas laborales y postulate con un clic.

---

## Stack

| Capa | Tecnología |
|------|-----------|
| **Runtime** | Java 21 |
| **Framework** | Spring Boot 4.1.0 |
| **ORM** | Spring Data JPA / Hibernate |
| **Seguridad** | Spring Security + JWT (jjwt 0.12.6) |
| **Base de datos** | MySQL |
| **Validación** | Jakarta Validation |
| **Documentación** | Springdoc OpenAPI (Swagger UI) |
| **Utilidades** | Lombok, Slf4j |

---
## Arquitectura

- EC2 t3.small (Ubuntu) → Spring Boot en Docker, IP pública, puerto 80(HTTP)
- EC2 t3.small (Ubuntu)  → Base de datos MySQL en una instancia separada.

---

## Setup

```bash
# 1. Clonar
git clone https://github.com/thomyconhachedev/devjobs.git
cd devjobs

# 2. Configurar base de datos y JWT en application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/devjobs
spring.datasource.username=root
spring.datasource.password=tu-password
app.jwt.secret=tu-llave-secreta-jwt

# 2.1 Configurar base de datos y JWT en application.yaml 
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/devjobs_db
    username: db_user
    password: db_password

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    open-in-view: false
    database-platform: org.hibernate.dialect.MySQLDialect
jwt:
  secret: your-secret-here
  expiration: 3600000 # 1 hora en milisegundos

# 3. Ejecutar
mvnw spring-boot:run
```

> La app arranca en `http://localhost:8080`.  
> Swagger UI disponible en `http://localhost:8080/swagger-ui.html`.

---

## Endpoints

### Auth (`/api/v1/auth`)

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `POST` | `/register` | Registrarse como CANDIDATE o EMPLOYER | ❌ |
| `POST` | `/login` | Iniciar sesión, devuelve JWT | ❌ |

### Companies (`/api/v1/company`)

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `GET` | `/getAll` | Listar todas las compañías (paginado) | ❌ |
| `GET` | `/getById/{id}` | Obtener compañía por ID | ❌ |
| `POST` | `/createCompany` | Crear compañía (solo EMPLOYER) | ✅ |
| `PATCH` | `/updateCompany/{id}` | Actualizar compañía (solo dueño) | ✅ |
| `DELETE` | `/deleteCompany/{id}` | Eliminar compañía (solo dueño) | ✅ |

### Jobs (`/api/v1/job`)

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `GET` | `/getAllJobs` | Listar ofertas activas (con filtros y paginación) | ❌ |
| `GET` | `/getJobById/{id}` | Detalle de oferta | ❌ |
| `GET` | `/getCompanyJobs/companyId/{id}/jobs` | Ofertas activas de una empresa | ❌ |
| `POST` | `/createJob` | Crear oferta laboral (solo dueño de la company) | ✅ |
| `PATCH` | `/updateJob/{id}` | Actualizar oferta (solo dueño) | ✅ |
| `PATCH` | `/activateJob/{id}` | Reactivar oferta (solo dueño) | ✅ |
| `DELETE` | `/deactivateJob/{id}` | Desactivar oferta (solo dueño) | ✅ |

**Filtros en GET `/getAllJobs`:** `technology`, `modality`, `seniority`, `location` + paginación (`page`, `size`).

### Applications (`/api/v1/app`)

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `POST` | `/apply/{jobId}` | Postularse a una oferta (solo CANDIDATE) | ✅ |
| `GET` | `/getApplications/me` | Mis postulaciones (paginado) | ✅ |
| `GET` | `/getApplicationsByJobId/{id}` | Postulaciones de una oferta (solo dueño, paginado) | ✅ |
| `GET` | `/getApplicationsEventsId/{id}` | Historial de cambios de una postulación | ✅ |
| `PATCH` | `/changeStatusId/{id}` | Cambiar estado de postulación (solo dueño) | ✅ |

### Admin (`/api/v1/admin`)

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `GET` | `/users` | Listar todos los usuarios (paginado) | 🔒 ADMIN |
| `GET` | `/users/{id}` | Obtener usuario por ID | 🔒 ADMIN |
| `DELETE` | `/users/{id}` | Eliminar usuario | 🔒 ADMIN |

---

## Modelo de datos

```
User (users)
├── id, email, password_hash, full_name, role, created_at, updated_at
└── tiene → Company (owner)

Company (companies)
├── id, owner_id → User, name, industry, website, description, location, created_at
└── tiene → JobPosting []

JobPosting (job_postings)
├── id, company_id → Company, title, description, location, modality,
│   seniority, salary_min, salary_max, is_active, published_at, expires_at
├── technologies → job_technologies []
└── tiene → Application []

Application (applications)
├── id, user_id → User, job_id → JobPosting, status, cover_letter,
│   cv_url, applied_at, updated_at
└── tiene → ApplicationEvent []

ApplicationEvent (application_events)
    id, application_id → Application, from_status, to_status, note, changed_at
```

### Enums

| Enum | Valores |
|------|---------|
| **Role** | `CANDIDATE`, `EMPLOYER`, `ADMIN` |
| **Modality** | `REMOTE`, `HYBRID`, `ON_SITE` |
| **Seniority** | `JUNIOR`, `MID`, `SENIOR`, `LEAD` |
| **ApplicationStatus** | `PENDING`, `REVIEWING`, `INTERVIEW`, `REJECTED`, `ACCEPTED` |

---

## Manejo de errores

Todas las excepciones devuelven una respuesta uniforme:

```json
{
  "status": 409,
  "message": "Email ya existe.",
  "path": "/api/v1/auth/register",
  "timestamp": "2026-06-21T12:00:00"
}
```

| Excepción | Status | Cuándo ocurre |
|-----------|--------|---------------|
| `ResourceNotFoundException` | 404 | Entidad no encontrada |
| `UnauthorizedActionException` | 401 | Credenciales inválidas o acción no permitida |
| `AuthenticationException` | 401 | No autenticado |
| `AccessDeniedException` | 403 | No tienes permisos para este recurso |
| `DuplicateEmailException` | 409 | Email ya registrado |
| `DuplicateApplicationException` | 409 | Ya te postulaste a esa oferta |
| `DataIntegrityViolationException` | 409 | Conflicto de integridad de datos |
| `MethodArgumentNotValidException` | 400 | Errores de validación en los campos |
| `Exception` (general) | 500 | Error interno |

---

## Roles y permisos

| Rol | Puede hacer |
|-----|-------------|
| **CANDIDATE** | Registrarse, ver ofertas, postularse, ver sus postulaciones |
| **EMPLOYER** | Registrar empresa, crear/editar/activar/desactivar ofertas de su empresa, revisar postulaciones, cambiar estados |
| **ADMIN** | Todo lo anterior + gestión de usuarios (`/api/v1/admin/**`) |

El JWT incluye los claims `role` y `email`, validados en cada request mediante `JwtAuthFilter`.

---

## Ejemplos rápidos

```bash
# Registrar un employer
curl -X POST localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"empresa@mail.com","password":"123456","fullName":"Mi Empresa","role":"EMPLOYER"}'

# Login
curl -X POST localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"empresa@mail.com","password":"123456"}'

# Crear empresa (usar el token del login)
curl -X POST localhost:8080/api/v1/company/createCompany \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"name":"Tech Corp","industry":"Software","location":"Remote"}'

# Listar ofertas con filtros
curl "localhost:8080/api/v1/job/getAllJobs?technology=Java&modality=REMOTE&page=0&size=10"

# Postularse (como CANDIDATE)
curl -X POST localhost:8080/api/v1/app/apply/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"cvUrl":"https://cv.com/mi-cv.pdf"}'
```

---
## Motivación

Este proyecto nace con fines personales y académicos. Mi motivación principal es aprender y mejorar continuamente
mis habilidades como desarrollador backend a través de la práctica constante y la creación de proyectos funcionales.
Subir este tipo de proyectos a GitHub me permite documentar mi progreso, aplicar nuevos conceptos y construir un
portafolio sólido que refleje mi camino hacia mi primer empleo en el área de desarrollo de software.

---
## Licencia

MIT
