package com.sepl.classbhim.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.errorprone.annotations.Var;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sepl.classbhim.MainActivity;
import com.sepl.classbhim.R;
import com.sepl.classbhim.activities.AuthActivity;
import com.sepl.classbhim.classes.Variables;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.sepl.classbhim.classes.Variables.emailPattern;
import static com.sepl.classbhim.classes.Variables.firebaseAuth;
import static com.sepl.classbhim.classes.Variables.firebaseFirestore;


public class SignupFragment extends Fragment {

    private ImageView backBtn;
    private EditText nameEt, emailEt, passEt, confirmPassEt, phoneEt;
    private Button signUpB;
    private String name, email, pass, cnfPass, phone, fcmToken;
    private ProgressBar progressBar;


    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore firebaseFirestore;

    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        backBtn = view.findViewById(R.id.backB);
        nameEt = view.findViewById(R.id.usernme);
        emailEt = view.findViewById(R.id.email);
        passEt = view.findViewById(R.id.password);
        confirmPassEt = view.findViewById(R.id.confirm_pass);
        signUpB = view.findViewById(R.id.signupB);
        phoneEt = view.findViewById(R.id.phone);
        progressBar = view.findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AuthActivity.currentFragment = 1;


        signUpB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                captureTextFromET();
                Variables.hideKeyboard(getActivity());

                if (checkInputs()){
                    checkEmailAndPassword();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new LoginFragment());
            }
        });
    }


    private boolean checkInputs() {
        if (!TextUtils.isEmpty(name)){
            if(!TextUtils.isEmpty(email)){
                if (!TextUtils.isEmpty(phone)) {
                    if (!TextUtils.isEmpty(pass) && pass.length() >=6){
                        if (!TextUtils.isEmpty(cnfPass)){
                            return true;
                        }else {
                            confirmPassEt.setError("Please Confirm Your Password!");
                            return false;
                        }

                    }else{
                        passEt.setError("Please Enter a Password!");
                        return false;

                    }
                }else{
                    phoneEt.setError("Please Enter Your Phone Number");
                    return false;

                }
            }else {
                emailEt.setError("Please Enter Your Email Address!");
                return false;

            }
        } else {
            nameEt.setError("Please Enter Your Name!");
            return false;


        }
    }

    private void captureTextFromET(){

        name = nameEt.getText().toString().trim();
        email = emailEt.getText().toString().trim();
        pass = passEt.getText().toString().trim();
        cnfPass = confirmPassEt.getText().toString().trim();
        phone = phoneEt.getText().toString().trim();

    }

    private void checkEmailAndPassword(){
        if (email.matches(emailPattern)){
            if (cnfPass.equals(pass)){

                progressBar.setVisibility(View.VISIBLE);
                signUpB.setEnabled(false);
                signUpB.setText("");

                firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){

                                    Map<String,Object> userdata = new HashMap<>();
                                    userdata.put("name",name);
                                    userdata.put("email",email);
                                    userdata.put("phone",phone);
                                    userdata.put("fcm_token", "");
                                    userdata.put("role", Variables.STUDENT);
                                    userdata.put("created_at",new Date());
                                    userdata.put("updated_at",new Date());

                                    List<String> emptyList = new ArrayList<>();

                                    Map<String, Object> map = new HashMap<>();
                                    map.put("test_ids", emptyList);

                                    Map<String, Object> map2 = new HashMap<>();
                                    map2.put("ignore", "");

                                    firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                                            .collection("USER_DATA").document("attemptedTests")
                                            .set(map);

                                    firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                                            .collection("JOINED_CLASSES").document("ignore")
                                            .set(map2);

                                    firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                                            .set(userdata)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){

                                                        sendFCMTOken();
                                                        saveDataToSharedPref();
                                                        mainIntent();
                                                    }else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    signUpB.setEnabled(true);
                                    signUpB.setText("Sign Up");
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else {
                confirmPassEt.setError("Confirm Password does not matches with Password!");

            }
        }else {
            emailEt.setError("Please Enter a Valid Email Address!");

        }

    }

    private void saveDataToSharedPref() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getContext().getPackageName() + ".basic",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("name", name);
        myEdit.putString("email", email);
        myEdit.putInt("role", Variables.STUDENT);
        myEdit.putString("phone", phone);
        myEdit.commit();
    }

    private void mainIntent() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        getContext().startActivity(intent);
        getActivity().finish();
    }

    private void sendFCMTOken() {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String token = "";
                if (task.isSuccessful()){
                    token = task.getResult();

                }else {
                    token ="NA";
                }


                firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                        .update("fcm_token", token);
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