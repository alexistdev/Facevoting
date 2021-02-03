package com.berkatfaatulohalawa1711010164.facevoting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import com.berkatfaatulohalawa1711010164.facevoting.fragment.akun_fragment;
import com.berkatfaatulohalawa1711010164.facevoting.fragment.hasil_fragment;
import com.berkatfaatulohalawa1711010164.facevoting.fragment.home_fragment;
import com.berkatfaatulohalawa1711010164.facevoting.fragment.votefragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;



public class MainActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new home_fragment());
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment;
            switch (item.getItemId()){
                case R.id.home_menu:
                    fragment = new home_fragment();
                    break;
                case R.id.vote_menu:
                    fragment = new votefragment();
                    break;
                case R.id.hasil_menu:
                    fragment = new hasil_fragment();
                    break;
                default:
                    fragment = new akun_fragment();
                    break;
            }
            return loadFragment(fragment);
        });

    }

    /* Meload Fragment */
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}