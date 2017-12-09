package com.upload.adeogo.dokita.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.models.Question;

import java.util.List;

/**
 * Created by Adeogo on 10/8/2017.
 */

public class QuestionAdapter extends ArrayAdapter<Question> {
    public QuestionAdapter(Context context, int resource, List<Question> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_question, parent, false);
        }

        CardView youCardView = convertView.findViewById(R.id.mYouMessage);
        CardView notYouCardView = convertView.findViewById(R.id.mNotYouMessage);

        TextView youMessageTextView = (TextView) convertView.findViewById(R.id.youQuestionMessageTextView);

        TextView messageTextView = (TextView) convertView.findViewById(R.id.questionMessageTextView);

        Question question = getItem(position);

        int check = question.getYou();

        if (check == 0){
            notYouCardView.setVisibility(View.GONE);
            youCardView.setVisibility(View.VISIBLE);
            youMessageTextView.setText(question.getText());
        }else if (check == 1){
            youCardView.setVisibility(View.GONE);
            notYouCardView.setVisibility(View.VISIBLE);
            messageTextView.setText(question.getText());

        }

        return convertView;
    }
}