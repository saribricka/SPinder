package com.example.progetto;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class SignupTwoFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.reg_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        final Activity activity = getActivity();

        Button profileBt = view.findViewById(R.id.go_to_profile);

        profileBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utilities.insertFragment((AppCompatActivity) activity, new ProfileFragment(), "ProfileFragment");
            }
        });
    }
}
