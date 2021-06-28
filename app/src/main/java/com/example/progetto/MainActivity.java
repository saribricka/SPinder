package com.example.progetto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.progetto.RecyclerView.CardAdapter;
import com.example.progetto.ViewModel.AddViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import swipeable.com.layoutmanager.OnItemSwiped;
import swipeable.com.layoutmanager.SwipeableLayoutManager;
import swipeable.com.layoutmanager.SwipeableTouchHelperCallback;
import swipeable.com.layoutmanager.touchelper.ItemTouchHelper;

import static com.example.progetto.Utilities.REQUEST_IMAGE_CAPTURE;

public class MainActivity extends AppCompatActivity {

    private AddViewModel addViewModel;
    public BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            Utilities.insertFragment(this, new LoginFragment(), "LoginFragment");
        } else {
            Utilities.insertFragment(this, new MainFragment(), "MainFragment");
        }

        nav = findViewById(R.id.bottomnav);
        nav.setSelectedItemId(R.id.profileNav);
        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.messageNav:
                        Utilities.insertFragment(MainActivity.this, new ChatFragment(), "ChatFragment");
                        nav.getMenu().findItem(R.id.messageNav).setChecked(true);
                        return true;

                    case R.id.homeNav:
                        Utilities.insertFragment(MainActivity.this, new HomePageFragment(), "HomePageFragment");
                        nav.getMenu().findItem(R.id.homeNav).setChecked(true);
                        return true;

                    case R.id.profileNav:
                        if (savedInstanceState == null) {
                            Utilities.insertFragment(MainActivity.this, new LoginFragment(), "LoginFragment");
                            nav.getMenu().findItem(R.id.profileNav).setChecked(true);
                            return true;
                        } else {
                            Utilities.insertFragment(MainActivity.this, new ProfileFragment(), "ProfileFragment");
                            nav.getMenu().findItem(R.id.profileNav).setChecked(true);
                            return true;
                        }

                }
                return false;
            }
        });

        nav.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.messageNav:
                        Utilities.insertFragment(MainActivity.this, new ChatFragment(), "ChatFragment");
                        nav.getMenu().findItem(R.id.messageNav).setChecked(true);

                    case R.id.homeNav:
                        Utilities.insertFragment(MainActivity.this, new HomePageFragment(), "HomePageFragment");
                        nav.getMenu().findItem(R.id.homeNav).setChecked(true);

                    case R.id.profileNav:
                        if (savedInstanceState == null) {
                            Utilities.insertFragment(MainActivity.this, new LoginFragment(), "LoginFragment");
                            nav.getMenu().findItem(R.id.profileNav).setChecked(true);
                        } else {
                            Utilities.insertFragment(MainActivity.this, new ProfileFragment(), "ProfileFragment");
                            nav.getMenu().findItem(R.id.profileNav).setChecked(true);
                        }
                }
            }
        });

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
        getMenuInflater().inflate(R.menu.bottom_nav, menu);
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