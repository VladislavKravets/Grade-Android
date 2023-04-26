package com.example.grade_frontend.services.authorization;

import static com.example.grade_frontend.contsants.Constants.API_TEACHER;
import static com.example.grade_frontend.contsants.Constants.BASE_URL;

import androidx.annotation.NonNull;

import com.example.grade_frontend.pojo.Teacher;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AuthorizationService {
    private final OkHttpClient client = new OkHttpClient();

    private void makeRequest(String url, AuthorizationServiceCallback callback, GetNameQueries nameQueries) {
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();

                // обработка без успешного получения запроса
                switch (nameQueries) {
                    case TEACHER_OR_STUDENT: {
                        callback.verifyOnTeacherOrStudent("");
                    }break;
                }

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();

                    // обработка успешного получения запроса
                    switch (nameQueries) {
                        case TEACHER_OR_STUDENT: {
                            callback.verifyOnTeacherOrStudent(responseBody);
                        }break;

                    }

                } else {
                    onFailure(call, new IOException("Unsuccessful response"));
                }
            }
        });
    }

    /* */
    public void verifyTeacherOrStudent(String email, AuthorizationServiceCallback callback) {
        String url = BASE_URL + API_TEACHER + "getTeacherOrStudentByEmail?email=" + email;
        makeRequest(url, callback, GetNameQueries.TEACHER_OR_STUDENT);
    }
}
