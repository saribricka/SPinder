package com.example.progetto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.progetto.ViewModel.AddViewModel;

import static com.example.progetto.Utilities.REQUEST_IMAGE_CAPTURE;

public class MainActivity extends AppCompatActivity {

    private static final String FRAGMENT_TAG_HOME = "HomeFragment";
    private static final String ACTIVITY_TAG_LOGIN = "LoginActivity";

    private AddViewModel addViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if there was no configuration change, create the fragment
        if (savedInstanceState == null) {
            setContentView(R.layout.log_in);

        } else {
            setContentView(R.layout.activity_main);
            Utilities.insertFragment(this, new HomeFragment(), FRAGMENT_TAG_HOME);
        }

        addViewModel = new ViewModelProvider(this).get(AddViewModel.class);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.app_bar_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }

    /**
     * Called after the picture is taken
     * @param requestCode requestCode of the intent
     * @param resultCode result of the intent
     * @param data data of the intent (picture)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                addViewModel.setImageBitmpap(imageBitmap);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}