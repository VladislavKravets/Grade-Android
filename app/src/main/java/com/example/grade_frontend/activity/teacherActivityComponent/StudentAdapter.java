package com.example.grade_frontend.activity.teacherActivityComponent;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.grade_frontend.R;
import com.example.grade_frontend.activity.StudentInformation;
import com.example.grade_frontend.activity.TeacherActivity;
import com.example.grade_frontend.pojo.Student;

import java.util.List;

public class StudentAdapter extends ArrayAdapter<Student> {
    private final int layoutResource;
    private final String courseName; // предмет

    public StudentAdapter(Context context, int resource, List<Student> students, String courseName) {
        super(context, resource, students);
        this.layoutResource = resource;
        this.courseName = courseName;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Получаем объект студента для текущей позиции
        Student student = getItem(position);

        // Если элемент списка не был создан ранее, создаем его из кастомного макета
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layoutResource, parent, false);
        }

        // Находим элементы интерфейса в макете
        TextView nameTextView = convertView.findViewById(R.id.text_name);
        TextView patronimicTextView = convertView.findViewById(R.id.text_patronimic);
        TextView surnameTextView = convertView.findViewById(R.id.text_surname);
        Button scoresButton = convertView.findViewById(R.id.score_btn);
        Button absencesButton = convertView.findViewById(R.id.absences_btn);

        // Устанавливаем значения элементов интерфейса из объекта студента
        nameTextView.setText(student.getName());
        patronimicTextView.setText(student.getPatronimic());
        surnameTextView.setText(student.getSurname());

        // Обрабатываем нажатия на кнопки "Посмотреть оценки" и "Пропуски"
        scoresButton.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), StudentInformation.class);
            intent.putExtra("student", student);
            intent.putExtra("status", Status.GET_GRADE);
            intent.putExtra("courseName", courseName);
            getContext().startActivity(intent);
        });

        absencesButton.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), StudentInformation.class);
            intent.putExtra("student", student);
            intent.putExtra("status", Status.GET_ABSENCE);
            intent.putExtra("courseName", courseName);
            getContext().startActivity(intent);
        });

        return convertView;
    }
}
