package com.example.progetto.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.progetto.R;

/**
 * A ViewHolder describes an item view and the metadata about its place within the RecyclerView.
 */
public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    ImageView imageCardView;
    TextView nameTextView;
    TextView birthdayTextView;
    TextView placeTextView;
    TextView bio_descriptionTextView;

    private OnItemListener itemListener;

    CardViewHolder(@NonNull View itemView, OnItemListener lister) {
        super(itemView);
        imageCardView = itemView.findViewById(R.id.profile_image);
        nameTextView = itemView.findViewById(R.id.nameTextView);
        birthdayTextView = itemView.findViewById(R.id.date_pic);
        placeTextView = itemView.findViewById(R.id.place_name);
        bio_descriptionTextView = itemView.findViewById(R.id.pic_description);
        itemListener = lister;

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemListener.onItemClick(getAdapterPosition());
    }
}
