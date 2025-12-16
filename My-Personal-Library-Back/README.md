# My Personal Library Backend

A Spring Boot backend for managing a personal library, supporting user authentication, book management, reviews, exchanges, and friendships.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Database](#database)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## Overview
This project is a backend system for a personal library application. It allows users to register, authenticate, manage their book collections, review books, exchange books with friends, and track reading statuses. The backend is built with Java, Spring Boot, and uses a relational database.

## Features
- User registration, login, and JWT-based authentication
- Book CRUD operations
- Book details and reviews
- Book exchange requests and status tracking
- Friend management (add, remove, list friends)
- User book status (reading, completed, wishlist, etc.)
- Secure endpoints with role-based access
- Exception handling and API response standardization

## Project Structure
```text
My-Personal-Library-Back/
├── src/
│   ├── main/
│   │   ├── java/com/bibliotek/personal/
│   │   │   ├── apiResponse/         # Standard API response wrappers
│   │   │   ├── config/              # Security and JWT configuration
│   │   │   ├── controller/          # REST controllers (Auth, Book, Exchange, Friendship, User)
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── entity/              # JPA entities
│   │   │   ├── exception/           # Custom exceptions
│   │   │   ├── mapper/              # Entity-DTO mappers
│   │   │   ├── repository/          # Spring Data JPA repositories
│   │   │   └── service/             # Business logic services
│   │   └── resources/
│   │       ├── application.properties # Main configuration
│   │       └── data/library.db        # Database file (if using SQLite)
│   └── test/                         # Unit and integration tests
├── pom.xml                           # Maven build file
├── Dockerfile                        # Docker containerization
└── README.md                         # Project documentation
```

## Getting Started
### Prerequisites
- Java 17+
- Maven 3.6+
- (Optional) Docker

### Build & Run
1. **Clone the repository:**
   ```bash
   git clone <repo-url>
   cd My-Personal-Library-Back
   ```
2. **Configure the database and properties** in `src/main/resources/application.properties`.
3. **Build the project:**
   ```bash
   ./mvnw clean install
   ```
4. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   Or with Docker:
   ```bash
   docker build -t my-library-backend .
   docker run -p 8080:8080 my-library-backend
   ```

## Configuration
Edit `src/main/resources/application.properties` to set up your database, JWT secret, and other environment-specific settings.

## API Endpoints
The backend exposes RESTful endpoints for all major features. Example endpoints:
- `/api/auth/register` - Register a new user
- `/api/auth/login` - Authenticate and receive JWT
- `/api/books` - CRUD operations for books
- `/api/friends` - Manage friendships
- `/api/exchanges` - Book exchange requests
- `/api/reviews` - Book reviews
- `/api/user/status` - User book status

All endpoints are secured and require JWT authentication except for registration and login.

## Database
- Uses a relational database (H2, SQLite, or other, configurable via properties)
- Entities are defined in `entity/` and repositories in `repository/`
- Example data can be placed in `src/main/resources/data/`

## Testing
Unit and integration tests are located in `src/test/java/`.
- Run tests with:
  ```bash
  ./mvnw test
  ```

## Contributing
1. Fork the repository
2. Create a new branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

## License
This project is licensed under the MIT License. See [LICENSE.md](LICENSE.md) for details. 