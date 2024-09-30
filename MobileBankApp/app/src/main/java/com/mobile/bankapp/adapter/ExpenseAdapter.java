package com.mobile.bankapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.mobile.bankapp.R;
import com.mobile.bankapp.database.DataSource;
import com.mobile.bankapp.database.TransactionTable;
import com.mobile.bankapp.model.Account;
import com.mobile.bankapp.model.CreditCard;
import com.mobile.bankapp.model.ExCategory;
import com.mobile.bankapp.tools.CurrencyFormatter;
import com.mobile.bankapp.tools.DateFormatConverter;

public class ExpenseAdapter extends CursorAdapter {

    // Źródło danych oraz kontekst aplikacji
    private final DataSource mDataSource;
    private final Context mContext;

    public ExpenseAdapter(Context context, Cursor cursor, int flag){
        super(context, cursor, flag);
        this.mDataSource = new DataSource(context);
        this.mContext = context;
    }

    // Metoda tworząca nowy widok dla każdego elementu listy
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        // Użycie LayoutInflater do utworzenia nowego widoku zdefiniowanego w pliku XML
        return LayoutInflater.from(context).inflate(R.layout.expense_item, null);
    }

    // Metoda bindView wiąże dane z kursora z poszczególnymi elementami widoku
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Wyszukiwanie elementów TextView w layoucie
        TextView date = view.findViewById(R.id.expense_list_date);
        TextView amount = view.findViewById(R.id.expense_list_amount);
        TextView from = view.findViewById(R.id.expense_list_from);
        TextView desc = view.findViewById(R.id.expense_list_desc);
        TextView category = view.findViewById(R.id.expense_list_category);

        // Pobieranie danych z kursora i konwersja daty
        String exDate = DateFormatConverter.
                convertDateToCustom(cursor.getString(cursor.getColumnIndex(TransactionTable.COL2)));
        String exAmount = cursor.getString(cursor.getColumnIndex(TransactionTable.COL3));
        String exDesc = cursor.getString(cursor.getColumnIndex(TransactionTable.COL4));

        // Pobieranie ID kategorii wydatku
        int exCategory_id = cursor.getInt(cursor.getColumnIndex(TransactionTable.COL5));
        // Logika określająca, czy wydatek pochodzi z konta czy z karty kredytowej
        int exFrom_id;

        if(cursor.getInt(cursor.getColumnIndex(TransactionTable.COL6)) >= 1){
            exFrom_id = cursor.getInt(cursor.getColumnIndex(TransactionTable.COL6));
            Account account = mDataSource.getAccount(exFrom_id);
            from.setText(account.getName());
        } else {
            exFrom_id = cursor.getInt(cursor.getColumnIndex(TransactionTable.COL7));
            CreditCard card = mDataSource.getCreditCard(exFrom_id);
            from.setText(card.getName());
        }

        // Ustawianie tekstu w TextView
        date.setText(exDate);
        amount.setText(String.format("- %s", CurrencyFormatter.format(mContext, exAmount)));
        desc.setText(exDesc);

        // Pobieranie kategorii wydatku z DataSource
        ExCategory exCategory = mDataSource.getExCategory(exCategory_id);
        category.setText(exCategory.getName());
    }
}
