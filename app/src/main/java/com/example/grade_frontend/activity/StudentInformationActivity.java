package com.example.grade_frontend.activity;

import static com.example.grade_frontend.activity.teacherActivityComponent.Status.GET_ABSENCE;
import static com.example.grade_frontend.activity.teacherActivityComponent.Status.GET_GRADE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.example.grade_frontend.R;
import com.example.grade_frontend.activity.studentActivityComponent.ListAdapter;
import com.example.grade_frontend.pojo.Absence;
import com.example.grade_frontend.pojo.Grade;
import com.example.grade_frontend.pojo.Student;
import com.example.grade_frontend.services.student.StudentInformationActivityServiceCallback;
import com.example.grade_frontend.services.student.StudentInformationService;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class StudentInformationActivity extends AppCompatActivity implements StudentInformationActivityServiceCallback {
  private Student student; // наш полученый студент из предыдущей активити
  private Enum status; // тип запроса (оценки/пропуски)
  private String course;
  private int courseId;
  private int semester;

  private ListView listView;
  private TextView textView;
  private TextView infoTextView;
  private Button dateInput;

  private Button addDataButton;
  private Button absenceButton;

  private String fromDate, toDate;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_student_information);

    // initialization
    listView = findViewById(R.id.listView);
    dateInput = findViewById(R.id.dateInput);
    infoTextView = findViewById(R.id.infoTextView);
    addDataButton = findViewById(R.id.addDataButton);
    addDataButton.setOnClickListener(v -> showInputDialog());
    absenceButton = findViewById(R.id.absenceButton);

    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    dateInput.setText("Виберіть потрібний діапазон дат");

    // Создание диалога выбора диапазона дат
    MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
    builder.setTitleText("Выберіть проміжок часу");

    // инициализация календаря
    MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();

    student = (Student) getIntent().getSerializableExtra("student");
    course = (String) getIntent().getSerializableExtra("courseName");
    courseId = (int) getIntent().getSerializableExtra("courseId");
    semester = (int) getIntent().getSerializableExtra("semester");
    status = (Enum) getIntent().getSerializableExtra("status");

    if (status.equals(GET_GRADE)) {
      addDataButton.setVisibility(View.VISIBLE);
    } else {
      StudentInformationService studentInformationService = new StudentInformationService();
      studentInformationService.getboolAbsence(student.getId(), courseId, semester, this);
    }

    String groupName = (String) getIntent().getSerializableExtra("groupName");

    setTitle("Студент: " + student.toString());

    textView = findViewById(R.id.student_text_view);
    textView.setText("Група: " + groupName + "\n" + (status.equals(GET_GRADE) ? "Оцінки" : "Пропуски" + " студента"));


    dateInput.setOnClickListener(v -> {
      // Отображение диалога выбора даты
      materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
    });

    // Установка слушателя для получения выбранного диапазона дат
    materialDatePicker.addOnPositiveButtonClickListener(selection -> {
      // Обработка выбранного диапазона дат
      Date firstDate = new Date(selection.first);
      Date secondDate = new Date(selection.second);
      fromDate = sdf.format(firstDate);
      toDate = sdf.format(secondDate);
      dateInput.setText("Від: " + fromDate + " до " + toDate);
      getGradesOrAbsence();
    });

    absenceButton.setOnClickListener(v -> {
      StudentInformationService studentInformationService = new StudentInformationService();
      studentInformationService.postAbsenceForStudent(student.getId(), courseId, semester, this);
      absenceButton.setVisibility(View.INVISIBLE);
    });
  }

  private void getGradesOrAbsence() {
    StudentInformationService studentService = new StudentInformationService();
    if (GET_GRADE.equals(status)) {
      studentService.getGradeByStudentEmailAndDate(student.getEmail(), fromDate, toDate, this);
    } else if (GET_ABSENCE.equals(status)) {
      studentService.getAbsenceByStudentEmailAndDate(student.getEmail(), fromDate, toDate, this);
    }
  }

  @Override
  public void onStudentForGrade(List<Grade> grades) {
    runOnUiThread(() -> {
      List<Grade> gradeFilterList = grades.stream().filter(g -> g.getCourseName().equals(course)).map(g -> new Grade(g.getGrade(), g.getCourseName(), g.getCreatedAt())).collect(Collectors.toList());
      if (gradeFilterList.size() == 0) {
        Snackbar.make(findViewById(android.R.id.content).getRootView(), "Немає оцінок по предмету", Snackbar.LENGTH_LONG).show();
      } else {
        infoTextView.setText("\nСередній бал по предмету: " + Math.round(calculateAverageGrade(gradeFilterList)));
        List<Object> objectGrades = new ArrayList<>(gradeFilterList);
        ListAdapter adapter = new ListAdapter(this, objectGrades);
        listView.setAdapter(adapter);
      }
    });
  }

  @Override
  public void onStudentForAbsence(List<Absence> absences) {
    runOnUiThread(() -> {
      List<Absence> absencesFilterList = absences.stream().filter(a -> a.getCourseName().equals(course)).map(a -> new Absence(a.getCourseName(), a.getDate())).collect(Collectors.toList());
      if (absencesFilterList.size() == 0) {
        Snackbar.make(findViewById(android.R.id.content).getRootView(), "Немає відсутності по предмету", Snackbar.LENGTH_LONG).show();
      } else {
        infoTextView.setText("\nКількість пропусків по предмету: " + absencesFilterList.size());
        List<Object> objectAbsence = new ArrayList<>(absencesFilterList);
        ListAdapter adapter = new ListAdapter(this, objectAbsence);
        listView.setAdapter(adapter);
      }
    });
  }

  // добавить обработку добавления оценки
  @Override
  public void onPOSTStudentForGrade() {
    runOnUiThread(() -> {

    });
  }
  // добавить обработку добавления отсутвия
  @Override
  public void onPOSTStudentForAbsence() {
    runOnUiThread(() -> {

    });
  }

  @Override
  public void onStudentForAbsenceForDate(boolean active) {
    runOnUiThread(() -> {
      if (!active)
        absenceButton.setVisibility(View.VISIBLE);
      else
        absenceButton.setVisibility(View.INVISIBLE);
    });
  }

  private double calculateAverageGrade(List<Grade> grades) {
    int sum = 0;
    for (Grade grade : grades) {
      sum += grade.getGrade();
    }
    return (double) sum / grades.size();
  }

  private void showInputDialog() {
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
    LayoutInflater inflater = getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_input, null);
    dialogBuilder.setView(dialogView);

    final EditText inputEditText = dialogView.findViewById(R.id.gradeInput);

    dialogBuilder.setTitle("Додати оцінку");
    dialogBuilder.setPositiveButton("Add", (dialog, whichButton) -> {
      String inputData = inputEditText.getText().toString();
      StudentInformationService studentInformationService = new StudentInformationService();
      studentInformationService.postGradeForStudent(
              student.getId(),
              courseId,
              Integer.parseInt(inputData),
              semester, this);
    });
    dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.cancel());

    AlertDialog alertDialog = dialogBuilder.create();
    alertDialog.show();
  }

}