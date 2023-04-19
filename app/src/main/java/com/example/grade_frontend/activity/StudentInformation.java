package com.example.grade_frontend.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.grade_frontend.R;
import com.example.grade_frontend.pojo.Student;

public class StudentInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_information);
        Student student = (Student) getIntent().getSerializableExtra("student");
        TextView textView = findViewById(R.id.student_text_view);
        textView.setText(student.toString());
    }
}