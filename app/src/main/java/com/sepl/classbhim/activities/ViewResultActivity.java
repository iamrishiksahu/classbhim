package com.sepl.classbhim.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sepl.classbhim.R;
import com.sepl.classbhim.classes.adapters.TestSetsAdapter;
import com.sepl.classbhim.classes.models.ResultDataModel;
import com.sepl.classbhim.classes.models.TestSetModel;

import java.util.ArrayList;
import java.util.List;

import static com.sepl.classbhim.classes.Variables.TAG;

public class ViewResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<String> attemptedTestsList;
    private FirebaseFirestore firestore;
    private List<ResultDataModel> resultModelList;
    private List<TestSetModel> testSetList;
    private String schoolId="", classId="";
    private TestSetsAdapter adapter;
    private TextView noResultsTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_result);

        getSupportActionBar().setTitle("Select Test");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getDataFromIntent();

        initializeItems();

        getAttemptedTestsList();


    }

    private void getDataFromIntent() {
        schoolId = getIntent().getStringExtra("school_id");
        classId = getIntent().getStringExtra("class_id");
    }

    private void getTestMeta() {

        if ((attemptedTestsList != null) && (!schoolId.equals("")) && (!classId.equals(""))) {

            noResultsTv.setVisibility(View.GONE);

            for (int i = 0; i < attemptedTestsList.size(); i++) {

                firestore.collection("SCHOOLS")
                        .document(schoolId)
                        .collection("CLASS")
                        .document(classId)
                        .collection("TEST")
                        .document(attemptedTestsList.get(i))
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){

                            DocumentSnapshot shot = task.getResult();

                            String createdAt = (String) shot.get("created_at");
                            String updatedAt = (String) shot.get("updated_at");
                            String id = (String) shot.get("id");
                            String title = (String) shot.get("title");
                            String noEntry = (String) shot.get("no_entry");
                            String activeAt = (String) shot.get("active_at");
                            boolean resultPublished = (boolean) shot.get("result_published");
                            boolean resultOnSubmission = (boolean) shot.get("result_on_submission");

                            addToTestMetaList(createdAt, updatedAt, noEntry, id, title, activeAt, resultOnSubmission, resultPublished);


                        }else {

                        }
                    }
                });
            }

            progressBar.setVisibility(View.INVISIBLE);


        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
            noResultsTv.setVisibility(View.VISIBLE);

        }
    }

    private void setRecyclerView() {

        adapter = new TestSetsAdapter(testSetList, true, classId);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void addToTestMetaList(String createdAt, String updatedAt,String noEntry, String id, String title, String activeAt, boolean resultOnSubmission, boolean resultPublished) {
        testSetList.add(new TestSetModel(activeAt, createdAt, updatedAt,noEntry, id , title, resultOnSubmission, resultPublished));
        adapter.notifyDataSetChanged();
        Log.d(TAG, "onComplete: list size: "+ testSetList.size());

    }


    private void getAttemptedTestsList() {

        firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("attemptedTests").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    List<String> list = (List<String>) task.getResult().get("test_ids");

                    if (list.size() == 0){
                        noResultsTv.setVisibility(View.VISIBLE);
                    }else {
                        noResultsTv.setVisibility(View.GONE);
                    }

                    setAttemptedTestsList(list);

                    getTestMeta();

                }else {

                }
            }
        });
    }

    private void setAttemptedTestsList(List<String> list){
        this.attemptedTestsList = list;
    }


    private void configureRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);

        setRecyclerView();

    }

    private void initializeItems(){
        recyclerView = findViewById(R.id.resultRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        noResultsTv = findViewById(R.id.noResultTv);

        firestore = FirebaseFirestore.getInstance();
        resultModelList = new ArrayList<>();
        testSetList = new ArrayList<>();

        configureRecyclerView();


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