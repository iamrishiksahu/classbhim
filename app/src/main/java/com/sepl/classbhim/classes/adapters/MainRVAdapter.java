package com.sepl.classbhim.classes.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sepl.classbhim.R;
import com.sepl.classbhim.activities.ClassActivity;
import com.sepl.classbhim.classes.models.MainRvModel;

import java.util.List;

import static com.sepl.classbhim.classes.Variables.CHARACTER_COUNT_IN_SCHOOL_ID;

public class MainRVAdapter extends RecyclerView.Adapter<MainRVAdapter.ViewHolder> {

    private List<MainRvModel> list;

    public MainRVAdapter(List<MainRvModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MainRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainRVAdapter.ViewHolder holder, int position) {

        holder.setData(list.get(position).getClassName(),list.get(position).getSchoolName(), list.get(position).getStatus() );

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView schoolNameTv, classNameTv, statusTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            schoolNameTv = itemView.findViewById(R.id.schoolNameTv);
            classNameTv = itemView.findViewById(R.id.classNameTv);
            statusTv= itemView.findViewById(R.id.statusTv);
        }

        private void setData(String className, String schoolName, int status){
            schoolNameTv.setText(schoolName);
            classNameTv.setText("" + className);
            if (status == 1){
                statusTv.setText("Active");
                statusTv.setTextColor(itemView.getContext().getResources().getColor(R.color.successGreen, itemView.getContext().getTheme()));

            }else if (status == 2){
                statusTv.setText("Request Rejected");
                statusTv.setTextColor(itemView.getContext().getResources().getColor(R.color.red, itemView.getContext().getTheme()));

            }else {
                statusTv.setText("Request Sent");
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (status == 1){
                        String schoolId= list.get(getAdapterPosition()).getClassId().substring(0,CHARACTER_COUNT_IN_SCHOOL_ID).toUpperCase();

                        Intent intent = new Intent(itemView.getContext(), ClassActivity.class);
                        intent.putExtra("class_id", list.get(getAdapterPosition()).getClassId());
                        intent.putExtra("school_id", schoolId);
                        intent.putExtra("class_name", list.get(getAdapterPosition()).getClassName());
                        itemView.getContext().startActivity(intent);
                    }else {
                        //This class is not active

                        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
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
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(itemView.getContext().getResources().getColor(R.color.black, itemView.getContext().getTheme()));


                            }
                        });
                        dialog.show();
                    }
                }
            });
        }
    }


}
