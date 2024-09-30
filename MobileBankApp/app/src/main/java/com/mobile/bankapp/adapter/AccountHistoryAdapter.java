package com.mobile.bankapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.mobile.bankapp.R;
import com.mobile.bankapp.database.TransactionTable;
import com.mobile.bankapp.tools.CurrencyFormatter;
import com.mobile.bankapp.tools.DateFormatConverter;

public class AccountHistoryAdapter extends CursorAdapter {

    // Zmienna kontekstowa używana do dostępu do zasobów aplikacji
    Context mContext;

    public AccountHistoryAdapter(Context context, Cursor cursor, int flag) {
        super(context, cursor, flag);
        mContext = context;
    }

    // Metoda tworząca nowy widok dla elementu listy
    // Wykorzystuje LayoutInflater do stworzenia widoku zdefiniowanego w XML
    @SuppressLint("InflateParams")
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.account_history_item, null);
    }

    // Metoda bindView wiąże dane z kursora z poszczególnymi elementami widoku w elemencie listy
    @SuppressLint("SetTextI18n")
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Wyszukiwanie elementów TextView w layoucie
        TextView date = view.findViewById(R.id.account_history_list_date);
        TextView amount = view.findViewById(R.id.account_history_list_amount);
        TextView desc = view.findViewById(R.id.account_history_list_desc);

        // Pobieranie danych z kursora i konwersja daty na odpowiedni format
        String transDate = DateFormatConverter.convertDateToCustom(cursor.getString(cursor.getColumnIndex(TransactionTable.COL2)));
        String transAmount = cursor.getString(cursor.getColumnIndex(TransactionTable.COL3));
        String transDesc = cursor.getString(cursor.getColumnIndex(TransactionTable.COL4));
        int transType = cursor.getInt(cursor.getColumnIndex(TransactionTable.COL8));

        // Ustawianie tekstu w widokach TextView
        date.setText(transDate);
        desc.setText(transDesc);

        // Warunkowe formatowanie tekstu dla kwoty transakcji
        // Zmiana koloru i dodanie znaku w zależności od typu transakcji
        if (transType == TransactionTable.TRANS_TYPE1 || transType == TransactionTable.TRANS_TYPE3 ||
                transType == TransactionTable.TRANS_TYPE5) {
            amount.setTextColor(Color.RED);
            amount.setText("-" + CurrencyFormatter.format(mContext, transAmount));
        } else {
            amount.setTextColor(context.getResources().getColor(R.color.colorAccent));
            amount.setText("+" + transAmount);
        }
    }
}
