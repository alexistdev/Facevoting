# TASK-03 — Database Seeders

**Phase**: 1 — Foundation  
**Depends on**: TASK-02  
**Estimated effort**: 1 hour

---

## Description

Create seeders that reproduce all seed rows from `facevoting.sql` exactly. After running `php artisan db:seed`, every table must contain the same rows as the original dump, with the same primary key IDs.

---

## Affected Files

### Create
- `database/seeders/AdminSeeder.php`
- `database/seeders/AlbumSeeder.php`
- `database/seeders/UserSeeder.php`
- `database/seeders/DetailUserSeeder.php`
- `database/seeders/KategoriSeeder.php`
- `database/seeders/PaslonSeeder.php`
- `database/seeders/DetailPaslonSeeder.php`
- `database/seeders/OtorisasiPemilihSeeder.php`
- `database/seeders/TbjurusanSeeder.php`

### Modify
- `database/seeders/DatabaseSeeder.php` — call all seeders in dependency order

---

## Implementation Notes

Use `DB::table()->insert()` instead of model `create()` to bypass timestamps and control exact column values.

### Seed Data (verbatim from `facevoting.sql`)

#### `admin` (1 row)
```php
DB::table('admin')->insert([
    'id_admin'  => 1,
    'username'  => 'admin',
    'password'  => '$2y$10$lqkCunzVQwEvp7WPZWuQlOLHTDiq1JQ9GpyTNfMaW3bFwaAerLEAW',
    'status'    => 1,
]);
```

#### `album` (1 row — id_album must be 3)
```php
DB::table('album')->insert([
    'id_album'   => 3,
    'nama_album' => 'Facevoting2021',
    'kode_album' => '859d15e7156ef8128f921671b6a3d941a4a7f686b4e762bbc679c4809bdebb19',
]);
```

#### `user` (1 row — id_user must be 10)
```php
DB::table('user')->insert([
    'id_user'       => 10,
    'email'         => 'alexistdev@gmail.com',
    'password'      => '325339',
    'token_firebase' => 'a1231231',
    'token_login'   => 'ada1231',
    'entry_id'      => '1231231',
    'status'        => 1,
]);
```

#### `detail_user` (1 row — id_detail must be 10)
```php
DB::table('detail_user')->insert([
    'id_detail'  => 10,
    'id_user'    => 10,
    'nama'       => 'Alexsander Hendra Wijaya',
    'identitas'  => '123456',
]);
```

#### `kategori` (3 rows — IDs 8, 9, 10)
```php
DB::table('kategori')->insert([
    ['id_kategori' => 8,  'nama_kategori' => 'BEM',       'logo_kategori' => 'bem_logo.png',    'status_kategori' => 2],
    ['id_kategori' => 9,  'nama_kategori' => 'HIMKRIS',   'logo_kategori' => 'himkris_logo.png','status_kategori' => 2],
    ['id_kategori' => 10, 'nama_kategori' => 'UKM MUSIC', 'logo_kategori' => 'music_logo.png',  'status_kategori' => 1],
]);
```

> Use exact logo filenames from `facevoting.sql`. The above are placeholders — check the actual INSERT statements in the SQL dump.

#### `paslon` (3 rows — IDs 1, 2, 3)
Reproduce from `facevoting.sql` INSERT statements verbatim, including `perolehan` values.

#### `detail_paslon` (3 rows — IDs 2, 3, 4)
Reproduce from `facevoting.sql` INSERT statements verbatim.

#### `otorisasi_pemilih` (2 rows)
```php
DB::table('otorisasi_pemilih')->insert([
    ['id_user' => 10, 'id_kategori' => 8,  'status' => 1],
    ['id_user' => 10, 'id_kategori' => 9,  'status' => 1],
]);
```

#### `tbjurusan` (2 rows)
Reproduce from `facevoting.sql` INSERT statements verbatim.

### DatabaseSeeder call order
```php
$this->call([
    AdminSeeder::class,
    AlbumSeeder::class,
    UserSeeder::class,
    DetailUserSeeder::class,
    KategoriSeeder::class,
    PaslonSeeder::class,
    DetailPaslonSeeder::class,
    OtorisasiPemilihSeeder::class,
    TbjurusanSeeder::class,
]);
```

---

## Acceptance Criteria

- [ ] `php artisan db:seed` runs without error
- [ ] `SELECT id_admin, username FROM admin` returns 1 row: `{1, 'admin'}`
- [ ] `SELECT id_album FROM album` returns exactly `3`
- [ ] `SELECT id_user, email FROM user` returns 1 row: `{10, 'alexistdev@gmail.com'}`
- [ ] `SELECT id_detail, id_user FROM detail_user` returns `{10, 10}`
- [ ] `SELECT COUNT(*) FROM kategori` returns `3`
- [ ] `SELECT id_kategori FROM kategori` contains `8`, `9`, `10`
- [ ] `SELECT status_kategori FROM kategori WHERE id_kategori = 8` returns `2`
- [ ] `SELECT status_kategori FROM kategori WHERE id_kategori = 10` returns `1`
- [ ] `SELECT COUNT(*) FROM paslon` returns `3`
- [ ] `SELECT COUNT(*) FROM detail_paslon` returns `3`
- [ ] `SELECT COUNT(*) FROM otorisasi_pemilih` returns `2`
- [ ] Both `otorisasi_pemilih` rows have `status = 1`
- [ ] `SELECT COUNT(*) FROM tbjurusan` returns `2`
- [ ] `php artisan migrate:fresh --seed` completes without error
