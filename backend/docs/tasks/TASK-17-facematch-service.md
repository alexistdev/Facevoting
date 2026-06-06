# TASK-17 — FaceX Face Match Service

**Phase**: 4 — Service Layer  
**Depends on**: TASK-01  
**Estimated effort**: 1 hour

---

## Description

Create `FaceMatchService` — a Laravel service class that wraps the FaceXAPI comparison call. Used by the mobile API's face verification endpoint (`POST /api/gambar/cek`). The service returns the `data.status` string from the FaceXAPI response (`"matched"` or `"unmatched"`), or null on failure.

---

## Affected Files

### Create
- `app/Services/FaceMatchService.php`

### Modify
- `app/Providers/AppServiceProvider.php` — singleton binding
- `config/services.php` — FaceX user ID
- `.env` / `.env.example` — FACEX_USER_ID

---

## Implementation Notes

### `app/Services/FaceMatchService.php`

```php
namespace App\Services;

use Illuminate\Support\Facades\Http;

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
        ])
        ->timeout(30)
        ->post('https://www.facexapi.com/match_faces', [
            'img_1' => $trainingImageUrl,
            'img_2' => $verifyImageUrl,
        ]);

        if ($response->failed()) return null;

        return $response->json('data.status');
    }
}
```

### AppServiceProvider binding
```php
$this->app->singleton(\App\Services\FaceMatchService::class);
```

### API Parameters (from CI3 source)

| Key | Value |
|---|---|
| User ID | `603f05b94e6c5e6c15c171e7` |
| Header name | `User_id` (capital U, underscore — exact parity) |
| Endpoint | `https://www.facexapi.com/match_faces` |
| Method | POST |
| Field `img_1` | URL of training photo |
| Field `img_2` | URL of verification photo |
| Response field | `data.status` |

### Image URL Format (parity)

Both image URLs must use the hardcoded base:
```
http://facevoting.xyz/gambar/user/{filename}
```
The calling controller constructs this URL — `FaceMatchService.compare()` receives full URLs as arguments.

---

## Acceptance Criteria

- [ ] `app()->make(FaceMatchService::class)` resolves without error
- [ ] `compare()` sends a POST to `https://www.facexapi.com/match_faces`
- [ ] Header `User_id` (exact capitalisation + underscore) is sent with value `603f05b94e6c5e6c15c171e7`
- [ ] Request body contains `img_1` and `img_2` fields
- [ ] Returns `null` on HTTP failure
- [ ] Returns the string value of `data.status` on success (e.g. `"matched"`)
- [ ] Timeout is 30 seconds
- [ ] Unit test with Http fake verifies correct header name and response parsing
- [ ] Service is a singleton — same instance resolved from container multiple times
