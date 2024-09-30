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
import com.mobile.bankapp.tools.CurrencyFormatter;

public class AccountsAdapter extends CursorAdapter {

    // Kontekst aplikacji, używany do dostępu do zasobów i serwisów systemu
    Context mContext;

    public AccountsAdapter(Context context, Cursor cursor, int flag) {
        super(context, cursor, flag);
        mContext = context;
    }

    // Metoda tworząca nowy widok dla każdego elementu listy
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        // Użycie LayoutInflater do utworzenia nowego widoku zdefiniowanego w pliku XML
        return LayoutInflater.from(context).inflate(R.layout.accounts_item, viewGroup, false);
    }

    // Metoda bindView wiąże dane z kursora z poszczególnymi elementami widoku
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Pobieranie danych z kursora
        String name = cursor.getString(cursor.getColumnIndex(AccountsTable.COL2));
        String type = cursor.getString(cursor.getColumnIndex(AccountsTable.COL3));
        String balance = cursor.getString(cursor.getColumnIndex(AccountsTable.COL5));

        // Wyszukiwanie elementów TextView w layoucie i przypisywanie do nich danych
        TextView accountName = view.findViewById(R.id.account_item_name);
        TextView accountType = view.findViewById(R.id.account_item_type);
        TextView accountBalance = view.findViewById(R.id.account_item_balance);

        // Ustawienie tekstu w TextView, formatowanie salda jako waluty
        accountName.setText(name);
        accountType.setText(type);
        accountBalance.setText(CurrencyFormatter.format(mContext, balance));
    }
}
