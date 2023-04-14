package com.example.grade_frontend.services.teacher;

import com.example.grade_frontend.pojo.Student;
import com.example.grade_frontend.pojo.StudentGroup;
import com.example.grade_frontend.pojo.StudentGroupInfo;

import java.util.List;

public interface TeacherServiceCallback {
    void onTeacherInfoForGroups(List<StudentGroup> studentGroupList);
    void onGroupInfoForId(StudentGroupInfo studentGroupInfo);
    void onStudentsGroupList(List<Student> studentList);

}
