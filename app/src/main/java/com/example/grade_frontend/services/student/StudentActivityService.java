package com.example.grade_frontend.services.student;

import static com.example.grade_frontend.contsants.Constants.API_STUDENT;
import static com.example.grade_frontend.contsants.Constants.BASE_URL;

import androidx.annotation.NonNull;

import com.example.grade_frontend.pojo.Absence;
import com.example.grade_frontend.pojo.Grade;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StudentActivityService {
  private final OkHttpClient client = new OkHttpClient();
  private void makeRequest(String url, StudentActivityServiceCallback callback, GetNameQueries nameQueries) {
    Request request = new Request.Builder().url(url).build();
    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(@NonNull Call call, @NonNull IOException e) {
        e.printStackTrace();
        // обработка без успешного получения запроса
        switch (nameQueries) {
          case COURSE_FOR_STUDENT:
            callback.onCourseForStudentEmail(null);
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
            case COURSE_FOR_STUDENT: {
              // получение типа
              Type type = new TypeToken<List<String>>() {}.getType();
              // разбираем json
              Object object = gson.fromJson(responseBody, type);
              callback.onCourseForStudentEmail((List<String>) object);
            } break;
          }
        } else {
          onFailure(call, new IOException("Unsuccessful response"));
        }
      }
    });
  }

  public void getCourseByStudentEmailAndCourseSemester(String email, int semester,
                                            StudentActivityServiceCallback callback) {
    String url = BASE_URL + API_STUDENT + "getCoursesByEmailAndSemester"
            + "?email=" + email
            + "&semester=" + semester;
    makeRequest(url, callback, GetNameQueries.COURSE_FOR_STUDENT);
  }
}
