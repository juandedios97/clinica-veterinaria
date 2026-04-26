# Diagrama de Clases - Version de Entrega

Este diagrama esta simplificado para documentacion academica. Muestra las clases principales del dominio y sus relaciones mas importantes.

```mermaid
classDiagram
direction LR

class Propietario {
  +id : Long
  +nombre : String
  +apellidos : String
  +telefono : String
  +email : String
  +direccion : String
}

class Mascota {
  +id : Long
  +nombre : String
  +especie : String
  +raza : String
  +fechaNacimiento : LocalDate
  +fechaRegistro : LocalDate
}

class Veterinario {
  +id : Long
  +nombre : String
  +apellidos : String
  +especialidad : String
  +telefono : String
  +email : String
}

class Cita {
  +id : Long
  +fechaHora : LocalDateTime
  +motivo : String
  +estado : EstadoCita
}

class HistorialMedico {
  +id : Long
  +fecha : LocalDate
  +descripcion : String
  +diagnostico : String
}

class Tratamiento {
  +id : Long
  +nombre : String
  +descripcion : String
  +dosis : String
  +frecuencia : String
  +duracion : String
  +tipo : TipoTratamiento
  +estado : EstadoTratamiento
}

class Vacunacion {
  +id : Long
  +tipoVacuna : String
  +fechaAplicada : LocalDate
  +proximaDosis : LocalDate
  +observaciones : String
}

class Factura {
  +id : Long
  +numero : String
  +fechaEmision : LocalDate
  +concepto : String
  +subtotal : BigDecimal
  +total : BigDecimal
  +estado : EstadoFactura
}

class Usuario {
  +id : Long
  +nombreCompleto : String
  +username : String
  +password : String
  +rol : RolUsuario
  +activo : boolean
}

class DocumentoClinico {
  +id : Long
  +titulo : String
  +tipoDocumento : TipoDocumentoClinico
  +nombreArchivo : String
  +fechaSubida : LocalDateTime
}

class EstadoCita
class EstadoFactura
class EstadoTratamiento
class TipoTratamiento
class TipoDocumentoClinico
class RolUsuario

Propietario "1" --> "0..*" Mascota : tiene
Propietario "1" --> "0..*" Usuario : usa
Mascota "1" --> "0..*" Cita : registra
Mascota "1" --> "0..*" HistorialMedico : genera
Mascota "1" --> "0..*" Vacunacion : recibe
Mascota "1" --> "0..*" Factura : produce
Veterinario "1" --> "0..*" Cita : atiende
Veterinario "1" --> "0..*" HistorialMedico : crea
HistorialMedico "1" --> "0..*" Tratamiento : incluye
HistorialMedico "1" --> "0..*" DocumentoClinico : adjunta
Cita "0..1" --> "0..*" Factura : origina

Cita --> EstadoCita
Factura --> EstadoFactura
Tratamiento --> TipoTratamiento
Tratamiento --> EstadoTratamiento
DocumentoClinico --> TipoDocumentoClinico
Usuario --> RolUsuario
```

## Explicacion breve

- `Propietario` representa al cliente de la clinica.
- `Mascota` es la entidad central del sistema y se relaciona con citas, historial, vacunas y facturas.
- `Veterinario` participa en las consultas y en el registro del historial medico.
- `Cita` permite gestionar la agenda de la clinica.
- `HistorialMedico` almacena la informacion clinica de cada mascota.
- `Tratamiento` y `DocumentoClinico` dependen del historial medico.
- `Vacunacion` permite controlar las dosis y proximas fechas.
- `Usuario` gestiona el acceso a la aplicacion segun roles.

