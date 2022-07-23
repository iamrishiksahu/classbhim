package com.sepl.classbhim.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.ColumnInfo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.LoadBundleTask;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sepl.classbhim.MainActivity;
import com.sepl.classbhim.R;
import com.sepl.classbhim.classes.adapters.TestSetsAdapter;
import com.sepl.classbhim.classes.models.NotificationModel;
import com.sepl.classbhim.classes.models.QuestionsModel;
import com.sepl.classbhim.classes.models.TestSetModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.sepl.classbhim.classes.Variables.TAG;
import static com.sepl.classbhim.classes.Variables.UNATTEMPTED;
import static com.sepl.classbhim.classes.Variables.firebaseFirestore;

public class TestSetsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String subject, classId, schoolId;
    private ProgressBar progressBar;
    private List<QuestionsModel> demoTestList;
    private TextView notAvailableTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sets);

        recyclerView = findViewById(R.id.testSetsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        notAvailableTv = findViewById(R.id.notAvailableTv);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Choose Test");

        firebaseFirestore = FirebaseFirestore.getInstance();

        configureRecyclerView();

        getDataFromIntent();

        getSetsData(classId, schoolId,subject);


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

        Intent intent = new Intent(TestSetsActivity.this, QuestionDisplayActivity.class);
        intent.putExtra("time", 10);
        intent.putExtra("marks", 40);
        intent.putExtra("id", "demoTest");
        intent.putExtra("set_title", "Demo Test");


        QuestionDisplayActivity.questionList = demoTestList;
        startActivity(intent);
    }

    private void getDataFromIntent() {
        classId = getIntent().getStringExtra("class_id");
        schoolId = getIntent().getStringExtra("school_id");
        subject = getIntent().getStringExtra("subject");
    }

    private void configureRecyclerView(){
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
    }

    private void getSetsData(String classId, String schoolId, String subject){

        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("SCHOOLS").document(schoolId)
                .collection("CLASS")
                .document(classId)
                .collection("TEST")
                .whereEqualTo("subject_name", subject)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){

                    List<TestSetModel> testList = new ArrayList<>();

                    for (DocumentSnapshot shot : task.getResult()){

                        String createdAt = (String) shot.get("created_at");
                        String updatedAt = (String) shot.get("updated_at");
                        String id = (String) shot.get("id");
                        String title = (String) shot.get("title");
                        String noEntry = (String) shot.get("no_entry");
                        String activeAt = (String) shot.get("active_at");
                        boolean resultPublished = (boolean) shot.get("result_published");
                        boolean resultOnSubmission = (boolean) shot.get("result_on_submission");

                        testList.add(new TestSetModel(activeAt,createdAt, updatedAt, noEntry, id, title, resultOnSubmission, resultPublished));

                    }

                    Collections.sort(testList, new Comparator<TestSetModel>() {
                        @Override
                        public int compare(TestSetModel o1, TestSetModel o2) {
                            return o1.getCreatedAt().compareTo(o2.getCreatedAt());
                        }

                    });

                    Collections.reverse(testList);

                    if(testList.size() == 0){
                        notAvailableTv.setVisibility(View.VISIBLE);
                    }else{
                        notAvailableTv.setVisibility(View.GONE);
                    }

                    TestSetsAdapter adapter = new TestSetsAdapter(testList, false, classId);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(TestSetsActivity.this, "Something went wrong! Please try again later.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "not able to load tests: " + task.getException().getMessage());
                }
            }
        });

//        firebaseFirestore.collection("SCHOOLS").document(schoolId)
//                .collection("CLASS")
//                .document(classId)
//                .collection(subject)
//                .document("tests")
//                .collection("TESTS")
//                .orderBy("created_at")
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//
//                if (task.isSuccessful()){
//                    List<TestSetModel> testList = new ArrayList<>();
//
//                    for (DocumentSnapshot shot : task.getResult()){
//
//                        String createdAt = (String) shot.get("created_at");
//                        String updatedAt = (String) shot.get("updated_at");
//                        String id = (String) shot.get("id");
//                        String title = (String) shot.get("title");
//                        String activeAt = (String) shot.get("active_at");
//                        boolean resultPublished = (boolean) shot.get("result_published");
//                        boolean resultOnSubmission = (boolean) shot.get("result_on_submission");
//
//                        testList.add(new TestSetModel(activeAt,createdAt, updatedAt,id, title, resultOnSubmission, resultPublished));
//
//                    }
//
//                    Collections.reverse(testList);
//
//                    TestSetsAdapter adapter = new TestSetsAdapter(testList, false);
//                    recyclerView.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
//                    progressBar.setVisibility(View.GONE);
//
//                }else {
//                    progressBar.setVisibility(View.GONE);
//                    Toast.makeText(TestSetsActivity.this, "Something went wrong! Please try again later.", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "not able to load tests: " + task.getException().getMessage());
//                }
//
//
//            }
//        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test_demo_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }else  if(item.getItemId() == R.id.demoTest){

            prepareForDemoTest();

            return true;

        } else {

            return false;
        }
    }
}