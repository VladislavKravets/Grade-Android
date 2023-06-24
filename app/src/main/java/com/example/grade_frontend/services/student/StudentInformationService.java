package com.example.grade_frontend.services.student;

import static com.example.grade_frontend.contsants.Constants.API_STUDENT;
import static com.example.grade_frontend.contsants.Constants.API_TEACHER;
import static com.example.grade_frontend.contsants.Constants.BASE_URL;

import android.text.BoringLayout;

import androidx.annotation.NonNull;

import com.example.grade_frontend.pojo.Absence;
import com.example.grade_frontend.pojo.Grade;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
          case BOOL_ABSENCE_FOR_DATE:
            callback.onStudentForAbsenceForDate(false);
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
            case BOOL_ABSENCE_FOR_DATE: {
              // получение типа
              Type type = new TypeToken<Boolean>() {
              }.getType();
              // разбираем json
              Object object = gson.fromJson(responseBody, type);
              callback.onStudentForAbsenceForDate((boolean) object);
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
  public void getGradeByStudentEmailAndDate(String email, String startDate, String endDate,
                                            StudentInformationActivityServiceCallback callback) {
    String url = BASE_URL + API_STUDENT + "getGradesByStudentEmail"
            + "?email=" + email
            + "&startDate=" + stringToLocalDate(startDate)
            + "&endDate=" + stringToLocalDate(endDate);
    makeRequest(url, callback, GetNameQueries.GRADE_FOR_STUDENT);
  }

  public void getAbsenceByStudentEmailAndDate(String email, String startDate, String endDate,
                                              StudentInformationActivityServiceCallback callback) {
    String url = BASE_URL + API_STUDENT + "getAbsencesByStudentEmail"
            + "?email=" + email
            + "&startDate=" + stringToLocalDate(startDate)
            + "&endDate=" + stringToLocalDate(endDate);
    makeRequest(url, callback, GetNameQueries.ABSENCE_FOR_STUDENT);
  }

  public void getboolAbsence(Long studentId, int courseId, int semester,
                             StudentInformationActivityServiceCallback callback) {
    String url = BASE_URL + API_TEACHER + "getBoolAbsenceNow"
            + "?studentId=" + studentId
            + "&courseId=" + courseId
            + "&semester=" + semester
            + "&date=" + LocalDate.now();
    makeRequest(url, callback, GetNameQueries.BOOL_ABSENCE_FOR_DATE);
  }

  public void postGradeForStudent(Long studentId, int courseId, int grade, int semester,
                                  StudentInformationActivityServiceCallback callback) {
    String url = BASE_URL + API_TEACHER + "setGradeForStudentId"
            + "?studentId=" + studentId
            + "&courseId=" + courseId
            + "&grade=" + grade
            + "&semester=" + semester
            + "&date=" + LocalDate.now();

    // Формування запиту POST
    RequestBody requestBody = new FormBody.Builder()
            .add("studentId", String.valueOf(studentId))
            .add("courseId", String.valueOf(courseId))
            .add("grade", String.valueOf(grade))
            .add("date", String.valueOf(LocalDate.now()))
            .build();

    Request request = new Request.Builder()
            .url(url)
            .post(requestBody)
            .build();

    // Виконання запиту
    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(@NonNull Call call, @NonNull IOException e) {
        e.printStackTrace();
        // Обробка неуспішного запиту
        callback.onPOSTStudentForGrade();
      }

      @Override
      public void onResponse(@NonNull Call call, @NonNull Response response) {
        if (response.isSuccessful()) {
          // Обробка успішного запиту
          callback.onPOSTStudentForGrade();
        } else {
          onFailure(call, new IOException("Unsuccessful response"));
        }
      }
    });
  }

  public void postAbsenceForStudent(Long studentId, int courseId, int semester,
                                    StudentInformationActivityServiceCallback callback) {
    String url = BASE_URL + API_TEACHER + "setAbsenceForStudentId"
            + "?studentId=" + studentId
            + "&courseId=" + courseId
            + "&semester=" + semester
            + "&date=" + LocalDate.now();

    // Формування запиту POST
    RequestBody requestBody = new FormBody.Builder()
            .add("studentId", String.valueOf(studentId))
            .add("courseId", String.valueOf(courseId))
            .add("date", String.valueOf(LocalDate.now()))
            .build();

    Request request = new Request.Builder()
            .url(url)
            .post(requestBody)
            .build();

    // Виконання запиту
    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(@NonNull Call call, @NonNull IOException e) {
        e.printStackTrace();
        // Обробка неуспішного запиту
        callback.onPOSTStudentForAbsence();
      }

      @Override
      public void onResponse(@NonNull Call call, @NonNull Response response) {
        if (response.isSuccessful()) {
          // Обробка успішного запиту
          callback.onPOSTStudentForAbsence();
        } else {
          onFailure(call, new IOException("Unsuccessful response"));
        }
      }
    });
  }

  private LocalDate stringToLocalDate(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    return LocalDate.parse(date, formatter);
  }
}
