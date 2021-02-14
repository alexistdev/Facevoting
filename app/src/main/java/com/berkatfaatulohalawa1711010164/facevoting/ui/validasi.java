package com.berkatfaatulohalawa1711010164.facevoting.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.berkatfaatulohalawa1711010164.facevoting.R;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class validasi extends AppCompatActivity {
    private ImageView mPhoto;
    private Button mRekam,mCek;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validasi);
        init();
        mRekam.setOnClickListener(this::takePicture);
        mCek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void init()
    {
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage("Loading.....");
        mPhoto =findViewById(R.id.photo);
        mRekam = findViewById(R.id.btnRekam);
        mCek = findViewById(R.id.btnCek);
        mRekam.setVisibility(View.VISIBLE);
        mCek.setVisibility(View.GONE);
    }

    public void takePicture(View view)
    {
        Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(imageTakeIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(imageTakeIntent,REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mPhoto.setImageBitmap(imageBitmap);
            mRekam.setVisibility(View.GONE);
            mCek.setVisibility(View.VISIBLE);
        }
    }

    private void saveWajah(String id){
        RequestBody requestBodyId = RequestBody.create(MediaType.parse("text/plain"), id);

    }

}