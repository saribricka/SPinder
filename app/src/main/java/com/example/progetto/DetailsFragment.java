package com.example.progetto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.progetto.ViewModel.ListViewModel;

public class DetailsFragment extends Fragment {

    private TextView placeTextView;
    private TextView dateTextView;
    private TextView descriptionTextView;
    private ImageView placeImageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        placeTextView = view.findViewById(R.id.place_name);
        descriptionTextView = view.findViewById(R.id.place_description);
        dateTextView = view.findViewById(R.id.travel_date);
        placeImageView = view.findViewById(R.id.place_image);

        Activity activity = getActivity();
        if (activity != null) {
            Utilities.setUpToolbar((AppCompatActivity) activity, "Details");

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
                        placeImageView.setImageDrawable(drawable);
                    } else {
                        Bitmap bitmap = Utilities.getImageBitmap(activity, Uri.parse(image_path));
                        if (bitmap != null){
                            placeImageView.setImageBitmap(bitmap);
                            placeImageView.setBackgroundColor(Color.WHITE);
                        }
                    }
                }
            });


            view.findViewById(R.id.shareButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, v.getContext().getString(R.string.user_name) + ": " +
                            placeTextView.getText().toString() + "\n" + v.getContext().getString(R.string.bio_description) + ": " +
                            descriptionTextView.getText().toString() + "\n" + v.getContext().getString(R.string.age) + ": " +
                            dateTextView.getText().toString());

                    sendIntent.setType("text/plain");
                    if (v.getContext() != null &&
                            sendIntent.resolveActivity(v.getContext().getPackageManager()) != null) {
                        v.getContext().startActivity(Intent.createChooser(sendIntent, null));
                    }
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.findItem(R.id.app_bar_search).setVisible(false);
    }
}
