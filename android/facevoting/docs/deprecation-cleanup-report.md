# Deprecation Cleanup Report — Phase 5

## Summary

All deprecated Android APIs identified in the FaceVoting app have been replaced with modern equivalents. Behavior is unchanged.

---

## Changes

### 1. OkHttp — `MediaType.parse()` / `RequestBody.create()` → extension functions
**File:** `ui/Validasi.kt`

| Before | After |
|---|---|
| `import okhttp3.MediaType` | `import okhttp3.MediaType.Companion.toMediaTypeOrNull` |
| `import okhttp3.RequestBody` | `import okhttp3.RequestBody.Companion.toRequestBody` |
| `RequestBody.create(MediaType.parse("multipart/form-data"), myId ?: "")` | `(myId ?: "").toRequestBody("multipart/form-data".toMediaTypeOrNull())` |
| `RequestBody.create(MediaType.parse("application/octet-stream"), byteArray)` | `byteArray.toRequestBody("application/octet-stream".toMediaTypeOrNull())` |

`Rekam.kt` already used the modern extension-function style; `Validasi.kt` now matches.

---

### 2. BottomNavigationView — `setOnNavigationItemSelectedListener` → `setOnItemSelectedListener`
**File:** `MainActivity.kt`

| Before | After |
|---|---|
| `bottomNavigationView.setOnNavigationItemSelectedListener { item -> … }` | `bottomNavigationView.setOnItemSelectedListener { item -> … }` |

Same callback signature; no logic changes.

---

### 3. ConnectivityManager — `activeNetworkInfo` / `NetworkInfo` → `NetworkCapabilities`
**File:** `API/NetworkConnectionInterceptor.kt`

The `activeNetworkInfo` and `NetworkInfo` APIs are deprecated since API 29. Replaced with `ConnectivityManager.activeNetwork` + `getNetworkCapabilities()` + `NET_CAPABILITY_INTERNET`.

| Before | After |
|---|---|
| `connectivityManager.activeNetworkInfo` | `cm.activeNetwork` → `cm.getNetworkCapabilities(network)` |
| `netInfo != null && netInfo.isConnected` | `caps.hasCapability(NET_CAPABILITY_INTERNET)` |
| `@Suppress("DEPRECATION")` annotation | Removed |

---

### 4. `ProgressDialog` → `AlertDialog` (four fragments)
**Files:** `fragment/votefragment.kt`, `fragment/home_fragment.kt`, `fragment/akun_fragment.kt`, `fragment/hasil_fragment.kt`

`android.app.ProgressDialog` is deprecated. All four fragments now use `androidx.appcompat.app.AlertDialog` built via its builder, matching the pattern already used in `Rekam.kt` and `Validasi.kt`.

| Before | After |
|---|---|
| `import android.app.ProgressDialog` | `import androidx.appcompat.app.AlertDialog` |
| `private lateinit var …: ProgressDialog` | `private lateinit var …: AlertDialog` |
| `ProgressDialog(context).apply { setCancelable(false); setMessage("Loading.....") }` | `AlertDialog.Builder(requireContext()).setMessage("Loading.....").setCancelable(false).create()` |

`isShowing`, `show()`, and `dismiss()` are available on `AlertDialog` with the same semantics, so no call-site changes were needed.

---

### 5. Deprecated storage permissions removed from manifest
**File:** `AndroidManifest.xml`

`WRITE_EXTERNAL_STORAGE` and `READ_EXTERNAL_STORAGE` are deprecated for apps targeting Android 11+ (API 30). Removed both declarations.

The app already uses `getExternalFilesDir(Environment.DIRECTORY_PICTURES)` for camera output in both `Rekam.kt` and `Validasi.kt`, which is app-private external storage and requires **no manifest permissions**. The removals have no runtime impact.

---

## APIs Not Present (confirmed absent)

| API | Status |
|---|---|
| `AsyncTask` | Not found — app uses Retrofit callbacks |
| `startActivityForResult` / `onActivityResult` | Not found — app already uses Activity Result API (`registerForActivityResult`) |
| `requestPermissions` / `onRequestPermissionsResult` | Not found |
| `Environment.getExternalStorageDirectory()` | Not found — app uses `getExternalFilesDir()` |

---

## Files Modified

| File | Change |
|---|---|
| `app/src/main/java/…/ui/Validasi.kt` | OkHttp extension functions |
| `app/src/main/java/…/MainActivity.kt` | BottomNav listener |
| `app/src/main/java/…/API/NetworkConnectionInterceptor.kt` | NetworkCapabilities API |
| `app/src/main/java/…/fragment/votefragment.kt` | AlertDialog |
| `app/src/main/java/…/fragment/home_fragment.kt` | AlertDialog |
| `app/src/main/java/…/fragment/akun_fragment.kt` | AlertDialog |
| `app/src/main/java/…/fragment/hasil_fragment.kt` | AlertDialog |
| `app/src/main/AndroidManifest.xml` | Remove storage permissions |
