package com.mobile.bankapp.database;

public class AccountsTable {
    // Definicja nazwy tabeli kont
    public static final String TABLE_NAME = "ACCOUNTS";

    // Definicje nazw kolumn w tabeli
    public static final String COL1 = "_id"; // Kolumna dla identyfikatora konta
    public static final String COL2 = "name"; // Kolumna dla nazwy konta
    public static final String COL3 = "type"; // Kolumna dla typu konta
    public static final String COL4 = "starting_balance"; // Kolumna dla początkowego salda
    public static final String COL5 = "current_balance"; // Kolumna dla bieżącego salda

    // Tablica zawierająca wszystkie kolumny, używana np. przy zapytaniach do bazy danych
    public static final String[] ALL_COLS = {COL1, COL2, COL3, COL4, COL5};

    // Instrukcja SQL do tworzenia tabeli
    // Używa klauzuli "IF NOT EXISTS" - tworzy tabelę tylko jeśli jeszcze nie istnieje
    public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COL2 + " TEXT NOT NULL, " +
            COL3 + " TEXT, " +
            COL4 + " TEXT, " +
            COL5 + " TEXT);";

    // Instrukcja SQL do usuwania tabeli
    public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
}
