# TASK-12 — Candidate Pair Management

**Phase**: 3 — Admin CRUD Features  
**Depends on**: TASK-04, TASK-07, TASK-08, TASK-18 (file storage)  
**Estimated effort**: 3 hours

---

## Description

Implement candidate pair list, creation (with two optional photo uploads), and deletion. Corresponds to CI3's `Paslon.php` controller. Two sequential DB inserts (paslon → detail_paslon) using the inserted ID.

---

## Affected Files

### Create
- `app/Http/Controllers/Admin/CandidateController.php`
- `app/Http/Requests/Admin/StoreCandidateRequest.php`
- `resources/views/admin/candidates/index.blade.php`
- `resources/views/admin/candidates/create.blade.php`

### Modify
- `routes/web.php` — candidate routes inside protected group

---

## Implementation Notes

### Routes
```php
Route::get('/paslon',          [CandidateController::class, 'index'])->name('admin.candidates.index');
Route::get('/paslon/tambah',   [CandidateController::class, 'create'])->name('admin.candidates.create');
Route::post('/paslon/tambah',  [CandidateController::class, 'store'])->name('admin.candidates.store');
Route::get('/paslon/hapus/{id}',[CandidateController::class, 'destroy'])->name('admin.candidates.destroy');
```

### Form Request (`StoreCandidateRequest.php`)
```php
public function rules(): array
{
    return [
        'namaKategori'  => ['required'],
        'judulPaslon'   => ['required'],
        'ketuaPaslon'   => ['required'],
        'profilCatum'   => ['required'],
        'wakilPaslon'   => ['required'],
        'profilCawatum' => ['required'],
        'visimisi'      => ['required'],
    ];
}
public function messages(): array
{
    return [
        'namaKategori.required'  => 'Nama Kategori harus diisi!',
        'judulPaslon.required'   => 'Judul Paslon harus diisi!',
        'ketuaPaslon.required'   => 'Nama Calon Ketua harus diisi!',
        'profilCatum.required'   => 'Profil Calon Ketua harus diisi!',
        'wakilPaslon.required'   => 'Nama Calon Wakil harus diisi!',
        'profilCawatum.required' => 'Profil Calon Wakil harus diisi!',
        'visimisi.required'      => 'Visi dan Misi harus diisi!',
    ];
}
```

### Controller Key Behaviours

**index():**
```php
$candidates = Candidate::with(['category', 'detail'])->get();
return view('admin.candidates.index', compact('candidates'));
```

**create():**
```php
$daftarKategori = Category::where('status_kategori', 1)->get();
return view('admin.candidates.create', compact('daftarKategori'));
```
> Only categories with `status_kategori = 1` appear in the dropdown.

**store(StoreCandidateRequest $request):**
1. Validation runs automatically.
2. On failure: flash `message1` with validation errors, reload `daftarKategori`, re-render view.
3. Upload `photo1` if file provided. Filename auto-generated. Upload to `storage/app/public/images/`.
4. Upload `photo2` if file provided.
5. Photos are optional — if `$request->hasFile('photo1')` is false, `photo1_paslon` is null.
6. Insert `paslon`: `{id_kategori, judul_paslon, ketua_paslon, wakil_paslon, photo1_paslon, photo2_paslon, perolehan: 0}`.
7. Capture new ID.
8. Insert `detail_paslon`: `{id_paslon: newId, visi_misi, profil_catum, profil_cawatum}`.
9. Flash `message2` = `<div class="alert alert-success" role="alert">Pasangan Calon berhasil ditambahkan!</div>`.
10. Redirect to `/Paslon`.

**destroy($id):**
```php
if (!$id) return redirect('/Paslon');
$candidate = Candidate::find($id);
if (!$candidate) return redirect('/Paslon');
$candidate->delete();  // cascades to detail_paslon and voting
return redirect('/Paslon')->with('message2',
    '<div class="alert alert-danger" role="alert">Data paslon berhasil dihapus!</div>');
```
> Photo files in `gambar/` are NOT deleted.

---

## Acceptance Criteria

- [ ] `GET /paslon` shows all candidates with category name and detail data
- [ ] `GET /paslon/tambah` shows category dropdown — only categories with `status_kategori = 1`
- [ ] `POST /paslon/tambah` with missing required fields produces all 7 validation error messages
- [ ] Each field produces its exact Indonesian validation message
- [ ] `POST /paslon/tambah` without photos — `photo1_paslon` and `photo2_paslon` are NULL in database
- [ ] `POST /paslon/tambah` with photos — both photo filenames stored
- [ ] `perolehan` is stored as `0` on create
- [ ] Two DB rows inserted: one in `paslon`, one in `detail_paslon` using `id_paslon`
- [ ] `GET /paslon/hapus/{id}` deletes candidate and cascades to `detail_paslon`
- [ ] `voting` rows for the deleted candidate are also deleted (FK CASCADE)
- [ ] Photo files remain on disk after candidate deletion
- [ ] Redirect after create: `/Paslon` (capital P)
- [ ] Redirect after delete: `/Paslon` (capital P)
