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
import android.widget.Switch;
import android.widget.TextView;

import com.example.grade_frontend.R;
import com.example.grade_frontend.activity.studentInformationComponent.ListAdapter;
import com.example.grade_frontend.pojo.Absence;
import com.example.grade_frontend.pojo.Grade;
import com.example.grade_frontend.services.student.StudentActivityService;
import com.example.grade_frontend.services.student.StudentActivityServiceCallback;
import com.example.grade_frontend.services.student.StudentInformationService;
import com.example.grade_frontend.services.student.StudentInformationActivityServiceCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StudentActivity extends AppCompatActivity implements StudentInformationActivityServiceCallback, StudentActivityServiceCallback {
  private FirebaseAuth mAuth = FirebaseAuth.getInstance();
  private Spinner courseSpinner;
  private ListView listView;
  private String course; // выбраный предмет
  private boolean gradeOrAbsence = false;

  private ListAdapter listViewAdapter; // адаптер вывода оценок и пропусков
  private ArrayAdapter<String> courseAdapter; // адаптер предметов

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_student);

    // initialization
    Spinner semesterSpinner = findViewById(R.id.semesterSpinner); // семестр
    courseSpinner = findViewById(R.id.courseSpinner); // предмет
    listView = findViewById(R.id.listView); // показываем либо пропуски либо оценки
    Switch switchGradeOrAbsence = findViewById(R.id.switchGradeOrAbsence); // оценки или пропуски

    /* инициализация семестров */
    Integer[] semesters = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, semesters);
    semesterSpinner.setAdapter(adapter);
    /* */

    /*  */
    TextView textView = findViewById(R.id.student_text_view);
    textView.setText(mAuth.getCurrentUser().getDisplayName());
    /* */

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

    /* Switch listener */
    // grade or absence
    switchGradeOrAbsence.setOnCheckedChangeListener((buttonView, isChecked) -> {
      gradeOrAbsence = isChecked;
      // костыльная очистка
      if(listViewAdapter != null) {
        listViewAdapter.clear();
        listViewAdapter.notifyDataSetChanged();
      }
      getGradeOrAbsence();
    });


    /* spinner listener */
    // semester
    semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int semester = (int) parent.getItemAtPosition(position);
        if(courseAdapter != null) {
          courseAdapter.clear();
          courseAdapter.notifyDataSetChanged();
        }
        StudentActivityService studentActivityService = new StudentActivityService();
        studentActivityService.getCourseByStudentEmailAndCourseSemester(
                mAuth.getCurrentUser().getEmail(),
                semester,
                StudentActivity.this);
      }
      @Override
      public void onNothingSelected(AdapterView<?> parent) {}
    });
    courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        course = (String) parent.getItemAtPosition(position); // получаем выбраный предмет
        // костыльная очистка
        if(listViewAdapter != null) {
          listViewAdapter.clear();
          listViewAdapter.notifyDataSetChanged();
        }
        getGradeOrAbsence();
      }
      @Override
      public void onNothingSelected(AdapterView<?> parent) {}
    });
  }
  // даём команду тянуть данные оценки или отсутвие
  private void getGradeOrAbsence(){
    StudentInformationService studentInformationService = new StudentInformationService();
    if (gradeOrAbsence) {
      studentInformationService.getAbsenceByStudentEmailAndDate(mAuth.getCurrentUser().getEmail(),
              LocalDate.of(2000, 1, 1),
              LocalDate.now(),
              StudentActivity.this
      );
    }else{
      studentInformationService.getGradeByStudentEmailAndDate(mAuth.getCurrentUser().getEmail(),
              LocalDate.of(2000,1,1),
              LocalDate.now(),
              StudentActivity.this
      );
    }
  }

  // получение оценок
  @Override
  public void onStudentForGrade(List<Grade> grades) {
    runOnUiThread(() -> {
      List<Grade> gradeFilterList = grades.stream()
              .filter(g -> g.getCourseName().equals(course))
              .map(g -> new Grade(g.getGrade(), g.getCourseName(), g.getCreatedAt()))
              .collect(Collectors.toList());
      if (gradeFilterList.size() == 0) {
        Snackbar.make(findViewById(android.R.id.content).getRootView(),
                "Немає оцінок по предмету",
                Snackbar.LENGTH_LONG).show();
      } else {
        List<Object> objectGrades = new ArrayList<>(gradeFilterList);
        listViewAdapter = new ListAdapter(this, objectGrades);
        listView.setAdapter(listViewAdapter);
      }
    });
  }
  // получение пропусков
  @Override
  public void onStudentForAbsence(List<Absence> absences) {
    runOnUiThread(() -> {
      List<Absence> absencesFilterList = absences.stream()
              .filter(a -> a.getCourseName().equals(course))
              .map(a -> new Absence(a.getCourseName(), a.getDate()))
              .collect(Collectors.toList());
      if (absencesFilterList.size() == 0) {
        Snackbar.make(findViewById(android.R.id.content).getRootView(),
                "Немає відсутності по предмету",
                Snackbar.LENGTH_LONG).show();
      } else {
        List<Object> objectAbsence = new ArrayList<>(absencesFilterList);
        listViewAdapter = new ListAdapter(this, objectAbsence);
        listView.setAdapter(listViewAdapter);
      }
    });
  }
  // получение предметов студента
  @Override
  public void onCourseForStudentEmail(List<String> courses) {
    runOnUiThread(() -> {
      courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
      courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      courseSpinner.setAdapter(courseAdapter);
    });
  }
}