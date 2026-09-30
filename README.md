# MVNO Plan Management

Spring Boot 3.2, Java 21, PostgreSQL service for managing MVNO plans.

## Run with Docker Compose

Set `POSTGRES_DB`, `POSTGRES_USER`, and `POSTGRES_PASSWORD` in `.env`, then start the stack:

```sh
docker compose up --build
```

Flyway creates the schema and inserts the starter data. The API is available under `/api/v1`; Swagger UI is at `/api/v1/swagger-ui.html`.

## Plan API

All successful operations return HTTP 200 with an `ApiResponse` body (`code: 1000`).

| Method | Path | Purpose |
|---|---|---|
| `POST` | `/api/v1/plans` | Create a plan |
| `GET` | `/api/v1/plans` | Search and page through plans |
| `GET` | `/api/v1/plans/{planId}` | Get plan details |
| `PUT` | `/api/v1/plans/{planId}` | Replace plan fields and child rows |
| `PATCH` | `/api/v1/plans/{planId}/status` | Activate or deactivate a plan |
| `DELETE` | `/api/v1/plans/{planId}` | Delete a plan unless a subscription uses it |

List filters: `isActive`, `keyword`, `durationMonths`, `page`, `size`, and allowlisted `sort` (for example `createdAt,desc`).

MVNO Plan Management (Mock VNSKY) - ERD
![Mô tả ảnh](./vnsky.png)