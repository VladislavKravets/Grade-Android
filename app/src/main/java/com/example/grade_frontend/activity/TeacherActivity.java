package com.example.grade_frontend.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.grade_frontend.R;
import com.example.grade_frontend.activity.teacherActivityComponent.GroupSpinnerAdapter;
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
    private Spinner spinner; // Групируем групы преподавателя
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

        spinner = findViewById(R.id.list_group_chek);
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

        // listener spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StudentIncompleteGroup studentIncompleteGroup = (StudentIncompleteGroup) parent.getItemAtPosition(position);
                teacherService.getGroupInfo(studentIncompleteGroup.getId(), TeacherActivity.this); // инфу по групе с бекенда
                teacherService.getStudentsInGroup(studentIncompleteGroup.getId(), TeacherActivity.this); // инфу по все студентам
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
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
            GroupSpinnerAdapter groupSpinnerAdapter = new GroupSpinnerAdapter(this,
                    studentGroupList);
            groupSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(groupSpinnerAdapter);
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