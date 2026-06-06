# TASK-14 — Voting Results View

**Phase**: 3 — Admin CRUD Features  
**Depends on**: TASK-04, TASK-07, TASK-08  
**Estimated effort**: 1 hour

---

## Description

Implement the voting results page. Shows all candidate pairs from all categories ordered by vote count descending. Parity note: in CI3's `Hasil::index()`, a token mismatch does NOT trigger `_unlogin()` — the page renders regardless. In Laravel this is naturally satisfied since authentication state is binary (logged in / not logged in) and there is no separate token mismatch concept.

---

## Affected Files

### Create
- `app/Http/Controllers/Admin/ResultController.php`
- `resources/views/admin/results/index.blade.php`

### Modify
- `routes/web.php` — results route inside protected group

---

## Implementation Notes

### Route
```php
Route::get('/hasil', [ResultController::class, 'index'])->name('admin.results.index');
```

### Controller
```php
class ResultController extends Controller
{
    public function index(): View
    {
        $dataPaslon = Candidate::with(['category', 'detail'])
            ->orderByDesc('perolehan')
            ->get();
        return view('admin.results.index', compact('dataPaslon'));
    }
}
```

### View (`resources/views/admin/results/index.blade.php`)

Port the HTML table structure from CI3's `v_hasil.php`. Display all candidates from all categories with their vote count (`perolehan`), sorted highest first.

```blade
@extends('layouts.admin')
@section('content')
    @foreach ($dataPaslon as $paslon)
        <tr>
            <td>{{ $paslon->judul_paslon }}</td>
            <td>{{ $paslon->category->nama_kategori }}</td>
            <td>{{ $paslon->perolehan }}</td>
        </tr>
    @endforeach
@endsection
```

---

## Acceptance Criteria

- [ ] `GET /hasil` returns HTTP 200 when authenticated
- [ ] `GET /hasil` without session redirects to `/login` (middleware still applies)
- [ ] All candidate pairs from all categories appear in the result
- [ ] Results are ordered by `perolehan` DESC — highest vote count first
- [ ] Each row includes data from both `paslon` and `kategori` (category name visible)
- [ ] `detail_paslon` data is available if the view uses it
- [ ] Page title is `Dashboard | FaceVoting Versi 1.0`
