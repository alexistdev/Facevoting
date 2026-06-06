# TASK-24 — API Photo Controller (Enroll, Verify)

**Phase**: 5 — Mobile API  
**Depends on**: TASK-04, TASK-17 (FaceMatchService), TASK-18 (file storage), TASK-19  
**Estimated effort**: 3 hours

---

## Description

Implement the two photo API endpoints: training photo upload (enroll) and face verification (verify). The enroll endpoint saves the photo to `gambar/user/` and inserts a `photo` row with `status_train = 2`. The verify endpoint uploads a photo, calls FaceXAPI to compare it against the first training photo, and returns the match result.

---

## Affected Files

### Create
- `app/Http/Controllers/Api/PhotoController.php`

### Modify
- `routes/api.php` — already defined in TASK-19

---

## Implementation Notes

### `enroll()` — `POST /api/gambar/tambah`

```php
public function enroll(Request $request): JsonResponse
{
    $user = User::find($request->id_user);
    if (!$user) {
        return response()->json(['status' => 'failed', 'message' => 'User tidak ditemukan!'], 404);
    }

    // File upload validation (match CI3 upload library config)
    $request->validate([
        'upload' => ['required', 'file', 'mimes:jpg,png,jpeg', 'max:10000',
                     'dimensions:max_width=4200,max_height=4200'],
    ]);

    $extension = $request->file('upload')->getClientOriginalExtension();
    $stem      = substr(sha1(time()), 1, 10);
    $filename  = $stem . '.' . $extension;

    // Store to gambar/user/ path
    $request->file('upload')->storeAs('images/users', $filename, 'public');

    Photo::create([
        'id_album'    => 3,                  // hardcoded
        'id_user'     => $request->id_user,
        'entry_id'    => $user->entry_id,
        'nama_photo'  => $stem,
        'gambar'      => $filename,
        'status_train' => 2,                 // parity: stores 2, not 0
    ]);

    return response()->json([
        'status'    => 'success',
        'message'   => 'Gambar berhasil diupload',
        'nama_file' => $filename,
    ]);
}
```

> Field name for the file is `upload` — not `file` or `image` (parity).
> `status_train = 2` on upload (parity — CI3 bug preserved).
> `id_album` hardcoded to `3`.

### `verify()` — `POST /api/gambar/cek`

```php
public function verify(Request $request, FaceMatchService $faceMatch): JsonResponse
{
    $user = User::find($request->id_user);
    if (!$user) {
        return response()->json(['status' => 'failed', 'message' => 'User tidak ditemukan!'], 404);
    }

    // Upload verification photo (same constraints as enroll)
    $request->validate([
        'upload' => ['required', 'file', 'mimes:jpg,png,jpeg', 'max:10000',
                     'dimensions:max_width=4200,max_height=4200'],
    ]);

    $extension = $request->file('upload')->getClientOriginalExtension();
    $stem      = substr(sha1(time()), 1, 10);
    $filename  = $stem . '.' . $extension;
    $request->file('upload')->storeAs('images/users', $filename, 'public');

    // Insert pencocokan row — score is empty string (parity bug preserved)
    FaceMatch::create([
        'id_user'    => $request->id_user,
        'nama_photo' => $stem,
        'gambar'     => $filename,
        'score'      => '',                  // empty — parity bug
    ]);

    // Get first training photo (no ORDER BY — parity)
    $trainingPhoto = Photo::where('id_user', $request->id_user)->first();
    if (!$trainingPhoto) {
        return response()->json([
            'status'  => 'nopic',
            'message' => 'Anda belum melakukan perekaman!',
        ], 404);
    }

    // Construct hardcoded domain URLs (parity)
    $baseUrl       = 'http://facevoting.xyz/gambar/user/';
    $trainingUrl   = $baseUrl . $trainingPhoto->gambar;
    $verifyUrl     = $baseUrl . $filename;

    $matchResult = $faceMatch->compare($trainingUrl, $verifyUrl);

    if ($matchResult === null) {
        return response()->json([
            'status'  => 'failed',
            'message' => 'Sementara tidak dapat melakukan voting!',
        ], 404);
    }

    return response()->json([
        'status'  => 'success',
        'message' => $matchResult,    // the data.status string from FaceXAPI
    ]);
}
```

> `pencocokan.score` is stored as empty string before the API call. The score is never updated after the call (parity).
> Training photo is the first row by default insertion order — no `ORDER BY` (parity).
> Image URLs use `http://facevoting.xyz` (hardcoded — parity).

---

## Acceptance Criteria

- [ ] `POST /api/gambar/tambah` file field name must be `upload` (not `file`)
- [ ] Uploaded file stored in `storage/app/public/images/users/`
- [ ] Filename is `substr(sha1(time()), 1, 10)` + extension (offset 1, not 0)
- [ ] DB row created with `id_album = 3` (hardcoded)
- [ ] `status_train` stored as `2` (not `0`)
- [ ] `entry_id` taken from `user.entry_id`
- [ ] `POST /api/gambar/tambah` with unknown `id_user` → HTTP 404 `{"status":"failed","message":"User tidak ditemukan!"}`
- [ ] `POST /api/gambar/cek` inserts `pencocokan` row with empty `score` string
- [ ] `POST /api/gambar/cek` with user who has no training photo → HTTP 404 `{"status":"nopic","message":"Anda belum melakukan perekaman!"}`
- [ ] `POST /api/gambar/cek` calls FaceXAPI with `http://facevoting.xyz/gambar/user/{filename}` URLs
- [ ] `POST /api/gambar/cek` on API unavailable → HTTP 404 `"Sementara tidak dapat melakukan voting!"`
- [ ] `POST /api/gambar/cek` on API success → HTTP 200 with `"message"` = the `data.status` string from FaceXAPI
- [ ] Both endpoints accessible without Authorization header
- [ ] Upload fails with HTTP 422 on file type or size constraint violation
