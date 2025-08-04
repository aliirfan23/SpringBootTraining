# 🌱 Spring Boot Training – Redmath


## Objectives

- Understand the fundamentals of Spring Boot
- Implement RESTful APIs
- Use Spring Data JPA for database interaction
- Apply best practices in backend development
- Explore features like:
  - Dependency injection
  - Entity-relationship mapping
  - Exception handling
  - Logging with SLF4J
  - JWT authentication 

## Tech Stack

- Java 21  
- Spring Boot  
- Maven  
- Hibernate / JPA  
- MySQL / H2  
- SLF4J  
- Lombok
# 📌 Inventory Management System

A Spring Boot-based inventory management system with dual authentication (OAuth2 & JWT) and comprehensive RESTful APIs.

## Features

- Secure item inventory management (CRUD operations)
- Stock management with inward/outward tracking
- Real-time stock reporting
- Dual authentication:
  - OAuth2 with Google
  - JWT-based local authentication
- Role-based access control (Admin, Editor, Reporter)
- H2 database with Liquibase migrations
- Comprehensive test coverage
- Docker support

## Tech Stack

- Java 21
- Spring Boot 3.x
- React (Frontend)
- Maven
- H2 Database
- JWT & OAuth2 Authentication
- Docker
- Liquibase

## Database Schema

### Items Table
- item_id (PK)
- name
- price
- quantity
- supplier
- status
- created_at
- updated_at

### Users Table
- user_id (PK)
- username (unique)
- password
- roles
- created_at

## API Endpoints

### Authentication
- `POST /login` - User login
- `GET /items/info` - Get authenticated user info

### Items Management
- `GET /items` - Get all items
- `POST /items` - Create new item
- `GET /items/{id}` - Get item by ID
- `PATCH /items/{id}` - Update item
- `DELETE /items/{id}` - Delete item
- `POST /items/{id}/inward` - Stock inward
- `POST /items/{id}/outward` - Stock outward
- `GET /items/stock-report` - Get stock report

## Running the Application

### Docker

go to the Dockerfile in the InventoryMangement module
and run the following commands:

```bash
docker build -t inventory-management .
docker run -p 8080:8080 inventory-management
