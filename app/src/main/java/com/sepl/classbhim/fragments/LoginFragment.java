package com.sepl.classbhim.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.errorprone.annotations.Var;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sepl.classbhim.MainActivity;
import com.sepl.classbhim.R;
import com.sepl.classbhim.activities.AuthActivity;
import com.sepl.classbhim.classes.Variables;

import org.w3c.dom.Text;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends Fragment {

    private TextView signupBtn, resetPassBtn;
    private Button loginBtn;
    private EditText emailEt, passEt;
    private String email, pass;
    private ProgressBar progressBar;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        signupBtn = view.findViewById(R.id.signupB);
        resetPassBtn = view.findViewById(R.id.forgot_pass);
        emailEt = view.findViewById(R.id.email);
        passEt = view.findViewById(R.id.password);
        loginBtn = view.findViewById(R.id.loginB);
        progressBar = view.findViewById(R.id.progressBar);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AuthActivity.currentFragment = 3;

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignupFragment());
            }
        });

        resetPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new ResetPasswordFragment());
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Variables.hideKeyboard(getActivity());

                captureTexts();

                if (email.matches(Variables.emailPattern)){
                    progressBar.setVisibility(View.VISIBLE);
                    loginBtn.setEnabled(false);
                    loginBtn.setText("");

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){

                                        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()){

                                                    DocumentSnapshot shot = task.getResult();

                                                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(getContext().getPackageName() + ".basic",MODE_PRIVATE);
                                                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                                    myEdit.putString("name", shot.get("name").toString());
                                                    myEdit.putString("email", shot.get("email").toString());
                                                    myEdit.putInt("role", Variables.STUDENT);
                                                    myEdit.putString("phone", shot.get("phone").toString());
                                                    myEdit.commit();

                                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                                    getContext().startActivity(intent);
                                                    getActivity().finish();

                                                }else {
                                                    Toast.makeText(getContext(), "Please try logging in again!", Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    loginBtn.setEnabled(true);
                                                    loginBtn.setText("Log In");
                                                }
                                            }
                                        });





                                    }else {
                                        Toast.makeText(getContext(), "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                        loginBtn.setEnabled(true);
                                        loginBtn.setText("Log In");


                                    }
                                }
                            });
                }else {
                    emailEt.setError("Please enter a valid email address!");
                }
            }
        });
    }

    private void captureTexts() {
        email = emailEt.getText().toString().trim();
        pass = passEt.getText().toString().trim();
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);
        fragmentTransaction.replace(R.id.parentFrameLayout, fragment);
        fragmentTransaction.commit();
    }


}