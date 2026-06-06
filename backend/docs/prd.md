# Product Requirements Document — FaceVoting Laravel 12 Rewrite

**Document type**: Product Requirements Document (PRD)  
**Project**: FaceVoting — Full Rewrite (CodeIgniter 3 → Laravel 12)  
**Parity target**: Feature, UI behaviour, Database, API  
**New features**: None  
**Source of truth**: `facevoting.sql`, all controllers, all models, all views  
**Prepared**: 2026-06-06

---

## Table of Contents

1. [Objective](#1-objective)
2. [Scope](#2-scope)
3. [Definitions](#3-definitions)
4. [Feature Parity Requirements](#4-feature-parity-requirements)
   - [F-01 Admin Login](#f-01-admin-login)
   - [F-02 Admin Logout](#f-02-admin-logout)
   - [F-03 Admin Dashboard](#f-03-admin-dashboard)
   - [F-04 Admin Password Change](#f-04-admin-password-change)
   - [F-05 Voter List](#f-05-voter-list)
   - [F-06 Voter Activation](#f-06-voter-activation)
   - [F-07 Voter Deletion](#f-07-voter-deletion)
   - [F-08 Election Category List](#f-08-election-category-list)
   - [F-09 Election Category Creation](#f-09-election-category-creation)
   - [F-10 Election Category Detail](#f-10-election-category-detail)
   - [F-11 Voter Authorization](#f-11-voter-authorization)
   - [F-12 Open Election Voting](#f-12-open-election-voting)
   - [F-13 Close Election Voting](#f-13-close-election-voting)
   - [F-14 Candidate Pair List](#f-14-candidate-pair-list)
   - [F-15 Candidate Pair Creation](#f-15-candidate-pair-creation)
   - [F-16 Candidate Pair Deletion](#f-16-candidate-pair-deletion)
   - [F-17 Training Photo List](#f-17-training-photo-list)
   - [F-18 Train Photo to Lambda](#f-18-train-photo-to-lambda)
   - [F-19 Delete Training Photo](#f-19-delete-training-photo)
   - [F-20 Album View](#f-20-album-view)
   - [F-21 Album Rebuild](#f-21-album-rebuild)
   - [F-22 Voting Results View](#f-22-voting-results-view)
5. [API Parity Requirements](#5-api-parity-requirements)
   - [A-01 Voter Registration](#a-01-voter-registration)
   - [A-02 Voter Login](#a-02-voter-login)
   - [A-03 Token Refresh](#a-03-token-refresh)
   - [A-04 Account Status Check](#a-04-account-status-check)
   - [A-05 View Account Profile](#a-05-view-account-profile)
   - [A-06 Update Account Profile](#a-06-update-account-profile)
   - [A-07 List Authorized Categories](#a-07-list-authorized-categories)
   - [A-08 List All Open Categories](#a-08-list-all-open-categories)
   - [A-09 List Candidates by Category](#a-09-list-candidates-by-category)
   - [A-10 Candidate Detail](#a-10-candidate-detail)
   - [A-11 Upload Face Training Photo](#a-11-upload-face-training-photo)
   - [A-12 Face Verification](#a-12-face-verification)
   - [A-13 Submit Vote](#a-13-submit-vote)
   - [A-14 Voting History](#a-14-voting-history)
   - [A-15 Vote Counts by Category](#a-15-vote-counts-by-category)
6. [UI Behaviour Parity](#6-ui-behaviour-parity)
7. [Database Parity Requirements](#7-database-parity-requirements)
8. [Validation Parity](#8-validation-parity)
9. [File Handling Parity](#9-file-handling-parity)
10. [External Integration Parity](#10-external-integration-parity)
11. [Authentication Behaviour Parity](#11-authentication-behaviour-parity)
12. [Non-Functional Requirements](#12-non-functional-requirements)
13. [Acceptance Criteria](#13-acceptance-criteria)
14. [Out of Scope](#14-out-of-scope)

---

## 1. Objective

Rewrite the FaceVoting backend from CodeIgniter 3.1.11 to Laravel 12 while preserving **exact behavioural parity** on four axes:

| Axis | Requirement |
|---|---|
| **Feature parity** | Every admin panel function works identically |
| **UI behaviour parity** | Every page, message, redirect, and validation error is identical |
| **Database parity** | Same tables, columns, types, constraints, and seed data |
| **API parity** | Same endpoints, verbs, request fields, response shapes, and HTTP status codes |

The mobile application (Flutter/native) must require **zero changes** after the backend is replaced. The admin panel must look and behave identically from the administrator's perspective.

Known bugs in the CI3 implementation are **preserved as-is** unless explicitly listed in §14 Out of Scope. The mobile client was built against the existing API contract — changing behaviour would break it.

---

## 2. Scope

### In Scope

- All 22 admin panel features (F-01 through F-22)
- All 15 mobile API endpoints (A-01 through A-15)
- All database tables, columns, types, indexes, and foreign keys from `facevoting.sql`
- All existing seed data
- All validation rules and exact error message strings
- All flash message strings (exact Indonesian text)
- All redirect targets after form submission
- All HTTP status codes on API responses
- All JSON response field names and shapes
- AdminLTE 3.0.5 admin UI (same look and feel)
- All file upload constraints (types, sizes, dimensions)
- All external service integrations (Lambda, FaceXAPI)
- All authentication flows

### Out of Scope

See §14 for the complete list. In summary: no new features, no new endpoints, no UI changes, no schema additions beyond what is needed to support Laravel internals (sessions, password resets, Sanctum tokens table).

---

## 3. Definitions

| Term | Meaning |
|---|---|
| **Parity** | Behaviour that is indistinguishable from the CI3 implementation |
| **Exact string** | A quoted string that must appear character-for-character in the output |
| **Flash message** | A one-request session message displayed after a redirect |
| **Token** | The `token_login` value stored in `user.token_login` and returned to the mobile client |
| **Authorization** | A row in `otorisasi_pemilih` granting a voter the right to vote in a category |
| **Status (user)** | `1` = active, `2` = pending admin activation |
| **Status (category)** | `1` = setup/closed, `2` = open for voting |
| **Status (authorization)** | `1` = authorized, not voted; `2` = voted |
| **Status (photo train)** | Existing values `0`, `1`, `2` — must be preserved as-is |

---

## 4. Feature Parity Requirements

Each feature section specifies: the exact page title, the data displayed, the exact validation messages, the exact flash messages, and the exact redirect targets. All text is in Bahasa Indonesia as in the original.

---

### F-01 Admin Login

**Route**: `GET /login`, `POST /login`  
**Page title**: `Login | FaceVoting Versi 1.0`  
**Default route**: The root URL `/` must redirect to `/login`

#### Page Load (GET)

- Render the login form
- Generate a CAPTCHA image and embed it on the page
- CAPTCHA configuration:
  - Width: 150 px
  - Height: 50 px
  - Character count: 5
  - Store word in session key `captchaword`
- If a session is already active (`is_login_in === true`), redirect to `/Member` before rendering

#### Form Submission (POST)

**Input fields** (all trimmed before validation):

| Field | HTML name | Type |
|---|---|---|
| Username | `username` | text |
| Password | `password` | password |
| CAPTCHA answer | `captcha` | text |

**Validation sequence** (run in this order):

1. `username` — required. Error: `Username harus diisi!`
2. `password` — required. Error: `Password harus diisi!`
3. `captcha` — required. Error: `Captcha harus diisi!`
4. `captcha` — compare (case-sensitive) to `session('captchaword')`. Error: `Captcha yang anda masukkan salah!`

**On validation failure**:
- Store validation errors in flash (key `pesan`)
- Generate a new CAPTCHA
- Re-render the login page (do not redirect)

**On validation pass — credential check**:
- Look up admin by `username`
- Verify password with bcrypt (`password_verify`)
- On failure: set flash key `pesan2` = `<div class="alert alert-danger" role="alert">Username atau password anda salah!</div>`
- Redirect to `/Login`

**On credential pass**:
- Generate token = `sha1(date('Y-m-d H:i:s'))`
- Convert same datetime to Unix timestamp
- If a token already exists for this admin, delete it first
- Insert new token row: `{id_admin: 1, token: $key, time: $logTime}`
- Set session: `{id_admin: 1, token: $key, is_login_in: true}`
- Redirect to `/Member`

#### Acceptance Criteria

- [ ] GET `/login` renders a form with CAPTCHA image
- [ ] Already-authenticated requests redirect to `/Member`
- [ ] Each of the 4 validation rules fires with the exact message string above
- [ ] Wrong credentials show the exact danger alert string above
- [ ] Successful login sets session and redirects to `/Member`
- [ ] A new CAPTCHA is generated on every GET and on every failed POST

---

### F-02 Admin Logout

**Route**: `GET /member/logout`

#### Behaviour

- Destroy the PHP session entirely
- Redirect to `/Login`

> The `token` row in the database is **not** deleted on logout (parity with CI3 behaviour). It is deleted on the next successful login.

#### Acceptance Criteria

- [ ] Session is destroyed
- [ ] Redirect target is `/Login` (capital L, as in CI3)
- [ ] Token row in `token` table is not deleted

---

### F-03 Admin Dashboard

**Route**: `GET /member`  
**Page title**: `Dashboard | FaceVoting Versi 1.0`

#### Session Guard

Applied in constructor of every protected controller. Sequence:

1. If `session('is_login_in') !== true` → redirect to `/login`
2. Read `token` from DB where `id_admin = session('id_admin')`
3. In every method: if `session('token') !== db_token` → call `_unlogin()` (destroy session + redirect to `/login`)

This guard applies identically to all admin controllers below.

#### Page Load

- Render view `v_member` with page title
- No data query — dashboard is a static summary page

#### Acceptance Criteria

- [ ] Unauthenticated GET → redirect `/login`
- [ ] Token mismatch → session destroyed → redirect `/login`
- [ ] Authenticated → renders dashboard view

---

### F-04 Admin Password Change

**Route**: `GET /setting`, `POST /setting`  
**Page title**: `Dashboard | FaceVoting Versi 1.0`

#### Page Load (GET)

- Render view `v_setting`
- If form errors exist in flash key `message1`, display them

#### Form Submission (POST)

**Input fields**:

| Field | HTML name |
|---|---|
| New password | `password1` |
| Confirm password | `password2` |

**Validation rules** (exact):

| Field | Rule | Error message |
|---|---|---|
| `password1` | required | `Password harus diisi!` |
| `password1` | min_length 4 | `Panjang karakter Password minimal 6 karakter!` |
| `password1` | max_length 16 | `Panjang karakter Password maksimal 16 karakter!` |
| `password2` | required | `Password harus diisi!` |
| `password2` | matches `password1` | `Password tidak sama!` |

> The min_length error message says "6" while the rule enforces 4. This discrepancy must be preserved exactly.

**On validation failure**:
- Store errors in flash key `message1`
- Re-render `v_setting`

**On validation pass**:
- Hash `password1` with `PASSWORD_BCRYPT`
- Update `admin` row where `id_admin = 1`
- Set flash `message2` = `<div class="alert alert-success" role="alert">Berhasil Memperbaharui data!</div>`
- Redirect to `/Setting`

#### Acceptance Criteria

- [ ] Both fields required
- [ ] Min 4 chars enforced; error message says "minimal 6 karakter"
- [ ] Max 16 chars enforced
- [ ] Mismatch produces "Password tidak sama!"
- [ ] Success shows exact success flash and redirects to `/Setting`
- [ ] New password stored as bcrypt hash

---

### F-05 Voter List

**Route**: `GET /user`  
**Page title**: `Dashboard | FaceVoting Versi 1.0`

#### Data Displayed

Query: `SELECT * FROM user JOIN detail_user ON detail_user.id_user = user.id_user`  
All users regardless of status (pending and active both shown).

**Columns available to view**:
- `id_user`
- `email`
- `status` (1 or 2)
- `nama` (from `detail_user`)
- `identitas` (from `detail_user`)

#### Acceptance Criteria

- [ ] All users (status 1 and 2) appear in the list
- [ ] Data comes from JOIN of `user` and `detail_user`

---

### F-06 Voter Activation

**Route**: `GET /user/aktivasi/{id}`

#### Behaviour Sequence

1. If `$id` is null or empty string → redirect to `/User`
2. Query `user JOIN detail_user WHERE id_user = $id AND status = 2`
3. If 0 rows returned → redirect to `/User` (silently, no message)
4. Set `user.status = 1` where `id_user = $id`
5. Set flash `pesan2` = `<div class="alert alert-success" role="alert">User telah berhasil diaktifkan!</div>`
6. Redirect to `/User`

> Step 3 means already-active users (status=1) silently redirect with no message — parity required.

#### Acceptance Criteria

- [ ] Null/empty ID → silent redirect to `/User`
- [ ] Non-existent or already-active user (status≠2) → silent redirect to `/User`
- [ ] Valid pending user → status set to 1 → success flash → redirect `/User`
- [ ] Success flash is exactly the string above

---

### F-07 Voter Deletion

**Route**: `GET /user/hapus/{id}`

#### Behaviour Sequence

1. If `$id` is null or empty string → redirect to `/User`
2. Query `user WHERE id_user = $id` (no status filter)
3. If 0 rows → redirect to `/User` (silent)
4. Delete from `user` where `id_user = $id`
5. Set flash `pesan` = `<div class="alert alert-danger" role="alert">User telah dihapus!</div>`
6. Redirect to `/User`

**Cascade effects** (must occur in same transaction or via FK cascade):
- `detail_user` rows deleted
- `photo` rows deleted
- `otorisasi_pemilih` rows deleted
- `voting` rows deleted
- `pencocokan` rows **not** deleted (no FK)
- Files in `gambar/user/` **not** deleted

#### Acceptance Criteria

- [ ] Null/empty ID → silent redirect
- [ ] Non-existent ID → silent redirect
- [ ] Valid ID → user deleted → cascade fires → danger flash → redirect `/User`
- [ ] `pencocokan` rows remain (orphaned) — parity with CI3
- [ ] Physical files remain on disk — parity with CI3

---

### F-08 Election Category List

**Route**: `GET /kategori`  
**Page title**: `Dashboard | FaceVoting Versi 1.0`

#### Data Displayed

`SELECT * FROM kategori` — all rows, no filter.

**Columns**: `id_kategori`, `nama_kategori`, `logo_kategori`, `status_kategori`

#### Acceptance Criteria

- [ ] All categories shown regardless of `status_kategori`

---

### F-09 Election Category Creation

**Route**: `GET /kategori/tambah`, `POST /kategori/tambah`  
**Page title**: `Dashboard | FaceVoting Versi 1.0`

#### Page Load (GET)

- Render view `v_tambahkategori`
- Display flash `message1` (validation errors) if present
- Display flash `message2` (upload errors) if present

#### Form Submission (POST)

**Input fields**:

| Field | HTML name | Type |
|---|---|---|
| Category name | `namaKategori` | text |
| Logo image | `logo` | file |

**Validation — text fields**:

| Field | Rule | Error |
|---|---|---|
| `namaKategori` | required, trim | `Nama Kategori harus diisi!` |

**On text validation failure**:
- Store in flash `message1`
- Re-render `v_tambahkategori`
- Do **not** attempt file upload

**File upload** (attempted only after text validation passes):

| Constraint | Value |
|---|---|
| Upload path | `./gambar/` |
| Allowed types | `jpg`, `jpeg`, `png`, `gif` |
| Max size | 2,424 KB |
| Filename | `substr(sha1(time()), 0, 10)` |

**On upload failure**:
- Store upload error text in flash `message2` = `<div class="alert alert-danger" role="alert">{error}</div>`
- Redirect to `/Kategori/tambah`

**On success**:
- Insert: `{nama_kategori, logo_kategori: uploaded_filename, status_kategori: 1}`
- Set flash `message2` = `<div class="alert alert-success" role="alert">Kategori berhasil ditambahkan!</div>`
- Redirect to `/Kategori`

#### Acceptance Criteria

- [ ] Text validation fires before upload attempt
- [ ] Upload path is `./gambar/` (relative to project root / public)
- [ ] Filename is 10-char sha1 substring
- [ ] New category has `status_kategori = 1`
- [ ] Upload errors stored in `message2`, text errors in `message1`
- [ ] Success redirects to `/Kategori`, failure redirects to `/Kategori/tambah`

---

### F-10 Election Category Detail

**Route**: `GET /kategori/detail/{id}`  
**Page title**: `Dashboard | FaceVoting Versi 1.0`

#### Behaviour Sequence

1. If `$id` is null or empty, or category doesn't exist → redirect to `/Kategori`
2. Load `dataPaslon` = `SELECT * FROM paslon WHERE id_kategori = $id`
3. Load `dataPemilih` = `SELECT * FROM otorisasi_pemilih LEFT JOIN detail_user WHERE id_kategori = $id`
4. Load `dataCalonPemilih` = `SELECT user.id_user, detail_user.nama FROM user JOIN detail_user ... WHERE user.id_user NOT IN (SELECT id_user FROM otorisasi_pemilih WHERE id_kategori = $id)`
5. Pass `idKategori = $id` to view
6. Render `v_detailkategori`

#### Acceptance Criteria

- [ ] Invalid ID → redirect `/Kategori`
- [ ] `dataCalonPemilih` uses the exact subquery pattern (NOT IN)
- [ ] All 4 data arrays passed to view
- [ ] `idKategori` variable passed separately

---

### F-11 Voter Authorization

**Route**: `GET /kategori/tambah_pemilih/{idKat}/{idUser}`

#### Behaviour Sequence

1. If either param is null/empty → redirect to `/Kategori`
2. Verify category exists via `get_data_kategori($idKat)` → 0 rows → redirect `/Kategori`
3. Verify user exists via `get_data_user2($idUser)` → 0 rows → redirect `/Kategori`
4. Insert: `{id_user: $idUser, id_kategori: $idKat, status: 1}`
5. Set flash `message` = `<div class="alert alert-success" role="alert">User berhasil diberi hak akses!</div>`
6. Redirect to `/Kategori/detail/{idKat}`

> No duplicate check is performed. Calling this URL twice creates two authorization rows. This is existing behaviour and must be preserved.

#### Acceptance Criteria

- [ ] Either param missing → redirect `/Kategori`
- [ ] Category or user not found → redirect `/Kategori`
- [ ] Duplicate authorization rows are allowed (no uniqueness check)
- [ ] Flash key is `message` (not `pesan` or `message2`)
- [ ] Redirect target is `/Kategori/detail/{idKat}` (not `/Kategori`)

---

### F-12 Open Election Voting

**Route**: `GET /kategori/Buka/{id}`

#### Behaviour Sequence

1. If `$id` is empty → redirect `/Kategori` (no message)
2. If category doesn't exist → redirect `/Kategori` (no message)
3. Set `status_kategori = 2`
4. Set flash `message2` = `<div class="alert alert-success" role="alert">Pemilu saat ini telah dibuka!</div>`
5. Redirect to `/Kategori`

#### Acceptance Criteria

- [ ] Sets `status_kategori = 2`
- [ ] Success flash is green (alert-success)
- [ ] Redirect to `/Kategori`

---

### F-13 Close Election Voting

**Route**: `GET /kategori/Tutup/{id}`

#### Behaviour Sequence

1. If `$id` is empty → redirect `/Kategori`
2. If category doesn't exist → redirect `/Kategori`
3. Set `status_kategori = 1`
4. Set flash `message2` = `<div class="alert alert-danger" role="alert">Pemilu saat ini telah ditutup!</div>`
5. Redirect to `/Kategori`

#### Acceptance Criteria

- [ ] Sets `status_kategori = 1`
- [ ] Success flash is red (alert-danger)
- [ ] Redirect to `/Kategori`

---

### F-14 Candidate Pair List

**Route**: `GET /paslon`  
**Page title**: `Dashboard | FaceVoting Versi 1.0`

#### Data Displayed

Query: `SELECT * FROM paslon JOIN kategori ON ... JOIN detail_paslon ON ...`  
All candidate pairs with their category name and profile details.

#### Acceptance Criteria

- [ ] All candidate pairs shown (no filter)
- [ ] Each row includes `kategori` and `detail_paslon` data

---

### F-15 Candidate Pair Creation

**Route**: `GET /paslon/tambah`, `POST /paslon/tambah`  
**Page title**: `Dashboard | FaceVoting Versi 1.0`

#### Page Load (GET)

- Load `daftarKategori` = `SELECT * FROM kategori WHERE status_kategori = 1`
- Render `v_tambahpaslon` with category dropdown
- Display flash `message1` if present

#### Form Submission (POST)

**Input fields**:

| Field | HTML name | Type | Required |
|---|---|---|---|
| Category ID | `namaKategori` | select (value = id_kategori) | Yes |
| Pair label | `judulPaslon` | text | Yes |
| Leader name | `ketuaPaslon` | text | Yes |
| Leader profile | `profilCatum` | textarea | Yes |
| Deputy name | `wakilPaslon` | text | Yes |
| Deputy profile | `profilCawatum` | textarea | Yes |
| Vision & mission | `visimisi` | textarea | Yes |
| Leader photo | `photo1` | file | No |
| Deputy photo | `photo2` | file | No |

**Validation rules** — all text fields required with trim:

| Field | Error message |
|---|---|
| `namaKategori` | `Nama Kategori harus diisi!` |
| `judulPaslon` | `Judul Paslon harus diisi!` |
| `ketuaPaslon` | `Nama Calon Ketua harus diisi!` |
| `profilCatum` | `Profil Calon Ketua harus diisi!` |
| `wakilPaslon` | `Nama Calon Wakil harus diisi!` |
| `profilCawatum` | `Profil Calon Wakil harus diisi!` |
| `visimisi` | `Visi dan Misi harus diisi!` |

**On text validation failure**:
- Store errors in flash `message1`
- Reload `daftarKategori` and re-render `v_tambahpaslon`

**Photo upload** (only if file field is not empty):

| Constraint | Value |
|---|---|
| Upload path | `./gambar/` |
| Allowed types | `jpg`, `jpeg`, `png`, `gif` |
| Max size | 2,424 KB |
| Filename | Auto-generated by CI upload library |

**On validation + upload success**:
1. Insert into `paslon`: `{id_kategori, judul_paslon, ketua_paslon, wakil_paslon, photo1_paslon, photo2_paslon, perolehan: 0}`
2. Capture `insert_id()`
3. Insert into `detail_paslon`: `{id_paslon, visi_misi, profil_catum, profil_cawatum}`
4. Set flash `message2` = `<div class="alert alert-success" role="alert">Pasangan Calon berhasil ditambahkan!</div>`
5. Redirect to `/Paslon`

> Photos are optional. If `$_FILES['photo1']['name']` is empty, no upload is attempted. Photo columns may be null.

#### Acceptance Criteria

- [ ] Category dropdown only shows categories with `status_kategori = 1`
- [ ] All 7 text fields are required
- [ ] Each field produces its exact Indonesian error message
- [ ] Photos are optional; if omitted, columns are null
- [ ] Two sequential inserts (paslon then detail_paslon) using insert_id
- [ ] `perolehan` initialised to `0`
- [ ] Success flash and redirect to `/Paslon`

---

### F-16 Candidate Pair Deletion

**Route**: `GET /paslon/hapus/{id}`

#### Behaviour Sequence

1. If `$id` is empty → redirect `/Paslon`
2. Query `paslon WHERE id_paslon = $id` → 0 rows → redirect `/Paslon`
3. Delete from `paslon WHERE id_paslon = $id`
4. Set flash `message2` = `<div class="alert alert-danger" role="alert">Data paslon berhasil dihapus!</div>`
5. Redirect to `/Paslon`

**Cascade effects**:
- `detail_paslon` rows deleted (FK CASCADE)
- `voting` rows deleted (FK CASCADE)
- Photo files in `gambar/` **not** deleted

#### Acceptance Criteria

- [ ] Empty/invalid ID → silent redirect `/Paslon`
- [ ] Valid ID → delete → cascade → danger flash → redirect `/Paslon`
- [ ] Physical photo files remain on disk

---

### F-17 Training Photo List

**Route**: `GET /photo`  
**Page title**: `Dashboard | FaceVoting Versi 1.0`

#### Data Displayed

Query: `SELECT * FROM photo JOIN album ON ... JOIN detail_user ON ... ORDER BY photo.status_train DESC`

**Columns**: `id_photo`, `gambar`, `status_train`, `nama_album`, `kode_album`, `nama`, `identitas`

> Ordered by `status_train DESC` — trained photos (higher status values) appear first.

#### Acceptance Criteria

- [ ] Joins all three tables
- [ ] Ordered by `status_train DESC`

---

### F-18 Train Photo to Lambda

**Route**: `GET /photo/rekam/{id}`

#### Behaviour Sequence

1. If `$id` is null/empty → redirect `/Photo`
2. Query `photo JOIN album WHERE id_photo = $id` → 0 rows → redirect `/Photo`
3. Read: `gambar`, `nama_album`, `kode_album`, `entry_id` from row
4. Call Lambda API `POST /album_train` with:
   - `urls` = `http://facevoting.xyz/gambar/user/{gambar}`
   - `album` = `nama_album`
   - `albumkey` = `kode_album`
   - `entryid` = `entry_id`
5. On cURL error (null response):
   - Flash `pesan` = `<div class="alert alert-danger" role="alert">Perekaman data Gagal!</div>`
   - Redirect `/Photo`
6. On success:
   - JSON-decode response
   - Flash `pesan` = `<div class="alert alert-success" role="alert">Berhasil, jumlah photo tersimpan ke dalam album <span class="text-warning">{album}</span> adalah <span class="text-warning">{image_count}</span> gambar </div>`
   - Redirect `/Photo`

> `status_train` is **not** updated after a successful train call. This is existing behaviour and must be preserved.

#### Lambda API Call Spec

| Property | Value |
|---|---|
| URL | `https://lambda-face-recognition.p.rapidapi.com/album_train` |
| Method | POST |
| Content-Type | `application/x-www-form-urlencoded` |
| Header `x-rapidapi-host` | `lambda-face-recognition.p.rapidapi.com` |
| Header `x-rapidapi-key` | `932571abf0msh45cf0f3cef74aacp19e151jsn33e9949a1974` |
| Timeout | 30 seconds |
| Follow redirects | Yes |
| Max redirects | 10 |

#### Acceptance Criteria

- [ ] Null/empty ID → silent redirect `/Photo`
- [ ] Invalid ID → silent redirect `/Photo`
- [ ] On cURL failure: exact danger flash, redirect `/Photo`
- [ ] On success: exact success flash with album name and image count, redirect `/Photo`
- [ ] `status_train` NOT updated
- [ ] Exact Lambda endpoint and headers used

---

### F-19 Delete Training Photo

**Route**: `GET /photo/hapus/{id}`

#### Behaviour Sequence

1. If `$id` is null/empty → redirect `/Photo`
2. Query `photo JOIN album WHERE id_photo = $id` → 0 rows → redirect `/Photo`
3. Read `gambar` filename from row
4. Delete from `photo WHERE id_photo = $id`
5. Delete file at `{FCPATH}gambar/user/{gambar}` using `unlink()`
6. Set flash `pesan` = `<div class="alert alert-danger" role="alert">Photo berhasil dihapus!</div>`
7. Redirect `/Photo`

#### Acceptance Criteria

- [ ] DB row deleted
- [ ] Physical file deleted via `unlink()`
- [ ] Danger flash with exact string
- [ ] Redirect `/Photo`

---

### F-20 Album View

**Route**: `GET /photo/viewAlbum`  
**Page title**: `Dashboard | FaceVoting Versi 1.0`

#### Behaviour

- Fetch album where `id_album = 3` (hardcoded)
- Call Lambda API `GET /album?album={nama_album}&albumkey={kode_album}`
- Pass raw JSON string as `getViewAlbum` to view `v_detailalbum`
- On cURL error: pass `null` to view

#### Acceptance Criteria

- [ ] Album ID hardcoded to `3`
- [ ] Raw JSON string passed to view (not decoded)
- [ ] cURL error produces `null` variable in view

---

### F-21 Album Rebuild

**Route**: `GET /photo/rebuild`  
**Page title**: `Dashboard | FaceVoting Versi 1.0`

#### Behaviour

- Fetch album where `id_album = 3` (hardcoded)
- Call Lambda API `GET /album_rebuild?album={nama_album}&albumkey={kode_album}`
- Pass raw JSON string as `getRebuild` to view `v_rebuildalbum`
- On cURL error: pass `null` to view

#### Acceptance Criteria

- [ ] Album ID hardcoded to `3`
- [ ] Raw JSON passed to view
- [ ] Uses `GET /album_rebuild` (not POST)

---

### F-22 Voting Results View

**Route**: `GET /hasil`  
**Page title**: `Dashboard | FaceVoting Versi 1.0`

#### Data Displayed

Query: `SELECT * FROM paslon JOIN kategori ON ... JOIN detail_paslon ON ... ORDER BY paslon.perolehan DESC`

#### Behaviour

- Render `v_hasil` with `dataPaslon` sorted by `perolehan` descending
- Session guard is applied but token mismatch does **not** call `_unlogin()` in this controller's `index()` method — the page renders regardless of token state

> The missing `_unlogin()` call in CI3's `Hasil::index()` must be preserved as-is (parity).

#### Acceptance Criteria

- [ ] All candidates from all categories shown
- [ ] Ordered by `perolehan` DESC
- [ ] Token mismatch does NOT prevent page render (parity with CI3 bug)

---

## 5. API Parity Requirements

All API endpoints must return JSON. Content-Type must be `application/json`.  
All field names in request and response must be preserved exactly.  
HTTP status codes must match exactly.

The base path is `/api/`.

---

### A-01 Voter Registration

**Endpoint**: `POST /api/daftar/tambah`  
**Authentication**: None

#### Request Body

| Field | Type | Required |
|---|---|---|
| `nama` | string | Yes (no server-side validation) |
| `identitas` | string | Yes (no server-side validation) |
| `email` | string | Yes |
| `password` | string | Yes (no server-side validation beyond existence) |
| `token_firebase` | string | No |

#### Processing

1. SHA1-hash `password`
2. Check `email` uniqueness via JOIN `user + detail_user WHERE email = ?`
3. If email exists: return 404 with error body
4. Generate `token_login` = `substr(str_shuffle('0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 1, 10)`
5. Generate `entry_id` = `substr(sha1(time()), 1, 10)`
6. Insert `user`: `{email, password: sha1_hash, token_firebase, token_login, entry_id, status: 2}`
7. Capture `insert_id`
8. Insert `detail_user`: `{id_user: insert_id, nama, identitas}`

#### Response — Success (HTTP 200)

```json
{
  "status": "Success",
  "message": "Data User berhasil disimpan!",
  "id_user": 12,
  "validasi": 2,
  "token_login": "A3B7X9Z1KP"
}
```

#### Response — Email Exists (HTTP 404)

```json
{
  "status": "Failed",
  "message": "Email sudah terdaftar, silahkan gunakan email yang lain!"
}
```

#### Acceptance Criteria

- [ ] Exact field names in response
- [ ] `validasi` always `2` on success
- [ ] `status` is `"Success"` / `"Failed"` (capitalised)
- [ ] Email check uses JOIN on `detail_user`
- [ ] No validation on `nama`, `identitas`, `password` format
- [ ] HTTP 200 on success, 404 on duplicate email

---

### A-02 Voter Login

**Endpoint**: `POST /api/login/otentikasi`  
**Authentication**: None

#### Request Body

| Field | Type |
|---|---|
| `email` | string |
| `password` | string (plaintext — SHA1-hashed server-side) |

#### Processing

1. SHA1-hash `password`
2. Query `user WHERE email = ? AND password = sha1_hash` → count rows
3. If count = 0: return 404
4. Fetch user data via `cekEmail($email)` (SELECT user JOIN detail_user WHERE email)
5. Generate new `token_login` = `substr(str_shuffle('0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 1, 10)`
6. Update `user.token_login` where `id_user = ?`

#### Response — Success (HTTP 200)

```json
{
  "token_login": "A3B7X9Z1KP",
  "id_user": 10,
  "nama": "Alexsander Hendra Wijaya",
  "identitas": "123456",
  "email": "alexistdev@gmail.com",
  "validasi": 1
}
```

#### Response — Invalid Credentials (HTTP 404)

```json
{
  "message": "Username atau Password yang anda masukkan salah"
}
```

> Note: `validasi` field reflects `user.status`. Pending users (status=2) receive a token and `"validasi": 2` — not blocked server-side.

#### Acceptance Criteria

- [ ] Password SHA1-hashed before comparison
- [ ] `nama` and `identitas` come from `detail_user` via second query
- [ ] All 6 response fields present on success
- [ ] HTTP 404 with exact message string on failure

---

### A-03 Token Refresh

**Endpoint**: `POST /api/login/sudahlogin`  
**Authentication**: None

#### Request Body

| Field | Type |
|---|---|
| `id_user` | integer |

#### Processing

1. Query `user WHERE id_user = ?` → count rows
2. If 0 rows: return 404
3. Generate new `token_login`
4. Update `user.token_login`

#### Response — Success (HTTP 200)

```json
{
  "token_login": "X9Z1KPA3B7",
  "id_user": 10,
  "email": "alexistdev@gmail.com",
  "validasi": 1
}
```

#### Response — Not Found (HTTP 404)

```json
{
  "message": "Gagal mendapatkan data"
}
```

> No password or token verification — only `id_user` required. Parity must be preserved.

#### Acceptance Criteria

- [ ] No authentication required
- [ ] 4 fields in success response (note: no `nama`, `identitas` — different from login)
- [ ] HTTP 404 with exact message on failure

---

### A-04 Account Status Check

**Endpoint**: `GET /api/login/cekstatus?id_user={id}`  
**Authentication**: None

#### Processing

1. Query `user JOIN detail_user WHERE id_user = ?`
2. If 0 rows: return 404

#### Response — Success (HTTP 200)

```json
{
  "validasi": 1,
  "token_login": "A3B7X9Z1KP",
  "nama": "Alexsander Hendra Wijaya",
  "identitas": "123456",
  "message": "Berhasil mendapatkan data"
}
```

#### Response — Not Found (HTTP 404)

```json
{
  "message": "Gagal mendapatkan data"
}
```

> `token_login` is returned in the response. This is existing behaviour and must be preserved.

#### Acceptance Criteria

- [ ] `token_login` included in success response
- [ ] 5 fields in success response
- [ ] HTTP 200 / 404

---

### A-05 View Account Profile

**Endpoint**: `GET /api/akun/tampil?id_user={id}`  
**Authentication**: None required (parity)

#### Processing

1. Query `user JOIN detail_user WHERE id_user = ?`
2. If 0 rows: return 404

#### Response — Success (HTTP 200)

```json
{
  "email": "alexistdev@gmail.com",
  "nama": "Alexsander Hendra Wijaya",
  "identitas": "123456"
}
```

#### Response — Not Found (HTTP 404)

```json
{
  "status": "gagal",
  "message": "data kosong!"
}
```

> Note lowercase `"gagal"` and lowercase `"data kosong!"` — exact string parity required.

#### Acceptance Criteria

- [ ] Exactly 3 fields in success response
- [ ] Error uses `"gagal"` / `"data kosong!"` (lowercase)
- [ ] HTTP 200 / 404

---

### A-06 Update Account Profile

**Endpoint**: `PUT /api/akun/tampil/{id_user}`  
**Authentication**: None required (parity)

#### Request Body

| Field | Type | Required |
|---|---|---|
| `nama` | string | Yes (no server-side format validation) |
| `identitas` | string | Yes (no server-side format validation) |
| `password` | string | No |

#### Processing

1. Query `user JOIN detail_user WHERE id_user = ?`
2. If 0 rows: return 404 `{"status":"gagal","message":"Silahkan relogin!"}`
3. If `password` is non-empty: SHA1-hash and update `user.password`
4. Update `detail_user SET nama = ?, identitas = ? WHERE id_user = ?`
5. If update returns false: return 404 `{"status":"gagal","message":"Gagal menyimpan ke dalam server!"}`

#### Response — Success (HTTP 200)

```json
{
  "status": "berhasil",
  "message": "Data berhasil diperbaharui"
}
```

#### Acceptance Criteria

- [ ] `password` is optional — only updated if non-empty
- [ ] Password SHA1-hashed if provided
- [ ] `"status": "berhasil"` (lowercase)
- [ ] Exact error message strings for each failure case

---

### A-07 List Authorized Categories

**Endpoint**: `GET /api/kategori/tampil?id_user={id}`  
**Authentication**: None required (parity)

#### Processing

Query: `SELECT * FROM otorisasi_pemilih JOIN kategori ON ... WHERE id_user = ? AND otorisasi_pemilih.status = 1`

#### Response — Success (HTTP 200)

```json
{
  "status": "berhasil",
  "result": [ { "id_otorisasi": 8, "id_user": 10, "id_kategori": 8, "status": 1, "nama_kategori": "BEM", "logo_kategori": "bem_logo.png", "status_kategori": 2 } ],
  "message": "Data berhasil didapatkan"
}
```

#### Response — Empty (HTTP 404)

```json
{
  "status": "gagal",
  "result": [],
  "message": "data kosong!"
}
```

> Only categories with `otorisasi_pemilih.status = 1` are returned. Voted categories (status=2) are excluded.

#### Acceptance Criteria

- [ ] Filter: `otorisasi_pemilih.status = 1` only
- [ ] `result` is `[]` on 404 (not omitted)
- [ ] 3-field wrapper: `status`, `result`, `message`

---

### A-08 List All Open Categories

**Endpoint**: `GET /api/kategori/semua`  
**Authentication**: None

#### Processing

Query: `SELECT * FROM kategori WHERE status_kategori = 2`

#### Response — Success (HTTP 200)

```json
{
  "status": "berhasil",
  "result": [ { "id_kategori": 8, "nama_kategori": "BEM", "logo_kategori": "bem_logo.png", "status_kategori": 2 } ],
  "message": "Data berhasil didapatkan"
}
```

#### Response — Empty (HTTP 404)

```json
{
  "status": "gagal",
  "result": [],
  "message": "data kosong!"
}
```

#### Acceptance Criteria

- [ ] Only `status_kategori = 2` returned
- [ ] Consistent 3-field wrapper

---

### A-09 List Candidates by Category

**Endpoint**: `POST /api/paslon/tampil`  
**Authentication**: None required (parity)

#### Request Body

| Field | Type |
|---|---|
| `id_kategori` | integer |

#### Processing

Query: `SELECT * FROM paslon JOIN detail_paslon ON detail_paslon.id_paslon = paslon.id_paslon WHERE paslon.id_kategori = ?`

#### Response — Success (HTTP 200)

```json
{
  "status": "berhasil",
  "result": [
    {
      "id_paslon": 1,
      "id_kategori": 8,
      "judul_paslon": "Paslon 1",
      "ketua_paslon": "Steve Job",
      "wakil_paslon": "Steve Wozniak",
      "photo1_paslon": "stevejob.jpg",
      "photo2_paslon": "stevewozniak.jpg",
      "perolehan": 0,
      "id_detailpaslon": 2,
      "visi_misi": "Mencerdaskan kehidupan Bangsa",
      "profil_catum": "...",
      "profil_cawatum": "..."
    }
  ],
  "message": "Data berhasil didapatkan"
}
```

> Photo values are filenames only — not full URLs. The mobile app constructs the URL.

#### Response — Empty (HTTP 404)

```json
{ "status": "gagal", "result": [], "message": "data kosong!" }
```

#### Acceptance Criteria

- [ ] Uses `POST` verb (not `GET`)
- [ ] `detail_paslon` fields merged into each result row
- [ ] Photo fields are bare filenames

---

### A-10 Candidate Detail

**Endpoint**: `POST /api/paslon/detail`  
**Authentication**: None required (parity)

#### Request Body

| Field | Type |
|---|---|
| `id_paslon` | integer |

#### Processing

Query from `detail_paslon`: `SELECT * FROM detail_paslon JOIN paslon ON paslon.id_paslon = detail_paslon.id_paslon WHERE detail_paslon.id_paslon = ?`

#### Response — Success (HTTP 200)

```json
{
  "status": "berhasil",
  "result": [
    {
      "id_detailpaslon": 2,
      "id_paslon": 1,
      "visi_misi": "...",
      "profil_catum": "...",
      "profil_cawatum": "...",
      "id_kategori": 8,
      "judul_paslon": "Paslon 1",
      "ketua_paslon": "Steve Job",
      "wakil_paslon": "Steve Wozniak",
      "photo1_paslon": "stevejob.jpg",
      "photo2_paslon": "stevewozniak.jpg",
      "perolehan": 0
    }
  ],
  "message": "Data berhasil didapatkan"
}
```

> This query starts from `detail_paslon` and JOINs `paslon` — the order of fields in the result differs from A-09.

#### Acceptance Criteria

- [ ] Uses `POST` verb
- [ ] Query root table is `detail_paslon`
- [ ] All `paslon` fields included in merged row

---

### A-11 Upload Face Training Photo

**Endpoint**: `POST /api/gambar/tambah`  
**Authentication**: None required (parity)  
**Content-Type**: `multipart/form-data`

#### Request Body

| Field | Type |
|---|---|
| `id_user` | integer |
| `upload` | file |

#### File Constraints

| Constraint | Value |
|---|---|
| Upload path | `gambar/user/` |
| Allowed types | `jpg`, `png`, `jpeg` |
| Max size | 10,000 KB |
| Max width | 4,200 px |
| Max height | 4,200 px |
| Filename | `substr(sha1(time()), 1, 10)` |

#### Processing

1. Query `user WHERE id_user = ?` → not found: 404
2. Generate filename = `substr(sha1(time()), 1, 10)`
3. Upload file to `gambar/user/`
4. Insert `photo`: `{id_album: 3, id_user, entry_id: user.entry_id, nama_photo: stem, gambar: filename, status_train: 2}`

> `status_train = 2` — not `0`. Parity required.  
> `id_album` hardcoded to `3`.  
> No Lambda API call is made in this endpoint.

#### Response — Success (HTTP 200)

```json
{
  "status": "success",
  "message": "Gambar berhasil diupload",
  "nama_file": "b6a0595f28.jpg"
}
```

#### Response — User Not Found (HTTP 404)

```json
{
  "status": "failed",
  "message": "User tidak ditemukan!"
}
```

#### Response — Upload Failure (HTTP 404)

```json
{
  "status": "failed",
  "message": "{CI upload library error text}"
}
```

> Upload error text is stripped of HTML tags (`strip_tags()`).

#### Acceptance Criteria

- [ ] File field name is `upload` (not `file` or `image`)
- [ ] `status_train` stored as `2`
- [ ] `id_album` hardcoded to `3`
- [ ] `entry_id` taken from `user.entry_id`
- [ ] Upload errors are `strip_tags()` cleaned
- [ ] HTTP 200 / 404

---

### A-12 Face Verification

**Endpoint**: `POST /api/gambar/cek`  
**Authentication**: None required (parity)  
**Content-Type**: `multipart/form-data`

#### Request Body

| Field | Type |
|---|---|
| `id_user` | integer |
| `upload` | file |

#### File Constraints

Same as A-11: types jpg/png/jpeg, max 10,000 KB, max 4,200×4,200 px, path `gambar/user/`.

#### Processing

1. Query `user WHERE id_user = ?` → not found: 404
2. Generate filename, upload to `gambar/user/`
3. Insert `pencocokan`: `{id_user, nama_photo: stem, gambar: filename}` — `score` not set (empty string by default)
4. Query `photo WHERE id_user = ?` → first row → read `gambar`
5. If no photo rows: return 404 with `"status": "nopic"`
6. Call FaceXAPI comparing training photo vs verification photo
7. On cURL error: return 404 `"Sementara tidak dapat melakukan voting!"`
8. On success: return `$json->data->status`

#### FaceXAPI Call Spec

| Property | Value |
|---|---|
| URL | `https://www.facexapi.com/match_faces` |
| Method | POST |
| Header | `User_id: 603f05b94e6c5e6c15c171e7` |
| Field `img_1` | `http://facevoting.xyz/gambar/user/{training_photo}` |
| Field `img_2` | `http://facevoting.xyz/gambar/user/{verification_photo}` |

> Base URL is hardcoded as `http://facevoting.xyz`. Parity required.

#### Response — Match Result (HTTP 200)

```json
{
  "status": "success",
  "message": "<value of json->data->status from FaceXAPI>"
}
```

#### Response — No Training Photo (HTTP 404)

```json
{
  "status": "nopic",
  "message": "Anda belum melakukan perekaman!"
}
```

#### Response — FaceXAPI Unavailable (HTTP 404)

```json
{
  "status": "failed",
  "message": "Sementara tidak dapat melakukan voting!"
}
```

#### Response — Upload Failure (HTTP 404)

```json
{
  "status": "failed",
  "message": "{upload error text}"
}
```

#### Acceptance Criteria

- [ ] `pencocokan` row inserted with empty score before API call
- [ ] Training photo is the first row returned by `WHERE id_user = ?` (no ordering)
- [ ] FaceXAPI `User_id` header is exactly `603f05b94e6c5e6c15c171e7`
- [ ] Image URLs use hardcoded `http://facevoting.xyz` domain
- [ ] Returns `json->data->status` string (not a numeric score)
- [ ] `"nopic"` status string for missing training photo

---

### A-13 Submit Vote

**Endpoint**: `POST /api/suara/vote`  
**Authentication**: None required (parity)

#### Request Body

| Field | Type |
|---|---|
| `id_user` | integer |
| `id_kategori` | integer |
| `id_paslon` | integer |

#### Processing (exact sequence)

1. Query `paslon WHERE id_paslon = ?` → read `perolehan`
2. Increment `perolehan` by 1
3. Update `paslon SET perolehan = ? WHERE id_paslon = ?`
4. Insert `voting`: `{id_user, id_kategori, id_paslon, tanggal_voting: date('Y-m-d H:i:s')}`
5. Update `otorisasi_pemilih SET status = 2 WHERE id_user = ?` (no `id_kategori` filter)

> No authorization check before voting.  
> No election open/close check.  
> `WHERE id_user` only — all authorizations for the user across all categories are set to status=2.  
> All three of these behaviours are existing and must be preserved for parity.

#### Response — Success (HTTP 200)

```json
{
  "status": "berhasil",
  "message": "Berhasil Voting"
}
```

#### Acceptance Criteria

- [ ] `otorisasi_pemilih` update uses `WHERE id_user` only (no `id_kategori`)
- [ ] No prior authorization check
- [ ] No election status check
- [ ] Exact 2-field success response
- [ ] HTTP 200 always (no error handling in CI3 implementation)

---

### A-14 Voting History

**Endpoint**: `GET /api/suara/tampil/{id_user}`  
**Authentication**: None required (parity)

#### Processing

Query: `SELECT * FROM voting JOIN detail_user ON detail_user.id_user = voting.id_user JOIN kategori ON kategori.id_kategori = voting.id_kategori WHERE voting.id_user = ?`

#### Response — Success (HTTP 200)

```json
{
  "status": "berhasil",
  "result": [
    {
      "id_voting": 1,
      "id_user": 10,
      "id_kategori": 8,
      "id_paslon": 1,
      "tanggal_voting": "2021-07-20 10:00:00",
      "id_detail": 10,
      "nama": "Alexsander Hendra Wijaya",
      "identitas": "123456",
      "nama_kategori": "BEM",
      "logo_kategori": "bem_logo.png",
      "status_kategori": 2
    }
  ],
  "message": "Data berhasil didapatkan"
}
```

#### Response — No History (HTTP 404)

```json
{
  "status": "gagal",
  "message": "Silahkan relogin!"
}
```

> The 404 message "Silahkan relogin!" is unrelated to the actual condition (no votes found). Parity required.

#### Acceptance Criteria

- [ ] `id_user` is a URL segment, not query string
- [ ] JOIN of `voting + detail_user + kategori`
- [ ] Exact error message "Silahkan relogin!" on empty

---

### A-15 Vote Counts by Category

**Endpoint**: `GET /api/suara/perolehan?id_kategori={id}`  
**Authentication**: None

#### Processing

Query: `SELECT * FROM paslon WHERE id_kategori = ?`  
Returns `perolehan` column from the `paslon` table (denormalized counter — not a COUNT from `voting`).

#### Response — Always HTTP 200

```json
{
  "status": "berhasil",
  "result": [
    {
      "id_paslon": 2,
      "id_kategori": 9,
      "judul_paslon": "Paslon 1",
      "ketua_paslon": "Rasmus Lerdorf",
      "wakil_paslon": "Brendan Eich",
      "photo1_paslon": "rasmus.jpg",
      "photo2_paslon": "brendan.jpg",
      "perolehan": 1
    }
  ],
  "message": "Data berhasil didapatkan"
}
```

> Always returns HTTP 200, even if `result` is empty. This is existing behaviour.

#### Acceptance Criteria

- [ ] Source is `paslon.perolehan`, not `COUNT(voting.*)`
- [ ] Always HTTP 200
- [ ] `result` may be an empty array

---

## 6. UI Behaviour Parity

### Page Titles

Every admin page must have the exact title string:

| Page | `<title>` value |
|---|---|
| Login | `Login | FaceVoting Versi 1.0` |
| All other pages | `Dashboard | FaceVoting Versi 1.0` |

### Flash Message Keys

Flash messages are stored under specific session keys. The view reads these keys:

| Key | Colour | Used by |
|---|---|---|
| `pesan` | Red (`alert-danger`) | Login error, delete actions |
| `pesan2` | Green (`alert-success`) | Login credential error (uses alert-danger), voter activation |
| `message` | Green (`alert-success`) | Voter authorization |
| `message1` | Red (`alert-danger`) | Form validation errors |
| `message2` | Green or Red | Category, candidate, setting actions |

> `pesan2` on the login page is used for credential errors with `alert-danger` styling despite the key name. All keys must work as in CI3.

### Flash Message HTML

All flash messages are stored as complete HTML strings and rendered with `{!! !!}` (raw output). The exact HTML must be produced:

```html
<!-- Success template -->
<div class="alert alert-success" role="alert">{message text}</div>

<!-- Danger template -->
<div class="alert alert-danger" role="alert">{message text}</div>

<!-- Special: photo training success (includes inner spans) -->
<div class="alert alert-success" role="alert">
  Berhasil, jumlah photo tersimpan ke dalam album 
  <span class="text-warning">{album_name}</span> adalah 
  <span class="text-warning">{image_count}</span> gambar 
</div>
```

### Redirect Targets (Case-Sensitive)

CI3 URLs are case-sensitive on some servers. Redirect targets must match exactly:

| After action | Redirect to |
|---|---|
| Successful login | `/Member` |
| Logout | `/Login` |
| Failed login (credential) | `/Login` |
| Voter activated | `/User` |
| Voter deleted | `/User` |
| Category created | `/Kategori` |
| Category opened/closed | `/Kategori` |
| Category upload error | `/Kategori/tambah` |
| Voter authorized | `/Kategori/detail/{id}` |
| Candidate created | `/Paslon` |
| Candidate deleted | `/Paslon` |
| Photo trained | `/Photo` |
| Photo deleted | `/Photo` |
| Password changed | `/Setting` |

### Admin Panel Layout

The admin panel uses AdminLTE 3.0.5. The following layout structure must be preserved:

**Sidebar navigation items** (exact labels and targets):

| Label | Target |
|---|---|
| Dashboard | `/member` |
| Data User | `/user` |
| Album | `/album` |
| Photo | `/photo` |
| Kategori | `/kategori` |
| Paslon | `/paslon` |
| Hasil | `/hasil` |
| Setting | `/setting` |

**Top navbar**: Brand name "FaceVoting" (or equivalent). Logout link at right → `GET /member/logout`.

### Form Behaviour

- All forms re-populate with `old()` values after validation failure (existing CI3 behaviour via `set_value()`)
- Category creation form: on GET, file input is empty
- Candidate creation form: category dropdown populated only with `status_kategori = 1` categories
- All form action URLs must match their CI3 equivalents

---

## 7. Database Parity Requirements

The Laravel database must be schema-equivalent to the CI3 `facevoting.sql` dump. Every table, column, type, constraint, and seed row must be reproduced.

### Tables

| Table name | Must exist | Seed data required |
|---|---|---|
| `admin` | Yes | Yes — 1 row |
| `token` | Yes | No — managed at runtime |
| `user` | Yes | Yes — 1 row |
| `detail_user` | Yes | Yes — 1 row |
| `album` | Yes | Yes — 1 row (id=3) |
| `photo` | Yes | No |
| `pencocokan` | Yes | No |
| `kategori` | Yes | Yes — 3 rows |
| `paslon` | Yes | Yes — 3 rows |
| `detail_paslon` | Yes | Yes — 3 rows |
| `otorisasi_pemilih` | Yes | Yes — 2 rows |
| `voting` | Yes | No |
| `tbjurusan` | Yes | Yes — 2 rows |

> `personal_access_tokens` (Sanctum) may be added as a Laravel internal table. No other tables may be added.

### Column Exact Specifications

All column names, types, sizes, nullability, and defaults must match:

#### `admin`
| Column | Type | Null | Default | Extra |
|---|---|---|---|---|
| `id_admin` | int(11) | NO | — | AUTO_INCREMENT, PK |
| `username` | varchar(30) | NO | — | |
| `password` | varchar(255) | NO | — | |
| `status` | int(11) | NO | — | |

#### `token`
| Column | Type | Null | Default | Extra |
|---|---|---|---|---|
| `id_token` | int(11) | NO | — | AUTO_INCREMENT, PK |
| `id_admin` | int(11) | NO | — | FK |
| `token` | varchar(100) | NO | — | |
| `time` | int(11) | NO | — | |

#### `user`
| Column | Type | Null | Default | Extra |
|---|---|---|---|---|
| `id_user` | int(11) | NO | — | AUTO_INCREMENT, PK |
| `email` | varchar(100) | NO | — | |
| `password` | varchar(100) | NO | — | |
| `token_firebase` | varchar(255) | YES | NULL | |
| `token_login` | varchar(100) | YES | NULL | |
| `entry_id` | varchar(255) | NO | — | |
| `status` | tinyint(4) | NO | — | |

#### `detail_user`
| Column | Type | Null | Default | Extra |
|---|---|---|---|---|
| `id_detail` | int(11) | NO | — | AUTO_INCREMENT, PK |
| `id_user` | int(11) | NO | — | FK (CASCADE DELETE) |
| `nama` | varchar(100) | NO | — | |
| `identitas` | varchar(50) | NO | — | |

#### `album`
| Column | Type | Null | Default | Extra |
|---|---|---|---|---|
| `id_album` | int(11) | NO | — | AUTO_INCREMENT, PK |
| `nama_album` | varchar(255) | NO | — | |
| `kode_album` | varchar(255) | NO | — | |

#### `photo`
| Column | Type | Null | Default | Extra |
|---|---|---|---|---|
| `id_photo` | int(11) | NO | — | AUTO_INCREMENT, PK |
| `id_album` | int(11) | NO | — | (no FK constraint) |
| `id_user` | int(11) | NO | — | FK (CASCADE DELETE) |
| `entry_id` | varchar(100) | NO | — | |
| `nama_photo` | varchar(100) | NO | — | |
| `gambar` | varchar(128) | NO | — | |
| `status_train` | int(11) | NO | — | |

#### `pencocokan`
| Column | Type | Null | Default | Extra |
|---|---|---|---|---|
| `id_pencocokan` | int(11) | NO | — | AUTO_INCREMENT, PK |
| `id_user` | int(11) | NO | — | (no FK constraint) |
| `nama_photo` | varchar(100) | NO | — | |
| `gambar` | varchar(128) | NO | — | |
| `score` | varchar(10) | NO | — | |

#### `kategori`
| Column | Type | Null | Default | Extra |
|---|---|---|---|---|
| `id_kategori` | int(11) | NO | — | AUTO_INCREMENT, PK |
| `nama_kategori` | varchar(80) | NO | — | |
| `logo_kategori` | varchar(100) | NO | — | |
| `status_kategori` | int(11) | NO | — | COMMENT preserved |

#### `paslon`
| Column | Type | Null | Default | Extra |
|---|---|---|---|---|
| `id_paslon` | int(11) | NO | — | AUTO_INCREMENT, PK |
| `id_kategori` | int(11) | NO | — | FK (RESTRICT) |
| `judul_paslon` | varchar(20) | NO | — | |
| `ketua_paslon` | varchar(50) | NO | — | |
| `wakil_paslon` | varchar(50) | NO | — | |
| `photo1_paslon` | varchar(100) | YES | NULL | |
| `photo2_paslon` | varchar(100) | YES | NULL | |
| `perolehan` | int(11) | YES | NULL | |

#### `detail_paslon`
| Column | Type | Null | Default | Extra |
|---|---|---|---|---|
| `id_detailpaslon` | int(11) | NO | — | AUTO_INCREMENT, PK |
| `id_paslon` | int(11) | NO | — | FK (CASCADE DELETE) |
| `visi_misi` | text | NO | — | |
| `profil_catum` | text | NO | — | |
| `profil_cawatum` | text | NO | — | |

#### `otorisasi_pemilih`
| Column | Type | Null | Default | Extra |
|---|---|---|---|---|
| `id_otorisasi` | int(11) | NO | — | AUTO_INCREMENT, PK |
| `id_user` | int(11) | YES | NULL | FK (CASCADE DELETE) |
| `id_kategori` | int(11) | YES | NULL | FK (CASCADE DELETE) |
| `status` | int(11) | YES | NULL | |

#### `voting`
| Column | Type | Null | Default | Extra |
|---|---|---|---|---|
| `id_voting` | int(11) | NO | — | AUTO_INCREMENT, PK |
| `id_user` | int(11) | NO | — | FK (CASCADE DELETE) |
| `id_kategori` | int(11) | NO | — | FK (CASCADE DELETE) |
| `id_paslon` | int(11) | NO | — | FK (CASCADE DELETE) |
| `tanggal_voting` | datetime | YES | NULL | |

#### `tbjurusan`
| Column | Type | Null | Default |
|---|---|---|---|
| `id_jurusan` | int(11) | NO | — |
| `kode_jurusan` | varchar(50) | NO | — |
| `nama_jurusan` | varchar(100) | NO | — |

### AUTO_INCREMENT Starting Values

Must match CI3 dump to preserve ID space:

| Table | AUTO_INCREMENT |
|---|---|
| `admin` | 2 |
| `album` | 4 |
| `detail_paslon` | 7 |
| `detail_user` | 12 |
| `kategori` | 11 |
| `otorisasi_pemilih` | 11 |
| `paslon` | 6 |
| `pencocokan` | 4 |
| `photo` | 32 |
| `tbjurusan` | 3 |
| `token` | 26 |
| `user` | 12 |
| `voting` | 13 |

### Foreign Key Constraints

Must be reproduced exactly, including the intentional asymmetry:

| Constraint name | Table | Column | References | On Delete | On Update |
|---|---|---|---|---|---|
| `token_ibfk_1` | `token` | `id_admin` | `admin(id_admin)` | CASCADE | NO ACTION |
| `detail_user_ibfk_1` | `detail_user` | `id_user` | `user(id_user)` | CASCADE | NO ACTION |
| `photo_ibfk_1` | `photo` | `id_user` | `user(id_user)` | CASCADE | NO ACTION |
| `otorisasi_pemilih_ibfk_1` | `otorisasi_pemilih` | `id_user` | `user(id_user)` | CASCADE | NO ACTION |
| `otorisasi_pemilih_ibfk_2` | `otorisasi_pemilih` | `id_kategori` | `kategori(id_kategori)` | CASCADE | NO ACTION |
| `paslon_ibfk_1` | `paslon` | `id_kategori` | `kategori(id_kategori)` | RESTRICT | RESTRICT |
| `detail_paslon_ibfk_1` | `detail_paslon` | `id_paslon` | `paslon(id_paslon)` | CASCADE | NO ACTION |
| `voting_ibfk_1` | `voting` | `id_user` | `user(id_user)` | CASCADE | NO ACTION |
| `voting_ibfk_2` | `voting` | `id_kategori` | `kategori(id_kategori)` | CASCADE | NO ACTION |
| `voting_ibfk_3` | `voting` | `id_paslon` | `paslon(id_paslon)` | CASCADE | NO ACTION |

> `paslon_ibfk_1` uses RESTRICT (not CASCADE) — this is intentional and must be preserved. `photo.id_album` has no FK constraint — this must also be preserved.

### Indexes

| Table | Index name | Type | Columns |
|---|---|---|---|
| `admin` | PRIMARY | PRIMARY KEY | `id_admin` |
| `album` | PRIMARY | PRIMARY KEY | `id_album` |
| `detail_paslon` | PRIMARY | PRIMARY KEY | `id_detailpaslon` |
| `detail_paslon` | `id_paslon` | INDEX | `id_paslon` |
| `detail_user` | PRIMARY | PRIMARY KEY | `id_detail` |
| `detail_user` | `id_user` | INDEX | `id_user` |
| `kategori` | PRIMARY | PRIMARY KEY | `id_kategori` |
| `otorisasi_pemilih` | PRIMARY | PRIMARY KEY | `id_otorisasi` |
| `otorisasi_pemilih` | `id_user` | INDEX | `id_user` |
| `otorisasi_pemilih` | `id_kategori` | INDEX | `id_kategori` |
| `paslon` | PRIMARY | PRIMARY KEY | `id_paslon` |
| `paslon` | `id_kategori` | INDEX | `id_kategori` |
| `pencocokan` | PRIMARY | PRIMARY KEY | `id_pencocokan` |
| `photo` | PRIMARY | PRIMARY KEY | `id_photo` |
| `photo` | `id_user` | INDEX | `id_user` |
| `tbjurusan` | PRIMARY | PRIMARY KEY | `id_jurusan` |
| `token` | PRIMARY | PRIMARY KEY | `id_token` |
| `token` | `token_ibfk_1` | INDEX | `id_admin` |
| `user` | PRIMARY | PRIMARY KEY | `id_user` |
| `voting` | PRIMARY | PRIMARY KEY | `id_voting` |
| `voting` | `id_user` | INDEX | `id_user` |
| `voting` | `id_kategori` | INDEX | `id_kategori` |
| `voting` | `id_paslon` | INDEX | `id_paslon` |

### Charset and Engine

| Setting | Value |
|---|---|
| Engine | InnoDB |
| Default charset | utf8mb4 |

### Seed Data

Must be present after `php artisan db:seed`:

**`admin`**: `{id_admin:1, username:'admin', password:'$2y$10$lqkCunzVQwEvp7WPZWuQlOLHTDiq1JQ9GpyTNfMaW3bFwaAerLEAW', status:1}`

**`album`**: `{id_album:3, nama_album:'Facevoting2021', kode_album:'859d15e7156ef8128f921671b6a3d941a4a7f686b4e762bbc679c4809bdebb19'}`

**`user`**: `{id_user:10, email:'alexistdev@gmail.com', password:'325339', token_firebase:'a1231231', token_login:'ada1231', entry_id:'1231231', status:1}`

**`detail_user`**: `{id_detail:10, id_user:10, nama:'Alexsander Hendra Wijaya', identitas:'123456'}`

**`kategori`**: 3 rows — BEM (id:8, status:2), HIMKRIS (id:9, status:2), UKM MUSIC (id:10, status:1)

**`paslon`**: 3 rows — id:1 in kategori:8, id:2 and id:3 in kategori:9

**`detail_paslon`**: 3 rows — id:2,3,4 matching paslon:1,2,3

**`otorisasi_pemilih`**: 2 rows — user:10 in kategori:8 (status:1) and kategori:9 (status:1)

**`tbjurusan`**: 2 rows — sistem informasi, teknik informatika

---

## 8. Validation Parity

### Exact Validation Error Messages

All messages must be reproduced verbatim, including the inconsistency in F-04:

| Field | Rule | Message (Bahasa Indonesia) |
|---|---|---|
| `username` (login) | required | `Username harus diisi!` |
| `password` (login) | required | `Password harus diisi!` |
| `captcha` (login) | required | `Captcha harus diisi!` |
| `captcha` (login) | callback | `Captcha yang anda masukkan salah!` |
| `password1` (setting) | required | `Password harus diisi!` |
| `password1` (setting) | min_length[4] | `Panjang karakter Password minimal 6 karakter!` |
| `password1` (setting) | max_length[16] | `Panjang karakter Password maksimal 16 karakter!` |
| `password2` (setting) | required | `Password harus diisi!` |
| `password2` (setting) | matches | `Password tidak sama!` |
| `namaKategori` (category) | required | `Nama Kategori harus diisi!` |
| `namaKategori` (candidate) | required | `Nama Kategori harus diisi!` |
| `judulPaslon` (candidate) | required | `Judul Paslon harus diisi!` |
| `ketuaPaslon` (candidate) | required | `Nama Calon Ketua harus diisi!` |
| `profilCatum` (candidate) | required | `Profil Calon Ketua harus diisi!` |
| `wakilPaslon` (candidate) | required | `Nama Calon Wakil harus diisi!` |
| `profilCawatum` (candidate) | required | `Profil Calon Wakil harus diisi!` |
| `visimisi` (candidate) | required | `Visi dan Misi harus diisi!` |

### Error Display Format

CI3 wraps all validation errors in configured delimiters:

```html
<div class="alert alert-danger" role="alert">{error message}</div>
```

Each field error is wrapped in its own `<div>`. Multiple errors stack vertically.

---

## 9. File Handling Parity

### Upload Paths

| Context | CI3 Path | Laravel Path |
|---|---|---|
| Category logos | `./gambar/` | `storage/app/public/images/` |
| Candidate photos | `./gambar/` | `storage/app/public/images/` |
| Training photos | `gambar/user/` | `storage/app/public/images/users/` |
| Verification photos | `gambar/user/` | `storage/app/public/images/users/` |
| Captcha images | `captcha/` | `storage/app/public/captcha/` |

> The Laravel paths must be symlinked to `public/` so that existing hardcoded URLs using `http://facevoting.xyz/gambar/` continue to resolve. A `storage:link` symlink must map to a compatible URL structure.

### Filename Generation

| Context | Algorithm |
|---|---|
| Category logo | `substr(sha1(time()), 0, 10)` + original extension |
| Candidate photos | CI upload library auto-naming (original filename) |
| Training/verification photos | `substr(sha1(time()), 1, 10)` + original extension |

### Upload Constraints

| Context | Allowed Types | Max Size | Max Dimensions |
|---|---|---|---|
| Category logo | jpg, jpeg, png, gif | 2,424 KB | None |
| Candidate photos | jpg, jpeg, png, gif | 2,424 KB | None |
| Training photo | jpg, png, jpeg | 10,000 KB | 4,200 × 4,200 px |
| Verification photo | jpg, png, jpeg | 10,000 KB | 4,200 × 4,200 px |

### File Deletion

| Context | Behaviour |
|---|---|
| Voter deleted | Files in `gambar/user/` **not** deleted |
| Training photo deleted (F-19) | File **is** deleted from `gambar/user/` |
| Candidate deleted | Files in `gambar/` **not** deleted |

---

## 10. External Integration Parity

### Lambda Face Recognition API

All calls must use the exact same headers, parameters, and URL structure as CI3.

| Property | Value |
|---|---|
| API key | `932571abf0msh45cf0f3cef74aacp19e151jsn33e9949a1974` |
| Host header | `lambda-face-recognition.p.rapidapi.com` |
| Timeout | 30 seconds |
| Max redirects | 10 |
| Follow redirects | Yes |

**Endpoint: Train photo**

```
POST https://lambda-face-recognition.p.rapidapi.com/album_train
Content-Type: application/x-www-form-urlencoded

urls={photo_url}&album={album_name}&albumkey={album_key}&entryid={entry_id}
```

Photo URL format (hardcoded domain): `http://facevoting.xyz/gambar/user/{gambar}`

**Endpoint: View album**

```
GET https://lambda-face-recognition.p.rapidapi.com/album?album={name}&albumkey={key}
```

**Endpoint: Rebuild album**

```
GET https://lambda-face-recognition.p.rapidapi.com/album_rebuild?album={name}&albumkey={key}
```

### FaceXAPI

| Property | Value |
|---|---|
| User ID | `603f05b94e6c5e6c15c171e7` |
| Endpoint | `https://www.facexapi.com/match_faces` |
| Method | POST |
| Header | `User_id: 603f05b94e6c5e6c15c171e7` |

Request fields: `img_1`, `img_2` — both hardcoded domain `http://facevoting.xyz/gambar/user/{filename}`

Response field extracted: `json->data->status`

### Error Handling for External Services

| Condition | Behaviour |
|---|---|
| Lambda cURL error | Return `null`; show danger flash or 404 API response |
| FaceXAPI cURL error | Return `null`; API responds with "Sementara tidak dapat melakukan voting!" |
| Lambda success | JSON-decode and use specific fields |
| FaceXAPI success | Extract `data.status` string |

---

## 11. Authentication Behaviour Parity

### Admin Web Session

| Behaviour | Requirement |
|---|---|
| Session key `is_login_in` | Must be set to `true` (boolean) on login |
| Session key `token` | Must hold the SHA1 token string |
| Session key `id_admin` | Must hold `1` (integer) |
| Token stored in `token` table | Must exist as a DB row during authenticated session |
| Token NOT deleted on logout | Parity with CI3 — deleted only on next successful login |
| Session destroyed on logout | `sess_destroy()` equivalent |
| Redirect after login | `GET /Member` (capital M) |
| Redirect after logout | `GET /Login` (capital L) |

### Mobile API Token

| Behaviour | Requirement |
|---|---|
| Token field name | `token_login` in `user` table |
| Token stored in DB | `user.token_login` updated on every login/refresh |
| Token format | 10-character alphanumeric (uppercase + digits) |
| Token generation | `substr(str_shuffle('0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 1, 10)` |
| Token validation | Not performed server-side on any endpoint (parity) |
| Token returned in registration | Yes — returned immediately, even for status=2 |
| Token returned in login | Yes |
| Token returned in refresh | Yes |
| Token returned in status check | Yes — exposed unauthenticated (parity) |

### Password Handling

| Context | Algorithm |
|---|---|
| Admin password verification | `password_verify($input, $bcrypt_hash)` |
| Admin password storage | `password_hash($input, PASSWORD_BCRYPT)` |
| Voter password storage | `sha1($input)` |
| Voter password verification | Direct compare: `sha1($input) === db_value` |

---

## 12. Non-Functional Requirements

### Performance

| Requirement | Target |
|---|---|
| Admin page load time | ≤ 2 seconds (local server) |
| API response time (no external calls) | ≤ 200 ms |
| API response time (with Lambda/FaceXAPI) | ≤ 35 seconds (matches 30s curl timeout) |
| Concurrent admin users | 1 (single admin design) |
| Concurrent API users | No explicit limit in CI3; Laravel default |

### Compatibility

| Requirement | Value |
|---|---|
| PHP version | 8.2+ |
| MySQL/MariaDB | 5.7+ / 10.3+ |
| Laravel version | 12.x |
| AdminLTE version | 3.0.5 (same as CI3) |
| Mobile API contract | 100% backward-compatible — zero mobile client changes |

### File System

| Requirement | Value |
|---|---|
| `gambar/user/` equivalent must be web-accessible | Yes — for Lambda and FaceXAPI URL references |
| Upload directories must be writable | Yes |
| Captcha directory must be writable | Yes |

### Configuration

| Requirement | Value |
|---|---|
| API keys in source code | Preserved as-is for parity (not moved to .env) |
| Base URL | Must be configurable |
| Database connection | Configurable via `.env` |
| Session lifetime | 7,200 seconds (120 minutes) |

---

## 13. Acceptance Criteria

### Admin Panel Acceptance Tests

| ID | Test | Pass Condition |
|---|---|---|
| AC-01 | Login with correct credentials + CAPTCHA | Session created, redirect to `/Member` |
| AC-02 | Login with wrong password | Danger flash, stay on `/Login` |
| AC-03 | Login with wrong CAPTCHA | CAPTCHA error message, new CAPTCHA generated |
| AC-04 | Logout | Session destroyed, redirect `/Login`, token row NOT deleted |
| AC-05 | Access `/member` without session | Redirect to `/login` |
| AC-06 | Change admin password | bcrypt hash saved, success flash, redirect `/Setting` |
| AC-07 | Change password, fields don't match | "Password tidak sama!" error |
| AC-08 | Activate pending voter | status=1, success flash, redirect `/User` |
| AC-09 | Activate already-active voter | Silent redirect `/User`, no flash |
| AC-10 | Delete voter | Cascade delete, danger flash, redirect `/User` |
| AC-11 | Create category with valid data + logo | Row inserted status=1, redirect `/Kategori` |
| AC-12 | Create category without name | Validation error flash, no upload attempted |
| AC-13 | Create category with oversized logo | Upload error flash, redirect `/Kategori/tambah` |
| AC-14 | Open election | status=2, green flash, redirect `/Kategori` |
| AC-15 | Close election | status=1, red flash, redirect `/Kategori` |
| AC-16 | Add voter to category | Authorization row inserted, redirect `/Kategori/detail/{id}` |
| AC-17 | Add same voter to category twice | Two rows inserted (no duplicate check) |
| AC-18 | Create candidate pair with all fields + photos | Two rows (paslon + detail), redirect `/Paslon` |
| AC-19 | Create candidate pair without photos | Photo columns null, redirect `/Paslon` |
| AC-20 | Delete candidate pair | Cascade to detail_paslon, redirect `/Paslon` |
| AC-21 | Train photo (successful Lambda response) | Success flash with album name + count |
| AC-22 | Train photo (Lambda unreachable) | Danger flash "Perekaman data Gagal!" |
| AC-23 | Delete training photo | DB row deleted, file deleted from disk |
| AC-24 | View album | Raw JSON from Lambda displayed |
| AC-25 | Rebuild album | Raw JSON from Lambda displayed |
| AC-26 | View results | All candidates ordered by perolehan DESC |

### API Acceptance Tests

| ID | Test | Pass Condition |
|---|---|---|
| AC-27 | Register new voter | HTTP 200, 5 response fields, status=2 |
| AC-28 | Register with duplicate email | HTTP 404, exact error message |
| AC-29 | Login with valid credentials | HTTP 200, 6 response fields, new token |
| AC-30 | Login with wrong credentials | HTTP 404, exact message |
| AC-31 | Token refresh with valid id_user | HTTP 200, 4 response fields |
| AC-32 | Account status check | HTTP 200, token_login included in response |
| AC-33 | Get account profile | HTTP 200, 3 fields (email, nama, identitas) |
| AC-34 | Update profile with new password | SHA1 hash stored, HTTP 200 |
| AC-35 | List authorized categories (authorized user) | HTTP 200, otorisasi status=1 only |
| AC-36 | List authorized categories (no authorizations) | HTTP 404, result:[] |
| AC-37 | List all open categories | HTTP 200, only status_kategori=2 |
| AC-38 | List candidates by category | HTTP 200, detail_paslon fields merged |
| AC-39 | Get candidate detail | HTTP 200, query root from detail_paslon |
| AC-40 | Upload training photo | HTTP 200, status_train=2 in DB, id_album=3 |
| AC-41 | Upload oversized training photo | HTTP 404, strip_tags error message |
| AC-42 | Face verification — match | HTTP 200, FaceXAPI status string |
| AC-43 | Face verification — no training photo | HTTP 404, status="nopic" |
| AC-44 | Face verification — FaceXAPI unavailable | HTTP 404, exact Indonesian message |
| AC-45 | Submit vote | HTTP 200, perolehan++, voting row, otorisasi ALL updated |
| AC-46 | Get voting history | HTTP 200, JOIN of 3 tables |
| AC-47 | Get vote counts | HTTP 200 always, from paslon.perolehan |

### Database Acceptance Tests

| ID | Test | Pass Condition |
|---|---|---|
| AC-48 | All 13 tables exist | Schema matches exactly |
| AC-49 | All column types match | INT, VARCHAR sizes, TINYINT, TEXT, DATETIME exact |
| AC-50 | All FK constraints match | CASCADE/RESTRICT actions as specified |
| AC-51 | `photo.id_album` has no FK | Confirmed by information_schema |
| AC-52 | `pencocokan.id_user` has no FK | Confirmed by information_schema |
| AC-53 | `paslon.id_kategori` FK is RESTRICT | Deleting a category with candidates fails |
| AC-54 | AUTO_INCREMENT values match | All 13 tables start at specified values |
| AC-55 | Seed data present | All seed rows from SQL dump exist |
| AC-56 | `tbjurusan` table exists | Present even though unused |

---

## 14. Out of Scope

The following are explicitly excluded from this project. Any item in this list must not be implemented.

### Excluded Features

| Item | Reason |
|---|---|
| New admin roles or multi-admin support | Not in CI3 |
| API authentication via Sanctum tokens replacing `token_login` | Would break mobile client |
| Email verification on registration | Not in CI3 |
| Password reset flow | Not in CI3 |
| Email notifications (activation, confirmation) | Not in CI3 |
| Pagination on any list | Not in CI3 |
| Search or filtering | Not in CI3 |
| Soft deletes | Not in CI3 |
| Audit logging | Not in CI3 |
| Rate limiting on API | Not in CI3 |
| HTTPS enforcement | Not enforced in CI3 |
| CORS headers | Not configured in CI3 |
| API versioning | Not in CI3 |
| New API endpoints | Zero |
| New database tables (beyond Sanctum personal_access_tokens) | Zero |
| New database columns | Zero |
| UI redesign | Not permitted |
| AdminLTE version change | Must remain 3.0.5 |
| Mobile push notifications (FCM) | Token stored, not sent — parity |
| `Testing.php` controller | Do not port — it is a development artifact |
| `tbjurusan` integration | Table exists but remains unused — parity |

### Bug Fixes Excluded (Must Be Preserved for Parity)

The following known bugs in CI3 must be reproduced exactly in Laravel to maintain behavioural parity with the mobile client and admin panel:

| Bug | Parity Requirement |
|---|---|
| SHA1 voter passwords | Must store passwords as SHA1 |
| `token_login` not validated server-side | API endpoints remain unauthenticated by server |
| `perbaharui_data_otorisasi` uses `WHERE id_user` only | All authorization rows updated on vote |
| `pencocokan.score` stored as empty string | Score column not populated |
| `status_train = 2` on photo upload | Value stored as `2`, not `0` |
| `Hasil::index()` renders despite token mismatch | Results page accessible without valid token |
| No duplicate check on voter authorization | Two calls create two rows |
| Token row not deleted on logout | Token table not cleared on logout |
| `perolehan` counter via read-modify-write | Same non-atomic pattern |
| FaceXAPI + Lambda use hardcoded `http://facevoting.xyz` | Hardcoded domain preserved |
| `cekstatus_get` returns `token_login` | Token exposed in unauthenticated response |
| Pending users (status=2) can receive tokens | No status check on login |

> If a future phase requires bug fixes, a separate PRD must be issued. This document governs parity only.
