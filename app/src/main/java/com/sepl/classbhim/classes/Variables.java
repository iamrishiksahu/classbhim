package com.sepl.classbhim.classes;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sepl.classbhim.classes.models.UserModel;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Variables {

    //USER ROLES
    public static final int SUPER_ADMIN = 0, SCHOOL_ADMIN = 1, CLASS_ADMIN = 2, STUDENT = 3;

    //RELATED TO JOIN REQUESTS
    public static final int CHARACTER_COUNT_IN_SCHOOL_ID = 4, INACTIVE = 0, ACTIVE =1, REJECTED = 2 ;

    //RELATED TO QUESTION ATTEMPT
    public static final int UNATTEMPTED = 5 , NOTVISITED = 2, CORRECT = 0, INCORRECT = 1;

    //RELATED TO NOTIFICATIONS
    public static final String CLASS_NOTICES_CHANNEL_ID = "channel_class", GENERAL_NOTIFICATION_CHANNEL_ID = "channel_general";

    //RANDOM
    public static boolean IS_NEW_TOKEN_GENERATED;
    public static String fcmToken;
    public static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    public static UserModel localUserData;


    //DEBUG
    public static String TAG = "MY_ADMIN_DEBUG";


    public static FirebaseAuth firebaseAuth;
    public static FirebaseFirestore firebaseFirestore;

    public static void hideKeyboard(Activity passActivityHere){
        try {
            InputMethodManager imm = (InputMethodManager) passActivityHere.getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(passActivityHere.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


}

