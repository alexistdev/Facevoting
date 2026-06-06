# FaceVoting — System Analysis

**Framework**: CodeIgniter 3.1.11  
**Database**: MySQL / MariaDB (`evoting`)  
**UI**: AdminLTE 3.0.5 (admin panel), REST JSON (mobile API)  
**Generated**: 2026-06-06

---

## Table of Contents

1. [System Overview](#1-system-overview)
2. [Business Processes](#2-business-processes)
3. [User Roles](#3-user-roles)
4. [Database Schema](#4-database-schema)
5. [Controllers](#5-controllers)
6. [Models](#6-models)
7. [Libraries](#7-libraries)
8. [Helpers](#8-helpers)
9. [Cron Jobs](#9-cron-jobs)
10. [APIs](#10-apis)
11. [Authentication Flow](#11-authentication-flow)
12. [Billing Flow](#12-billing-flow)
13. [Payment Flow](#13-payment-flow)
14. [Reports](#14-reports)
15. [External Integrations](#15-external-integrations)
16. [Frontend Pages](#16-frontend-pages)
17. [Navigation Structure](#17-navigation-structure)

---

## 1. System Overview

FaceVoting is a **biometric e-voting platform** designed for institutional elections (student government, campus organizations, etc.). It combines a **web-based admin panel** with a **REST API** consumed by a mobile application.

The core differentiator is **face recognition gating**: voters must pass a facial identity check before their vote is accepted. The system stores a training photo per voter and, at voting time, compares a live capture against the stored photo using two external AI APIs.

### Component Map

```
┌─────────────────────────────────────────────────────────────┐
│                        FaceVoting                           │
│                                                             │
│  ┌──────────────────┐          ┌───────────────────────┐   │
│  │   Admin Panel    │          │     Mobile REST API   │   │
│  │  (Web Browser)   │          │   (Flutter / native)  │   │
│  │                  │          │                       │   │
│  │  CodeIgniter     │          │  CodeIgniter +        │   │
│  │  Controllers     │          │  REST Server lib      │   │
│  └────────┬─────────┘          └──────────┬────────────┘   │
│           │                               │                 │
│           └──────────┬────────────────────┘                 │
│                      │                                      │
│              ┌───────▼────────┐                             │
│              │  MySQL DB      │                             │
│              │  (evoting)     │                             │
│              └───────┬────────┘                             │
│                      │                                      │
│         ┌────────────┼──────────────────┐                   │
│         ▼            ▼                  ▼                   │
│  ┌─────────────┐ ┌──────────────┐ ┌──────────────┐         │
│  │  Lambda     │ │  FaceXAPI    │ │  Firebase    │         │
│  │  Face Recog │ │  Face Match  │ │  (Push Notif)│         │
│  └─────────────┘ └──────────────┘ └──────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

### Technology Stack

| Layer | Technology |
|---|---|
| Backend framework | CodeIgniter 3.1.11 |
| REST server | chriskacerguis/codeigniter-restserver ^3.1 |
| Database | MariaDB 10.4 / MySQL (InnoDB, utf8mb4) |
| Admin UI | AdminLTE 3.0.5 |
| Composer | PHP Composer |
| Face training | Lambda Face Recognition (RapidAPI) |
| Face matching | FaceXAPI |
| Push notifications | Firebase Cloud Messaging (token stored, not implemented in backend) |
| File storage | Local filesystem (`gambar/`, `captcha/`) |

---

## 2. Business Processes

### 2.1 Voter Registration & Activation

```
Mobile app user fills registration form
        │
        ▼
POST /api/daftar/tambah
  → stores user + detail_user rows (status = 2 = pending)
        │
        ▼
Admin reviews user list in panel
        │
        ▼
Admin clicks "Aktivasi" → status = 1 (active)
        │
        ▼
User can now log in and vote
```

### 2.2 Face Enrollment

```
Activated user opens mobile app
        │
        ▼
POST /api/gambar/tambah  (upload training photo)
  → saves image to gambar/user/<hash>.jpg
  → inserts photo row (status_train = 0)
  → calls Lambda API POST /album_train
     (associates photo URL with user's entry_id in the album)
  → updates status_train = 1 on success
        │
        ▼
Admin can trigger "Rebuild" to re-index the Lambda album
```

### 2.3 Election Setup

```
Admin creates voting category (Kategori)
  → upload logo, set name → status_kategori = 1 (open for setup)
        │
        ▼
Admin creates candidate pairs (Paslon) for that category
  → set names, upload photos, enter vision/mission
        │
        ▼
Admin authorizes voters per category (otorisasi_pemilih)
  → each record: id_user + id_kategori + status = 1
        │
        ▼
Admin opens voting → status_kategori = 2
```

### 2.4 Voting Process

```
User logs in on mobile → token_login issued
        │
        ▼
GET /api/kategori/tampil  → lists categories user is authorized for
        │
        ▼
POST /api/paslon/tampil   → lists candidates for chosen category
        │
        ▼
POST /api/gambar/cek  (live face capture upload)
  → compares uploaded image vs stored training photo
     via FaceXAPI → returns similarity score
  → if score passes threshold → user proceeds
        │
        ▼
POST /api/suara/vote
  → checks otorisasi_pemilih.status = 1 (authorized, not yet voted)
  → inserts voting record
  → increments paslon.perolehan
  → sets otorisasi_pemilih.status = 2 (voted)
        │
        ▼
Vote recorded — user cannot vote again in same category
```

### 2.5 Results

```
Admin closes voting → status_kategori = 1
        │
        ▼
Admin views /hasil → sorted list of candidates by perolehan
        │
        ▼
GET /api/suara/perolehan → mobile app shows live results
```

---

## 3. User Roles

### 3.1 Administrator (Web Panel)

- Single admin account (stored in `admin` table)
- Default credentials: `admin` / `admin` (bcrypt hash in seed)
- Full access to all management features
- Authenticated via username + password + CAPTCHA
- Session managed with server-side token in `token` table

**Capabilities:**
- Manage voter accounts (activate, delete)
- Create and manage voting categories (open/close)
- Create and manage candidate pairs with photos
- Authorize which voters can vote in which category
- View voting results
- Manage face recognition albums (train, rebuild)
- Change own password

### 3.2 Voter / Member (Mobile API)

- Registered via mobile app
- Must be activated by Admin before login is possible
- Authenticates via email + password, receives `token_login`
- Can only vote in categories they have been authorized for
- Must pass face recognition check before each vote
- One vote per category (enforced by `otorisasi_pemilih.status`)

**Capabilities:**
- Register account
- View open voting categories
- Upload face training photo
- Verify identity via live face capture
- Submit vote
- View voting history
- View results

---

## 4. Database Schema

**Database name**: `evoting`  
**Engine**: InnoDB  
**Charset**: utf8mb4

### Entity-Relationship Overview

```
admin ──────────────── token
                         (session tokens per login)

user ──── detail_user
  │
  ├──── otorisasi_pemilih ──── kategori ──── paslon ──── detail_paslon
  │
  ├──── voting ──────────────── kategori
  │                └─────────── paslon
  │
  └──── photo ──── album

pencocokan ──── user
tbjurusan (unused)
```

---

### Table: `admin`

Stores administrator credentials for web panel login.

| Column | Type | Notes |
|---|---|---|
| `id_admin` | int(11) PK | Auto-increment |
| `username` | varchar(30) | Login username |
| `password` | varchar(255) | PASSWORD_BCRYPT hash |
| `status` | int(11) | 1 = active |

Seed: `admin` / `admin` (bcrypt)

---

### Table: `token`

Server-side session tokens for admin. Invalidated on logout.

| Column | Type | Notes |
|---|---|---|
| `id_token` | int(11) PK | Auto-increment |
| `id_admin` | int(11) FK → admin | |
| `token` | varchar(100) | SHA1 of username+password+microtime |
| `time` | int(11) | Unix timestamp of login |

---

### Table: `user`

Voter accounts created via mobile app registration.

| Column | Type | Notes |
|---|---|---|
| `id_user` | int(11) PK | Auto-increment |
| `email` | varchar(100) | Login email (unique in practice) |
| `password` | varchar(100) | SHA1 hash (weak — see security notes) |
| `token_firebase` | varchar(255) | FCM push notification token |
| `token_login` | varchar(100) | Current session token (10-char alphanum) |
| `entry_id` | varchar(255) | Unique ID used in Lambda face album |
| `status` | tinyint(4) | **1** = active, **2** = pending activation |

---

### Table: `detail_user`

Extended profile for voter (split from `user` for normalization).

| Column | Type | Notes |
|---|---|---|
| `id_detail` | int(11) PK | Auto-increment |
| `id_user` | int(11) FK → user | One-to-one |
| `nama` | varchar(100) | Full name |
| `identitas` | varchar(50) | Identity number (NIM / KTP / etc.) |

---

### Table: `kategori`

Voting election categories (e.g., BEM, HIMKRIS).

| Column | Type | Notes |
|---|---|---|
| `id_kategori` | int(11) PK | Auto-increment |
| `nama_kategori` | varchar(80) | Display name |
| `logo_kategori` | varchar(100) | Filename in `gambar/` |
| `status_kategori` | int(11) | **1** = open/setup, **2** = voting open, **3** = not available |

> Note: The comments in the SQL dump label status 1 as "open" and 2 as "closed", but controller logic uses 2 to open voting (`Buka()`) and 1 to close (`Tutup()`). The API `semua_get()` returns categories where `status_kategori = 2`.

---

### Table: `paslon`

Candidate pairs (pasangan calon) per category.

| Column | Type | Notes |
|---|---|---|
| `id_paslon` | int(11) PK | Auto-increment |
| `id_kategori` | int(11) FK → kategori | |
| `judul_paslon` | varchar(20) | Label (e.g., "Paslon 1") |
| `ketua_paslon` | varchar(50) | Candidate leader name |
| `wakil_paslon` | varchar(50) | Deputy candidate name |
| `photo1_paslon` | varchar(100) | Leader photo filename in `gambar/` |
| `photo2_paslon` | varchar(100) | Deputy photo filename in `gambar/` |
| `perolehan` | int(11) | Vote count (incremented on each vote) |

---

### Table: `detail_paslon`

Extended info for candidate pair.

| Column | Type | Notes |
|---|---|---|
| `id_detailpaslon` | int(11) PK | Auto-increment |
| `id_paslon` | int(11) FK → paslon | One-to-one |
| `visi_misi` | text | Vision and mission statement |
| `profil_catum` | text | Candidate (leader) biography |
| `profil_cawatum` | text | Deputy candidate biography |

---

### Table: `otorisasi_pemilih`

Authorization table: which users may vote in which category.

| Column | Type | Notes |
|---|---|---|
| `id_otorisasi` | int(11) PK | Auto-increment |
| `id_user` | int(11) FK → user | |
| `id_kategori` | int(11) FK → kategori | |
| `status` | int(11) | **1** = authorized (not yet voted), **2** = voted |

This is the primary anti-double-vote guard. Checked before vote submission; updated to 2 after successful vote.

---

### Table: `voting`

Audit log of submitted votes.

| Column | Type | Notes |
|---|---|---|
| `id_voting` | int(11) PK | Auto-increment |
| `id_user` | int(11) FK → user | |
| `id_kategori` | int(11) FK → kategori | |
| `id_paslon` | int(11) FK → paslon | |
| `tanggal_voting` | datetime | Timestamp of vote submission |

---

### Table: `album`

Face recognition album configuration for Lambda API.

| Column | Type | Notes |
|---|---|---|
| `id_album` | int(11) PK | Auto-increment |
| `nama_album` | varchar(255) | Human-readable album name |
| `kode_album` | varchar(255) | Lambda API album identifier (SHA256-like hash) |

Seed: Album `Facevoting2021` with code `859d15e7...`.

---

### Table: `photo`

Training face photos per voter.

| Column | Type | Notes |
|---|---|---|
| `id_photo` | int(11) PK | Auto-increment |
| `id_album` | int(11) FK → album | Album the photo belongs to |
| `id_user` | int(11) FK → user | Owner voter |
| `entry_id` | varchar(100) | Lambda API face entry identifier |
| `nama_photo` | varchar(100) | Filename stem (no extension) |
| `gambar` | varchar(128) | Full filename in `gambar/user/` |
| `status_train` | int(11) | **0** = not trained, **1** = trained in Lambda |

---

### Table: `pencocokan`

Log of face comparison attempts at voting time.

| Column | Type | Notes |
|---|---|---|
| `id_pencocokan` | int(11) PK | Auto-increment |
| `id_user` | int(11) FK → user | |
| `nama_photo` | varchar(100) | Temporary filename stem |
| `gambar` | varchar(128) | Filename of the verification photo |
| `score` | varchar(10) | Similarity score from FaceXAPI |

---

### Table: `tbjurusan` (unused)

Department/major reference table. Present in schema but not referenced by any controller or model in the active codebase.

| Column | Type |
|---|---|
| `id_jurusan` | int(11) PK |
| `kode_jurusan` | varchar(50) |
| `nama_jurusan` | varchar(100) |

---

## 5. Controllers

All controllers live under `application/controllers/`.  
API controllers live under `application/controllers/api/` and extend `REST_Controller`.

### 5.1 Web Admin Controllers

#### `Login.php`

| Method | HTTP | URI | Description |
|---|---|---|---|
| `index()` | GET/POST | `/login` | Render login form and process submission |
| `_create_captcha()` | — | internal | Generate 5-char captcha image to `captcha/` |
| `_check_captcha($id)` | — | internal | Validate captcha against DB |

**Flow**: renders captcha → validates captcha on submit → `password_verify()` against bcrypt hash → generates `sha1(username+password+microtime)` token → stores in `token` table → sets `$_SESSION` → redirects to `/member`.

---

#### `Member.php`

| Method | HTTP | URI | Description |
|---|---|---|---|
| `index()` | GET | `/member` | Admin dashboard (summary counts) |
| `logout()` | GET | `/member/logout` | Destroy session and redirect to login |

Protected by `_unlogin()` guard (checks session + token table).

---

#### `User.php`

| Method | HTTP | URI | Description |
|---|---|---|---|
| `index()` | GET | `/user` | List all voters with status |
| `aktivasi($idx)` | GET | `/user/aktivasi/{id}` | Set user.status = 1 |
| `hapus($idx)` | GET | `/user/hapus/{id}` | Delete user and detail_user rows |

---

#### `Album.php`

| Method | HTTP | URI | Description |
|---|---|---|---|
| `index()` | GET | `/album` | List albums with photo counts (join with detail_user) |

---

#### `Kategori.php`

| Method | HTTP | URI | Description |
|---|---|---|---|
| `index()` | GET | `/kategori` | List all voting categories |
| `detail($idx)` | GET | `/kategori/detail/{id}` | Category detail with candidates and voter list |
| `tambah_pemilih($idKat, $idUser)` | GET | `/kategori/tambah_pemilih/{katId}/{userId}` | Add voter authorization for a category |
| `tambah()` | GET/POST | `/kategori/tambah` | Create new category with logo upload |
| `Tutup($idx)` | GET | `/kategori/Tutup/{id}` | Close voting (status = 1) |
| `Buka($idx)` | GET | `/kategori/Buka/{id}` | Open voting (status = 2) |

Logo upload: max 2424 KB, allowed types jpg/jpeg/png/gif.

---

#### `Paslon.php`

| Method | HTTP | URI | Description |
|---|---|---|---|
| `index()` | GET | `/paslon` | List all candidate pairs |
| `tambah()` | GET/POST | `/paslon/tambah` | Create candidate pair with photo upload |
| `hapus($id)` | GET | `/paslon/hapus/{id}` | Delete candidate pair and details |
| `ddoo_upload($filename)` | — | internal | Handle photo file upload to `gambar/` |

Two photos uploaded per candidate pair (leader + deputy). Allowed types: jpg/jpeg/png/gif.

---

#### `Photo.php`

| Method | HTTP | URI | Description |
|---|---|---|---|
| `index()` | GET | `/photo` | List user training photos |
| `rebuild()` | GET | `/photo/rebuild` | Rebuild Lambda album (re-trains all photos) |
| `viewAlbum()` | GET | `/photo/viewAlbum` | Fetch and display raw album data from Lambda |
| `rekam($idx)` | GET | `/photo/rekam/{id}` | Train single photo to Lambda API |
| `hapus($idx)` | GET | `/photo/hapus/{id}` | Delete training photo record |
| `train_album()` | GET | `/photo/train_album` | Trigger Lambda album training endpoint |
| `_cekAlbum()` | — | internal | Check album list via Lambda GET /album |
| `_prosesRebuild()` | — | internal | POST each photo to Lambda GET /album_rebuild |

---

#### `Hasil.php`

| Method | HTTP | URI | Description |
|---|---|---|---|
| `index()` | GET | `/hasil` | Display voting results ordered by `perolehan` DESC |

---

#### `Setting.php`

| Method | HTTP | URI | Description |
|---|---|---|---|
| `index()` | GET/POST | `/setting` | Change admin password (bcrypt, 4–16 chars) |

---

#### `Testing.php` (development only)

| Method | URI | Description |
|---|---|---|
| `index()` | `/testing` | Test Lambda album query |
| `rebuild()` | `/testing/rebuild` | Test album rebuild |
| `demo()` | `/testing/demo` | Test with CELEBS demo album |
| `viewAlbum()` | `/testing/viewAlbum` | View album contents |
| `buatAlbum()` | `/testing/buatAlbum` | Create new album in Lambda |
| `banding()` | `/testing/banding` | Test face comparison with FaceXAPI |

This controller should be removed or access-restricted before production deployment.

---

### 5.2 API Controllers (`application/controllers/api/`)

All extend `REST_Controller`. Base path: `/api/{controller}/{method}`.

#### `api/Login.php`

| Method | HTTP | Endpoint | Auth |
|---|---|---|---|
| `otentikasi_post()` | POST | `/api/login/otentikasi` | None |
| `sudahlogin_post()` | POST | `/api/login/sudahlogin` | Token |
| `cekstatus_get()` | GET | `/api/login/cekstatus` | None |

`otentikasi_post`: validates email + SHA1(password), returns `token_login`, `id_user`, `status`.  
`sudahlogin_post`: refreshes token for already-logged-in user.  
`cekstatus_get`: checks whether a user's account is activated.

---

#### `api/Daftar.php`

| Method | HTTP | Endpoint | Auth |
|---|---|---|---|
| `tambah_post()` | POST | `/api/daftar/tambah` | None |

Registers new voter. Creates `user` (status=2) and `detail_user` rows. Generates unique `entry_id` and `token_login`.

Request body: `nama`, `identitas`, `email`, `password`, `token_firebase`

---

#### `api/Akun.php`

| Method | HTTP | Endpoint | Auth |
|---|---|---|---|
| `tampil_get()` | GET | `/api/akun/tampil` | Token |
| `tampil_put($idUser)` | PUT | `/api/akun/tampil/{id}` | Token |

GET: returns user profile (name, identity, email).  
PUT: updates name, identity number, and/or password.

---

#### `api/Kategori.php`

| Method | HTTP | Endpoint | Auth |
|---|---|---|---|
| `tampil_get()` | GET | `/api/kategori/tampil` | Token |
| `semua_get()` | GET | `/api/kategori/semua` | None |

`tampil_get`: categories the requesting user is authorized to vote in.  
`semua_get`: all categories with `status_kategori = 2` (voting open).

---

#### `api/Paslon.php`

| Method | HTTP | Endpoint | Auth |
|---|---|---|---|
| `tampil_post()` | POST | `/api/paslon/tampil` | Token |
| `detail_post()` | POST | `/api/paslon/detail` | Token |

`tampil_post`: candidate list for given `id_kategori`.  
`detail_post`: full profile of a specific candidate pair.

---

#### `api/Gambar.php`

| Method | HTTP | Endpoint | Auth |
|---|---|---|---|
| `tambah_post()` | POST | `/api/gambar/tambah` | Token |
| `cek_post()` | POST | `/api/gambar/cek` | Token |
| `_banding()` | — | internal | FaceXAPI comparison call |

`tambah_post`: saves uploaded image to `gambar/user/`, inserts `photo` row, calls Lambda to train.  
Max upload: 10 MB, 4200×4200 px.

`cek_post`: saves verification photo, calls `_banding()` which hits FaceXAPI comparing the new image URL against the user's first stored training photo URL. Saves result to `pencocokan`. Returns score.

---

#### `api/Suara.php`

| Method | HTTP | Endpoint | Auth |
|---|---|---|---|
| `vote_post()` | POST | `/api/suara/vote` | Token |
| `tampil_get($idUser)` | GET | `/api/suara/tampil/{id}` | Token |
| `perolehan_get()` | GET | `/api/suara/perolehan` | None |

`vote_post`: validates authorization (status=1), inserts voting record, increments `paslon.perolehan`, sets auth status=2.  
`tampil_get`: voting history for a user.  
`perolehan_get`: vote totals grouped by category.

---

#### `api/Hasil.php`

Stub class — no methods implemented.

---

## 6. Models

### `Admin_m.php`

Primary model for all admin-panel database operations.

**Token management**

| Method | Query | Returns |
|---|---|---|
| `get_token_byId($id)` | SELECT from `token` WHERE id_admin | row |
| `cek_token($token)` | SELECT from `token` WHERE token | row |
| `hapus_token($id)` | DELETE from `token` WHERE id_admin | — |
| `simpan_token($data)` | INSERT into `token` | — |

**User management**

| Method | Description |
|---|---|
| `get_data_user()` | SELECT user + detail_user (LEFT JOIN) |
| `get_data_user2()` | SELECT user + detail_user for voter picker |
| `aktivasi_user($id)` | UPDATE user SET status=1 |
| `hapus_user($id)` | DELETE user and detail_user |

**Album & photo**

| Method | Description |
|---|---|
| `get_data_album()` | SELECT album with photo count |
| `data_album()` | SELECT all albums |
| `simpan_album($data)` | INSERT album |
| `get_data_photo()` | SELECT photo + detail_user JOIN |
| `data_photo()` | SELECT all photos |
| `hapus_photo($id)` | DELETE from photo |

**Category management**

| Method | Description |
|---|---|
| `get_data_kategori()` | SELECT all categories |
| `simpan_kategori($data)` | INSERT kategori |
| `update_kategori($data, $id)` | UPDATE kategori.status_kategori |
| `get_data_kategori_paslon($id)` | SELECT category + paslon count |

**Candidate management**

| Method | Description |
|---|---|
| `get_data_paslon()` | SELECT paslon + kategori JOIN |
| `get_data_paslon2()` | SELECT all paslon (simple) |
| `get_data_paslon_byKategori($id)` | SELECT paslon for category |
| `cek_paslon($id)` | Check existence of paslon |
| `simpan_paslon($data)` | INSERT paslon |
| `simpan_detail_paslon($data)` | INSERT detail_paslon |
| `hapus_paslon($id)` | DELETE paslon and detail_paslon |

**Voter authorization**

| Method | Description |
|---|---|
| `get_data_pemilih($id)` | SELECT authorized voters for a category |
| `get_data_calon($id)` | SELECT non-authorized voters for a category |
| `simpan_otorisasi($data)` | INSERT otorisasi_pemilih |

**Admin auth**

| Method | Description |
|---|---|
| `validasi_login($data)` | SELECT admin by username for login |
| `update_admin($data, $id)` | UPDATE admin password |

---

### `Api_model.php`

Model for all mobile API database operations. Auto-loaded as `$this->api`.

**Voting**

| Method | Description |
|---|---|
| `simpan_voting($data)` | INSERT voting record |
| `get_data_voting($id)` | SELECT voting history for user |
| `get_data_perolehan()` | SELECT SUM(perolehan) grouped by kategori |

**Authorization**

| Method | Description |
|---|---|
| `get_data_otorisasi($uid, $kid)` | SELECT otorisasi_pemilih by user+category |
| `perbaharui_data_otorisasi($data, $id)` | UPDATE otorisasi_pemilih.status |

**Photo & face matching**

| Method | Description |
|---|---|
| `simpan_pencocokan($data)` | INSERT pencocokan record |
| `simpan_photo($data)` | INSERT photo record |
| `get_data_photo($id)` | SELECT first photo for user (for face comparison) |
| `get_data_album()` | SELECT album (first row) |

**User account**

| Method | Description |
|---|---|
| `cekEmail($email)` | Check if email exists |
| `simpan_data_user($data)` | INSERT user |
| `simpan_data_detail($data)` | INSERT detail_user |
| `validasi_login($email, $pass)` | SELECT user by email, verify SHA1 password |
| `simpan_token_login($token, $id)` | UPDATE user.token_login |
| `get_data_user($token)` | SELECT user by token_login |
| `get_data_akun($id)` | SELECT user + detail_user |
| `update_password($pass, $id)` | UPDATE user.password |
| `update_detail_user($data, $id)` | UPDATE detail_user |

**Category & candidate**

| Method | Description |
|---|---|
| `get_data_kategori_hasil()` | SELECT all categories |
| `get_data_paslon($id)` | SELECT paslon by category |
| `get_data_paslon2($idUser, $idKat)` | SELECT paslon user is authorized for |
| `perbaharui_paslon($id)` | INCREMENT paslon.perolehan by 1 |
| `get_detail_paslon($id)` | SELECT paslon + detail_paslon |

---

## 7. Libraries

**No custom libraries are implemented.**

The `application/libraries/` directory is empty.

Built-in CodeIgniter libraries used (auto-loaded):

| Library | Purpose |
|---|---|
| `database` | Query builder + MySQLi driver |
| `session` | File-based session management |
| `form_validation` | Input validation rules |
| `encryption` | AES-256 encryption (key: `mrasT`) |
| `upload` | File upload handling |
| `captcha` | CAPTCHA image generation |

Third-party library:

| Library | Source | Purpose |
|---|---|---|
| `REST_Controller` | chriskacerguis/codeigniter-restserver | REST API routing and response formatting |

---

## 8. Helpers

### `facevoting_helper.php`

| Function | Description |
|---|---|
| `config_captcha()` | Returns captcha config array: 150×50 px, 5 chars, `captcha/` path, 5-minute DB expiry |
| `_unlogin()` | Destroys session and redirects to `/login` — used as auth guard in all web controllers |

### `keamanan_helper.php`

| Function | Description |
|---|---|
| `filter_output($str)` | Wraps `htmlspecialchars($str, ENT_QUOTES)` for XSS-safe output in views |

### CodeIgniter built-in helpers (auto-loaded)

| Helper | Purpose |
|---|---|
| `form` | Form opening/closing tags, input generation |
| `url` | `base_url()`, `site_url()`, `redirect()` |
| `captcha` | Low-level captcha image generation |

---

## 9. Cron Jobs

**No cron jobs are configured or implemented** in this codebase.

There are no:
- Shell scripts for scheduled tasks
- Cronjob-specific controllers
- CI hooks for scheduling

If periodic face album rebuilding or voting deadline enforcement is needed in the future, it would be implemented as a CLI controller and scheduled via the server's crontab.

---

## 10. APIs

### Base URL

```
http://<host>/api/
```

REST format: JSON. Authentication: token passed as POST/GET parameter or request body field `token_login`.

> The REST server is configured with `rest_auth = false` — there is no HTTP-level authentication (no API key, no Basic Auth, no Bearer token). Token validation is done manually in each controller method.

### Complete Endpoint Reference

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/login/otentikasi` | — | Login: email + password |
| POST | `/api/login/sudahlogin` | Token | Refresh token |
| GET | `/api/login/cekstatus` | — | Check account activation status |
| POST | `/api/daftar/tambah` | — | Register new voter account |
| GET | `/api/akun/tampil` | Token | Get own profile |
| PUT | `/api/akun/tampil/{id}` | Token | Update profile / password |
| GET | `/api/kategori/tampil` | Token | List authorized categories |
| GET | `/api/kategori/semua` | — | List all open categories |
| POST | `/api/paslon/tampil` | Token | List candidates by category |
| POST | `/api/paslon/detail` | Token | Get candidate detail |
| POST | `/api/gambar/tambah` | Token | Upload & train face photo |
| POST | `/api/gambar/cek` | Token | Upload & verify face (comparison) |
| POST | `/api/suara/vote` | Token | Submit vote |
| GET | `/api/suara/tampil/{id}` | Token | Get voting history |
| GET | `/api/suara/perolehan` | — | Get vote counts by category |

### Response Format

All responses follow CodeIgniter REST Server format:

```json
{
  "status": true,
  "message": "...",
  "data": { ... }
}
```

Error responses use HTTP status codes (400, 401, 404, 500).

---

## 11. Authentication Flow

### 11.1 Admin Web Panel

```
1. GET /login
   → generate captcha (5 chars, stored in DB with 5-min expiry)
   → render login form with captcha image

2. POST /login
   → _check_captcha(): SELECT captcha from DB by ID, compare answer, DELETE after use
   → if captcha invalid: reload with error

   → Admin_m::validasi_login(['username' => $user])
   → password_verify($pass, $row->password)   // bcrypt
   → if invalid: reload with error

   → generate token = sha1($user . $pass . microtime())
   → Admin_m::simpan_token(['id_admin', 'token', 'time' => time()])
   → $_SESSION['token'] = $token
   → $_SESSION['id_admin'] = $id
   → redirect to /member

3. Every protected controller action:
   → read $_SESSION['token']
   → Admin_m::cek_token($token) — must return a row
   → if no row: _unlogin() → destroy session → redirect /login

4. GET /member/logout
   → Admin_m::hapus_token($id_admin) — DELETE from token table
   → session_destroy()
   → redirect /login
```

### 11.2 Mobile API

```
1. POST /api/daftar/tambah  (registration)
   → validate required fields
   → Api_model::cekEmail() — reject if email exists
   → entry_id = random alphanumeric
   → token_login = random 10-char alphanumeric
   → password = SHA1($password)
   → Api_model::simpan_data_user()  (status = 2 = pending)
   → Api_model::simpan_data_detail()
   → respond with token_login, id_user

2. POST /api/login/otentikasi  (login)
   → Api_model::validasi_login($email, SHA1($password))
   → if status = 2: respond "not yet activated"
   → generate new token_login (10-char)
   → Api_model::simpan_token_login($token, $id)
   → respond with token_login, id_user, status

3. Subsequent API calls:
   → each controller reads token_login from request
   → Api_model::get_data_user($token) — must return user row
   → if no row: respond 401 / error
```

---

## 12. Billing Flow

**This system has no billing functionality.**

FaceVoting is an institutional election platform. There is no subscription management, invoicing, credit system, or payment collection built into the codebase.

---

## 13. Payment Flow

**This system has no payment processing.**

No payment gateways (Midtrans, Stripe, PayPal, etc.) are integrated. There are no transaction tables, payment status columns, or financial record structures in the database schema.

---

## 14. Reports

### 14.1 Voting Results (Admin Panel)

**Controller**: `Hasil::index()` → view `v_hasil.php`  
**Query**: `SELECT paslon.*, kategori.nama_kategori ORDER BY perolehan DESC`

Displays a table of all candidate pairs with their vote counts, sorted highest to lowest, grouped by category.

### 14.2 Voter List per Category

**Controller**: `Kategori::detail($idx)`  
Shows which voters are authorized for a category, and their authorization status (voted / not voted).

### 14.3 User Management List

**Controller**: `User::index()`  
Shows all registered voters with their activation status.

### 14.4 Photo/Album Status

**Controller**: `Photo::index()`, `Album::index()`  
Shows training photos uploaded per user and whether each has been successfully trained in the Lambda face recognition system.

### 14.5 API Results Endpoint

**Endpoint**: `GET /api/suara/perolehan`  
Returns a JSON array of vote counts by category, suitable for displaying real-time results in the mobile app.

---

## 15. External Integrations

### 15.1 Lambda Face Recognition (RapidAPI)

**Purpose**: Train and store face recognition models from uploaded voter photos.

| Property | Value |
|---|---|
| Base URL | `https://lambda-face-recognition.p.rapidapi.com/` |
| API Key | `932571abf0msh45cf0f3cef74aacp19e151jsn33e9949a1974` (hardcoded) |
| RapidAPI Host | `lambda-face-recognition.p.rapidapi.com` |

**Endpoints used:**

| Lambda Endpoint | Method | Used by | Description |
|---|---|---|---|
| `/album` | GET | `Photo::_cekAlbum()`, `Testing` | List albums |
| `/album` | POST | `Testing::buatAlbum()` | Create album |
| `/album_train` | POST | `Photo::rekam()`, `Gambar::tambah_post()` | Train face from photo URL |
| `/album_rebuild` | GET | `Photo::_prosesRebuild()` | Rebuild face index |

**Request pattern** (cURL, used across multiple controllers):

```php
curl_setopt($curl, CURLOPT_URL, "https://lambda-face-recognition.p.rapidapi.com/album_train");
curl_setopt($curl, CURLOPT_POSTFIELDS, [
    "albumcode" => $kode_album,
    "tag"       => $entry_id,      // voter's unique ID
    "imageurl"  => base_url() . "gambar/user/" . $gambar
]);
curl_setopt($curl, CURLOPT_HTTPHEADER, [
    "x-rapidapi-host: lambda-face-recognition.p.rapidapi.com",
    "x-rapidapi-key: 932571abf0msh45cf0f3cef74aacp19e151jsn33e9949a1974"
]);
```

---

### 15.2 FaceXAPI — Face Comparison

**Purpose**: Compare two face photos and return a similarity score. Used at voting time to verify the voter is the same person as in the training photo.

| Property | Value |
|---|---|
| Endpoint | `https://www.facexapi.com/match_faces` |
| User ID | `603f05b94e6c5e6c15c171e7` (hardcoded) |
| Method | POST |

**Request (from `Gambar::_banding()`):**

```php
curl_setopt($curl, CURLOPT_URL, "https://www.facexapi.com/match_faces");
curl_setopt($curl, CURLOPT_POSTFIELDS, [
    "user_id" => "603f05b94e6c5e6c15c171e7",
    "img_1"   => base_url() . "gambar/user/" . $photo_training->gambar,
    "img_2"   => base_url() . "gambar/user/" . $gambar_baru
]);
```

Returns a similarity score stored in `pencocokan.score`. The mobile app decides whether the score is sufficient to allow voting.

---

### 15.3 Firebase Cloud Messaging

**Status**: Partially integrated — `token_firebase` is stored in the `user` table during registration and login, but **no server-side FCM push notification calls are made** in the current backend codebase. Push notification delivery (if any) is likely handled on the mobile side only.

---

## 16. Frontend Pages

All admin views use AdminLTE 3.0.5. Views are split into a template layer (`application/views/template/`) and content layer (`application/views/konten/`), assembled in each controller via multiple `$this->load->view()` calls.

### Page Inventory

| URI | Controller::Method | View files | Description |
|---|---|---|---|
| `/login` | `Login::index()` | `v_login` / `k_login` | Admin login with CAPTCHA |
| `/member` | `Member::index()` | `v_member` / `k_member` | Dashboard (summary stats) |
| `/user` | `User::index()` | `v_user` / `k_user` | Voter list with activate/delete |
| `/album` | `Album::index()` | `v_album` / `k_album` | Face recognition album list |
| `/album/detail` | — | `v_detailalbum` / `k_detailalbum` | Album photo details |
| `/kategori` | `Kategori::index()` | `v_kategori` / `k_kategori` | Voting category list |
| `/kategori/tambah` | `Kategori::tambah()` | `v_tambahkategori` / `k_tambahkategori` | Create new category |
| `/kategori/detail/{id}` | `Kategori::detail()` | `v_detailkategori` / `k_detailkategori` | Category detail + voter assignment |
| `/paslon` | `Paslon::index()` | `v_paslon` / `k_paslon` | Candidate list |
| `/paslon/tambah` | `Paslon::tambah()` | `v_tambahpaslon` / `k_tambahpaslon` | Create new candidate pair |
| `/photo` | `Photo::index()` | `v_photo` / `k_photo` | Training photo management |
| `/photo/rebuild` | `Photo::rebuild()` | `v_rebuildalbum` / `k_rebuildalbum` | Lambda album rebuild |
| `/hasil` | `Hasil::index()` | `v_hasil` / `k_hasil` | Voting results display |
| `/setting` | `Setting::index()` | `v_setting` / `k_setting` | Admin password change |

### View Assembly Pattern

Each page is assembled using 4 partial views:

```php
$this->load->view('template/v_header', $data);
$this->load->view('template/v_navbar', $data);
$this->load->view('template/v_sidebar', $data);
$this->load->view('konten/k_<page>', $data);
$this->load->view('template/v_footer', $data);
```

---

## 17. Navigation Structure

### Admin Panel Sidebar

```
FaceVoting Admin
│
├── Dashboard          → /member
│
├── Data Master
│   ├── Data User      → /user
│   ├── Album          → /album
│   └── Photo          → /photo
│
├── Pemilihan (Voting)
│   ├── Kategori       → /kategori
│   └── Paslon         → /paslon
│
├── Hasil              → /hasil
│
└── Setting            → /setting
```

### Top Navbar

```
[FaceVoting Logo / Brand]          [Admin Name]  [Logout → /member/logout]
```

### Mobile App Navigation (inferred from API structure)

```
App
│
├── Auth
│   ├── Register     → POST /api/daftar/tambah
│   └── Login        → POST /api/login/otentikasi
│
├── Home / Categories
│   └── My Voting    → GET /api/kategori/tampil
│
├── Vote Flow
│   ├── Candidates   → POST /api/paslon/tampil
│   ├── Face Check   → POST /api/gambar/cek
│   └── Submit Vote  → POST /api/suara/vote
│
├── Profile
│   └── My Account   → GET/PUT /api/akun/tampil
│
└── Results          → GET /api/suara/perolehan
```

---

## Appendix A: Security Notes

The following issues exist in the current codebase and should be addressed before production deployment:

| # | Issue | Severity | Location |
|---|---|---|---|
| 1 | API user passwords hashed with SHA1 (broken algorithm) | High | `Api_model::simpan_data_user()`, `validasi_login()` |
| 2 | Hardcoded RapidAPI key in source code | High | `Photo.php`, `Testing.php`, `Gambar.php` |
| 3 | Hardcoded FaceXAPI user ID in source code | Medium | `Gambar.php` |
| 4 | Weak encryption key (`mrasT`) in `config.php` | Medium | `application/config/config.php` |
| 5 | No HTTPS enforcement (`force_https = false`) | Medium | `application/config/rest.php` |
| 6 | No API-level authentication (token in body, not signed) | Medium | All API controllers |
| 7 | No rate limiting on API endpoints | Medium | `application/config/rest.php` |
| 8 | `Testing.php` controller accessible in production | Medium | `application/controllers/Testing.php` |
| 9 | No CORS configuration | Low | `application/config/rest.php` |
| 10 | `tbjurusan` table unused — dead schema | Info | Database |

---

## Appendix B: File Storage

| Path | Content | Max Size |
|---|---|---|
| `gambar/user/` | Voter face photos (training + verification) | 10 MB per file |
| `gambar/` | Category logos + candidate photos | 2424 KB (logos), unlimited (candidates) |
| `captcha/` | Temporary captcha images (auto-expired) | Minimal |

Photo filenames are 10-character random hex strings (e.g., `b6a0595f28.jpg`).

---

*End of system analysis.*
