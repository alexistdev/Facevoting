# TASK-26 — Admin Panel Feature Tests

**Phase**: 7 — Testing  
**Depends on**: TASK-06 through TASK-15  
**Estimated effort**: 4 hours

---

## Description

Write Pest feature tests for all admin panel functionality. Each test verifies a specific acceptance criterion from the PRD. Tests use a real SQLite or MySQL test database (no mocking the database). External HTTP calls (Lambda) are faked via `Http::fake()`.

---

## Affected Files

### Create
- `tests/Feature/Admin/AdminLoginTest.php`
- `tests/Feature/Admin/VoterManagementTest.php`
- `tests/Feature/Admin/CategoryManagementTest.php`
- `tests/Feature/Admin/CandidateManagementTest.php`
- `tests/Feature/Admin/PhotoManagementTest.php`
- `tests/Feature/Admin/SettingsTest.php`
- `tests/Feature/Admin/ResultsTest.php`

---

## Implementation Notes

### Setup

```php
uses(RefreshDatabase::class);

beforeEach(function () {
    $this->seed(DatabaseSeeder::class);
    // Log in as admin
    $this->actingAs(Admin::find(1), 'admin');
});
```

### `tests/Feature/Admin/AdminLoginTest.php`

```php
it('redirects root to login', function () {
    $this->get('/')->assertRedirect('/login');
});

it('shows login form with captcha', function () {
    $this->get('/login')->assertStatus(200)->assertSee('captcha');
});

it('redirects authenticated admin to /Member', function () {
    $this->actingAs(Admin::find(1), 'admin')
         ->get('/login')
         ->assertRedirect('/Member');
});

it('rejects missing username', function () {
    $this->post('/login', ['password' => 'x', 'captcha' => 'x'])
         ->assertSessionHas('pesan');
});

it('rejects wrong password with exact danger flash', function () {
    session(['captchaword' => 'VALID']);
    $this->post('/login', [
        'username' => 'admin',
        'password' => 'wrongpassword',
        'captcha'  => 'VALID',
    ])->assertSessionHas('pesan2',
        '<div class="alert alert-danger" role="alert">Username atau password anda salah!</div>'
    )->assertRedirect('/Login');
});

it('logs in successfully and creates token row', function () {
    session(['captchaword' => 'VALID']);
    $this->post('/login', [
        'username' => 'admin',
        'password' => 'admin',       // seeded bcrypt hash
        'captcha'  => 'VALID',
    ])->assertRedirect('/Member');

    $this->assertDatabaseHas('token', ['id_admin' => 1]);
});

it('logout destroys session but does not delete token row', function () {
    DB::table('token')->insert(['id_admin' => 1, 'token' => 'abc', 'time' => time()]);

    $this->get('/member/logout')->assertRedirect('/Login');

    $this->assertDatabaseHas('token', ['id_admin' => 1, 'token' => 'abc']);
});
```

### `tests/Feature/Admin/VoterManagementTest.php`

```php
it('lists all voters', function () {
    $this->get('/user')->assertStatus(200)
         ->assertSee('Alexsander Hendra Wijaya');
});

it('activates a pending voter', function () {
    // Create pending voter
    $user = User::factory()->create(['status' => 2]);

    $this->get("/user/aktivasi/{$user->id_user}")
         ->assertRedirect('/User')
         ->assertSessionHas('pesan2',
             '<div class="alert alert-success" role="alert">User telah berhasil diaktifkan!</div>');

    $this->assertDatabaseHas('user', ['id_user' => $user->id_user, 'status' => 1]);
});

it('silently redirects when activating active voter', function () {
    $this->get('/user/aktivasi/10')    // seeded user has status=1
         ->assertRedirect('/User')
         ->assertSessionMissing('pesan2');
});

it('deletes voter and cascades', function () {
    $user = User::factory()->has(UserDetail::factory())->create();

    $this->get("/user/hapus/{$user->id_user}")
         ->assertRedirect('/User')
         ->assertSessionHas('pesan',
             '<div class="alert alert-danger" role="alert">User telah dihapus!</div>');

    $this->assertDatabaseMissing('user', ['id_user' => $user->id_user]);
    $this->assertDatabaseMissing('detail_user', ['id_user' => $user->id_user]);
});
```

### `tests/Feature/Admin/CategoryManagementTest.php`

