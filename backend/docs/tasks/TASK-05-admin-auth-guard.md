# TASK-05 — Admin Auth Guard Configuration

**Phase**: 2 — Admin Authentication  
**Depends on**: TASK-04  
**Estimated effort**: 30 minutes

---

## Description

Configure Laravel's auth system to support a separate `admin` guard backed by the `admin` table. This separates admin sessions from voter API tokens and allows `Auth::guard('admin')` to be used throughout the admin panel.

---

## Affected Files

### Modify
- `config/auth.php` — add `admin` guard and `admins` provider
- `app/Models/Admin.php` — extend `Authenticatable`, add `getAuthPassword()` if needed

---

## Implementation Notes

### `config/auth.php`
```php
'defaults' => [
    'guard'     => 'web',
    'passwords' => 'users',
],

'guards' => [
    'web' => [
        'driver'   => 'session',
        'provider' => 'users',
    ],
    'admin' => [
        'driver'   => 'session',
        'provider' => 'admins',
    ],
],

'providers' => [
    'users' => [
        'driver' => 'eloquent',
        'model'  => App\Models\User::class,
    ],
    'admins' => [
        'driver' => 'eloquent',
        'model'  => App\Models\Admin::class,
    ],
],
```

### `app/Models/Admin.php` — ensure it extends `Authenticatable`

The `admin` table uses `password` (bcrypt). Laravel's `Auth::attempt()` uses `getAuthPassword()` which defaults to returning `$this->password`. No override needed if the column is named `password`.

The `admin` table has no `remember_token` column. Add this to the model to prevent errors:
```php
protected $rememberTokenName = false;
```

---

## Acceptance Criteria

- [ ] `config('auth.guards.admin.driver')` returns `'session'`
- [ ] `config('auth.providers.admins.model')` returns `App\Models\Admin::class`
- [ ] `Auth::guard('admin')->attempt(['username' => 'admin', 'password' => 'correct_password'])` returns `true` against the seeded admin row
- [ ] `Auth::guard('admin')->attempt(['username' => 'admin', 'password' => 'wrong'])` returns `false`
- [ ] `Auth::guard('web')` still resolves to the `users` provider (not broken by changes)
- [ ] No "Column not found: remember_token" errors when logging in as admin
