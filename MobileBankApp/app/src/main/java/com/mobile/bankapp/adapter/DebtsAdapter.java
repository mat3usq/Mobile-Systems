package com.mobile.bankapp.adapter;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.mobile.bankapp.R;
import com.mobile.bankapp.database.DataSource;
import com.mobile.bankapp.database.TransactionTable;
import com.mobile.bankapp.model.ExCategory;
import com.mobile.bankapp.model.Transaction;
import com.mobile.bankapp.tools.CurrencyFormatter;
import com.mobile.bankapp.tools.DateFormatConverter;

public class DebtsAdapter extends CursorAdapter {

    private final DataSource mDataSource; // Źródło danych transakcji
    private final OnChecked fragment; // Fragment obsługujący zaznaczenie zadłużenia
    private final Context mContext; // Kontekst aplikacji

    public DebtsAdapter(Context context, Cursor cursor, int flag, Fragment fragment) {
        super(context, cursor, flag);
        this.mContext = context;
        this.fragment = (OnChecked) fragment;
        mDataSource = new DataSource(context);
    }

    // Metoda tworząca nowy widok dla każdego elementu listy
    @SuppressLint("InflateParams")
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        // Użycie LayoutInflater do utworzenia nowego widoku zdefiniowanego w pliku XML
        return LayoutInflater.from(context).inflate(R.layout.debts_item, null);
    }

    // Metoda bindView wiąże dane z kursora z poszczególnymi elementami widoku
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Inicjalizacja zmiennej transaction
        Transaction transaction;

        // Wyszukiwanie elementów TextView w layoucie
        TextView date = view.findViewById(R.id.debts_list_date);
        TextView amount = view.findViewById(R.id.debts_list_amount);
        TextView desc = view.findViewById(R.id.debts_list_desc);
        TextView category = view.findViewById(R.id.debts_list_category);

        // Wyszukiwanie CheckBox w layoucie
        CheckBox checkBox = view.findViewById(R.id.checkBox);

        // Pobieranie danych z kursora i konwersja daty
        String exDate = DateFormatConverter.convertDateToCustom(cursor.getString(cursor.getColumnIndex(TransactionTable.COL2)));
        String exAmount = cursor.getString(cursor.getColumnIndex(TransactionTable.COL3));
        String exDesc = cursor.getString(cursor.getColumnIndex(TransactionTable.COL4));
        int exCategory_id = cursor.getInt(cursor.getColumnIndex(TransactionTable.COL5));

        // Pobieranie transakcji z DataSource
        transaction = mDataSource.getTransaction(cursor.getInt(cursor.getColumnIndex(TransactionTable.COL1)));

        // Ustawianie tekstu w TextView
        date.setText(exDate);
        amount.setText(CurrencyFormatter.format(mContext, exAmount));
        desc.setText(exDesc);

        // Pobieranie kategorii wydatku z DataSource
        ExCategory exCategory = mDataSource.getExCategory(exCategory_id);
        category.setText(exCategory.getName());

        // Ustawienie słuchacza zdarzeń dla CheckBox
        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            // Zmiana stanu zaznaczenia w obiekcie transaction
            transaction.setChecked(b);

            // Wywołanie metody z interfejsu fragmentu
            fragment.onItemChecked(transaction);
        });
    }

    // Interfejs OnChecked z metodą obsługującą zaznaczenie elementu
    public interface OnChecked {
        void onItemChecked(Transaction transaction);
    }
}
