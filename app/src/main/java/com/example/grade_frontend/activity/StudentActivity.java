package com.example.grade_frontend.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.grade_frontend.R;
import com.example.grade_frontend.activity.studentActivityComponent.DatePickerFragment;
import com.example.grade_frontend.activity.studentActivityComponent.ListAdapter;
import com.example.grade_frontend.pojo.Absence;
import com.example.grade_frontend.pojo.Grade;
import com.example.grade_frontend.services.student.StudentActivityService;
import com.example.grade_frontend.services.student.StudentActivityServiceCallback;
import com.example.grade_frontend.services.student.StudentInformationService;
import com.example.grade_frontend.services.student.StudentInformationActivityServiceCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class StudentActivity extends AppCompatActivity implements StudentInformationActivityServiceCallback, StudentActivityServiceCallback {
  private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

  // android vidgets
  private Spinner courseSpinner;
  private ListView listView;
  private ProgressBar loadingListView;
  // states
  private String course; // выбраный предмет
  private boolean gradeOrAbsence = false; // пропуски или оценки

  // диапазон дат между оценками / пропусками
  // получаем текущии значение по умолчанию
  private final int year = LocalDate.now().getYear();
  private final int month = LocalDate.now().getMonthValue();
  private final int day = LocalDate.now().getDayOfMonth();
  // загружаем в фильтр по умолчанию что бы было промежуток от год -2 - год
  private int[] fromDate = {year-2, month, day};
  private int[] toDate = {year, month, day};

  // adapters
  private ListAdapter listViewAdapter; // адаптер вывода оценок и пропусков
  private ArrayAdapter<String> courseAdapter; // адаптер предметов

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_student);
    // Установить кастомный заголовок
    getSupportActionBar().setDisplayShowCustomEnabled(true);
    getSupportActionBar().setCustomView(R.layout.active_user_title);

    // initialization
    Spinner semesterSpinner = findViewById(R.id.semesterSpinner); // семестр
    courseSpinner = findViewById(R.id.courseSpinner); // предмет
    listView = findViewById(R.id.listView); // показываем либо пропуски либо оценки
    loadingListView = findViewById(R.id.loadingListView); // загрузка
    Switch switchGradeOrAbsence = findViewById(R.id.switchGradeOrAbsence); // оценки или пропуски
    TextView titleText = findViewById(R.id.title_text);
    Button titleButton = findViewById(R.id.title_button);

    MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
    materialDateBuilder.setTitleText("SELECT DATE");
    final MaterialDatePicker materialFromDatePicker = materialDateBuilder.build();
    final MaterialDatePicker materialToDatePicker = materialDateBuilder.build();

    // title text
    titleText.setText("Студент " + mAuth.getCurrentUser().getDisplayName());

    /* инициализация семестров */
    Integer[] semesters = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, semesters);
    semesterSpinner.setAdapter(adapter);
    /* */

    /*  */
