# TASK-18 — File Storage Configuration

**Phase**: 4 — Service Layer  
**Depends on**: TASK-01  
**Estimated effort**: 1.5 hours

---

## Description

Configure Laravel's storage system to serve uploaded files publicly, create the helper function for filename generation, and run `storage:link` to make files accessible via the web server. This task also migrates existing uploaded files from the CI3 `gambar/` directory.

---

## Affected Files

### Create
- `app/Helpers/FacevotingHelper.php`
- `storage/app/public/images/` (directory)
- `storage/app/public/images/users/` (directory)
- `storage/app/public/captcha/` (directory)

### Modify
- `config/filesystems.php` — public disk configuration
- `composer.json` — autoload files entry

---

## Implementation Notes

### `config/filesystems.php`
```php
'disks' => [
    'local' => [
        'driver' => 'local',
        'root'   => storage_path('app/private'),
        'throw'  => false,
    ],
    'public' => [
        'driver'     => 'local',
        'root'       => storage_path('app/public'),
        'url'        => env('APP_URL') . '/storage',
        'visibility' => 'public',
        'throw'      => false,
    ],
],
```

### Storage symlink setup

Run once during deployment:
```bash
php artisan storage:link
```

This creates `public/storage` → `storage/app/public`. Uploaded files then resolve to:
- `public/storage/images/{file}` → web accessible
- `public/storage/images/users/{file}` → web accessible

### `app/Helpers/FacevotingHelper.php`
```php
<?php

if (!function_exists('generate_filename')) {
    function generate_filename(string $extension, int $offset = 0): string
    {
        return substr(sha1(time()), $offset, 10) . '.' . $extension;
    }
}
```

Callers:
- Category logo: `substr(sha1(time()), 0, 10)` → `generate_filename($ext, 0)`
- Training/verification photo: `substr(sha1(time()), 1, 10)` → `generate_filename($ext, 1)`

### `composer.json` autoload
```json
"autoload": {
    "psr-4": { "App\\": "app/" },
    "files": ["app/Helpers/FacevotingHelper.php"]
}
```
Run `composer dump-autoload` after adding.

### Existing file migration

When cutting over from CI3, copy existing uploaded files:
```bash
cp -r /path/to/ci3/gambar/       storage/app/public/images/
cp -r /path/to/ci3/gambar/user/  storage/app/public/images/users/
```

### URL for Lambda/FaceXAPI

The Lambda and FaceXAPI integrations use the hardcoded URL `http://facevoting.xyz/gambar/user/{file}` (parity requirement). Controllers must construct this URL manually — do NOT use `Storage::url()` for these specific calls.

---

## Acceptance Criteria

- [ ] `php artisan storage:link` creates `public/storage` symlink without error
- [ ] A file stored via `Storage::disk('public')->put('images/test.txt', 'data')` is accessible at `{APP_URL}/storage/images/test.txt`
- [ ] `generate_filename('jpg', 0)` returns a 14-char string matching `/^[0-9a-f]{10}\.jpg$/`
- [ ] `generate_filename('jpg', 1)` returns a 14-char string matching `/^[0-9a-f]{10}\.jpg$/`
- [ ] `Storage::disk('public')->delete('images/users/test.jpg')` deletes the physical file
- [ ] `function_exists('generate_filename')` returns true in any context (autoloaded)
- [ ] `composer dump-autoload` runs without error
- [ ] `storage/app/public/images/users/` directory is writable by the web server
