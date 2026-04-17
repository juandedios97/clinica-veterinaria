# Clinica Veterinaria

Aplicacion web para la gestion de una clinica veterinaria, desarrollada con Spring Boot y Thymeleaf.

## Tecnologias

- Java 17
- Spring Boot 3.2.0
- Spring MVC
- Spring Data JPA
- Spring Security
- Thymeleaf
- MySQL 8
- Maven

## Funcionalidades

- Gestion de mascotas, propietarios y veterinarios
- Registro y administracion de citas
- Historial medico de mascotas
- Modulo de facturacion
- Control de vacunacion
- Busqueda global de entidades
- Autenticacion y roles de usuario

## Requisitos previos

- JDK 17 instalado
- Maven 3.9+
- MySQL 8 en ejecucion

## Configuracion de base de datos

La aplicacion usa por defecto la base de datos `clinica_veterinaria` en MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/clinica_veterinaria?useSSL=false&serverTimezone=Europe/Madrid&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
```

Puedes ajustar estos valores en `src/main/resources/application.properties` segun tu entorno.

## Como ejecutar el proyecto

1. Clona el repositorio:

```bash
git clone https://github.com/juandedios97/clinica-veterinaria.git
cd clinica-veterinaria
```

2. Compila el proyecto:

```bash
mvn clean install
```

3. Ejecuta la aplicacion:

```bash
mvn spring-boot:run
```

4. Abre en el navegador:

```text
http://localhost:8080
```

## Estructura del proyecto

```text
src/main/java/com/clinica/veterinaria
	|- config
	|- controller
	|- dto
	|- model
	|- repository
	|- service

src/main/resources
	|- templates
	|- static
	|- application.properties
```

## Notas

- `spring.jpa.hibernate.ddl-auto=update` esta habilitado para desarrollo.
- Se recomienda configurar credenciales seguras para produccion.
- Para entornos productivos, evita subir archivos con datos sensibles.

## Autor

Proyecto Clinica Veterinaria.
