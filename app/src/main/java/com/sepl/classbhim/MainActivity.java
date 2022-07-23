package com.sepl.classbhim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.Image;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.errorprone.annotations.Var;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sepl.classbhim.activities.AuthActivity;
import com.sepl.classbhim.activities.ClassActivity;
import com.sepl.classbhim.activities.NotificationsActivity;
import com.sepl.classbhim.activities.QuestionDisplayActivity;
import com.sepl.classbhim.activities.TestSetsActivity;
import com.sepl.classbhim.activities.ViewResultActivity;
import com.sepl.classbhim.classes.LocalDatabase;
import com.sepl.classbhim.classes.PosterSliderView;
import com.sepl.classbhim.classes.Variables;
import com.sepl.classbhim.classes.adapters.MainRVAdapter;
import com.sepl.classbhim.classes.models.MainRvModel;
import com.sepl.classbhim.classes.models.NotificationModel;
import com.sepl.classbhim.classes.models.QuestionsModel;
import com.sepl.classbhim.classes.models.UserModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.sepl.classbhim.classes.Variables.ACTIVE;
import static com.sepl.classbhim.classes.Variables.CHARACTER_COUNT_IN_SCHOOL_ID;
import static com.sepl.classbhim.classes.Variables.INACTIVE;
import static com.sepl.classbhim.classes.Variables.REJECTED;
import static com.sepl.classbhim.classes.Variables.TAG;
import static com.sepl.classbhim.classes.Variables.UNATTEMPTED;
import static com.sepl.classbhim.classes.Variables.firebaseAuth;
import static com.sepl.classbhim.classes.Variables.firebaseFirestore;

public class MainActivity extends AppCompatActivity {

    public static DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private androidx.appcompat.widget.Toolbar toolbar;
    private ConstraintLayout rootCl, noClassesTv, mainClContainer ,subjectsBtn, noticeBoardBtn, classDetailsLayout;

    private Dialog addClassDialog;
    private Button addClassBtn;
    private TextView  versionTv, bioTv, schoolNameTv, classNameTv, statusTv;
    private ImageView schoolLogoIv;

    private List<String> bannersList;
    private ViewPager viewPager;
    private List<QuestionsModel> demoTestList;

