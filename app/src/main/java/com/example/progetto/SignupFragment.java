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

public class SignupFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        final Activity activity = getActivity();

        Button proceedBt = view.findViewById(R.id.reg_proceed);

        proceedBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utilities.insertFragment((AppCompatActivity) activity, new SignupTwoFragment(), "SignupTwoFragment");
            }
        });
    }
}
