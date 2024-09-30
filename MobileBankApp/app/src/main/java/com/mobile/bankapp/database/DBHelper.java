package com.mobile.bankapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Klasa DBHelper, rozszerzająca SQLiteOpenHelper, służy do tworzenia i zarządzania wersjami bazy danych
public class DBHelper extends SQLiteOpenHelper {
    // Stałe definiujące nazwę bazy danych i jej wersję
    private static final String DB_NAME = "Bank_Database_App";
    private static final int DB_VER = 1;

    // Konstruktor klasy przyjmujący kontekst aplikacji
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Metoda onCreate jest wywoływana, gdy baza danych jest tworzona po raz pierwszy
        // Wykonuje instrukcje SQL tworzące każdą z tabel
        db.execSQL(UserTable.SQL_CREATE);
        db.execSQL(AccountsTable.SQL_CREATE);
        db.execSQL(TransactionTable.SQL_CREATE);
        db.execSQL(ExCategoryTable.SQL_CREATE);
        db.execSQL(InCategoryTable.SQL_CREATE);
        db.execSQL(CreditTable.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        // Metoda onUpgrade jest wywoływana, gdy numer wersji bazy danych się zwiększa
        // Umożliwia aktualizację schematu bazy danych poprzez usunięcie starych tabel
        // i ponowne ich utworzenie
        db.execSQL(UserTable.SQL_DELETE);
        db.execSQL(AccountsTable.SQL_DELETE);
        db.execSQL(TransactionTable.SQL_DELETE);
        db.execSQL(ExCategoryTable.SQL_DELETE);
        db.execSQL(InCategoryTable.SQL_DELETE);
        db.execSQL(CreditTable.SQL_DELETE);
        // Ponowne utworzenie tabel
        onCreate(db);
    }
}
