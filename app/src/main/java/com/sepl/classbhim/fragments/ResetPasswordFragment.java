package com.sepl.classbhim.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.sepl.classbhim.R;
import com.sepl.classbhim.activities.AuthActivity;
import com.sepl.classbhim.classes.Variables;


public class ResetPasswordFragment extends Fragment {

    private TextView goBackBtn;
    private Button resetBtn;
    private ProgressBar progressBar;
    private EditText emailEt;
    private LinearLayout ll;
    private TextView spamTv;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);

        goBackBtn = view.findViewById(R.id.forgotPass_goBackBtn);
        resetBtn = view.findViewById(R.id.forgotPass_resetPassBtn);
        progressBar = view.findViewById(R.id.forgotPass_progressBar);
        emailEt = view.findViewById(R.id.forgotPass_email);
        ll = view.findViewById(R.id.successfulRecoverySentLinearLayout);
        spamTv = view.findViewById(R.id.txt_checkSpamFolder);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AuthActivity.currentFragment = 2;


        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new LoginFragment());
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Variables.hideKeyboard(getActivity());

                String email = emailEt.getText().toString().trim();
                if (!email.equals("")) {
                    progressBar.setVisibility(View.VISIBLE);
                    resetBtn.setText("");
                    resetBtn.setEnabled(false);

                    FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                Toast.makeText(getContext(), "Email Sent!", Toast.LENGTH_SHORT).show();

                                spamTv.setVisibility(View.VISIBLE);
                                ll.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                resetBtn.setText("RESET PASSWORD");
                                resetBtn.setEnabled(true);

                            }else {
                                Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                resetBtn.setText("RESET PASSWORD");
                                resetBtn.setEnabled(true);

                            }
                        }
                    });
                }



            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(R.id.parentFrameLayout, fragment);
        fragmentTransaction.commit();
    }
}