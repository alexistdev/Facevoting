# TASK-23 — API Candidate Controller (By Category, Detail)

**Phase**: 5 — Mobile API  
**Depends on**: TASK-04, TASK-19  
**Estimated effort**: 1.5 hours

---

## Description

Implement two candidate endpoints: list candidates by category (with `detail_paslon` merged in), and candidate detail (query root is `detail_paslon`, not `paslon`). Both use POST verb. Photo values are bare filenames — not URLs.

---

## Affected Files

### Create
- `app/Http/Controllers/Api/CandidateController.php`

### Modify
- `routes/api.php` — already defined in TASK-19

---

## Implementation Notes

### `index()` — `POST /api/paslon/tampil`

Query: `paslon JOIN detail_paslon WHERE paslon.id_kategori = ?`  
Result: merged row with all columns from both tables.

```php
public function index(Request $request): JsonResponse
{
    $candidates = DB::table('paslon')
        ->join('detail_paslon', 'detail_paslon.id_paslon', '=', 'paslon.id_paslon')
        ->where('paslon.id_kategori', $request->id_kategori)
        ->get();

    if ($candidates->isEmpty()) {
        return response()->json([
            'status'  => 'gagal',
            'result'  => [],
            'message' => 'data kosong!',
        ], 404);
    }

    return response()->json([
        'status'  => 'berhasil',
        'result'  => $candidates,
        'message' => 'Data berhasil didapatkan',
    ]);
}
```

> Uses `DB::table()` not Eloquent to get the exact column merge that CI3 produces from `result_array()`.
> Photo values (`photo1_paslon`, `photo2_paslon`) are bare filenames. Mobile app constructs the URL.

### `show()` — `POST /api/paslon/detail`

Query root is `detail_paslon`, JOINs `paslon`. Order of columns in result starts with `detail_paslon` columns.

```php
public function show(Request $request): JsonResponse
{
    $detail = DB::table('detail_paslon')
        ->join('paslon', 'paslon.id_paslon', '=', 'detail_paslon.id_paslon')
        ->where('detail_paslon.id_paslon', $request->id_paslon)
        ->get();

    if ($detail->isEmpty()) {
        return response()->json([
            'status'  => 'gagal',
            'result'  => [],
            'message' => 'data kosong!',
        ], 404);
    }

    return response()->json([
        'status'  => 'berhasil',
        'result'  => $detail,
        'message' => 'Data berhasil didapatkan',
    ]);
}
```

> This endpoint queries from `detail_paslon` and JOINs `paslon` — the column order in the result differs from `index()`. The field `id_detailpaslon` appears before `id_paslon` in the response.

---

## Acceptance Criteria

- [ ] `POST /api/paslon/tampil` uses `POST` verb (not `GET`)
- [ ] `POST /api/paslon/tampil` with `id_kategori=8` → HTTP 200, result contains candidate(s) for BEM category
- [ ] Each result row contains merged columns from both `paslon` and `detail_paslon`
- [ ] Result includes: `id_paslon`, `id_kategori`, `judul_paslon`, `ketua_paslon`, `wakil_paslon`, `photo1_paslon`, `photo2_paslon`, `perolehan`, `id_detailpaslon`, `visi_misi`, `profil_catum`, `profil_cawatum`
- [ ] Photo values are bare filenames (e.g. `"abc123def4.jpg"`) — not full URLs
- [ ] `POST /api/paslon/tampil` with unknown `id_kategori` → HTTP 404, `result: []`
- [ ] `POST /api/paslon/detail` uses `POST` verb
- [ ] `POST /api/paslon/detail` query root is `detail_paslon` — `id_detailpaslon` appears first in merged row
- [ ] `POST /api/paslon/detail` with valid `id_paslon` → HTTP 200, all `paslon` fields included
- [ ] Both endpoints accessible without Authorization header
