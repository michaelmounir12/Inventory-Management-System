# Inventory Management System - Spring Boot Backend

Simple Java Spring Boot backend for an Inventory Management System with PostgreSQL and CRUD REST APIs for `Item`.

## Tech Stack

- Java 17
- Spring Boot 3 (Web, Data JPA, Validation)
- PostgreSQL
- Maven

## Domain

`Item` entity fields:

- `id` (Long, auto-generated)
- `name` (String)
- `category` (String)
- `quantity` (Integer)
- `location` (String)

## REST Endpoints

Base path: `/api/items`

- `POST /api/items` — create a new item
- `PUT /api/items/{id}` — update an existing item
- `DELETE /api/items/{id}` — delete an item
- `GET /api/items` — list all items
- `GET /api/items/{id}` — get a single item by id

If an invalid/non-existing `id` is used for update, delete, or get, the API returns:

- HTTP status `404 Not Found`
- JSON body with `timestamp`, `status`, `error`, `message`

## Database Setup (PostgreSQL)

Create a database and user (example):

```sql
CREATE DATABASE inventory_db;
CREATE USER inventory_user WITH ENCRYPTED PASSWORD 'inventory_password';
GRANT ALL PRIVILEGES ON DATABASE inventory_db TO inventory_user;
```

Update `src/main/resources/application.properties` if your connection details differ:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/inventory_db
spring.datasource.username=inventory_user
spring.datasource.password=inventory_password
```

## Sample Data

On application startup, `src/main/resources/data.sql` will insert sample items into the `items` table.

## Run the Application

From the project root:

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

## Quick cURL Examples

Create item:

```bash
curl -X POST http://localhost:8080/api/items ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Mouse\",\"category\":\"Electronics\",\"quantity\":50,\"location\":\"Warehouse A\"}"
```

List items:

```bash
curl http://localhost:8080/api/items
```

