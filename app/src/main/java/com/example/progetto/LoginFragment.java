package com.example.progetto;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.log_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Activity activity = getActivity();

        //signup button in first login page takes me to sign up fragment
        view.findViewById(R.id.signupBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.insertFragment((AppCompatActivity) activity, new SignupFragment(), "SignupFragment");
            }
        });

        view.findViewById(R.id.loginBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.insertFragment((AppCompatActivity) activity, new ProfileFragment(), "ProfileFragment");
            }
        });

    }
}
