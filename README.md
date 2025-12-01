# GastroConecta 

**GastroConecta** es una plataforma integral (Web y Móvil) diseñada para compartir, calificar y descubrir recetas culinarias. El sistema implementa una arquitectura Fullstack con un Backend robusto en la nube, una Web SPA y una Aplicación Nativa Android.

---

## Integrantes
* **Vicente Núñez**
* **Eduardo Chacana**

---

##  Funcionalidades

###  Plataforma Web (React + Vite)
* **Exploración:** Buscador de recetas en tiempo real con filtros por etiquetas dietéticas.
* **Gestión de Contenido:** Creación de recetas con subida de imágenes (soporte URL/Base64).
* **Interacción:** Sistema de "Me gusta", comentarios y calificación con estrellas (Rating).
* **Perfil:** Visualización de recetas propias, guardadas, seguidores y seguidos.
* **Administración:** Panel exclusivo (`/admin`) para aprobar recetas pendientes.

###  Aplicación Móvil (Android - Jetpack Compose)
* **Cámara y Galería:** Integración nativa para subir fotos de recetas directamente desde el celular (compresión automática a Base64).
* **Sincronización:** Todo lo que haces en el celular se refleja en la Web (misma Base de Datos).
* **Interacción Nativa:** Navegación fluida entre perfiles, detalles de recetas y sistema de comentarios.
* **Seguridad:** Manejo de sesiones persistentes y tráfico seguro.

---

##  Arquitectura y Endpoints (Backend)

El Backend está construido con **Java 17 (Spring Boot)** y **MySQL**, desplegado en una instancia **AWS EC2**.

###  Base URL
`http://54.87.102.198:8080/api/v1` (IP Elástica AWS)

###  Usuarios (`/users`)
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/users` | Registro de nuevo usuario (Rol por defecto: 'user'). |
| `POST` | `/users/login` | Inicio de sesión (retorna objeto usuario). |
| `GET` | `/users/{id}` | Obtener perfil público de un usuario. |
| `POST` | `/users/{id}/seguir/{targetId}` | Seguir o dejar de seguir a otro usuario. |
| `POST` | `/users/{id}/guardar/{recipeId}` | Guardar/Quitar receta del recetario personal. |

###  Recetas (`/recetas`)
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/recetas` | Obtener todas las recetas. |
| `POST` | `/recetas` | Crear una nueva receta (Soporta img Base64 hasta 10MB). |
| `POST` | `/recetas/{id}/like` | Dar o quitar "Me gusta" (query param: `userId`). |
| `POST` | `/recetas/{id}/comentar` | Agregar un comentario a la receta. |
| `POST` | `/recetas/{id}/rate` | Calificar receta (1 a 5 estrellas). |
| `PUT` | `/recetas/{id}/confirmar` | (Admin) Aprobar una receta para que sea pública. |

---

##  Instrucciones de Ejecución

### 1. Backend (AWS EC2)
El backend ya se encuentra desplegado y activo.
* **Tecnología:** Spring Boot 3, Maven, Java 17.
* **Base de Datos:** MySQL 8 (alojada en la misma instancia EC2).
* **Configuración:** `application.properties` configurado para aceptar archivos grandes (10MB) y actualizaciones de esquema (`update`).

### 2. Frontend Web (AWS S3)
La página web es accesible públicamente.
* **Tecnología:** React, Vite, Bootstrap.
* **Despliegue:** Alojado como sitio estático en AWS S3 Bucket.
* **Ejecución Local:**
    ```bash
    cd GastroConecta
    npm install
    npm run dev
    ```

### 3. Aplicación Android
* **Tecnología:** Kotlin, Jetpack Compose, Retrofit, Coil.
* **Requisitos:** Android 7.0 (API 24) o superior.
* **Instalación:**
    1.  Abrir el proyecto en **Android Studio**.
    2.  Sincronizar Gradle (Sync Project).
    3.  Conectar dispositivo físico o Emulador.
    4.  Ejecutar `Run 'app'`.

---

##  Entregables (APK)

* **APK Firmado:** `app-release.apk` (Ubicado en la raíz del repositorio o carpeta de entrega).
* **Llave de Firma (.jks):** `keystore.jks` (Ubicado en carpeta segura/entrega, credenciales en documento adjunto).

> **Nota:** La aplicación requiere conexión a internet para funcionar.
