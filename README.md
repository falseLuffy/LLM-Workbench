# LLM Workbench Service

A modernized Java backend service built with Spring Boot 2.7.x, following the architecture optimization plan.

## Technology Stack
- **Framework**: Spring Boot 2.7.18
- **Language**: Java 8
- **Persistence**: MyBatis Plus 3.5.x
- **Database**: MySQL 
- **Security**: Spring Security + JWT (Skeleton)
- **API Documentation**: SpringDoc / Swagger 3
- **Build Tool**: Maven

## Getting Started

### Prerequisites
- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+

### Configuration
Update `src/main/resources/application.yml` with your database credentials:
- `DB_HOST`: Database host
- `DB_NAME`: Database name
- `DB_USER`: Database username
- `DB_PASS`: Database password

### Running the application
```bash
mvn spring-boot:run
```

### API Documentation
Once the application is running, you can access the Swagger UI at:
`http://localhost:8080/swagger-ui.html`

## Project Structure
- `com.example.llm.common`: Common utilities and response models.
- `com.example.llm.config`: Configuration classes (Security, MyBatis Plus).
- `com.example.llm.controller`: REST API controllers.
- `com.example.llm.entity`: Database entities.
- `com.example.llm.mapper`: MyBatis mapper interfaces.
- `com.example.llm.service`: Business logic services.
