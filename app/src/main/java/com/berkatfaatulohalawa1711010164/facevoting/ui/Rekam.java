package com.berkatfaatulohalawa1711010164.facevoting.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.berkatfaatulohalawa1711010164.facevoting.API.APIService;
import com.berkatfaatulohalawa1711010164.facevoting.BuildConfig;
import com.berkatfaatulohalawa1711010164.facevoting.R;
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants;
import com.berkatfaatulohalawa1711010164.facevoting.helper.ErrorHelper;
import com.berkatfaatulohalawa1711010164.facevoting.helper.SessionHelper;
import com.berkatfaatulohalawa1711010164.facevoting.model.ErrorModel;
import com.berkatfaatulohalawa1711010164.facevoting.model.MessageModel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class Rekam extends AppCompatActivity {
    private ImageView mPhoto;
    private ProgressDialog progressDialog;
    private Button mRekam,mCek;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekam);
        init();
        mRekam.setOnClickListener(v -> dispatchTakePictureIntent());
    }
    /* Mengambil gambar */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void init()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading.....");
        mPhoto =findViewById(R.id.photo);
        mRekam = findViewById(R.id.btnRekam);
        mCek = findViewById(R.id.btnCek);
        mRekam.setVisibility(View.VISIBLE);
        mCek.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            /* Pengaturan Photo*/
            int targetW = mPhoto.getWidth();
            int targetH = mPhoto.getHeight();
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            Bitmap converetdImage = getResizedBitmap(bitmap, 1024);


            /* Rotasi Bitmap */
            Matrix matrix = new Matrix();
            matrix.postRotate(270);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(converetdImage, mPhoto.getWidth(), mPhoto.getHeight(), true);
            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

            /* Konversi Bitmap ke byteArray*/
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            /*Preview Gambar*/
            mPhoto.setImageBitmap(rotatedBitmap);
            mCek.setVisibility(View.VISIBLE);

            /* Pengaturan Tombol Validasi*/
            mCek.setOnClickListener(v -> {
                tampilLoading();
                /* Mengecek user id*/
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                        Constants.USER_KEY, Context.MODE_PRIVATE);
                String myId = sharedPreferences.getString("id_user", null);
                RequestBody idUser = RequestBody.create(MediaType.parse("multipart/form-data"), myId);
                RequestBody requestBody = RequestBody
                        .create(MediaType.parse("application/octet-stream"), byteArray);
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("upload",  currentPhotoPath, requestBody);
                Call<MessageModel> call = APIService.Factory.create(getApplicationContext()).rekamWajah(idUser,filePart);
                call.enqueue(new Callback<MessageModel>() {
                    @EverythingIsNonNull
                    @Override
                    public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
                        hideLoading();
                        if(response.isSuccessful()){
                            if (SessionHelper.catatrekam(Rekam.this)){
                                    Intent intent = new Intent(Rekam.this, Checkpoint.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                        } else {
                            ErrorModel error = ErrorHelper.parseError(response);
                            tampilPesan(error.message());
                        }
                    }
                    @EverythingIsNonNull
                    @Override
                    public void onFailure(Call<MessageModel> call, Throwable t) {
                        hideLoading();
                        tampilPesan(t.getMessage());
                    }
                });
            });
        }
    }

    /* Membuat gambar dari camera */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /* Mengurangi ukuran dari Bitmap */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /* Menampilkan pesan notifikasi */
    public void tampilPesan(String pesan)
    {
        Toast.makeText(getApplicationContext(), pesan, Toast.LENGTH_LONG).show();
    }

    /* Menampilkan Loading */
    private void tampilLoading(){
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    /* Menghilangkan Loading */
    private void hideLoading(){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}