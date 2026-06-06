# TASK-09 — Admin Dashboard

**Phase**: 3 — Admin CRUD Features  
**Depends on**: TASK-07, TASK-08  
**Estimated effort**: 30 minutes

---

## Description

Implement the admin dashboard — a protected page that renders a static summary view. No data queries are made; CI3's `Member::index()` only loads the view.

---

## Affected Files

### Create
- `app/Http/Controllers/Admin/DashboardController.php`
- `resources/views/admin/dashboard.blade.php`

### Modify
- `routes/web.php` — add dashboard route inside the protected group

---

## Implementation Notes

### Route
```php
Route::get('/member', [DashboardController::class, 'index'])->name('admin.dashboard');
```

### Controller
```php
class DashboardController extends Controller
{
    public function index(): View
    {
        return view('admin.dashboard');
    }
}
```

### View (`resources/views/admin/dashboard.blade.php`)
```blade
@extends('layouts.admin')

@section('content')
    <div class="row">
        <div class="col-12">
            <h4>Dashboard</h4>
        </div>
    </div>
@endsection
```

> The CI3 dashboard renders only a greeting/summary — no data. Port the existing HTML structure from `v_member.php`.

---

## Acceptance Criteria

- [ ] `GET /member` with valid session returns HTTP 200
- [ ] `GET /member` without session redirects to `/login`
- [ ] Response body contains the dashboard page title `Dashboard | FaceVoting Versi 1.0`
- [ ] Sidebar and navbar are rendered (layout is extended)
- [ ] No database queries are made by this controller method
