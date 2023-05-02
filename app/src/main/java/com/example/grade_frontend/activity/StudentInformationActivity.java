package com.example.grade_frontend.activity;

import static com.example.grade_frontend.activity.teacherActivityComponent.Status.GET_ABSENCE;
import static com.example.grade_frontend.activity.teacherActivityComponent.Status.GET_GRADE;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.grade_frontend.R;
import com.example.grade_frontend.activity.studentActivityComponent.ListAdapter;
import com.example.grade_frontend.pojo.Absence;
import com.example.grade_frontend.pojo.Grade;
import com.example.grade_frontend.pojo.Student;
import com.example.grade_frontend.services.student.StudentInformationService;
import com.example.grade_frontend.services.student.StudentInformationActivityServiceCallback;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StudentInformationActivity extends AppCompatActivity implements StudentInformationActivityServiceCallback {
  private String course;
  private ListView listView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_student_information);

    // initialization
    listView = findViewById(R.id.listView);

    //
    StudentInformationService studentService = new StudentInformationService();

    Student student = (Student) getIntent().getSerializableExtra("student");
    course = (String) getIntent().getSerializableExtra("courseName");
    Enum status = (Enum) getIntent().getSerializableExtra("status");
    String groupName = (String) getIntent().getSerializableExtra("groupName");

    if (GET_GRADE.equals(status)) {
      studentService.getGradeByStudentEmailAndDate(student.getEmail(),
              new int[] {2000,1,1},
              new int[] {2023,1,1},
//              LocalDate.of(2000, 1, 1),
//              LocalDate.now(),
              this
      );
    } else if (GET_ABSENCE.equals(status)) {
      studentService.getAbsenceByStudentEmailAndDate(student.getEmail(),
              new int[] {2000,1,1},
              new int[] {2023,1,1},
//              LocalDate.of(2000, 1, 1),
//              LocalDate.now(),
              this
      );
    }

    setTitle("Студент: " + student.toString());

    TextView textView = findViewById(R.id.student_text_view);
    textView.setText("Група: " +  groupName + "\n"
            + (status.equals(GET_GRADE) ? "Оцінки" : "Пропуски" + " студента"));
  }

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
        ListAdapter adapter = new ListAdapter(this, objectGrades);
        listView.setAdapter(adapter);
      }
    });
  }

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
        ListAdapter adapter = new ListAdapter(this, objectAbsence);
        listView.setAdapter(adapter);
      }
    });
  }
}