package com.sepl.classbhim.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.errorprone.annotations.Var;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sepl.classbhim.R;
import com.sepl.classbhim.classes.Variables;
import com.sepl.classbhim.classes.adapters.ResultAnalysisAdapter;
import com.sepl.classbhim.classes.models.QuestionsModel;

import java.util.ArrayList;
import java.util.List;

import static com.sepl.classbhim.classes.Variables.TAG;
import static com.sepl.classbhim.classes.Variables.UNATTEMPTED;

public class TestInstructionsActivity extends AppCompatActivity {

    private TextView instructionTv, maxMarksTv,maxTimeTv, titleTv;
    private Button startTestBtn;
    
    private Long maxTime, maxMarks;
    private String setTitle, id, schoolId, classId;
    private boolean resultOnSubmission;
    private int attemptStatus;

    private DatabaseReference reference;

    private List<QuestionsModel> questionList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_instructions);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Instruction");

        initializeLayoutElements();

        setViewsInvisible();

        reference = FirebaseDatabase.getInstance().getReference();

        getDataFromIntent();

        getDataFromDB();

        isAlreadyAttempted(id);

    }

    private void getDataFromIntent() {
        setTitle = getIntent().getStringExtra("set_title");
        id = getIntent().getStringExtra("id");
        schoolId = getIntent().getStringExtra("school_id");
        classId = getIntent().getStringExtra("class_id");
        resultOnSubmission = getIntent().getBooleanExtra("resultOnSubmission", true);
    }

    private void getDataFromDB() {

        reference = FirebaseDatabase.getInstance().getReference();

        reference.child("QuestionPapers").child(schoolId).child(id).child("set1").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (task.isSuccessful()){

                    DataSnapshot shot = task.getResult();

                    questionList = new ArrayList<>();

                    maxMarks = (Long) shot.child("maxMarks").getValue(Long.class);
                    maxTime = (Long) shot.child("maxTime").getValue(Long.class);

                    for (DataSnapshot snapshot : shot.child("lang1").getChildren()){
                        questionList.add( snapshot.getValue(QuestionsModel.class ));
                    }
                    setViewsVisible();
                    setLayoutTexts();

                }else {
                    Toast.makeText(TestInstructionsActivity.this, "Something went wrong! Please try again later! : "+ task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                    Log.d(TAG, "unable to fetch realtime database data in "+ id +" because :" + task.getException().getMessage());
                }

            }
        });
    }

    private void setAttemptStatus(int i){
        attemptStatus = i;
    }

    private void initializeLayoutElements() {
        startTestBtn = findViewById(R.id.startTestBtn);
        maxMarksTv = findViewById(R.id.maxMarksTV);
        maxTimeTv = findViewById(R.id.timeLimitTV);
        titleTv = findViewById(R.id.testTitleTV);
        instructionTv = findViewById(R.id.basicInstruction);
        progressBar = findViewById(R.id.progressBar);

        startTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maxTime != null){

                    if(attemptStatus == 1){
                        //attempted
                        AlertDialog.Builder builder = new AlertDialog.Builder(TestInstructionsActivity.this);
                        builder.setTitle("Already attempted!");
                        builder.setMessage("You have already attempted this test, you cannot attempt it again. You can see the question paper only. Do you want to see the question paper?");
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(TestInstructionsActivity.this, ResultActivity.class);
                                intent.putExtra("isViewQuestions", true);

                                ResultActivity.list = questionList;

                                startActivity(intent);
                            }
                        });
                        builder.setCancelable(true);

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                    else if(attemptStatus == 2){
                        //unattempted

                        Intent intent = new Intent(TestInstructionsActivity.this, QuestionDisplayActivity.class);
                        intent.putExtra("time", maxTime.intValue());
                        intent.putExtra("marks", maxMarks.intValue());
                        intent.putExtra("id", id);
                        intent.putExtra("set_title", setTitle);
                        intent.putExtra("resultOnSubmission", resultOnSubmission);
                        intent.putExtra("school_id", schoolId);
                        intent.putExtra("class_id", classId);

                        QuestionDisplayActivity.questionList = questionList;
                        startActivity(intent);
                        finish();

                    }
                    else if(attemptStatus == 3){
                        //could not fetch
                        Toast.makeText(TestInstructionsActivity.this, "Could not verify attempt status! Please try again later.", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        Toast.makeText(TestInstructionsActivity.this, "Something went wrong! Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(TestInstructionsActivity.this, "Test duration is not available. Please contact your teacher!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void isAlreadyAttempted(String testId){


        // 1 means attempted, 2 means unattempted, 3 means could not verify so try again.

        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("attemptedTests").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    List<String> list = (List<String>) task.getResult().get("test_ids");

                    if (list != null){
                        if (list.contains(testId)) {
                           setAttemptStatus(1);
                        }else{
                            setAttemptStatus(2);
                        }
                    }else {
                        setAttemptStatus(2);
                    }

                }else{

                    setAttemptStatus(3);
                }
            }
        });



    }

    private void setViewsInvisible(){

        startTestBtn.setVisibility(View.INVISIBLE);
        maxMarksTv.setVisibility(View.INVISIBLE);
        maxTimeTv.setVisibility(View.INVISIBLE);
        titleTv.setVisibility(View.INVISIBLE);
        instructionTv.setVisibility(View.INVISIBLE);

        progressBar.setVisibility(View.VISIBLE);

    }

    private void setViewsVisible(){
        startTestBtn.setVisibility(View.VISIBLE);
        maxMarksTv.setVisibility(View.VISIBLE);
        maxTimeTv.setVisibility(View.VISIBLE);
        titleTv.setVisibility(View.VISIBLE);
        instructionTv.setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.GONE);
    }

    private void setLayoutTexts() {

        maxMarksTv.setText("Max Marks: " + maxMarks);
        maxTimeTv.setText("Time Limit: " + maxTime + " mins");
        titleTv.setText(setTitle);
        startTestBtn.setText("Start Test");


        String englishInstruction = "<ul><li>You have to finish the test in " + maxTime + " minutes.</li>" +
                "<li>Each question contains 4 options out of which only one is correct.</li>" +
                "<li>Negative marking in each question will be displayed at the top in red color.</li>" +
                "<li>Positive marking in each question will be displayed at the top in green color.</li>" +
                "<li>There is no negative marking for the question which you have not attempted.</li>" +
                "<li>After time is over, the test gets submitted automatically.</li></ul>";


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            instructionTv.setText(Html.fromHtml(englishInstruction, Html.FROM_HTML_MODE_COMPACT));
        } else {
            instructionTv.setText(Html.fromHtml(englishInstruction));
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }else {
            return false;
        }
    }
}