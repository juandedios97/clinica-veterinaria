DROP DATABASE IF EXISTS clinica_veterinaria;
CREATE DATABASE clinica_veterinaria
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE clinica_veterinaria;

CREATE TABLE propietario (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    telefono VARCHAR(9) NOT NULL,
    email VARCHAR(120) NOT NULL,
    direccion VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY uk_propietario_email (email)
);

CREATE TABLE veterinario (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    especialidad VARCHAR(100),
    telefono VARCHAR(9),
    email VARCHAR(120),
    numero_colegiado VARCHAR(50),
    anios_experiencia INT,
    fecha_incorporacion DATE,
    biografia TEXT,
    foto LONGBLOB,
    foto_content_type VARCHAR(100),
    PRIMARY KEY (id)
);

CREATE TABLE mascota (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    especie VARCHAR(50) NOT NULL,
    raza VARCHAR(100),
    fecha_nacimiento DATE,
    fecha_registro DATE,
    foto LONGBLOB,
    foto_content_type VARCHAR(100),
    propietario_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_mascota_propietario (propietario_id),
    CONSTRAINT fk_mascota_propietario
        FOREIGN KEY (propietario_id) REFERENCES propietario(id)
        ON DELETE CASCADE
);

CREATE TABLE usuario (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre_completo VARCHAR(120) NOT NULL,
    username VARCHAR(60) NOT NULL,
    password VARCHAR(120) NOT NULL,
    rol VARCHAR(30) NOT NULL,
    activo BIT(1) NOT NULL,
    propietario_id BIGINT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_username (username),
    KEY idx_usuario_propietario (propietario_id),
    CONSTRAINT fk_usuario_propietario
        FOREIGN KEY (propietario_id) REFERENCES propietario(id)
        ON DELETE SET NULL
);

CREATE TABLE cita (
    id BIGINT NOT NULL AUTO_INCREMENT,
    fecha_hora DATETIME NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    mascota_id BIGINT NOT NULL,
    veterinario_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_cita_mascota (mascota_id),
    KEY idx_cita_veterinario (veterinario_id),
    CONSTRAINT fk_cita_mascota
        FOREIGN KEY (mascota_id) REFERENCES mascota(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_cita_veterinario
        FOREIGN KEY (veterinario_id) REFERENCES veterinario(id)
);

CREATE TABLE historial_medico (
    id BIGINT NOT NULL AUTO_INCREMENT,
    fecha DATE NOT NULL,
    descripcion TEXT NOT NULL,
    diagnostico TEXT,
    mascota_id BIGINT NOT NULL,
    veterinario_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_historial_mascota (mascota_id),
    KEY idx_historial_veterinario (veterinario_id),
    CONSTRAINT fk_historial_mascota
        FOREIGN KEY (mascota_id) REFERENCES mascota(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_historial_veterinario
        FOREIGN KEY (veterinario_id) REFERENCES veterinario(id)
);

CREATE TABLE tratamiento (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    dosis VARCHAR(100),
    frecuencia VARCHAR(100),
    duracion VARCHAR(100),
    tipo VARCHAR(30) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    historial_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_tratamiento_historial (historial_id),
    CONSTRAINT fk_tratamiento_historial
        FOREIGN KEY (historial_id) REFERENCES historial_medico(id)
        ON DELETE CASCADE
);

CREATE TABLE documento_clinico (
    id BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(150) NOT NULL,
    tipo_documento VARCHAR(30) NOT NULL,
    contenido LONGBLOB NOT NULL,
    nombre_archivo VARCHAR(255),
    content_type VARCHAR(120),
    observaciones VARCHAR(255),
    fecha_subida DATETIME NOT NULL,
    historial_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_documento_historial (historial_id),
    CONSTRAINT fk_documento_historial
        FOREIGN KEY (historial_id) REFERENCES historial_medico(id)
        ON DELETE CASCADE
);

CREATE TABLE vacunacion (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tipo_vacuna VARCHAR(120) NOT NULL,
    fecha_aplicada DATE NOT NULL,
    proxima_dosis DATE,
    observaciones VARCHAR(255),
    mascota_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_vacunacion_mascota (mascota_id),
    CONSTRAINT fk_vacunacion_mascota
        FOREIGN KEY (mascota_id) REFERENCES mascota(id)
        ON DELETE CASCADE
);

CREATE TABLE factura (
    id BIGINT NOT NULL AUTO_INCREMENT,
    numero VARCHAR(30) NOT NULL,
    fecha_emision DATE NOT NULL,
    fecha_vencimiento DATE,
    concepto VARCHAR(255) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    impuesto DECIMAL(12,2) NOT NULL,
    con_iva BIT(1) NOT NULL,
    porcentaje_iva DECIMAL(5,2) NOT NULL,
    total DECIMAL(12,2) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_pago DATE,
    mascota_id BIGINT NOT NULL,
    cita_id BIGINT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_factura_numero (numero),
    KEY idx_factura_mascota (mascota_id),
    KEY idx_factura_cita (cita_id),
    CONSTRAINT fk_factura_mascota
        FOREIGN KEY (mascota_id) REFERENCES mascota(id),
    CONSTRAINT fk_factura_cita
        FOREIGN KEY (cita_id) REFERENCES cita(id)
);
