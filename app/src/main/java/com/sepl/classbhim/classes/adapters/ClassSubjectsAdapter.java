package com.sepl.classbhim.classes.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sepl.classbhim.R;
import com.sepl.classbhim.activities.TestSetsActivity;

import java.util.List;

public class ClassSubjectsAdapter extends RecyclerView.Adapter<ClassSubjectsAdapter.ViewHolder> {

    private List<String> list;
    private String classId, schoolId;

    public ClassSubjectsAdapter(List<String> list, String classId, String schoolId) {
        this.list = list;
        this.classId = classId;
        this.schoolId = schoolId;
    }

    @NonNull
    @Override
    public ClassSubjectsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_subject_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassSubjectsAdapter.ViewHolder holder, int position) {
        holder.setData(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView subjectNameTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectNameTv = itemView.findViewById(R.id.subjectNameTv);
        }

        private void setData(String subject){
            subjectNameTv.setText(subject);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), TestSetsActivity.class);
                    intent.putExtra("subject", subject);
                    intent.putExtra("class_id", classId);
                    intent.putExtra("school_id", schoolId);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
