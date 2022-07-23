package com.sepl.classbhim.classes.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.sepl.classbhim.R;
import com.sepl.classbhim.classes.models.QuestionsModel;

import java.util.List;

import static com.sepl.classbhim.classes.Variables.CORRECT;
import static com.sepl.classbhim.classes.Variables.INCORRECT;
import static com.sepl.classbhim.classes.Variables.TAG;

public class ResultAnalysisAdapter extends RecyclerView.Adapter<ResultAnalysisAdapter.ViewHolder> {

    private List<QuestionsModel> list;
    private boolean isShowCorrectAns;

    public ResultAnalysisAdapter(List<QuestionsModel> list, boolean isShowCorrectAns) {
        this.list = list;
        this.isShowCorrectAns = isShowCorrectAns;
    }

    @NonNull
    @Override
    public ResultAnalysisAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.analysis_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultAnalysisAdapter.ViewHolder holder, int position) {

        holder.setData(list.get(position).getQ(),
                list.get(position).getA(),
                list.get(position).getB(),
                list.get(position).getC(),
                list.get(position).getD());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView qTv, aTv, bTv,cTv,dTv, qNTv;
        private ImageView qIv;
        private LinearLayout optionsLL;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            optionsLL = itemView.findViewById(R.id.optionsContainer);
            aTv = itemView.findViewById(R.id.optA);
            bTv = itemView.findViewById(R.id.optB);
            cTv = itemView.findViewById(R.id.optC);
            dTv = itemView.findViewById(R.id.optD);
            qIv = itemView.findViewById(R.id.questionImage);
            qTv = itemView.findViewById(R.id.questionText);
            qNTv = itemView.findViewById(R.id.qnTv);
        }

        private void setData(String question, String a, String b, String c,String d){

            qNTv.setText("Q." + (getAdapterPosition() + 1));

            if (list.get(getAdapterPosition()).q.contains("https://")){
                //it is an image question
                qTv.setVisibility(View.INVISIBLE);
                qIv.setVisibility(View.VISIBLE);
                String loadingUrl = "";
                if(question.contains("<p>") && question.contains("</p>")){
                    loadingUrl = question.substring(3, question.length() - 4);
                }else{
                    loadingUrl = question;
                }
                Glide.with(itemView.getContext()).load(loadingUrl).transition(GenericTransitionOptions.with(R.anim.fade_in)).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).apply(new RequestOptions().placeholder(R.drawable.placeholder_image_short)).into(qIv);

            }else {
                qTv.setVisibility(View.VISIBLE);
                qIv.setVisibility(View.INVISIBLE);
                qTv.setText("" + question);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    qTv.setText(Html.fromHtml("" + question, Html.FROM_HTML_MODE_COMPACT));
                }else {
                    qTv.setText(Html.fromHtml("" + question));
                }


            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                aTv.setText(Html.fromHtml("A) " + a, Html.FROM_HTML_MODE_COMPACT));
                bTv.setText(Html.fromHtml("B) " + b, Html.FROM_HTML_MODE_COMPACT));
                cTv.setText(Html.fromHtml("C) " + c, Html.FROM_HTML_MODE_COMPACT));
                dTv.setText(Html.fromHtml("D) " + d, Html.FROM_HTML_MODE_COMPACT));
            } else {
                aTv.setText(Html.fromHtml("A) " +a));
                bTv.setText(Html.fromHtml("B) " +b));
                cTv.setText(Html.fromHtml("C) " +c));
                dTv.setText(Html.fromHtml("D) " +d));
            }

            retrieveAttemptDetails(getAdapterPosition());

        }

        public void retrieveAttemptDetails(int questionIndex){


            int x = list.get(questionIndex).getSelectedResponse().intValue();
            isOptionClickable(false);

            int dpValue = setDp(16, itemView.getContext());

            if(isShowCorrectAns){

                if (x < 4){

                    if(x == (list.get(getAdapterPosition()).getCorrect() - 1)){

                        optionsLL.getChildAt(x).setBackground(itemView.getContext().getDrawable(R.drawable.rounder_corner_outline_green));
                        optionsLL.getChildAt(x).setPadding(dpValue,0,0,0 );

                    }else if(x != (list.get(getAdapterPosition()).getCorrect() - 1)){
                        optionsLL.getChildAt(x).setBackground(itemView.getContext().getDrawable(R.drawable.rounder_corner_outline_red));
                        optionsLL.getChildAt(x).setPadding(dpValue,0,0,0 );

                        optionsLL.getChildAt((int)(list.get(getAdapterPosition()).getCorrect() - 1)).setBackground(itemView.getContext().getDrawable(R.drawable.rounder_corner_outline_green));
                        optionsLL.getChildAt((int)(list.get(getAdapterPosition()).getCorrect() - 1)).setPadding(dpValue,0,0,0 );
                        optionsLL.getChildAt((int)(list.get(getAdapterPosition()).getCorrect() - 1)).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#87FFA5")));

                    }else{

                    }


                }




            }else {

                if ((list.get(questionIndex).getStatus() == CORRECT)){
                    if(x < 4) {
                        optionsLL.getChildAt(x).setBackground(itemView.getContext().getDrawable(R.drawable.rounder_corner_outline_green));
                        optionsLL.getChildAt(x).setPadding(dpValue,0,0,0 );

//                        optionsLL.getChildAt(x).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#87FFA5")));
                    }
                }else if((list.get(questionIndex).getStatus() == INCORRECT)){
                    if(x < 4) {
                        optionsLL.getChildAt(x).setBackground(itemView.getContext().getDrawable(R.drawable.rounder_corner_outline_red));
                        optionsLL.getChildAt(x).setPadding(dpValue,0,0,0 );

//                        optionsLL.getChildAt(x).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFA9A9")));
                    }
                }else {
                    if(x < 4) {
                        optionsLL.getChildAt(x).setBackground(null);

//                        optionsLL.getChildAt(x).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                    }
                }

            }



        }

        public void isOptionClickable(boolean value){


            int dpValue = setDp(16, itemView.getContext());

            for (int i = 0; i < 4; i++){
                optionsLL.getChildAt(i).setClickable(value);
                optionsLL.getChildAt(i).setBackground(null);
                optionsLL.getChildAt(i).setPadding(dpValue,0,0,0 );
            }
        }

        private int setDp(int dp, Context context) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        }

    }
}
