# TASK-20 — API Auth Controller (Register, Login, Refresh, Status)

**Phase**: 5 — Mobile API  
**Depends on**: TASK-04, TASK-19  
**Estimated effort**: 3 hours

---

## Description

Implement the four authentication-related API endpoints: voter registration, voter login, token refresh, and account status check. All response shapes, field names, HTTP status codes, and business logic (including SHA1 password hashing, token generation algorithm, and unauthenticated access) must match CI3 exactly.

---

## Affected Files

### Create
- `app/Http/Controllers/Api/AuthController.php`

### Modify
- `routes/api.php` — auth routes already defined in TASK-19

---

## Implementation Notes

### `register()` — `POST /api/daftar/tambah`

```php
public function register(Request $request): JsonResponse
{
    // No validation on nama, identitas, password format (parity)
    $email = $request->email;

    // Check duplicate: JOIN user + detail_user WHERE email = ?
    $exists = User::where('email', $email)->exists();
    if ($exists) {
        return response()->json([
            'status'  => 'Failed',
            'message' => 'Email sudah terdaftar, silahkan gunakan email yang lain!',
        ], 404);
    }

    // SHA1 password (parity — do NOT use bcrypt for voters)
    $password = sha1($request->password);

    // Token generation — exact CI3 algorithm
    $tokenLogin = substr(str_shuffle('0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 1, 10);
    $entryId    = substr(sha1(time()), 1, 10);

    $user = User::create([
        'email'         => $email,
        'password'      => $password,
        'token_firebase' => $request->token_firebase,
        'token_login'   => $tokenLogin,
        'entry_id'      => $entryId,
        'status'        => 2,
    ]);

    UserDetail::create([
        'id_user'   => $user->id_user,
        'nama'      => $request->nama,
        'identitas' => $request->identitas,
    ]);

    return response()->json([
        'status'      => 'Success',
        'message'     => 'Data User berhasil disimpan!',
        'id_user'     => $user->id_user,
        'validasi'    => 2,
        'token_login' => $tokenLogin,
    ]);
}
```

### `login()` — `POST /api/login/otentikasi`

```php
public function login(Request $request): JsonResponse
{
    $sha1Password = sha1($request->password);

    $count = User::where('email', $request->email)
                 ->where('password', $sha1Password)
                 ->count();

    if ($count === 0) {
        return response()->json([
            'message' => 'Username atau Password yang anda masukkan salah',
        ], 404);
    }

    // Second query to get full user data (CI3 uses cekEmail() — parity)
    $user = User::with('detail')->where('email', $request->email)->first();

    $tokenLogin = substr(str_shuffle('0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 1, 10);
    $user->update(['token_login' => $tokenLogin]);

    return response()->json([
        'token_login' => $tokenLogin,
        'id_user'     => $user->id_user,
        'nama'        => $user->detail->nama,
        'identitas'   => $user->detail->identitas,
        'email'       => $user->email,
        'validasi'    => $user->status,
    ]);
}
```

> Response field `'validasi'` maps to `user.status`. Pending users (status=2) receive a token — no status check (parity).

### `refresh()` — `POST /api/login/sudahlogin`

```php
public function refresh(Request $request): JsonResponse
{
    $user = User::find($request->id_user);

    if (!$user) {
        return response()->json(['message' => 'Gagal mendapatkan data'], 404);
    }

    $tokenLogin = substr(str_shuffle('0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 1, 10);
    $user->update(['token_login' => $tokenLogin]);

    return response()->json([
        'token_login' => $tokenLogin,
        'id_user'     => $user->id_user,
        'email'       => $user->email,
        'validasi'    => $user->status,
    ]);
}
```

> No password or token verification — only `id_user` required (parity with CI3 security flaw).
> Response has 4 fields (NOT 6 — no `nama` or `identitas` — different from login response).

### `status()` — `GET /api/login/cekstatus?id_user={id}`

```php
public function status(Request $request): JsonResponse
{
    $user = User::with('detail')->where('id_user', $request->query('id_user'))->first();

    if (!$user) {
        return response()->json(['message' => 'Gagal mendapatkan data'], 404);
    }

    return response()->json([
        'validasi'    => $user->status,
        'token_login' => $user->token_login,    // exposed unauthenticated — parity
        'nama'        => $user->detail->nama,
        'identitas'   => $user->detail->identitas,
        'message'     => 'Berhasil mendapatkan data',
    ]);
}
```

---

## Acceptance Criteria

- [ ] `POST /api/daftar/tambah` with new email → HTTP 200, 5 fields: `status`, `message`, `id_user`, `validasi`, `token_login`
- [ ] `POST /api/daftar/tambah` — `validasi` is always `2` on success
- [ ] `POST /api/daftar/tambah` — `status` is `"Success"` (capital S)
- [ ] `POST /api/daftar/tambah` with duplicate email → HTTP 404, `status: "Failed"` (capital F)
- [ ] Registered voter's password in DB is `sha1($password)` (40-char hex string, NOT bcrypt)
- [ ] `POST /api/login/otentikasi` with valid credentials → HTTP 200, exactly 6 fields
- [ ] `POST /api/login/otentikasi` — `validasi` reflects `user.status` (can be 1 or 2)
- [ ] `POST /api/login/otentikasi` with wrong credentials → HTTP 404, `message: "Username atau Password yang anda masukkan salah"` (no period)
- [ ] `POST /api/login/sudahlogin` with valid `id_user` → HTTP 200, exactly 4 fields (no `nama`, `identitas`)
- [ ] `POST /api/login/sudahlogin` with invalid `id_user` → HTTP 404, `message: "Gagal mendapatkan data"`
- [ ] `POST /api/login/sudahlogin` requires NO password or token — only `id_user`
- [ ] `GET /api/login/cekstatus?id_user=10` → HTTP 200, 5 fields including `token_login`
- [ ] `GET /api/login/cekstatus?id_user=99999` → HTTP 404, `message: "Gagal mendapatkan data"`
- [ ] All endpoints accessible without Authorization header
