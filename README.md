# Inventory Management System

Full-stack Inventory Management System with:

- **Backend**: Java 17, Spring Boot 3 (Web, Data JPA, Validation, Swagger/OpenAPI), PostgreSQL, Maven  
- **Frontend**: Next.js + React + Material-UI  
- **Database**: PostgreSQL

### Backend Domain

`Item` entity fields:

- `id` (Long, auto-generated)
- `name` (String)
- `category` (String)
- `quantity` (Integer)
- `location` (String)
- `lowStock` (Boolean) – automatically flagged for quantity < 5

### Core REST Endpoints (backend)

Base path: `/api/items`

- `POST /api/items` — create a new item  
- `PUT /api/items/{id}` — update an existing item  
- `DELETE /api/items/{id}` — delete an item  
- `GET /api/items` — list all items  
- `GET /api/items/{id}` — get a single item by id  
- `POST /api/items/low-stock/refresh` — flag low-stock items and return them  
- `GET /api/items/low-stock` — list low-stock items  
- `GET /api/items/sorted-by-quantity?order=asc|desc` — items sorted by quantity  
- `GET /api/items/summary/quantity-per-category` — total quantity per category  
- `GET /api/items/summary/top-categories` — top 5 categories by item count

If an invalid/non-existing `id` is used for update, delete, or get, the API returns:

- HTTP status `404 Not Found`  
- JSON body with `timestamp`, `status`, `error`, `message`

---

## 1. Database Setup (PostgreSQL)

1. **Create database and user** (example):

   ```sql
   CREATE DATABASE inventory_db;
   CREATE USER inventory_user WITH ENCRYPTED PASSWORD 'inventory_password';
   GRANT ALL PRIVILEGES ON DATABASE inventory_db TO inventory_user;
   ```

2. **Configure Spring Boot datasource** in `src/main/resources/application.properties` if your connection details differ:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/inventory_db
   spring.datasource.username=inventory_user
   spring.datasource.password=inventory_password
   ```

3. **Schema + sample data**

   - Hibernate will create/update tables automatically (`spring.jpa.hibernate.ddl-auto=update`).  
   - On application startup, `src/main/resources/data.sql` will insert sample items into the `items` table.

---

## 2. Run the Backend (Spring Boot)

From the project root (`d:\inventory management system`):

```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`.

### Swagger / OpenAPI UI

Swagger is enabled via `springdoc-openapi`. After the backend is running:

- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`  
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

You can explore and test all endpoints directly in the Swagger UI.

### Testing with Postman

1. Open Postman and create a new **Collection** (e.g., `Inventory API`).  
2. Add requests such as:
   - **GET** `http://localhost:8080/api/items` – list items  
   - **POST** `http://localhost:8080/api/items` – create an item  
     **Body → raw → JSON**:

     ```json
     {
       "name": "Mouse",
       "category": "Electronics",
       "quantity": 50,
       "location": "Warehouse A"
     }
     ```

   - **PUT** `http://localhost:8080/api/items/1` – update item with ID 1  
   - **DELETE** `http://localhost:8080/api/items/1` – delete item with ID 1  
   - **GET** `http://localhost:8080/api/items/low-stock` – view low-stock items  
3. Save and re-run requests as needed while developing.

---

## 3. Run the Frontend (Next.js + MUI)

The frontend code lives under the `frontend` folder.

1. **Install dependencies**:

   ```bash
   cd frontend
   npm install
   ```

2. **Start the dev server**:

   ```bash
   npm run dev
   ```

3. Open the app in your browser:

   - `http://localhost:3000`

The frontend uses `http://localhost:8080/api/items` as the backend base URL (configured inside the Next.js pages).

### Frontend Features

- Items table showing: ID, name, category, quantity, location  
- Form to add new items  
- Edit / delete buttons per row  
- Search/filter by name and category  
- Table updates in real-time after CRUD operations by updating client-side state.

---

## 4. Quick cURL Examples (Backend)

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

