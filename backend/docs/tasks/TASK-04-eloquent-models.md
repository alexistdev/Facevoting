# TASK-04 — Eloquent Models

**Phase**: 1 — Foundation  
**Depends on**: TASK-02  
**Estimated effort**: 2 hours

---

## Description

Create all Eloquent model classes. Each model must declare the correct table name, `$fillable` columns, `$timestamps` setting, and all Eloquent relationships. These models replace the CI3 god-models `Admin_m` and `Api_model`.

---

## Affected Files

### Create
- `app/Models/Admin.php`
- `app/Models/User.php`
- `app/Models/UserDetail.php`
- `app/Models/Album.php`
- `app/Models/Photo.php`
- `app/Models/FaceMatch.php`
- `app/Models/Category.php`
- `app/Models/Candidate.php`
- `app/Models/CandidateDetail.php`
- `app/Models/VoterAuthorization.php`
- `app/Models/Vote.php`

---

## Implementation Notes

### `app/Models/Admin.php`
```php
class Admin extends Authenticatable
{
    use HasFactory;
    protected $table    = 'admin';
    protected $fillable = ['username', 'password', 'status'];
    protected $hidden   = ['password'];
    public $timestamps  = false;
}
```

### `app/Models/User.php`
```php
class User extends Authenticatable
{
    use HasApiTokens, HasFactory;
    protected $table    = 'user';
    protected $primaryKey = 'id_user';
    protected $fillable = ['email', 'password', 'token_firebase', 'token_login', 'entry_id', 'status'];
    protected $hidden   = ['password'];
    public $timestamps  = false;

    public function detail(): HasOne         { return $this->hasOne(UserDetail::class, 'id_user'); }
    public function photos(): HasMany        { return $this->hasMany(Photo::class, 'id_user'); }
    public function authorizations(): HasMany{ return $this->hasMany(VoterAuthorization::class, 'id_user'); }
    public function votes(): HasMany         { return $this->hasMany(Vote::class, 'id_user'); }
    public function faceMatches(): HasMany   { return $this->hasMany(FaceMatch::class, 'id_user'); }
}
```

### `app/Models/UserDetail.php`
```php
class UserDetail extends Model
{
    protected $table      = 'detail_user';
    protected $primaryKey = 'id_detail';
    protected $fillable   = ['id_user', 'nama', 'identitas'];
    public $timestamps    = false;

    public function user(): BelongsTo { return $this->belongsTo(User::class, 'id_user'); }
}
```

### `app/Models/Album.php`
```php
class Album extends Model
{
    protected $table    = 'album';
    protected $primaryKey = 'id_album';
    protected $fillable = ['nama_album', 'kode_album'];
    public $timestamps  = false;

    public function photos(): HasMany { return $this->hasMany(Photo::class, 'id_album'); }
}
```

### `app/Models/Photo.php`
```php
class Photo extends Model
{
    protected $table    = 'photo';
    protected $primaryKey = 'id_photo';
    protected $fillable = ['id_album', 'id_user', 'entry_id', 'nama_photo', 'gambar', 'status_train'];
    public $timestamps  = false;

    public function user(): BelongsTo  { return $this->belongsTo(User::class, 'id_user'); }
    public function album(): BelongsTo { return $this->belongsTo(Album::class, 'id_album'); }
}
```

### `app/Models/FaceMatch.php`
```php
class FaceMatch extends Model
{
    protected $table    = 'pencocokan';
    protected $primaryKey = 'id_pencocokan';
    protected $fillable = ['id_user', 'nama_photo', 'gambar', 'score'];
    public $timestamps  = false;

    public function user(): BelongsTo { return $this->belongsTo(User::class, 'id_user'); }
}
```

