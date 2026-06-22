# DevJobs 🚀

API REST para conectar **desarrolladores** con **empresas** tecnológicas.  
Registrate, creá tu empresa, publicá ofertas laborales y postulate con un clic.

---

## Stack

| Capa | Tecnología |
|------|-----------|
| **Runtime** | Java 21 |
| **Framework** | Spring Boot 3.4 |
| **ORM** | Spring Data JPA / Hibernate |
| **Seguridad** | Spring Security + JWT (jjwt 0.12) |
| **Base de datos** | MySQL |
| **Validación** | Jakarta Validation |
| **Utilidades** | Lombok, Slf4j |

---

## Setup

```bash
# 1. Clonar
git clone https://github.com/tu-usuario/devjobs.git
cd devjobs

# 2. Configurar base de datos en application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/devjobs
spring.datasource.username=root
spring.datasource.password=tu-password
app.jwt.secret=tu-secreto-jwt

# 3. Ejecutar
./mvnw spring-boot:run
```

> La app arranca en `http://localhost:8080`.

---

## Endpoints

### Auth (`/api/v1/auth`)

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `POST` | `/register` | Registrarse como CANDIDATE o EMPLOYER | ❌ |
| `POST` | `/login` | Iniciar sesión, devuelve JWT | ❌ |

### Companies (`/api/v1/companies`)

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `POST` | `/` | Crear empresa (solo EMPLOYER) | ✅ |
| `GET` | `/{id}` | Obtener empresa por ID | ❌ |
| `PATCH` | `/{id}` | Actualizar empresa (solo dueño) | ✅ |
| `DELETE` | `/{id}` | Eliminar empresa (solo dueño) | ✅ |

### Jobs (`/api/v1/jobs`)

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `POST` | `/` | Crear oferta laboral (solo dueño de la company) | ✅ |
| `GET` | `/` | Listar ofertas activas (con filtros y paginación) | ❌ |
| `GET` | `/{id}` | Detalle de oferta | ❌ |
| `PATCH` | `/{id}` | Actualizar oferta (solo dueño) | ✅ |
| `PATCH` | `/{id}/deactivate` | Desactivar oferta (solo dueño) | ✅ |
| `GET` | `/company/{companyId}` | Ofertas activas de una empresa | ❌ |

**Filtros disponibles en GET `/`:** `technology`, `modality`, `seniority`, `location` + paginación (`page`, `size`).

### Applications (`/api/v1/app`)

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `POST` | `/{jobId}/apply` | Postularse a una oferta (solo CANDIDATE) | ✅ |
| `GET` | `/my-applications` | Mis postulaciones (solo CANDIDATE) | ✅ |
| `GET` | `/job/{jobId}` | Ver postulaciones de una oferta (solo dueño) | ✅ |
| `PATCH` | `/{applicationId}/status` | Cambiar estado de postulación (solo dueño) | ✅ |

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
  "mensaje": "Email ya existe.",
  "path": "/api/v1/auth/register",
  "timestamp": "2026-06-21T12:00:00"
}
```

| Excepción | Status | Cuándo ocurre |
|-----------|--------|---------------|
| `ResourceNotFoundException` | 404 | Entidad no encontrada |
| `UnauthorizedActionException` | 403 | Acción no permitida para el usuario |
| `DuplicateEmailException` | 409 | Email ya registrado |
| `DuplicateApplicationException` | 409 | Ya te postulaste a esa oferta |
| `MethodArgumentNotValidException` | 400 | Errores de validación |
| `Exception` (general) | 500 | Error interno |

---

## Roles y permisos

| Rol | Puede hacer |
|-----|-------------|
| **CANDIDATE** | Registrarse, ver ofertas, postularse, ver sus postulaciones |
| **EMPLOYER** | Registrar empresa, crear/editar/desactivar ofertas de su empresa, revisar postulaciones, cambiar estados |
| **ADMIN** | *(en desarrollo)* |

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
curl -X POST localhost:8080/api/v1/companies \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"name":"Tech Corp","industry":"Software","location":"Remote"}'

# Listar ofertas con filtros
curl "localhost:8080/api/v1/jobs?technology=Java&modality=REMOTE&page=0&size=10"
```

---

## Licencia

MIT
