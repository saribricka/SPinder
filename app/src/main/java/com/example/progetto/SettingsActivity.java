package com.example.progetto;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (savedInstanceState == null)
            Utilities.insertFragment(this, new SettingsFragment(),
                    SettingsFragment.class.getSimpleName());
    }
}
