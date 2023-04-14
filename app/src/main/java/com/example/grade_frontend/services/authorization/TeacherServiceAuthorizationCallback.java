package com.example.grade_frontend.services.authorization;

import com.example.grade_frontend.pojo.Teacher;

public interface TeacherServiceAuthorizationCallback {
    void onTeacherInfoReceived(Teacher teacherPojo);
}
