# TASK-16 — Lambda Face Recognition Service

**Phase**: 4 — Service Layer  
**Depends on**: TASK-01  
**Estimated effort**: 2 hours

---

## Description

Create `LambdaService` — a Laravel service class that wraps all HTTP calls to the Lambda Face Recognition API (RapidAPI). Replaces the inline cURL blocks scattered across CI3 controllers. The service is bound as a singleton and injected into controllers that need it.

---

## Affected Files

### Create
- `app/Services/LambdaService.php`

### Modify
- `app/Providers/AppServiceProvider.php` — singleton binding
- `config/services.php` — Lambda API key, album name, album key
- `.env` / `.env.example` — Lambda env vars

---

## Implementation Notes

### `app/Services/LambdaService.php`

```php
namespace App\Services;

use Illuminate\Support\Facades\Http;

class LambdaService
{
    private string $apiKey;
    private string $host = 'lambda-face-recognition.p.rapidapi.com';

    public function __construct()
    {
        $this->apiKey = config('services.lambda.key');
    }

    public function trainPhoto(string $albumName, string $albumKey, string $entryId, string $imageUrl): ?array
    {
        $response = Http::withHeaders([
            'x-rapidapi-host' => $this->host,
            'x-rapidapi-key'  => $this->apiKey,
        ])
        ->timeout(30)
        ->asForm()
        ->post("https://{$this->host}/album_train", [
            'urls'     => $imageUrl,
            'album'    => $albumName,
            'albumkey' => $albumKey,
            'entryid'  => $entryId,
        ]);

        if ($response->failed()) return null;
        return $response->json();
    }

    public function viewAlbum(string $albumName, string $albumKey): ?array
    {
        $response = Http::withHeaders([
            'x-rapidapi-host' => $this->host,
            'x-rapidapi-key'  => $this->apiKey,
        ])
        ->timeout(30)
        ->get("https://{$this->host}/album", [
            'album'    => $albumName,
            'albumkey' => $albumKey,
        ]);

        if ($response->failed()) return null;
        return $response->json();
    }

    public function rebuildAlbum(string $albumName, string $albumKey): ?array
    {
        $response = Http::withHeaders([
            'x-rapidapi-host' => $this->host,
            'x-rapidapi-key'  => $this->apiKey,
        ])
        ->timeout(30)
        ->get("https://{$this->host}/album_rebuild", [
            'album'    => $albumName,
            'albumkey' => $albumKey,
        ]);

        if ($response->failed()) return null;
        return $response->json();
    }
}
```

### `app/Providers/AppServiceProvider.php`
```php
$this->app->singleton(\App\Services\LambdaService::class);
```

### API Parameters (from CI3 source, verified values)

| Key | Value |
|---|---|
| API Key | `932571abf0msh45cf0f3cef74aacp19e151jsn33e9949a1974` |
| Host | `lambda-face-recognition.p.rapidapi.com` |
| Album name | `Facevoting2021` |
| Album key | `859d15e7156ef8128f921671b6a3d941a4a7f686b4e762bbc679c4809bdebb19` |
| Timeout | 30 seconds |
| Train verb | POST |
| View/rebuild verb | GET |

### Image URL Format (parity)

The image URL passed to Lambda must use the hardcoded domain `http://facevoting.xyz`:
```
http://facevoting.xyz/gambar/user/{gambar_filename}
```
This is the CI3 parity requirement. The calling controller constructs this URL — LambdaService receives it as a parameter.

---

## Acceptance Criteria

- [ ] `app()->make(LambdaService::class)` resolves without error
- [ ] `LambdaService::trainPhoto()` sends a POST request to `https://lambda-face-recognition.p.rapidapi.com/album_train`
- [ ] Request includes `x-rapidapi-host` and `x-rapidapi-key` headers
- [ ] Request is form-encoded (`Content-Type: application/x-www-form-urlencoded`)
- [ ] Timeout is 30 seconds on all methods
- [ ] `trainPhoto()` returns null on HTTP failure
- [ ] `trainPhoto()` returns decoded JSON array on success
- [ ] `viewAlbum()` sends a GET to `/album` with query params
- [ ] `rebuildAlbum()` sends a GET to `/album_rebuild` with query params
- [ ] Unit test with Http fake verifies correct headers and body are sent for each method
- [ ] Service is a singleton — same instance resolved from container multiple times
