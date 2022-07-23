package com.sepl.classbhim.classes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sepl.classbhim.R;
import com.sepl.classbhim.classes.models.NotificationModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationModel> list;

    public NotificationAdapter(List<NotificationModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        holder.setData(list.get(position).title, list.get(position).body, list.get(position).dateValue, list.get(position).author);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView bodyTv, titleTv, dateTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bodyTv = itemView.findViewById(R.id.ntBody);
            titleTv = itemView.findViewById(R.id.ntTitle);
            dateTv = itemView.findViewById(R.id.ntDate);
        }

        private void setData(String title, String body, String date, String author){
            titleTv.setText(title);
            bodyTv.setText(body);

            String pattern = "dd/MM/yyy 'at' h:mm a";
            long foo = Long.parseLong(date);

            try {
                Date dateD = new Date(foo);
                DateFormat formatter = new SimpleDateFormat(pattern);
                dateTv.setText("- " + author + "  (" + formatter.format(dateD) + ")");
            }catch (Exception e){
                
            }
        }
    }
}
