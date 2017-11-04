package com.work.adeogo.dokita.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.work.adeogo.dokita.R;
import com.work.adeogo.dokita.models.Question;

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

        TextView messageTextView = (TextView) convertView.findViewById(R.id.questionMessageTextView);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.questionNameTextView);

        Question question = getItem(position);

        messageTextView.setText(question.getText());
        nameTextView.setText(question.getName());

        return convertView;
    }
}