```php
it('creates category with logo', function () {
    $file = UploadedFile::fake()->image('logo.jpg', 100, 100);

    $this->post('/kategori/tambah', [
        'namaKategori' => 'Test Kategori',
        'logo'         => $file,
    ])->assertRedirect('/Kategori');

    $this->assertDatabaseHas('kategori', [
        'nama_kategori'   => 'Test Kategori',
        'status_kategori' => 1,
    ]);
});

it('rejects missing category name without uploading', function () {
    Storage::fake('public');
    $file = UploadedFile::fake()->image('logo.jpg');

    $this->post('/kategori/tambah', ['logo' => $file])
         ->assertSessionHas('message1');

    Storage::disk('public')->assertNothingStored();
});

it('allows duplicate voter authorizations', function () {
    $this->get('/kategori/tambah_pemilih/8/10');
    $this->get('/kategori/tambah_pemilih/8/10');

    $this->assertEquals(2, VoterAuthorization::where('id_user', 10)->where('id_kategori', 8)->count());
});

it('opens election with correct flash and status', function () {
    $this->get('/kategori/Buka/10')
         ->assertRedirect('/Kategori')
         ->assertSessionHas('message2',
             '<div class="alert alert-success" role="alert">Pemilu saat ini telah dibuka!</div>');

    $this->assertDatabaseHas('kategori', ['id_kategori' => 10, 'status_kategori' => 2]);
});

it('closes election with red flash', function () {
    $this->get('/kategori/Tutup/8')
         ->assertRedirect('/Kategori')
         ->assertSessionHas('message2',
             '<div class="alert alert-danger" role="alert">Pemilu saat ini telah ditutup!</div>');

    $this->assertDatabaseHas('kategori', ['id_kategori' => 8, 'status_kategori' => 1]);
});
```

### `tests/Feature/Admin/PhotoManagementTest.php`

```php
it('trains photo — calls Lambda and shows success flash', function () {
    Http::fake([
        'lambda-face-recognition.p.rapidapi.com/*' => Http::response([
            'album' => 'Facevoting2021', 'image_count' => 3,
        ], 200),
    ]);

    // Create a photo record
    $photo = Photo::factory()->create(['id_album' => 3, 'id_user' => 10]);

    $this->get("/photo/rekam/{$photo->id_photo}")
         ->assertRedirect('/Photo')
         ->assertSessionHas('pesan');
});

it('deletes photo and removes file from storage', function () {
    Storage::fake('public');
    $file = UploadedFile::fake()->image('test.jpg');
    Storage::disk('public')->putFileAs('images/users', $file, 'test.jpg');

    $photo = Photo::factory()->create(['gambar' => 'test.jpg', 'id_album' => 3]);

    $this->get("/photo/hapus/{$photo->id_photo}")
         ->assertRedirect('/Photo');

    Storage::disk('public')->assertMissing('images/users/test.jpg');
    $this->assertDatabaseMissing('photo', ['id_photo' => $photo->id_photo]);
});
```

### `tests/Feature/Admin/SettingsTest.php`

```php
it('min 4 chars rule with message saying 6', function () {
    $this->post('/setting', ['password1' => 'abc', 'password2' => 'abc'])
         ->assertSessionHasErrors(['password1']);

    $errors = session('errors');
    expect($errors->first('password1'))->toBe('Panjang karakter Password minimal 6 karakter!');
});

it('updates password with bcrypt hash', function () {
    $this->post('/setting', ['password1' => 'newpass', 'password2' => 'newpass'])
         ->assertRedirect('/Setting');

    $admin = Admin::find(1)->fresh();
    expect(Hash::check('newpass', $admin->password))->toBeTrue();
});
```

---

## Acceptance Criteria

- [ ] All tests in all 7 files pass with `php artisan test --filter=Admin`
- [ ] Zero database test failures (no query errors, no missing column errors)
- [ ] Flash message string comparisons are exact (character-for-character)
- [ ] Redirect target case is verified: `/User`, `/Kategori`, `/Paslon`, `/Photo`, `/Member`, `/Login`, `/Setting` (capital letters match CI3)
- [ ] Http::fake() is used for all Lambda API calls — no real external HTTP in tests
- [ ] `RefreshDatabase` is used — each test starts with a clean seeded state
- [ ] No test has more than one assertion group (each test is focused on one behaviour)
