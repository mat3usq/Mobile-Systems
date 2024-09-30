package com.mobile.bankapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mobile.bankapp.adapter.DebtsAdapter;
import com.mobile.bankapp.database.AccountsTable;
import com.mobile.bankapp.database.CreditTable;
import com.mobile.bankapp.database.DataProvider;
import com.mobile.bankapp.database.DataSource;
import com.mobile.bankapp.database.TransactionTable;
import com.mobile.bankapp.model.Account;
import com.mobile.bankapp.model.CreditCard;
import com.mobile.bankapp.model.Transaction;
import com.mobile.bankapp.tools.BigDecimalCalculator;

import java.util.ArrayList;

public class PayingDebtsFragment extends DialogFragment implements DebtsAdapter.OnChecked, LoaderManager.LoaderCallbacks<Cursor> {
    // Deklaracja zmiennych klasy
    private DebtsAdapter adapter;
    private CreditCard card;
    private DataSource mDataSource;
    private int cardId;
    private TextView debtsTotal;
    private TextView billsTotal;

    // Lista do przechowywania transakcji
    private final ArrayList<Transaction> transactions = new ArrayList<>();

    public PayingDebtsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating (ładowanie) layoutu dla tego fragmentu
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_paying_debts, null);

        // Inicjalizacja widoków i przycisków
        ListView expense_lv = view.findViewById(R.id.debts_listView);
        Button payBills = view.findViewById(R.id.pay_bills_button);
        debtsTotal = view.findViewById(R.id.debts_total);
        billsTotal = view.findViewById(R.id.bills_total);

        // Pobranie ID karty kredytowej z argumentów
        cardId = getArguments().getInt("CardId");

        // Inicjalizacja źródła danych
        mDataSource = new DataSource(getActivity());
        // Ustawienie ogólnej sumy długów
        setTotalDebts(cardId);

        // Utworzenie i ustawienie adaptera dla listy transakcji
        String[] selectionArgs = {String.valueOf(cardId)};
        Cursor cursor = getActivity().getContentResolver().query(DataProvider.TRANSACTION_URI,
                TransactionTable.ALL_COLS, TransactionTable.COL6 + " IS NULL AND " + TransactionTable.COL7 + "=?",
                selectionArgs, TransactionTable.COL2 + " DESC, " + TransactionTable.COL1 + " DESC");
        adapter = new DebtsAdapter(getActivity(), cursor, 0, this);
        expense_lv.setAdapter(adapter);

        // Ustawienie listenera na przycisku do płatności rachunków
        setListenerOnPayBillsButton(payBills, transactions);

        // Inicjalizacja LoaderManagera
        getLoaderManager().initLoader(0, null, this);

        return view;
    }

    // Metoda do ustawienia łącznej sumy długów
    private void setTotalDebts(int cardId) {
        card = mDataSource.getCreditCard(cardId);
        debtsTotal.setText(String.format("Total: %s", card.getAmount()));
    }

    // Metoda do obliczenia i wyświetlenia łącznej sumy wybranych rachunków
    private void setTotalBills(ArrayList<Transaction> transactions) {
        billsTotal.setText(String.format("Total: %s", getTotalAmount(transactions)));
    }

    // Metoda do ponownego ładowania danych
    private void reLoad() {
        getLoaderManager().restartLoader(0, null, this);
        transactions.clear();
        setTotalDebts(cardId);
        setTotalBills(transactions);
    }

    // Ustawienie listenera na przycisku do płacenia rachunków
    private void setListenerOnPayBillsButton(Button payBills, final ArrayList<Transaction> transactions) {
        final Account account = mDataSource.getAccount(card.getAccount_id());
        payBills.setOnClickListener(view -> {
            final String totalAmount = getTotalAmount(transactions);
            // Obsługa płatności rachunków
            if (!transactions.isEmpty()) {
                // Pokazanie dialogu potwierdzającego płatność
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Paying Credit Bills")
                        .setMessage(String.format("Total amount %s will be paid from %s ", totalAmount, account.getName()))
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            // Obsługa potwierdzenia płatności
                            deductDebts(totalAmount);
                            deductFromAccount(totalAmount);
                            // Aktualizacja transakcji
                            for (Transaction transaction : transactions) {
                                String[] stringArgs = {String.valueOf(transaction.getId())};
                                ContentValues values = new ContentValues();
                                values.put(TransactionTable.COL6, card.getAccount_id());
                                getActivity().getContentResolver().update(DataProvider.TRANSACTION_URI, values, TransactionTable.COL1 + "=?", stringArgs);
                            }
                            // Ponowne ładowanie danych
                            reLoad();
                            dialogInterface.dismiss();
                        })
                        .setNegativeButton("Cancel", null)
                        .create().show();
            } else {
                // Wyświetlenie dialogu informującego o braku wybranych płatności
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Paying bills")
                        .setMessage("No Payment Selected")
                        .setNegativeButton("Cancel", null)
                        .create().show();
            }
        });
    }

    // Metoda do obliczania łącznej sumy wybranych rachunków
    private String getTotalAmount(ArrayList<Transaction> transactions) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String currencyCode = prefs.getString("CURRENCY", "Canada");

        String totalAmount = String.valueOf(BigDecimalCalculator.roundValue(0.0, currencyCode));
        for (int i = 0; i < transactions.size(); i++) {
            totalAmount = BigDecimalCalculator.add(totalAmount, transactions.get(i).getAmount()).toString();
        }
        return totalAmount;
    }

    // Metoda do odjęcia sumy rachunków od salda karty kredytowej
    private void deductDebts(String totalAmount) {
        String newBalance = BigDecimalCalculator.subtract(card.getAmount(), totalAmount).toString();
        ContentValues values = new ContentValues();
        values.put(CreditTable.COL5, newBalance);
        String[] stringArgs = {String.valueOf(card.getId())};
        getActivity().getContentResolver().update(DataProvider.CREDIT_URI, values, CreditTable.COL1 + "=?", stringArgs);
    }

    // Metoda do odjęcia sumy rachunków od salda konta powiązanego z kartą kredytową
    private void deductFromAccount(String totalAmount) {
        Account account = mDataSource.getAccount(card.getAccount_id());
        String newBalance = BigDecimalCalculator.subtract(account.getCurrent_balance(), totalAmount).toString();
        ContentValues values = new ContentValues();
        values.put(AccountsTable.COL5, newBalance);
        String[] stringArgs = {String.valueOf(account.getId())};
        getActivity().getContentResolver().update(DataProvider.ACCOUNTS_URI, values, AccountsTable.COL1 + "=?", stringArgs);
    }

    @Override
    public void onItemChecked(Transaction transaction) {
        // Metoda wywoływana po zaznaczeniu/odznaczeniu rachunku do zapłaty
        if (transaction.isChecked()) {
            transactions.add(transaction);
        } else {
            transactions.remove(transaction);
        }
        setTotalBills(transactions);
    }

    // Implementacja metod LoaderManager.LoaderCallbacks<Cursor>
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] selectionArgs = {String.valueOf(cardId)};
        return new CursorLoader(getContext(), DataProvider.TRANSACTION_URI, null, TransactionTable.COL6 + " IS NULL AND " + TransactionTable.COL7 + "=?", selectionArgs, TransactionTable.COL2 + " DESC, " + TransactionTable.COL1 + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
