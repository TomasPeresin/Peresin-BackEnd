# 🚀 Peresín BackEnd API — Portfolio & CMS Engine

[![Java](https://img.shields.io/badge/Java-11%20%2F%208-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.6.7-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Hibernate](https://img.shields.io/badge/Hibernate-ORM-59666C?logo=hibernate&logoColor=white)](https://hibernate.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![Render](https://img.shields.io/badge/Render-Deployed-46E3B7?logo=render&logoColor=white)](https://backendpti.onrender.com)

API RESTful robusta y administrable desarrollada en **Java y Spring Boot** para la gestión dinámica de contenidos del portfolio personal de **Tomás Ignacio Peresín** (perfil profesional, proyectos, experiencia laboral, formación académica, habilidades técnicas y gestión de archivos multimedia).

---

## 📑 Tabla de Contenidos
- [Arquitectura y Características](#-arquitectura-y-características)
- [Stack Tecnológico](#-stack-tecnológico)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Endpoints de la API](#-endpoints-de-la-api)
- [Seguridad y Autenticación](#-seguridad-y-autenticación)
- [Configuración y Variables de Entorno](#-configuración-y-variables-de-entorno)
- [Puesta en Marcha Local](#-puesta-en-marcha-local)
- [Despliegue](#-despliegue)

---

## 🏛️ Arquitectura y Características

- **Arquitectura en Capas:** Controladores REST (`Controller`), Servicios Transaccionales (`Service`), Interfaces y Repositorios (`Repository`), Entidades JPA (`Entity`) y Objetos de Transferencia de Datos (`Dto`).
- **Seguridad Stateless JWT:** Interceptores de autenticación y autorización mediante JSON Web Tokens (`jjwt:0.9.1`), contraseñas hasheadas con BCrypt y control de acceso basado en roles (`ROLE_ADMIN`, `ROLE_USER`).
- **Almacenamiento y Media:** Servicio de almacenamiento de archivos (`FileStorageService`) con sanitización de nombres, validación de tipos MIME (JPEG, PNG, WebP, GIF, SVG) y subida asociada a proyectos.
- **Tolerancia a Cold Start:** Endpoints optimizados para arranque rápido e integración fluida con clientes SPA.

---

## 🛠️ Stack Tecnológico

| Capa / Módulo | Tecnología | Descripción |
|---|---|---|
| **Lenguaje** | Java 11 / Java 8 | OpenJDK Amazon Corretto |
| **Framework** | Spring Boot `2.6.7` | Framework backend principal |
| **Seguridad** | Spring Security + JJWT `0.9.1` | Autenticación stateless y roles |
| **Persistencia** | Spring Data JPA + Hibernate | Mapeo objeto-relacional (ORM) |
| **Base de Datos** | MySQL 8 | Clever Cloud / Docker local |
| **Herramientas** | Lombok, Commons Lang3, Validation | Reducción de boilerplate y validación |
| **Build Tool** | Apache Maven (`mvnw`) | Gestor de dependencias del proyecto |
| **Contenedores** | Docker & Docker Compose | Contenerización y base de datos local |

---

## 📁 Estructura del Proyecto

```
src/main/java/com/portfolio/pti/
├── Controller/               # Controladores REST expuestos
│   ├── CEducacion.java       # CRUD de Educación
│   ├── CExperiencia.java     # CRUD de Experiencia laboral
│   ├── CHys.java             # CRUD de Habilidades
│   ├── CPersona.java         # Gestión del perfil principal
│   ├── CProyecto.java        # CRUD de Proyectos y upload de portadas
│   └── FileStorageController # Subida y descarga de archivos
├── Dto/                      # Data Transfer Objects para requests
├── Entity/                   # Entidades JPA (Persona, Educacion, Experiencia, etc.)
├── Interface/                # Interfaces de servicios
├── Repository/               # Interfaces JpaRepository para MySQL
├── Security/                 # Módulo de seguridad Spring Security
│   ├── Controller/           # AuthController (/auth/login, /auth/nuevo, etc.)
│   ├── Dto/                  # DTOs de login, registro, JWT
│   ├── Entity/               # Entidades Usuario, Rol, RolNombre
│   ├── Enums/                # Enum RolNombre (ROLE_ADMIN, ROLE_USER)
│   ├── jwt/                  # JwtProvider, JwtEntryPoint, JwtTokenFilter
│   └── Service/              # RolService, UsuarioService, UserDetailsImpl
└── Service/                  # Lógica de negocio transaccional (@Service)
```

---

## 🔌 Endpoints de la API

### Autenticación (`/auth`)
| Método | Endpoint | Descripción | Seguridad |
|---|---|---|---|
| `POST` | `/auth/nuevo` | Registro de nuevo usuario | Público |
| `POST` | `/auth/login` | Inicio de sesión y generación de JWT | Público |

### Perfil Principal (`/personas`)
| Método | Endpoint | Descripción | Seguridad |
|---|---|---|---|
| `GET` | `/personas/traer/perfil` | Obtiene el perfil principal (ID: 1) | Público |
| `GET` | `/personas/traer` | Listado general de personas | Público |
| `POST` | `/personas/crear` | Crea un nuevo perfil | `ROLE_ADMIN` |
| `PUT` | `/personas/editar/{id}` | Actualiza datos del perfil | `ROLE_ADMIN` |
| `DELETE`| `/personas/borrar/{id}` | Elimina el perfil por ID | `ROLE_ADMIN` |

### Proyectos (`/proyecto`)
| Método | Endpoint | Descripción | Seguridad |
|---|---|---|---|
| `GET` | `/proyecto/lista` | Lista todos los proyectos | Público |
| `GET` | `/proyecto/detail/{id}` | Detalle de un proyecto por ID | Público |
| `POST` | `/proyecto/create` | Crea un proyecto | `ROLE_ADMIN` |
| `PUT` | `/proyecto/update/{id}` | Actualiza un proyecto | `ROLE_ADMIN` |
| `DELETE`| `/proyecto/delete/{id}` | Elimina un proyecto | `ROLE_ADMIN` |
| `POST` | `/proyecto/{id}/upload-img` | Sube y asocia imagen al proyecto | `ROLE_ADMIN` |

### Experiencia Laboral (`/explab`)
| Método | Endpoint | Descripción | Seguridad |
|---|---|---|---|
| `GET` | `/explab/lista` | Lista experiencias laborales | Público |
| `GET` | `/explab/detail/{id}` | Detalle de experiencia por ID | Público |
| `POST` | `/explab/create` | Crea nueva experiencia | `ROLE_ADMIN` |
| `PUT` | `/explab/update/{id}` | Modifica experiencia | `ROLE_ADMIN` |
| `DELETE`| `/explab/delete/{id}` | Elimina experiencia | `ROLE_ADMIN` |

### Educación y Formación (`/educacion`)
| Método | Endpoint | Descripción | Seguridad |
|---|---|---|---|
| `GET` | `/educacion/lista` | Lista antecedentes académicos | Público |
| `GET` | `/educacion/detail/{id}`| Detalle académico por ID | Público |
| `POST` | `/educacion/create` | Crea registro educativo | `ROLE_ADMIN` |
| `PUT` | `/educacion/update/{id}`| Modifica registro educativo | `ROLE_ADMIN` |
| `DELETE`| `/educacion/delete/{id}`| Elimina registro educativo | `ROLE_ADMIN` |

### Habilidades Técnicas (`/skill`)
| Método | Endpoint | Descripción | Seguridad |
|---|---|---|---|
| `GET` | `/skill/lista` | Lista habilidades registradas | Público |
| `GET` | `/skill/detail/{id}` | Detalle de habilidad por ID | Público |
| `POST` | `/skill/create` | Crea nueva habilidad | `ROLE_ADMIN` |
| `PUT` | `/skill/update/{id}` | Modifica habilidad | `ROLE_ADMIN` |
| `DELETE`| `/skill/delete/{id}` | Elimina habilidad | `ROLE_ADMIN` |

### Archivos y Multimedia (`/files` y `/uploads`)
| Método | Endpoint | Descripción | Seguridad |
|---|---|---|---|
| `POST` | `/files/upload` | Carga de imagen con validación MIME | `ROLE_ADMIN` |
| `GET` | `/files/{filename}` | Descarga / visualización de archivo | Público |
| `DELETE`| `/files/{filename}` | Elimina archivo físico del disco | `ROLE_ADMIN` |
| `GET` | `/uploads/{filename}` | Acceso a recurso estático | Público |

---

## 🔒 Seguridad y Autenticación

1. **Header de Autorización:** Las peticiones protegidas deben incluir la cabecera:
   ```http
   Authorization: Bearer <tu_token_jwt>
   ```
2. **Peticiones Públicas:** Las peticiones públicas no requieren token y el filtro `JwtTokenFilter` las procesa sin exigir credenciales.
3. **CORS:** Configurado para admitir orígenes autorizados (`https://frontendpti.web.app`, `http://localhost:4200`, `http://localhost:3000`).

---

## ⚙️ Configuración y Variables de Entorno

El archivo `src/main/resources/application.properties` se parametriza mediante variables de entorno:

| Variable | Descripción | Ejemplo Local |
|---|---|---|
| `DB_URL` | URL JDBC de conexión a MySQL | `jdbc:mysql://localhost:3306/backenpti?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` |
| `DB_USER` | Usuario de la base de datos | `root` |
| `DB_PASSWORD` | Contraseña de la base de datos | `root` |
| `JWT_SECRET` | Clave secreta para firma HMAC | `miClaveSecretaSuperSegura` |
| `PORT` *(Opcional)* | Puerto de escucha HTTP | `8080` (default) |

---

## 💻 Puesta en Marcha Local

### Prerrequisitos
- JDK 11 o JDK 8 instalado y configurado en `JAVA_HOME`.
- Docker y Docker Compose (opcional, para levantar MySQL fácilmente).

### 1. Iniciar Base de Datos con Docker
```bash
docker-compose up -d
```
*Esto iniciará un contenedor MySQL 8 en el puerto `3306` con la base de datos `backenpti`.*

### 2. Configurar Variables de Entorno y Ejecutar

**En Windows (PowerShell):**
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/backenpti?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USER="root"
$env:DB_PASSWORD="root"
$env:JWT_SECRET="secret"

.\mvnw.cmd spring-boot:run
```

**En Linux / macOS (Bash):**
```bash
export DB_URL="jdbc:mysql://localhost:3306/backenpti?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DB_USER="root"
export DB_PASSWORD="root"
export JWT_SECRET="secret"

./mvnw spring-boot:run
```

---

## 🌐 Despliegue en Producción

- **Hosting Backend:** [Render](https://backendpti.onrender.com)
- **Base de Datos Remota:** MySQL 8 en Clever Cloud.
- **Docker Build:**
  ```bash
  docker build -t peresin-backend .
  docker run -p 8080:8080 -e DB_URL=... -e DB_USER=... -e DB_PASSWORD=... peresin-backend
  ```

---

## 👤 Autor
- **Tomás Ignacio Peresín**
- **GitHub:** [@TomasPeresin](https://github.com/TomasPeresin)
- **LinkedIn:** [Tomás Peresín](https://www.linkedin.com/in/tomas-peresin-364588213/)