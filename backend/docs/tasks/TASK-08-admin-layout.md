# TASK-08 — Admin Blade Layout (AdminLTE)

**Phase**: 2 — Admin Authentication  
**Depends on**: TASK-01  
**Estimated effort**: 3 hours

---

## Description

Create the base Blade layout that all admin pages extend. The layout wraps AdminLTE 3.0.5 and must replicate the same sidebar navigation items, navbar brand, and page structure as the CI3 `template/v_header`, `v_navbar`, `v_sidebar`, `v_footer` views.

---

## Affected Files

### Create
- `resources/views/layouts/admin.blade.php`
- `resources/views/layouts/partials/sidebar.blade.php`
- `resources/views/layouts/partials/navbar.blade.php`
- `resources/views/layouts/partials/footer.blade.php`

---

## Implementation Notes

### Layout structure (`layouts/admin.blade.php`)
```blade
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>@yield('title', 'Dashboard | FaceVoting Versi 1.0')</title>
    {{-- AdminLTE CSS --}}
    @stack('styles')
</head>
<body class="hold-transition sidebar-mini layout-fixed">
<div class="wrapper">
    @include('layouts.partials.navbar')
    @include('layouts.partials.sidebar')
    <div class="content-wrapper">
        <div class="content">
            <div class="container-fluid">
                @yield('content')
            </div>
        </div>
    </div>
    @include('layouts.partials.footer')
</div>
@stack('scripts')
</body>
</html>
```

### Sidebar navigation items (exact labels and targets, from CI3 `v_sidebar.php`)

| Label | Route / URL |
|---|---|
| Dashboard | `/member` |
| Data User | `/user` |
| Album | `/album` |
| Photo | `/photo` |
| Kategori | `/kategori` |
| Paslon | `/paslon` |
| Hasil | `/hasil` |
| Setting | `/setting` |

### Navbar
- Brand text: `FaceVoting`
- Logout link (top right): `GET /member/logout`

### Flash message display

All admin views display flash messages. The layout should include a standard flash block that all pages can use via `@yield('flash')` or inline `@if(session())` checks:

```blade
@if (session('pesan'))
    {!! session('pesan') !!}
@endif
@if (session('pesan2'))
    {!! session('pesan2') !!}
@endif
@if (session('message'))
    {!! session('message') !!}
@endif
@if (session('message1'))
    {!! session('message1') !!}
@endif
@if (session('message2'))
    {!! session('message2') !!}
@endif
```

> Flash values are stored as complete HTML strings (parity with CI3). Use `{!! !!}` — not `{{ }}`.

### Page title

All pages except login use the title `Dashboard | FaceVoting Versi 1.0`. The layout default should be this value. Individual views override via `@section('title', 'Login | FaceVoting Versi 1.0')` only for the login page.

---

## Acceptance Criteria

- [ ] Every admin page that extends `layouts.admin` has `<title>Dashboard | FaceVoting Versi 1.0</title>` without overriding
- [ ] Sidebar displays all 8 navigation items with correct labels and hrefs
- [ ] Logout link in navbar points to `/member/logout`
- [ ] Flash messages rendered with `{!! !!}` (raw HTML output — so alert divs render as HTML)
- [ ] All 5 flash keys (`pesan`, `pesan2`, `message`, `message1`, `message2`) are checked and displayed
- [ ] Layout does not throw any Blade compile errors
- [ ] AdminLTE CSS and JS assets load correctly (no 404 on asset requests)
- [ ] Layout is responsive (AdminLTE sidebar collapses on small screens)
