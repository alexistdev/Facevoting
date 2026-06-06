# TASK-25 — API Vote Controller (Submit, History, Results)

**Phase**: 5 — Mobile API  
**Depends on**: TASK-04, TASK-19  
**Estimated effort**: 2 hours

---

## Description

Implement the three vote-related endpoints: submit a vote, get voting history, and get vote counts by category. The submit endpoint must replicate CI3's exact (buggy) behaviour — including no authorization check, no election-open check, and updating ALL of the voter's authorizations (not just the voted category).

---

## Affected Files

### Create
- `app/Http/Controllers/Api/VoteController.php`

### Modify
- `routes/api.php` — already defined in TASK-19

---

## Implementation Notes

### `store()` — `POST /api/suara/vote`

This endpoint has three critical parity behaviours:
1. No authorization check (anyone can call it)
2. No election open/close status check
3. `UPDATE otorisasi_pemilih SET status=2 WHERE id_user=?` — ALL categories, not just the voted one

```php
public function store(Request $request): JsonResponse
{
    $idUser    = $request->id_user;
    $idKategori = $request->id_kategori;
    $idPaslon  = $request->id_paslon;

    // Read-modify-write (parity — do NOT use increment())
    $paslon = DB::table('paslon')->where('id_paslon', $idPaslon)->first();
    $perolehan = $paslon->perolehan + 1;
    DB::table('paslon')->where('id_paslon', $idPaslon)->update(['perolehan' => $perolehan]);

    // Insert voting row
    DB::table('voting')->insert([
        'id_user'       => $idUser,
        'id_kategori'   => $idKategori,
        'id_paslon'     => $idPaslon,
        'tanggal_voting' => now()->format('Y-m-d H:i:s'),
    ]);

    // Update ALL authorizations for this user (no id_kategori filter — parity bug)
    DB::table('otorisasi_pemilih')
        ->where('id_user', $idUser)
        ->update(['status' => 2]);

    return response()->json([
        'status'  => 'berhasil',
        'message' => 'Berhasil Voting',
    ]);
}
```

> Uses `DB::table()` and read-modify-write to preserve CI3 behaviour exactly.
> Always returns HTTP 200 — no error handling (parity).

### `history()` — `GET /api/suara/tampil/{id_user}`

```php
public function history(int $id_user): JsonResponse
{
    $votes = DB::table('voting')
        ->join('detail_user', 'detail_user.id_user', '=', 'voting.id_user')
        ->join('kategori', 'kategori.id_kategori', '=', 'voting.id_kategori')
        ->where('voting.id_user', $id_user)
        ->get();

    if ($votes->isEmpty()) {
        return response()->json([
            'status'  => 'gagal',
            'message' => 'Silahkan relogin!',   // misleading — parity required
        ], 404);
    }

    return response()->json([
        'status'  => 'berhasil',
        'result'  => $votes,
        'message' => 'Data berhasil didapatkan',
    ]);
}
```

> The 404 message "Silahkan relogin!" is unrelated to the actual condition (no votes). Parity required.
> `id_user` is a URL segment, not a query string.

### `results()` — `GET /api/suara/perolehan?id_kategori={id}`

```php
public function results(Request $request): JsonResponse
{
    $paslon = DB::table('paslon')
        ->where('id_kategori', $request->query('id_kategori'))
        ->get();

    // Always HTTP 200 — even if result is empty (parity)
    return response()->json([
        'status'  => 'berhasil',
        'result'  => $paslon,
        'message' => 'Data berhasil didapatkan',
    ]);
}
```

> Source is `paslon.perolehan` (denormalized counter) — not `COUNT(voting.*)`.
> Always returns HTTP 200, even for empty or invalid `id_kategori`.

---

## Acceptance Criteria

- [ ] `POST /api/suara/vote` increments `paslon.perolehan` by 1
- [ ] `POST /api/suara/vote` inserts a row in `voting`
- [ ] `POST /api/suara/vote` updates `otorisasi_pemilih` with `WHERE id_user = ?` only — ALL categories for that user set to status=2
- [ ] `POST /api/suara/vote` requires NO authentication
- [ ] `POST /api/suara/vote` returns HTTP 200 regardless of whether user is authorized
- [ ] `POST /api/suara/vote` returns HTTP 200 regardless of election open/close status
- [ ] Response is exactly `{"status":"berhasil","message":"Berhasil Voting"}`
- [ ] `GET /api/suara/tampil/{id_user}` returns vote history with `detail_user` and `kategori` data merged
- [ ] `GET /api/suara/tampil/{id_user}` for user with no votes → HTTP 404 `{"status":"gagal","message":"Silahkan relogin!"}`
- [ ] `GET /api/suara/perolehan?id_kategori=8` returns paslon.perolehan values (not count from voting table)
- [ ] `GET /api/suara/perolehan?id_kategori=99999` → HTTP 200 with `result: []` (always 200)
- [ ] All 3 endpoints accessible without Authorization header