    private String classIdGlobal="", schoolIdGlobal="";
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeItems();
        setUpDrawer();
        configureNavigationItem();
        setClickListeners();
        getAllClasses(true);
        configureNotificationSystem();
        sendFcmTokenToDB();

    }

    private void sendFcmTokenToDB(){

        if (Variables.IS_NEW_TOKEN_GENERATED){
            FirebaseFirestore.getInstance().collection("USERS")
                    .document(FirebaseAuth.getInstance().getUid())
                    .update("fcm_token", Variables.fcmToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Log.d(TAG, "fc_token sent success");
                    }else {
                        Log.d(TAG, "fc_token sending failed: " + task.getException().getMessage());

                    }
                }
            });
        }
    }

    private void configureNavigationItem() {

        navigationView.setItemIconTintList(null);
//        navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryDark, getTheme())));
        navigationView.getMenu().getItem(0).setChecked(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            MenuItem menuItem;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                menuItem = item;

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.nav_demo_test:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        prepareForDemoTest();
                        break;

                    case R.id.nav_results:

                        if (schoolIdGlobal.equals("")){
                            Toast.makeText(MainActivity.this, "Please join a class to view your result!", Toast.LENGTH_SHORT).show();
                        }else {
                            Intent aIntent = new Intent(MainActivity.this, ViewResultActivity.class);
                            aIntent.putExtra("school_id", schoolIdGlobal);
                            aIntent.putExtra("class_id", classIdGlobal);
                            drawerLayout.closeDrawer(GravityCompat.START);

                            startActivity(aIntent);
                        }

                        break;


                    case R.id.nav_rate:

                        final String appPackageName = getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.nav_share:

                        try {
                            Intent myIntent = new Intent(Intent.ACTION_SEND);
                            myIntent.setType("text/plain");
                            String body = "https://play.google.com/store/apps/details?id=" + getPackageName();
                            String sub = "";
                            myIntent.putExtra(Intent.EXTRA_SUBJECT,sub);
                            myIntent.putExtra(Intent.EXTRA_TEXT,body);
                            drawerLayout.closeDrawer(GravityCompat.START);
                            startActivity(Intent.createChooser(myIntent, "Share Using"));
                            drawerLayout.closeDrawer(GravityCompat.START);

                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, "This service is currently unavailable!", Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case R.id.nav_privacy:

                        try {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)));
                            startActivity(browserIntent);
                            drawerLayout.closeDrawer(GravityCompat.START);

                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, "This service is currently unavailable!", Toast.LENGTH_SHORT).show();

                        }

                        break;

                    case R.id.nav_logout:


                        onUserLogout();

                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Toast.makeText(MainActivity.this, "Successfully Logged Out!", Toast.LENGTH_SHORT).show();

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        finish();

                        break;


                }

                return true;
            }

        });

    }

    private void prepareForDemoTest() {

        demoTestList = new ArrayList<>();

        demoTestList.add(new QuestionsModel("Lion", "Tiger", "Cheetah", "Dinosourous",1L, "Which animal is the king of jungle?", 1D, 4D, new Long(UNATTEMPTED), false, new Long(UNATTEMPTED)));
        demoTestList.add(new QuestionsModel("PV Sindhu", "Niraj Chopra", "Sania Mirza", "Salman Khan",2L, "Who won gold medal in Javelin Throw at Tokyo Olympics?", 1D, 4D, new Long(UNATTEMPTED), false, new Long(UNATTEMPTED)));
        demoTestList.add(new QuestionsModel("Chennai", "Mumbai", "Delhi", "Noida",3L, "What is the capital of India?", 1D, 4D, new Long(UNATTEMPTED), false, new Long(UNATTEMPTED)));
        demoTestList.add(new QuestionsModel("Mukesh Ambani", "Dhiru Bhai Ambani", "Sir Ratan Tata", "Radhakishan Damani",1L, "Who is the richest person in India?", 1D, 4D, new Long(UNATTEMPTED), false, new Long(UNATTEMPTED)));
        demoTestList.add(new QuestionsModel("Monitor", "Keyboard", "Mirror", "Mouse",3L, "Which of the following is not a computer peripheral device?", 1D, 4D, new Long(UNATTEMPTED), false, new Long(UNATTEMPTED)));
        demoTestList.add(new QuestionsModel("Salman Khan", "Prabhas", "Vijay Mallya", "Narendra Kant",2L, "Who is the lead male actor in Bahubali movie?", 1D, 4D, new Long(UNATTEMPTED), false, new Long(UNATTEMPTED)));
        demoTestList.add(new QuestionsModel("1", "3", "8", "6",4L, "How many faces are there in a dice?", 1D, 4D, new Long(UNATTEMPTED), false, new Long(UNATTEMPTED)));
        demoTestList.add(new QuestionsModel("25", "23", "26", "24",3L, "How many letters are there in english alphabet?", 1D, 4D, new Long(UNATTEMPTED), false, new Long(UNATTEMPTED)));
        demoTestList.add(new QuestionsModel("1946", "1947", "1948", "1949",2L, "In which year India got independence?", 1D, 4D, new Long(UNATTEMPTED), false, new Long(UNATTEMPTED)));
        demoTestList.add(new QuestionsModel("Mahatma Gandhi", "Manmohan Singh", "Narendra Modi", "Vijay Shekhar Sharma",1L, "Whose photograph is printed on Indian currency notes?", 1D, 4D, new Long(UNATTEMPTED), false, new Long(UNATTEMPTED)));

        Intent intent = new Intent(MainActivity.this, QuestionDisplayActivity.class);
        intent.putExtra("time", 10);
        intent.putExtra("marks", 40);
        intent.putExtra("id", "demoTest");
        intent.putExtra("set_title", "Demo Test");


        QuestionDisplayActivity.questionList = demoTestList;
        startActivity(intent);
    }


    private void setClassLayout(String className, String schoolName, int status, String logoUrl, String classId, String schoolId){

        Glide.with(this).load(logoUrl).transition(GenericTransitionOptions.with(R.anim.fade_in)).apply(new RequestOptions().placeholder(R.drawable.school_logo_placeholder)).into(schoolLogoIv);
        classNameTv.setText(className);
        schoolNameTv.setText(schoolName);

        boolean isActive = false;
        if (status == INACTIVE){
            statusTv.setText("Request Sent");
        }else if (status == ACTIVE){
            statusTv.setText("Active");
            isActive = true;
        }else if (status == REJECTED){
            statusTv.setText("Rejected");
        }

        setClickListenerOnClassItem(isActive, classId, schoolId, className);
    }

    private void setClickListenerOnClassItem(boolean isActive, String classId, String schoolId, String className){

        subjectsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isActive){

                    Intent intent = new Intent(MainActivity.this, ClassActivity.class);
                    intent.putExtra("class_id", classId);
                    intent.putExtra("school_id", schoolId);
                    intent.putExtra("class_name", className);
                    startActivity(intent);
                }else {

                    //this class isnt active


                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Inactive Class!");
                    builder.setMessage("This class is not active, please ask your teacher to accept your join request.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black,getTheme()));
                        }
                    });
                    dialog.show();

                }
            }
        });

        noticeBoardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isActive){
                    Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
                    intent.putExtra("class_id", classId);
                    intent.putExtra("school_id", schoolId);
                    intent.putExtra("class_name", className);
                    startActivity(intent);
                }else {
                    //This class is not active

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Inactive Class!");
                    builder.setMessage("This class is not active, please ask your teacher to accept your join request.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black,getTheme()));
                        }
                    });
                    dialog.show();
                }
            }
        });
    }

    private void onUserLogout(){
        SharedPreferences preferences =getSharedPreferences(getPackageName() + ".basic", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        SharedPreferences banner =getSharedPreferences(getPackageName() + ".banners", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = banner.edit();
        editor1.clear();
        editor1.apply();

        SharedPreferences preferences1 =getSharedPreferences(getPackageName() + ".classdata", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = preferences1.edit();
        editor2.clear();
        editor2.apply();

        LocalDatabase db = LocalDatabase.getDbInstance(MainActivity.this);
        db.mainRvDao().deleteAll();
        db.notificationDao().deleteAll();

    }

    private void configureNotificationSystem() {

        createNotificationChannel();

        subscribeUserToTopic();
    }

    private void setClickListeners() {

        addClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!refreshLayout.isRefreshing()){
                    showAddClassDialog();
                }
            }
        });
    }

    private void showAddClassDialog(){

        addClassDialog = new Dialog(this);
        addClassDialog.setContentView(R.layout.add_class_dialog_layout);
        addClassDialog.setCancelable(true);
        addClassDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button addBtn = addClassDialog.findViewById(R.id.enterClassBtn);
        ProgressBar prog = addClassDialog.findViewById(R.id.progressBar);
        EditText et = addClassDialog.findViewById(R.id.classIdEt);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String classID = et.getText().toString().toUpperCase().trim();
                if (classID.length() >= 8){

                    prog.setVisibility(View.VISIBLE);
                    addBtn.setText("");

                    if (!isClassAlreadyJoined(classID)){
                        sendAddClassRequest(classID);
                    }else{
                        prog.setVisibility(View.INVISIBLE);
                        addBtn.setText("Add Class");
                        Toast.makeText(MainActivity.this, "This class is already joined or requested to join!", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(MainActivity.this, "Please enter a valid class code.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        addClassDialog.show();
    }

    private void fetchBanners(String schoolId){

        String school = "" + schoolId;
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("SCHOOLS").document(school)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){


                    try {
                        List<String> bannerUrls = (List<String>) task.getResult().get("banners");

                        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + ".banners",MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();

                        myEdit.putInt("totalBanners", bannerUrls.size());

                        for (int i = 0; i < bannerUrls.size(); i++){
                            myEdit.putString("banner_" + i, bannerUrls.get(i));
                        }

                        myEdit.commit();

                    }catch (Exception e){

                    }


                    getBannersFromSharePrefs();

                }else{

                }
            }
        });

    }

    private void sendAddClassRequest(String classId){

        final String schoolId = classId.substring(0,CHARACTER_COUNT_IN_SCHOOL_ID).toUpperCase();
        final String requestID = UUID.randomUUID().toString().substring(0,9);

        firebaseFirestore.collection("SCHOOLS")
                .document(schoolId).collection("CLASS")
                .document(classId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot shot = task.getResult();

                    if (shot.exists()){

                        String classNameee = task.getResult().get("class_name").toString();
                        String schoolNameee = task.getResult().get("school_name").toString();
                        String schoolLogooo = task.getResult().get("school_logo").toString();

                        SharedPreferences shared = getSharedPreferences(getPackageName() + ".basic", MODE_PRIVATE);
                        String name = (shared.getString("name", ""));

                        Map<String, Object> map = new HashMap<>();
                        map.put("request_id", requestID);
                        map.put("is_approved", false);
                        map.put("is_rejected", false);
                        map.put("student_name", name );
                        map.put("requested_at", new Date());
                        map.put("requested_class_id", classId);
                        map.put("requester_user_id", FirebaseAuth.getInstance().getUid());

                        firebaseFirestore.collection("SCHOOLS")
                                .document(schoolId)
                                .collection("JOIN_REQUESTS")
                                .document(requestID).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){


                                    Map<String, Object> send = new HashMap<>();
                                    send.put("classId", classId);
                                    send.put("schoolName", schoolNameee);
                                    send.put("schoolLogo", schoolLogooo);
                                    send.put("isActivated", false);
                                    send.put("isRejected", false);
                                    send.put("requestId", requestID);
                                    send.put("time", String.valueOf(new Date()));
                                    send.put("className", classNameee);

                                    onRequestSentSuccess(classId, requestID, classNameee, schoolNameee, schoolLogooo,send, schoolId);
                                }else {
                                    onRequestSentFailed();
                                }
                            }
                        });



                    }else {
                        Toast.makeText(MainActivity.this, "This is not a valid class ID!", Toast.LENGTH_SHORT).show();
                        addClassDialog.dismiss();

                    }


                }else{
                    addClassDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Something went wrong! Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void onRequestSentSuccess(String id, String reqId, String className, String schoolName,String schoolLogo, Map<String, Object> map, String schoolId){

        addClassToDB(id,reqId, true, schoolName , schoolLogo, className, schoolId, INACTIVE, map);

        Toast.makeText(MainActivity.this, "Request Sent Successfully!", Toast.LENGTH_SHORT).show();
        addClassDialog.dismiss();

    }

    private boolean isClassAlreadyJoined(String classID){

        LocalDatabase db = LocalDatabase.getDbInstance(this);
        List<MainRvModel> list = db.mainRvDao().getAllClasses();

        boolean a =false;

        for(int i = 0; i<list.size(); i++){
            if( list.get(i).getClassId().equals(classID) ){

                a = true;
                break;
            }
        }
        return a;
    }

    private void getBannersFromSharePrefs(){

        try{
            SharedPreferences shared = getSharedPreferences(getPackageName() + ".banners", MODE_PRIVATE);
            int size = (shared.getInt("totalBanners", 0));


            for (int i =0 ; i< size; i++){
                bannersList.add(shared.getString("banner_" + i, ""));
            }

            if (!bannersList.isEmpty()) {
                viewPager.setVisibility(View.VISIBLE);
                PosterSliderView view = new PosterSliderView(rootCl);
                view.setPosters(bannersList);
            }else {
                viewPager.setVisibility(View.GONE);
            }

        }catch (Exception e){

        }
    }

    private void getAllClasses(boolean isFirstTime){

        SharedPreferences shared = getSharedPreferences(getPackageName() + ".classdata", MODE_PRIVATE);
        String classId = (shared.getString("classId", ""));
        String schoolId = (shared.getString("schoolId", ""));
        String className = (shared.getString("className", ""));
        String schoolName = (shared.getString("schoolName", ""));
        String schoolLogo = (shared.getString("schoolLogo", ""));
        String reqId = (shared.getString("requestId", ""));
        int status = (shared.getInt("status", INACTIVE));

        this.schoolIdGlobal =schoolId;
        this.classIdGlobal =classId;

        if (isFirstTime) {
            if (!classId.equals("")) {
                noClassesTv.setVisibility(View.GONE);
                classDetailsLayout.setVisibility(View.VISIBLE);

                getBannersFromSharePrefs();

                if (status == INACTIVE) {

                        firebaseFirestore.collection("USERS")
                                .document(FirebaseAuth.getInstance().getUid()).collection("JOINED_CLASSES")
                                .document(reqId)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot shot = task.getResult();

                                    try {
                                        boolean isAccepted = (boolean) shot.get("isActivated");
                                        boolean isRejected = (boolean) shot.get("isRejected");

                                        int status = INACTIVE;

                                        if (isAccepted) {

                                            status = ACTIVE;

                                            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + ".classdata",MODE_PRIVATE);
                                            SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                            myEdit.putInt("status", ACTIVE);
                                            myEdit.apply();

                                        }

                                        if (isRejected) {

                                            status = REJECTED;

                                            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + ".classdata",MODE_PRIVATE);
                                            SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                            myEdit.putInt("status", REJECTED);
                                            myEdit.apply();


                                        }

                                        setClassLayout(className, schoolName, status, schoolLogo, classId, schoolId);

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.d(TAG, "Unable to fetch request approval details: " + task.getException().getMessage());
                                }
                            }
                        });
                }else if (status == ACTIVE){
                    setClassLayout(className, schoolName, status, schoolLogo, classId, schoolId);
                }else {
                    setClassLayout(className, schoolName, status, schoolLogo, classId, schoolId);
                }


            }
            else {

                //no class present in the shared pref
                //check in the firebase
                noClassesTv.setVisibility(View.VISIBLE);
                classDetailsLayout.setVisibility(View.GONE);

                FirebaseFirestore.getInstance().collection("USERS")
                        .document(FirebaseAuth.getInstance().getUid())
                        .collection("JOINED_CLASSES")
                        .orderBy("time")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){

                            for (QueryDocumentSnapshot shot : task.getResult()) {

                                String classId = shot.get("classId").toString();
                                String className = shot.get("className").toString();
                                String schoolName = shot.get("schoolName").toString();
                                String schoolLogo = shot.get("schoolLogo").toString();
                                String requestId = shot.get("requestId").toString();
//                                long longTime = Long.parseLong(shot.get("time").toString());
                                boolean isActivated = (boolean) shot.get("isActivated");
                                boolean isRejected= (boolean) shot.get("isRejected");
                                String schoolId = classId.substring(0,CHARACTER_COUNT_IN_SCHOOL_ID);

                                int status;
                                if(isActivated){
                                    status = ACTIVE;
                                }else if (isRejected){
                                    status = REJECTED;
                                }else {
                                    status = INACTIVE;
                                }

                                    SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + ".classdata",MODE_PRIVATE);
                                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                    myEdit.putString("schoolName", schoolName);
                                    myEdit.putString("className", className);
                                    myEdit.putString("requestId", requestId);
                                    myEdit.putString("classId", classId);
                                    myEdit.putString("schoolId", schoolId);
                                    myEdit.putString("schoolLogo", schoolLogo);
                                    myEdit.putInt("status", status);
                                    myEdit.commit();



                                Map<String, Object> map = new HashMap<>();
                                addClassToDB(classId, requestId, false, schoolName,schoolLogo, className, schoolId, status, map);

                            }

                            getAllClasses(false);


                        }else {
                            Toast.makeText(MainActivity.this, "Could not load the joined classes: "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });


                noClassesTv.setVisibility(View.VISIBLE);
                classDetailsLayout.setVisibility(View.GONE);

            }
        }
        else {
            if ((!classId.equals(""))){

                noClassesTv.setVisibility(View.INVISIBLE);
                classDetailsLayout.setVisibility(View.VISIBLE);

                getBannersFromSharePrefs();

                setClassLayout(className, schoolName, status, schoolLogo, classId, schoolId);
            }
        }

        if(refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
    }

    private void addClassToDB(String classId, String reqID, boolean isRequiredFetching, String schoolName, String schoolLogo, String className, String schoolId, int status, Map<String, Object> map){

        if (isRequiredFetching){


            FirebaseFirestore.getInstance().collection("USERS")
                    .document(FirebaseAuth.getInstance().getUid())
                    .collection("JOINED_CLASSES").document(reqID)
                    .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + ".classdata",MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("schoolName", schoolName);
                        myEdit.putString("className", className);
                        myEdit.putString("requestId", reqID);
                        myEdit.putString("classId", classId);
                        myEdit.putString("schoolId", schoolId);
                        myEdit.putString("schoolLogo", schoolLogo);
                        myEdit.putInt("status", status);
                        myEdit.commit();

                        fetchBanners(schoolId);

                        getAllClasses(false);
                    }else {
                        Toast.makeText(MainActivity.this, "Error while adding class to DB: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });


        }
        else {

            fetchBanners(schoolId);

            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + ".classdata",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("schoolName", schoolName);
            myEdit.putString("className", className);
            myEdit.putString("requestId", reqID);
            myEdit.putString("classId", classId);
            myEdit.putString("schoolId", schoolId);
            myEdit.putString("schoolLogo", schoolLogo);
            myEdit.putInt("status", status);
            myEdit.commit();

            setClassLayout(className, schoolName, status, schoolLogo, classId, schoolId);

        }




    }

    private void onRequestSentFailed(){
        //todo: notify support team
        Toast.makeText(MainActivity.this, "Request sending failed! Please try again later!", Toast.LENGTH_LONG).show();
        addClassDialog.dismiss();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            //for general notifications
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            CharSequence name = "General";
            String description = "General Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(Variables.GENERAL_NOTIFICATION_CHANNEL_ID, name,
                    importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setSound(defaultSoundUri, audioAttributes);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);


            //for class notices
            AudioAttributes audioAttributes1 = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            Uri defaultSoundUri1= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            CharSequence name1 = "Class Notices";
            String description1 = "Important class notifications";
            int importance1 = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel1 = new NotificationChannel(Variables.GENERAL_NOTIFICATION_CHANNEL_ID, name1,
                    importance1);
            channel.setDescription(description1);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setSound(defaultSoundUri1, audioAttributes1);

            NotificationManager notificationManager1 = getSystemService(NotificationManager.class);
            notificationManager1.createNotificationChannel(channel1);
        }
    }

    private void subscribeUserToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic("allLoggedIn").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                }else {

                }
            }
        });
    }
    private void initializeItems() {
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.mainNavView);
        toolbar = findViewById(R.id.activity_main_toolbar);
        addClassBtn = findViewById(R.id.addClassBtn);
        noClassesTv = findViewById(R.id.notAddedInAnyClassText);
        versionTv = findViewById(R.id.appVersionTv);
        bioTv = navigationView.getHeaderView(0).findViewById(R.id.bioTv);
        rootCl = findViewById(R.id.rootMainL);
        viewPager = findViewById(R.id.poster_slider_view_pager);
        statusTv = findViewById(R.id.statusTv);
        schoolNameTv = findViewById(R.id.schoolNameTv);
        schoolLogoIv = findViewById(R.id.schoolLogo);
        classNameTv = findViewById(R.id.classNameTv);
        refreshLayout = findViewById(R.id.swipeRefreshLayout);
        classDetailsLayout = findViewById(R.id.classDetailsLayout);
        subjectsBtn = findViewById(R.id.subjectsBtn);
        noticeBoardBtn = findViewById(R.id.noticeBoardBtn);

        bannersList = new ArrayList<>();

        setSwipeRefresh();

    }

    private void setSwipeRefresh(){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshLayout.setRefreshing(true);
                addClassBtn.setEnabled(false);
                //flushing data
                SharedPreferences banner =getSharedPreferences(getPackageName() + ".banners", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = banner.edit();
                editor1.clear();
                editor1.apply();

                SharedPreferences preferences1 =getSharedPreferences(getPackageName() + ".classdata", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = preferences1.edit();
                editor2.clear();
                editor2.apply();

                getAllClasses(true);

            }
        });
    }
    private void setUpDrawer() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_toggle, R.string.close_toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black, getTheme()));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        SharedPreferences shared = getSharedPreferences(getPackageName() + ".basic", MODE_PRIVATE);
        String name = (shared.getString("name", ""));
        String email = (shared.getString("email", ""));

        String versionName = BuildConfig.VERSION_NAME;
        versionTv.setText("Version: " + versionName);
        bioTv.setText("" + name + "\n" + email);
    }

    @Override
    public void onBackPressed() {
         final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Are You Sure You Want To Exit?")
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .setCancelable(true)
                .show();

        Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeBtn.setTextColor(Color.parseColor("#0F94FF"));
        positiveBtn.setTextColor(Color.parseColor("#000000"));
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }
}