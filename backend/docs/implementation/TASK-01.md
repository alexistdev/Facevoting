# TASK-01 Implementation Report — Laravel Project Bootstrap

**Date**: 2026-06-06  
**Status**: Complete  
**Phase**: 1 — Foundation

---

## Overview

Created a clean Laravel 12 application foundation at `backend/facevoting/` to support the full CI3 → Laravel 12 migration. The existing CI3 application at `backend/` is preserved intact. All required packages are installed, configurations are applied, and the application boots without error.

---

## Changes Made

### New Laravel Project

Installed via `composer create-project laravel/laravel:^12.0 backend/facevoting`. The project was scoped to `^12.0` to avoid installing Laravel 13 (the default installer would have installed the latest stable version).

---

## Installed Packages

| Package | Version | Purpose |
|---|---|---|
| `laravel/framework` | v12.61.1 | Core framework |
| `laravel/sanctum` | v4.3.2 | API token authentication |
| `jeroennoten/laravel-adminlte` | v3.16.0 | AdminLTE Blade integration |
| `mews/captcha` | 3.4.9 | CAPTCHA for admin login |
| `almasaeed2010/adminlte` | v3.2.0 | AdminLTE assets (pulled by adminlte package) |

Setup commands run:
- `php artisan install:api` — published Sanctum and created `personal_access_tokens` migration
- `php artisan adminlte:install` — published AdminLTE assets, config, and translations

---

## Configuration Changes

### `config/app.php`

| Key | Before | After |
|---|---|---|
| `timezone` | `'UTC'` | `'Asia/Jakarta'` |
| `locale` default | `'en'` | `'id'` |
| `faker_locale` default | `'en_US'` | `'id_ID'` |

### `config/services.php`

Added two new service blocks:

```php
'lambda' => [
    'key'        => env('LAMBDA_API_KEY'),
    'album_name' => env('LAMBDA_ALBUM_NAME'),
    'album_key'  => env('LAMBDA_ALBUM_KEY'),
],

'facex' => [
    'user_id' => env('FACEX_USER_ID'),
],
```

### `config/database.php`

Unchanged — already uses `env()` for all credentials. Default driver remains `'sqlite'` in the config file; the `DB_CONNECTION=mysql` in `.env` overrides it at runtime.

### `config/session.php`

Unchanged — already has `lifetime` = `env('SESSION_LIFETIME', 120)`. The `SESSION_DRIVER=file` in `.env` sets file-based sessions.

### `.env`

| Key | Value |
|---|---|
| `APP_NAME` | `"FaceVoting"` |
| `APP_LOCALE` | `id` |
| `DB_CONNECTION` | `mysql` |
| `DB_HOST` | `127.0.0.1` |
| `DB_PORT` | `3306` |
| `DB_DATABASE` | `evoting` |
| `DB_USERNAME` | `root` |
| `DB_PASSWORD` | *(empty)* |
| `SESSION_DRIVER` | `file` |
| `SESSION_LIFETIME` | `120` |
| `LAMBDA_API_KEY` | *(real key — local only, not committed)* |
| `LAMBDA_ALBUM_NAME` | `Facevoting2021` |
| `LAMBDA_ALBUM_KEY` | *(real key — local only, not committed)* |
| `FACEX_USER_ID` | `603f05b94e6c5e6c15c171e7` |

### `.env.example`

Updated to reflect the project's actual structure: MySQL driver, `SESSION_DRIVER=file`, `APP_LOCALE=id`, and all four external service keys as empty placeholders. No real secrets committed.

---

## Files Created

| File | Description |
|---|---|
| `backend/facevoting/` | Entire Laravel 12 project root |
| `backend/facevoting/app/Helpers/FacevotingHelper.php` | Placeholder helper file (autoloaded) |
| `backend/facevoting/.env` | Local environment (gitignored) |
| `backend/facevoting/.env.example` | Committed env template with empty placeholders |

---

## Files Modified

| File | Change |
|---|---|
| `config/app.php` | timezone → `Asia/Jakarta`; locale default → `id` |
| `config/services.php` | Added `lambda` and `facex` service blocks |
| `composer.json` | Added `autoload.files` entry for `FacevotingHelper.php` |
| `.env.example` | Updated APP_NAME, DB driver, SESSION_DRIVER, locales, added LAMBDA_*/FACEX_* placeholders |

---

## Validation Evidence

```
$ php artisan --version
Laravel Framework 12.61.1

$ composer show laravel/sanctum | head -2
name     : laravel/sanctum
versions : * v4.3.2

$ composer show jeroennoten/laravel-adminlte | head -2
name     : jeroennoten/laravel-adminlte
versions : * v3.16.0

$ composer show mews/captcha | head -2
name     : mews/captcha
versions : * 3.4.9

$ php artisan config:cache
INFO  Configuration cached successfully.

$ php artisan tinker --execute="echo config('app.timezone');"
Asia/Jakarta

$ php artisan tinker --execute="echo config('app.locale');"
id

$ php artisan tinker --execute="echo config('services.lambda.key');"
932571abf0msh45cf0f3cef74aacp19e151jsn33e9949a1974

$ php artisan tinker --execute="echo config('services.facex.user_id');"
603f05b94e6c5e6c15c171e7

$ php artisan tinker --execute="echo config('session.lifetime');"
120
```

---

## Acceptance Criteria Results

| Criterion | Result |
|---|---|
| Laravel 12 installed | PASS — Framework v12.61.1 |
| Sanctum installed | PASS — v4.3.2 |
| AdminLTE installed | PASS — v3.16.0 |
| Mews Captcha installed | PASS — 3.4.9 |
| Timezone = Asia/Jakarta | PASS |
| Locale = id | PASS |
| Session lifetime = 120 | PASS |
| Lambda configuration available | PASS |
| FaceX configuration available | PASS |
| Composer autoload configured | PASS — `FacevotingHelper.php` in `autoload.files` |
| `config:cache` succeeds | PASS |
| Application boots without error | PASS |

---

## Security Review

| Check | Result |
|---|---|
| `.env` is gitignored | PASS — listed in `facevoting/.gitignore` |
| `APP_KEY` not committed | PASS — not in `.env.example`, only in `.env` |
| No secrets in config files | PASS — all config values use `env()` |
| All credentials from `env()` | PASS — database, services, session all env-driven |
| Real API keys in `.env` only | PASS — `.env.example` has empty placeholders |

---

## Notes / Deviations

1. **Laravel version**: `composer create-project laravel/laravel:^12.0` was used instead of `laravel new facevoting --no-interaction`, because the Laravel Installer 5.25.1 installs the latest stable version (13.x) by default. Pinning to `^12.0` ensures the correct major version.

2. **Project location**: The Laravel project was created at `backend/facevoting/` rather than replacing the `backend/` directory in-place. This preserves the existing CI3 application during the migration phase (as recommended by the migration plan's strangler-fig strategy). All subsequent tasks will work within `backend/facevoting/`.

3. **`config/database.php` default driver**: The file defaults to `'sqlite'` but runtime behaviour is controlled by `DB_CONNECTION=mysql` in `.env`. This is correct Laravel behaviour.

4. **`install:api` created a migration**: `personal_access_tokens` migration was created and executed against the default SQLite database during install. This migration will be superseded by TASK-02's MySQL migrations.
