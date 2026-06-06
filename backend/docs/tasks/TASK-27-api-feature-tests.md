# TASK-27 — API Feature Tests

**Phase**: 7 — Testing  
**Depends on**: TASK-20 through TASK-25  
**Estimated effort**: 4 hours

---

## Description

Write Pest feature tests for all 15 mobile API endpoints. Tests verify exact response shapes, HTTP status codes, field names, and business logic. FaceXAPI calls are faked via `Http::fake()`.

---

## Affected Files

### Create
- `tests/Feature/Api/RegistrationTest.php`
- `tests/Feature/Api/AuthenticationTest.php`
- `tests/Feature/Api/AccountTest.php`
- `tests/Feature/Api/CategoryApiTest.php`
- `tests/Feature/Api/CandidateApiTest.php`
- `tests/Feature/Api/PhotoApiTest.php`
- `tests/Feature/Api/VotingTest.php`

---

## Implementation Notes

### Setup pattern
```php
uses(RefreshDatabase::class);
beforeEach(fn() => $this->seed(DatabaseSeeder::class));
```

### `tests/Feature/Api/RegistrationTest.php`

```php
it('registers a new voter', function () {
    $response = $this->postJson('/api/daftar/tambah', [
        'nama'       => 'Test User',
        'identitas'  => '9876543',
        'email'      => 'new@test.com',
        'password'   => 'testpass',
    ]);

    $response->assertStatus(200)
             ->assertJsonStructure(['status', 'message', 'id_user', 'validasi', 'token_login'])
             ->assertJsonFragment(['status' => 'Success', 'validasi' => 2]);

    $this->assertDatabaseHas('user', ['email' => 'new@test.com']);

    // Verify SHA1 password stored
    $user = User::where('email', 'new@test.com')->first();
    expect($user->password)->toBe(sha1('testpass'));
});

it('rejects duplicate email with 404', function () {
    $response = $this->postJson('/api/daftar/tambah', [
        'email'    => 'alexistdev@gmail.com',  // seeded
        'password' => 'x',
    ]);

    $response->assertStatus(404)
             ->assertJsonFragment([
                 'status'  => 'Failed',
                 'message' => 'Email sudah terdaftar, silahkan gunakan email yang lain!',
             ]);
});
```

### `tests/Feature/Api/AuthenticationTest.php`

```php
it('logs in with correct credentials', function () {
    $response = $this->postJson('/api/login/otentikasi', [
        'email'    => 'alexistdev@gmail.com',
        'password' => '325339',   // sha1 of this is in the seeded row: sha1('325339')
    ]);

    $response->assertStatus(200)
             ->assertJsonStructure(['token_login', 'id_user', 'nama', 'identitas', 'email', 'validasi'])
             ->assertJsonFragment(['id_user' => 10]);
});

it('rejects wrong password with 404 and exact message', function () {
    $response = $this->postJson('/api/login/otentikasi', [
        'email'    => 'alexistdev@gmail.com',
        'password' => 'wrongpassword',
    ]);

    $response->assertStatus(404)
             ->assertJsonFragment([
                 'message' => 'Username atau Password yang anda masukkan salah',
             ]);
});

it('refresh requires only id_user', function () {
    $response = $this->postJson('/api/login/sudahlogin', ['id_user' => 10]);

    $response->assertStatus(200)
             ->assertJsonCount(4)   // exactly 4 fields
             ->assertJsonStructure(['token_login', 'id_user', 'email', 'validasi'])
             ->assertJsonMissing('nama')
             ->assertJsonMissing('identitas');
});

it('status check returns token_login', function () {
    $response = $this->getJson('/api/login/cekstatus?id_user=10');

    $response->assertStatus(200)
             ->assertJsonStructure(['validasi', 'token_login', 'nama', 'identitas', 'message']);

    expect($response->json('token_login'))->not->toBeNull();
});
```

### `tests/Feature/Api/CategoryApiTest.php`

```php
it('returns authorized categories for voter', function () {
    $response = $this->getJson('/api/kategori/tampil?id_user=10');

    $response->assertStatus(200)
             ->assertJsonFragment(['status' => 'berhasil'])
             ->assertJsonPath('result.0.status', 1);   // only status=1 authorizations
});

it('returns 404 with result array when no authorizations', function () {
    $response = $this->getJson('/api/kategori/tampil?id_user=99999');

    $response->assertStatus(404)
             ->assertJsonFragment(['result' => []]);
});

it('returns all open categories', function () {
    $response = $this->getJson('/api/kategori/semua');

    $response->assertStatus(200);
    $categories = $response->json('result');

    foreach ($categories as $cat) {
        expect($cat['status_kategori'])->toBe(2);
    }
});
```

