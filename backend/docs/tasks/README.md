# Migration Task Index — CI3 → Laravel 12

**Total tasks**: 28  
**Source**: `docs/migration-plan.md`, `docs/prd.md`  
**Rule**: Each task is independently testable, has acceptance criteria, and lists affected files.

---

## Phase 1 — Foundation (Week 1–2)

| Task | Title | Effort | Depends On |
|---|---|---|---|
| [TASK-01](TASK-01-laravel-bootstrap.md) | Laravel Project Bootstrap | 2 h | — |
| [TASK-02](TASK-02-database-migrations.md) | Database Migrations (13 tables) | 3 h | TASK-01 |
| [TASK-03](TASK-03-database-seeders.md) | Database Seeders (all seed rows) | 1 h | TASK-02 |
| [TASK-04](TASK-04-eloquent-models.md) | Eloquent Models + Relationships | 2 h | TASK-02 |

## Phase 2 — Admin Authentication (Week 2)

| Task | Title | Effort | Depends On |
|---|---|---|---|
| [TASK-05](TASK-05-admin-auth-guard.md) | Admin Auth Guard Configuration | 0.5 h | TASK-04 |
| [TASK-06](TASK-06-admin-login.md) | Admin Login & Logout | 3 h | TASK-05, TASK-08 |
| [TASK-07](TASK-07-admin-middleware.md) | Admin Route Protection Middleware | 1 h | TASK-05 |
| [TASK-08](TASK-08-admin-layout.md) | Admin Blade Layout (AdminLTE) | 3 h | TASK-01 |

## Phase 3 — Admin CRUD Features (Week 3–4)

| Task | Title | Effort | Depends On |
|---|---|---|---|
| [TASK-09](TASK-09-dashboard.md) | Admin Dashboard | 0.5 h | TASK-07, TASK-08 |
| [TASK-10](TASK-10-voter-management.md) | Voter Management (list, activate, delete) | 2 h | TASK-04, TASK-07, TASK-08 |
| [TASK-11](TASK-11-category-management.md) | Election Category Management | 4 h | TASK-04, TASK-07, TASK-08, TASK-18 |
| [TASK-12](TASK-12-candidate-management.md) | Candidate Pair Management | 3 h | TASK-04, TASK-07, TASK-08, TASK-18 |
| [TASK-13](TASK-13-photo-album-management.md) | Photo & Album Management | 3 h | TASK-04, TASK-07, TASK-08, TASK-16 |
| [TASK-14](TASK-14-results-view.md) | Voting Results View | 1 h | TASK-04, TASK-07, TASK-08 |
| [TASK-15](TASK-15-admin-settings.md) | Admin Password Settings | 1.5 h | TASK-04, TASK-05, TASK-07, TASK-08 |

## Phase 4 — Service Layer (Week 4)

| Task | Title | Effort | Depends On |
|---|---|---|---|
| [TASK-16](TASK-16-lambda-service.md) | Lambda Face Recognition Service | 2 h | TASK-01 |
| [TASK-17](TASK-17-facematch-service.md) | FaceX Face Match Service | 1 h | TASK-01 |
| [TASK-18](TASK-18-file-storage.md) | File Storage Configuration | 1.5 h | TASK-01 |

## Phase 5 — Mobile API (Week 5–6)

| Task | Title | Effort | Depends On |
|---|---|---|---|
| [TASK-19](TASK-19-sanctum-setup.md) | Sanctum Setup & API Route Structure | 1 h | TASK-01, TASK-04 |
| [TASK-20](TASK-20-api-auth.md) | API Auth (register, login, refresh, status) | 3 h | TASK-04, TASK-19 |
| [TASK-21](TASK-21-api-account.md) | API Account (view profile, update profile) | 1.5 h | TASK-04, TASK-19 |
| [TASK-22](TASK-22-api-categories.md) | API Categories (authorized, all-open) | 1 h | TASK-04, TASK-19 |
| [TASK-23](TASK-23-api-candidates.md) | API Candidates (by category, detail) | 1.5 h | TASK-04, TASK-19 |
| [TASK-24](TASK-24-api-photos.md) | API Photos (enroll, verify) | 3 h | TASK-04, TASK-17, TASK-18, TASK-19 |
| [TASK-25](TASK-25-api-votes.md) | API Votes (submit, history, results) | 2 h | TASK-04, TASK-19 |

## Phase 6 — Testing (Week 7–8)

| Task | Title | Effort | Depends On |
|---|---|---|---|
| [TASK-26](TASK-26-admin-feature-tests.md) | Admin Panel Feature Tests | 4 h | TASK-06 – TASK-15 |
| [TASK-27](TASK-27-api-feature-tests.md) | API Feature Tests | 4 h | TASK-20 – TASK-25 |
| [TASK-28](TASK-28-service-unit-tests.md) | Service Unit Tests | 2 h | TASK-16, TASK-17 |

---

## Execution Order (critical path)

```
TASK-01
  └── TASK-02
        └── TASK-03
        └── TASK-04
              └── TASK-05
                    └── TASK-07
                    └── TASK-06 (+ TASK-08)
              └── TASK-10 – TASK-15 (+ TASK-07 + TASK-08)
              └── TASK-19
                    └── TASK-20 – TASK-25
  └── TASK-08
  └── TASK-16 (needed by TASK-13)
  └── TASK-17 (needed by TASK-24)
  └── TASK-18 (needed by TASK-11, TASK-12, TASK-24)
```

TASK-26, TASK-27, TASK-28 run last (all implementation complete).

---

## Quick Reference — Parity Constraints Per Task

| Task | Critical Parity Behaviour |
|---|---|
| TASK-02 | `photo.id_album` and `pencocokan.id_user` have NO FK constraints |
| TASK-02 | `paslon_ibfk_1` uses RESTRICT (not CASCADE) |
| TASK-03 | Seed user.password is `'325339'` (SHA1 string) — not bcrypt |
| TASK-06 | Token row NOT deleted on logout |
| TASK-06 | Redirect after login: `/Member` (capital M) |
| TASK-10 | `pencocokan` rows orphaned on user delete (no FK) |
| TASK-11 | Duplicate voter authorizations allowed (no uniqueness check) |
| TASK-13 | `status_train` NOT updated after Lambda train call |
| TASK-15 | Min 4 chars enforced; error message says "minimal 6 karakter" |
| TASK-20 | Voter passwords stored as SHA1 |
| TASK-20 | `sudahlogin` requires only `id_user` — no auth check |
| TASK-21 | Error strings lowercase: `"gagal"`, `"data kosong!"` |
| TASK-24 | `status_train = 2` on photo upload (not 0) |
| TASK-24 | `pencocokan.score` stored as empty string |
| TASK-25 | `otorisasi_pemilih` update: `WHERE id_user` only — ALL categories |
| TASK-25 | Vote endpoint always returns HTTP 200 |
| TASK-25 | Vote counts from `paslon.perolehan`, not `COUNT(voting.*)` |
