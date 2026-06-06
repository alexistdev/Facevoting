# TASK-13 — Photo & Album Management (Admin)

**Phase**: 3 — Admin CRUD Features  
**Depends on**: TASK-04, TASK-07, TASK-08, TASK-16 (LambdaService)  
**Estimated effort**: 3 hours

---

## Description

Implement the training photo list, train-to-Lambda, photo deletion, album view, and album rebuild features. Also implements the simple album index. Corresponds to CI3's `Photo.php` and `Album.php` controllers.

---

## Affected Files

### Create
- `app/Http/Controllers/Admin/PhotoController.php`
- `app/Http/Controllers/Admin/AlbumController.php`
- `resources/views/admin/photos/index.blade.php`
- `resources/views/admin/photos/album-view.blade.php`
- `resources/views/admin/photos/rebuild.blade.php`
- `resources/views/admin/albums/index.blade.php`

### Modify
- `routes/web.php` — photo and album routes inside protected group

---

## Implementation Notes

### Routes
```php
Route::get('/photo',                [PhotoController::class, 'index'])->name('admin.photos.index');
Route::get('/photo/rekam/{id}',     [PhotoController::class, 'train'])->name('admin.photos.train');
Route::get('/photo/hapus/{id}',     [PhotoController::class, 'destroy'])->name('admin.photos.destroy');
Route::get('/photo/viewAlbum',      [PhotoController::class, 'viewAlbum'])->name('admin.photos.album');
Route::get('/photo/rebuild',        [PhotoController::class, 'rebuild'])->name('admin.photos.rebuild');
Route::get('/album',                [AlbumController::class, 'index'])->name('admin.albums.index');
```

### PhotoController Key Behaviours

**index():**
```php
$photos = DB::table('photo')
    ->join('album', 'album.id_album', '=', 'photo.id_album')
    ->join('detail_user', 'detail_user.id_user', '=', 'photo.id_user')
    ->select('photo.*', 'album.nama_album', 'album.kode_album', 'detail_user.nama', 'detail_user.identitas')
    ->orderByDesc('photo.status_train')
    ->get();
return view('admin.photos.index', compact('photos'));
```

**train($id):**
1. If null/empty → redirect `/Photo`.
2. `$photo = DB::table('photo')->join('album', ...)->where('id_photo', $id)->first()`.
3. If not found → redirect `/Photo`.
4. Call `$this->lambda->trainPhoto(album_name, album_key, entry_id, image_url)`.
   - Image URL: `http://facevoting.xyz/gambar/user/{gambar}` (hardcoded domain — parity).
5. On null response (cURL error): flash `pesan` danger, redirect `/Photo`.
6. On success: flash `pesan` = `<div class="alert alert-success" role="alert">Berhasil, jumlah photo tersimpan ke dalam album <span class="text-warning">{album}</span> adalah <span class="text-warning">{image_count}</span> gambar </div>`.
7. Redirect `/Photo`.
8. `status_train` is **NOT** updated (parity).

**destroy($id):**
1. If null/empty → redirect `/Photo`.
2. `$photo = DB::table('photo')->join('album', ...)->where('id_photo', $id)->first()`.
3. If not found → redirect `/Photo`.
4. Read `$gambar = $photo->gambar`.
5. `Photo::destroy($id)`.
6. `Storage::disk('public')->delete('images/users/' . $gambar)` (delete physical file).
7. Flash `pesan` = `<div class="alert alert-danger" role="alert">Photo berhasil dihapus!</div>`.
8. Redirect `/Photo`.

**viewAlbum():**
```php
$album = Album::find(3);  // hardcoded id_album = 3
$getViewAlbum = $this->lambda->viewAlbum($album->nama_album, $album->kode_album);
// getViewAlbum is the raw JSON string (not decoded) — parity
return view('admin.photos.album-view', compact('getViewAlbum'));
```

**rebuild():**
```php
$album = Album::find(3);  // hardcoded
$getRebuild = $this->lambda->rebuildAlbum($album->nama_album, $album->kode_album);
return view('admin.photos.rebuild', compact('getRebuild'));
```

### AlbumController

**index():**
```php
// CI3's Album::index() showed album list with photo counts
// Port from v_album.php view content
$albums = Album::withCount('photos')->get();
return view('admin.albums.index', compact('albums'));
```

---

## Acceptance Criteria

- [ ] `GET /photo` returns all photos ordered by `status_train DESC`
- [ ] Photo list includes `nama_album`, `kode_album`, `nama`, `identitas` from joined tables
- [ ] `GET /photo/rekam/{id}` calls LambdaService with the hardcoded `http://facevoting.xyz` image URL
- [ ] On Lambda success: flash contains album name and image_count from response
- [ ] On Lambda failure (null response): flash danger "Perekaman data Gagal!"
- [ ] `status_train` is NOT changed after a successful train call
- [ ] `GET /photo/hapus/{id}` deletes DB row AND physical file from storage
- [ ] `GET /photo/viewAlbum` fetches album id=3, passes raw response to view
- [ ] `GET /photo/rebuild` fetches album id=3, uses GET `/album_rebuild` endpoint
- [ ] Album index at `GET /album` renders without error
- [ ] All redirect targets use `/Photo` (capital P) to match CI3
- [ ] LambdaService is injected via constructor (not instantiated inline)
