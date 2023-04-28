package com.example.grade_frontend.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.grade_frontend.R;
import com.example.grade_frontend.activity.teacherActivityComponent.StudentGroupSpinnerAdapter;
import com.example.grade_frontend.activity.teacherActivityComponent.SubjectSpinnerAdapter;
import com.example.grade_frontend.activity.teacherActivityComponent.StudentAdapter;
import com.example.grade_frontend.pojo.CourseForGroup;
import com.example.grade_frontend.pojo.Student;
import com.example.grade_frontend.pojo.StudentGroupInfo;
import com.example.grade_frontend.pojo.StudentGroupSmall;
import com.example.grade_frontend.services.teacher.TeacherService;
import com.example.grade_frontend.services.teacher.TeacherServiceCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


public class TeacherActivity extends AppCompatActivity implements TeacherServiceCallback {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance(); // получаем авторизованый клас с нашим юзвером
    private ListView listView; // лист студентов
    private Spinner listGroupSpinner; // Групируем групы преподавателя
    private Spinner semesterSpinner; // Семестр
    private Spinner subjectSpinner; // Предмет
    private TextView groupInfoTextView; // Вывод информации по групе
    private int semester; // глобально инициализируем семестр
    private String courseName; // глобально предмет для передачи в адаптер (listView student)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        TeacherService teacherService = new TeacherService(); // работа с беком
        //teacherService.getGroupByTeacherEmail(mAuth.getCurrentUser().getEmail(), this); // получаем список груп

        TextView displayTextView = findViewById(R.id.textView2);
        displayTextView.setText(mAuth.getCurrentUser().getDisplayName()); // выводим ФИО преподавателя как записано в google

        listGroupSpinner = findViewById(R.id.list_group_spinner);

        subjectSpinner = findViewById(R.id.subject_spiner);

        /* */
        semesterSpinner = findViewById(R.id.semestr_spinner);
        Integer[] data = {1,2,3,4,5,6,7,8};
        // Создаем адаптер для Spinner
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, data);
        // Устанавливаем адаптер в Spinner
        semesterSpinner.setAdapter(adapter);
        /* */

        /* */
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

        // listeners

        // group spinner
        listGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                StudentGroupSmall studentGroupSmall = (StudentGroupSmall) parent.getItemAtPosition(position);
                if(studentGroupSmall.getStudentGroupId() > 0) {
                    teacherService.getGroupInfo(studentGroupSmall.getStudentGroupId(), TeacherActivity.this); // инфу по групе с бекенда
                    teacherService.getStudentsInGroup(studentGroupSmall.getStudentGroupId(), TeacherActivity.this); // инфу по все студентам
                }else {
                    Snackbar.make(findViewById(android.R.id.content).getRootView(),
                            "Немає груп",
                            Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subjectSpinner.setSelection(0);
                listView.setSelection(0);
                listGroupSpinner.setSelection(0);
                semester = (int) parent.getItemAtPosition(position);
                teacherService.getCourseByTeacherEmail(mAuth.getCurrentUser().getEmail(), semester, TeacherActivity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CourseForGroup courseForGroup = (CourseForGroup) parent.getItemAtPosition(position);
                courseName = courseForGroup.getCourseName();
                teacherService.getGroupByEmailSemesterAndIdNameCourse(
                        mAuth.getCurrentUser().getEmail(),
                        semester,
                        courseForGroup.getCourseCourseNameId(),
                        TeacherActivity.this);
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

//    @Override
//    public void onTeacherInfoForGroups(List<CourseForGroups> studentGroupList) {
//        runOnUiThread(() -> {
////            GroupSpinnerAdapter groupSpinnerAdapter = new GroupSpinnerAdapter(this,
////                    studentGroupList);
////            groupSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
////            listGroupSpinner.setAdapter(groupSpinnerAdapter);
//        });
//    }

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
                    studentList,
                    courseName);

            listView.setAdapter(adapter);
        });
    }

    @Override
    public void onCourseByTeacherEmail(List<CourseForGroup> courseForGroups) {
        runOnUiThread(() -> {
            SubjectSpinnerAdapter groupSpinnerAdapter = new SubjectSpinnerAdapter(this,
                    courseForGroups);
            groupSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subjectSpinner.setAdapter(groupSpinnerAdapter);
        });
    }

    @Override
    public void onGroupsByTeacherEmail(List<StudentGroupSmall> studentGroupSmall) {
        runOnUiThread(() -> {
            StudentGroupSpinnerAdapter studentGroupSpinnerAdapter
                    = new StudentGroupSpinnerAdapter(this, studentGroupSmall);

            studentGroupSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            listGroupSpinner.setAdapter(studentGroupSpinnerAdapter);
        });
    }
}