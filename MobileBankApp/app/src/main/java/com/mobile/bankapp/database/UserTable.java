package com.mobile.bankapp.database;

public class UserTable {
    // Nazwa tabeli
    public static final String TABLE_NAME = "USER";

    // Nazwy kolumn tabeli
    public static final String COL1 = "EMAIL"; // Kolumna dla adresu email użytkownika
    public static final String COL2 = "PASSWORD"; // Kolumna dla hasła użytkownika
    public static final String COL3 = "SECURITY_QUESTION_NUM"; // Kolumna dla numeru pytania bezpieczeństwa
    public static final String COL4 = "SECURITY_ANSWER"; // Kolumna dla odpowiedzi na pytanie bezpieczeństwa

    // Tablica zawierająca wszystkie kolumny - użyteczna przy tworzeniu zapytań do bazy danych
    public static final String[] ALL_COLS = {COL1, COL2, COL3, COL4};

    // Instrukcja SQL do tworzenia tabeli
    public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            COL1 + " TEXT, " +
            COL2 + " TEXT, " +
            COL3 + " INT, " +
            COL4 + " TEXT)";

    // Instrukcja SQL do usuwania tabeli
    public static final String SQL_DELETE = "DELETE TABLE IF EXISTS " + TABLE_NAME;
}
