## Part 3: Data model and API conventions

### Flyway V1 (Phase 1 tables)

```sql
CREATE TABLE app_user (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  email VARCHAR(255) UNIQUE,
  phone VARCHAR(20) NOT NULL UNIQUE,
  password_hash VARCHAR(100),
  role VARCHAR(20) NOT NULL,                 -- ADMIN, OWNER, STAFF, CUSTOMER
  status VARCHAR(20) NOT NULL,               -- INVITED, ACTIVE, SUSPENDED
  owner_verification_status VARCHAR(20),     -- PENDING, APPROVED, REJECTED (OWNER only)
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE refresh_token (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES app_user(id),
  token_hash VARCHAR(100) NOT NULL UNIQUE,
  expires_at TIMESTAMPTZ NOT NULL,
  revoked BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE otp_request (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  phone VARCHAR(20) NOT NULL,
  otp_hash VARCHAR(100) NOT NULL,
  purpose VARCHAR(30) NOT NULL,              -- CLAIM_ACCOUNT
  expires_at TIMESTAMPTZ NOT NULL,
  attempts INT NOT NULL DEFAULT 0,
  consumed BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE property (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  owner_id BIGINT NOT NULL REFERENCES app_user(id),
  name VARCHAR(150) NOT NULL,
  type VARCHAR(10) NOT NULL,                 -- PG, FLAT
  city VARCHAR(100) NOT NULL,
  address VARCHAR(500) NOT NULL,
  description VARCHAR(2000),
  payment_upi_id VARCHAR(100),
  deleted_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE property_member (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  property_id BIGINT NOT NULL REFERENCES property(id),
  user_id BIGINT NOT NULL REFERENCES app_user(id),
  staff_role VARCHAR(20) NOT NULL,           -- MANAGER, CARETAKER
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (property_id, user_id)
);

CREATE TABLE room (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  property_id BIGINT NOT NULL REFERENCES property(id),
  room_no VARCHAR(30) NOT NULL,
  sharing_type INT NOT NULL,                 -- 1,2,3...
  rent NUMERIC(10,2) NOT NULL,
  deleted_at TIMESTAMPTZ,
  UNIQUE (property_id, room_no)
);

CREATE TABLE bed (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  room_id BIGINT NOT NULL REFERENCES room(id),
  bed_no VARCHAR(30) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',  -- AVAILABLE, OCCUPIED, MAINTENANCE
  deleted_at TIMESTAMPTZ,
  UNIQUE (room_id, bed_no)
);

CREATE TABLE tenancy (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  tenant_id BIGINT NOT NULL REFERENCES app_user(id),
  bed_id BIGINT NOT NULL REFERENCES bed(id),
  booking_id BIGINT,                         -- FK added in Phase 2
  start_date DATE NOT NULL,
  end_date DATE,
  deposit NUMERIC(10,2) NOT NULL DEFAULT 0,
  monthly_rent NUMERIC(10,2) NOT NULL,
  status VARCHAR(20) NOT NULL,               -- ACTIVE, ENDED
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX one_active_tenancy_per_bed ON tenancy (bed_id) WHERE status = 'ACTIVE';
CREATE INDEX idx_tenancy_tenant ON tenancy (tenant_id);
CREATE INDEX idx_room_property ON room (property_id);
CREATE INDEX idx_bed_room ON bed (room_id);
CREATE INDEX idx_member_user ON property_member (user_id);
```

### Later tables (separate migrations, fields only)
- `booking_request`: id, customer_id, bed_id, move_in_date, status (PENDING/APPROVED/REJECTED/CANCELLED), created_at
- `rent_payment`: id, tenancy_id, due_month (date, first of month), amount, status (PENDING/MARKED_PAID/CONFIRMED), reference_no, paid_at, confirmed_by
- `complaint`: id, tenancy_id, category, description, status (OPEN/IN_PROGRESS/RESOLVED), created_at
- `vacate_request`: id, tenancy_id, requested_date, status (PENDING/APPROVED/REJECTED)
- `notice`: id, property_id, created_by, title, body, created_at

### API conventions
- Base path `/api/v1`. Auth header `Authorization: Bearer <jwt>`.
- JWT carries only `userId` and global `role`. Property memberships are **not** in the token; they are checked from the DB on every request.
- Create returns `201` with the resource. Soft delete (`deleted_at`) for property, room, bed.
- Pagination: `?page=0&size=20&sort=field,asc`.
- Standard error body: `{ "timestamp", "status", "code", "message", "fieldErrors": [{ "field", "message" }] }`.

### Phase 1 endpoints

| Method | Endpoint | Who |
|---|---|---|
| POST | `/auth/register` (role OWNER or CUSTOMER) | Public |
| POST | `/auth/login` | Public |
| POST | `/auth/refresh` | Public |
| POST | `/auth/logout` | Logged in |
| POST | `/auth/claim/request` (phone) | Public |
| POST | `/auth/claim/verify` (phone, otp, newPassword) | Public |
| GET, PUT | `/users/me` | Logged in |
| PUT | `/users/me/password` | Logged in |
| GET | `/admin/owners?status=PENDING` | Admin |
| PATCH | `/admin/owners/{userId}/status` | Admin |
| POST | `/properties` | Approved owner |
| GET | `/properties` (mine: owned or assigned) | Owner, Staff |
| GET | `/properties/{pid}` | Owner, Staff of it |
| PUT | `/properties/{pid}` | Owner, Manager |
| DELETE | `/properties/{pid}` (soft; reject if ACTIVE tenancy) | Owner |
| POST, GET | `/properties/{pid}/members` | Owner (GET also Manager) |
| PUT, DELETE | `/properties/{pid}/members/{memberId}` | Owner |
| POST, GET | `/properties/{pid}/rooms` | Owner, Manager (GET also Caretaker) |
| GET, PUT, DELETE | `/properties/{pid}/rooms/{rid}` | Owner, Manager |
| POST, GET | `/properties/{pid}/rooms/{rid}/beds` | Owner, Manager (GET also Caretaker) |
| PUT, DELETE | `/properties/{pid}/beds/{bid}` | Owner, Manager |
| PATCH | `/properties/{pid}/beds/{bid}/status` (AVAILABLE or MAINTENANCE only) | Owner, Manager |
| POST | `/properties/{pid}/tenancies` | Owner, Manager |
| GET | `/properties/{pid}/tenancies?status=ACTIVE` | Owner, Staff |
| GET | `/properties/{pid}/tenancies/{tid}` | Owner, Staff |
| PATCH | `/properties/{pid}/tenancies/{tid}/end` | Owner, Manager |
| GET | `/tenancies/me` | Customer/Tenant |

**Phase 2:** `GET /listings` (public search), `POST /bookings`, approve/reject, `GET /bookings/me`.
**Phase 3:** payments, complaints, notices, vacate requests, rest of admin panel, Docker deployment.

---
