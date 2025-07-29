# API de Actividades - Documentación

## Endpoints Disponibles

### 1. Crear Actividad
**POST** `/activity/create`

**Parámetros:**
- `idUsuario` (String): ID del usuario propietario
- `descripcion` (String): Descripción de la actividad
- `lugar` (String, opcional): Lugar donde se realizará la actividad
- `fechaActividad` (long): Timestamp de la fecha programada para la actividad

**Ejemplo:**
```
POST /activity/create
Content-Type: application/x-www-form-urlencoded

idUsuario=user123&descripcion=Reunión de equipo&lugar=Sala de conferencias&fechaActividad=1706097600000
```

### 2. Actualizar Actividad
**PUT** `/activity/update/{idActividad}`

**Parámetros:**
- `idUsuario` (String): ID del usuario (debe ser el propietario)
- `descripcion` (String, opcional): Nueva descripción
- `lugar` (String, opcional): Nuevo lugar
- `estado` (String, opcional): Nuevo estado ("iniciado" o "concluido")
- `fechaActividad` (Long, opcional): Nueva fecha programada

**Ejemplo:**
```
PUT /activity/update/activity123
Content-Type: application/x-www-form-urlencoded

idUsuario=user123&descripcion=Reunión de equipo actualizada&estado=concluido
```

### 3. Obtener Actividad por ID
**GET** `/activity/get/{idActividad}`

**Ejemplo:**
```
GET /activity/get/activity123
```

### 4. Obtener Actividades por Usuario
**GET** `/activity/user/{idUsuario}`

**Ejemplo:**
```
GET /activity/user/user123
```

### 5. Obtener Actividades por Usuario y Estado
**GET** `/activity/user/{idUsuario}/status/{estado}`

**Estados válidos:** `iniciado`, `concluido`

**Ejemplo:**
```
GET /activity/user/user123/status/iniciado
```

### 6. Obtener Todas las Actividades
**GET** `/activity/getall`

**Ejemplo:**
```
GET /activity/getall
```

### 7. Marcar Actividad como Concluida
**PUT** `/activity/complete/{idActividad}`

**Parámetros:**
- `idUsuario` (String): ID del usuario (debe ser el propietario)

**Ejemplo:**
```
PUT /activity/complete/activity123
Content-Type: application/x-www-form-urlencoded

idUsuario=user123
```

### 8. Eliminar Actividad
**DELETE** `/activity/delete/{idActividad}`

**Parámetros:**
- `idUsuario` (String): ID del usuario (debe ser el propietario)

**Ejemplo:**
```
DELETE /activity/delete/activity123?idUsuario=user123
```

## Estructura de Respuesta

### Respuesta Exitosa
```json
{
    "type": "success",
    "listMessage": ["Mensaje de éxito"],
    "data": {
        "idActividad": "uuid-activity",
        "idUsuario": "uuid-user",
        "descripcion": "Descripción de la actividad",
        "lugar": "Lugar de la actividad",
        "estado": "iniciado",
        "fechaActividad": "2024-01-24T10:00:00Z",
        "fechaRegistro": "2024-01-20T10:00:00Z",
        "fechaActualizacion": "2024-01-20T10:00:00Z",
        "nombreUsuario": "Juan",
        "apellidoUsuario": "Pérez",
        "emailUsuario": "juan@email.com"
    }
}
```

### Respuesta de Error
```json
{
    "type": "error",
    "listMessage": ["Mensaje de error"],
    "data": null
}
```

## Estados de Actividad

- **iniciado**: Estado inicial de toda actividad. La actividad está pendiente de completarse.
- **concluido**: La actividad ha sido marcada como completada por el usuario o automáticamente por el sistema cuando se alcanza la fecha programada.

## Funcionalidades Automáticas

### Conclusión Automática de Actividades
El sistema ejecuta automáticamente cada hora una tarea que:
1. Busca todas las actividades con estado "iniciado" cuya fecha programada ya pasó
2. Las marca como "concluido" automáticamente
3. Actualiza la fecha de actualización

### Validaciones de Seguridad
- Solo el propietario de una actividad puede modificarla o eliminarla
- No se pueden crear actividades con fechas en el pasado
- Los estados solo pueden ser "iniciado" o "concluido"
- La descripción es obligatoria al crear una actividad

## Notas Importantes

1. **Timestamps**: Todas las fechas se manejan como timestamps en milisegundos desde epoch
2. **UUIDs**: Los IDs de actividades se generan automáticamente como UUIDs
3. **Transacciones**: Todas las operaciones de escritura son transaccionales
4. **Relación con Usuarios**: Cada actividad debe estar asociada a un usuario existente

## Base de Datos

Asegúrate de ejecutar el script SQL `create_activity_table.sql` antes de usar estos endpoints para crear la tabla necesaria en tu base de datos.
