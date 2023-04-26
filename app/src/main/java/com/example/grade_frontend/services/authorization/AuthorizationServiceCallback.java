package com.example.grade_frontend.services.authorization;

import com.example.grade_frontend.pojo.Teacher;

public interface AuthorizationServiceCallback {
    void verifyOnTeacherOrStudent(String nameEntity);
}
