package com.example.grade_frontend.services.student;

import java.util.List;

public interface StudentActivityServiceCallback {
  void onCourseForStudentEmail(List<String> courses);
}
