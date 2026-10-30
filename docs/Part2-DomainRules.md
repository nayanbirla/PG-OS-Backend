## Part 2: Domain rules (decisions already made)

**Roles (global, on `app_user.role`):** `ADMIN`, `OWNER`, `STAFF`, `CUSTOMER`.
There is no separate TENANT role. A tenant is a `CUSTOMER` who has an `ACTIVE` tenancy. Customer to tenant is a status change, not a new account.

**Staff:** a user with role `STAFF` is linked to properties through `property_member` with `staff_role` of `MANAGER` or `CARETAKER`. Access is per property.

**Ownership:** one owner can own many properties. One property can have many staff members.

**User status:** `INVITED` (created by an owner for a walk-in tenant, no password, cannot log in) → `ACTIVE` (claimed via OTP) → `SUSPENDED`.
Owners also have `owner_verification_status`: `PENDING` → `APPROVED` / `REJECTED`. An owner cannot create properties until `APPROVED`.

**Occupancy:** one bed has at most one tenant at a time. A room can have many beds. A bed's history can have many tenancies, but only one `ACTIVE` at once (enforced by a partial unique index). A bed becomes `OCCUPIED` when a tenancy is created and `AVAILABLE` when it ends. `OCCUPIED` is never set manually.

**Phone:** unique, required, stored in E.164 (`+919876543210`). It is the identity for INVITED users and for claim.

**Booking (Phase 2):** customer sends a booking request for a bed; owner or manager approves or rejects. Approval creates the tenancy in one transaction.

**Payments:** rent goes **directly to the owner**. The platform never holds money. The platform only tracks status (`PENDING` → `MARKED_PAID` by tenant → `CONFIRMED` by owner/staff). Phase 3 starts with the owner's UPI ID/QR; a gateway can be added later behind the `PaymentService` interface.

**OTP:** sent through an `OtpSender` interface. The dev implementation only logs the OTP. A real provider is plugged in later.

**Open decisions (defaults used until changed):**
- Can one tenant have two ACTIVE tenancies at once? Default: no (reject).
- Access token 15 min, refresh token 7 days, refresh tokens rotate on use.
- Owner verification is on the user. Per-listing verification is not in scope yet.

### Permission matrix

| Action | Admin | Owner | Manager | Caretaker | Customer/Tenant |
|---|---|---|---|---|---|
| Verify owners | yes | | | | |
| Create/edit property, rooms, beds | | yes | edit only | | |
| Add/remove staff | | yes | | | |
| Create/end tenancy | | yes | yes | | |
| Approve/reject booking (Ph2) | | yes | yes | | |
| Confirm rent received (Ph3) | | yes | yes | yes | |
| Handle complaints, post notices (Ph3) | | yes | yes | yes | |
| Search listings, request booking (Ph2) | | | | | yes |
| Mark rent paid, raise complaint, vacate request (Ph3) | | | | | yes (own tenancy) |

---
