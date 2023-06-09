package com.example.grade_frontend.services.teacher;

import static com.example.grade_frontend.contsants.Constants.API_TEACHER;
import static com.example.grade_frontend.contsants.Constants.BASE_URL;

import androidx.annotation.NonNull;

import com.example.grade_frontend.pojo.CourseForGroup;
import com.example.grade_frontend.pojo.Student;
import com.example.grade_frontend.pojo.StudentGroupInfo;

import com.example.grade_frontend.pojo.StudentGroupSmall;
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

public class TeacherActivityService {
  private final OkHttpClient client = new OkHttpClient();

  private void makeRequest(String url, TeacherActivityServiceCallback callback, GetNameQueries nameQueries) {
    Request request = new Request.Builder().url(url).build();

    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(@NonNull Call call, @NonNull IOException e) {
        e.printStackTrace();
        // обработка без успешного получения запроса
        switch (nameQueries) {
          case STUDENT_GROUP_LIST:
            callback.onStudentsGroupList(null);
            break;

//          case STUDENT_INCOMPLETE_GROUP_LIST:
//            callback.onTeacherInfoForGroups(null);
//            break;

          case STUDENT_GROUP_INFO:
            callback.onGroupInfoForId(null);
            break;

          case GET_SUBJECTS_FOR_SEMESTER:
            callback.onCourseByTeacherEmail(null);
            break;

          case GET_GROUPS_FOR_SEMESTER:
            callback.onGroupsByTeacherEmail(null);
            break;
        }
      }

      // обработка успешного выполнения запроса
      @Override
      public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        if (response.isSuccessful()) {
          String responseBody = response.body().string();
          Gson gson = new Gson();

          // обработка успешного получения запроса
          switch (nameQueries) {
            case STUDENT_GROUP_LIST: {
              // получение типа
              Type type = new TypeToken<List<Student>>() {
              }.getType();
              // разбираем json
              Object object = gson.fromJson(responseBody, type);
              callback.onStudentsGroupList((List<Student>) object);
            }
            break;

//            case STUDENT_INCOMPLETE_GROUP_LIST: {
//              Type type = new TypeToken<List<CourseForGroups>>() {
//              }.getType();
//              Object object = gson.fromJson(responseBody, type);
//              callback.onTeacherInfoForGroups((List<CourseForGroups>) object);
//            }
//            break;

            case STUDENT_GROUP_INFO: {
              StudentGroupInfo studentGroupInfo = gson.fromJson(responseBody, StudentGroupInfo.class);
              callback.onGroupInfoForId(studentGroupInfo);
            }
            break;

            case GET_SUBJECTS_FOR_SEMESTER: {
              Type type = new TypeToken<List<CourseForGroup>>() {
              }.getType();
              Object object = gson.fromJson(responseBody, type);
              callback.onCourseByTeacherEmail((List<CourseForGroup>) object);
            } break;

            case GET_GROUPS_FOR_SEMESTER: {
              Type type = new TypeToken<List<StudentGroupSmall>>() {
              }.getType();
              Object object = gson.fromJson(responseBody, type);
              callback.onGroupsByTeacherEmail((List<StudentGroupSmall>) object);
            } break;


          }

        } else {
          onFailure(call, new IOException("Unsuccessful response"));
        }
      }
    });
  }

//  public void getGroupByTeacherEmail(String email, TeacherServiceCallback callback) {
//    String url = BASE_URL + API_TEACHER + "getGroupByTeacherEmail?email=" + email;
//    makeRequest(url, callback, GetNameQueries.STUDENT_INCOMPLETE_GROUP_LIST);
//  }

  public void getGroupInfo(int id, TeacherActivityServiceCallback callback) {
    String url = BASE_URL + API_TEACHER + "getGroupInfo?id=" + id;
    makeRequest(url, callback, GetNameQueries.STUDENT_GROUP_INFO);
  }

  public void getStudentsInGroup(int id, TeacherActivityServiceCallback callback) {
    String url = BASE_URL + API_TEACHER + "getStudentsByStudentGroupId?id=" + id;
    makeRequest(url, callback, GetNameQueries.STUDENT_GROUP_LIST);
  }

  public void getCourseByTeacherEmail(String email, int semester, TeacherActivityServiceCallback callback) {
    String url = BASE_URL + API_TEACHER +
            "getCourseByTeacherEmail?email=" + email + "&" + "semester=" + semester;
    makeRequest(url, callback, GetNameQueries.GET_SUBJECTS_FOR_SEMESTER);
  }

  public void getGroupByEmailSemesterAndIdNameCourse(String email, int semester, int id, TeacherActivityServiceCallback callback) {
    String url = BASE_URL + API_TEACHER +
            "getGroupByEmailSemesterAndIdNameCourse?"
            + "email=" + email + "&"
            + "semester=" + semester + "&"
            + "id=" + id;
    makeRequest(url, callback, GetNameQueries.GET_GROUPS_FOR_SEMESTER);
  }
}
