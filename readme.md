# Facevoting Application

This is a facial detection validation application for verifying campus voting.

## Details:
- Minimum PHP version: 5.3.7
- CodeIgniter 3.1.11: [Download](http://codeigniter.com/download)
- CodeIgniter-RestServer 3.1: [Repository](https://github.com/chriskacerguis/codeigniter-restserver)
- AdminLTE 3.0.5: [Website](https://adminlte.io/)
- Image Processing Facex: [Website](https://facex.io/)

## Installation Instructions:

1. Clone the repository:
   ```bash
   git clone https://github.com/alexistdev/Facevoting.git
   ```
2. Create a database and import the database file located in the Facevoting folder.
3. Run the following command in the terminal:
   ```bash
   composer install
   ```
4. Update `config.php` and `database.php` with the appropriate URL and database configurations.
5. Open Postman and access the API endpoint at: `http://localhost/Facevoting/api/`
6. Open the Android project files located in the "android" subfolder using Android Studio.
7. Modify `config.java` with your localhost or web hosting URL settings.
8. Create an account at [facex.io](https://facex.io/) and obtain your API KEY.
9. Open `Controller/api/Gambar.php` and check the `_banding()` method. Insert your API key in the following section:
   ```php
   $headers[] = 'User_id: 603f05b94e6c5e6c15c171e7';
   ```
10. Change the URL in the `_banding()` method to use `base_url()`:
    ```php
    $post = array(
      'img_1' => 'http://facevoting.xyz/gambar/user/'.$photoAwal,
      'img_2' => 'http://facevoting.xyz/gambar/user/'.$photoPembanding
    );
    ```

## Screenshots

| Splash Activity                                            | Face Detection                                              | Voting Page                                                |
|------------------------------------------------------------|-------------------------------------------------------------|------------------------------------------------------------|
| ![Splash Activity](https://i.postimg.cc/141gs1n7/splashactivity.png) | ![Face Detection](https://i.postimg.cc/TwhCFFDW/deteksi-wajah.png) | ![Voting Page](https://i.postimg.cc/YC3R9MNW/voting.png) |
