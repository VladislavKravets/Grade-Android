package com.example.grade_frontend.services.authorization;

import com.example.grade_frontend.pojo.Teacher;

public interface AuthorizationServiceCallback {
    //void onTeacherInfoReceived(Teacher teacherPojo);
    void verifyOnTeacherOrStudent(String nameEntity);
}
