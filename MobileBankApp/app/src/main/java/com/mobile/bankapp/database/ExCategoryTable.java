package com.mobile.bankapp.database;

public class ExCategoryTable {
    // Nazwa tabeli
    public static final String TABLE_NAME = "EX_CATEGORY";

    // Nazwy kolumn tabeli
    public static final String COL1 = "_id"; // Kolumna identyfikatora, klucz główny
    public static final String COL2 = "name"; // Kolumna przechowująca nazwę kategorii wydatków
    public static final String COL3 = "amount"; // Kolumna przechowująca kwotę przypisaną do kategorii

    // Tablica zawierająca wszystkie kolumny - użyteczna przy tworzeniu zapytań do bazy danych
    public static final String[] ALL_COLS = {COL1, COL2, COL3};

    // Instrukcja SQL do tworzenia tabeli
    public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COL2 + " TEXT NOT NULL, " + COL3 + " TEXT NOT NULL);";

    // Instrukcja SQL do usuwania tabeli
    public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
}
