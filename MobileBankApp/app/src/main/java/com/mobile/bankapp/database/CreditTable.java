package com.mobile.bankapp.database;

public class CreditTable {
    // Definicja nazwy tabeli kredytów
    public static final String TABLE_NAME = "CREDIT";
    // Definicje nazw kolumn w tabeli
    public static final String COL1 = "_id"; // Kolumna dla identyfikatora kredytu
    public static final String COL2 = "name"; // Kolumna dla nazwy kredytu
    public static final String COL3 = "pay_date"; // Kolumna dla daty płatności
    public static final String COL4 = "accounts_id"; // Kolumna dla identyfikatora powiązanego konta
    public static final String COL5 = "debt"; // Kolumna dla kwoty długu
    public static final String COL6 = "cycleStart"; // Kolumna dla początku cyklu płatności
    public static final String COL7 = "cycleEnd"; // Kolumna dla końca cyklu płatności

    // Tablica zawierająca wszystkie kolumny, używana przy zapytaniach do bazy danych
    public static final String[] ALL_COLS = {COL1, COL2, COL3, COL4, COL5, COL6, COL7};

    // Instrukcja SQL do tworzenia tabeli
    // Używa klauzuli "IF NOT EXISTS" - tworzy tabelę tylko jeśli jeszcze nie istnieje
    public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL2 + " TEXT, " +
            COL3 + " INTEGER, " +
            COL4 + " INTEGER, " +
            COL5 + " TEXT, " +
            COL6 + " INTEGER, " +
            COL7 + " INTEGER)";

    // Instrukcja SQL do usuwania tabeli
    public static final String SQL_DELETE = "DELETE TABLE IF EXISTS " + TABLE_NAME;
}
