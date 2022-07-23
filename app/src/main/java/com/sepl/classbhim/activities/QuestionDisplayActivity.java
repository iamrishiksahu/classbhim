package com.sepl.classbhim.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sepl.classbhim.R;
import com.sepl.classbhim.classes.OnSwipeTouchListener;
import com.sepl.classbhim.classes.Variables;
import com.sepl.classbhim.classes.adapters.JumpQuestionAdapter;
import com.sepl.classbhim.classes.models.QuestionsModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sepl.classbhim.classes.Variables.CORRECT;
import static com.sepl.classbhim.classes.Variables.INCORRECT;
import static com.sepl.classbhim.classes.Variables.NOTVISITED;
import static com.sepl.classbhim.classes.Variables.TAG;
import static com.sepl.classbhim.classes.Variables.UNATTEMPTED;
import static com.sepl.classbhim.classes.Variables.firebaseFirestore;

public class QuestionDisplayActivity extends AppCompatActivity {

    private int time, marks, currentQuestion = 0;
    private String setTitle, id, classId, schoolId;
    private boolean isSubjective, resultOnSubmission;
    public static List<QuestionsModel> questionList;
    private Double scoredMarks =0D;

    private TextView marksTv, timerTv, qTv, aTv, bTv, cTv, dTv, pTv, nTv, qNTv;
    private Button submitBtn, nxtBtn, prevBtn, clrBtn, allQsBtn;
    private ImageView questionImage;
    private LinearLayout optionsContainer;
    private ScrollView rootLayout;

    private DrawerLayout drawer;
    private JumpQuestionAdapter jumpQuestionAdapter;
    private GridView gridView;

