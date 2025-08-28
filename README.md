# Aplicación Móvil Meteorológica 'PachaApp'


##  Descripción del Proyecto

**PachaApp** es una aplicación móvil desarrollada con Android Studio (Java) como frontend y Spring Boot (Java) como backend, ofreciendo un sistema completo de gestión de actividades con información meteorológica contextual.

## Tecnologías Utilizadas

#### Frontend (Android)
- **Lenguaje**: Java 11
- **IDE**: Android Studio 2024.1.1
- **SDK Mínimo**: API 24 (Android 7.0)
- **SDK Target**: API 35 (Android 15)
- **Arquitectura**: MVC (Model-View-Controller)

#### Backend (Spring Boot)
- **Lenguaje**: Java 21
- **Framework**: Spring Boot 3.5.3
- **Base de Datos**: MySQL/MariaDB
- **ORM**: Spring Data JPA
- **Seguridad**: Spring Security
- **Build Tool**: Maven

#### APIs y Servicios Externos
- **OpenWeatherMap API**: Datos meteorológicos en tiempo real
- **Firebase Authentication**: Autenticación de usuarios
- **Firebase Cloud Messaging**: Notificaciones push
- **Google Play Services**: Servicios de geolocalización
- **Supabase Storage**: Almacenamiento de archivos

### Funcionalidades Principales

#### Gestión de Actividades
- **Crear Actividades**: Formulario completo con descripción, lugar, fecha y hora
- **Listar Actividades**: Vista organizada por fecha con estado visual
- **Editar Actividades**: Modificación de todos los campos excepto estado concluido
- **Eliminar Actividades**: Eliminación con confirmación
- **Marcar como Completado**: Cambio de estado de actividades
- **Filtros por Estado**: Visualización de actividades iniciadas/concluidas

#### Información Meteorológica
- **Clima Actual**: Datos meteorológicos en tiempo real por geolocalización
- **Búsqueda de Ciudades**: Consulta de clima por nombre de ciudad
- **Integración en Actividades**: Información meteorológica contextual para cada actividad
- **Pronóstico Extendido**: Datos meteorológicos detallados
- **Historial de Búsquedas**: Registro de consultas meteorológicas

#### Sistema de Notificaciones
- **Notificaciones Automáticas**: Recordatorios cada 15 minutos para actividades próximas
- **Notificaciones Diarias**: Recordatorios a las 8 PM para actividades del día siguiente
- **Notificaciones de Creación**: Confirmación al crear nuevas actividades
- **Gestión de Tokens FCM**: Sincronización automática con el servidor
- **Notificaciones Personalizadas**: Sistema configurable de alertas

#### Autenticación y Usuarios
- **Registro con Firebase**: Creación de cuentas mediante Google
- **Inicio de Sesión**: Autenticación segura con Firebase
- **Gestión de Perfil**: Configuración de datos personales
- **Persistencia de Sesión**: Mantenimiento de sesión activa
- **Cierre de Sesión**: Limpieza segura de datos locales