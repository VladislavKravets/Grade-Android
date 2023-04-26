package com.example.grade_frontend.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.grade_frontend.R;
import com.example.grade_frontend.pojo.Teacher;
import com.example.grade_frontend.services.authorization.AuthorizationService;
import com.example.grade_frontend.services.authorization.AuthorizationServiceCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthorizationActivity extends AppCompatActivity implements AuthorizationServiceCallback {

    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;

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

        // Очистка автосохранения выбора
        mAuth.signOut();
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();

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
                                // Инициализация объекта TeacherAuthorization
                                AuthorizationService authorizationService = new AuthorizationService();
                                authorizationService.verifyTeacherOrStudent(account.getEmail(), this);
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
    public void verifyOnTeacherOrStudent(String nameEntity) {
        FirebaseUser user = mAuth.getCurrentUser();

        // Сохранение данных пользователя в SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences(
                "UserData", MODE_PRIVATE
        ).edit();

        editor.putString("userId", user.getUid());
        editor.putString("email", user.getEmail());

        switch (nameEntity) {
            case "teacher": {
                editor.putString("role", "teacher");
                editor.apply();

                Intent intent = new Intent(this, TeacherActivity.class);
                startActivity(intent);
                finish();
            }break;
            case "student": {
                editor.putString("role", "student");
                editor.apply();

                Intent intent = new Intent(this, StudentActivity.class);
                startActivity(intent);
                finish();
            }break;
            default: {
                mAuth.signOut();
                GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();

                Snackbar.make(findViewById(android.R.id.content).getRootView(),
                        "Помилка вашої ролі немає в базі даних",
                        Snackbar.LENGTH_LONG).show();

            }
        }
    }


}