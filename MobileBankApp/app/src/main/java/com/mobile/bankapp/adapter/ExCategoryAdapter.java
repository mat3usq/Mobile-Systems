package com.mobile.bankapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.mobile.bankapp.tools.BigDecimalCalculator;
import com.mobile.bankapp.R;
import com.mobile.bankapp.database.DataProvider;
import com.mobile.bankapp.database.ExCategoryTable;
import com.mobile.bankapp.database.TransactionTable;
import com.mobile.bankapp.tools.CurrencyFormatter;

import java.math.BigDecimal;

public class ExCategoryAdapter extends CursorAdapter {

    // Kontekst aplikacji
    private final Context mContext;

    public ExCategoryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.mContext = context;
    }

    // Metoda tworząca nowy widok dla każdego elementu listy
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        // Użycie LayoutInflater do utworzenia nowego widoku zdefiniowanego w pliku XML
        return LayoutInflater.from(context).inflate(R.layout.budget_item, viewGroup, false);
    }

    // Metoda bindView wiąże dane z kursora z poszczególnymi elementami widoku
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Wyszukiwanie elementu TextView dla pozostałej kwoty
        TextView remaining = view.findViewById(R.id.sofar);
        remaining.setText(R.string.remaining);

        // Pobieranie danych z kursora
        int id = cursor.getInt(cursor.getColumnIndex(ExCategoryTable.COL1));
        String name = cursor.getString(cursor.getColumnIndex(ExCategoryTable.COL2));
        String amount = cursor.getString(cursor.getColumnIndex(ExCategoryTable.COL3));

        // Inicjalizacja zmiennej dla obecnego salda
        String currentBalance = "";

        // Tworzenie zapytania do bazy danych
        String selection = TransactionTable.COL2+"<= date('now', 'start of month', '+1 month', '-1 day') AND "+
                TransactionTable.COL2 + ">= date('now', 'start of month') AND "
                +TransactionTable.COL5+"=? AND "+TransactionTable.COL8+"=?";
        String[] selectionArgs = {String.valueOf(id), String.valueOf(TransactionTable.TRANS_TYPE1)};
        cursor = context.getContentResolver().query(DataProvider.TRANSACTION_URI, TransactionTable.ALL_COLS,
                selection, selectionArgs, TransactionTable.COL1);

        try {
            // Obliczanie całkowitej wydanej kwoty
            BigDecimal totalSpentAmount = new BigDecimal("0.0");

            while (cursor.moveToNext()) {
                totalSpentAmount = BigDecimalCalculator.add(totalSpentAmount.toString(),
                        cursor.getString(cursor.getColumnIndex(TransactionTable.COL3)));
            }

            // Obliczanie obecnego salda
            currentBalance = BigDecimalCalculator.subtract(amount, totalSpentAmount.toString()).toString();

        } finally {
            // Zamknięcie kursora
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        // Wyszukiwanie elementów TextView w layoucie
        TextView cate_name = view.findViewById(R.id.category_list_name);
        TextView cate_amount = view.findViewById(R.id.category_list_amount);
        TextView cate_balance = view.findViewById(R.id.category_list_balance);

        // Ustawianie tekstu w TextView
        cate_name.setText(name);
        cate_amount.setText(CurrencyFormatter.format(mContext, amount));
        cate_balance.setText(CurrencyFormatter.format(mContext, currentBalance));
    }
}
