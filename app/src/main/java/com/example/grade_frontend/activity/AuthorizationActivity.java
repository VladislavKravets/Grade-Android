package com.example.grade_frontend.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.grade_frontend.R;
import com.example.grade_frontend.pojo.Teacher;
import com.example.grade_frontend.services.TeacherService;
import com.example.grade_frontend.services.authorization.TeacherServiceAuthorizationCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthorizationActivity extends AppCompatActivity implements TeacherServiceAuthorizationCallback {

    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;

    private TeacherService teacherService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Получение объекта GoogleSignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Получение объекта FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Отображение кнопки для аутентификации через Google
        Button signGoogleInButton = findViewById(R.id.google_auth_button);
        signGoogleInButton.setOnClickListener(view -> loginWithGoogle());

    }

    // формируем url для google
    private void loginWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // гугл авторизация
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Аутентификация в Firebase с помощью учетных данных Google
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, task1 -> {
                            if (task1.isSuccessful()) {
                                // Инициализация объекта TeacherService
                                teacherService = new TeacherService();
                                // Вызов метода getTeacherInfoByEmail
                                teacherService.getTeacherInfoByEmail(account.getEmail(), this);
                            } else {
                                // Обработка ошибок при аутентификации
                                Toast.makeText(AuthorizationActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (ApiException e) {
                // Обработка ошибок при выборе учетной записи Google
                Toast.makeText(AuthorizationActivity.this,
                        "Google sign in failed.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onTeacherInfoReceived(Teacher teacherPojo) {
        if (teacherPojo != null) {
            Intent intent = new Intent(this, TeacherActivity.class);
            startActivity(intent);
            finish();
        }
    }


}