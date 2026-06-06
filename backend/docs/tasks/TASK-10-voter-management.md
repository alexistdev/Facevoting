# TASK-10 — Voter Management

**Phase**: 3 — Admin CRUD Features  
**Depends on**: TASK-04, TASK-07, TASK-08  
**Estimated effort**: 2 hours

---

## Description

Implement the voter list, voter activation, and voter deletion features. These correspond to CI3's `User.php` controller. All flash messages, redirect targets, and edge-case behaviours (silent redirect on invalid ID, cascade behaviour on delete) must match exactly.

---

## Affected Files

### Create
- `app/Http/Controllers/Admin/VoterController.php`
- `resources/views/admin/voters/index.blade.php`

### Modify
- `routes/web.php` — voter routes inside protected group

---

## Implementation Notes

### Routes
```php
Route::get('/user',              [VoterController::class, 'index'])->name('admin.voters.index');
Route::get('/user/aktivasi/{id}',[VoterController::class, 'activate'])->name('admin.voters.activate');
Route::get('/user/hapus/{id}',   [VoterController::class, 'destroy'])->name('admin.voters.destroy');
```

> Routes use CI3 URL patterns exactly — `GET` verb for activate and delete (parity).

### Controller (`app/Http/Controllers/Admin/VoterController.php`)

**index():**
```php
$voters = User::with('detail')->get();
return view('admin.voters.index', compact('voters'));
```

**activate($id):**
```php
if (!$id) return redirect('/User');
$user = User::where('id_user', $id)->where('status', 2)->first();
if (!$user) return redirect('/User');
$user->update(['status' => 1]);
return redirect('/User')->with('pesan2',
    '<div class="alert alert-success" role="alert">User telah berhasil diaktifkan!</div>');
```

> Redirect target is `/User` (capital U) — parity with CI3.

**destroy($id):**
```php
if (!$id) return redirect('/User');
$user = User::find($id);
if (!$user) return redirect('/User');
$user->delete();   // cascades via FK to detail_user, photo, otorisasi_pemilih, voting
return redirect('/User')->with('pesan',
    '<div class="alert alert-danger" role="alert">User telah dihapus!</div>');
```

> `pencocokan` rows are NOT deleted (no FK) — orphaned rows remain. Physical files in `gambar/user/` are NOT deleted. Both are parity requirements.

### View (`resources/views/admin/voters/index.blade.php`)

Display table with columns: `id_user`, `email`, `status`, `nama`, `identitas` (from detail).  
Activation button only shown if `status == 2`.  
Delete button shown for all.

```blade
@extends('layouts.admin')
@section('content')
    @foreach ($voters as $voter)
        <tr>
            <td>{{ $voter->id_user }}</td>
            <td>{{ $voter->email }}</td>
            <td>{{ $voter->status }}</td>
            <td>{{ $voter->detail->nama ?? '-' }}</td>
            <td>{{ $voter->detail->identitas ?? '-' }}</td>
            <td>
                @if ($voter->status == 2)
                    <a href="{{ url('/user/aktivasi/' . $voter->id_user) }}">Aktifkan</a>
                @endif
                <a href="{{ url('/user/hapus/' . $voter->id_user) }}">Hapus</a>
            </td>
        </tr>
    @endforeach
@endsection
```

---

## Acceptance Criteria

- [ ] `GET /user` returns HTTP 200 with all users (status 1 and 2)
- [ ] Each voter row shows name from `detail_user`
- [ ] `GET /user/aktivasi/{id}` with a valid pending user (status=2) sets status to 1 and shows success flash
- [ ] `GET /user/aktivasi/{id}` with an already-active user (status=1) silently redirects to `/User` with no flash
- [ ] `GET /user/aktivasi/{id}` with null or empty id silently redirects to `/User`
- [ ] `GET /user/hapus/{id}` with a valid id deletes the user and cascades to `detail_user`, `photo`, `otorisasi_pemilih`, `voting`
- [ ] `pencocokan` rows for the deleted user remain in the database
- [ ] No physical files are deleted from disk on user deletion
- [ ] Success flash for activation is exactly `<div class="alert alert-success" role="alert">User telah berhasil diaktifkan!</div>`
- [ ] Success flash for deletion is exactly `<div class="alert alert-danger" role="alert">User telah dihapus!</div>`
- [ ] All redirect targets use `/User` (capital U) to match CI3
