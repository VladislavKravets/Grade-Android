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

public class StudentInformationService {
  private final OkHttpClient client = new OkHttpClient();

  private void makeRequest(String url, StudentInformationActivityServiceCallback callback,
                           GetNameQueries nameQueries) {
    Request request = new Request.Builder().url(url).build();

    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(@NonNull Call call, @NonNull IOException e) {
        e.printStackTrace();
        // обработка без успешного получения запроса
        switch (nameQueries) {

          case GRADE_FOR_STUDENT:
            callback.onStudentForGrade(null);
            break;
          case ABSENCE_FOR_STUDENT:
            callback.onStudentForAbsence(null);
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
            case GRADE_FOR_STUDENT: {
              // получение типа
              Type type = new TypeToken<List<Grade>>() {
              }.getType();
              // разбираем json
              Object object = gson.fromJson(responseBody, type);
              callback.onStudentForGrade((List<Grade>) object);
            }
            break;
            case ABSENCE_FOR_STUDENT: {
              // получение типа
              Type type = new TypeToken<List<Absence>>() {
              }.getType();
              // разбираем json
              Object object = gson.fromJson(responseBody, type);
              callback.onStudentForAbsence((List<Absence>) object);
            }
            break;

          }
        } else {
          onFailure(call, new IOException("Unsuccessful response"));
        }
      }
    });
  }

  //public void getGradeByStudentEmailAndDate(String email, LocalDate startDate, LocalDate endDate, StudentInformationActivityServiceCallback callback) {
  public void getGradeByStudentEmailAndDate(String email, int[] startDate, int[] endDate,
                                            StudentInformationActivityServiceCallback callback) {
    String url = BASE_URL + API_STUDENT + "getGradesByStudentEmail"
            + "?email=" + email
            + "&startDate=" + LocalDate.of(startDate[0], startDate[1], startDate[2])
            + "&endDate=" + LocalDate.of(endDate[0], endDate[1], endDate[2]);
    makeRequest(url, callback, GetNameQueries.GRADE_FOR_STUDENT);
  }

  public void getAbsenceByStudentEmailAndDate(String email, int[] startDate, int[] endDate,
                                              StudentInformationActivityServiceCallback callback) {
    String url = BASE_URL + API_STUDENT + "getAbsencesByStudentEmail"
            + "?email=" + email
            + "&startDate=" + LocalDate.of(startDate[0], startDate[1], startDate[2])
            + "&endDate=" + LocalDate.of(endDate[0], endDate[1], endDate[2]);
    makeRequest(url, callback, GetNameQueries.ABSENCE_FOR_STUDENT);
  }
}
