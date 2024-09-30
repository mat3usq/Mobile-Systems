package com.mobile.bankapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.mobile.bankapp.R;
import com.mobile.bankapp.database.AccountsTable;
import com.mobile.bankapp.database.DataProvider;
import com.mobile.bankapp.database.InCategoryTable;
import com.mobile.bankapp.database.TransactionTable;
import com.mobile.bankapp.tools.CurrencyFormatter;
import com.mobile.bankapp.tools.DateFormatConverter;

public class IncomeAdapter extends CursorAdapter {

    // Kontekst aplikacji
    private final Context mContext;

    public IncomeAdapter(Context context, Cursor cursor, int flag) {
        super(context, cursor, flag);
        this.mContext = context;
    }

    // Metoda tworząca nowy widok dla każdego elementu listy
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        // Użycie LayoutInflater do utworzenia nowego widoku zdefiniowanego w pliku XML
        return LayoutInflater.from(context).inflate(R.layout.income_item, null);
    }

    // Metoda bindView wiąże dane z kursora z poszczególnymi elementami widoku
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Wyszukiwanie elementów TextView w layoucie
        TextView date = view.findViewById(R.id.income_list_date);
        TextView amount = view.findViewById(R.id.income_list_amount);
        TextView to = view.findViewById(R.id.income_list_to);
        TextView desc = view.findViewById(R.id.income_list_desc);
        TextView category = view.findViewById(R.id.income_list_category);

        // Pobieranie danych z kursora i konwersja daty
        String inDate = DateFormatConverter.convertDateToCustom(cursor.getString(cursor.getColumnIndex(TransactionTable.COL2)));
        String inAmount = cursor.getString(cursor.getColumnIndex(TransactionTable.COL3));
        String inDesc = cursor.getString(cursor.getColumnIndex(TransactionTable.COL4));
        int inCategory_id = cursor.getInt(cursor.getColumnIndex(TransactionTable.COL5));
        int inTo_id = cursor.getInt(cursor.getColumnIndex(TransactionTable.COL6));

        // Ustawianie tekstu w TextView
        date.setText(inDate);
        amount.setText(CurrencyFormatter.format(mContext, "+" + inAmount));
        desc.setText(inDesc);

        // Pobieranie kategorii przychodu
        cursor = context.getContentResolver().query(DataProvider.IN_CATEGORY_URI, InCategoryTable.ALL_COLS,
                InCategoryTable.COL1 + "=" + inCategory_id, null, null);

        // Blok try-catch służy do bezpiecznego przetwarzania zapytań do bazy danych
        try {
            // Iterowanie przez wyniki zapytania do kursora
            while (cursor.moveToNext()) {
                // Pobieranie nazwy kategorii przychodów z kursora
                String inCategory = cursor.getString(cursor.getColumnIndex(InCategoryTable.COL2));
                // Ustawianie pobranej nazwy kategorii w widoku TextView
                category.setText(inCategory);
            }
        } finally {
            // W bloku finally upewniamy się, że kursor zostanie zamknięty
            // Jest to ważne, aby uniknąć wycieków pamięci i innych problemów z bazą danych
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        // Pobieranie konta docelowego przychodu
        // Ponowne zapytanie do bazy danych, tym razem do tabeli kont
        cursor = context.getContentResolver().query(DataProvider.ACCOUNTS_URI, AccountsTable.ALL_COLS,
                AccountsTable.COL1 + "=" + inTo_id, null, null);

        try {
            // Iterowanie przez wyniki zapytania
            while (cursor.moveToNext()) {
                // Pobieranie nazwy konta docelowego z kursora
                String inTo = cursor.getString(cursor.getColumnIndex(AccountsTable.COL2));
                // Ustawianie nazwy konta w widoku TextView
                to.setText(inTo);
            }
        } finally {
            // Ponowne zamknięcie kursora w bloku finally
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }
}
