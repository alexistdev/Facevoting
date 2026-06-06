# Migration Plan — CodeIgniter 3 → Laravel 12

**Source**: CodeIgniter 3.1.11 (`evoting` application)  
**Target**: Laravel 12.x (PHP 8.2+)  
**Prepared**: 2026-06-06

---

## Table of Contents

1. [Strategy Overview](#1-strategy-overview)
2. [Quick Reference Map](#2-quick-reference-map)
3. [Directory Structure](#3-directory-structure)
4. [Routing](#4-routing)
5. [Controllers](#5-controllers)
6. [Models → Eloquent](#6-models--eloquent)
7. [Eloquent Relationships](#7-eloquent-relationships)
8. [Database Migrations](#8-database-migrations)
9. [Authentication](#9-authentication)
10. [Middleware](#10-middleware)
11. [Form Validation → Form Requests](#11-form-validation--form-requests)
12. [Session](#12-session)
13. [File Uploads → Laravel Storage](#13-file-uploads--laravel-storage)
14. [Views → Blade](#14-views--blade)
15. [Helpers](#15-helpers)
16. [Libraries → Services](#16-libraries--services)
17. [Configuration](#17-configuration)
18. [External HTTP Calls → Laravel Http Client](#18-external-http-calls--laravel-http-client)
19. [API Layer → Sanctum + API Resources](#19-api-layer--sanctum--api-resources)
20. [Error Handling](#20-error-handling)
21. [Testing](#21-testing)
22. [Migration Phases](#22-migration-phases)
23. [Bugs to Fix During Migration](#23-bugs-to-fix-during-migration)

---

## 1. Strategy Overview

### Approach: Full Rewrite (Strangler Fig)

A direct 1:1 port is not viable because CI3 and Laravel 12 have fundamentally different architectural idioms. The recommended strategy is a **full rewrite in phases**, keeping the existing CI3 application running in production while building the Laravel replacement behind a feature flag or subdomain.

### Why Not Line-for-Line Port

| CI3 Pattern | Problem |
|---|---|
| Single `Admin_m` model handles all tables | Violates single responsibility; replace with one Eloquent model per table |
| Raw query builder with manual JOINs | Replace with Eloquent relationships |
| Custom token table for admin sessions | Replace with Laravel Auth + Sanctum |
| Manual cURL for every HTTP call | Replace with Laravel Http facade |
| No input sanitization on API endpoints | Add Form Requests to every endpoint |
| Hardcoded `id_admin = 1` throughout | Replace with `Auth::id()` |

### Target Stack

| Layer | Technology |
|---|---|
| Framework | Laravel 12 |
| PHP | 8.2+ |
| ORM | Eloquent |
| Admin auth | Laravel Breeze (session-based) |
| API auth | Laravel Sanctum |
| Validation | Form Request classes |
| Views | Blade + AdminLTE |
| HTTP client | Laravel `Http` facade (Guzzle) |
| File storage | Laravel Storage (local disk) |
| Queue | Laravel Queue (for Lambda API calls) |
| Testing | Pest |

---

## 2. Quick Reference Map

### Framework Core

| CI3 | Laravel 12 | Notes |
|---|---|---|
| `CI_Controller` | `Illuminate\Routing\Controller` | `extends Controller` |
| `REST_Controller` | API Controller + `response()->json()` | Sanctum for auth |
| `CI_Model` | `Illuminate\Database\Eloquent\Model` | `extends Model` |
| `$this->db` (Query Builder) | `DB::table()` or Eloquent | Prefer Eloquent |
| `$this->load->model()` | Dependency injection / `new Model()` | Auto-resolved |
| `$this->load->view()` | `return view()` | Blade |
| `$this->load->library()` | Service class / Facade | Injected |
| `$this->load->helper()` | Global helper files / Service | `app/Helpers/` |
| `$this->input->post()` | `$request->input()` / `$request->post()` | Type-hinted |
| `$this->input->get()` | `$request->query()` | — |
| `$this->session->userdata()` | `session()->get()` | — |
| `$this->session->set_userdata()` | `session()->put()` | — |
| `$this->session->set_flashdata()` | `session()->flash()` | — |
| `$this->session->sess_destroy()` | `Auth::logout()` + `request->session()->invalidate()` | — |
| `redirect()` | `return redirect()` | Must return |
| `base_url()` | `url()` / `asset()` | — |
| `site_url()` | `route()` / `url()` | Named routes |

### Query Builder

| CI3 | Laravel 12 |
|---|---|
| `$this->db->get($table)` | `DB::table($table)->get()` or `Model::all()` |
| `$this->db->where($col, $val)` | `->where($col, $val)` |
| `$this->db->join($table, $cond)` | `->join($table, $cond)` or Eloquent `with()` |
| `$this->db->insert($table, $data)` | `Model::create($data)` |
| `$this->db->insert_id()` | `$model->id` (after `save()` or `create()`) |
| `$this->db->update($table, $data)` | `$model->update($data)` |
| `$this->db->delete($table)` | `$model->delete()` or `Model::destroy($id)` |
| `->result_array()` | `->toArray()` |
| `->row()` | `->first()` |
| `->row_array()` | `->first()->toArray()` |
| `->num_rows()` | `->count()` |
| `$this->db->order_by($col, $dir)` | `->orderBy($col, $dir)` |
| `$this->db->select($cols)` | `->select($cols)` |

### Validation

| CI3 | Laravel 12 |
|---|---|
| `$this->form_validation->set_rules()` | Form Request class methods |
| `required` | `'required'` |
| `trim` | Handled by `$request->input()` or `TrimStrings` middleware |
| `min_length[n]` | `'min:n'` |
| `max_length[n]` | `'max:n'` |
| `matches[field]` | `'same:field'` or `'confirmed'` |
| `callback__check_captcha` | Custom validation rule class |
| `$this->form_validation->run()` | `$request->validated()` (in Form Request) |
| `validation_errors()` | `$errors` bag in Blade (`@error`) |

### Authentication

| CI3 | Laravel 12 |
|---|---|
| Manual session + `token` table | Laravel Auth (web guard) + Sanctum (API) |
| `password_verify()` | `Hash::check()` |
| `password_hash(PASSWORD_BCRYPT)` | `Hash::make()` |
| `sha1($password)` | `Hash::make()` (migrate SHA1 → bcrypt) |
| Custom admin session token | `Auth::guard('admin')->login()` |
| Manual `token_login` on user table | `$user->createToken()` (Sanctum) |

---

## 3. Directory Structure

### CI3 → Laravel 12

```
CI3 (application/)                Laravel 12
─────────────────────────────────────────────────────────────────
controllers/                   → app/Http/Controllers/
  Login.php                    →   Auth/AdminLoginController.php
  Member.php                   →   Admin/DashboardController.php
  User.php                     →   Admin/VoterController.php
  Album.php                    →   Admin/AlbumController.php
  Kategori.php                 →   Admin/CategoryController.php
  Paslon.php                   →   Admin/CandidateController.php
  Photo.php                    →   Admin/PhotoController.php
  Hasil.php                    →   Admin/ResultController.php
  Setting.php                  →   Admin/SettingController.php
  Testing.php                  →   (DELETE — dev artifact)

  api/Login.php                →   Api/AuthController.php
  api/Daftar.php               →   Api/AuthController.php (merged)
  api/Akun.php                 →   Api/AccountController.php
  api/Kategori.php             →   Api/CategoryController.php
  api/Paslon.php               →   Api/CandidateController.php
  api/Gambar.php               →   Api/PhotoController.php
  api/Suara.php                →   Api/VoteController.php
  api/Hasil.php                →   Api/ResultController.php

models/                        → app/Models/
  Admin_m.php                  →   Admin.php (split — see §6)
  Api_model.php                →   (split into individual models)

views/                         → resources/views/
  view/v_login.php             →   auth/admin-login.blade.php
  view/v_member.php            →   admin/dashboard.blade.php
  view/v_user.php              →   admin/voters/index.blade.php
  view/v_album.php             →   admin/albums/index.blade.php
  view/v_kategori.php          →   admin/categories/index.blade.php
  view/v_detailkategori.php    →   admin/categories/show.blade.php
  view/v_tambahkategori.php    →   admin/categories/create.blade.php
  view/v_paslon.php            →   admin/candidates/index.blade.php
  view/v_tambahpaslon.php      →   admin/candidates/create.blade.php
  view/v_photo.php             →   admin/photos/index.blade.php
  view/v_rebuildalbum.php      →   admin/photos/rebuild.blade.php
  view/v_detailalbum.php       →   admin/photos/album.blade.php
  view/v_hasil.php             →   admin/results/index.blade.php
  view/v_setting.php           →   admin/settings/index.blade.php
  template/v_header.php        →   layouts/admin.blade.php (@section head)
  template/v_navbar.php        →   layouts/admin.blade.php (@section navbar)
  template/v_sidebar.php       →   layouts/partials/sidebar.blade.php
  template/v_footer.php        →   layouts/admin.blade.php (@section footer)

helpers/                       → app/Helpers/ + global functions
  facevoting_helper.php        →   app/Helpers/CaptchaHelper.php
  keamanan_helper.php          →   (deleted — use Laravel's e())

libraries/                     → app/Services/
  (empty)                      →   app/Services/LambdaService.php (new)
                               →   app/Services/FaceXService.php (new)

config/                        → config/ + .env
  config.php                   →   config/app.php + .env
  database.php                 →   .env + config/database.php
  routes.php                   →   routes/web.php + routes/api.php
  autoload.php                 →   config/app.php providers + bootstrap
  rest.php                     →   config/cors.php + config/sanctum.php
  hooks.php                    →   app/Http/Middleware/ or Providers

third_party/                   → composer.json (require)
  REST Server                  →   (removed — use native Laravel API)
  AdminLTE                     →   composer require jeroennoten/laravel-adminlte

migrations/ (disabled)         → database/migrations/
  (SQL dump)                   →   one file per table (see §8)

gambar/                        → storage/app/public/images/
gambar/user/                   → storage/app/public/images/users/
captcha/                       → storage/app/public/captcha/

vendor/                        → vendor/ (new composer install)
index.php                      → public/index.php (Laravel entry point)
composer.json                  → composer.json (rewritten)
facevoting.sql                 → database/migrations/ (converted)
```

---

## 4. Routing

### CI3 `application/config/routes.php`

```php
// CI3 — implicit method routing, no explicit route definitions
$route['default_controller'] = 'login';
```

### Laravel 12 `routes/web.php`

```php
// Admin auth
Route::get('/', fn() => redirect()->route('admin.login'));
Route::get('/login',  [AdminLoginController::class, 'showForm'])->name('admin.login');
Route::post('/login', [AdminLoginController::class, 'login'])->name('admin.login.submit');
Route::post('/logout',[AdminLoginController::class, 'logout'])->name('admin.logout');

// Protected admin routes
Route::middleware(['auth:admin'])->prefix('admin')->name('admin.')->group(function () {
    Route::get('/dashboard', [DashboardController::class, 'index'])->name('dashboard');

    Route::resource('voters',    VoterController::class)->only(['index', 'destroy']);
    Route::patch('voters/{voter}/activate', [VoterController::class, 'activate'])
         ->name('voters.activate');

    Route::resource('categories', CategoryController::class)
         ->only(['index', 'create', 'store', 'show']);
    Route::patch('categories/{category}/open',  [CategoryController::class, 'open'])
         ->name('categories.open');
    Route::patch('categories/{category}/close', [CategoryController::class, 'close'])
         ->name('categories.close');
    Route::post('categories/{category}/voters', [CategoryController::class, 'addVoter'])
         ->name('categories.voters.add');

    Route::resource('candidates', CandidateController::class)
         ->only(['index', 'create', 'store', 'destroy']);

    Route::resource('photos', PhotoController::class)->only(['index', 'destroy']);
    Route::post('photos/{photo}/train', [PhotoController::class, 'train'])
         ->name('photos.train');
    Route::get('photos/album',   [PhotoController::class, 'viewAlbum'])
         ->name('photos.album');
    Route::post('photos/rebuild', [PhotoController::class, 'rebuild'])
         ->name('photos.rebuild');

    Route::get('albums',  [AlbumController::class, 'index'])->name('albums.index');
    Route::get('results', [ResultController::class, 'index'])->name('results.index');
    Route::get('settings',[SettingController::class, 'edit'])->name('settings.edit');
    Route::patch('settings',[SettingController::class, 'update'])->name('settings.update');
});
```

### Laravel 12 `routes/api.php`

```php
// Public API routes
Route::post('/auth/register', [AuthController::class, 'register']);
Route::post('/auth/login',    [AuthController::class, 'login']);
Route::get('/auth/status',    [AuthController::class, 'status']);
Route::get('/categories',     [CategoryController::class, 'index']);
Route::get('/results',        [VoteController::class, 'results']);

// Authenticated API routes (Sanctum)
Route::middleware('auth:sanctum')->group(function () {
    Route::post('/auth/refresh',          [AuthController::class, 'refresh']);
    Route::get('/account',                [AccountController::class, 'show']);
    Route::put('/account',                [AccountController::class, 'update']);
    Route::get('/categories/mine',        [CategoryController::class, 'mine']);
    Route::post('/candidates',            [CandidateController::class, 'index']);
    Route::post('/candidates/detail',     [CandidateController::class, 'show']);
    Route::post('/photos/enroll',         [PhotoController::class, 'enroll']);
    Route::post('/photos/verify',         [PhotoController::class, 'verify']);
    Route::post('/votes',                 [VoteController::class, 'store']);
    Route::get('/votes/history/{user}',   [VoteController::class, 'history']);
});
```

### Key Routing Differences

| CI3 | Laravel 12 |
|---|---|
| URL segments map implicitly to method names | Explicit route definitions required |
| `GET /Kategori/Buka/8` | `PATCH /admin/categories/8/open` (RESTful) |
| `GET /user/hapus/5` | `DELETE /admin/voters/5` (uses DELETE verb) |
| No route naming | Named routes via `->name()` |
| No route model binding | `Route::bind` or implicit binding |
| Default controller in config | `Route::get('/', ...)` |
| API uses `_post` / `_get` suffixed methods | HTTP verb drives method selection |

---

## 5. Controllers

### CI3 Controller Pattern → Laravel Controller Pattern

**CI3:**
```php
class Kategori extends CI_Controller {
    public function __construct() {
        parent::__construct();
        $this->load->model('Admin_m', 'admin');
        if ($this->session->userdata('is_login_in') !== TRUE) {
            redirect('login');
        }
        $this->tokenSession = $this->session->userdata('token');
        $this->tokenServer  = $this->admin->get_token_byId(1)->row()->token;
    }

    public function tambah() {
        $this->form_validation->set_rules('namaKategori', 'Nama Kategori', 'required|trim');
        if ($this->form_validation->run() === false) {
            $this->load->view('view/v_tambahkategori', $data);
        } else {
            // process...
            redirect('Kategori');
        }
    }
}
```

**Laravel 12:**
```php
namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Http\Requests\Admin\StoreCategoryRequest;
use App\Models\Category;
use Illuminate\Http\RedirectResponse;
use Illuminate\View\View;

class CategoryController extends Controller
{
    public function create(): View
    {
        return view('admin.categories.create');
    }

    public function store(StoreCategoryRequest $request): RedirectResponse
    {
        $path = $request->file('logo')->store('images', 'public');

        Category::create([
            'nama_kategori'  => $request->namaKategori,
            'logo_kategori'  => basename($path),
            'status_kategori' => 1,
        ]);

        return redirect()->route('admin.categories.index')
                         ->with('success', 'Kategori berhasil ditambahkan!');
    }
}
```

### Controller Mapping Table

| CI3 Controller | CI3 Method | Laravel Controller | Laravel Method |
|---|---|---|---|
| `Login` | `index()` GET | `Auth\AdminLoginController` | `showForm()` |
| `Login` | `index()` POST | `Auth\AdminLoginController` | `login()` |
| `Member` | `index()` | `Admin\DashboardController` | `index()` |
| `Member` | `logout()` | `Auth\AdminLoginController` | `logout()` |
| `User` | `index()` | `Admin\VoterController` | `index()` |
| `User` | `aktivasi($idx)` | `Admin\VoterController` | `activate(Voter $voter)` |
| `User` | `hapus($idx)` | `Admin\VoterController` | `destroy(Voter $voter)` |
| `Album` | `index()` | `Admin\AlbumController` | `index()` |
| `Kategori` | `index()` | `Admin\CategoryController` | `index()` |
| `Kategori` | `detail($idx)` | `Admin\CategoryController` | `show(Category $category)` |
| `Kategori` | `tambah()` GET | `Admin\CategoryController` | `create()` |
| `Kategori` | `tambah()` POST | `Admin\CategoryController` | `store(StoreCategoryRequest)` |
| `Kategori` | `tambah_pemilih($k,$u)` | `Admin\CategoryController` | `addVoter(Category, Voter)` |
| `Kategori` | `Buka($idx)` | `Admin\CategoryController` | `open(Category $category)` |
| `Kategori` | `Tutup($idx)` | `Admin\CategoryController` | `close(Category $category)` |
| `Paslon` | `index()` | `Admin\CandidateController` | `index()` |
| `Paslon` | `tambah()` GET | `Admin\CandidateController` | `create()` |
| `Paslon` | `tambah()` POST | `Admin\CandidateController` | `store(StoreCandidateRequest)` |
| `Paslon` | `hapus($id)` | `Admin\CandidateController` | `destroy(Candidate $candidate)` |
| `Photo` | `index()` | `Admin\PhotoController` | `index()` |
| `Photo` | `rekam($idx)` | `Admin\PhotoController` | `train(Photo $photo)` |
| `Photo` | `hapus($idx)` | `Admin\PhotoController` | `destroy(Photo $photo)` |
| `Photo` | `viewAlbum()` | `Admin\PhotoController` | `viewAlbum()` |
| `Photo` | `rebuild()` | `Admin\PhotoController` | `rebuild()` |
| `Hasil` | `index()` | `Admin\ResultController` | `index()` |
| `Setting` | `index()` | `Admin\SettingController` | `edit()` / `update()` |
| `Testing` | all | *(deleted)* | — |
| `api/Login` | `otentikasi_post()` | `Api\AuthController` | `login()` |
| `api/Login` | `sudahlogin_post()` | `Api\AuthController` | `refresh()` |
| `api/Login` | `cekstatus_get()` | `Api\AuthController` | `status()` |
| `api/Daftar` | `tambah_post()` | `Api\AuthController` | `register()` |
| `api/Akun` | `tampil_get()` | `Api\AccountController` | `show()` |
| `api/Akun` | `tampil_put($id)` | `Api\AccountController` | `update()` |
| `api/Kategori` | `tampil_get()` | `Api\CategoryController` | `mine()` |
| `api/Kategori` | `semua_get()` | `Api\CategoryController` | `index()` |
| `api/Paslon` | `tampil_post()` | `Api\CandidateController` | `index()` |
| `api/Paslon` | `detail_post()` | `Api\CandidateController` | `show()` |
| `api/Gambar` | `tambah_post()` | `Api\PhotoController` | `enroll()` |
| `api/Gambar` | `cek_post()` | `Api\PhotoController` | `verify()` |
| `api/Suara` | `vote_post()` | `Api\VoteController` | `store()` |
| `api/Suara` | `tampil_get($id)` | `Api\VoteController` | `history()` |
| `api/Suara` | `perolehan_get()` | `Api\VoteController` | `results()` |
| `api/Hasil` | *(stub, empty)* | `Api\ResultController` | *(implement)* |

---

## 6. Models → Eloquent

### CI3 God-Model Pattern → One Eloquent Model per Table

CI3's `Admin_m` and `Api_model` are god-objects that mix queries for every table. In Laravel, each table gets its own Eloquent model.

### Model Mapping

| CI3 Model | Table(s) handled | Laravel Eloquent Model | File |
|---|---|---|---|
| `Admin_m` | `admin` | `Admin` | `app/Models/Admin.php` |
| `Admin_m` | `token` | *(replaced by Sanctum)* | — |
| `Admin_m` / `Api_model` | `user` | `User` | `app/Models/User.php` |
| `Admin_m` / `Api_model` | `detail_user` | `UserDetail` | `app/Models/UserDetail.php` |
| `Admin_m` / `Api_model` | `album` | `Album` | `app/Models/Album.php` |
| `Admin_m` / `Api_model` | `photo` | `Photo` | `app/Models/Photo.php` |
| `Api_model` | `pencocokan` | `FaceMatch` | `app/Models/FaceMatch.php` |
| `Admin_m` / `Api_model` | `kategori` | `Category` | `app/Models/Category.php` |
| `Admin_m` / `Api_model` | `paslon` | `Candidate` | `app/Models/Candidate.php` |
| `Admin_m` / `Api_model` | `detail_paslon` | `CandidateDetail` | `app/Models/CandidateDetail.php` |
| `Admin_m` / `Api_model` | `otorisasi_pemilih` | `VoterAuthorization` | `app/Models/VoterAuthorization.php` |
| `Admin_m` / `Api_model` | `voting` | `Vote` | `app/Models/Vote.php` |
| *(unused)* | `tbjurusan` | *(omit or `Department`)* | — |

### Query Method Translation Examples

**CI3 `Admin_m::get_data_paslon()`:**
```php
// CI3
public function get_data_paslon() {
    $this->db->join('kategori', 'kategori.id_kategori = paslon.id_kategori');
    $this->db->join('detail_paslon', 'detail_paslon.id_paslon = paslon.id_paslon');
    return $this->db->get('paslon');
}
// Usage: $this->admin->get_data_paslon()->result_array()
```

**Laravel Eloquent:**
```php
// Eloquent (app/Models/Candidate.php)
class Candidate extends Model {
    public function category(): BelongsTo {
        return $this->belongsTo(Category::class, 'id_kategori');
    }
    public function detail(): HasOne {
        return $this->hasOne(CandidateDetail::class, 'id_paslon');
    }
}

// In controller:
Candidate::with(['category', 'detail'])->get();
```

---

**CI3 `Admin_m::get_data_calon($id)` (subquery):**
```php
// CI3
$this->db->where("user.id_user NOT IN (
    select otorisasi_pemilih.id_user
    from otorisasi_pemilih
    where otorisasi_pemilih.id_kategori = $data
)", NULL);
return $this->db->get("user");
```

**Laravel Eloquent:**
```php
// Eloquent
User::whereDoesntHave('authorizations', function ($q) use ($categoryId) {
    $q->where('id_kategori', $categoryId);
})->with('detail')->get();
```

---

**CI3 `Api_model::simpan_voting()`:**
```php
// CI3
public function simpan_voting($data) {
    $this->db->insert($this->tableVoting, $data);
}
```

**Laravel Eloquent:**
```php
// Eloquent
Vote::create([
    'id_user'       => $user->id,
    'id_kategori'   => $categoryId,
    'id_paslon'     => $candidateId,
    'tanggal_voting' => now(),
]);
```

---

**CI3 `Api_model::perbaharui_paslon()` (race-prone counter):**
```php
// CI3 — read-modify-write race condition
$getPerolehan = $this->api->get_data_paslon2($idPaslon)->row()->perolehan;
$getPerolehan += 1;
$this->api->perbaharui_paslon(['perolehan' => $getPerolehan], $idPaslon);
```

**Laravel Eloquent — atomic increment:**
```php
// Laravel — atomic DB-level increment (no race condition)
Candidate::where('id_paslon', $candidateId)->increment('perolehan');
```

---

### Complete Eloquent Model Definitions

#### `app/Models/Admin.php`
```php
class Admin extends Authenticatable
{
    use HasFactory;
    protected $table    = 'admin';
    protected $fillable = ['username', 'password', 'status'];
    protected $hidden   = ['password'];
}
```

#### `app/Models/User.php`
```php
class User extends Authenticatable
{
    use HasApiTokens, HasFactory;   // HasApiTokens for Sanctum
    protected $table    = 'user';
    protected $fillable = ['email','password','token_firebase','entry_id','status'];
    protected $hidden   = ['password','token_login'];

    public function detail(): HasOne        { return $this->hasOne(UserDetail::class, 'id_user'); }
    public function photos(): HasMany       { return $this->hasMany(Photo::class, 'id_user'); }
    public function authorizations(): HasMany { return $this->hasMany(VoterAuthorization::class, 'id_user'); }
    public function votes(): HasMany        { return $this->hasMany(Vote::class, 'id_user'); }
    public function faceMatches(): HasMany  { return $this->hasMany(FaceMatch::class, 'id_user'); }
}
```

#### `app/Models/UserDetail.php`
```php
class UserDetail extends Model
{
    protected $table    = 'detail_user';
    protected $fillable = ['id_user','nama','identitas'];
    public $timestamps  = false;

    public function user(): BelongsTo { return $this->belongsTo(User::class, 'id_user'); }
}
```

#### `app/Models/Category.php`
```php
class Category extends Model
{
    protected $table    = 'kategori';
    protected $fillable = ['nama_kategori','logo_kategori','status_kategori'];
    public $timestamps  = false;

    public function candidates(): HasMany      { return $this->hasMany(Candidate::class, 'id_kategori'); }
    public function authorizations(): HasMany  { return $this->hasMany(VoterAuthorization::class, 'id_kategori'); }
    public function votes(): HasMany           { return $this->hasMany(Vote::class, 'id_kategori'); }

    public function scopeOpen($query)          { return $query->where('status_kategori', 2); }
    public function scopeSetup($query)         { return $query->where('status_kategori', 1); }
}
```

#### `app/Models/Candidate.php`
```php
class Candidate extends Model
{
    protected $table    = 'paslon';
    protected $fillable = ['id_kategori','judul_paslon','ketua_paslon','wakil_paslon',
                           'photo1_paslon','photo2_paslon','perolehan'];
    public $timestamps  = false;

    public function category(): BelongsTo { return $this->belongsTo(Category::class, 'id_kategori'); }
    public function detail(): HasOne      { return $this->hasOne(CandidateDetail::class, 'id_paslon'); }
    public function votes(): HasMany      { return $this->hasMany(Vote::class, 'id_paslon'); }
}
```

#### `app/Models/VoterAuthorization.php`
```php
class VoterAuthorization extends Model
{
    protected $table    = 'otorisasi_pemilih';
    protected $fillable = ['id_user','id_kategori','status'];
    public $timestamps  = false;

    public function user(): BelongsTo     { return $this->belongsTo(User::class, 'id_user'); }
    public function category(): BelongsTo { return $this->belongsTo(Category::class, 'id_kategori'); }
}
```

#### `app/Models/Vote.php`
```php
class Vote extends Model
{
    protected $table    = 'voting';
    protected $fillable = ['id_user','id_kategori','id_paslon','tanggal_voting'];
    protected $dates    = ['tanggal_voting'];
    const CREATED_AT    = 'tanggal_voting';
    const UPDATED_AT    = null;

    public function user(): BelongsTo      { return $this->belongsTo(User::class, 'id_user'); }
    public function category(): BelongsTo  { return $this->belongsTo(Category::class, 'id_kategori'); }
    public function candidate(): BelongsTo { return $this->belongsTo(Candidate::class, 'id_paslon'); }
}
```

---

## 7. Eloquent Relationships

### Full Relationship Map

```
Admin ──────────────────── (no Eloquent relation; uses Sanctum sessions)

User
├── hasOne      → UserDetail        (detail_user.id_user)
├── hasMany     → Photo             (photo.id_user)
├── hasMany     → VoterAuthorization (otorisasi_pemilih.id_user)
├── hasMany     → Vote              (voting.id_user)
└── hasMany     → FaceMatch         (pencocokan.id_user)

Album
└── hasMany     → Photo             (photo.id_album)

Photo
├── belongsTo   → User              (user.id_user)
└── belongsTo   → Album             (album.id_album)

Category
├── hasMany     → Candidate         (paslon.id_kategori)
├── hasMany     → VoterAuthorization (otorisasi_pemilih.id_kategori)
└── hasMany     → Vote              (voting.id_kategori)

Candidate
├── belongsTo   → Category          (kategori.id_kategori)
├── hasOne      → CandidateDetail   (detail_paslon.id_paslon)
└── hasMany     → Vote              (voting.id_paslon)

CandidateDetail
└── belongsTo   → Candidate         (paslon.id_paslon)

VoterAuthorization
├── belongsTo   → User              (user.id_user)
└── belongsTo   → Category          (kategori.id_kategori)

Vote
├── belongsTo   → User              (user.id_user)
├── belongsTo   → Category          (kategori.id_kategori)
└── belongsTo   → Candidate         (paslon.id_paslon)

FaceMatch
└── belongsTo   → User              (user.id_user)
```

### Replacing Manual JOINs with Eager Loading

| CI3 Manual JOIN | Laravel Equivalent |
|---|---|
| `JOIN detail_user ON detail_user.id_user = user.id_user` | `User::with('detail')->get()` |
| `JOIN kategori ON kategori.id_kategori = paslon.id_kategori` | `Candidate::with('category')->get()` |
| `JOIN detail_paslon ON detail_paslon.id_paslon = paslon.id_paslon` | `Candidate::with('detail')->get()` |
| `LEFT JOIN detail_user ON detail_user.id_user = otorisasi_pemilih.id_user` | `VoterAuthorization::with('user.detail')->get()` |
| Subquery for unauthorized users | `User::whereDoesntHave('authorizations', fn($q) => $q->where('id_kategori', $id))` |

---

## 8. Database Migrations

### CI3 Approach → Laravel Migrations

CI3 had migrations disabled. All schema came from `facevoting.sql`. In Laravel, each table becomes a migration file.

```
database/migrations/
  2024_01_01_000001_create_admin_table.php
  2024_01_01_000002_create_albums_table.php
  2024_01_01_000003_create_users_table.php
  2024_01_01_000004_create_user_details_table.php
  2024_01_01_000005_create_categories_table.php
  2024_01_01_000006_create_candidates_table.php
  2024_01_01_000007_create_candidate_details_table.php
  2024_01_01_000008_create_voter_authorizations_table.php
  2024_01_01_000009_create_votes_table.php
  2024_01_01_000010_create_photos_table.php
  2024_01_01_000011_create_face_matches_table.php
  2024_01_01_000012_add_unique_constraints.php        ← fixes from schema-analysis
  2024_01_01_000013_create_personal_access_tokens_table.php  ← Sanctum
```

### Migration Example: `users` table with fixes applied

```php
Schema::create('user', function (Blueprint $table) {
    $table->id('id_user');
    $table->string('email', 100)->unique();          // UNIQUE added (was missing)
    $table->string('password', 255);                  // extended for bcrypt
    $table->string('token_firebase', 255)->nullable();
    $table->string('entry_id', 255);
    $table->tinyInteger('status')->default(2);
    $table->timestamps();
});
```

### Migration Example: `otorisasi_pemilih` with fixes applied

```php
Schema::create('otorisasi_pemilih', function (Blueprint $table) {
    $table->id('id_otorisasi');
    $table->unsignedBigInteger('id_user');
    $table->unsignedBigInteger('id_kategori');
    $table->tinyInteger('status')->default(1);
    $table->timestamps();

    $table->unique(['id_user', 'id_kategori']);       // UNIQUE added (was missing)
    $table->foreign('id_user')->references('id_user')->on('user')->cascadeOnDelete();
    $table->foreign('id_kategori')->references('id_kategori')->on('kategori')->cascadeOnDelete();
});
```

### Migration Example: `voting` table with fixes applied

```php
Schema::create('voting', function (Blueprint $table) {
    $table->id('id_voting');
    $table->unsignedBigInteger('id_user');
    $table->unsignedBigInteger('id_kategori');
    $table->unsignedBigInteger('id_paslon');
    $table->timestamp('tanggal_voting')->useCurrent();

    $table->unique(['id_user', 'id_kategori']);       // UNIQUE added — double-vote guard
    $table->foreign('id_user')->references('id_user')->on('user')->cascadeOnDelete();
    $table->foreign('id_kategori')->references('id_kategori')->on('kategori')->cascadeOnDelete();
    $table->foreign('id_paslon')->references('id_paslon')->on('paslon')->cascadeOnDelete();
});
```

### Password Migration Strategy

CI3 stored voter passwords as SHA1. During migration:

```php
// In a migration or seeder — mark SHA1 passwords for re-hashing
Schema::table('user', function (Blueprint $table) {
    $table->boolean('needs_password_reset')->default(false)->after('status');
});

// On next login, detect SHA1, verify, re-hash with bcrypt:
// In AuthController::login()
if (strlen($user->password) === 40 && hash_equals($user->password, sha1($request->password))) {
    $user->update([
        'password'              => Hash::make($request->password),
        'needs_password_reset'  => false,
    ]);
} elseif (!Hash::check($request->password, $user->password)) {
    return response()->json(['message' => 'Invalid credentials'], 401);
}
```

---

## 9. Authentication

### Admin Web Authentication

| CI3 | Laravel 12 |
|---|---|
| Custom `token` table | Laravel session-based auth guard |
| `sha1(datetime)` token | PHP session (Laravel manages) |
| Manual `cek_token()` on every request | Auth middleware |
| `password_verify()` | `Hash::check()` |
| Session variables `is_login_in`, `token` | `Auth::guard('admin')->check()` |
| `_unlogin()` helper | `Auth::guard('admin')->logout()` |

**Laravel config/auth.php:**
```php
'guards' => [
    'web' => ['driver' => 'session', 'provider' => 'users'],
    'admin' => ['driver' => 'session', 'provider' => 'admins'],  // separate guard
],
'providers' => [
    'users'  => ['driver' => 'eloquent', 'model' => App\Models\User::class],
    'admins' => ['driver' => 'eloquent', 'model' => App\Models\Admin::class],
],
```

**Laravel `Auth\AdminLoginController::login()`:**
```php
public function login(AdminLoginRequest $request): RedirectResponse
{
    $credentials = $request->only('username', 'password');

    if (!Auth::guard('admin')->attempt($credentials)) {
        return back()->withErrors(['username' => 'Username atau password salah.']);
    }

    $request->session()->regenerate();
    return redirect()->route('admin.dashboard');
}
```

### API Authentication (Voters)

| CI3 | Laravel 12 |
|---|---|
| `token_login` column on `user` table | Sanctum `personal_access_tokens` table |
| `substr(str_shuffle(...), 1, 10)` | `$user->createToken('mobile')` |
| Manual `WHERE token_login = ?` | `Auth::guard('sanctum')->user()` |
| Token returned as plain string in response | `$token->plainTextToken` |
| No token expiry | Sanctum `expiration` config (minutes) |
| No token scopes | Sanctum abilities/scopes |

**Laravel `Api\AuthController::login()`:**
```php
public function login(LoginRequest $request): JsonResponse
{
    $user = User::where('email', $request->email)->first();

    if (!$user || !Hash::check($request->password, $user->password)) {
        return response()->json(['message' => 'Credentials incorrect'], 401);
    }

    $user->tokens()->delete();  // revoke old tokens
    $token = $user->createToken('mobile-app')->plainTextToken;

    return response()->json([
        'token'    => $token,
        'id_user'  => $user->id_user,
        'nama'     => $user->detail->nama,
        'validasi' => $user->status,
    ]);
}
```

**Protecting API routes:**
```php
// routes/api.php
Route::middleware('auth:sanctum')->group(function () {
    Route::post('/votes', [VoteController::class, 'store']);
    // ...
});

// In controller — get authenticated user:
$user = $request->user();  // replaces $this->post('id_user')
```

---

## 10. Middleware

### CI3 Constructor Guards → Laravel Middleware

**CI3 pattern (repeated in every controller constructor):**
```php
if ($this->session->userdata('is_login_in') !== TRUE) {
    redirect('login');
}
$this->tokenSession = $this->session->userdata('token');
$this->tokenServer  = $this->admin->get_token_byId(1)->row()->token;
// then in every method:
if ($this->tokenSession != $this->tokenServer) { _unlogin(); }
```

**Laravel: single middleware applied once:**
```php
// app/Http/Middleware/AdminAuthenticated.php
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

// Register in bootstrap/app.php or Kernel:
Route::middleware(['auth:admin'])->group(...);
```

### Middleware Mapping

| CI3 Mechanism | Laravel Middleware |
|---|---|
| Session `is_login_in` check in constructor | `auth:admin` (built-in) |
| Token DB cross-check in every method | `auth:admin` (eliminates need for token table) |
| `_unlogin()` helper | `auth:admin` + `RedirectIfAuthenticated` |
| API `id_user` + `token_login` manual check | `auth:sanctum` |
| CORS (disabled in CI3) | `Illuminate\Http\Middleware\HandleCors` + `config/cors.php` |
| CSRF (not present in CI3 API) | `\Illuminate\Foundation\Http\Middleware\ValidateCsrfToken` (auto on web routes) |
| Input trimming (`trim` in validation rules) | `TrimStrings` (auto in Laravel) |
| Output XSS encoding (`filter_output`) | `e()` helper in Blade (`{{ $var }}` auto-escapes) |

---

## 11. Form Validation → Form Requests

### CI3 → Laravel Form Request

**CI3 inline validation:**
```php
$this->form_validation->set_rules('password1', 'Password', 'trim|min_length[4]|max_length[16]|required');
$this->form_validation->set_rules('password2', 'Password', 'trim|matches[password1]|required');
if ($this->form_validation->run() === false) {
    // show form again
}
```

**Laravel Form Request:**
```php
// app/Http/Requests/Admin/ChangePasswordRequest.php
class ChangePasswordRequest extends FormRequest
{
    public function rules(): array
    {
        return [
            'password'              => ['required', 'string', 'min:4', 'max:16', 'confirmed'],
            'password_confirmation' => ['required'],
        ];
    }

    public function messages(): array
    {
        return [
            'password.min'       => 'Panjang karakter Password minimal 4 karakter!',
            'password.max'       => 'Panjang karakter Password maksimal 16 karakter!',
            'password.confirmed' => 'Password tidak sama!',
        ];
    }
}

// Controller method signature — validation runs automatically:
public function update(ChangePasswordRequest $request): RedirectResponse
{
    Auth::guard('admin')->user()->update([
        'password' => Hash::make($request->password),
    ]);
    return redirect()->route('admin.settings.edit')->with('success', 'Password updated!');
}
```

### Form Request Class Map

| CI3 Validation | Laravel Form Request | File |
|---|---|---|
| `Login::index()` | `AdminLoginRequest` | `Requests/Admin/AdminLoginRequest.php` |
| `Kategori::tambah()` | `StoreCategoryRequest` | `Requests/Admin/StoreCategoryRequest.php` |
| `Paslon::tambah()` | `StoreCandidateRequest` | `Requests/Admin/StoreCandidateRequest.php` |
| `Setting::index()` | `ChangePasswordRequest` | `Requests/Admin/ChangePasswordRequest.php` |
| `api/Daftar::tambah_post()` | `RegisterRequest` | `Requests/Api/RegisterRequest.php` |
| `api/Akun::tampil_put()` | `UpdateProfileRequest` | `Requests/Api/UpdateProfileRequest.php` |
| `api/Suara::vote_post()` | `VoteRequest` | `Requests/Api/VoteRequest.php` |
| `api/Gambar::tambah_post()` | `EnrollPhotoRequest` | `Requests/Api/EnrollPhotoRequest.php` |
| `api/Gambar::cek_post()` | `VerifyPhotoRequest` | `Requests/Api/VerifyPhotoRequest.php` |

### CAPTCHA Validation

| CI3 | Laravel 12 |
|---|---|
| `callback__check_captcha` in `set_rules` | Custom Rule class + session compare |
| `create_captcha(config_captcha())` | `gregwar/captcha` package or `mews/captcha` |
| CAPTCHA word in `$_SESSION['captchaword']` | `session('captcha_word')` |

```php
// app/Rules/ValidCaptcha.php
class ValidCaptcha implements ValidationRule
{
    public function validate(string $attribute, mixed $value, Closure $fail): void
    {
        if (!hash_equals(session('captcha_word', ''), strtoupper($value))) {
            $fail('Captcha yang anda masukkan salah!');
        }
    }
}
```

### Validation Rule Translation

| CI3 Rule | Laravel Rule |
|---|---|
| `required` | `'required'` |
| `trim` | Removed (handled by `TrimStrings` middleware) |
| `min_length[n]` | `'min:n'` |
| `max_length[n]` | `'max:n'` |
| `matches[field]` | `'same:field'` or `'confirmed'` (use `confirmed` + `field_confirmation` naming) |
| `callback__check_captcha` | `new ValidCaptcha()` |
| File upload config `allowed_types` | `'mimes:jpg,jpeg,png,gif'` |
| File upload config `max_size` (KB) | `'max:2424'` (Laravel also in KB) |
| File upload config `max_width/height` | `'dimensions:max_width=4200,max_height=4200'` |

---

## 12. Session

### CI3 Session → Laravel Session

| CI3 | Laravel 12 |
|---|---|
| `$this->session->set_userdata(['key' => 'val'])` | `session()->put('key', 'val')` |
| `$this->session->userdata('key')` | `session('key')` or `session()->get('key')` |
| `$this->session->set_flashdata('key', 'msg')` | `session()->flash('key', 'msg')` |
| `$this->session->sess_destroy()` | `Auth::logout(); $request->session()->invalidate(); $request->session()->regenerateToken();` |
| Session driver: `files` | `SESSION_DRIVER=file` in `.env` (default) |
| Session expiration: 7200s | `SESSION_LIFETIME=120` (minutes) in `.env` |
| Encryption key: `'mrasT'` | `APP_KEY=base64:...` (generated, 32 bytes) |

### Flash Messages

**CI3:**
```php
$this->session->set_flashdata('pesan', '<div class="alert alert-success">...</div>');
// In view: echo $this->session->flashdata('pesan');
```

**Laravel Blade:**
```php
// Controller:
return redirect()->back()->with('success', 'User berhasil diaktifkan!');

// Blade layout:
@if (session('success'))
    <div class="alert alert-success">{{ session('success') }}</div>
@endif
@if (session('error'))
    <div class="alert alert-danger">{{ session('error') }}</div>
@endif
```

---

## 13. File Uploads → Laravel Storage

### CI3 Upload Library → Laravel Storage

| CI3 | Laravel 12 |
|---|---|
| `$this->load->library('upload', $config)` | `$request->file('field')` |
| `$config['upload_path'] = './gambar/'` | `Storage::disk('public')` |
| `$config['allowed_types'] = 'jpg|jpeg|png'` | `'mimes:jpg,jpeg,png'` in Form Request |
| `$config['max_size'] = 10000` | `'max:10000'` in Form Request |
| `$this->upload->do_upload('field')` | `$request->file('field')->store(...)` |
| `$this->upload->data('file_name')` | Return value of `store()` |
| `FCPATH . "gambar/user/" . $filename` | `storage_path('app/public/images/users/' . $filename)` |
| `unlink(FCPATH . "gambar/user/" . $file)` | `Storage::disk('public')->delete('images/users/' . $file)` |

**CI3 upload pattern:**
```php
$config['upload_path']   = './gambar/';
$config['allowed_types'] = 'jpg|jpeg|png|gif';
$config['max_size']      = 2424;
$config['file_name']     = substr(sha1(time()), 0, 10);
$this->load->library('upload', $config);
if (!$this->upload->do_upload('logo')) {
    $error = $this->upload->display_errors();
}
$filename = $this->upload->data('file_name');
```

**Laravel equivalent:**
```php
// Form Request validation (handles type/size constraints)
// 'logo' => ['required', 'file', 'mimes:jpg,jpeg,png,gif', 'max:2424']

// Controller:
$filename = Str::random(10) . '.' . $request->file('logo')->getClientOriginalExtension();
$path = $request->file('logo')->storeAs('images', $filename, 'public');
// $path = 'images/a3b7x9z1kp.jpg'
```

### Storage Disk Configuration

```php
// config/filesystems.php
'disks' => [
    'public' => [
        'driver'     => 'local',
        'root'       => storage_path('app/public'),
        'url'        => env('APP_URL') . '/storage',
        'visibility' => 'public',
    ],
    'user_photos' => [
        'driver' => 'local',
        'root'   => storage_path('app/public/images/users'),
        'url'    => env('APP_URL') . '/storage/images/users',
    ],
],
```

**Serving files** — run once: `php artisan storage:link` (creates `public/storage` symlink).

### Existing File Migration

```bash
# Move CI3 gambar/ to Laravel storage
cp -r /path/to/ci3/gambar/       storage/app/public/images/
cp -r /path/to/ci3/gambar/user/  storage/app/public/images/users/
```

---

## 14. Views → Blade

### CI3 View Loading → Blade Templates

**CI3 template assembly (4 separate loads):**
```php
$this->load->view('template/v_header', $data);
$this->load->view('template/v_navbar',  $data);
$this->load->view('template/v_sidebar', $data);
$this->load->view('konten/k_user',      $data);
$this->load->view('template/v_footer',  $data);
```

**Laravel Blade layout:**
```blade
{{-- resources/views/layouts/admin.blade.php --}}
<!DOCTYPE html>
<html>
<head>
    <title>@yield('title', 'FaceVoting Admin')</title>
    @vite(['resources/css/app.css', 'resources/js/app.js'])
    @stack('styles')
</head>
<body class="hold-transition sidebar-mini layout-fixed">
<div class="wrapper">
    @include('layouts.partials.navbar')
    @include('layouts.partials.sidebar')
    <div class="content-wrapper">
        @yield('content')
    </div>
    @include('layouts.partials.footer')
</div>
@stack('scripts')
</body>
</html>
```

```blade
{{-- resources/views/admin/voters/index.blade.php --}}
@extends('layouts.admin')

@section('title', 'Data User | FaceVoting')

@section('content')
    @if (session('success'))
        <div class="alert alert-success">{{ session('success') }}</div>
    @endif

    <table>
        @foreach ($voters as $voter)
            <tr>
                <td>{{ $voter->detail->nama }}</td>
                <td>
                    <a href="{{ route('admin.voters.activate', $voter) }}">Aktifkan</a>
                    <form method="POST" action="{{ route('admin.voters.destroy', $voter) }}">
                        @csrf @method('DELETE')
                        <button type="submit">Hapus</button>
                    </form>
                </td>
            </tr>
        @endforeach
    </table>
@endsection
```

### CI3 View Syntax → Blade Equivalents

| CI3 View (PHP) | Blade |
|---|---|
| `<?php foreach($data as $row): ?>` | `@foreach($data as $row)` |
| `<?php endforeach; ?>` | `@endforeach` |
| `<?php if($cond): ?>` | `@if($cond)` |
| `<?php echo $var; ?>` | `{{ $var }}` (auto-escaped) |
| `<?php echo $html_string; ?>` | `{!! $htmlString !!}` (raw, use carefully) |
| `<?php echo form_open('/url'); ?>` | `<form method="POST" action="{{ route('name') }}">@csrf` |
| `<?php echo anchor('/path', 'text'); ?>` | `<a href="{{ route('name') }}">text</a>` |
| `<?php echo base_url('gambar/' . $logo); ?>` | `{{ asset('storage/images/' . $logo) }}` |
| `<?php echo $this->session->flashdata('pesan'); ?>` | `{{ session('error') }}` with `@if` |
| `<?php echo validation_errors(); ?>` | `@error('field') {{ $message }} @enderror` |

### AdminLTE Integration

```bash
composer require jeroennoten/laravel-adminlte
php artisan adminlte:install
```

This provides Blade components, sidebar config, and asset publishing — replacing the manual AdminLTE includes in CI3 view templates.

---

## 15. Helpers

### CI3 Helpers → Laravel Equivalents

#### `facevoting_helper.php`

| CI3 Function | Laravel Equivalent | Notes |
|---|---|---|
| `config_captcha()` | Inline config in controller or `config/captcha.php` | |
| `_unlogin()` | `Auth::guard('admin')->logout()` + redirect | Called by middleware, not manually |

#### `keamanan_helper.php`

| CI3 Function | Laravel Equivalent | Notes |
|---|---|---|
| `filter_output($str)` → `htmlspecialchars($str, ENT_QUOTES)` | `e($str)` or `{{ $var }}` in Blade | Blade auto-escapes with `{{ }}` — function no longer needed |

#### Built-in CI3 Helpers → Laravel

| CI3 Helper | Laravel Equivalent |
|---|---|
| `form_helper` — `form_open()`, `form_close()` | Blade `<form>` with `@csrf` |
| `form_helper` — `form_input()`, `set_value()` | Blade `<input>` with `old()` |
| `url_helper` — `base_url()` | `url()` or `asset()` |
| `url_helper` — `site_url()` | `route('name')` |
| `url_helper` — `redirect()` | `return redirect()->route('name')` |
| `captcha_helper` — `create_captcha()` | `mews/captcha` package |

#### Creating Custom Global Helpers

```php
// app/Helpers/FacevotingHelper.php
if (!function_exists('generate_filename')) {
    function generate_filename(): string {
        return substr(sha1(now()->timestamp . random_int(0, 9999)), 0, 10);
    }
}

// Register in composer.json:
"autoload": {
    "files": ["app/Helpers/FacevotingHelper.php"]
}
```

---

## 16. Libraries → Services

### CI3 Libraries → Laravel Service Classes

CI3's `application/libraries/` is empty, but three external integrations need service wrappers.

#### Lambda Face Recognition → `LambdaService`

**CI3 (inline cURL in Photo controller):**
```php
$curl = curl_init();
curl_setopt_array($curl, [
    CURLOPT_URL => "https://lambda-face-recognition.p.rapidapi.com/album_train",
    CURLOPT_POSTFIELDS => "urls=http://facevoting.xyz/gambar/user/".$gambar."&...",
    CURLOPT_HTTPHEADER => ["x-rapidapi-key: 932571abf0msh45..."],
]);
$response = curl_exec($curl);
```

**Laravel `app/Services/LambdaService.php`:**
```php
class LambdaService
{
    private string $apiKey;
    private string $host = 'lambda-face-recognition.p.rapidapi.com';

    public function __construct()
    {
        $this->apiKey = config('services.lambda.key');  // from .env
    }

    public function trainPhoto(string $albumName, string $albumKey, string $entryId, string $imageUrl): array
    {
        $response = Http::withHeaders([
            'x-rapidapi-host' => $this->host,
            'x-rapidapi-key'  => $this->apiKey,
        ])->asForm()->post("https://{$this->host}/album_train", [
            'urls'     => $imageUrl,
            'album'    => $albumName,
            'albumkey' => $albumKey,
            'entryid'  => $entryId,
        ]);

        return $response->json();
    }

    public function viewAlbum(string $albumName, string $albumKey): array
    {
        $response = Http::withHeaders([
            'x-rapidapi-host' => $this->host,
            'x-rapidapi-key'  => $this->apiKey,
        ])->get("https://{$this->host}/album", [
            'album'    => $albumName,
            'albumkey' => $albumKey,
        ]);

        return $response->json();
    }

    public function rebuildAlbum(string $albumName, string $albumKey): array
    {
        $response = Http::withHeaders([
            'x-rapidapi-host' => $this->host,
            'x-rapidapi-key'  => $this->apiKey,
        ])->get("https://{$this->host}/album_rebuild", [
            'album'    => $albumName,
            'albumkey' => $albumKey,
        ]);

        return $response->json();
    }
}
```

#### FaceXAPI → `FaceMatchService`

**Laravel `app/Services/FaceMatchService.php`:**
```php
class FaceMatchService
{
    private string $userId;

    public function __construct()
    {
        $this->userId = config('services.facex.user_id');
    }

    public function compare(string $trainingImageUrl, string $verifyImageUrl): ?string
    {
        $response = Http::withHeaders([
            'User_id' => $this->userId,
        ])->post('https://www.facexapi.com/match_faces', [
            'img_1' => $trainingImageUrl,
            'img_2' => $verifyImageUrl,
        ]);

        if ($response->failed()) {
            return null;
        }

        return $response->json('data.status');  // "matched" | "unmatched"
    }
}
```

#### Service Configuration (`.env`)

```dotenv
LAMBDA_API_KEY=932571abf0msh45cf0f3cef74aacp19e151jsn33e9949a1974
LAMBDA_ALBUM_NAME=Facevoting2021
LAMBDA_ALBUM_KEY=859d15e7156ef8128f921671b6a3d941a4a7f686b4e762bbc679c4809bdebb19
FACEX_USER_ID=603f05b94e6c5e6c15c171e7
```

```php
// config/services.php
'lambda' => [
    'key'        => env('LAMBDA_API_KEY'),
    'album_name' => env('LAMBDA_ALBUM_NAME'),
    'album_key'  => env('LAMBDA_ALBUM_KEY'),
],
'facex' => [
    'user_id' => env('FACEX_USER_ID'),
],
```

#### Service Binding

```php
// app/Providers/AppServiceProvider.php
$this->app->singleton(LambdaService::class);
$this->app->singleton(FaceMatchService::class);
```

#### Service Injection in Controllers

```php
// Instead of inline cURL in Photo controller:
class PhotoController extends Controller
{
    public function __construct(private LambdaService $lambda) {}

    public function train(Photo $photo): RedirectResponse
    {
        $result = $this->lambda->trainPhoto(
            albumName: config('services.lambda.album_name'),
            albumKey:  config('services.lambda.album_key'),
            entryId:   $photo->entry_id,
            imageUrl:  Storage::disk('public')->url('images/users/' . $photo->gambar),
        );

        if (!$result) {
            return back()->with('error', 'Perekaman data Gagal!');
        }

        $photo->update(['status_train' => 1]);
        return back()->with('success', "Berhasil, {$result['image_count']} gambar tersimpan.");
    }
}
```

---

## 17. Configuration

### CI3 `application/config/` → Laravel `.env` + `config/`

| CI3 Config File | CI3 Key | Laravel Equivalent |
|---|---|---|
| `config.php` | `$config['base_url']` | `APP_URL` in `.env` |
| `config.php` | `$config['encryption_key'] = 'mrasT'` | `APP_KEY=base64:...` (32 bytes, generated) |
| `config.php` | `$config['sess_expiration'] = 7200` | `SESSION_LIFETIME=120` (minutes) |
| `config.php` | `$config['sess_driver'] = 'files'` | `SESSION_DRIVER=file` |
| `config.php` | `$config['composer_autoload']` | Standard Laravel Composer autoload |
| `database.php` | `$db['default']['hostname']` | `DB_HOST` in `.env` |
| `database.php` | `$db['default']['username']` | `DB_USERNAME` in `.env` |
| `database.php` | `$db['default']['password']` | `DB_PASSWORD` in `.env` |
| `database.php` | `$db['default']['database']` | `DB_DATABASE` in `.env` |
| `database.php` | `$db['default']['dbdriver'] = 'mysqli'` | `DB_CONNECTION=mysql` |
| `database.php` | `$db['default']['char_set'] = 'utf8'` | `DB_CHARSET=utf8mb4` (upgrade charset) |
| `autoload.php` | `$autoload['libraries']` | Service Providers in `bootstrap/providers.php` |
| `autoload.php` | `$autoload['helper']` | `composer.json` autoload files |
| `autoload.php` | `$autoload['model']` | Dependency injection |
| `rest.php` | `$config['rest_default_format'] = 'json'` | Default for `response()->json()` |
| `rest.php` | `$config['rest_auth'] = false` | Sanctum config (`config/sanctum.php`) |
| `rest.php` | `$config['force_https'] = false` | `FORCE_HTTPS=true` via middleware |
| `hooks.php` | empty | Middleware stack / Service Providers |

---

## 18. External HTTP Calls → Laravel Http Client

### CI3 cURL → Laravel `Http` Facade

| CI3 cURL | Laravel Http |
|---|---|
| `$curl = curl_init()` | `Http::post(url, data)` |
| `curl_setopt(CURLOPT_URL, ...)` | First arg to `Http::get/post()` |
| `curl_setopt(CURLOPT_POSTFIELDS, ...)` | Second arg (array) |
| `curl_setopt(CURLOPT_HTTPHEADER, [...])` | `Http::withHeaders([...])` |
| `curl_setopt(CURLOPT_RETURNTRANSFER, true)` | Automatic |
| `curl_setopt(CURLOPT_TIMEOUT, 30)` | `Http::timeout(30)` |
| `curl_exec($curl)` | `->post(...)` returns `Response` |
| `curl_error($curl)` | `$response->failed()` / `Http::throw()` |
| `curl_close($curl)` | Automatic (GC) |
| `json_decode($result)` | `$response->json()` |
| `$result->data->status` | `$response->json('data.status')` |

**CI3 cURL block (15 lines):**
```php
$curl = curl_init();
curl_setopt_array($curl, [
    CURLOPT_URL => "https://lambda.../album_train",
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 30,
    CURLOPT_CUSTOMREQUEST => "POST",
    CURLOPT_POSTFIELDS => "urls=...&album=...&albumkey=...&entryid=...",
    CURLOPT_HTTPHEADER => ["x-rapidapi-key: ...", "content-type: ..."],
]);
$response = curl_exec($curl);
$err = curl_error($curl);
curl_close($curl);
if ($err) { return null; }
return $response;
```

**Laravel Http (3 lines):**
```php
return Http::withHeaders(['x-rapidapi-key' => $this->apiKey])
           ->timeout(30)
           ->asForm()
           ->post('https://lambda.../album_train', $params)
           ->json();
```

---

## 19. API Layer → Sanctum + API Resources

### REST Response Format

**CI3 (REST Server library):**
```php
$this->response(['status' => 'berhasil', 'result' => $data], 200);
$this->response(['message' => 'error'], 404);
```

**Laravel:**
```php
return response()->json(['status' => 'berhasil', 'result' => $data]);
return response()->json(['message' => 'error'], 404);
```

### API Resources (Data Transformation)

Replace raw `result_array()` with typed API Resources:

```php
// app/Http/Resources/CandidateResource.php
class CandidateResource extends JsonResource
{
    public function toArray(Request $request): array
    {
        return [
            'id_paslon'     => $this->id_paslon,
            'judul_paslon'  => $this->judul_paslon,
            'ketua_paslon'  => $this->ketua_paslon,
            'wakil_paslon'  => $this->wakil_paslon,
            'photo1_url'    => $this->photo1_paslon
                ? asset('storage/images/' . $this->photo1_paslon)
                : null,
            'photo2_url'    => $this->photo2_paslon
                ? asset('storage/images/' . $this->photo2_paslon)
                : null,
            'perolehan'     => $this->perolehan,
            'detail'        => new CandidateDetailResource($this->whenLoaded('detail')),
        ];
    }
}

// In controller:
return CandidateResource::collection(
    Candidate::with('detail')->where('id_kategori', $request->id_kategori)->get()
);
```

### Vote Submission — Fixed Implementation

**CI3 (multiple bugs):**
```php
// No auth check, race condition on counter, wrong WHERE clause
$getPerolehan = $this->api->get_data_paslon2($idPaslon)->row()->perolehan;
$getPerolehan += 1;
$this->api->perbaharui_paslon(['perolehan' => $getPerolehan], $idPaslon);
$this->api->simpan_voting($dataVoting);
$this->api->perbaharui_data_otorisasi(['status' => 2], $idUser);  // updates ALL categories
```

**Laravel (all bugs fixed):**
```php
public function store(VoteRequest $request): JsonResponse
{
    $user     = $request->user();       // Sanctum — no id_user param needed
    $category = Category::findOrFail($request->id_kategori);
    $candidate = Candidate::where('id_paslon', $request->id_paslon)
                          ->where('id_kategori', $category->id_kategori)
                          ->firstOrFail();

    // Check authorization and prevent double-vote atomically
    $authorization = VoterAuthorization::where('id_user', $user->id_user)
                                       ->where('id_kategori', $category->id_kategori)
                                       ->where('status', 1)
                                       ->lockForUpdate()     // pessimistic lock
                                       ->first();

    if (!$authorization) {
        return response()->json(['message' => 'Tidak memiliki hak suara atau sudah voting'], 403);
    }

    if ($category->status_kategori !== 2) {
        return response()->json(['message' => 'Pemilu belum dibuka'], 422);
    }

    DB::transaction(function () use ($user, $category, $candidate, $authorization) {
        $candidate->increment('perolehan');     // atomic DB increment

        Vote::create([
            'id_user'       => $user->id_user,
            'id_kategori'   => $category->id_kategori,
            'id_paslon'     => $candidate->id_paslon,
            'tanggal_voting' => now(),
        ]);

        $authorization->update(['status' => 2]);   // only THIS category
    });

    return response()->json(['status' => 'berhasil', 'message' => 'Berhasil Voting']);
}
```

---

## 20. Error Handling

### CI3 Error Pages → Laravel Exception Handler

| CI3 | Laravel 12 |
|---|---|
| `application/views/errors/html/error_404.php` | `resources/views/errors/404.blade.php` |
| `application/views/errors/html/error_500.php` | `resources/views/errors/500.blade.php` |
| `show_error()` | `abort(500, 'message')` |
| `show_404()` | `abort(404)` |
| CI3 database error display (debug=true) | `APP_DEBUG=false` in production |
| `->row()` on empty result → silent null | Eloquent `->firstOrFail()` → 404 exception |
| `->row()->token` on empty → fatal error | `->first()?->token` null-safe or `firstOrFail()` |

**Laravel exception handler customization:**
```php
// bootstrap/app.php
->withExceptions(function (Exceptions $exceptions) {
    $exceptions->render(function (ModelNotFoundException $e, Request $request) {
        if ($request->is('api/*')) {
            return response()->json(['message' => 'Data tidak ditemukan'], 404);
        }
    });
})
```

---

## 21. Testing

### CI3 (no tests) → Laravel Pest

```bash
composer require pestphp/pest --dev
php artisan pest:install
```

### Test Structure

```
tests/
  Feature/
    Admin/
      AdminLoginTest.php
      VoterManagementTest.php
      CategoryManagementTest.php
      CandidateManagementTest.php
      PhotoManagementTest.php
    Api/
      RegistrationTest.php
      AuthenticationTest.php
      VotingTest.php
      FaceVerificationTest.php
  Unit/
    Services/
      LambdaServiceTest.php
      FaceMatchServiceTest.php
    Models/
      VoterAuthorizationTest.php
      VoteTest.php
```

### Example Feature Test

```php
// tests/Feature/Api/VotingTest.php
it('prevents double voting in the same category', function () {
    $user     = User::factory()->create(['status' => 1]);
    $category = Category::factory()->create(['status_kategori' => 2]);
    $candidate = Candidate::factory()->for($category)->create();
    VoterAuthorization::factory()->create([
        'id_user'     => $user->id_user,
        'id_kategori' => $category->id_kategori,
        'status'      => 2,  // already voted
    ]);

    $token = $user->createToken('test')->plainTextToken;

    $response = $this->withToken($token)->postJson('/api/votes', [
        'id_kategori' => $category->id_kategori,
        'id_paslon'   => $candidate->id_paslon,
    ]);

    $response->assertStatus(403);
});
```

---

## 22. Migration Phases

### Phase 1 — Foundation (Week 1–2)

| Task | Detail |
|---|---|
| New Laravel 12 project | `laravel new facevoting` |
| Configure `.env` | DB, mail, storage, API keys |
| Write all migrations | From `facevoting.sql` with schema fixes applied |
| Seed database | Port seed data from SQL INSERT statements |
| Create all Eloquent models | With relationships and `$fillable` |
| Install Sanctum | `php artisan install:api` |
| Install AdminLTE | `composer require jeroennoten/laravel-adminlte` |

### Phase 2 — Admin Authentication (Week 2)

| Task | Detail |
|---|---|
| Admin guard setup | `config/auth.php` with `admins` provider |
| `AdminLoginController` | Login, logout, CAPTCHA |
| `AdminAuthenticated` middleware | Replaces constructor token checks |
| `ChangePasswordRequest` | With correct validation rules |
| Admin login Blade view | Port from `v_login.php` |

### Phase 3 — Admin CRUD Features (Week 3–4)

| Task | Detail |
|---|---|
| `VoterController` | index, activate, destroy |
| `CategoryController` | index, create, store, show, open, close, addVoter |
| `CandidateController` | index, create, store, destroy |
| `PhotoController` | index, train, destroy, viewAlbum, rebuild |
| `ResultController` | index |
| `SettingController` | edit, update |
| `AlbumController` | index |
| All Form Requests | StoreCategoryRequest, StoreCandidateRequest, etc. |
| All Blade views | Port from CI3 views to Blade |
| File upload handling | Storage disk configuration |

### Phase 4 — Service Layer (Week 4)

| Task | Detail |
|---|---|
| `LambdaService` | Train, view, rebuild (Http client) |
| `FaceMatchService` | Compare faces (Http client) |
| Service Provider bindings | `AppServiceProvider` |
| Move API keys to `.env` | Remove hardcoded keys from code |
| Fix base URL for external calls | Use `Storage::url()` not hardcoded domain |

### Phase 5 — Mobile API (Week 5–6)

| Task | Detail |
|---|---|
| Sanctum installation | `php artisan install:api` |
| `AuthController` | register, login, refresh, status |
| `AccountController` | show, update |
| `CategoryController` (API) | index (open), mine (authorized) |
| `CandidateController` (API) | index, show |
| `PhotoController` (API) | enroll, verify |
| `VoteController` (API) | store (fixed), history, results |
| All API Form Requests | RegisterRequest, VoteRequest, etc. |
| API Resources | CandidateResource, CategoryResource, VoteResource |

### Phase 6 — Bug Fixes (Week 6)

| Bug | Fix |
|---|---|
| SHA1 passwords | Migration + re-hash on login |
| Double authorization | Unique constraint migration |
| `perbaharui_data_otorisasi` wrong WHERE | Add `id_kategori` to WHERE clause |
| Race condition on `perolehan` | `increment()` + `DB::transaction()` |
| No server-side auth on vote | Sanctum + authorization check |
| `status_train` inconsistency | Standardize to 0/1 |
| Missing token validation on all API endpoints | Sanctum middleware on all protected routes |
| Album `id_user` JOIN bug | Fix or remove `get_data_album()` |

### Phase 7 — Testing & Cutover (Week 7–8)

| Task | Detail |
|---|---|
| Feature tests for all controllers | Pest |
| Unit tests for services | Mock Http calls |
| Load testing vote submission | Ensure atomic increment works |
| Migrate file assets | `gambar/` → `storage/app/public/images/` |
| Migrate existing passwords | Mark SHA1 users for re-hash |
| DNS / deployment cutover | Blue-green or maintenance window |

---

## 23. Bugs to Fix During Migration

These CI3 bugs must be corrected in the Laravel implementation — do not port them as-is.

| # | Location | Bug | Laravel Fix |
|---|---|---|---|
| 1 | `Login::index()` | Token is `sha1(datetime)` — same token if two logins in same second | Use `Auth::attempt()` — no custom token |
| 2 | `Member::logout()` | Token row not deleted from DB | `Auth::logout()` handles everything |
| 3 | `Admin_m::update_admin()` | `WHERE id_admin = 1` hardcoded | `Auth::guard('admin')->user()->update()` |
| 4 | `Admin_m::hapus_token(1)` | Hardcoded admin ID on delete | Replaced by Laravel Auth session |
| 5 | `Kategori::tambah_pemilih()` | No duplicate authorization check | Unique constraint + `firstOrCreate()` |
| 6 | `Album::get_data_album()` | JOINs on `album.id_user` which doesn't exist | Fix JOIN or remove (album has no id_user) |
| 7 | `Photo::train_album()` | Hardcoded `http://facevoting.xyz` | `Storage::disk('public')->url(...)` |
| 8 | `Gambar::_banding()` | Hardcoded `http://facevoting.xyz` | `Storage::disk('public')->url(...)` |
| 9 | `Gambar::tambah_post()` | `status_train = 2` (undefined state) | Set to `0` (untrained) on upload |
| 10 | `api/Login::sudahlogin_post()` | Token refresh requires only `id_user` — no auth | Require `auth:sanctum` middleware |
| 11 | `api/Login::cekstatus_get()` | Returns `token_login` unauthenticated | Remove token from this response |
| 12 | `api/Suara::vote_post()` | No authorization check before voting | Sanctum + check `otorisasi_pemilih` |
| 13 | `api/Suara::vote_post()` | `perbaharui_data_otorisasi` WHERE id_user only | Add `AND id_kategori = ?` |
| 14 | `api/Suara::vote_post()` | Race condition on `perolehan` counter | `Candidate::increment('perolehan')` |
| 15 | `api/Gambar::cek_post()` | `pencocokan.score` saved as empty string | Save result after `_banding()` completes |
| 16 | `Hasil::index()` | Token mismatch doesn't call `_unlogin()` | `auth:admin` middleware on route |
| 17 | `Setting::index()` | `min_length[4]` but message says "minimal 6" | Fix message to say 4 |
| 18 | All API controllers | No `token_login` validation on any endpoint | `auth:sanctum` middleware on all protected routes |
| 19 | `user` table | No UNIQUE on `email` | Migration adds `->unique()` |
| 20 | `otorisasi_pemilih` | No UNIQUE on `(id_user, id_kategori)` | Migration adds composite unique |
| 21 | `voting` | No UNIQUE on `(id_user, id_kategori)` | Migration adds composite unique |
| 22 | `user.password` | SHA1 hashed (broken algorithm) | Bcrypt via `Hash::make()` + migration strategy |
