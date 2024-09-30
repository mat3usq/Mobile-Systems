package com.mobile.bankapp.database;

public class TransactionTable {
    // Nazwa tabeli
    public static final String TABLE_NAME = "TRANSACTIONS";

    // Nazwy kolumn tabeli
    public static final String COL1 = "_id"; // Kolumna identyfikatora, klucz główny
    public static final String COL2 = "date"; // Kolumna przechowująca datę transakcji
    public static final String COL3 = "amount"; // Kolumna przechowująca kwotę transakcji
    public static final String COL4 = "description"; // Kolumna przechowująca opis transakcji
    public static final String COL5 = "category_id"; // Kolumna przechowująca identyfikator kategorii wydatku/przychodu
    public static final String COL6 = "account_id"; // Kolumna przechowująca identyfikator konta
    public static final String COL7 = "credit_id"; // Kolumna przechowująca identyfikator karty kredytowej
    public static final String COL8 = "trans_type"; // Kolumna przechowująca typ transakcji

    // Stałe reprezentujące różne typy transakcji
    public static final int TRANS_TYPE1 = 1; // Wydatek
    public static final int TRANS_TYPE2 = 2; // Przychód
    public static final int TRANS_TYPE3 = 3; // Przelew wychodzący
    public static final int TRANS_TYPE4 = 4; // Przelew przychodzący
    public static final int TRANS_TYPE5 = 5; // Korekta wychodząca
    public static final int TRANS_TYPE6 = 6; // Korekta przychodząca

    // Tablica zawierająca wszystkie kolumny
    public static final String[] ALL_COLS = {COL1, COL2, COL3, COL4, COL5, COL6, COL7, COL8};

    // Instrukcja SQL do tworzenia tabeli
    public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COL2 + " TEXT NOT NULL, " +
            COL3 + " TEXT NOT NULL, " +
            COL4 + " TEXT, " +
            COL5 + " INTEGER, " +
            COL6 + " INTEGER, " +
            COL7 + " INTEGER, " +
            COL8 + " INTEGER);";

    // Instrukcja SQL do usuwania tabeli
    public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
}
