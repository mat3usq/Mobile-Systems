package com.mobile.bankapp;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.bankapp.database.DataProvider;
import com.mobile.bankapp.database.DataSource;
import com.mobile.bankapp.model.User;

public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        ShowingSecurityQuestionFragment.FromCallBack {
    // Deklaracja zmiennych klasy
    public static final String ENABLE_LOGIN = "ENABLE_LOGIN";
    private DataSource mDataSource;
    private User user;
    private EditText emailEdt;
    private EditText passwordEdt;
    private String email, password;
    private TextView forgotPassword;

    public static final String SECURITY_QUESTION = "security_question";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ustawienie layoutu dla tej aktywności
        setContentView(R.layout.activity_login);

        // Inicjalizacja źródła danych
        mDataSource = new DataSource(this);

        // Próba pobrania danych użytkownika
        try {
            user = mDataSource.getUser();
        } catch (Exception e) {
            // Wyświetlenie komunikatu, jeśli użytkownik nie zostanie znaleziony
            Toast.makeText(this, R.string.login_no_user, Toast.LENGTH_SHORT).show();
        }

        // Inicjalizacja pól tekstowych i przycisków
        emailEdt = findViewById(R.id.email);
        passwordEdt = findViewById(R.id.password);
        forgotPassword = findViewById(R.id.forgot_password);
        Button signIn = findViewById(R.id.sign_in_button);
        Button register = findViewById(R.id.register);

        // Ustawienie listenerów dla przycisków
        signIn.setOnClickListener(listener);
        register.setOnClickListener(listener);
        forgotPassword.setOnClickListener(listener);

        // Inicjalizacja LoaderManagera
        getLoaderManager().initLoader(0, null, this);
    }

    // Listener obsługujący kliknięcia na elementach UI
    View.OnClickListener listener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.sign_in_button:
                    // Sprawdzenie poprawności danych logowania
                    checkValidity();
                    break;
                case R.id.register:
                    // Przejście do aktywności rejestracji
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.forgot_password:
                    // Obsługa zapomnianego hasła
                    if (user != null) {
                        promptSecurityQuestion();
                    } else {
                        Toast.makeText(LoginActivity.this, "Please create a new account", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    // Metoda do sprawdzania poprawności danych logowania
    private void checkValidity() {
        // Pobranie wprowadzonych danych
        email = emailEdt.getText().toString();
        password = passwordEdt.getText().toString();

        // Utworzenie AlertDialog do wyświetlenia błędów
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.sign_in_error);

        // Walidacja e-maila i hasła
        if (email.isEmpty() || password.isEmpty()) {
            builder.setMessage(R.string.sign_in_error_prompt_email_password);
            builder.setPositiveButton("OK", null).create().show();
            return;
        }

        // Sprawdzenie, czy użytkownik istnieje w bazie danych
        if (user == null) {
            builder.setMessage(R.string.sign_in_error_no_user_in_database);
            builder.setPositiveButton("OK", null).create().show();
            return;
        }

        // Sprawdzenie poprawności e-maila i hasła
        if (user.getEmail().equals(email)) {
            if (user.getPassword().equals(password)) {
                // Przejście do głównej aktywności po pomyślnym logowaniu
                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            } else {
                builder.setMessage(R.string.sign_in_error_no_password);
                builder.setPositiveButton("OK", null).create().show();
            }
        } else {
            builder.setMessage(R.string.sign_in_error_no_email_in_database);
            builder.setPositiveButton("OK", null).create().show();
        }
    }

    // Metoda wyświetlająca pytanie zabezpieczające
    private void promptSecurityQuestion() {
        // Utworzenie fragmentu z pytaniem zabezpieczającym
        ShowingSecurityQuestionFragment fragment = new ShowingSecurityQuestionFragment();

        // Pobranie i ustawienie pytania zabezpieczającego
        int index = user.getSecurityQNum();
        String securityQuestion = getResources().getStringArray(R.array.security_question)[index];
        Bundle bundle = new Bundle();
        bundle.putString(SECURITY_QUESTION, securityQuestion);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "PROMPT_SECURITY_QUESTION");
    }

    // Metoda do ponownego ładowania danych
    private void reLoad() {
        getLoaderManager().restartLoader(0, null, this);
        user = mDataSource.getUser();
    }

    // Metody cyklu życia aktywności
    @Override
    public void onResume() {
        super.onResume();
        mDataSource.open();
        reLoad();
    }

    @Override
    public void onPause() {
        super.onPause();
        mDataSource.close();
    }

    // Implementacja metod LoaderManager.LoaderCallbacks<Cursor>
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, DataProvider.USER_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        // Metoda wywoływana po zakończeniu ładowania danych
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        // Metoda wywoływana przy resetowaniu loadera
    }

    // Metoda wywoływana po odpowiedzi na pytanie zabezpieczające
    @Override
    public void securityQuestionAnswered(String securityAnswer) {
        // Sprawdzenie poprawności odpowiedzi
        if (securityAnswer.toLowerCase().equals(user.getSecuirtyAnswer().toLowerCase())) {
            Toast.makeText(this, "Your password is: " + user.getPassword(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Wrong security answer!", Toast.LENGTH_SHORT).show();
        }
    }
}