### `tests/Feature/Api/VotingTest.php`

```php
it('submits vote and updates all authorizations', function () {
    // User 10 has authorizations in kategori 8 and 9
    $response = $this->postJson('/api/suara/vote', [
        'id_user'    => 10,
        'id_kategori' => 8,
        'id_paslon'  => 1,
    ]);

    $response->assertStatus(200)
             ->assertJsonFragment(['status' => 'berhasil', 'message' => 'Berhasil Voting']);

    // perolehan incremented
    $this->assertDatabaseHas('paslon', ['id_paslon' => 1, 'perolehan' => 1]);

    // voting row inserted
    $this->assertDatabaseHas('voting', ['id_user' => 10, 'id_kategori' => 8]);

    // ALL authorizations for user 10 updated to status=2 (not just kategori 8)
    $this->assertDatabaseHas('otorisasi_pemilih', ['id_user' => 10, 'id_kategori' => 8, 'status' => 2]);
    $this->assertDatabaseHas('otorisasi_pemilih', ['id_user' => 10, 'id_kategori' => 9, 'status' => 2]);
});

it('always returns 200 even without authorization', function () {
    $response = $this->postJson('/api/suara/vote', [
        'id_user'    => 99999,  // non-existent user
        'id_kategori' => 8,
        'id_paslon'  => 1,
    ]);

    $response->assertStatus(200);
});

it('returns vote counts from paslon.perolehan not count query', function () {
    $response = $this->getJson('/api/suara/perolehan?id_kategori=8');

    $response->assertStatus(200)
             ->assertJsonFragment(['status' => 'berhasil']);

    $result = $response->json('result');
    foreach ($result as $item) {
        expect($item)->toHaveKey('perolehan');  // from paslon table
        expect($item)->not->toHaveKey('vote_count');
    }
});

it('returns 200 with empty result for unknown category', function () {
    $this->getJson('/api/suara/perolehan?id_kategori=99999')
         ->assertStatus(200)
         ->assertJsonFragment(['result' => []]);
});

it('voting history 404 message is "Silahkan relogin!"', function () {
    $this->getJson('/api/suara/tampil/10')   // user 10 has no votes
         ->assertStatus(404)
         ->assertJsonFragment(['message' => 'Silahkan relogin!']);
});
```

### `tests/Feature/Api/PhotoApiTest.php`

```php
it('enrolls training photo with status_train=2', function () {
    Storage::fake('public');
    $file = UploadedFile::fake()->image('face.jpg', 200, 200);

    $response = $this->post('/api/gambar/tambah', [
        'id_user' => 10,
        'upload'  => $file,
    ]);

    $response->assertStatus(200)
             ->assertJsonFragment(['status' => 'success']);

    $this->assertDatabaseHas('photo', [
        'id_user'     => 10,
        'id_album'    => 3,
        'status_train' => 2,
    ]);
});

it('verify returns nopic when user has no training photos', function () {
    Storage::fake('public');
    $file = UploadedFile::fake()->image('verify.jpg');

    $response = $this->post('/api/gambar/cek', [
        'id_user' => 10,
        'upload'  => $file,
    ]);

    $response->assertStatus(404)
             ->assertJsonFragment(['status' => 'nopic']);
});

it('verify calls FaceXAPI and returns match status', function () {
    Storage::fake('public');
    Http::fake([
        'facexapi.com/*' => Http::response(['data' => ['status' => 'matched']], 200),
    ]);

    // Create a training photo first
    Photo::factory()->create(['id_user' => 10, 'gambar' => 'training.jpg', 'id_album' => 3]);

    $file = UploadedFile::fake()->image('verify.jpg');
    $response = $this->post('/api/gambar/cek', [
        'id_user' => 10,
        'upload'  => $file,
    ]);

    $response->assertStatus(200)
             ->assertJsonFragment(['status' => 'success', 'message' => 'matched']);
});
```

---

## Acceptance Criteria

- [ ] All tests in all 7 files pass with `php artisan test --filter=Api`
- [ ] SHA1 password storage is verified, not just existence of the row
- [ ] Response field counts are asserted where noted (e.g. refresh returns exactly 4 fields)
- [ ] `result: []` in 404 responses is asserted (not just status code)
- [ ] The `otorisasi_pemilih` ALL-categories update behaviour is explicitly tested
- [ ] `Http::fake()` is used for all FaceXAPI calls — no real external HTTP
- [ ] `Storage::fake('public')` is used for all file upload tests
- [ ] No test relies on execution order (each is fully isolated via `RefreshDatabase`)
