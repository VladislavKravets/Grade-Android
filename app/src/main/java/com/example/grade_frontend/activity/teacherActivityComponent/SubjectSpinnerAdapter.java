package com.example.grade_frontend.activity.teacherActivityComponent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.grade_frontend.pojo.CourseForGroup;

import java.util.List;

public class SubjectSpinnerAdapter extends ArrayAdapter<CourseForGroup> {

  public SubjectSpinnerAdapter(Context context, List<CourseForGroup> items) {
    super(context, 0, items);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    CourseForGroup item = getItem(position);

    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
    }

    TextView textView = convertView.findViewById(android.R.id.text1);
    textView.setText(item.getCourseName());

    return convertView;
  }

  @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    CourseForGroup item = getItem(position);

    if (convertView == null) {
      convertView = LayoutInflater.from(
              getContext()).inflate(android.R.layout.simple_spinner_dropdown_item,
              parent, false
      );
      convertView.setLayoutParams(new ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT, 150));
    }

    TextView textView = convertView.findViewById(android.R.id.text1);
    textView.setText(item.getCourseName());

    return convertView;
  }
}
