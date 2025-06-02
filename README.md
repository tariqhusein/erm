# ERM (Employee Resource Management)

## Overview
This is an Employee Resource Management system built with Spring Boot 3.5.0. The project is designed to manage employee resources and was created as part of a Sky Interview project.

## Technologies Used
- Java 17
- Spring Boot 3.5.0
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway for database migrations
- MapStruct for object mapping
- Lombok for reducing boilerplate code
- OpenAPI/Swagger for API documentation
- Spring Actuator & Prometheus for metrics
- Docker for containerization

## Prerequisites
- Java 17 or higher
- Maven
- Docker (optional, for containerized deployment)
- PostgreSQL

## Building the Project
To build the project, run:
```bash
./mvnw clean install
```

## Running the Application
### Using Maven
```bash
./mvnw spring-boot:run
```

### Using Docker
1. Build the Docker image:
```bash
docker build -t erm .
```

2. Run the application using docker-compose:
```bash
docker-compose up
```

## API Documentation
The API documentation is generated using OpenAPI/Swagger and can be accessed at:
- `/swagger-ui.html` - Swagger UI
- `/v3/api-docs` - OpenAPI specification

## Monitoring
The application includes Spring Actuator and Prometheus integration for monitoring:
- Health check: `/actuator/health`
- Metrics: `/actuator/prometheus`

## Database Management
- The application uses PostgreSQL as the database
- Flyway is configured for database migration management
- Migration scripts are located in `src/main/resources/db/migration`

## Project Structure
```
erm/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```

