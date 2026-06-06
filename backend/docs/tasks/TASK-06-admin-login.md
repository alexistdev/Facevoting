# TASK-06 — Admin Login & Logout

**Phase**: 2 — Admin Authentication  
**Depends on**: TASK-05, TASK-08 (layout)  
**Estimated effort**: 3 hours

---

## Description

Implement the admin login page (GET + POST) and logout endpoint. Behaviour must match CI3 exactly: CAPTCHA on every load, token row written to the `token` table on login, token NOT deleted on logout, exact flash message strings, case-sensitive redirect targets.

---

## Affected Files

### Create
- `app/Http/Controllers/Auth/AdminLoginController.php`
- `app/Http/Requests/Admin/AdminLoginRequest.php`
- `app/Rules/ValidCaptcha.php`
- `resources/views/auth/admin-login.blade.php`

### Modify
- `routes/web.php` — login/logout routes

---

## Implementation Notes

### Routes (`routes/web.php`)
```php
Route::get('/', fn() => redirect()->route('admin.login'));
Route::get('/login',   [AdminLoginController::class, 'showForm'])->name('admin.login');
Route::post('/login',  [AdminLoginController::class, 'login'])->name('admin.login.submit');
Route::get('/member/logout', [AdminLoginController::class, 'logout'])->name('admin.logout');
```

> The logout route is `GET /member/logout` — parity with CI3's `Member::logout()`.

### CAPTCHA Rule (`app/Rules/ValidCaptcha.php`)
```php
class ValidCaptcha implements ValidationRule
{
    public function validate(string $attribute, mixed $value, Closure $fail): void
    {
        if (!hash_equals(session('captchaword', ''), strtoupper($value))) {
            $fail('Captcha yang anda masukkan salah!');
        }
    }
}
```

> CAPTCHA comparison is case-insensitive on user input (strtoupper on input before comparing), case-sensitive on stored word. Parity with CI3 `_check_captcha()`.

### Form Request (`app/Http/Requests/Admin/AdminLoginRequest.php`)
```php
public function rules(): array
{
    return [
        'username' => ['required'],
        'password' => ['required'],
        'captcha'  => ['required', new ValidCaptcha],
    ];
}

public function messages(): array
{
    return [
        'username.required' => 'Username harus diisi!',
        'password.required' => 'Password harus diisi!',
        'captcha.required'  => 'Captcha harus diisi!',
    ];
}
```

### Controller (`app/Http/Controllers/Auth/AdminLoginController.php`)

**showForm():**
- If `Auth::guard('admin')->check()` → redirect to `/Member`
- Generate captcha, store word in `session('captchaword')`
- Return view with captcha image

**login():**
- Run form validation via `AdminLoginRequest`
- On validation failure: store `$errors` in flash key `pesan`, regenerate captcha, re-render form (do not redirect — parity)
- Look up admin by `username`
- `Hash::check($request->password, $admin->password)` — on failure:
  - Flash key `pesan2` = `<div class="alert alert-danger" role="alert">Username atau password anda salah!</div>`
  - Redirect to `/Login`
- On success:
  - Generate token = `sha1(now()->format('Y-m-d H:i:s'))`
  - Generate logTime = `now()->timestamp`
  - Delete existing token row where `id_admin = 1`
  - Insert new token row: `{id_admin: 1, token: $key, time: $logTime}`
  - Set session: `{id_admin: 1, token: $key, is_login_in: true}`
  - Redirect to `/Member`

**logout():**
- Destroy session (`$request->session()->invalidate()`)
- Redirect to `/Login`
- Do NOT delete the token row from the database

### Login View (`resources/views/auth/admin-login.blade.php`)
- Page title: `Login | FaceVoting Versi 1.0`
- Display flash `pesan` (validation errors)
- Display flash `pesan2` (credential error)
- Show CAPTCHA image
- Form action: `POST /login` with `@csrf`
- Fields: `username`, `password`, `captcha`

---

## Acceptance Criteria

- [ ] `GET /` redirects to `/login`
- [ ] `GET /login` renders form with a CAPTCHA image
- [ ] `GET /login` when already logged in redirects to `/Member` (capital M)
- [ ] Submitting empty form shows all 3 required-field messages
- [ ] Submitting wrong CAPTCHA shows `'Captcha yang anda masukkan salah!'`
- [ ] Submitting correct CAPTCHA but wrong password shows exactly `<div class="alert alert-danger" role="alert">Username atau password anda salah!</div>` and redirects to `/Login`
- [ ] Successful login inserts a row into `token` table
- [ ] `session('is_login_in')` is `true` after successful login
- [ ] `session('token')` matches the value in `token.token` after successful login
- [ ] `GET /member/logout` destroys session and redirects to `/Login`
- [ ] `token` table row is NOT deleted after logout
- [ ] A new CAPTCHA is generated on every GET `/login` request
- [ ] A new CAPTCHA is generated after a failed POST (form re-renders, not redirect)
