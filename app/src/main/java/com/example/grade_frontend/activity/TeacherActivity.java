package com.example.grade_frontend.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.grade_frontend.R;
import com.example.grade_frontend.activity.teacherActivityComponent.StudentAdapter;
import com.example.grade_frontend.pojo.Student;
import com.example.grade_frontend.pojo.StudentIncompleteGroup;
import com.example.grade_frontend.pojo.StudentGroupInfo;
import com.example.grade_frontend.services.teacher.TeacherService;
import com.example.grade_frontend.services.teacher.TeacherServiceCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


public class TeacherActivity extends AppCompatActivity implements TeacherServiceCallback {
    private ListView listView; // лист студентов
    private RadioGroup radioGroup; // Групируем групы преподавателя
    private TextView groupInfoTextView; // Вывод информации по групе

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        // получаем авторизованый клас с нашим юзвером
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        TeacherService teacherService = new TeacherService(); // работа с беком

        teacherService.getGroupByTeacherEmail(mAuth.getCurrentUser().getEmail(), this); // получаем список груп

        TextView displayTextView = findViewById(R.id.textView2);
        displayTextView.setText(mAuth.getCurrentUser().getDisplayName()); // выводим ФИО преподавателя как записано в google

        radioGroup = findViewById(R.id.list_group_chek);
        groupInfoTextView = findViewById(R.id.group_info_text_view);

        listView = findViewById(R.id.listview);

        // выход из аккаунта и очистка данных с мобилки
        findViewById(R.id.logout_btn).setOnClickListener(e -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // очищаем все значения
            editor.apply(); // сохраняем изменения

            mAuth.signOut();
            GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();

            Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
            startActivity(intent);
            finish();
        });

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

    // Обработчик нажатия кнопки Назад
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Вернуться на MainActivity
    }

    @Override
    public void onTeacherInfoForGroups(List<StudentIncompleteGroup> studentGroupList) {
        runOnUiThread(() -> {
            for (StudentIncompleteGroup studentGroup : studentGroupList) {
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
        runOnUiThread(() -> {
            ArrayAdapter<Student> adapter = new StudentAdapter(this,
                    R.layout.student_view,
                    studentList);

            listView.setAdapter(adapter);
        });
    }
}