# TASK-11 — Election Category Management

**Phase**: 3 — Admin CRUD Features  
**Depends on**: TASK-04, TASK-07, TASK-08, TASK-18 (file storage)  
**Estimated effort**: 4 hours

---

## Description

Implement all election category features: list, create (with logo upload), detail view, voter authorization, open voting, and close voting. These correspond to CI3's `Kategori.php` controller.

---

## Affected Files

### Create
- `app/Http/Controllers/Admin/CategoryController.php`
- `app/Http/Requests/Admin/StoreCategoryRequest.php`
- `resources/views/admin/categories/index.blade.php`
- `resources/views/admin/categories/create.blade.php`
- `resources/views/admin/categories/show.blade.php`

### Modify
- `routes/web.php` — category routes inside protected group

---

## Implementation Notes

### Routes
```php
Route::get('/kategori',                           [CategoryController::class, 'index'])->name('admin.categories.index');
Route::get('/kategori/tambah',                    [CategoryController::class, 'create'])->name('admin.categories.create');
Route::post('/kategori/tambah',                   [CategoryController::class, 'store'])->name('admin.categories.store');
Route::get('/kategori/detail/{id}',               [CategoryController::class, 'show'])->name('admin.categories.show');
Route::get('/kategori/tambah_pemilih/{idKat}/{idUser}', [CategoryController::class, 'addVoter'])->name('admin.categories.voters.add');
Route::get('/kategori/Buka/{id}',                 [CategoryController::class, 'open'])->name('admin.categories.open');
Route::get('/kategori/Tutup/{id}',                [CategoryController::class, 'close'])->name('admin.categories.close');
```

### Form Request (`StoreCategoryRequest.php`)
```php
public function rules(): array
{
    return ['namaKategori' => ['required', 'string']];
}
public function messages(): array
{
    return ['namaKategori.required' => 'Nama Kategori harus diisi!'];
}
```

### Controller Key Behaviours

**index():** `Category::all()` — no filter.

**create():** Render form. Display flash `message1` and `message2`.

**store(StoreCategoryRequest $request):**
1. Validation runs automatically via Form Request. On failure: store in flash `message1`, redirect to create.
2. Attempt file upload of `logo` field.
3. Filename = `substr(sha1(time()), 0, 10)` + extension.
4. Upload to `storage/app/public/images/` (symlinked to `public/storage/images/`).
5. On upload failure: flash `message2` = `<div class="alert alert-danger" role="alert">{error}</div>`, redirect to `/Kategori/tambah`.
6. On success: `Category::create([...status_kategori => 1])`, flash `message2` success, redirect to `/Kategori`.

**show($id):**
- If null or no category → redirect `/Kategori`.
- `$dataPaslon` = candidates for this category.
- `$dataPemilih` = authorized voters with their `detail_user` data.
- `$dataCalonPemilih` = users NOT yet authorized for this category.
- Pass `$idKategori = $id` separately to view.

```php
$dataCalonPemilih = User::with('detail')
    ->whereDoesntHave('authorizations', fn($q) => $q->where('id_kategori', $id))
    ->get();
```

**addVoter($idKat, $idUser):**
- Either param null → redirect `/Kategori`.
- Category not found → redirect `/Kategori`.
- User not found → redirect `/Kategori`.
- `VoterAuthorization::create([...status => 1])` — no duplicate check.
- Flash `message` = `<div class="alert alert-success" role="alert">User berhasil diberi hak akses!</div>`.
- Redirect to `/Kategori/detail/{idKat}`.

**open($id):**
- `Category::find($id)->update(['status_kategori' => 2])`.
- Flash `message2` = `<div class="alert alert-success" role="alert">Pemilu saat ini telah dibuka!</div>`.
- Redirect `/Kategori`.

**close($id):**
- `Category::find($id)->update(['status_kategori' => 1])`.
- Flash `message2` = `<div class="alert alert-danger" role="alert">Pemilu saat ini telah ditutup!</div>`.
- Redirect `/Kategori`.

---

## Acceptance Criteria

- [ ] `GET /kategori` shows all categories regardless of status
- [ ] `GET /kategori/tambah` renders the create form
- [ ] `POST /kategori/tambah` with empty `namaKategori` stores flash `message1` = validation error HTML, re-renders form, does NOT attempt upload
- [ ] `POST /kategori/tambah` with valid fields and valid logo creates category with `status_kategori = 1`
- [ ] Category logo filename is 10 chars from `sha1(time())`
- [ ] `GET /kategori/detail/{id}` passes 4 variables to view: `dataPaslon`, `dataPemilih`, `dataCalonPemilih`, `idKategori`
- [ ] `dataCalonPemilih` does NOT include users already in `otorisasi_pemilih` for that category
- [ ] `GET /kategori/tambah_pemilih/{idKat}/{idUser}` inserts row with `status = 1`
- [ ] Calling `tambah_pemilih` twice creates 2 authorization rows (no uniqueness check)
- [ ] Flash key for addVoter is `message` (not `pesan` or `message2`)
- [ ] Redirect after addVoter is `/Kategori/detail/{idKat}`
- [ ] `GET /kategori/Buka/{id}` sets `status_kategori = 2` and flashes green alert
- [ ] `GET /kategori/Tutup/{id}` sets `status_kategori = 1` and flashes red alert
- [ ] Upload error flash goes to key `message2`, redirect to `/Kategori/tambah`
- [ ] Success flash for category create goes to key `message2`, redirect to `/Kategori`
