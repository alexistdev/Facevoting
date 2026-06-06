# TASK-07 — Admin Route Protection Middleware

**Phase**: 2 — Admin Authentication  
**Depends on**: TASK-05  
**Estimated effort**: 1 hour

---

## Description

Create the middleware that protects all admin routes. The CI3 pattern ran a session check and a token DB cross-check in every controller constructor. This task centralises that logic into a single Laravel middleware applied once at the route group level.

---

## Affected Files

### Create
- `app/Http/Middleware/AdminAuthenticated.php`

### Modify
- `routes/web.php` — wrap all protected routes in `auth:admin` middleware group
- `bootstrap/app.php` — register the middleware alias

---

## Implementation Notes

### Middleware (`app/Http/Middleware/AdminAuthenticated.php`)

CI3's constructor guard did two things:
1. Check `session('is_login_in') === true` → redirect to login
2. Check `session('token') === db token` on every method call

In Laravel, `Auth::guard('admin')->check()` replaces both checks. The token table is no longer queried on every request — the Laravel session itself is the source of truth for authentication state.

```php
class AdminAuthenticated
{
    public function handle(Request $request, Closure $next): Response
    {
        if (!Auth::guard('admin')->check()) {
            return redirect()->route('admin.login');
        }
        return $next($request);
    }
}
```

### Route Group (`routes/web.php`)
```php
Route::middleware(['auth.admin'])->group(function () {
    Route::get('/member',   [DashboardController::class, 'index'])->name('admin.dashboard');
    Route::get('/user',     [VoterController::class, 'index'])->name('admin.voters.index');
    // ... all protected admin routes
});
```

### Registration (`bootstrap/app.php`)
```php
->withMiddleware(function (Middleware $middleware) {
    $middleware->alias([
        'auth.admin' => \App\Http\Middleware\AdminAuthenticated::class,
    ]);
})
```

### Parity Note

CI3's `Hasil::index()` does NOT call `_unlogin()` on token mismatch — it renders the results page regardless. In Laravel, the results route must still be in the protected middleware group (the admin must be logged in to see it). The CI3 bug is that a token mismatch did not trigger logout — this is already fixed by using `auth:admin` which simply checks session state.

However the PRD requires preserving the *behaviour* that the results page renders on token mismatch. Since in Laravel there is no separate "token mismatch" concept (the session IS the auth state), this parity concern is automatically satisfied.

---

## Acceptance Criteria

- [ ] `GET /member` without a session redirects to `/login` (lowercase, parity with CI3 redirect target)
- [ ] `GET /user` without a session redirects to `/login`
- [ ] `GET /kategori` without a session redirects to `/login`
- [ ] `GET /paslon` without a session redirects to `/login`
- [ ] `GET /photo` without a session redirects to `/login`
- [ ] `GET /hasil` without a session redirects to `/login`
- [ ] `GET /setting` without a session redirects to `/login`
- [ ] After successful login, all protected routes are accessible
- [ ] Middleware does not break any routes not in its group (login, logout, API routes)
- [ ] No error when registering middleware alias in `bootstrap/app.php`
