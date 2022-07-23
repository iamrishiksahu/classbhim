package com.sepl.classbhim.classes.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.errorprone.annotations.Var;
import com.sepl.classbhim.R;
import com.sepl.classbhim.activities.QuestionDisplayActivity;
import com.sepl.classbhim.activities.ResultActivity;
import com.sepl.classbhim.activities.TestInstructionsActivity;
import com.sepl.classbhim.classes.Variables;
import com.sepl.classbhim.classes.models.TestSetModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.sepl.classbhim.classes.Variables.CHARACTER_COUNT_IN_SCHOOL_ID;

public class TestSetsAdapter extends RecyclerView.Adapter<TestSetsAdapter.ViewHolder> {

    private List<TestSetModel> list;
    private boolean isResultView;
    private String classId;

    public TestSetsAdapter(List<TestSetModel> list, boolean isResultView, String classId) {
        this.list = list;
        this.isResultView = isResultView;
        this.classId = classId;
    }

    @NonNull
    @Override
    public TestSetsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_sets_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestSetsAdapter.ViewHolder holder, int position) {
        holder.setData(list.get(position).getTitle(), list.get(position).getId(), list.get(position).getActiveAt(), list.get(position).getNoEntry(),list.get(position).isResultOnSubmission());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTv, openingTv, closingTv, closingTextTv, openingTextTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.testTitleTv);
            openingTv = itemView.findViewById(R.id.openingTimeTv);
            closingTv = itemView.findViewById(R.id.closingTimeTv);
            closingTextTv = itemView.findViewById(R.id.closingTextTv);
            openingTextTv = itemView.findViewById(R.id.openingTextTv);
        }

        private void setData(String title, String id, String time, String noEntryTime, boolean resultOnSub){

            if(isResultView){
                openingTv.setVisibility(View.GONE);
                closingTv.setVisibility(View.GONE);
                openingTextTv.setVisibility(View.GONE);
                closingTextTv.setVisibility(View.GONE);
            }else{
                openingTv.setVisibility(View.VISIBLE);
                closingTv.setVisibility(View.VISIBLE);
                openingTextTv.setVisibility(View.VISIBLE);
                closingTextTv.setVisibility(View.VISIBLE);

                try{
                    String opening = new SimpleDateFormat( "dd-MM-yyyy 'at' hh:mm a").format(Long.parseLong(list.get(getAdapterPosition()).getActiveAt()));
                    String closing = new SimpleDateFormat( "dd-MM-yyyy 'at' hh:mm a").format(Long.parseLong(list.get(getAdapterPosition()).getNoEntry()));
                    openingTv.setText("" + opening);
                    closingTv.setText("" + closing);

                }catch (Exception e){
                    //
                }

            }

            titleTv.setText(title);

            Long timeLong = Long.parseLong(time);
            Long noEntryTimeLong = Long.parseLong(noEntryTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!id.equals("")){

                        if (isResultView){
                            //result view activity me hai

                            if (list.get(getAdapterPosition()).isResultPublished()){
                                String schoolId = classId.substring(0,CHARACTER_COUNT_IN_SCHOOL_ID).toUpperCase();

                                Intent intent = new Intent(itemView.getContext(), ResultActivity.class);
                                intent.putExtra("isResultView", true);
                                intent.putExtra("school_id", schoolId);
                                intent.putExtra("class_id", classId);
                                intent.putExtra("id", id);
                                itemView.getContext().startActivity(intent);

                            }else {
                                Toast.makeText(itemView.getContext(), "Result is not published yet!", Toast.LENGTH_LONG).show();
                            }
                        }else {
                            //test sets activity me hai

                            if (timeLong <= System.currentTimeMillis()){
                                if(noEntryTimeLong >= System.currentTimeMillis()){
                                    String schoolId = classId.substring(0,CHARACTER_COUNT_IN_SCHOOL_ID).toUpperCase();
                                    Intent intent = new Intent(itemView.getContext(), TestInstructionsActivity.class);
                                    intent.putExtra("id",id );
                                    intent.putExtra("school_id", schoolId);
                                    intent.putExtra("class_id", classId);
                                    intent.putExtra("set_title",title );
                                    intent.putExtra("resultOnSubmission",resultOnSub );
                                    itemView.getContext().startActivity(intent);
                                }else{
                                    Toast.makeText(itemView.getContext(), "Entry closed! Sorry, you cannot enter the exam now!", Toast.LENGTH_LONG).show();
                                }



                            }else {
                                Toast.makeText(itemView.getContext(), "Test is currently not activated.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else {
                        Toast.makeText(itemView.getContext(), "Test set ID empty! Please notify admin!", Toast.LENGTH_SHORT).show();
                        Log.d(Variables.TAG,"Test id is empty in the test set number" + getAdapterPosition() + 1 );
                    }
                }
            });

        }
    }
}
