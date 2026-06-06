# TASK-21 — API Account Controller (View Profile, Update Profile)

**Phase**: 5 — Mobile API  
**Depends on**: TASK-04, TASK-19  
**Estimated effort**: 1.5 hours

---

## Description

Implement the two account profile endpoints: view profile and update profile (with optional password change). Both endpoints use query param `id_user` for identification and require no authentication (parity with CI3).

---

## Affected Files

### Create
- `app/Http/Controllers/Api/AccountController.php`

### Modify
- `routes/api.php` — already defined in TASK-19

---

## Implementation Notes

### `show()` — `GET /api/akun/tampil?id_user={id}`

```php
public function show(Request $request): JsonResponse
{
    $user = User::with('detail')
        ->where('id_user', $request->query('id_user'))
        ->first();

    if (!$user) {
        return response()->json([
            'status'  => 'gagal',
            'message' => 'data kosong!',
        ], 404);
    }

    return response()->json([
        'email'     => $user->email,
        'nama'      => $user->detail->nama,
        'identitas' => $user->detail->identitas,
    ]);
}
```

> Error response uses lowercase `"gagal"` and `"data kosong!"`. Exact parity required.
> Success response has exactly 3 fields: `email`, `nama`, `identitas` — no `id_user`, no `status`.

### `update()` — `PUT /api/akun/tampil/{id_user}`

```php
public function update(Request $request, $id_user): JsonResponse
{
    $user = User::with('detail')->where('id_user', $id_user)->first();

    if (!$user) {
        return response()->json([
            'status'  => 'gagal',
            'message' => 'Silahkan relogin!',
        ], 404);
    }

    // Update password if provided
    if ($request->filled('password')) {
        $user->update(['password' => sha1($request->password)]);
    }

    // Update detail_user
    $result = $user->detail->update([
        'nama'      => $request->nama,
        'identitas' => $request->identitas,
    ]);

    if (!$result) {
        return response()->json([
            'status'  => 'gagal',
            'message' => 'Gagal menyimpan ke dalam server!',
        ], 404);
    }

    return response()->json([
        'status'  => 'berhasil',
        'message' => 'Data berhasil diperbaharui',
    ]);
}
```

> `"status": "berhasil"` (lowercase). Password SHA1-hashed if non-empty. No validation on format.

---

## Acceptance Criteria

- [ ] `GET /api/akun/tampil?id_user=10` → HTTP 200 with 3 fields: `email`, `nama`, `identitas`
- [ ] `GET /api/akun/tampil?id_user=99999` → HTTP 404 with `{"status":"gagal","message":"data kosong!"}`
- [ ] Error strings are lowercase (`"gagal"`, `"data kosong!"`)
- [ ] `PUT /api/akun/tampil/10` with `nama` and `identitas` → updates `detail_user`, HTTP 200
- [ ] `PUT /api/akun/tampil/10` with `password` field → SHA1 hash stored in `user.password`
- [ ] `PUT /api/akun/tampil/10` without `password` field → password unchanged
- [ ] `PUT /api/akun/tampil/99999` → HTTP 404 with `{"status":"gagal","message":"Silahkan relogin!"}`
- [ ] `"status": "berhasil"` (lowercase b) on successful update
- [ ] Both endpoints accessible without Authorization header
