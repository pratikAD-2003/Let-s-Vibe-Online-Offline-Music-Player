package com.example.musicplayer;

import static com.example.musicplayer.Onboarding.OnboardingScreen.NAME_PKG;
import static com.example.musicplayer.Onboarding.OnboardingScreen.STATUS;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.musicplayer.Home.HomeScreen;
import com.example.musicplayer.Onboarding.OnboardingScreen;
import com.example.musicplayer.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences(NAME_PKG, MODE_PRIVATE);
        String type = sharedPreferences.getString(STATUS, "false");
        if (Objects.equals(type, "true")) {
            startActivity(new Intent(MainActivity.this, HomeScreen.class));
            finish();
        } else {
            startActivity(new Intent(MainActivity.this, OnboardingScreen.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
    }
}