package com.mobile.bankapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.bankapp.database.DataProvider;
import com.mobile.bankapp.database.DataSource;
import com.mobile.bankapp.database.UserTable;
import com.mobile.bankapp.model.User;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class RegisterActivity extends AppCompatActivity implements ChoosingSecurityQuestionFragment.FromCallBack {
    // Deklaracja zmiennych klasy
    private DataSource mDataSource;
    private User user;
    private boolean email_valid = false;
    private boolean password_valid = false;
    private boolean security_question_selected = false;
    private boolean security_answer_valid = false;

    // Deklaracja pól tekstowych i przycisku w interfejsie użytkownika
    private EditText emailEdt, passwordEdt, securityATv;
    private TextView securityQTv;
    private TextView security_question_notselected;
    private Button create_account;
    private int securityQNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ustawienie layoutu dla aktywności
        setContentView(R.layout.activity_register);

        // Inicjalizacja widoków i przypisanie ich do zmiennych
        emailEdt = findViewById(R.id.register_email);
        passwordEdt = findViewById(R.id.register_password);
        securityQTv = findViewById(R.id.security_question);
        securityATv = findViewById(R.id.security_answer);
        create_account = findViewById(R.id.create_account);
        security_question_notselected = findViewById(R.id.security_question_notselected_warning);

        // Inicjalizacja źródła danych
        mDataSource = new DataSource(this);
        mDataSource.open();

        // Pobranie użytkownika (jeśli istnieje) z bazy danych
        user = mDataSource.getUser();

        // Ustawienie listenerów na odpowiednich elementach interfejsu
        securityQTv.setOnClickListener(listener);
        create_account.setOnClickListener(listener);
    }

    // Listener obsługujący kliknięcia na elementach UI
    @SuppressLint("NonConstantResourceId")
    View.OnClickListener listener = view -> {
        switch (view.getId()) {
            case R.id.security_question:
                // Wyświetlenie fragmentu do wyboru pytania zabezpieczającego
                ChoosingSecurityQuestionFragment fragment = new ChoosingSecurityQuestionFragment();
                fragment.show(getFragmentManager(), "CHOOSE_SECURITY_QUESTION");
                break;
            case R.id.create_account:
                // Rozpoczęcie procesu rejestracji
                register();
                break;
        }
    };

    // Metoda do rejestracji nowego użytkownika
    private void register() {
        // Ponowne pobranie użytkownika z bazy danych
        user = mDataSource.getUser();

        // Utworzenie AlertDialog do wyświetlania błędów
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.register_error);

        // Sprawdzenie, czy użytkownik już istnieje w bazie danych
        if (user != null) {
            builder.setMessage(R.string.sign_up_error_user_already_in_database);
            builder.setPositiveButton("OK", null).create().show();
            return;
        }

        // Pobranie danych wprowadzonych przez użytkownika
        String email = emailEdt.getText().toString();
        String password = passwordEdt.getText().toString();
        String securityAnswer = securityATv.getText().toString();

        // Sprawdzenie poprawności danych
        checkInputValidation(email, password, securityAnswer);

        // Wprowadzenie użytkownika do bazy danych, jeśli dane są poprawne
        if (email_valid && password_valid && security_answer_valid) {
            insertUser(email, password, securityQNumber, securityAnswer);
        }
    }

    // Metoda do sprawdzania poprawności danych wejściowych
    private void checkInputValidation(String email, String password, String securityAnswer) {
        // Odnalezienie i ustawienie widoczności komunikatów o błędach
        TextView invalid_email_warning = findViewById(R.id.invalid_email_warning);
        TextView invalid_password_warning = findViewById(R.id.invalid_password_warning);
        TextView invalid_security_answer = findViewById(R.id.invalid_security_answer_warning);

        // Walidacja adresu e-mail
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_valid = true;
            invalid_email_warning.setVisibility(GONE);
        } else {
            email_valid = false;
            invalid_email_warning.setVisibility(VISIBLE);
        }

        // Walidacja hasła
        if (password.trim().length() >= 8) {
            password_valid = true;
            invalid_password_warning.setVisibility(GONE);
        } else {
            password_valid = false;
            invalid_password_warning.setVisibility(VISIBLE);
        }

        // Walidacja odpowiedzi na pytanie zabezpieczające
        if (!securityAnswer.trim().isEmpty()) {
            security_answer_valid = true;
            invalid_security_answer.setVisibility(GONE);
        } else {
            security_answer_valid = false;
            invalid_security_answer.setVisibility(VISIBLE);
        }

        // Walidacja wyboru pytania zabezpieczającego
        if (security_question_selected) {
            security_question_notselected.setVisibility(GONE);
        } else {
            security_question_notselected.setVisibility(VISIBLE);
        }
    }

    // Metoda do wstawiania nowego użytkownika do bazy danych
    private void insertUser(String email, String password, int securityQNumber, String securityAnswer) {
        ContentValues values = new ContentValues();
        values.put(UserTable.COL1, email);
        values.put(UserTable.COL2, password);
        values.put(UserTable.COL3, securityQNumber);
        values.put(UserTable.COL4, securityAnswer);

        getContentResolver().insert(DataProvider.USER_URI, values);

        Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show();

        // Przejście do ekranu logowania po utworzeniu konta
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Metoda wywoływana po wybraniu pytania zabezpieczającego
    @Override
    public void securityQuestionSelected(int index, String question) {
        securityATv.setVisibility(VISIBLE);

        securityQNumber = index;
        security_question_selected = true;

        security_question_notselected.setVisibility(GONE);
        securityQTv.setText(question);
    }
}