### `app/Models/Category.php`
```php
class Category extends Model
{
    protected $table    = 'kategori';
    protected $primaryKey = 'id_kategori';
    protected $fillable = ['nama_kategori', 'logo_kategori', 'status_kategori'];
    public $timestamps  = false;

    public function candidates(): HasMany     { return $this->hasMany(Candidate::class, 'id_kategori'); }
    public function authorizations(): HasMany { return $this->hasMany(VoterAuthorization::class, 'id_kategori'); }
    public function votes(): HasMany          { return $this->hasMany(Vote::class, 'id_kategori'); }

    public function scopeOpen($query)         { return $query->where('status_kategori', 2); }
    public function scopeSetup($query)        { return $query->where('status_kategori', 1); }
}
```

### `app/Models/Candidate.php`
```php
class Candidate extends Model
{
    protected $table    = 'paslon';
    protected $primaryKey = 'id_paslon';
    protected $fillable = ['id_kategori', 'judul_paslon', 'ketua_paslon', 'wakil_paslon',
                           'photo1_paslon', 'photo2_paslon', 'perolehan'];
    public $timestamps  = false;

    public function category(): BelongsTo { return $this->belongsTo(Category::class, 'id_kategori'); }
    public function detail(): HasOne      { return $this->hasOne(CandidateDetail::class, 'id_paslon'); }
    public function votes(): HasMany      { return $this->hasMany(Vote::class, 'id_paslon'); }
}
```

### `app/Models/CandidateDetail.php`
```php
class CandidateDetail extends Model
{
    protected $table    = 'detail_paslon';
    protected $primaryKey = 'id_detailpaslon';
    protected $fillable = ['id_paslon', 'visi_misi', 'profil_catum', 'profil_cawatum'];
    public $timestamps  = false;

    public function candidate(): BelongsTo { return $this->belongsTo(Candidate::class, 'id_paslon'); }
}
```

### `app/Models/VoterAuthorization.php`
```php
class VoterAuthorization extends Model
{
    protected $table    = 'otorisasi_pemilih';
    protected $primaryKey = 'id_otorisasi';
    protected $fillable = ['id_user', 'id_kategori', 'status'];
    public $timestamps  = false;

    public function user(): BelongsTo     { return $this->belongsTo(User::class, 'id_user'); }
    public function category(): BelongsTo { return $this->belongsTo(Category::class, 'id_kategori'); }
}
```

### `app/Models/Vote.php`
```php
class Vote extends Model
{
    protected $table    = 'voting';
    protected $primaryKey = 'id_voting';
    protected $fillable = ['id_user', 'id_kategori', 'id_paslon', 'tanggal_voting'];
    public $timestamps  = false;

    public function user(): BelongsTo      { return $this->belongsTo(User::class, 'id_user'); }
    public function category(): BelongsTo  { return $this->belongsTo(Category::class, 'id_kategori'); }
    public function candidate(): BelongsTo { return $this->belongsTo(Candidate::class, 'id_paslon'); }
}
```

---

## Acceptance Criteria

- [ ] `User::find(10)->email` returns `'alexistdev@gmail.com'`
- [ ] `User::find(10)->detail->nama` returns `'Alexsander Hendra Wijaya'`
- [ ] `Category::find(8)->candidates` returns a collection
- [ ] `Candidate::with('category')->first()->category instanceof Category` is true
- [ ] `Candidate::with('detail')->first()->detail instanceof CandidateDetail` is true
- [ ] `Vote::with(['user', 'category', 'candidate'])->get()` runs without eager loading errors
- [ ] `User::find(10)->authorizations` returns a collection with 2 items
- [ ] `Category::open()->get()` returns only categories with `status_kategori = 2`
- [ ] `Category::setup()->get()` returns only categories with `status_kategori = 1`
- [ ] `Admin::find(1)->username` returns `'admin'`
- [ ] `Photo::with('album')->get()` runs without error
- [ ] All models have correct `$table` values (no Laravel convention guessing)
- [ ] No model has `$timestamps = true` (none of the CI3 tables have created_at/updated_at)
