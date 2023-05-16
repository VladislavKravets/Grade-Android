package com.example.grade_frontend.services.teacher;

import com.example.grade_frontend.pojo.CourseForGroup;
import com.example.grade_frontend.pojo.Student;
import com.example.grade_frontend.pojo.StudentGroupInfo;
import com.example.grade_frontend.pojo.StudentGroupSmall;

import java.util.List;

public interface TeacherActivityServiceCallback {
//    void onTeacherInfoForGroups(List<CourseForGroups> studentGroupList);
    void onGroupInfoForId(StudentGroupInfo studentGroupInfo);
    void onStudentsGroupList(List<Student> studentList);
    void onCourseByTeacherEmail(List<CourseForGroup> courseForGroups);
    void onGroupsByTeacherEmail(List<StudentGroupSmall> studentGroupSmall);
}