//    TextView textView = findViewById(R.id.student_text_view);
//    textView.setText(mAuth.getCurrentUser().getDisplayName());
    /* */

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

    Button fromDateButton = findViewById(R.id.show_from_date_button);
    fromDateButton.setText(fromDate[0] + "-" + fromDate[1] + "-" + fromDate[2]);
    fromDateButton.setOnClickListener(v -> {
      materialFromDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
    });

    materialFromDatePicker.addOnPositiveButtonClickListener(selection -> {
      Date date = new Date((Long) selection);
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
      String formattedDate = dateFormat.format(date);
      fromDateButton.setText(formattedDate);
      fromDate = getArrayDateToString(formattedDate);
      getGradeOrAbsence();
    });

    Button toDateButton = findViewById(R.id.show_to_date_button);
    toDateButton.setText(toDate[0] + "-" + toDate[1] + "-" + toDate[2]);
    toDateButton.setOnClickListener(v -> {
      materialToDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
    });

    materialToDatePicker.addOnPositiveButtonClickListener(selection -> {
      Date date = new Date((Long) selection);
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
      String formattedDate = dateFormat.format(date);
      toDateButton.setText(formattedDate);
      toDate = getArrayDateToString(formattedDate);
      getGradeOrAbsence();
    });

    /* Switch listener */
    // grade or absence
    switchGradeOrAbsence.setOnCheckedChangeListener((buttonView, isChecked) -> {
      gradeOrAbsence = isChecked;
      // костыльная очистка
      if (listViewAdapter != null) {
        listViewAdapter.clear();
        listViewAdapter.notifyDataSetChanged();
      }
      getGradeOrAbsence(); // тянем данные
    });

    /* spinner listener */
    // semester
    semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int semester = (int) parent.getItemAtPosition(position);
        if (courseAdapter != null) {
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
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
    courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        course = (String) parent.getItemAtPosition(position); // получаем выбраный предмет
        // костыльная очистка
        if (listViewAdapter != null) {
          listViewAdapter.clear();
          listViewAdapter.notifyDataSetChanged();
        }
        getGradeOrAbsence(); // тянем данные
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }

  // даём команду тянуть данные оценки или отсутвие
  private void getGradeOrAbsence() {
    listView.setVisibility(View.INVISIBLE);
    loadingListView.setVisibility(View.VISIBLE);
    if (listViewAdapter != null) {
      listViewAdapter.clear();
      listViewAdapter.notifyDataSetChanged();
    }
    StudentInformationService studentInformationService = new StudentInformationService();
    if (gradeOrAbsence) {
      studentInformationService.getAbsenceByStudentEmailAndDate(mAuth.getCurrentUser().getEmail(),
              fromDate,
              toDate,
              StudentActivity.this
      );
    } else {
      studentInformationService.getGradeByStudentEmailAndDate(mAuth.getCurrentUser().getEmail(),
              fromDate,
              toDate,
              StudentActivity.this
      );
    }
  }

  private int[] getArrayDateToString(String dateString) {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate localDate = LocalDate.parse(dateString, dateFormatter);
    int year = localDate.getYear();
    int month = localDate.getMonthValue();
    int day = localDate.getDayOfMonth();
    return new int[]{year, month, day};
  }

  // получение оценок
  @Override
  public void onStudentForGrade(List<Grade> grades) {
    runOnUiThread(() -> {
      listView.setVisibility(View.VISIBLE);
      loadingListView.setVisibility(View.INVISIBLE);

      if (grades != null) {
        listView.setVisibility(View.VISIBLE);
        List<Grade> gradeFilterList = grades.stream()
                .filter(g -> g.getCourseName().equals(course))
                .map(g -> new Grade(g.getGrade(), g.getCourseName(), g.getCreatedAt()))
                .collect(Collectors.toList());
        if (gradeFilterList.size() == 0) {
          Snackbar.make(findViewById(android.R.id.content).getRootView(),
                  "Немає оцінок по предмету, за вибраний проміжок часу",
                  Snackbar.LENGTH_LONG).show();
        } else {
          List<Object> objectGrades = new ArrayList<>(gradeFilterList);
          listViewAdapter = new ListAdapter(this, objectGrades);
          listView.setAdapter(listViewAdapter);
        }
      } else {
        Snackbar.make(findViewById(android.R.id.content).getRootView(),
                "Помилка підключення до серверу",
                Snackbar.LENGTH_LONG).show();
      }
    });
  }

  // получение пропусков
  @Override
  public void onStudentForAbsence(List<Absence> absences) {
    runOnUiThread(() -> {
      listView.setVisibility(View.VISIBLE);
      loadingListView.setVisibility(View.INVISIBLE);

      if (absences != null) {
        List<Absence> absencesFilterList = absences.stream()
                .filter(a -> a.getCourseName().equals(course))
                .map(a -> new Absence(a.getCourseName(), a.getDate()))
                .collect(Collectors.toList());
        if (absencesFilterList.size() == 0) {
          Snackbar.make(findViewById(android.R.id.content).getRootView(),
                  "Немає відсутності по предмету, за вибраний проміжок часу",
                  Snackbar.LENGTH_LONG).show();
        } else {
          List<Object> objectAbsence = new ArrayList<>(absencesFilterList);
          listViewAdapter = new ListAdapter(this, objectAbsence);
          listView.setAdapter(listViewAdapter);
        }
      } else {
        Snackbar.make(findViewById(android.R.id.content).getRootView(),
                "Помилка підключення до серверу",
                Snackbar.LENGTH_LONG).show();
      }
    });
  }

  // получение предметов студента
  @Override
  public void onCourseForStudentEmail(List<String> courses) {
    runOnUiThread(() -> {
      if (courses != null) {
        courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(courseAdapter);
      } else {
        Snackbar.make(findViewById(android.R.id.content).getRootView(),
                "Помилка підключення до серверу",
                Snackbar.LENGTH_LONG).show();
      }
    });
  }
}