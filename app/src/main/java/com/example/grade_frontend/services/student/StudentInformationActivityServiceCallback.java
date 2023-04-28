package com.example.grade_frontend.services.student;

import com.example.grade_frontend.pojo.Absence;
import com.example.grade_frontend.pojo.Grade;

import java.util.List;

public interface StudentInformationActivityServiceCallback {
  void onStudentForGrade(List<Grade> grades);
  void onStudentForAbsence(List<Absence> absences);

}
