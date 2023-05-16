package com.example.grade_frontend.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.example.grade_frontend.services.teacher.TeacherActivityService;
import com.example.grade_frontend.services.teacher.TeacherActivityServiceCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


public class TeacherActivity extends AppCompatActivity implements TeacherActivityServiceCallback {
  private FirebaseAuth mAuth = FirebaseAuth.getInstance(); // получаем авторизованый клас с нашим юзвером
  // android widgets
  private ListView listView; // лист студентов
  private Spinner listGroupSpinner; // Групируем групы преподавателя
  private Spinner semesterSpinner; // Семестр
  private Spinner subjectSpinner; // Предмет
  private TextView groupInfoTextView; // Вывод информации по групе
  // глобальные ппеременные
  private int semester; // глобально инициализируем семестр
  private String courseName; // глобально предмет для передачи в адаптер (listView student)
  private String groupName; // глобально група для передачи в studentInformation
  // adapters
  private ArrayAdapter<Integer> semesterSpinnerAdapter;
  private ArrayAdapter<Student> listViewAdapter;
  private SubjectSpinnerAdapter subjectSpinnerAdapter;
  private StudentGroupSpinnerAdapter studentGroupSpinnerAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_teacher);
    // Установить кастомный заголовок
    getSupportActionBar().setDisplayShowCustomEnabled(true);
    getSupportActionBar().setCustomView(R.layout.active_user_title);
    // Получить ссылки на элементы в заголовке
    TextView titleText = findViewById(R.id.title_text);
    Button titleButton = findViewById(R.id.title_button);

    titleText.setText("Викладач " + mAuth.getCurrentUser().getDisplayName());

    TextView displayTextView = findViewById(R.id.textView2);
    displayTextView.setText(mAuth.getCurrentUser().getDisplayName()); // выводим ФИО преподавателя как записано в google

    /* initialization */
    listGroupSpinner = findViewById(R.id.list_group_spinner);
    subjectSpinner = findViewById(R.id.subject_spiner);
    groupInfoTextView = findViewById(R.id.group_info_text_view);
    listView = findViewById(R.id.listview);
    /* */

    /* количество семестров и их инициализация*/
    semesterSpinner = findViewById(R.id.semestr_spinner);
    Integer[] semesters = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    semesterSpinnerAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, semesters);
    semesterSpinner.setAdapter(semesterSpinnerAdapter);
    /* */

    /* buttons */
    // выход из аккаунта и очистка данных с мобилки
    titleButton.setOnClickListener(e -> {
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
    /* */

    // listeners spinners
    // group spinner
    listGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (listViewAdapter != null) {
          listViewAdapter.clear();
          listViewAdapter.notifyDataSetChanged();
        }
        groupInfoTextView.setText("");

        StudentGroupSmall studentGroupSmall = (StudentGroupSmall) parent.getItemAtPosition(position);
        groupName = studentGroupSmall.getStudentGroupName();

        if (studentGroupSmall.getStudentGroupId() > 0) {
          TeacherActivityService teacherService = new TeacherActivityService();
          teacherService.getGroupInfo(studentGroupSmall.getStudentGroupId(), TeacherActivity.this); // инфу по групе с бекенда
          teacherService.getStudentsInGroup(studentGroupSmall.getStudentGroupId(), TeacherActivity.this); // инфу по все студентам
        } else {
          Snackbar.make(findViewById(android.R.id.content).getRootView(),
                  "Немає груп",
                  Snackbar.LENGTH_LONG).show();
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
    semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (listViewAdapter != null) {
          listViewAdapter.clear();
          listViewAdapter.notifyDataSetChanged();
        }
        if (studentGroupSpinnerAdapter != null) {
          studentGroupSpinnerAdapter.clear();
          studentGroupSpinnerAdapter.notifyDataSetChanged();
        }

        groupInfoTextView.setText("");

        semester = (int) parent.getItemAtPosition(position);

        TeacherActivityService teacherService = new TeacherActivityService(); // работа с беком
        teacherService.getCourseByTeacherEmail(mAuth.getCurrentUser().getEmail(), semester, TeacherActivity.this);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
    subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (listViewAdapter != null) {
          listViewAdapter.clear();
          listViewAdapter.notifyDataSetChanged();
        }
        groupInfoTextView.setText("");

        CourseForGroup courseForGroup = (CourseForGroup) parent.getItemAtPosition(position);
        courseName = courseForGroup.getCourseName();

        TeacherActivityService teacherService = new TeacherActivityService(); // работа с беком
        teacherService.getGroupByEmailSemesterAndIdNameCourse(
                mAuth.getCurrentUser().getEmail(),
                semester,
                courseForGroup.getCourseCourseNameId(),
                TeacherActivity.this);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }

  // Обработчик нажатия кнопки Назад
  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  // информация по групе
  @Override
  public void onGroupInfoForId(StudentGroupInfo studentGroupInfo) {
    runOnUiThread(() -> {
      groupInfoTextView.setText(studentGroupInfo.toString());
    });
  }

  // получаем список студентов групы
  @Override
  public void onStudentsGroupList(List<Student> studentList) {
    runOnUiThread(() -> {
      if (studentList != null) {
        listViewAdapter = new StudentAdapter(this,
                R.layout.list_item_student,
                studentList,
                courseName,
                groupName
        );
        listView.setAdapter(listViewAdapter);
      }
    });
  }

  // получаем список предметов
  @Override
  public void onCourseByTeacherEmail(List<CourseForGroup> courseForGroups) {
    runOnUiThread(() -> {
      if (courseForGroups != null) {
        subjectSpinnerAdapter = new SubjectSpinnerAdapter(this, courseForGroups);
        subjectSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(subjectSpinnerAdapter);
      }
    });
  }

  // получаем список груп
  @Override
  public void onGroupsByTeacherEmail(List<StudentGroupSmall> studentGroupSmall) {
    runOnUiThread(() -> {
      if (studentGroupSmall != null) {
        studentGroupSpinnerAdapter = new StudentGroupSpinnerAdapter(this, studentGroupSmall);
        studentGroupSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listGroupSpinner.setAdapter(studentGroupSpinnerAdapter);
      }
    });
  }
}