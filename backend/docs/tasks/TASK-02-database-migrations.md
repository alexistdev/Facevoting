# TASK-02 — Database Migrations

**Phase**: 1 — Foundation  
**Depends on**: TASK-01  
**Estimated effort**: 3 hours

---

## Description

Write Laravel migration files for all 13 tables from `facevoting.sql`. Column names, types, sizes, nullability, defaults, indexes, and foreign key constraints must match the source schema exactly. AUTO_INCREMENT starting values must be set to match the SQL dump.

---

## Affected Files

### Create
- `database/migrations/2024_01_01_000001_create_admin_table.php`
- `database/migrations/2024_01_01_000002_create_token_table.php`
- `database/migrations/2024_01_01_000003_create_users_table.php`
- `database/migrations/2024_01_01_000004_create_detail_user_table.php`
- `database/migrations/2024_01_01_000005_create_albums_table.php`
- `database/migrations/2024_01_01_000006_create_photos_table.php`
- `database/migrations/2024_01_01_000007_create_pencocokan_table.php`
- `database/migrations/2024_01_01_000008_create_kategori_table.php`
- `database/migrations/2024_01_01_000009_create_paslon_table.php`
- `database/migrations/2024_01_01_000010_create_detail_paslon_table.php`
- `database/migrations/2024_01_01_000011_create_otorisasi_pemilih_table.php`
- `database/migrations/2024_01_01_000012_create_voting_table.php`
- `database/migrations/2024_01_01_000013_create_tbjurusan_table.php`
- `database/migrations/2024_01_01_000014_set_auto_increment_values.php`

---

## Implementation Notes

### Column Specifications (source: `facevoting.sql`)

#### `admin`
```php
$table->integer('id_admin')->autoIncrement();
$table->string('username', 30);
$table->string('password', 255);
$table->integer('status');
```

#### `token`
```php
$table->integer('id_token')->autoIncrement();
$table->integer('id_admin');
$table->string('token', 100);
$table->integer('time');
$table->foreign('id_admin')->references('id_admin')->on('admin')->cascadeOnDelete()->name('token_ibfk_1');
```

#### `user`
```php
$table->integer('id_user')->autoIncrement();
$table->string('email', 100);
$table->string('password', 100);
$table->string('token_firebase', 255)->nullable();
$table->string('token_login', 100)->nullable();
$table->string('entry_id', 255);
$table->tinyInteger('status');
```

#### `detail_user`
```php
$table->integer('id_detail')->autoIncrement();
$table->integer('id_user');
$table->string('nama', 100);
$table->string('identitas', 50);
$table->index('id_user');
$table->foreign('id_user')->references('id_user')->on('user')->cascadeOnDelete()->name('detail_user_ibfk_1');
```

#### `album`
```php
$table->integer('id_album')->autoIncrement();
$table->string('nama_album', 255);
$table->string('kode_album', 255);
```

#### `photo`
```php
$table->integer('id_photo')->autoIncrement();
$table->integer('id_album');           // NO FK constraint — intentional
$table->integer('id_user');
$table->string('entry_id', 100);
$table->string('nama_photo', 100);
$table->string('gambar', 128);
$table->integer('status_train');
$table->index('id_user');
$table->foreign('id_user')->references('id_user')->on('user')->cascadeOnDelete()->name('photo_ibfk_1');
// id_album has NO foreign key — do not add one
```

#### `pencocokan`
```php
$table->integer('id_pencocokan')->autoIncrement();
$table->integer('id_user');            // NO FK constraint — intentional
$table->string('nama_photo', 100);
$table->string('gambar', 128);
$table->string('score', 10);
// id_user has NO foreign key — do not add one
```

#### `kategori`
```php
$table->integer('id_kategori')->autoIncrement();
$table->string('nama_kategori', 80);
$table->string('logo_kategori', 100);
$table->integer('status_kategori');
```

#### `paslon`
```php
$table->integer('id_paslon')->autoIncrement();
$table->integer('id_kategori');
$table->string('judul_paslon', 20);
$table->string('ketua_paslon', 50);
$table->string('wakil_paslon', 50);
$table->string('photo1_paslon', 100)->nullable();
$table->string('photo2_paslon', 100)->nullable();
$table->integer('perolehan')->nullable();
$table->index('id_kategori');
$table->foreign('id_kategori')->references('id_kategori')->on('kategori')
      ->restrictOnDelete()->restrictOnUpdate()->name('paslon_ibfk_1');
```

