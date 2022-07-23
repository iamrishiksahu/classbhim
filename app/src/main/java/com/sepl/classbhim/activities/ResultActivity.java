package com.sepl.classbhim.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sepl.classbhim.R;
import com.sepl.classbhim.classes.adapters.ResultAnalysisAdapter;
import com.sepl.classbhim.classes.models.QuestionsModel;
import com.sepl.classbhim.classes.models.ResultDataModel;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private double scoredMarks, scoredPercentage;
    private boolean isViewQuestions, isResultView, isResultExist;
    private int totalMarks, correct, incorrect, unattempted, totalQuestions;
    private Dialog loadingDialog;

    private TextView marksTv, scoredTv, percentTv, correctTv, attemptedTv, questionsTv, incorrectTv;

    private ConstraintLayout analyseLayout;
    private Button analyseBtn;
    private RecyclerView recyclerView;
    private ImageView closeBtn;
    private ResultAnalysisAdapter adapter;

    private String schoolId, classId;
    public static List<QuestionsModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        initializeViews();
        getDataFromIntent();


        analyseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list != null){
                    analyseLayout.setVisibility(View.VISIBLE);
                }else {
                    Toast.makeText(ResultActivity.this, "Attempted questions are not available!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analyseLayout.setVisibility(View.GONE);
            }
        });

    }

    private void setTextViews() {

        scoredTv.setText("" + scoredMarks);
        percentTv.setText("" + scoredPercentage);
        correctTv.setText("" + correct);
        attemptedTv.setText("" + unattempted);
        questionsTv.setText("" + totalQuestions);
        incorrectTv.setText("" + incorrect);
        marksTv.setText("" + totalMarks);

    }

    private void setViews(){
        if (isViewQuestions){

            closeBtn.setVisibility(View.GONE);
            analyseLayout.setVisibility(View.VISIBLE);

            if (list != null){
                adapter = new ResultAnalysisAdapter(list, false);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this, "Attempted questions are not available", Toast.LENGTH_SHORT).show();
                finish();
            }

        }else if (isResultView){
            analyseLayout.setVisibility(View.GONE);
            closeBtn.setVisibility(View.VISIBLE);

            if (list != null){
                adapter = new ResultAnalysisAdapter(list, true);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

        } else {
            analyseLayout.setVisibility(View.GONE);
            closeBtn.setVisibility(View.VISIBLE);

            setTextViews();

            ResultAnalysisAdapter adapter = new ResultAnalysisAdapter(list, false);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }
    }

    private void initializeViews() {

        marksTv = findViewById(R.id.maxMarks);
        scoredTv = findViewById(R.id.marksScoredLarge);
        percentTv = findViewById(R.id.percentage);
        correctTv = findViewById(R.id.correctNumber);
        questionsTv = findViewById(R.id.totalQTv);
        incorrectTv = findViewById(R.id.incorrectNumber);
        attemptedTv = findViewById(R.id.unattemptedNumber);

        analyseBtn = findViewById(R.id.analyseButton);
        analyseLayout = findViewById(R.id.analysisCL);
        recyclerView = findViewById(R.id.analyseRv);
        closeBtn = findViewById(R.id.rvClose);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);

    }

    private void getDataFromIntent() {

        isViewQuestions = getIntent().getBooleanExtra("isViewQuestions", false);
        isResultView = getIntent().getBooleanExtra("isResultView", false);

        if (isViewQuestions){
            //do nothing
            setViews();

        }else if (isResultView){
            //fetch details from database
            String id  = getIntent().getStringExtra("id");
            schoolId = getIntent().getStringExtra("school_id");
            classId = getIntent().getStringExtra("class_id");

            ///loading Dialog
            loadingDialog = new Dialog(ResultActivity.this);
            loadingDialog.setContentView(R.layout.loading_dialog);
            loadingDialog.setCancelable(false);
//            loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            loadingDialog.show();
            /////end loading dialog

            getResultData(id);

        }
        else {
            scoredMarks = getIntent().getDoubleExtra("scored_marks", 0);
            scoredPercentage = getIntent().getDoubleExtra("scored_percent", 0);
            totalMarks = getIntent().getIntExtra("max_marks", 0);
            totalQuestions = getIntent().getIntExtra("total_questions", 0);
            correct = getIntent().getIntExtra("total_correct", 0);
            incorrect = getIntent().getIntExtra("total_incorrect", 0);
            unattempted = getIntent().getIntExtra("total_unattempted", 0);

            setViews();


        }
    }

    private void getQuestionPaper(String schoolId, String id, List<String> attemptedData) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("QuestionPapers").child(schoolId).child(id).child("set1").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){

                    DataSnapshot shot = task.getResult();

                    list = new ArrayList<>();
                    for (DataSnapshot snapshot : shot.child("lang1").getChildren()){
                        list.add( snapshot.getValue(QuestionsModel.class ));
                    }

                    for(int i =0; i < list.size(); i ++) {
                        list.get(i).setSelectedResponse(Long.parseLong(attemptedData.get(i)));
                    }

                    setViews();

                    adapter.notifyDataSetChanged();

                    setTextViews();

                    loadingDialog.dismiss();


                }else{

                }
            }
        });
    }

    private void setGlobalVariables(String maxMarks, String percentage, String scoredMarks, String studentName, String testName, Long correct, Long incorrect, Long unattempted, List<String> attemptedData){
        this.totalMarks = Integer.parseInt(maxMarks);
        this.scoredPercentage = Double.parseDouble(percentage);
        this.scoredMarks = Double.parseDouble(scoredMarks);
        this.correct = correct.intValue();
        this.incorrect = incorrect.intValue();
        this.unattempted = unattempted.intValue();
        this.totalQuestions = this.correct + this.incorrect+ this.unattempted;



    }


    private void getResultData(String productId) {

        FirebaseFirestore.getInstance()
                .collection("SCHOOLS").document(schoolId)
                .collection("CLASS").document(classId)
                .collection("RESULTS")
                .whereEqualTo("test_id",productId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){

                    if (task.getResult() != null){

                        for (DocumentSnapshot shot : task.getResult().getDocuments()){

                            if(shot.get("student_uid").equals(FirebaseAuth.getInstance().getUid())){
                                String maxMarks = (String) shot.get("max_marks");
                                String percentage = (String) shot.get("percentage");
                                String scoredMarks = (String) shot.get("scored_marks");
                                String studentName = (String) shot.get("student_name");
                                String testName = (String) shot.get("test_name");

                                Long correct = (Long) shot.get("correct");
                                Long incorrect = (Long) shot.get("incorrect");
                                Long unattempted = (Long) shot.get("unattempted");
                                List<String> attemptData = (List<String>) shot.get("attempted_data");

                                setIsResultExist(true);
                                getQuestionPaper(schoolId, productId, attemptData);

                                setGlobalVariables(maxMarks, percentage, scoredMarks, studentName, testName,correct,incorrect,unattempted, attemptData);

                            }


                        }

                        if(!isResultExist){
                            Toast.makeText(ResultActivity.this, "Your result was not found, please contact admin!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }else{

                        //this product Id does not exit

                        Toast.makeText(ResultActivity.this, "There was a serious error while loading the result. Please contact admin!", Toast.LENGTH_SHORT).show();
                        finish();
                    }


                }else {

                    Toast.makeText(ResultActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void setIsResultExist(boolean bol){
        this.isResultExist = bol;
    }


    @Override
    public void onBackPressed() {

        if (!isViewQuestions){
            if (analyseLayout.getVisibility() == View.VISIBLE){
                analyseLayout.setVisibility(View.GONE);
            }else{
                finish();
            }
        }else {
            finish();
        }

    }
}