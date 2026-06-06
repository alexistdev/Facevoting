# TASK-28 — Service Unit Tests

**Phase**: 7 — Testing  
**Depends on**: TASK-16, TASK-17  
**Estimated effort**: 2 hours

---

## Description

Write Pest unit tests for `LambdaService` and `FaceMatchService`. All tests use `Http::fake()` to intercept outbound HTTP calls — no real API calls are made. Tests verify correct URL, headers, request body, and response parsing.

---

## Affected Files

### Create
- `tests/Unit/Services/LambdaServiceTest.php`
- `tests/Unit/Services/FaceMatchServiceTest.php`

---

## Implementation Notes

### `tests/Unit/Services/LambdaServiceTest.php`

```php
use App\Services\LambdaService;
use Illuminate\Support\Facades\Http;

beforeEach(function () {
    $this->service = app(LambdaService::class);
});

it('trainPhoto sends POST to correct URL with required headers', function () {
    Http::fake([
        'lambda-face-recognition.p.rapidapi.com/album_train' => Http::response([
            'album' => 'Facevoting2021', 'image_count' => 5,
        ], 200),
    ]);

    $result = $this->service->trainPhoto(
        albumName: 'Facevoting2021',
        albumKey:  'test_key',
        entryId:   'abc123',
        imageUrl:  'http://facevoting.xyz/gambar/user/photo.jpg',
    );

    expect($result)->toBeArray()
                   ->toHaveKey('album')
                   ->toHaveKey('image_count');

    Http::assertSent(function ($request) {
        return $request->url() === 'https://lambda-face-recognition.p.rapidapi.com/album_train'
            && $request->method() === 'POST'
            && $request->hasHeader('x-rapidapi-host', 'lambda-face-recognition.p.rapidapi.com')
            && $request->hasHeader('x-rapidapi-key');
    });
});

it('trainPhoto returns null on HTTP failure', function () {
    Http::fake([
        'lambda-face-recognition.p.rapidapi.com/*' => Http::response([], 500),
    ]);

    $result = $this->service->trainPhoto('album', 'key', 'entry', 'url');

    expect($result)->toBeNull();
});

it('viewAlbum sends GET with query params', function () {
    Http::fake([
        'lambda-face-recognition.p.rapidapi.com/album*' => Http::response(['entries' => []], 200),
    ]);

    $result = $this->service->viewAlbum('Facevoting2021', 'test_key');

    Http::assertSent(function ($request) {
        return $request->method() === 'GET'
            && str_contains($request->url(), '/album')
            && str_contains($request->url(), 'album=Facevoting2021');
    });

    expect($result)->toBeArray();
});

it('rebuildAlbum sends GET to /album_rebuild', function () {
    Http::fake([
        'lambda-face-recognition.p.rapidapi.com/album_rebuild*' => Http::response(['status' => 'ok'], 200),
    ]);

    $this->service->rebuildAlbum('Facevoting2021', 'test_key');

    Http::assertSent(function ($request) {
        return str_contains($request->url(), 'album_rebuild');
    });
});

it('is a singleton in the container', function () {
    $a = app(LambdaService::class);
    $b = app(LambdaService::class);
    expect($a)->toBe($b);
});
```

### `tests/Unit/Services/FaceMatchServiceTest.php`

```php
use App\Services\FaceMatchService;
use Illuminate\Support\Facades\Http;

beforeEach(function () {
    $this->service = app(FaceMatchService::class);
});

it('sends POST to FaceXAPI with correct User_id header', function () {
    Http::fake([
        'facexapi.com/*' => Http::response(['data' => ['status' => 'matched']], 200),
    ]);

    $result = $this->service->compare(
        'http://facevoting.xyz/gambar/user/train.jpg',
        'http://facevoting.xyz/gambar/user/verify.jpg',
    );

    expect($result)->toBe('matched');

    Http::assertSent(function ($request) {
        return $request->url() === 'https://www.facexapi.com/match_faces'
            && $request->method() === 'POST'
            && $request->hasHeader('User_id', '603f05b94e6c5e6c15c171e7');
    });
});

it('sends img_1 and img_2 fields in body', function () {
    Http::fake(['facexapi.com/*' => Http::response(['data' => ['status' => 'unmatched']], 200)]);

    $this->service->compare('http://url/img1.jpg', 'http://url/img2.jpg');

    Http::assertSent(function ($request) {
        $body = $request->data();
        return isset($body['img_1']) && isset($body['img_2'])
            && $body['img_1'] === 'http://url/img1.jpg'
            && $body['img_2'] === 'http://url/img2.jpg';
    });
});

it('returns null on HTTP failure', function () {
    Http::fake(['facexapi.com/*' => Http::response([], 500)]);

    $result = $this->service->compare('url1', 'url2');

    expect($result)->toBeNull();
});

it('extracts data.status from response', function () {
    Http::fake([
        'facexapi.com/*' => Http::response([
            'data' => ['status' => 'unmatched', 'score' => 0.12],
        ], 200),
    ]);

    $result = $this->service->compare('url1', 'url2');

    expect($result)->toBe('unmatched');
});

it('uses exact header name User_id with underscore and capital U', function () {
    Http::fake(['facexapi.com/*' => Http::response(['data' => ['status' => 'matched']], 200)]);

    $this->service->compare('u', 'v');

    Http::assertSent(function ($request) {
        // Header name must be 'User_id' not 'user-id' or 'user_id'
        return $request->hasHeader('User_id');
    });
});
```

---

## Acceptance Criteria

- [ ] All tests in both files pass with `php artisan test --filter=Unit`
- [ ] No real HTTP requests are made — `Http::fake()` intercepts all outbound calls
- [ ] `LambdaService::trainPhoto()` correct URL and header test passes
- [ ] `LambdaService::viewAlbum()` uses GET verb
- [ ] `LambdaService::rebuildAlbum()` uses `/album_rebuild` path
- [ ] `FaceMatchService::compare()` header name is `User_id` (exact capitalisation + underscore)
- [ ] `FaceMatchService::compare()` extracts `data.status` from response correctly
- [ ] Both services return `null` on HTTP 5xx or connection failure
- [ ] Singleton binding is verified for both services
- [ ] `php artisan test` runs all 3 test suites (Unit + Feature/Admin + Feature/Api) without error
