package com.sepl.classbhim.classes.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.sepl.classbhim.R;
import com.sepl.classbhim.activities.QuestionDisplayActivity;
import com.sepl.classbhim.classes.Variables;

public class JumpQuestionAdapter extends BaseAdapter {

    private Context context;
    private int count;

    public JumpQuestionAdapter(Context context, int count) {
        this.context = context;
        this.count = count;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_question_item_layout,parent,false);
        }
        else {
            view = convertView;
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof QuestionDisplayActivity)
                    ((QuestionDisplayActivity)context).loadQuestionsOnRequest(position);
            }
        });
        TextView quesTV = view.findViewById(R.id.ques_num);
        quesTV.setText(String.valueOf(position+1));

        if (QuestionDisplayActivity.questionList.get(position).getSelectedResponse() != Variables.UNATTEMPTED){
            //this question is attempted
            quesTV.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.successGreen,context.getTheme())));
        }else {
            quesTV.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white,context.getTheme())));
        }

        return view;
    }
}
