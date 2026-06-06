# TASK-22 — API Category Controller (Authorized Categories, All Open)

**Phase**: 5 — Mobile API  
**Depends on**: TASK-04, TASK-19  
**Estimated effort**: 1 hour

---

## Description

Implement two category listing endpoints: one returning only the categories a voter is authorized to vote in (status=1 authorizations), and one returning all open categories (status_kategori=2). Both return the same 3-field wrapper: `status`, `result`, `message`.

---

## Affected Files

### Create
- `app/Http/Controllers/Api/CategoryController.php`

### Modify
- `routes/api.php` — already defined in TASK-19

---

## Implementation Notes

### `mine()` — `GET /api/kategori/tampil?id_user={id}`

Returns categories where the voter has an `otorisasi_pemilih` row with `status = 1` (not yet voted).

```php
public function mine(Request $request): JsonResponse
{
    $authorizations = VoterAuthorization::with('category')
        ->where('id_user', $request->query('id_user'))
        ->where('status', 1)
        ->get();

    if ($authorizations->isEmpty()) {
        return response()->json([
            'status'  => 'gagal',
            'result'  => [],
            'message' => 'data kosong!',
        ], 404);
    }

    // Flatten into the same shape as CI3's result (all columns from both tables)
    $result = $authorizations->map(function ($auth) {
        return [
            'id_otorisasi'    => $auth->id_otorisasi,
            'id_user'         => $auth->id_user,
            'id_kategori'     => $auth->id_kategori,
            'status'          => $auth->status,
            'nama_kategori'   => $auth->category->nama_kategori,
            'logo_kategori'   => $auth->category->logo_kategori,
            'status_kategori' => $auth->category->status_kategori,
        ];
    });

    return response()->json([
        'status'  => 'berhasil',
        'result'  => $result,
        'message' => 'Data berhasil didapatkan',
    ]);
}
```

> Only `otorisasi_pemilih.status = 1` — voted categories (status=2) are excluded.
> `result` is `[]` (not omitted) on 404.

### `index()` — `GET /api/kategori/semua`

Returns all categories with `status_kategori = 2` (open for voting).

```php
public function index(): JsonResponse
{
    $categories = Category::where('status_kategori', 2)->get();

    if ($categories->isEmpty()) {
        return response()->json([
            'status'  => 'gagal',
            'result'  => [],
            'message' => 'data kosong!',
        ], 404);
    }

    return response()->json([
        'status'  => 'berhasil',
        'result'  => $categories,
        'message' => 'Data berhasil didapatkan',
    ]);
}
```

---

## Acceptance Criteria

- [ ] `GET /api/kategori/tampil?id_user=10` → HTTP 200, result contains 2 categories (user 10 has 2 authorizations with status=1 in seed data)
- [ ] Each item in result contains: `id_otorisasi`, `id_user`, `id_kategori`, `status`, `nama_kategori`, `logo_kategori`, `status_kategori`
- [ ] `GET /api/kategori/tampil?id_user=10` does NOT include categories with `otorisasi_pemilih.status = 2`
- [ ] `GET /api/kategori/tampil?id_user=99999` → HTTP 404, `{"status":"gagal","result":[],"message":"data kosong!"}`
- [ ] `result` is `[]` (not omitted) in 404 response
- [ ] `GET /api/kategori/semua` → HTTP 200 with categories where `status_kategori = 2`
- [ ] `GET /api/kategori/semua` does NOT include categories with `status_kategori = 1`
- [ ] Both endpoints accessible without Authorization header
- [ ] Wrapper has exactly 3 fields: `status`, `result`, `message`
