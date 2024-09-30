package com.mobile.bankapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobile.bankapp.R;
import com.mobile.bankapp.database.CreditTable;
import com.mobile.bankapp.tools.CurrencyFormatter;

public class CreditAdapter extends CursorAdapter {

    public CreditAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    // Metoda tworząca nowy widok dla każdego elementu listy
    @SuppressLint("InflateParams")
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Użycie LayoutInflater do utworzenia nowego widoku zdefiniowanego w pliku XML
        return LayoutInflater.from(context).inflate(R.layout.credit_item, null);
    }

    // Metoda bindView wiąże dane z kursora z poszczególnymi elementami widoku
    @SuppressLint({"RestrictedApi", "DefaultLocale"})
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Wyszukiwanie elementów TextView w layoucie
        TextView name = view.findViewById(R.id.credit_item_name);
        TextView date = view.findViewById(R.id.credit_due_date);
        TextView amount = view.findViewById(R.id.credit_amount);
        TextView cycle = view.findViewById(R.id.credit_cycle);

        // Pobieranie danych z kursora
        String credit_name = cursor.getString(cursor.getColumnIndex(CreditTable.COL2));
        int due = cursor.getInt(cursor.getColumnIndex(CreditTable.COL3));
        String credit_amount = cursor.getString(cursor.getColumnIndex(CreditTable.COL5));
        int cycleStart = cursor.getInt(cursor.getColumnIndex(CreditTable.COL6));
        int cycleEnd = cursor.getInt(cursor.getColumnIndex(CreditTable.COL7));

        // Ustawianie tekstu w TextView, w tym formatowanie daty i kwoty
        name.setText(credit_name);
        date.setText(String.format("due: %s", due));
        amount.setText(String.format("bal: %s" , CurrencyFormatter.format(mContext, credit_amount)));
        cycle.setText(String.format("cycle: %d to %d", cycleStart, cycleEnd));
    }
}
