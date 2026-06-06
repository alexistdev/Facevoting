# TASK-15 — Admin Password Settings

**Phase**: 3 — Admin CRUD Features  
**Depends on**: TASK-04, TASK-05, TASK-07, TASK-08  
**Estimated effort**: 1.5 hours

---

## Description

Implement the admin password change page. The form has two password fields with specific validation rules including a deliberate message inconsistency (min_length[4] but error says "minimal 6 karakter") that must be preserved exactly.

---

## Affected Files

### Create
- `app/Http/Controllers/Admin/SettingController.php`
- `app/Http/Requests/Admin/ChangePasswordRequest.php`
- `resources/views/admin/settings/index.blade.php`

### Modify
- `routes/web.php` — settings routes inside protected group

---

## Implementation Notes

### Routes
```php
Route::get('/setting',  [SettingController::class, 'edit'])->name('admin.settings.edit');
Route::post('/setting', [SettingController::class, 'update'])->name('admin.settings.update');
```

### Form Request (`ChangePasswordRequest.php`)
```php
public function rules(): array
{
    return [
        'password1' => ['required', 'string', 'min:4', 'max:16'],
        'password2' => ['required', 'string', 'same:password1'],
    ];
}
public function messages(): array
{
    return [
        'password1.required' => 'Password harus diisi!',
        'password1.min'      => 'Panjang karakter Password minimal 6 karakter!',  // intentional: says 6, enforces 4
        'password1.max'      => 'Panjang karakter Password maksimal 16 karakter!',
        'password2.required' => 'Password harus diisi!',
        'password2.same'     => 'Password tidak sama!',
    ];
}
```

> The `min:4` rule enforces 4 characters minimum, but the error message says "minimal 6 karakter". This discrepancy is from CI3 and must be preserved exactly.

### Controller (`SettingController.php`)

**edit():**
```php
return view('admin.settings.index');
```

**update(ChangePasswordRequest $request):**
```php
// Validation runs automatically via Form Request.
// On failure: errors in $errors bag, flash key 'message1' with validation error HTML, re-render.

Auth::guard('admin')->user()->update([
    'password' => Hash::make($request->password1),
]);

return redirect('/Setting')->with('message2',
    '<div class="alert alert-success" role="alert">Berhasil Memperbaharui data!</div>');
```

> Redirect target is `/Setting` (capital S). On validation failure, CI3 stored errors in flash `message1` and re-rendered (not redirected). In Laravel with Form Requests, on failure the errors are automatically flashed to the session and the request redirects back. Ensure the view reads from `$errors` bag OR from flash `message1`.

> In CI3, the admin is always `id_admin = 1`. In Laravel, use `Auth::guard('admin')->user()` — this is one of the bugs being fixed (hardcoded ID removed).

### View (`resources/views/admin/settings/index.blade.php`)

```blade
@extends('layouts.admin')
@section('content')
    {{-- Display validation errors from message1 flash or $errors bag --}}
    @if ($errors->any())
        @foreach ($errors->all() as $error)
            <div class="alert alert-danger" role="alert">{{ $error }}</div>
        @endforeach
    @endif

    <form method="POST" action="{{ url('/setting') }}">
        @csrf
        <input type="password" name="password1" placeholder="Password Baru">
        <input type="password" name="password2" placeholder="Konfirmasi Password">
        <button type="submit">Simpan</button>
    </form>
@endsection
```

---

## Acceptance Criteria

- [ ] `GET /setting` renders the password change form
- [ ] `POST /setting` with empty fields shows both "Password harus diisi!" messages
- [ ] `POST /setting` with `password1` of 3 characters shows `'Panjang karakter Password minimal 6 karakter!'` (message says 6, rule is 4)
- [ ] `POST /setting` with `password1` of 4 characters — min rule passes (no error)
- [ ] `POST /setting` with `password1` of 17 characters shows "maksimal 16 karakter"
- [ ] `POST /setting` with mismatched fields shows "Password tidak sama!"
- [ ] `POST /setting` with valid matching passwords hashes the new password with bcrypt
- [ ] New bcrypt hash is verified by `Hash::check('new_password', $admin->fresh()->password)` = true
- [ ] Success flash is exactly `<div class="alert alert-success" role="alert">Berhasil Memperbaharui data!</div>`
- [ ] Redirect after success is `/Setting` (capital S)
