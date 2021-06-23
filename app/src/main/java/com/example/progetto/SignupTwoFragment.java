package com.example.progetto;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.progetto.ViewModel.AddViewModel;
import com.example.progetto.ViewModel.ListViewModel;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.progetto.Utilities.REQUEST_IMAGE_CAPTURE;

public class SignupTwoFragment extends Fragment {

    private TextView placeTextView;
    private TextView dateTextView;
    private TextView descriptionTextView;
    private ImageView profileImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.reg_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        final Activity activity = getActivity();

        placeTextView = view.findViewById(R.id.placeTextInputEditText);
        dateTextView = view.findViewById(R.id.dateTextInputEditText);
        descriptionTextView = view.findViewById(R.id.descriptionTextInputEditText);
        ImageView profileImageView = view.findViewById(R.id.imageView);

        ListViewModel listViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(ListViewModel.class);
        listViewModel.getSelected().observe(getViewLifecycleOwner(), new Observer<CardItem>() {
            @Override
            public void onChanged(CardItem cardItem) {
                placeTextView.setText(cardItem.getPlace());
                dateTextView.setText(cardItem.getDate());
                descriptionTextView.setText(cardItem.getDescription());
                String image_path = cardItem.getImageResource();
                if (image_path.contains("ic_")) {
                    Drawable drawable = ContextCompat.getDrawable(activity, activity.getResources()
                            .getIdentifier(image_path, "drawable",
                                    activity.getPackageName()));
                    profileImageView.setImageDrawable(drawable);
                } else {
                    Bitmap bitmap = Utilities.getImageBitmap(activity, Uri.parse(image_path));
                    if (bitmap != null) {
                        profileImageView.setImageBitmap(bitmap);
                        profileImageView.setBackgroundColor(Color.WHITE);
                    }
                }
            }
        });

        view.findViewById(R.id.captureButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there is a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        ImageView imageView = view.findViewById(R.id.imageView);
        AddViewModel addViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(AddViewModel.class);
        addViewModel.getBitmap().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });

        Button profileBt = view.findViewById(R.id.go_to_profile);
        profileBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utilities.insertFragment((AppCompatActivity) activity, new ProfileFragment(), "ProfileFragment");
            }
        });

        view.findViewById(R.id.fab_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bitmap bitmap = addViewModel.getBitmap().getValue();
                    String imageUriString;
                    if (bitmap != null) {
                        //method to save the image in the gallery of the device
                        imageUriString = String.valueOf(saveImage(bitmap, activity));
                        //Toast.makeText(activity,"Image Saved", Toast.LENGTH_SHORT).show();
                    } else {
                        imageUriString = "ic_logo";
                    }

                    addViewModel.addCardItem(new CardItem(imageUriString,
                            placeTextView.getText().toString(),
                            dateTextView.getText().toString(),
                            descriptionTextView.getText().toString()));

                    addViewModel.setImageBitmpap(null);

                    ((AppCompatActivity) activity).getSupportFragmentManager().popBackStack();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Method called to save the image taken as a file in the gallery
     * @param bitmap the image taken
     * @throws IOException if there are some issue with the creation of the image file
     * @return the Uri of the image saved
     */
    private Uri saveImage(Bitmap bitmap, Activity activity) throws IOException {
        // Create an image file name
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ITALY).format(new Date());
        String name = "JPEG_" + timeStamp + "_.png";

        ContentResolver resolver = activity.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name + ".jpg");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Log.d("LAB-AddFragment", String.valueOf(imageUri));
        OutputStream fos = resolver.openOutputStream(imageUri);

        //for the jpeg quality, it goes from 0 to 100
        //for the png, the quality is ignored
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        if (fos != null) {
            fos.close();
        }
        return imageUri;
    }


}
