package com.example.grade_frontend.activity.StudentInformationComponent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.grade_frontend.R;
import com.example.grade_frontend.pojo.Absence;
import com.example.grade_frontend.pojo.Grade;

import java.util.List;

public class ListAdapter extends ArrayAdapter<Object> {

  private static final int TYPE_ABSENCE = 0;
  private static final int TYPE_GRADE = 1;

  private LayoutInflater inflater;

  public ListAdapter(Context context, List<Object> objects) {
    super(context, 0, objects);
    inflater = LayoutInflater.from(context);
  }

  @Override
  public int getViewTypeCount() {
    return 2;
  }

  @Override
  public int getItemViewType(int position) {
    Object item = getItem(position);
    if (item instanceof Absence) {
      return TYPE_ABSENCE;
    } else if (item instanceof Grade) {
      return TYPE_GRADE;
    }
    return -1;
  }

  @NonNull
  @Override
  public View getView(int position, View convertView, @NonNull ViewGroup parent) {
    int viewType = getItemViewType(position);
    if (convertView == null) {
      if (viewType == TYPE_ABSENCE) {
        convertView = inflater.inflate(R.layout.list_item_absence, parent, false);
      } else if (viewType == TYPE_GRADE) {
        convertView = inflater.inflate(R.layout.list_item_grade, parent, false);
      }
    }

    if (viewType == TYPE_ABSENCE) {
      TextView textViewCourseName = convertView.findViewById(R.id.textViewCourseName);
      TextView textViewDate = convertView.findViewById(R.id.textViewDate);

      Absence absence = (Absence) getItem(position);

      if (absence != null) {
        textViewCourseName.setText(absence.getCourseName());
        textViewDate.setText(absence.getDate());
      }
    } else if (viewType == TYPE_GRADE) {
      TextView textViewGrade = convertView.findViewById(R.id.textViewGrade);
      TextView textViewCourseName = convertView.findViewById(R.id.textViewCourseName);
      TextView textViewCreatedAt = convertView.findViewById(R.id.textViewCreatedAt);

      Grade grade = (Grade) getItem(position);

      if (grade != null) {
        textViewGrade.setText(String.valueOf(grade.getGrade()));
        textViewCourseName.setText(grade.getCourseName());
        textViewCreatedAt.setText(grade.getCreatedAt());
      }
    }

    return convertView;
  }
}