    private int[] attemptMetrics;
    private double scoredPercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_display);

        getSupportActionBar().hide();

        initializeLayoutElements();

        getDataFromIntent();

        setTextViewsOnce();

        firebaseFirestore = FirebaseFirestore.getInstance();

        setClickListeners();
        //dummy question list


        jumpQuestionAdapter = new JumpQuestionAdapter(this,questionList.size());
        gridView.setAdapter(jumpQuestionAdapter);

        currentQuestion = 0;
        setCurrentQuestion(currentQuestion);

        startTimer();

        for (int i = 0; i < 4; i++) {
            final int index = i;
            optionsContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "option selected: " + index);
                    String optionValue = ((TextView) v).getText().toString().substring(3).trim();
                    ((TextView) v).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7cc3fc")));
                    questionList.get(currentQuestion).setSelectedResponse(new Long(index));
                    checkAnswer(optionValue);
                }
            });
        }

    }

    public void checkAnswer(String selectedAnswer){
        isOptionClickable(false);
        String correctOption = "";

        int a = questionList.get(currentQuestion).getCorrect().intValue();

        switch (a){
            case 1:
                correctOption = questionList.get(currentQuestion).getA().trim();
                break;
            case 2:
                correctOption = questionList.get(currentQuestion).getB().trim();
                break;
            case 3:
                correctOption = questionList.get(currentQuestion).getC().trim();
                break;
            case 4:
                correctOption = questionList.get(currentQuestion).getD().trim();
                break;
            default:
                correctOption = "";
                break;
        }

        if (selectedAnswer.equals(correctOption)){
            questionList.get(currentQuestion).setStatus(new Long(CORRECT));
            scoredMarks = scoredMarks + Double.parseDouble(questionList.get(currentQuestion).getPositive().toString());
            System.out.println("Scored: " + scoredMarks + " correct");

        }else {
            questionList.get(currentQuestion).setStatus(new Long(INCORRECT));
            scoredMarks = scoredMarks - (questionList.get(currentQuestion).getNegative());
            System.out.println("Scored: " + scoredMarks + " wrong");
        }

    }

    public void resetOptionsBehaviour() {
        isOptionClickable(true);
        for (int i = 0; i < 4; i++) {
            optionsContainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        }
    }

    public void isOptionClickable(boolean value){
        for (int i = 0; i < 4; i++){
            optionsContainer.getChildAt(i).setClickable(value);
        }
    }

    public void retrieveAttemptDetails(int questionIndex){
        if ((questionList.get(questionIndex).getStatus() == CORRECT) || (questionList.get(questionIndex).getStatus() == INCORRECT)){
            int x = questionList.get(questionIndex).getSelectedResponse().intValue();
            if(x < 4) {
                optionsContainer.getChildAt(x).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7cc3fc")));
                isOptionClickable(false);
            }
        }
    }

    public void loadQuestionsOnRequest(int number){

        currentQuestion = number;

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        setCurrentQuestion(currentQuestion);
        resetOptionsBehaviour();
        retrieveAttemptDetails(currentQuestion);

    }

    public void setCurrentQuestion(int number){

        setShowCurrentQuestionNumber();
        setNegativePositiveTexts();


        if (questionList.get(number).q.contains("https://")){

            //it is an image question
            qTv.setVisibility(View.GONE);
            questionImage.setVisibility(View.VISIBLE);


            String loadingUrl = "";
            if(questionList.get(number).getQ().contains("<p>") && questionList.get(number).getQ().contains("</p>")){
                loadingUrl = questionList.get(number).getQ().substring(3, questionList.get(number).getQ().length() - 4);
            }else{
                loadingUrl = questionList.get(number).getQ();
            }
            Glide.with(this).load(loadingUrl).transition(GenericTransitionOptions.with(R.anim.fade_in)).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).apply(new RequestOptions().placeholder(R.drawable.placeholder_image_short)).into(questionImage);

        }else {
            qTv.setVisibility(View.VISIBLE);
            questionImage.setVisibility(View.GONE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                qTv.setText(Html.fromHtml(questionList.get(number).getQ(), Html.FROM_HTML_MODE_COMPACT));
            }else {
                qTv.setText(Html.fromHtml(questionList.get(number).getQ()));
            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            aTv.setText(Html.fromHtml("A) " + questionList.get(number).getA(), Html.FROM_HTML_MODE_COMPACT));
            bTv.setText(Html.fromHtml("B) " + questionList.get(number).getB(), Html.FROM_HTML_MODE_COMPACT));
            cTv.setText(Html.fromHtml("C) " + questionList.get(number).getC(), Html.FROM_HTML_MODE_COMPACT));
            dTv.setText(Html.fromHtml("D) " + questionList.get(number).getD(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            aTv.setText(Html.fromHtml("A) " +questionList.get(number).getA()));
            bTv.setText(Html.fromHtml("B) " +questionList.get(number).getB()));
            cTv.setText(Html.fromHtml("C) " +questionList.get(number).getC()));
            dTv.setText(Html.fromHtml("D) " +questionList.get(number).getD()));
        }


    }

    private void onTestSubmission(){
        attemptMetrics = calculateAttemptMetrics();


        if (!id.equals("demoTest")){
            uploadResultAndSetAttempted();
        }else {
            goToResultsActivity();
        }
    }

    private void uploadResultAndSetAttempted(){

        SharedPreferences shared = getSharedPreferences(getPackageName() + ".basic", MODE_PRIVATE);
        String name = (shared.getString("name", ""));

        String uid = FirebaseAuth.getInstance().getUid();

        List<String> attemptedDetails = new ArrayList<>();

        for (int i=0; i<questionList.size(); i++){
            attemptedDetails.add(String.valueOf(questionList.get(i).getSelectedResponse()));
        }

        Date date = new Date();
        long timeMilli = date.getTime();

        Map<String, Object> map = new HashMap<>();
        map.put("correct", attemptMetrics[0]);
        map.put("incorrect", attemptMetrics[1]);
        map.put("unattempted", attemptMetrics[2]);
        map.put("percentage", "" + scoredPercentage);
        map.put("scored_marks", "" + scoredMarks);
        map.put("submission_on", "" +timeMilli);
        map.put("student_name", name);
        map.put("student_uid", uid);
        map.put("test_id", id);
        map.put("attempted_data", attemptedDetails);
        map.put("test_name", setTitle);
        map.put("max_marks", "" + marks);

        String resultId = "" + uid + "_" + id;

        firebaseFirestore.collection("SCHOOLS").document(schoolId)
                .collection("CLASS").document(classId)
                .collection("RESULTS")
                .document(resultId).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                DocumentReference washingtonRef = firebaseFirestore.collection("USERS").document(uid)
                        .collection("USER_DATA")  .document("attemptedTests");

                washingtonRef.update("test_ids", FieldValue.arrayUnion(id))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(QuestionDisplayActivity.this, "Result Updated Successfully", Toast.LENGTH_SHORT).show();
                            goToResultsActivity();
                        }else {
                            Toast.makeText(QuestionDisplayActivity.this, "Result Upload Failed! Please contact your teacher.", Toast.LENGTH_SHORT).show();
                            goToResultsActivity();
                        }
                    }
                });

            }
        });



    }

    private int[] calculateAttemptMetrics(){
        int correct = 0, incorrect =0, unattempted = 0;

        for(int i=0; i < questionList.size(); i++){
            if(questionList.get(i).getStatus() == CORRECT){
                correct++;
            }else if(questionList.get(i).getStatus() == INCORRECT){
                incorrect++;
            }else if(questionList.get(i).getStatus() == UNATTEMPTED){
                unattempted++;
            }
        }

        int[] returningArray = {correct, incorrect, unattempted};

        //calculating percentage scored

        double percentValueNotRounded = Math.round((scoredMarks/marks) * 100);
        scoredPercentage = Math.round(percentValueNotRounded*100.0)/100.0;

        return returningArray;
    }

    private void goToResultsActivity(){

        if (resultOnSubmission){
            //show the result on submission
            Intent intent = new Intent(QuestionDisplayActivity.this, ResultActivity.class);

            ResultActivity.list = questionList;

            intent.putExtra("scored_marks", scoredMarks);
            intent.putExtra("max_marks", marks);
            intent.putExtra("isViewQuestions", false);
            intent.putExtra("scored_percent", scoredPercentage);
            intent.putExtra("total_questions", questionList.size());
            intent.putExtra("total_correct", attemptMetrics[0]);
            intent.putExtra("total_incorrect", attemptMetrics[1]);
            intent.putExtra("total_unattempted", attemptMetrics[2]);

            startActivity(intent);
            finish();

        }else {
            //show an alert box only and exit the user

            AlertDialog.Builder builder = new AlertDialog.Builder(QuestionDisplayActivity.this);
            builder.setTitle("Test Submitted!");
            builder.setMessage("Your test has been submitted successfully to your school.\n\nNow you can safely close this application.\n\nYour result will be available after evaluation by your teacher.");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.setCancelable(false);

            AlertDialog dialog = builder.create();
            dialog.show();

        }


    }

    private void implementSwipeGesture(){
        //implemented swipe feature
        rootLayout.setOnTouchListener(new OnSwipeTouchListener(QuestionDisplayActivity.this) {
            @Override
            public void onSwipeLeft() {
                //go to next button
                goToNextQuestion();
            }

            @Override
            public void onSwipeRight() {
                //go to previous question
               goToPrevQuestion();

            }
        });

    }

    private void goToNextQuestion(){
        if (currentQuestion < questionList.size() - 1){
            //this question is not the last question

            prevBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkOrange, getTheme())));
            prevBtn.setTextColor(getResources().getColor(R.color.white, getTheme()));

            resetOptionsBehaviour();
            currentQuestion++;
            loadQuestionsOnRequest(currentQuestion);
        }else {
            nxtBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey, getTheme())));
            nxtBtn.setTextColor(getResources().getColor(R.color.ligthGrey, getTheme()));
        }
    }

    private void goToPrevQuestion () {
        if (currentQuestion > 0){
            //this question is not the last question

            nxtBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue, getTheme())));
            nxtBtn.setTextColor(getResources().getColor(R.color.white, getTheme()));

            currentQuestion--;
            loadQuestionsOnRequest(currentQuestion);
        }else {
            prevBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey, getTheme())));
            prevBtn.setTextColor(getResources().getColor(R.color.ligthGrey, getTheme()));
        }
    }

    private void setClickListeners() {
        nxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             goToNextQuestion();
            }
        });


        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               goToPrevQuestion();
            }
        });

        clrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetOptionsBehaviour();
                normaliseMarks(currentQuestion);
                questionList.get(currentQuestion).setStatus(new Long(UNATTEMPTED));
                questionList.get(currentQuestion).setSelectedResponse(new Long(UNATTEMPTED));

            }
        });

        allQsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!drawer.isDrawerOpen(GravityCompat.START))
                {
                    jumpQuestionAdapter.notifyDataSetChanged();
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(QuestionDisplayActivity.this);
                builder.setTitle("Are you sure you want to submit?");
                builder.setMessage("If you submit the test, then you will not be able to change any answer.");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onTestSubmission();
                    }
                });
                builder.setCancelable(true);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void normaliseMarks(int questionIndex){
//        if (!(Integer.parseInt(questionList.get(questionIndex).getStatus().toString()) == UNATTEMPTED) || !(Integer.parseInt(questionList.get(questionIndex).getStatus().toString()) == NOTVISITED)) {

            if (questionList.get(questionIndex).getStatus() == CORRECT) {
                scoredMarks = scoredMarks - Double.parseDouble(questionList.get(questionIndex).getPositive().toString());
                Log.d(TAG, "scored marks after normalization:  " + scoredMarks);
            } else if (questionList.get(questionIndex).getStatus() == INCORRECT) {
                scoredMarks = scoredMarks + questionList.get(questionIndex).getNegative().doubleValue();

                Log.d(TAG, "scored marks after normalization:  " + scoredMarks);

            }


    }

    private void setShowCurrentQuestionNumber(){
        qNTv.setText("Q. " + (currentQuestion + 1) + "/" + questionList.size());
    }

    private void setNegativePositiveTexts(){
        nTv.setText("-" + questionList.get(currentQuestion).getNegative());
        pTv.setText("+" + questionList.get(currentQuestion).getPositive());
    }

    private void setTextViewsOnce() {
        marksTv.setText("Max Marks: " + marks);
    }

    private void initializeLayoutElements() {

        allQsBtn = findViewById(R.id.allQuestions);
        marksTv = findViewById(R.id.maxMarkstv);
        timerTv = findViewById(R.id.timerTv);
        qTv = findViewById(R.id.questionText);
        aTv = findViewById(R.id.optA);
        bTv = findViewById(R.id.optB);
        cTv = findViewById(R.id.optC);
        dTv = findViewById(R.id.optD);
        pTv = findViewById(R.id.positiveMarks);
        nTv = findViewById(R.id.negativeMarks);
        qNTv = findViewById(R.id.questionNumberTv);
        questionImage = findViewById(R.id.questionImage);
        submitBtn = findViewById(R.id.submitBtn);
        nxtBtn = findViewById(R.id.nextBtn);
        prevBtn = findViewById(R.id.prevBtn);
        clrBtn = findViewById(R.id.clrBtn);
        drawer = findViewById(R.id.questionDrawerLayout);
        gridView = findViewById(R.id.questionGridView);
        optionsContainer = findViewById(R.id.optionsContainer);
        rootLayout = findViewById(R.id.actualCTL);

        implementSwipeGesture();

    }

    private void getDataFromIntent(){
        time = getIntent().getIntExtra("time", 0);
        marks = getIntent().getIntExtra("marks", 0);
        setTitle = getIntent().getStringExtra("set_title");
        schoolId = getIntent().getStringExtra("school_id");
        classId = getIntent().getStringExtra("class_id");

        id = getIntent().getStringExtra("id");
        resultOnSubmission = getIntent().getBooleanExtra("resultOnSubmission", true);
    }

    public void startTimer(){
        long givenTime = time*60*1000;
        new CountDownTimer(givenTime, 1000){
            public void onTick(long millisUntilFinished){
                String secondsStr, minutesStr;
                int seconds = (int) millisUntilFinished/1000;
                int showSeconds = seconds % 60;
                int minutes = seconds / 60;
                int showMinutes = minutes % 60;
                int hours = minutes / 60;
                if (showSeconds < 10) {
                    secondsStr = "0" + String.valueOf(showSeconds);
                }else {
                    secondsStr = String.valueOf(showSeconds);
                } if (minutes < 10){
                    minutesStr = "0" + String.valueOf(showMinutes);
                }else {
                    minutesStr = String.valueOf(showMinutes);
                }
                timerTv.setText("0"+hours + ":" + minutesStr + ":" + secondsStr);
            }
            public  void onFinish(){
                onTestSubmission();

            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Are You Sure You Want To Exit?")
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .setCancelable(true)
                .show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dialog.setMessage(Html.fromHtml("If you will Exit then your test will be cancelled. <br><br> You can minimise this app and then come back again to the test.<br><br>Press <b>Yes</b> if you want to <b>Exit</b>", Html.FROM_HTML_MODE_COMPACT));
        }else {
            dialog.setMessage(Html.fromHtml("If you will Exit then your test will be cancelled. <br><br> You can minimise this app and then come back again to the test.<br><br>Press <b>Yes</b> if you want to <b>Exit</b>"));
        }

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

//        super.onBackPressed();
    }

}