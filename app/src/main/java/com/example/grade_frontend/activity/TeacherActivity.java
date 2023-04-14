package com.example.grade_frontend.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.grade_frontend.R;
import com.example.grade_frontend.activity.teacherActivityComponent.StudentAdapter;
import com.example.grade_frontend.pojo.Student;
import com.example.grade_frontend.pojo.StudentGroup;
import com.example.grade_frontend.pojo.StudentGroupInfo;
import com.example.grade_frontend.services.TeacherService;
import com.example.grade_frontend.services.teacher.TeacherServiceCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;


public class TeacherActivity extends AppCompatActivity implements TeacherServiceCallback {
    private ListView listView;
    private List<Student> students;

    private TeacherService teacherService; // Работа с беком

    private RadioGroup radioGroup; // Групируем групы преподавателя
    private TextView groupInfoTextView; // Вывод информации по групе

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        teacherService = new TeacherService(); // работа с беком
        teacherService.getGroupByTeacherEmail(user.getEmail(), this); // получаем список груп

        TextView displayTextView = findViewById(R.id.textView2);
        displayTextView.setText(user.getDisplayName()); // выводим ФИО преподавателя

        radioGroup = findViewById(R.id.list_group_chek);
        groupInfoTextView = findViewById(R.id.group_info_text_view);

        listView = findViewById(R.id.listview);

        // listener radio button
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Обработка выбора
            RadioButton radioButton = group.findViewById(checkedId);

            if (radioButton != null) {
                int groupId = radioButton.getId();
                teacherService.getGroupInfo(groupId, this); // инфу по групе с бекенда
                teacherService.getStudentsInGroup(groupId, this); // инфу по все студентам
            }
        });
    }

    @Override
    public void onTeacherInfoForGroups(List<StudentGroup> studentGroupList) {
        runOnUiThread(() -> {
            for (StudentGroup studentGroup : studentGroupList) {
                RadioButton radioButton = new RadioButton(TeacherActivity.this);
                radioButton.setText(studentGroup.getName());
                radioButton.setId(studentGroup.getId());
                radioGroup.addView(radioButton);
            }
        });
    }

    @Override
    public void onGroupInfoForId(StudentGroupInfo studentGroupInfo) {
        runOnUiThread(() -> {
            groupInfoTextView.setText(studentGroupInfo.toString());
        });
    }

    @Override
    public void onStudentsGroupList(List<Student> studentList) {
        students = studentList;
        runOnUiThread(() -> {

            ArrayAdapter<Student> adapter = new StudentAdapter(this,
                    R.layout.student_view,
                    students);

            listView.setAdapter(adapter);

        });
    }
}