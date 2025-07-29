-- Script SQL para crear la tabla de actividades
-- Ejecutar este script en tu base de datos antes de usar la aplicación

CREATE TABLE actividad (
    idActividad VARCHAR(255) PRIMARY KEY,
    idUsuario VARCHAR(255) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    lugar VARCHAR(200),
    estado VARCHAR(20) NOT NULL DEFAULT 'Iniciado',
    fechaActividad TIMESTAMP NOT NULL,
    fechaRegistro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fechaActualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Clave foránea hacia la tabla usuario
    CONSTRAINT fk_actividad_usuario 
        FOREIGN KEY (idUsuario) 
        REFERENCES usuario(idUsuario) 
        ON DELETE CASCADE,
    
    -- Constraint para validar que el estado solo puede ser 'Iniciado' o 'Concluido'
    CONSTRAINT chk_estado 
        CHECK (estado IN ('Iniciado', 'Concluido'))
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_actividad_usuario ON actividad(idUsuario);
CREATE INDEX idx_actividad_estado ON actividad(estado);
CREATE INDEX idx_actividad_fecha ON actividad(fechaActividad);
CREATE INDEX idx_actividad_usuario_estado ON actividad(idUsuario, estado);

-- Comentarios sobre la tabla
ALTER TABLE actividad COMMENT = 'Tabla para almacenar las actividades de los usuarios';
ALTER TABLE actividad MODIFY COLUMN idActividad VARCHAR(255) COMMENT 'Identificador único de la actividad (UUID)';
ALTER TABLE actividad MODIFY COLUMN idUsuario VARCHAR(255) COMMENT 'Referencia al usuario propietario de la actividad';
ALTER TABLE actividad MODIFY COLUMN descripcion VARCHAR(500) COMMENT 'Descripción de la actividad';
ALTER TABLE actividad MODIFY COLUMN lugar VARCHAR(200) COMMENT 'Lugar donde se realizará la actividad (opcional)';
ALTER TABLE actividad MODIFY COLUMN estado VARCHAR(20) COMMENT 'Estado de la actividad: iniciado o concluido';
ALTER TABLE actividad MODIFY COLUMN fechaActividad TIMESTAMP COMMENT 'Fecha y hora programada para la actividad';
ALTER TABLE actividad MODIFY COLUMN fechaRegistro TIMESTAMP COMMENT 'Fecha y hora de creación del registro';
ALTER TABLE actividad MODIFY COLUMN fechaActualizacion TIMESTAMP COMMENT 'Fecha y hora de la última actualización';
