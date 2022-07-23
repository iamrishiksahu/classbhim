package com.sepl.classbhim.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.errorprone.annotations.Var;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sepl.classbhim.MainActivity;
import com.sepl.classbhim.R;
import com.sepl.classbhim.classes.Variables;
import com.sepl.classbhim.classes.models.UserModel;

import static com.sepl.classbhim.classes.Variables.firebaseAuth;
import static com.sepl.classbhim.classes.Variables.firebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private ImageView logoIv;
    private Animation logoAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        logoIv = findViewById(R.id.logoIv);

        logoAnim = AnimationUtils.loadAnimation(this,R.anim.splash_top_animation);

        logoIv.setAnimation(logoAnim);

//        SharedPreferences sh = getSharedPreferences(getPackageName() +".basic", MODE_PRIVATE);
//        String name = sh.getString("name", "");
//        String email = sh.getString("email", "");
//        String phone = sh.getString("phone", "");
//        String token = sh.getString("fcm_token", "");
//        int role = sh.getInt("role", Variables.STUDENT);

//        Variables.localUserData = new UserModel(name, email, phone,  token, new Long(role));

        new Thread(){

            @Override
            public void run()
            {
                try {
                    sleep(3000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

                if (firebaseAuth.getCurrentUser() !=null){

                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();

                }else {
                    Intent intent = new Intent(SplashActivity.this,AuthActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }


            }
        }.start();

    }
}