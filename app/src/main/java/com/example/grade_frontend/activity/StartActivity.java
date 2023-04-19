package com.example.grade_frontend.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.grade_frontend.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);

        if (prefs.contains("userId")) {
            // Пользователь уже аутентифицирован
            if (prefs.getString("role", "").equals("teacher")) {
                Intent intent = new Intent(getApplicationContext(), TeacherActivity.class);
                startActivity(intent);
                finish();
            } else if (prefs.getString("role", "").equals("student")) {
                Intent intent = new Intent(getApplicationContext(), StudentActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            // Пользователь еще не аутентифицирован
            Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
            startActivity(intent);
            finish();
        }
    }
}