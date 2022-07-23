package com.sepl.classbhim.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sepl.classbhim.R;
import com.sepl.classbhim.classes.adapters.ClassSubjectsAdapter;
import com.sepl.classbhim.classes.adapters.TestSetsAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.sepl.classbhim.classes.Variables.TAG;
import static com.sepl.classbhim.classes.Variables.firebaseFirestore;

public class ClassActivity extends AppCompatActivity {


    private String className, classId, schoolId;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        initializeItems();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Subject");

        firebaseFirestore = FirebaseFirestore.getInstance();
        getValuesFromIntent();
//        setLayoutValues();

        configureRecyclerView();

        getSubjectList();
    }


    private void getSubjectList() {
        firebaseFirestore.collection("SCHOOLS")
                .document(schoolId).collection("CLASS")
                .document(classId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot shot = task.getResult();

                    List<String> subjectList;

                    subjectList = (List<String>) shot.get("subjects");

                    ClassSubjectsAdapter adapter = new ClassSubjectsAdapter(subjectList, classId, schoolId);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);

                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ClassActivity.this, "Something went wrong! Please try again later.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "not able to load tests: " + task.getException().getMessage());
                }
            }
        });
    }

    private void configureRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
    }

//    private void setLayoutValues(){
//        classNameTv.setText("" + className);
//    }

    private void initializeItems(){
        recyclerView = findViewById(R.id.classSubjectsRv);
//        classNameTv = findViewById(R.id.classNameTv);
//        noticesTv = findViewById(R.id.noticesTv);
        progressBar = findViewById(R.id.progressBar);
    }

    private void getValuesFromIntent(){
        className = getIntent().getStringExtra("class_name");
        classId = getIntent().getStringExtra("class_id");
        schoolId = getIntent().getStringExtra("school_id");

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