package com.example.grade_frontend.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.example.grade_frontend.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class StudentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        TextView textView = findViewById(R.id.student_text_view);
        textView.setText(mAuth.getCurrentUser().getDisplayName());

        // выход из аккаунта и очистка данных с мобилки
        findViewById(R.id.logout_btn).setOnClickListener(e -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // очищаем все значения
            editor.apply(); // сохраняем изменения

            mAuth.signOut();
            GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();

            Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
            startActivity(intent);
            finish();
        });
    }
}