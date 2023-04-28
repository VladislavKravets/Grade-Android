package com.example.grade_frontend.services.student;

import com.example.grade_frontend.pojo.Absence;
import com.example.grade_frontend.pojo.CourseForGroup;
import com.example.grade_frontend.pojo.Grade;
import com.example.grade_frontend.pojo.Student;
import com.example.grade_frontend.pojo.StudentGroupInfo;
import com.example.grade_frontend.pojo.StudentGroupSmall;

import java.util.List;

public interface StudentServiceCallback {
  void onStudentForGrade(List<Grade> grades);
  void onStudentForAbsence(List<Absence> absences);

}
