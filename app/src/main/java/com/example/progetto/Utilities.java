package com.example.progetto;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.InputStream;

public class Utilities {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    static void setUpToolbar(AppCompatActivity activity, String title) {
        Toolbar toolbar = activity.findViewById(R.id.topAppBar);
        toolbar.setTitle(title);

        if (activity.getSupportActionBar() == null){
            //Set a Toolbar to act as the ActionBar for the Activity
            activity.setSupportActionBar(toolbar);
        }
    }

    static void insertFragment(AppCompatActivity activity, Fragment fragment, String tag) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container_view with this fragment,
        transaction.replace(R.id.fragment_container_view, fragment, tag);

        //add the transaction to the back stack so the user can navigate back
        if (!(fragment instanceof MainFragment) && !(fragment instanceof SettingsFragment) &&
                !(fragment instanceof LoginFragment) && !(fragment instanceof SignupFragment) &&
                    !(fragment instanceof  SmsFragment) && !(fragment instanceof ChatFragment)) {
            transaction.addToBackStack(tag);
        }

        // Commit the transaction
        transaction.commit();
    }

    public static Bitmap getImageBitmap(Activity activity, Uri currentPhotoUri){
        ContentResolver resolver = activity.getApplicationContext()
                .getContentResolver();
        try {
            InputStream stream = resolver.openInputStream(currentPhotoUri);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static void displayRoundImageFromUrl(Context context, String profileUrl, ImageView profileImage) {
    }
}
