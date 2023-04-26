package com.example.grade_frontend.activity.teacherActivityComponent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.grade_frontend.pojo.StudentIncompleteGroup;

import java.util.ArrayList;
import java.util.List;

public class GroupSpinnerAdapter extends ArrayAdapter<StudentIncompleteGroup> {

  public GroupSpinnerAdapter(Context context, List<StudentIncompleteGroup> items) {
    super(context, 0, items);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    StudentIncompleteGroup item = getItem(position);

    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
    }

    TextView textView = convertView.findViewById(android.R.id.text1);
    textView.setText(item.getName());

    return convertView;
  }

  @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    StudentIncompleteGroup item = getItem(position);

    if (convertView == null) {
      convertView = LayoutInflater.from(
              getContext()).inflate(android.R.layout.simple_spinner_dropdown_item,
              parent, false
      );
      convertView.setLayoutParams(new ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT, 150));
    }

    TextView textView = convertView.findViewById(android.R.id.text1);
    textView.setText(item.getName());

    return convertView;
  }
}
