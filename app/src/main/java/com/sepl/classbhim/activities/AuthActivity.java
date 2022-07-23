package com.sepl.classbhim.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.sepl.classbhim.R;
import com.sepl.classbhim.fragments.LoginFragment;

public class AuthActivity extends AppCompatActivity {

    public static int currentFragment = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);


        setDefaultFragment(new LoginFragment());


    }

    private void setDefaultFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.parentFrameLayout, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {

        if (currentFragment == 1 || currentFragment == 2){
            //resetpass or signup
            setDefaultFragment(new LoginFragment());
        }else{
            super.onBackPressed();
        }
    }
}