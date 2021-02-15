package com.berkatfaatulohalawa1711010164.facevoting.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.berkatfaatulohalawa1711010164.facevoting.API.APIService;
import com.berkatfaatulohalawa1711010164.facevoting.BuildConfig;
import com.berkatfaatulohalawa1711010164.facevoting.MainActivity;
import com.berkatfaatulohalawa1711010164.facevoting.R;
import com.berkatfaatulohalawa1711010164.facevoting.helper.ErrorHelper;
import com.berkatfaatulohalawa1711010164.facevoting.model.ErrorModel;
import com.berkatfaatulohalawa1711010164.facevoting.model.MessageModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

public class validasi extends AppCompatActivity {
    private ImageView mPhoto;
    private TextView mText;
    private Button mRekam,mCek;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validasi);
        init();
        mRekam.setOnClickListener(v -> dispatchTakePictureIntent());

    }

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
        mText = findViewById(R.id.txtTesting);
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
            mPhoto.setImageBitmap(bitmap);
            mText.setText(currentPhotoPath);
            mRekam.setVisibility(View.GONE);
            mCek.setVisibility(View.VISIBLE);
            mCek.setOnClickListener(v -> {
                File file = savebitmap(bitmap);
                RequestBody idUser = RequestBody.create(MediaType.parse("multipart/form-data"), "4");
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("upload", file.getName(), RequestBody.create(MediaType.parse("files/Pictures/*"), file));
                Call<MessageModel> call = APIService.Factory.create(getApplicationContext()).rekamWajah(idUser,filePart);
                call.enqueue(new Callback<MessageModel>() {
                    @EverythingIsNonNull
                    @Override
                    public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
                        if(response.isSuccessful()){
                            Intent intent = new Intent(validasi.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                            startActivity(intent);
                            finish();
                        } else {
                            ErrorModel error = ErrorHelper.parseError(response);
                            tampilPesan(error.message());
                        }
                    }
                    @EverythingIsNonNull
                    @Override
                    public void onFailure(Call<MessageModel> call, Throwable t) {
                        tampilPesan(t.getMessage());
                    }
                });
            });
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        //String timeStamp =
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private File savebitmap(Bitmap bmp) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        File file = new File(extStorageDirectory, "temp.png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, "temp.png");

        }

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    public void tampilPesan(String pesan)
    {
        Toast.makeText(getApplicationContext(), pesan, Toast.LENGTH_LONG).show();
    }
}