# Employee API (Spring Boot)

## Run

Requirements: Java 17+, Maven.

```bash
mvn spring-boot:run
```

Starts on `http://localhost:8080` with 15 sample employees (in-memory H2, resets on restart).

## APIs

Only **two** endpoints, both on the same URL:

| Method | URL | Does |
|---|---|---|
| GET | `/api/employees` | Get all, get by id, search, filter, pagination, sort (all in one) |
| POST | `/api/employees` | Create |

### Parameters of `GET /api/employees` (all optional, combine freely)

| Param | Example | Purpose |
|---|---|---|
| *(none)* | | Get all |
| `id` | `5` | Get by id |
| `search` | `rahim` | Search full name, email, employee code |
| `department` | `IT` | Filter (case-insensitive) |
| `status` | `ACTIVE,ON_LEAVE` | Filter, one or more of `ACTIVE`, `ON_LEAVE`, `INACTIVE` |
| `joinedFrom` | `2022-01-01` | Filter |
| `joinedTo` | `2023-12-31` | Filter |
| `page` | `0` | Pagination (starts at 0) |
| `size` | `20` | Pagination (default 20, max 100) |
| `sort` | `joinDate,desc` | `name`, `joinDate`, `department`, `employeeCode`, `status` |

The response is always the same page shape, so the frontend handles every case the same way.

---

## Get all

`GET /api/employees?page=0&size=3`

```json
{
  "content": [
    {
      "id": 7,
      "employeeCode": "EMP-007",
      "fullName": "Arif Rahman",
      "email": "arif.rahman@acme.com",
      "phone": "01710000007",
      "department": "Sales",
      "designation": "Sales Executive",
      "status": "INACTIVE",
      "joinDate": "2019-11-03"
    },
    {
      "id": 13,
      "employeeCode": "EMP-013",
      "fullName": "Farhana Yasmin",
      "email": "farhana.yasmin@acme.com",
      "phone": "01710000013",
      "department": "Marketing",
      "designation": "Content Writer",
      "status": "INACTIVE",
      "joinDate": "2021-03-22"
    },
    {
      "id": 3,
      "employeeCode": "EMP-003",
      "fullName": "Fatema Begum",
      "email": "fatema.begum@acme.com",
      "phone": "01710000003",
      "department": "HR",
      "designation": "HR Manager",
      "status": "ACTIVE",
      "joinDate": "2020-01-10"
    }
  ],
  "page": 0,
  "size": 3,
  "totalElements": 15,
  "totalPages": 5,
  "first": true,
  "last": false,
  "sort": ["name,asc"]
}
```

## Get by id

`GET /api/employees?id=5`

```json
{
  "content": [
    {
      "id": 5,
      "employeeCode": "EMP-005",
      "fullName": "Tanvir Ahmed",
      "email": "tanvir.ahmed@acme.com",
      "phone": "01710000005",
      "department": "IT",
      "designation": "QA Engineer",
      "status": "ACTIVE",
      "joinDate": "2024-03-01"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true,
  "sort": ["name,asc"]
}
```

The employee is `content[0]`. If the id doesn't exist, you get **404**.

## Search

`GET /api/employees?search=rahim`

```json
{
  "content": [
    {
      "id": 1,
      "employeeCode": "EMP-001",
      "fullName": "Rahim Uddin",
      "email": "rahim.uddin@acme.com",
      "phone": "01710000001",
      "department": "IT",
      "designation": "Software Engineer",
      "status": "ACTIVE",
      "joinDate": "2023-02-15"
    },
    {
      "id": 9,
      "employeeCode": "EMP-009",
      "fullName": "Rahima Khatun",
      "email": "rahima.khatun@acme.com",
      "phone": "01710000009",
      "department": "Finance",
      "designation": "Finance Manager",
      "status": "ACTIVE",
      "joinDate": "2018-04-25"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 2,
  "totalPages": 1,
  "first": true,
  "last": true,
  "sort": ["name,asc"]
}
```

## Filter + sort + pagination

`GET /api/employees?department=IT&status=ACTIVE&sort=joinDate,desc&size=2`

```json
{
  "content": [
    {
      "id": 12,
      "employeeCode": "EMP-012",
      "fullName": "Jahid Hasan",
      "email": "jahid.hasan@acme.com",
      "phone": "01710000012",
      "department": "IT",
      "designation": "Junior Developer",
      "status": "ACTIVE",
      "joinDate": "2025-01-05"
    },
    {
      "id": 5,
      "employeeCode": "EMP-005",
      "fullName": "Tanvir Ahmed",
      "email": "tanvir.ahmed@acme.com",
      "phone": "01710000005",
      "department": "IT",
      "designation": "QA Engineer",
      "status": "ACTIVE",
      "joinDate": "2024-03-01"
    }
  ],
  "page": 0,
  "size": 2,
  "totalElements": 5,
  "totalPages": 3,
  "first": true,
  "last": false,
  "sort": ["joinDate,desc"]
}
```

## Create

`POST /api/employees` with header `Content-Type: application/json`

```json
{
  "employeeCode": "EMP-016",
  "firstName": "Nabila",
  "lastName": "Chowdhury",
  "email": "nabila.chowdhury@acme.com",
  "phone": "01812345678",
  "department": "IT",
  "designation": "Backend Developer",
  "joinDate": "2026-11-01",
  "salary": 75000
}
```

Response **201 Created** (header `Location: http://localhost:8080/api/employees?id=16`):

```json
{
  "id": 16,
  "employeeCode": "EMP-016",
  "fullName": "Nabila Chowdhury",
  "email": "nabila.chowdhury@acme.com",
  "phone": "01812345678",
  "department": "IT",
  "designation": "Backend Developer",
  "status": "ACTIVE",
  "joinDate": "2026-11-01"
}
```

`status` defaults to `ACTIVE`. `salary` is saved but never returned.

---

## Errors

All errors use the same shape:

```json
{
  "timestamp": "2026-11-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "...",
  "path": "/api/employees",
  "errors": { "field": "reason" }
}
```

| Case | Request | Status | Message |
|---|---|---|---|
| Id not found | `GET /api/employees?id=999` | 404 | `Employee 999 not found` |
| Bad id | `GET /api/employees?id=abc` | 400 | `errors.id: Invalid value 'abc'` |
| Bad sort field | `GET /api/employees?sort=salary,desc` | 400 | `Sorting by 'salary' is not allowed...` |
| Bad status | `GET /api/employees?status=RETIRED` | 400 | `errors.status: Invalid value 'RETIRED'` |
| Wrong date range | `GET /api/employees?joinedFrom=2025-01-01&joinedTo=2024-01-01` | 400 | `errors.dateRangeValid: joinedFrom must be on or before joinedTo` |
| Create: invalid body | `POST /api/employees` (missing/invalid fields) | 400 | `Validation failed` + `errors` per field |
| Create: duplicate | `POST /api/employees` (existing email or code) | 409 | `An employee with email '...' already exists` |

## Postman

Import `Employee-API.postman_collection.json` from this folder.
