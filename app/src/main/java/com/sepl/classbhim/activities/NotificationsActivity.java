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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sepl.classbhim.R;
import com.sepl.classbhim.classes.LocalDatabase;
import com.sepl.classbhim.classes.adapters.NotificationAdapter;
import com.sepl.classbhim.classes.models.NotificationModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.sepl.classbhim.classes.Variables.TAG;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private int classNumber = 0;
    private List<NotificationModel> list;
    private String classId, schoolId, className;
    private ProgressBar progressBar;
    private TextView notAvailableTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        recyclerView = findViewById(R.id.notificationsRec);
        progressBar = findViewById(R.id.progressBar);
        notAvailableTv = findViewById(R.id.notAvailableTv);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notice Board");

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);

        list = new ArrayList<>();

        getDataFromIntent();

        getNoticesFromDB();



//        LocalDatabase db = LocalDatabase.getDbInstance(this);
//        List<NotificationModel> list = db.notificationDao().getAllNotifications();
//        List<NotificationModel> displayingList = new ArrayList<>();

//        for (int i =0; i< list.size(); i++){
//            if (list.get(i).classNumber == classNumber){
//                displayingList.add(list.get(i));
//            }
//        }

//        Collections.reverse(displayingList);



    }

    private void getNoticesFromDB(){

        if ((schoolId != null) && (classId != null)) {
            FirebaseFirestore.getInstance().collection("SCHOOLS").document(schoolId)
                    .collection("NOTICES")
                    .whereEqualTo("class_id", classId)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot shot : task.getResult()) {

                            NotificationModel model = new NotificationModel();
                            model.title = (String) shot.get("title");
                            model.body = (String) shot.get("body");
                            model.dateValue = (String) shot.get("time");
                            model.author = (String) shot.get("author");
                            list.add(model);
                        }

                        Collections.sort(list, new Comparator<NotificationModel>() {
                            @Override
                            public int compare(NotificationModel o1, NotificationModel o2) {
                                return o1.getDateValue().compareTo(o2.getDateValue());
                            }
                        });

                        Collections.reverse(list);
                        if(list.size() == 0){
                            notAvailableTv.setVisibility(View.VISIBLE);
                        }else{
                            notAvailableTv.setVisibility(View.GONE);
                        }


                        adapter = new NotificationAdapter(list);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(NotificationsActivity.this, "Something went wrong while loading notifications. Please try again later!", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onComplete: " + task.getException().getMessage());
                    }

                }
            });
        }else{
            Toast.makeText(this, "Something went wrong! Please try later.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataFromIntent() {
        classId = getIntent().getStringExtra("class_id");
        schoolId = getIntent().getStringExtra("school_id");
        className = getIntent().getStringExtra("class_name");
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