#### `detail_paslon`
```php
$table->integer('id_detailpaslon')->autoIncrement();
$table->integer('id_paslon');
$table->text('visi_misi');
$table->text('profil_catum');
$table->text('profil_cawatum');
$table->index('id_paslon');
$table->foreign('id_paslon')->references('id_paslon')->on('paslon')->cascadeOnDelete()->name('detail_paslon_ibfk_1');
```

#### `otorisasi_pemilih`
```php
$table->integer('id_otorisasi')->autoIncrement();
$table->integer('id_user')->nullable();
$table->integer('id_kategori')->nullable();
$table->integer('status')->nullable();
$table->index('id_user');
$table->index('id_kategori');
$table->foreign('id_user')->references('id_user')->on('user')->cascadeOnDelete()->name('otorisasi_pemilih_ibfk_1');
$table->foreign('id_kategori')->references('id_kategori')->on('kategori')->cascadeOnDelete()->name('otorisasi_pemilih_ibfk_2');
```

#### `voting`
```php
$table->integer('id_voting')->autoIncrement();
$table->integer('id_user');
$table->integer('id_kategori');
$table->integer('id_paslon');
$table->dateTime('tanggal_voting')->nullable();
$table->index('id_user');
$table->index('id_kategori');
$table->index('id_paslon');
$table->foreign('id_user')->references('id_user')->on('user')->cascadeOnDelete()->name('voting_ibfk_1');
$table->foreign('id_kategori')->references('id_kategori')->on('kategori')->cascadeOnDelete()->name('voting_ibfk_2');
$table->foreign('id_paslon')->references('id_paslon')->on('paslon')->cascadeOnDelete()->name('voting_ibfk_3');
```

#### `tbjurusan`
```php
$table->integer('id_jurusan')->autoIncrement()->primary();
$table->string('kode_jurusan', 50);
$table->string('nama_jurusan', 100);
```

### AUTO_INCREMENT Reset Migration

Use raw DB statements to set starting values:
```php
DB::statement('ALTER TABLE admin AUTO_INCREMENT = 2');
DB::statement('ALTER TABLE album AUTO_INCREMENT = 4');
DB::statement('ALTER TABLE detail_paslon AUTO_INCREMENT = 7');
DB::statement('ALTER TABLE detail_user AUTO_INCREMENT = 12');
DB::statement('ALTER TABLE kategori AUTO_INCREMENT = 11');
DB::statement('ALTER TABLE otorisasi_pemilih AUTO_INCREMENT = 11');
DB::statement('ALTER TABLE paslon AUTO_INCREMENT = 6');
DB::statement('ALTER TABLE pencocokan AUTO_INCREMENT = 4');
DB::statement('ALTER TABLE photo AUTO_INCREMENT = 32');
DB::statement('ALTER TABLE tbjurusan AUTO_INCREMENT = 3');
DB::statement('ALTER TABLE token AUTO_INCREMENT = 26');
DB::statement('ALTER TABLE user AUTO_INCREMENT = 12');
DB::statement('ALTER TABLE voting AUTO_INCREMENT = 13');
```

### Engine & Charset

Every table must use:
```php
$table->engine = 'InnoDB';
$table->charset = 'utf8mb4';
```

---

## Acceptance Criteria

- [ ] `php artisan migrate` runs to completion with zero errors
- [ ] All 13 tables exist in the `evoting` database after migration
- [ ] `photo.id_album` has no FK constraint (verify via `SHOW CREATE TABLE photo`)
- [ ] `pencocokan.id_user` has no FK constraint (verify via `SHOW CREATE TABLE pencocokan`)
- [ ] `paslon_ibfk_1` is RESTRICT on delete and update (verify via information_schema)
- [ ] All other FK constraints are CASCADE on delete (verify via information_schema)
- [ ] `SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_NAME = 'user'` returns `12`
- [ ] `SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_NAME = 'album'` returns `4`
- [ ] `SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_NAME = 'photo'` returns `32`
- [ ] `otorisasi_pemilih.id_user` and `id_kategori` are nullable (match source)
- [ ] `user.password` is varchar(100) — not 255 (parity)
- [ ] `php artisan migrate:rollback` runs without error
