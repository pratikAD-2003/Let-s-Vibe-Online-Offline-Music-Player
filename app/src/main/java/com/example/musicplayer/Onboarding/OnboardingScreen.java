package com.example.musicplayer.Onboarding;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.musicplayer.Home.HomeScreen;
import com.example.musicplayer.R;
import com.example.musicplayer.databinding.ActivityOnboardingScreenBinding;

public class OnboardingScreen extends AppCompatActivity {
    ActivityOnboardingScreenBinding binding;
    public static final String NAME_PKG = "containingName";
    public static final String STATUS = "status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityOnboardingScreenBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences(NAME_PKG, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        binding.listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(STATUS, "true");
                editor.apply();
                startActivity(new Intent(OnboardingScreen.this, HomeScreen.class));
                finish();
            }
        });
    }
}