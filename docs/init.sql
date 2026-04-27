USE clinica_veterinaria;

-- Credenciales de prueba:
-- admin / admin123
-- recep / recep123
-- vet / vet123
-- maria.cliente / cliente123

INSERT INTO propietario (id, nombre, apellidos, telefono, email, direccion) VALUES
(1, 'Maria', 'Garcia Lopez', '612345678', 'maria.cliente@example.com', 'Calle Mayor 12, Madrid'),
(2, 'Carlos', 'Sanchez Ruiz', '623456789', 'carlos@email.com', 'Avenida Europa 44, Madrid'),
(3, 'Lucia', 'Martinez Perez', '634567890', 'lucia@email.com', 'Paseo del Prado 20, Madrid');

INSERT INTO veterinario (id, nombre, apellidos, especialidad, telefono, email, numero_colegiado, anios_experiencia, fecha_incorporacion, biografia) VALUES
(1, 'Ana', 'Fernandez Torres', 'Cirugia', '910000001', 'ana.vet@clinica.com', 'COL-001', 9, '2021-02-15', 'Veterinaria especializada en cirugia de pequenos animales.'),
(2, 'Pedro', 'Gomez Ruiz', 'Medicina interna', '910000002', 'pedro.vet@clinica.com', 'COL-002', 6, '2022-09-01', 'Veterinario centrado en medicina preventiva y diagnostico clinico.');

INSERT INTO mascota (id, nombre, especie, raza, fecha_nacimiento, fecha_registro, propietario_id, foto, foto_content_type) VALUES
(1, 'Luna', 'Perro', 'Labrador', '2021-05-10', '2026-04-08', 1, NULL, NULL),
(2, 'Milo', 'Gato', 'Comun europeo', '2022-03-22', '2026-04-08', 1, NULL, NULL),
(3, 'Thor', 'Perro', 'Doberman', '2020-11-01', '2026-04-09', 2, NULL, NULL),
(4, 'Nala', 'Gato', 'Siames', '2023-01-18', '2026-04-09', 3, NULL, NULL);

INSERT INTO usuario (id, nombre_completo, username, password, rol, activo, propietario_id) VALUES
(1, 'Administrador Principal', 'admin', '$2a$10$V/iMFML3.uJPTI.tueRqa.h1JTEalHVZIwVgqgKRTYU8MYb.NiAIK', 'ADMIN', b'1', NULL),
(2, 'Recepcion Test', 'recep', '$2a$10$lwatWBeFnvN7dcYktjEnr.UDLkMIBHqjF/zY0sCmKLo3JUx4dAunO', 'RECEPCIONISTA', b'1', NULL),
(3, 'Vet Test', 'vet', '$2a$10$BQ.dPNEFtU/WDTcgKVyu9eha.Ab28bRaOPhLe5As.kaOtUClT5fNO', 'VETERINARIO', b'1', NULL),
(4, 'Maria Garcia Lopez', 'maria.cliente', '$2a$10$w4VEZ8yazrMwZsNGqoJ2Zu3P5MhgCR3Pfmd23pBkRZ2TF29POVhNi', 'CLIENTE', b'1', 1);

INSERT INTO cita (id, fecha_hora, motivo, estado, mascota_id, veterinario_id) VALUES
(1, '2026-04-28 10:00:00', 'Revision general anual', 'PENDIENTE', 1, 2),
(2, '2026-04-29 16:30:00', 'Vacunacion de refuerzo', 'PENDIENTE', 2, 2),
(3, '2026-04-25 12:15:00', 'Dolor en la pata trasera', 'COMPLETADA', 3, 1);

INSERT INTO historial_medico (id, fecha, descripcion, diagnostico, mascota_id, veterinario_id) VALUES
(1, '2026-04-25', 'Exploracion por cojera y dolor al apoyar la pata trasera derecha.', 'Inflamacion leve de tejidos blandos.', 3, 1),
(2, '2026-04-20', 'Revision preventiva y control de peso.', 'Paciente estable, sin alteraciones relevantes.', 1, 2);

INSERT INTO tratamiento (id, nombre, descripcion, dosis, frecuencia, duracion, tipo, estado, historial_id) VALUES
(1, 'Antiinflamatorio', 'Tratamiento para reducir la inflamacion de la extremidad.', '1 comprimido', 'Cada 12 horas', '7 dias', 'MEDICACION', 'ACTIVO', 1),
(2, 'Control de peso', 'Seguimiento nutricional con revision mensual.', 'Plan nutricional', 'Diario', '30 dias', 'SEGUIMIENTO', 'ACTIVO', 2);

INSERT INTO documento_clinico (id, titulo, tipo_documento, contenido, nombre_archivo, content_type, observaciones, fecha_subida, historial_id) VALUES
(1, 'Informe revision Luna', 'INFORME', 0x255044462D312E340A25E2E3CFD30A, 'informe-luna.pdf', 'application/pdf', 'Documento de ejemplo para la memoria.', '2026-04-20 11:00:00', 2);

INSERT INTO vacunacion (id, tipo_vacuna, fecha_aplicada, proxima_dosis, observaciones, mascota_id) VALUES
(1, 'Rabia', '2026-04-20', '2027-04-20', 'Sin reacciones adversas.', 1),
(2, 'Trivalente felina', '2026-04-18', '2027-04-18', 'Vacunacion anual completada.', 2);

INSERT INTO factura (id, numero, fecha_emision, fecha_vencimiento, concepto, subtotal, impuesto, con_iva, porcentaje_iva, total, estado, fecha_pago, mascota_id, cita_id) VALUES
(1, 'FAC-00001', '2026-04-25', '2026-05-05', 'Consulta de traumatologia', 35.00, 7.35, b'1', 21.00, 42.35, 'PAGADA', '2026-04-25', 3, 3),
(2, 'FAC-00002', '2026-04-20', '2026-04-30', 'Revision preventiva', 28.00, 5.88, b'1', 21.00, 33.88, 'PENDIENTE', NULL, 1, NULL);
