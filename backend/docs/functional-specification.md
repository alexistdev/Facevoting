# Functional Specification — FaceVoting

**Version**: 1.0  
**Framework**: CodeIgniter 3.1.11  
**Analyzed from source**: 2026-06-06  
**Scope**: All implemented features across admin web panel and mobile REST API

---

## Table of Contents

### Admin Panel (Web)
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
- [F-18 Train Individual Photo to Lambda](#f-18-train-individual-photo-to-lambda)
- [F-19 Delete Training Photo](#f-19-delete-training-photo)
- [F-20 Face Recognition Album View](#f-20-face-recognition-album-view)
- [F-21 Face Recognition Album Rebuild](#f-21-face-recognition-album-rebuild)
- [F-22 Voting Results View](#f-22-voting-results-view)

### Mobile API
- [F-23 Voter Registration](#f-23-voter-registration)
- [F-24 Voter Login](#f-24-voter-login)
- [F-25 Token Refresh](#f-25-token-refresh)
- [F-26 Account Status Check](#f-26-account-status-check)
- [F-27 View Account Profile](#f-27-view-account-profile)
- [F-28 Update Account Profile](#f-28-update-account-profile)
- [F-29 List Authorized Categories](#f-29-list-authorized-categories)
- [F-30 List All Open Categories](#f-30-list-all-open-categories)
- [F-31 List Candidates by Category](#f-31-list-candidates-by-category)
- [F-32 Candidate Detail](#f-32-candidate-detail)
- [F-33 Upload Face Training Photo](#f-33-upload-face-training-photo)
- [F-34 Face Verification](#f-34-face-verification)
- [F-35 Submit Vote](#f-35-submit-vote)
- [F-36 Voting History](#f-36-voting-history)
- [F-37 Vote Counts by Category](#f-37-vote-counts-by-category)

---

## Conventions

| Term | Meaning |
|---|---|
| **Actor** | Who triggers or uses this feature |
| **Input** | Data received (form fields, URL params, POST body, file uploads) |
| **Output** | What the system returns or renders |
| **Validation** | Checks applied before processing |
| **Business rule** | Domain constraint that must hold true |
| **Dependency** | Other features, tables, or external services this feature relies on |
| `[BUG]` | A confirmed defect found in the source code |
| `[RISK]` | A security or integrity concern |

---

---

# Admin Panel (Web)

---

## F-01 Admin Login

**Controller**: `Login::index()`  
**Route**: `GET / POST /login`  
**Default route**: yes (`$route['default_controller'] = 'login'`)

### Purpose
Authenticate the administrator and establish a server-verified session with a CAPTCHA challenge to prevent automated login attempts.

### Actor
Administrator (unauthenticated)

### Inputs

| Field | Type | Source | Required |
|---|---|---|---|
| `username` | string | POST form | Yes |
| `password` | string | POST form | Yes |
| `captcha` | string | POST form | Yes |

### Outputs

**Success**: Session cookie set; redirect to `/Member`.  
**Failure**: Reload `/login` with flash error message and a new CAPTCHA image.

Session variables set on success:

| Key | Value |
|---|---|
| `id_admin` | Hardcoded `1` |
| `token` | SHA1 of current datetime string |
| `is_login_in` | `TRUE` |

### Validations

| Field | Rule | Error Message |
|---|---|---|
| `username` | required, trim | "Username harus diisi!" |
| `password` | required, trim | "Password harus diisi!" |
| `captcha` | required, trim, callback `_check_captcha` | "Captcha harus diisi!" / "Captcha yang anda masukkan salah!" |
| credential check | `password_verify($post, $db_hash)` against bcrypt | "Username atau password anda salah!" |

CAPTCHA validation: compares submitted string (case-sensitive) against `$_SESSION['captchaword']` set at page load.

### Business Rules

1. If session `is_login_in === TRUE` on constructor, redirect immediately to `/Member` without rendering the form (prevents re-login while already logged in).
2. Before saving the new token, the system checks whether a token already exists for `id_admin = 1`. If found, it is deleted first (`hapus_token(1)` hardcoded).
3. Token is generated as `sha1(date('Y-m-d H:i:s'))` — tied to the second of login.
4. Token is stored in both the `token` DB table and the PHP session. Every protected page cross-checks both values.
5. A new CAPTCHA is generated on every GET or failed POST. CAPTCHA configuration: 150×50 px, 5 characters, stored in session.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::validasi_login()` | Fetches admin row by username |
| `Admin_m::cek_token()` | Checks existing token via JOIN on admin.username |
| `Admin_m::hapus_token()` | Deletes existing token before issuing new one |
| `Admin_m::simpan_token()` | Persists new token to DB |
| `facevoting_helper::config_captcha()` | Returns CAPTCHA config array |
| CI `captcha` library | Generates CAPTCHA image to `captcha/` directory |
| `admin` table | Username + bcrypt password storage |
| `token` table | Server-side token storage |

### Known Issues

- `[BUG]` Token generation uses `sha1(date('Y-m-d H:i:s'))`. Two logins within the same second produce identical tokens.
- `[BUG]` `hapus_token(1)` hardcodes `id_admin = 1`, not the actual authenticated admin's ID. If multiple admins were ever added, only admin #1's token would be cleared.
- `[RISK]` SHA1 of a predictable timestamp is guessable by brute force within a one-second window.
- `[RISK]` CAPTCHA word stored in PHP session (`$_SESSION['captchaword']`), not database — vulnerable to session fixation if the session ID is reused.

---

## F-02 Admin Logout

**Controller**: `Member::logout()`  
**Route**: `GET /member/logout`

### Purpose
Terminate the administrator's session cleanly.

### Actor
Authenticated administrator

### Inputs
None (URL trigger only).

### Outputs
Session destroyed; redirect to `/Login`.

### Validations
None beyond requiring the admin to navigate to the URL (no CSRF protection).

### Business Rules

1. `session_destroy()` clears all session data.
2. The `token` record in the database is **not deleted** on logout — only session data is cleared. `[BUG]` The token row remains in the `token` table after logout. On next login, `cek_token()` finds it and `hapus_token(1)` deletes it, so the stale token is cleaned up on the next successful login. However, if a session is hijacked after logout but before the next login, the old token is still valid server-side.

### Dependencies

| Dependency | Detail |
|---|---|
| CI `session` library | `sess_destroy()` |

---

## F-03 Admin Dashboard

**Controller**: `Member::index()`  
**Route**: `GET /member`

### Purpose
Landing page after login. Displays a summary view confirming the admin is authenticated.

### Actor
Authenticated administrator

### Inputs
None.

### Outputs
Renders `view/v_member.php` with page title.

### Validations

**Session guard** (applied in constructor of every protected controller):
1. `$_SESSION['is_login_in'] !== TRUE` → redirect to `/login`.
2. `$_SESSION['token'] !== token.token WHERE id_admin = session.id_admin` → call `_unlogin()` (destroy session + redirect to `/login`).

### Business Rules

1. The session guard is applied on every protected controller constructor — unauthenticated requests are rejected before any method executes.
2. Token is fetched fresh from DB on every request, allowing server-side session invalidation.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::get_token_byId()` | Fetches current valid token from DB |
| `token` table | Server-side token comparison |

---

## F-04 Admin Password Change

**Controller**: `Setting::index()`  
**Route**: `GET / POST /setting`

### Purpose
Allow the administrator to change their own login password.

### Actor
Authenticated administrator

### Inputs

| Field | Type | Source | Required |
|---|---|---|---|
| `password1` | string | POST form | Yes |
| `password2` | string | POST form | Yes (confirmation) |

### Outputs

**Success**: Flash message "Berhasil Memperbaharui data!"; redirect to `/Setting`.  
**Failure**: Flash validation errors; re-render `view/v_setting.php`.

### Validations

| Field | Rule | Error Message |
|---|---|---|
| `password1` | required, trim | "Password harus diisi!" |
| `password1` | min_length[4] | "Panjang karakter Password minimal 6 karakter!" *(message says 6, rule says 4 — inconsistency)* |
| `password1` | max_length[16] | "Panjang karakter Password maksimal 16 karakter!" |
| `password2` | required, matches[password1] | "Password tidak sama!" |

### Business Rules

1. New password is hashed with `password_hash($password, PASSWORD_BCRYPT)` before storing.
2. No current-password verification is required — any authenticated admin session can reset the password without knowing the existing one.
3. Update always targets `WHERE id_admin = 1` (hardcoded in `Admin_m::update_admin()`).

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::update_admin()` | UPDATE admin SET password WHERE id_admin = 1 |
| `admin` table | Password column |

### Known Issues

- `[BUG]` Validation message says "minimal 6 karakter" but the rule enforces `min_length[4]`.
- `[RISK]` No old-password confirmation means an unattended session can be used to lock the real admin out.

---

## F-05 Voter List

**Controller**: `User::index()`  
**Route**: `GET /user`

### Purpose
Display all registered voter accounts with their profile and activation status so the admin can manage them.

### Actor
Authenticated administrator

### Inputs
None.

### Outputs
Renders `view/v_user.php` with `dataUser` array containing all `user` + `detail_user` JOIN results.

### Validations
Session guard (see F-03).

### Business Rules

1. All users are displayed regardless of status (pending and active).
2. Data is fetched via `get_data_user(null)` — the `null` parameter causes no status filter to be applied.
3. Each row exposes: `id_user`, `email`, `status`, `nama`, `identitas`.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::get_data_user()` | SELECT user JOIN detail_user |
| `user`, `detail_user` tables | — |

---

## F-06 Voter Activation

**Controller**: `User::aktivasi($idx)`  
**Route**: `GET /user/aktivasi/{id}`

### Purpose
Approve a pending voter account, enabling them to log in via the mobile API.

### Actor
Authenticated administrator

### Inputs

| Parameter | Type | Source | Required |
|---|---|---|---|
| `$idx` | integer | URL segment | Yes |

### Outputs

**Success**: Flash "User telah berhasil diaktifkan!"; redirect to `/User`.  
**Invalid ID**: Redirect to `/User` silently.

### Validations

1. `$idx` must not be null or empty string.
2. User must exist AND have `status = 2` (pending): `get_data_user($id)` applies `WHERE status = 2` — if the user doesn't exist or is already active, `num_rows() == 0` → redirect without action.

### Business Rules

1. Sets `user.status = 1`.
2. Only affects users currently in pending state (status = 2) due to the query filter.
3. No email notification is sent to the voter upon activation.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::get_data_user($id)` | Checks existence with status=2 filter |
| `Admin_m::aktivasi_user()` | UPDATE user SET status=1 WHERE id_user |
| `user` table | — |

---

## F-07 Voter Deletion

**Controller**: `User::hapus($idx)`  
**Route**: `GET /user/hapus/{id}`

### Purpose
Permanently remove a voter account and all associated data.

### Actor
Authenticated administrator

### Inputs

| Parameter | Type | Source | Required |
|---|---|---|---|
| `$idx` | integer | URL segment | Yes |

### Outputs

**Success**: Flash "User telah dihapus!"; redirect to `/User`.  
**Invalid ID**: Redirect to `/User` silently.

### Validations

1. `$idx` must not be null or empty string.
2. User must exist: `get_data_user2($id)` checks existence without status filter.

### Business Rules

1. `hapus_user($id)` issues `DELETE FROM user WHERE id_user = $id`.
2. Due to FK CASCADE constraints, deleting from `user` automatically deletes:
   - `detail_user` rows (CASCADE)
   - `photo` rows (CASCADE)
   - `otorisasi_pemilih` rows (CASCADE)
   - `voting` rows (CASCADE)
3. This cascade destroys the voter's entire vote history — irreversible.
4. `pencocokan` rows are **not** deleted (no FK constraint) — orphaned records remain.
5. Uploaded files (`gambar/user/*.jpg`) are **not** deleted from the filesystem.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::get_data_user2()` | Existence check (no status filter) |
| `Admin_m::hapus_user()` | DELETE from user (cascades) |
| `user`, `detail_user`, `photo`, `otorisasi_pemilih`, `voting` tables | Cascade targets |

### Known Issues

- `[RISK]` Cascading deletion of `voting` records destroys audit history. Deleting an active or formerly-active voter removes all evidence of their vote.
- `[RISK]` No confirmation step — a misclick on the URL immediately deletes the account.

---

## F-08 Election Category List

**Controller**: `Kategori::index()`  
**Route**: `GET /kategori`

### Purpose
Display all voting categories (elections) with their name, logo, and current status.

### Actor
Authenticated administrator

### Inputs
None.

### Outputs
Renders `view/v_kategori.php` with `dataKategori` array. Each row contains: `id_kategori`, `nama_kategori`, `logo_kategori`, `status_kategori`.

### Validations
Session guard.

### Business Rules

1. All categories are returned regardless of status.
2. The view is responsible for rendering status labels and action buttons (Open/Close) based on `status_kategori` value.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::get_data_kategori()` | SELECT all from kategori |
| `kategori` table | — |

---

## F-09 Election Category Creation

**Controller**: `Kategori::tambah()`  
**Route**: `GET / POST /kategori/tambah`

### Purpose
Create a new voting election category with a display logo.

### Actor
Authenticated administrator

### Inputs

| Field | Type | Source | Required |
|---|---|---|---|
| `namaKategori` | string | POST form | Yes |
| `logo` | file | POST multipart | Yes |

### Outputs

**Success**: Flash "Kategori berhasil ditambahkan!"; redirect to `/Kategori`.  
**Validation failure**: Flash validation errors; redirect to `/Kategori/tambah`.  
**Upload failure**: Flash upload error; redirect to `/Kategori/tambah`.

### Validations

| Check | Rule |
|---|---|
| `namaKategori` | required, trim |
| `logo` file type | jpg, jpeg, png, gif only |
| `logo` file size | max 2,424 KB |

### Business Rules

1. Logo filename is generated as `substr(sha1(time()), 0, 10)` — a 10-character hex string. The file extension is appended by the CI upload library from the original filename.
2. File is stored in `./gambar/` (project root relative).
3. New category is created with `status_kategori = 1` (setup / closed state — not yet open for voting).
4. `perolehan` (vote count) is not applicable to `kategori`; it lives on `paslon`.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::simpan_kategori()` | INSERT kategori |
| CI `upload` library | File handling |
| `gambar/` directory | Must be writable |
| `kategori` table | — |

---

## F-10 Election Category Detail

**Controller**: `Kategori::detail($idx)`  
**Route**: `GET /kategori/detail/{id}`

### Purpose
Display the full management view for a single category: its candidate pairs, the authorized voter list, and a picker to add more voters.

### Actor
Authenticated administrator

### Inputs

| Parameter | Type | Source | Required |
|---|---|---|---|
| `$idx` | integer | URL segment | Yes |

### Outputs
Renders `view/v_detailkategori.php` with:

| Variable | Content |
|---|---|
| `dataPaslon` | Candidate pairs for this category |
| `dataPemilih` | Currently authorized voters (LEFT JOIN detail_user) |
| `dataCalonPemilih` | Users NOT yet authorized in this category |
| `idKategori` | The category ID |

### Validations

1. `$idx` must not be null or empty.
2. Category must exist: `get_data_kategori($id)` must return at least one row.

### Business Rules

1. `dataCalonPemilih` is built via a subquery: `SELECT user WHERE id_user NOT IN (SELECT id_user FROM otorisasi_pemilih WHERE id_kategori = $id)`. This correctly excludes already-authorized voters.
2. All users (both status 1 and 2) appear in the voter picker — pending-activation users can be authorized before being activated.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::get_data_paslon_byKategori()` | Candidates for this category |
| `Admin_m::get_data_pemilih()` | Authorized voters |
| `Admin_m::get_data_calon()` | Un-authorized voters (subquery) |
| `kategori`, `paslon`, `otorisasi_pemilih`, `user`, `detail_user` tables | — |

---

## F-11 Voter Authorization

**Controller**: `Kategori::tambah_pemilih($idKat, $idUser)`  
**Route**: `GET /kategori/tambah_pemilih/{kategoriId}/{userId}`

### Purpose
Grant a specific voter the right to participate in a specific election category. Creates an authorization record that the mobile API checks before accepting a vote.

### Actor
Authenticated administrator

### Inputs

| Parameter | Type | Source | Required |
|---|---|---|---|
| `$idKat` | integer | URL segment | Yes |
| `$idUser` | integer | URL segment | Yes |

### Outputs

**Success**: Flash "User berhasil diberi hak akses!"; redirect to `/Kategori/detail/{idKat}`.  
**Invalid**: Redirect to `/Kategori` silently.

### Validations

1. Both `$idKat` and `$idUser` must be non-null and non-empty.
2. Category must exist: `get_data_kategori($idKategori)` must return rows.
3. User must exist: `get_data_user2($idUser)` must return rows.

### Business Rules

1. Inserts a new row into `otorisasi_pemilih` with `status = 1` (authorized, not yet voted).
2. No duplicate check is performed — calling this URL twice for the same user/category pair creates two authorization rows. `[BUG]`
3. Triggering this URL is sufficient; no confirmation or transaction is required.
4. The category does not need to be in any particular `status_kategori` state — authorization can be added before or after voting is opened.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::simpan_otorisasi()` | INSERT otorisasi_pemilih |
| `Admin_m::get_data_kategori()` | Category existence check |
| `Admin_m::get_data_user2()` | User existence check |
| `otorisasi_pemilih` table | — |

### Known Issues

- `[BUG]` No duplicate prevention — double-clicking produces two authorization rows, potentially allowing a user to vote twice in the same category.

---

## F-12 Open Election Voting

**Controller**: `Kategori::Buka($idx)`  
**Route**: `GET /kategori/Buka/{id}`

### Purpose
Open a voting category so that authorized voters can submit votes via the mobile app.

### Actor
Authenticated administrator

### Inputs

| Parameter | Type | Source | Required |
|---|---|---|---|
| `$idx` | integer | URL segment | Yes |

### Outputs

**Success**: Flash "Pemilu saat ini telah dibuka!" (green); redirect to `/Kategori`.  
**Invalid**: Redirect to `/Kategori` silently.

### Validations

1. `$idx` must not be empty.
2. Category must exist: `get_data_kategori($id)` must return rows.

### Business Rules

1. Sets `kategori.status_kategori = 2`.
2. `status_kategori = 2` is the value the mobile API uses to identify "open" elections (`semua_get` returns `WHERE status_kategori = 2`).
3. No guard prevents opening a category that is already open.
4. No guard prevents opening a category with zero candidates or zero authorized voters.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::update_kategori()` | UPDATE kategori SET status_kategori=2 |
| `kategori` table | — |

---

## F-13 Close Election Voting

**Controller**: `Kategori::Tutup($idx)`  
**Route**: `GET /kategori/Tutup/{id}`

### Purpose
Close an active election, preventing further votes from being submitted via the mobile app.

### Actor
Authenticated administrator

### Inputs

| Parameter | Type | Source | Required |
|---|---|---|---|
| `$idx` | integer | URL segment | Yes |

### Outputs

**Success**: Flash "Pemilu saat ini telah ditutup!" (red); redirect to `/Kategori`.  
**Invalid**: Redirect to `/Kategori` silently.

### Validations

1. `$idx` must not be empty.
2. Category must exist.

### Business Rules

1. Sets `kategori.status_kategori = 1`.
2. After closing, the mobile API `semua_get` no longer returns this category, blocking new votes.
3. `[RISK]` No atomicity: votes in-flight at the moment of closing are not rolled back or blocked by the status change — they complete normally because `vote_post()` does not check `status_kategori`.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::update_kategori()` | UPDATE kategori SET status_kategori=1 |
| `kategori` table | — |

---

## F-14 Candidate Pair List

**Controller**: `Paslon::index()`  
**Route**: `GET /paslon`

### Purpose
Display all candidate pairs across all election categories.

### Actor
Authenticated administrator

### Inputs
None.

### Outputs
Renders `view/v_paslon.php` with `dataPaslon` containing a JOIN of `paslon + kategori + detail_paslon`.

### Validations
Session guard.

### Business Rules

1. All candidate pairs are displayed regardless of their category's status.
2. Includes full profile data (`visi_misi`, `profil_catum`, `profil_cawatum`) from `detail_paslon`.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::get_data_paslon()` | SELECT paslon JOIN kategori JOIN detail_paslon |
| `paslon`, `kategori`, `detail_paslon` tables | — |

---

## F-15 Candidate Pair Creation

**Controller**: `Paslon::tambah()`  
**Route**: `GET / POST /paslon/tambah`

### Purpose
Register a new candidate pair (leader + deputy) under a voting category, including profile text and optional photos.

### Actor
Authenticated administrator

### Inputs

| Field | Type | Source | Required | Stored in |
|---|---|---|---|---|
| `namaKategori` | integer (id_kategori) | POST form select | Yes | `paslon.id_kategori` |
| `judulPaslon` | string | POST form | Yes | `paslon.judul_paslon` |
| `ketuaPaslon` | string | POST form | Yes | `paslon.ketua_paslon` |
| `wakilPaslon` | string | POST form | Yes | `paslon.wakil_paslon` |
| `profilCatum` | text | POST form | Yes | `detail_paslon.profil_catum` |
| `profilCawatum` | text | POST form | Yes | `detail_paslon.profil_cawatum` |
| `visimisi` | text | POST form | Yes | `detail_paslon.visi_misi` |
| `photo1` | file | POST multipart | No | `paslon.photo1_paslon` |
| `photo2` | file | POST multipart | No | `paslon.photo2_paslon` |

### Outputs

**Success**: Flash "Pasangan Calon berhasil ditambahkan!"; redirect to `/Paslon`.  
**Validation failure**: Flash validation errors; render `view/v_tambahpaslon.php` with category dropdown.

### Validations

| Field | Rule |
|---|---|
| All text fields | required, trim |
| `photo1`, `photo2` | Optional; if provided: types jpg/jpeg/png/gif, max 2,424 KB |

### Business Rules

1. The category dropdown (`daftarKategori`) is populated with categories where `status_kategori = 1`. Only setup-state categories accept new candidates.
2. Photos are uploaded to `./gambar/` via `ddoo_upload()`. If a photo is not provided (empty `$_FILES` name), no upload is attempted and `$file1` / `$file2` variables remain unset — they are passed as `null`/undefined to `simpan_paslon()`.
3. `perolehan` is initialized to `0` at creation.
4. Two DB rows are inserted in sequence: first `paslon` (returns `insert_id()`), then `detail_paslon` referencing that ID.
5. There is no limit on the number of candidate pairs per category.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::get_data_kategori_paslon()` | Populates category dropdown (status_kategori=1) |
| `Admin_m::simpan_paslon()` | INSERT paslon |
| `Admin_m::simpan_detail_paslon()` | INSERT detail_paslon |
| CI `upload` library | File handling |
| `gambar/` directory | Must be writable |
| `paslon`, `detail_paslon` tables | — |

### Known Issues

- `[BUG]` If `photo1` or `photo2` is not uploaded, `$file1` / `$file2` are undefined variables. PHP emits a notice; `simpan_paslon()` receives `null` for the photo columns (which is valid since they are nullable).

---

## F-16 Candidate Pair Deletion

**Controller**: `Paslon::hapus($id)`  
**Route**: `GET /paslon/hapus/{id}`

### Purpose
Remove a candidate pair and its profile detail from the system.

### Actor
Authenticated administrator

### Inputs

| Parameter | Type | Source | Required |
|---|---|---|---|
| `$id` | integer | URL segment | Yes |

### Outputs

**Success**: Flash "Data paslon berhasil dihapus!" (red); redirect to `/Paslon`.  
**Invalid**: Redirect to `/Paslon` silently.

### Validations

1. `$id` must not be empty.
2. Candidate pair must exist: `cek_paslon($id)` must return rows.

### Business Rules

1. `hapus_paslon($id)` issues `DELETE FROM paslon WHERE id_paslon = $id`.
2. Due to FK CASCADE on `detail_paslon_ibfk_1`, the corresponding `detail_paslon` row is deleted automatically.
3. `voting` rows referencing this `id_paslon` are also deleted by CASCADE (`voting_ibfk_3`) — vote records are destroyed.
4. Photo files (`photo1_paslon`, `photo2_paslon` filenames) stored in `gambar/` are **not** deleted from the filesystem.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::cek_paslon()` | Existence check |
| `Admin_m::hapus_paslon()` | DELETE paslon (cascades to detail_paslon and voting) |
| `paslon`, `detail_paslon`, `voting` tables | — |

---

## F-17 Training Photo List

**Controller**: `Photo::index()`  
**Route**: `GET /photo`

### Purpose
Display all voter face training photos with their training status in the Lambda face recognition system.

### Actor
Authenticated administrator

### Inputs
None.

### Outputs
Renders `view/v_photo.php` with `dataPhoto` — a JOIN of `photo + album + detail_user` ordered by `status_train DESC` (trained photos first).

Each row exposes: `id_photo`, `gambar`, `status_train`, `nama_album`, `kode_album`, `nama` (voter name), `identitas`.

### Validations
Session guard.

### Business Rules

1. Photos are ordered so already-trained ones appear at the top.
2. Both trained (`status_train = 1`) and untrained (`status_train = 0`) photos are displayed.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::get_data_photo()` | SELECT photo JOIN album JOIN detail_user |
| `photo`, `album`, `detail_user` tables | — |

---

## F-18 Train Individual Photo to Lambda

**Controller**: `Photo::rekam($idx)`  
**Route**: `GET /photo/rekam/{id}`

### Purpose
Send a single stored voter photo to the Lambda Face Recognition API to train (enroll) the voter's face in the recognition album.

### Actor
Authenticated administrator

### Inputs

| Parameter | Type | Source | Required |
|---|---|---|---|
| `$idx` | integer | URL segment | Yes |

### Outputs

**Success**: Flash "Berhasil, jumlah photo tersimpan ke dalam album `{album}` adalah `{count}` gambar" (green); redirect to `/Photo`.  
**API failure**: Flash "Perekaman data Gagal!" (red); redirect to `/Photo`.  
**Invalid ID**: Redirect to `/Photo` silently.

### Validations

1. `$idx` must not be null or empty.
2. Photo must exist in DB: `data_photo($id)` (JOINs album) must return rows.

### Business Rules

1. Calls Lambda API endpoint `POST /album_train` with:
   - `urls`: hardcoded base `http://facevoting.xyz/gambar/user/{gambar}`
   - `album`: album name from DB
   - `albumkey`: album hash from DB
   - `entryid`: voter's `entry_id` from photo row
2. On success (non-null response), response is JSON-decoded and the flash message shows the album name and total image count.
3. `status_train` column is **not updated** after a successful admin-panel train — only `Gambar::tambah_post()` sets `status_train`. `[BUG]`
4. Album ID is hardcoded to `3` in `rebuild()` and `viewAlbum()`, but `rekam()` uses the dynamic album ID from the photo's JOIN.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::data_photo()` | Fetch photo + album data |
| Lambda Face Recognition API | `POST /album_train` via cURL |
| `photo`, `album` tables | — |
| `gambar/user/` directory | Photo must be physically present |

### Known Issues

- `[RISK]` The photo URL is hardcoded as `http://facevoting.xyz` instead of `base_url()`. In development or staging environments the Lambda API will attempt to fetch from a production domain that may not exist or may have stale data.

---

## F-19 Delete Training Photo

**Controller**: `Photo::hapus($idx)`  
**Route**: `GET /photo/hapus/{id}`

### Purpose
Remove a voter's face training photo from the database and the filesystem.

### Actor
Authenticated administrator

### Inputs

| Parameter | Type | Source | Required |
|---|---|---|---|
| `$idx` | integer | URL segment | Yes |

### Outputs

**Success**: Flash "Photo berhasil dihapus!" (red); redirect to `/Photo`.  
**Invalid**: Redirect to `/Photo` silently.

### Validations

1. `$idx` must not be null or empty.
2. Photo must exist: `data_photo($id)` must return rows.

### Business Rules

1. Reads the filename (`gambar`) before deleting the DB row.
2. Calls `hapus_photo($id)` → `DELETE FROM photo WHERE id_photo = $id`.
3. Calls `unlink(FCPATH . "gambar/user/" . $namaPhoto)` to delete the file from disk.
4. The photo is **not** removed from the Lambda album — the face index becomes stale until the album is rebuilt.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::data_photo()` | Fetch photo details |
| `Admin_m::hapus_photo()` | DELETE from photo |
| `photo` table | — |
| `gambar/user/` directory | File deletion via `unlink()` |
| Lambda API | Indirectly affected — stale index |

---

## F-20 Face Recognition Album View

**Controller**: `Photo::viewAlbum()`  
**Route**: `GET /photo/viewAlbum`

### Purpose
Fetch and display the raw contents of the Lambda face recognition album (all indexed faces and their entry IDs).

### Actor
Authenticated administrator

### Inputs
None (album ID hardcoded to `3`).

### Outputs
Renders `view/v_detailalbum.php` with `getViewAlbum` — raw JSON string from Lambda API.

### Business Rules

1. Album ID `3` is hardcoded in the controller.
2. Calls Lambda `GET /album?album={name}&albumkey={key}`.
3. On cURL error, `null` is returned and the view receives an empty value.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::data_album(3)` | Fetches album name and kode_album |
| Lambda Face Recognition API | `GET /album` |
| `album` table | — |

---

## F-21 Face Recognition Album Rebuild

**Controller**: `Photo::rebuild()`  
**Route**: `GET /photo/rebuild`

### Purpose
Trigger a rebuild of the Lambda face recognition index, re-indexing all trained photos in the album. Used after photos are added or removed to refresh recognition accuracy.

### Actor
Authenticated administrator

### Inputs
None (album ID hardcoded to `3`).

### Outputs
Renders `view/v_rebuildalbum.php` with `getRebuild` — raw JSON string from Lambda API rebuild response.

### Business Rules

1. Calls Lambda `GET /album_rebuild?album={name}&albumkey={key}`.
2. Album ID `3` is hardcoded.
3. On cURL error, `null` is passed to the view.
4. This operation rebuilds the Lambda-side index from whatever photos are already trained there — it does not re-upload local photos.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::data_album(3)` | Fetches album credentials |
| Lambda Face Recognition API | `GET /album_rebuild` |
| `album` table | — |

---

## F-22 Voting Results View

**Controller**: `Hasil::index()`  
**Route**: `GET /hasil`

### Purpose
Display a leaderboard of all candidate pairs sorted by vote count descending, across all categories.

### Actor
Authenticated administrator

### Inputs
None.

### Outputs
Renders `view/v_hasil.php` with `dataPaslon` — all `paslon` rows (JOINed with `kategori` and `detail_paslon`) ordered by `perolehan DESC`.

### Validations

Session guard. Note: the token comparison is present in the constructor but the `index()` method does **not** call `_unlogin()` on mismatch before rendering — `[BUG]`: a token-mismatched session still renders the results page.

### Business Rules

1. Results are sorted by `paslon.perolehan` (the denormalized vote counter), not by a live `COUNT()` from the `voting` table.
2. All categories are shown together — there is no per-category filtering on this page.
3. No access control on which categories' results are visible.

### Dependencies

| Dependency | Detail |
|---|---|
| `Admin_m::get_data_paslon2()` | SELECT paslon JOIN kategori JOIN detail_paslon ORDER BY perolehan DESC |
| `paslon`, `kategori`, `detail_paslon` tables | — |

---

---

# Mobile API

---

## F-23 Voter Registration

**Controller**: `api/Daftar::tambah_post()`  
**Endpoint**: `POST /api/daftar/tambah`  
**Auth**: None

### Purpose
Register a new voter account from the mobile application. Account starts in pending state until activated by an administrator.

### Actor
Unauthenticated mobile user

### Inputs

| Field | Type | Required | Stored in |
|---|---|---|---|
| `nama` | string | Yes | `detail_user.nama` |
| `identitas` | string | Yes | `detail_user.identitas` |
| `email` | string | Yes | `user.email` |
| `password` | string | Yes | `user.password` (SHA1-hashed) |
| `token_firebase` | string | No | `user.token_firebase` |

### Outputs

**Success (HTTP 200)**:
```json
{
  "status": "Success",
  "message": "Data User berhasil disimpan!",
  "id_user": 12,
  "validasi": 2,
  "token_login": "A3B7X9Z1KP"
}
```

**Email already registered (HTTP 404)**:
```json
{
  "status": "Failed",
  "message": "Email sudah terdaftar, silahkan gunakan email yang lain!"
}
```

### Validations

1. Email must not already exist in `user` table (checked via `cekEmail()`).

### Business Rules

1. Password is hashed with `sha1($password)` — weak hashing.
2. `entry_id` is generated as `substr(sha1(time()), 1, 10)` — a 10-character substring of a SHA1 of the current Unix timestamp.
3. `token_login` is generated as `substr(str_shuffle('0123456789ABCDEF...'), 1, 10)` — a 10-character random alphanum string.
4. Account is created with `status = 2` (pending activation).
5. The token is returned immediately but is effectively unusable until the admin sets `status = 1`.
6. Two DB inserts occur: `user` first (returns `insert_id`), then `detail_user` with that ID.
7. No input validation beyond email duplication — empty strings for name, identitas, or password are accepted at the API level.

### Dependencies

| Dependency | Detail |
|---|---|
| `Api_model::cekEmail()` | Uniqueness check |
| `Api_model::simpan_data_user()` | INSERT user |
| `Api_model::simpan_data_detail()` | INSERT detail_user |
| `user`, `detail_user` tables | — |

### Known Issues

- `[RISK]` SHA1 password hashing is cryptographically broken. Passwords stored in plain SHA1 are vulnerable to rainbow table attacks.
- `[RISK]` No server-side validation of field formats (email format, password minimum length, name minimum length).
- `[RISK]` `entry_id` collision is possible — `sha1(time())` produces the same value for registrations within the same second.

---

## F-24 Voter Login

**Controller**: `api/Login::otentikasi_post()`  
**Endpoint**: `POST /api/login/otentikasi`  
**Auth**: None

### Purpose
Authenticate a voter with email and password. Returns a session token used by subsequent API calls.

### Actor
Registered, activated voter (mobile app)

### Inputs

| Field | Type | Required |
|---|---|---|
| `email` | string | Yes |
| `password` | string | Yes (plaintext, SHA1-hashed server-side) |

### Outputs

**Success (HTTP 200)**:
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

**Invalid credentials (HTTP 404)**:
```json
{
  "message": "Username atau Password yang anda masukkan salah"
}
```

### Validations

1. `validasi_login(email, sha1(password))` must return `num_rows() > 0`.

### Business Rules

1. Password is SHA1-hashed before DB comparison.
2. No check is made against `user.status` before returning a token — a pending user (status=2) receives a valid token. The `validasi` field in the response carries the status so the mobile app can decide to block pending accounts, but this is not enforced server-side.
3. A new `token_login` is generated on every login and overwrites the previous value in `user.token_login`.
4. Account profile data (`nama`, `identitas`) is fetched via a second query `cekEmail()` after the credential check.

### Dependencies

| Dependency | Detail |
|---|---|
| `Api_model::validasi_login()` | Credential check |
| `Api_model::cekEmail()` | Fetch profile after login |
| `Api_model::simpan_token_login()` | UPDATE user.token_login |
| `user`, `detail_user` tables | — |

### Known Issues

- `[RISK]` Pending accounts (status=2) can log in and receive a valid token. Rate limiting and session control must be handled by the mobile app.

---

## F-25 Token Refresh

**Controller**: `api/Login::sudahlogin_post()`  
**Endpoint**: `POST /api/login/sudahlogin`  
**Auth**: None (takes `id_user`)

### Purpose
Issue a new session token for a user who is already logged in (e.g., on app restart with a stored user ID).

### Actor
Mobile app (on behalf of previously authenticated voter)

### Inputs

| Field | Type | Required |
|---|---|---|
| `id_user` | integer | Yes |

### Outputs

**Success (HTTP 200)**:
```json
{
  "token_login": "X9Z1KPA3B7",
  "id_user": 10,
  "email": "alexistdev@gmail.com",
  "validasi": 1
}
```

**User not found (HTTP 404)**:
```json
{ "message": "Gagal mendapatkan data" }
```

### Validations

1. User must exist in `user` table by `id_user`.

### Business Rules

1. Generates a new `token_login` and saves it, replacing the previous value.
2. No password or existing token verification is required — any caller who knows a valid `id_user` can obtain a new token for that user. `[RISK]`

### Dependencies

| Dependency | Detail |
|---|---|
| `Api_model::get_data_user()` | Existence check |
| `Api_model::simpan_token_login()` | Save new token |
| `user` table | — |

### Known Issues

- `[RISK]` This endpoint is an account takeover vector — any actor who knows a `id_user` value (which is a simple auto-increment integer) can silently rotate that user's session token, logging out the legitimate user.

---

## F-26 Account Status Check

**Controller**: `api/Login::cekstatus_get()`  
**Endpoint**: `GET /api/login/cekstatus?id_user={id}`  
**Auth**: None

### Purpose
Check whether a voter account is activated and retrieve basic profile data. Used by the mobile app on startup to determine UI state.

### Actor
Mobile app (unauthenticated call)

### Inputs

| Parameter | Type | Source | Required |
|---|---|---|---|
| `id_user` | integer | GET query string | Yes |

### Outputs

**Success (HTTP 200)**:
```json
{
  "validasi": 1,
  "token_login": "A3B7X9Z1KP",
  "nama": "Alexsander Hendra Wijaya",
  "identitas": "123456",
  "message": "Berhasil mendapatkan data"
}
```

**Not found (HTTP 404)**:
```json
{ "message": "Gagal mendapatkan data" }
```

### Validations

1. User must exist by `id_user` (joined with `detail_user`).

### Business Rules

1. Returns the current `token_login` in the response — exposing the active session token to any unauthenticated caller who knows `id_user`. `[RISK]`

### Dependencies

| Dependency | Detail |
|---|---|
| `Api_model::get_data_akun()` | SELECT user JOIN detail_user WHERE id_user |
| `user`, `detail_user` tables | — |

### Known Issues

- `[RISK]` Unauthenticated endpoint that returns the current `token_login` — an attacker who guesses or enumerates `id_user` can obtain any user's active session token without credentials.

---

## F-27 View Account Profile

**Controller**: `api/Akun::tampil_get()`  
**Endpoint**: `GET /api/akun/tampil?id_user={id}`  
**Auth**: Token (manual check not implemented — no token validation in this controller)

### Purpose
Return the voter's profile information (name, identity number, email).

### Actor
Authenticated voter (mobile app)

### Inputs

| Parameter | Type | Source | Required |
|---|---|---|---|
| `id_user` | integer | GET query string | Yes |

### Outputs

**Success (HTTP 200)**:
```json
{
  "email": "alexistdev@gmail.com",
  "nama": "Alexsander Hendra Wijaya",
  "identitas": "123456"
}
```

**Not found (HTTP 404)**:
```json
{ "status": "gagal", "message": "data kosong!" }
```

### Validations

1. User must exist by `id_user`.

### Business Rules

1. No token validation is performed in this endpoint — any caller with a known `id_user` can read any voter's profile. `[RISK]`

### Dependencies

| Dependency | Detail |
|---|---|
| `Api_model::get_data_akun()` | SELECT user JOIN detail_user |
| `user`, `detail_user` tables | — |

---

## F-28 Update Account Profile

**Controller**: `api/Akun::tampil_put($idUser)`  
**Endpoint**: `PUT /api/akun/tampil/{id}`  
**Auth**: Token (not validated in controller — see risks)

### Purpose
Allow a voter to update their profile name, identity number, and optionally their password.

### Actor
Authenticated voter (mobile app)

### Inputs

| Field | Type | Source | Required |
|---|---|---|---|
| `nama` | string | PUT body | Yes |
| `identitas` | string | PUT body | Yes |
| `password` | string | PUT body | No (optional) |

### Outputs

**Success (HTTP 200)**:
```json
{ "status": "berhasil", "message": "Data berhasil diperbaharui" }
```

**User not found (HTTP 404)**:
```json
{ "status": "gagal", "message": "Silahkan relogin!" }
```

**DB update failed (HTTP 404)**:
```json
{ "status": "gagal", "message": "Gagal menyimpan ke dalam server!" }
```

### Validations

1. User must exist by `$idUser`.

### Business Rules

1. If `password` field is non-empty, it is SHA1-hashed and saved to `user.password`.
2. `nama` and `identitas` are always updated (even if empty strings — no required validation).
3. No current-password verification before changing the password.
4. No token validation — any caller who knows the `id_user` can overwrite another voter's profile and password. `[RISK]`

### Dependencies

| Dependency | Detail |
|---|---|
| `Api_model::get_data_akun()` | Existence check |
| `Api_model::update_password()` | UPDATE user.password |
| `Api_model::update_detail_user()` | UPDATE detail_user |
| `user`, `detail_user` tables | — |

---

## F-29 List Authorized Categories

**Controller**: `api/Kategori::tampil_get()`  
**Endpoint**: `GET /api/kategori/tampil?id_user={id}`  
**Auth**: Token (not validated)

### Purpose
Return the list of election categories that a specific voter is authorized to vote in (status = 1 — not yet voted).

### Actor
Authenticated voter (mobile app)

### Inputs

| Parameter | Type | Source | Required |
|---|---|---|---|
| `id_user` | integer | GET query string | Yes |

### Outputs

**Success (HTTP 200)**:
```json
{
  "status": "berhasil",
  "result": [
    {
      "id_otorisasi": 8,
      "id_user": 10,
      "id_kategori": 8,
      "status": 1,
      "id_kategori": 8,
      "nama_kategori": "BEM",
      "logo_kategori": "bem_logo.png",
      "status_kategori": 2
    }
  ],
  "message": "Data berhasil didapatkan"
}
```

**No authorized categories (HTTP 404)**:
```json
{ "status": "gagal", "result": [], "message": "data kosong!" }
```

### Validations

1. At least one `otorisasi_pemilih` row with `status = 1` must exist for this user.

### Business Rules

1. `get_data_otorisasi($id)` applies `WHERE id_user = $id AND status = 1`. Only categories where the user has not yet voted are returned.
2. The result JOINs `kategori`, so each row includes category details including `status_kategori`.
3. The mobile app should filter displayed categories by `status_kategori = 2` (open for voting), but the server returns all authorized/unvoted categories regardless of election status.

### Dependencies

| Dependency | Detail |
|---|---|
| `Api_model::get_data_otorisasi()` | SELECT otorisasi_pemilih JOIN kategori WHERE id_user AND status=1 |
| `otorisasi_pemilih`, `kategori` tables | — |

---

## F-30 List All Open Categories

**Controller**: `api/Kategori::semua_get()`  
**Endpoint**: `GET /api/kategori/semua`  
**Auth**: None

### Purpose
Return all currently open election categories. Used to browse elections without authentication.

### Actor
Any mobile user (unauthenticated)

### Inputs
None.

### Outputs

**Success (HTTP 200)**:
```json
{
  "status": "berhasil",
  "result": [{ "id_kategori": 8, "nama_kategori": "BEM", ... }],
  "message": "Data berhasil didapatkan"
}
```

**No open categories (HTTP 404)**:
```json
{ "status": "gagal", "result": [], "message": "data kosong!" }
```

### Business Rules

1. Returns categories WHERE `status_kategori = 2` (open).

### Dependencies

| Dependency | Detail |
|---|---|
| `Api_model::get_data_kategori_hasil()` | SELECT kategori WHERE status_kategori=2 |
| `kategori` table | — |

---

## F-31 List Candidates by Category

**Controller**: `api/Paslon::tampil_post()`  
**Endpoint**: `POST /api/paslon/tampil`  
**Auth**: Token (not validated)

### Purpose
Return all candidate pairs for a given election category, including their profile details.

### Actor
Voter (mobile app)

### Inputs

| Field | Type | Source | Required |
|---|---|---|---|
| `id_kategori` | integer | POST body | Yes |

### Outputs

**Success (HTTP 200)**:
```json
{
  "status": "berhasil",
  "result": [
    {
      "id_paslon": 1,
      "judul_paslon": "Paslon 1",
      "ketua_paslon": "Steve Job",
      "wakil_paslon": "Steve Wozniak",
      "photo1_paslon": "stevejob.jpg",
      "photo2_paslon": "stevewozniak.jpg",
      "perolehan": 0,
      "visi_misi": "...",
      "profil_catum": "...",
      "profil_cawatum": "..."
    }
  ],
  "message": "Data berhasil didapatkan"
}
```

**No candidates (HTTP 404)**:
```json
{ "status": "gagal", "result": [], "message": "data kosong!" }
```

### Business Rules

1. `get_data_paslon($id)` JOINs `detail_paslon` — profile text is always included.
2. All candidate pairs for the category are returned regardless of vote count.
3. Photo filenames (not URLs) are returned; the mobile app constructs the full URL using the known base path.

### Dependencies

| Dependency | Detail |
|---|---|
| `Api_model::get_data_paslon()` | SELECT paslon JOIN detail_paslon WHERE id_kategori |
| `paslon`, `detail_paslon` tables | — |

---

## F-32 Candidate Detail

**Controller**: `api/Paslon::detail_post()`  
**Endpoint**: `POST /api/paslon/detail`  
**Auth**: Token (not validated)

### Purpose
Return the full profile of a single candidate pair.

### Actor
Voter (mobile app)

### Inputs

| Field | Type | Source | Required |
|---|---|---|---|
| `id_paslon` | integer | POST body | Yes |

### Outputs

**Success (HTTP 200)**:
```json
{
  "status": "berhasil",
  "result": [{
    "id_detailpaslon": 2,
    "id_paslon": 1,
    "visi_misi": "...",
    "profil_catum": "...",
    "profil_cawatum": "...",
    "judul_paslon": "Paslon 1",
    "ketua_paslon": "Steve Job",
    "wakil_paslon": "Steve Wozniak"
  }],
  "message": "Data berhasil didapatkan"
}
```

### Business Rules

1. `get_detail_paslon($id)` queries from `detail_paslon` JOINing `paslon`. Returns combined profile + basic candidate data.

### Dependencies

| Dependency | Detail |
|---|---|
| `Api_model::get_detail_paslon()` | SELECT detail_paslon JOIN paslon WHERE id_paslon |
| `paslon`, `detail_paslon` tables | — |

---

## F-33 Upload Face Training Photo

**Controller**: `api/Gambar::tambah_post()`  
**Endpoint**: `POST /api/gambar/tambah`  
**Auth**: Token (not validated — only `id_user` is checked)

### Purpose
Upload a voter's face photo to the server for use as their identity reference during vote verification. Stores the photo locally and records it in the database.

### Actor
Activated voter (mobile app)

### Inputs

| Field | Type | Source | Required |
|---|---|---|---|
| `id_user` | integer | POST body | Yes |
| `upload` | file | POST multipart | Yes |

### Outputs

**Success (HTTP 200)**:
```json
{
  "status": "success",
  "message": "Gambar berhasil diupload",
  "nama_file": "b6a0595f28.jpg"
}
```

**Upload failure (HTTP 404)**:
```json
{ "status": "failed", "message": "{CI upload error}" }
```

**User not found (HTTP 404)**:
```json
{ "status": "failed", "message": "User tidak ditemukan!" }
```

### Validations

1. User must exist by `id_user`.
2. File type must be jpg, png, or jpeg.
3. File size must not exceed 10,000 KB (≈ 9.77 MB).
4. Image dimensions must not exceed 4,200 × 4,200 px.

### Business Rules

1. Filename is generated as `substr(sha1(time()), 1, 10)` — 10-char hex.
2. File is stored in `gambar/user/` relative to the project root.
3. A `photo` row is inserted with `status_train = 2` — note this is inconsistent with the described `0=untrained / 1=trained` convention used by the admin photo list. `[BUG]`
4. `id_album` is hardcoded to `3` in the insert — always uses the first/only configured album.
5. `entry_id` is read from `user.entry_id` to tag the face in Lambda.
6. The Lambda API training call (`album_train`) is **not** made in this endpoint (unlike described in the system-analysis doc). The photo is saved locally but not trained — `status_train = 2` is the stored state without Lambda confirmation. `[BUG]` The admin must trigger training separately via F-18.

### Dependencies

| Dependency | Detail |
|---|---|
| `Api_model::get_data_user()` | User existence check |
| `Api_model::simpan_photo()` | INSERT photo |
| CI `upload` library | File handling |
| `gambar/user/` directory | Must be writable |
| `photo` table | — |

---

## F-34 Face Verification

**Controller**: `api/Gambar::cek_post()`  
**Endpoint**: `POST /api/gambar/cek`  
**Auth**: Token (not validated — only `id_user` is checked)

### Purpose
Verify the voter's identity before allowing a vote: uploads a live photo and compares it against the stored training photo using FaceXAPI. Returns a match result that the mobile app uses to gate the voting step.

### Actor
Voter attempting to vote (mobile app)

### Inputs

| Field | Type | Source | Required |
|---|---|---|---|
| `id_user` | integer | POST body | Yes |
| `upload` | file | POST multipart | Yes |

### Outputs

**Match result success (HTTP 200)**:
```json
{
  "status": "success",
  "message": "<FaceXAPI match status string>"
}
```

**No training photo on file (HTTP 404)**:
```json
{ "status": "nopic", "message": "Anda belum melakukan perekaman!" }
```

**FaceXAPI unreachable (HTTP 404)**:
```json
{ "status": "failed", "message": "Sementara tidak dapat melakukan voting!" }
```

**Upload failure (HTTP 404)**:
```json
{ "status": "failed", "message": "{CI upload error}" }
```

### Validations

1. User must exist by `id_user`.
2. File constraints: jpg/png/jpeg, max 10,000 KB, max 4,200×4,200 px.
3. User must have at least one training photo in the `photo` table.

### Business Rules

1. Verification photo is saved to `gambar/user/` with a generated filename.
2. A `pencocokan` row is inserted with the filename and empty score before the comparison is made (score is not saved back after comparison). `[BUG]`
3. `get_data_photo($idUser)` fetches the **first** photo row for the user (no ordering specified — row returned is non-deterministic if multiple photos exist).
4. `_banding($photoAwal, $photoNew)` calls FaceXAPI `POST /match_faces` with hardcoded base URL `http://facevoting.xyz/gambar/user/`.
5. The response field `$json->data->status` is a string (e.g., `"matched"` / `"unmatched"`) — not a numeric score. The mobile app interprets this string to decide whether to allow the vote.
6. This endpoint does not itself accept or reject the vote — it only returns the comparison result. The mobile app is responsible for gating.

### Dependencies

| Dependency | Detail |
|---|---|
| `Api_model::get_data_user()` | User existence check |
| `Api_model::simpan_pencocokan()` | INSERT pencocokan |
| `Api_model::get_data_photo()` | Fetch first training photo |
| FaceXAPI | `POST /match_faces` via cURL |
| CI `upload` library | Verification file upload |
| `gambar/user/` directory | Both training and verification photos must be accessible via `http://facevoting.xyz` |
| `photo`, `pencocokan` tables | — |

### Known Issues

- `[RISK]` The matching decision is made entirely by the mobile app based on the returned string. A tampered or proxied client could ignore a "unmatched" result and proceed to vote.
- `[RISK]` Base URL for FaceXAPI comparison is hardcoded to `http://facevoting.xyz` — will fail in development/staging.
- `[BUG]` `pencocokan.score` is inserted as empty string and never updated with the actual comparison result — the audit log is effectively blank.

---

## F-35 Submit Vote

**Controller**: `api/Suara::vote_post()`  
**Endpoint**: `POST /api/suara/vote`  
**Auth**: Token (not validated)

### Purpose
Record a voter's choice for a candidate pair in an election category. Increments the candidate's vote counter and marks the voter as having voted.

### Actor
Voter who has passed face verification (mobile app)

### Inputs

| Field | Type | Source | Required |
|---|---|---|---|
| `id_user` | integer | POST body | Yes |
| `id_kategori` | integer | POST body | Yes |
| `id_paslon` | integer | POST body | Yes |

### Outputs

**Success (HTTP 200)**:
```json
{ "status": "berhasil", "message": "Berhasil Voting" }
```

### Validations

None enforced server-side beyond implicit DB constraints. `[BUG]`

### Business Rules

1. Reads current `paslon.perolehan`, increments by 1, and writes it back (read-modify-write — race condition possible under concurrent votes). `[BUG]`
2. Inserts a `voting` row with `tanggal_voting = date('Y-m-d H:i:s')`.
3. Updates `otorisasi_pemilih.status = 2` WHERE `id_user = $idUser` (no `id_kategori` filter). `[BUG]` — all authorization records for this user across all categories are set to status=2.
4. No check that `otorisasi_pemilih` record exists or has `status = 1` before voting.
5. No check that the election is open (`status_kategori = 2`).
6. No check that the candidate belongs to the stated category.
7. No face verification result is validated server-side — the mobile app is trusted to have completed F-34 and acted on the result.

### Dependencies

| Dependency | Detail |
|---|---|
| `Api_model::get_data_paslon2()` | Read current perolehan |
| `Api_model::perbaharui_paslon()` | UPDATE paslon.perolehan |
| `Api_model::simpan_voting()` | INSERT voting |
| `Api_model::perbaharui_data_otorisasi()` | UPDATE otorisasi_pemilih.status=2 WHERE id_user |
| `paslon`, `voting`, `otorisasi_pemilih` tables | — |

### Known Issues

- `[BUG]` No authorization check — any caller with any `id_user`, `id_kategori`, `id_paslon` can submit a vote. An unauthenticated attacker can vote repeatedly.
- `[BUG]` `perbaharui_data_otorisasi` uses `WHERE id_user` only, not `AND id_kategori`. Voting in category A also marks authorization as used in categories B, C, etc.
- `[BUG]` Race condition on `perolehan`: concurrent votes fetch the same counter value and both write `n+1`, losing one vote.
- `[RISK]` No server-side face verification enforcement — vote integrity depends entirely on mobile app honesty.

---

## F-36 Voting History

**Controller**: `api/Suara::tampil_get($idUser)`  
**Endpoint**: `GET /api/suara/tampil/{id_user}`  
**Auth**: Token (not validated)

### Purpose
Return the list of votes previously cast by a voter, including the category and timestamp.

### Actor
Voter (mobile app)

### Inputs

| Parameter | Type | Source | Required |
|---|---|---|---|
| `id_user` | integer | URL segment | Yes |

### Outputs

**Success (HTTP 200)**:
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
      "nama": "Alexsander Hendra Wijaya",
      "nama_kategori": "BEM"
    }
  ],
  "message": "Data berhasil didapatkan"
}
```

**No votes (HTTP 404)**:
```json
{ "status": "gagal", "message": "Silahkan relogin!" }
```

### Business Rules

1. `get_data_voting($idUser)` JOINs `detail_user` and `kategori` to enrich the response.
2. No token validation — any caller who knows `id_user` can see a voter's voting history. `[RISK]`

### Dependencies

| Dependency | Detail |
|---|---|
| `Api_model::get_data_voting()` | SELECT voting JOIN detail_user JOIN kategori WHERE id_user |
| `voting`, `detail_user`, `kategori` tables | — |

---

## F-37 Vote Counts by Category

**Controller**: `api/Suara::perolehan_get()`  
**Endpoint**: `GET /api/suara/perolehan?id_kategori={id}`  
**Auth**: None

### Purpose
Return the current vote tally for all candidate pairs within a given election category. Used by the mobile app to display live results.

### Actor
Any user (unauthenticated)

### Inputs

| Parameter | Type | Source | Required |
|---|---|---|---|
| `id_kategori` | integer | GET query string | Yes |

### Outputs

**Success (HTTP 200)**:
```json
{
  "status": "berhasil",
  "result": [
    {
      "id_paslon": 2,
      "judul_paslon": "Paslon 1",
      "ketua_paslon": "Rasmus Lerdorf",
      "wakil_paslon": "Brendan Eich",
      "perolehan": 1
    }
  ],
  "message": "Data berhasil didapatkan"
}
```

### Business Rules

1. `get_data_perolehan($idKategori)` queries `paslon WHERE id_kategori = $id` — it returns the denormalized `perolehan` counter, **not** a live `COUNT()` from the `voting` table. The commented-out code in `Api_model` shows an attempted but abandoned proper aggregation query.
2. Always returns HTTP 200 even if no candidates exist (empty result set is still a 200 response).
3. No filter on `status_kategori` — results are readable even for closed elections.

### Dependencies

| Dependency | Detail |
|---|---|
| `Api_model::get_data_perolehan()` | SELECT paslon WHERE id_kategori |
| `paslon` table | — |

---

---

# Cross-Cutting Concerns

## Session Guard (Admin Web)

Applied in the constructor of every protected web controller: `Member`, `User`, `Album`, `Kategori`, `Paslon`, `Photo`, `Hasil`, `Setting`.

**Logic**:
1. If `$_SESSION['is_login_in'] !== TRUE` → `redirect('login')`.
2. Read `$this->tokenServer = Admin_m::get_token_byId($id_admin)->row()->token` from DB.
3. In each method: if `$this->tokenSession != $this->tokenServer` → `_unlogin()`.

**Weakness**: Step 2 will throw a fatal error if `get_token_byId()` returns no rows (token deleted), because `->row()->token` is called on an empty result. `[BUG]`

---

## Token Authentication (Mobile API)

**No centralized middleware exists.** Each API controller is expected to manually validate the `token_login`, but **none of the current controllers actually do so** for any endpoint. The `id_user` field is used directly without verifying that the caller's token matches.

This means every mobile API endpoint is effectively unauthenticated at the server level.

---

## File Upload Constraints Summary

| Context | Field | Path | Allowed Types | Max Size | Max Dimensions |
|---|---|---|---|---|---|
| Category logo | `logo` | `gambar/` | jpg, jpeg, png, gif | 2,424 KB | None |
| Candidate photos | `photo1`, `photo2` | `gambar/` | jpg, jpeg, png, gif | 2,424 KB | None |
| Training photo (API) | `upload` | `gambar/user/` | jpg, png, jpeg | 10,000 KB | 4,200×4,200 px |
| Verification photo (API) | `upload` | `gambar/user/` | jpg, png, jpeg | 10,000 KB | 4,200×4,200 px |

---

## External Service Dependencies

| Service | Used by | Failure Behavior |
|---|---|---|
| Lambda Face Recognition API | F-18, F-21 | Returns `null`; flash error shown or empty view |
| FaceXAPI | F-34 | Returns `null`; API responds "Sementara tidak dapat melakukan voting!" |
| Firebase Cloud Messaging | None (token stored only) | N/A — not used server-side |

---

## Known System-Wide Issues Summary

| ID | Feature | Severity | Description |
|---|---|---|---|
| I-01 | F-01 | Medium | Admin token is `sha1(datetime)` — predictable within 1-second window |
| I-02 | F-02 | Medium | Logout does not delete the token row from DB |
| I-03 | F-11 | High | No duplicate authorization check — double-voting possible |
| I-04 | F-18 | Medium | `status_train` not updated after successful admin train |
| I-05 | F-23 | High | SHA1 password hashing (cryptographically broken) |
| I-06 | F-23 | Medium | `entry_id` collision possible within same second |
| I-07 | F-24 | Medium | Pending accounts receive valid tokens on login |
| I-08 | F-25 | Critical | Token refresh requires only `id_user` — account takeover vector |
| I-09 | F-26 | Critical | Unauthenticated endpoint exposes active `token_login` |
| I-10 | F-33 | Medium | `status_train = 2` used inconsistently (should be 0 for untrained) |
| I-11 | F-33 | Medium | Lambda training not called in `tambah_post()` despite earlier documentation |
| I-12 | F-34 | Medium | `pencocokan.score` always stored as empty string |
| I-13 | F-34 | High | Face match decision enforced only by mobile app — bypassable |
| I-14 | F-35 | Critical | No server-side authorization check before accepting vote |
| I-15 | F-35 | Critical | `perbaharui_data_otorisasi` sets status=2 for all categories — not just voted one |
| I-16 | F-35 | High | Race condition on `perolehan` counter under concurrent votes |
| I-17 | All API | High | No token validation in any API controller — all endpoints effectively public |
