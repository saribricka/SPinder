package com.example.progetto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static com.example.progetto.Utilities.REQUEST_IMAGE_CAPTURE;

public class ProfileFragment extends Fragment {

    private TextView placeTextView;
    private TextView dateTextView;
    private TextView descriptionTextView;
    private ImageView profileImageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        placeTextView = view.findViewById(R.id.place_name);
        dateTextView = view.findViewById(R.id.date_pic);
        descriptionTextView = view.findViewById(R.id.pic_description);
        profileImageView = view.findViewById(R.id.profile_image);

        Activity activity = getActivity();
        if (activity != null) {

            ListViewModel listViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(ListViewModel.class);
            listViewModel.getSelected().observe(getViewLifecycleOwner(), new Observer<CardItem>() {
                @Override
                public void onChanged(CardItem cardItem) {
                    placeTextView.setText(cardItem.getPlace());
                    descriptionTextView.setText(cardItem.getDescription());
                    dateTextView.setText(cardItem.getDate());
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


            view.findViewById(R.id.shareButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, v.getContext().getString(R.string.place_name) + ": " +
                            placeTextView.getText().toString() + "\n" + v.getContext().getString(R.string.place_description) + ": " +
                            descriptionTextView.getText().toString() + "\n" + v.getContext().getString(R.string.date) + ": " +
                            dateTextView.getText().toString());

                    sendIntent.setType("text/plain");
                    if (v.getContext() != null &&
                            sendIntent.resolveActivity(v.getContext().getPackageManager()) != null) {
                        v.getContext().startActivity(Intent.createChooser(sendIntent, null));
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

        }
    }

}
