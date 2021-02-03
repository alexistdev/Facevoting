package com.berkatfaatulohalawa1711010164.facevoting.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.berkatfaatulohalawa1711010164.facevoting.MainActivity;
import com.berkatfaatulohalawa1711010164.facevoting.R;
import com.berkatfaatulohalawa1711010164.facevoting.helper.MyFirebaseMessagingService;
import com.google.firebase.iid.FirebaseInstanceId;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView btnStart = findViewById(R.id.tombol_start);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, preLogin1.class);
                startActivity(intent);
            }
        });

        /* Mendapatkan data token */
        String mytoken = MyFirebaseMessagingService.getToken(getApplicationContext());
        Toast.makeText(this, mytoken, Toast.LENGTH_SHORT).show();
        //Log.d("newToken", this.getPreferences(Context.MODE_PRIVATE).getString("fb", "empty :("));
    }
}