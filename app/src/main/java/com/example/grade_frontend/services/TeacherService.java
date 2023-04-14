package com.example.grade_frontend.services;

import static com.example.grade_frontend.contsants.Constants.API_TEACHER;
import static com.example.grade_frontend.contsants.Constants.BASE_URL;

import androidx.annotation.NonNull;

import com.example.grade_frontend.pojo.Student;
import com.example.grade_frontend.pojo.StudentIncompleteGroup;
import com.example.grade_frontend.pojo.StudentGroupInfo;
import com.example.grade_frontend.pojo.Teacher;
import com.example.grade_frontend.services.authorization.TeacherServiceAuthorizationCallback;
import com.example.grade_frontend.services.teacher.TeacherServiceCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class TeacherService {

    /* Authorization Teacher Activity */
    /* */
    public void getTeacherInfoByEmail(String email, TeacherServiceAuthorizationCallback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASE_URL + API_TEACHER + "getTeacherInfoByEmail?email=" + email)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    Teacher teacherPojo = gson.fromJson(responseBody, Teacher.class);
                    callback.onTeacherInfoReceived(teacherPojo);
                } else {
                    callback.onTeacherInfoReceived(null);
                }
            }
        });
    }

    /* Teacher Activity */
    /*  */
    public void getGroupByTeacherEmail(String email, TeacherServiceCallback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASE_URL + API_TEACHER + "getGroupByTeacherEmail?email=" + email)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<StudentIncompleteGroup>>() {}.getType();
                    List<StudentIncompleteGroup> studentGroupList = gson.fromJson(responseBody, listType);

                    callback.onTeacherInfoForGroups(studentGroupList);
                } else {
                    callback.onTeacherInfoForGroups(null);
                }
            }
        });
    }

    /* */
    public void getGroupInfo(int id, TeacherServiceCallback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASE_URL + API_TEACHER + "getGroupInfo?id=" + id)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    StudentGroupInfo studentGroupInfo = gson.fromJson(responseBody, StudentGroupInfo.class);

                    callback.onGroupInfoForId(studentGroupInfo);
                } else {
                    callback.onGroupInfoForId(null);
                }
            }
        });
    }

    /* */
    public void getStudentsInGroup(int id, TeacherServiceCallback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASE_URL + API_TEACHER + "getGroupInfo?id=" + id)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Student>>() {}.getType();
                    List<Student> studentList = gson.fromJson(responseBody, listType);

                    callback.onStudentsGroupList(studentList);
                } else {
                    callback.onStudentsGroupList(null);
                }
            }
        });
    }


}
