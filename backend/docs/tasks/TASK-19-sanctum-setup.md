# TASK-19 — Sanctum Setup & API Route Structure

**Phase**: 5 — Mobile API  
**Depends on**: TASK-01, TASK-04  
**Estimated effort**: 1 hour

---

## Description

Install and configure Laravel Sanctum for the mobile API. Set up the `routes/api.php` skeleton with all 15 API endpoints grouped by auth requirement. The mobile client sends `token_login` as a bearer token — but per parity requirements, no server-side token validation is performed on most endpoints. Sanctum is installed but most routes remain unauthenticated (parity).

---

## Affected Files

### Create / Modify
- `routes/api.php` — all API route definitions
- `config/sanctum.php` — expiry, stateful domains
- `app/Models/User.php` — `HasApiTokens` trait (already in TASK-04)

---

## Implementation Notes

### Why Sanctum is installed but routes are unauthenticated

Per the PRD (§14 Out of Scope), API authentication via Sanctum tokens replacing `token_login` would break the mobile client. The `token_login` mechanism is preserved as-is:
- Mobile client sends `token_login` in requests
- Server does NOT validate it (parity with CI3)
- Sanctum is installed for the admin future use (if any), and because `install:api` is part of the standard setup

### `routes/api.php`
```php
// Public routes (no auth required — parity)
Route::prefix('daftar')->group(function () {
    Route::post('/tambah', [AuthController::class, 'register']);
});

Route::prefix('login')->group(function () {
    Route::post('/otentikasi', [AuthController::class, 'login']);
    Route::post('/sudahlogin', [AuthController::class, 'refresh']);
    Route::get('/cekstatus',   [AuthController::class, 'status']);
});

Route::prefix('akun')->group(function () {
    Route::get('/tampil',  [AccountController::class, 'show']);
    Route::put('/tampil/{id_user}', [AccountController::class, 'update']);
});

Route::prefix('kategori')->group(function () {
    Route::get('/tampil', [CategoryController::class, 'mine']);
    Route::get('/semua',  [CategoryController::class, 'index']);
});

Route::prefix('paslon')->group(function () {
    Route::post('/tampil', [CandidateController::class, 'index']);
    Route::post('/detail', [CandidateController::class, 'show']);
});

Route::prefix('gambar')->group(function () {
    Route::post('/tambah', [PhotoController::class, 'enroll']);
    Route::post('/cek',    [PhotoController::class, 'verify']);
});

Route::prefix('suara')->group(function () {
    Route::post('/vote',          [VoteController::class, 'store']);
    Route::get('/tampil/{id_user}',[VoteController::class, 'history']);
    Route::get('/perolehan',      [VoteController::class, 'results']);
});
```

### URL patterns (exact CI3 parity)

| CI3 endpoint | Laravel route |
|---|---|
| `POST /api/daftar/tambah` | `POST /api/daftar/tambah` |
| `POST /api/login/otentikasi` | `POST /api/login/otentikasi` |
| `POST /api/login/sudahlogin` | `POST /api/login/sudahlogin` |
| `GET /api/login/cekstatus?id_user=` | `GET /api/login/cekstatus` |
| `GET /api/akun/tampil?id_user=` | `GET /api/akun/tampil` |
| `PUT /api/akun/tampil/{id}` | `PUT /api/akun/tampil/{id_user}` |
| `GET /api/kategori/tampil?id_user=` | `GET /api/kategori/tampil` |
| `GET /api/kategori/semua` | `GET /api/kategori/semua` |
| `POST /api/paslon/tampil` | `POST /api/paslon/tampil` |
| `POST /api/paslon/detail` | `POST /api/paslon/detail` |
| `POST /api/gambar/tambah` | `POST /api/gambar/tambah` |
| `POST /api/gambar/cek` | `POST /api/gambar/cek` |
| `POST /api/suara/vote` | `POST /api/suara/vote` |
| `GET /api/suara/tampil/{id}` | `GET /api/suara/tampil/{id_user}` |
| `GET /api/suara/perolehan?id_kategori=` | `GET /api/suara/perolehan` |

---

## Acceptance Criteria

- [ ] `php artisan route:list` shows all 15 API endpoints under the `/api/` prefix
- [ ] All routes are accessible without an `Authorization` header (unauthenticated — parity)
- [ ] `GET /api/login/cekstatus?id_user=10` returns HTTP 200 or 404 (not 401/403)
- [ ] `POST /api/suara/vote` without any auth header returns HTTP 200 (not 401)
- [ ] `php artisan install:api` was run and `personal_access_tokens` table exists
- [ ] No CORS errors when mobile client hits any endpoint (or CORS is permissive)
- [ ] Route file has no syntax errors (`php artisan route:cache` succeeds)
