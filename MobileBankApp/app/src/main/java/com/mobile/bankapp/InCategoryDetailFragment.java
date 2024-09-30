package com.mobile.bankapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.bankapp.database.DataProvider;
import com.mobile.bankapp.database.DataSource;
import com.mobile.bankapp.database.InCategoryTable;
import com.mobile.bankapp.database.TransactionTable;
import com.mobile.bankapp.model.InCategory;
import com.mobile.bankapp.tools.BigDecimalCalculator;

import java.util.ArrayList;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class InCategoryDetailFragment extends DialogFragment {
    // Pola do przechowywania elementów UI i danych
    private EditText category_name;
    private EditText category_amount;
    private String filter;
    private DataSource mDataSource;
    private DataChangedListener mListener;
    private String oldName;
    private String oldAmount;
    private Long inCategory_id;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Inicjalizacja źródła danych i listenera zmian danych
        mDataSource = new DataSource(activity);
        mListener = (DataChangedListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Tworzenie widoku fragmentu
        View v = inflater.inflate(R.layout.fragment_category_detail, container, false);

        // Przypisanie elementów UI do zmiennych
        TextView title = v.findViewById(R.id.category_dialog_title);
        ImageView delete = v.findViewById(R.id.category_delete);
        ImageView edit = v.findViewById(R.id.category_edit);
        Button cancel = v.findViewById(R.id.category_detail_dialog_cancel);

        // Ustawienie koloru ikon edycji i usuwania
        edit.setColorFilter(R.color.colorPrimaryDark);
        delete.setColorFilter(R.color.colorPrimaryDark);

        // Ustawienie listenerów na przyciski
        cancel.setOnClickListener(listener);
        delete.setOnClickListener(listener);
        edit.setOnClickListener(listener);

        // Ustawienie tekstu tytułu
        title.setText(R.string.exit_category);

        // Pobranie identyfikatora kategorii z argumentów fragmentu
        inCategory_id = getArguments().getLong("IN_ID");

        // Utworzenie filtra dla zapytania do bazy danych
        filter = InCategoryTable.COL1 + "=" + inCategory_id;

        // Wykonanie zapytania do bazy danych
        Uri uri = Uri.parse(DataProvider.IN_CATEGORY_URI + "/" + inCategory_id);
        Cursor cursor = getActivity().getContentResolver().query(uri, InCategoryTable.ALL_COLS,
                filter, null, null);

        // Przypisanie pobranych danych do zmiennych
        try {
            while (cursor.moveToNext()) {
                oldName = cursor.getString(cursor.getColumnIndex(InCategoryTable.COL2));
                oldAmount = cursor.getString(cursor.getColumnIndex(InCategoryTable.COL3));
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        // Ustawienie wartości w polach tekstowych
        category_name = v.findViewById(R.id.category_dialog_name);
        category_amount = v.findViewById(R.id.category_dialog_amount);

        category_name.setText(oldName);
        category_amount.setText(String.valueOf(oldAmount));

        return v;
    }

    // Listener obsługujący zdarzenia kliknięcia na przyciski
    View.OnClickListener listener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.category_detail_dialog_cancel:
                    dismiss();
                    break;

                case R.id.category_delete:
                    if (inCategory_id == 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("You cannot delete default category").setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
                        return;
                    }

                    promptChooseAnotherCategory();

                    break;

                case R.id.category_edit:
                    // Pobieranie nowej nazwy kategorii z EditText
                    String newName = category_name.getText().toString();

                    // Sprawdzenie, czy nowa nazwa kategorii jest pusta lub zawiera tylko białe znaki
                    if (newName.isEmpty() || newName.trim().length() == 0) {
                        // Budowanie i wyświetlanie okna dialogowego, informującego użytkownika o błędzie
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Category name cannot be empty")
                                .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss())
                                .create().show();
                        return;
                    }

                    double newAmount;

                    // Pobranie preferencji ustawień aplikacji, aby uzyskać kod waluty
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String currencyCode = prefs.getString("CURRENCY", "Canada");

                    // Sprawdzenie, czy pole tekstowe z kwotą jest puste lub zawiera tylko białe znaki
                    if (category_amount.getText().toString().trim().length() == 0) {
                        // Jeśli tak, ustawienie nowej kwoty na 0.0 i zaokrąglenie jej zgodnie z kodem waluty
                        newAmount = BigDecimalCalculator.roundValue(0.0, currencyCode);
                    } else {
                        // W przeciwnym przypadku, próba przekonwertowania tekstu na liczbę typu double
                        try {
                            newAmount = BigDecimalCalculator.roundValue(Double.parseDouble(category_amount.getText().toString()), currencyCode);
                        } catch (Exception e) {
                            // W przypadku błędu (np. złego formatu liczby), ustawienie kwoty na 0.0
                            newAmount = BigDecimalCalculator.roundValue(0.0, currencyCode);
                        }
                    }

                    // Sprawdzenie, czy zarówno nazwa, jak i kwota nie uległy zmianie
                    if (oldName.equals(newName) && Double.parseDouble(oldAmount) == newAmount) {
                        // Jeśli nie, zamknięcie okna dialogowego
                        dismiss();
                    } else {
                        // W przeciwnym razie, aktualizacja szczegółów kategorii z nowymi wartościami
                        updateDetail(newName, newAmount);
                    }
                    // Zakończenie sekcji kodu
                    break;
            }
        }
    };

    @SuppressLint("NonConstantResourceId")
    private void promptChooseAnotherCategory() {
        // Budowanie okna dialogowego za pomocą AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Inflating (ładowanie) layoutu dla okna dialogowego
        View view = getActivity().getLayoutInflater().inflate(R.layout.delete_alert_dialog, null);

        // Ustawianie widoku i tytułu w AlertDialog
        builder.setView(view);
        builder.setTitle("Delete Category");
        builder.setMessage("Please select one");

        // Znajdowanie Spinnera w załadowanym widoku i ustawianie jego widoczności jako niewidoczny
        final Spinner categoryChooseSpinner = view.findViewById(R.id.delete_spinner);
        categoryChooseSpinner.setVisibility(INVISIBLE);

        // Pobieranie listy kategorii przychodów
        ArrayList<InCategory> inCategories = mDataSource.getAllInCategories();

        // Wyświetlenie Toasta z liczbą kategorii przychodów
        Toast.makeText(getContext(), String.valueOf(inCategories.size()), Toast.LENGTH_SHORT).show();

        // Usuwanie aktualnie wybranej kategorii z listy
        for (int i = 0; i < inCategories.size(); i++) {
            if (inCategories.get(i).get_id() == inCategory_id) {
                inCategories.remove(inCategories.get(i));
            }
        }

        // Przygotowanie finalnej listy kategorii przychodów po usunięciu
        final ArrayList<InCategory> finalInCategories = inCategories;

        // Ponowne wyświetlenie Toasta z aktualną liczbą kategorii
        Toast.makeText(getContext(), String.valueOf(inCategories.size()), Toast.LENGTH_SHORT).show();

        // Tworzenie tablicy nazw kategorii dla Spinnera
        final String[] inCategoryNames = new String[inCategories.size()];
        for (int i = 0; i < inCategories.size(); i++) {
            inCategoryNames[i] = inCategories.get(i).getName();
        }

        // Ustawianie adaptera dla Spinnera z nazwami kategorii
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, inCategoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryChooseSpinner.setAdapter(adapter);

        // Znajdowanie RadioGroup i ustawianie listenera na zmianę wybranej opcji
        final RadioGroup radioGroup = view.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            switch (i) {
                case R.id.radio_delete:
                    categoryChooseSpinner.setVisibility(INVISIBLE);
                    break;
                case R.id.radio_move:
                    categoryChooseSpinner.setVisibility(VISIBLE);
                    break;
            }
        });

        // Ustawianie akcji dla przycisków 'Cancel' i 'OK'
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dismiss())
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    // Akcje dla poszczególnych opcji w RadioGroup
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.radio_delete:
                            deleteBelongingTransactions();
                            break;
                        case R.id.radio_move:
                            Toast.makeText(getContext(), String.valueOf(categoryChooseSpinner.getSelectedItemPosition()), Toast.LENGTH_SHORT).show();
                            InCategory inCategory = finalInCategories.get(categoryChooseSpinner.getSelectedItemPosition());
                            Toast.makeText(getContext(), String.valueOf(inCategory.get_id()), Toast.LENGTH_SHORT).show();
                            moveBelongingTransactions(inCategory.get_id());
                            break;
                        default:
                            dismiss();
                    }
                    // Usunięcie kategorii
                    deleteCategory();
                });

        // Wyświetlenie okna dialogowego
        builder.create().show();
    }

    private void deleteBelongingTransactions() {
        // Usuwanie transakcji należących do kategorii
        String selection = TransactionTable.COL5 + "=? AND " + TransactionTable.COL8 + "=" + String.valueOf(TransactionTable.TRANS_TYPE2);
        String[] selectionArgs = {String.valueOf(inCategory_id)};

        getActivity().getContentResolver().delete(DataProvider.TRANSACTION_URI, selection, selectionArgs);
    }

    private void moveBelongingTransactions(int newCategoryId) {
        // Przenoszenie transakcji do nowej kategorii
        String selection = TransactionTable.COL5 + "=? AND " + TransactionTable.COL8 + "=?";
        String[] selectionArgs = {String.valueOf(inCategory_id), String.valueOf(TransactionTable.TRANS_TYPE2)};

        ContentValues values = new ContentValues();
        values.put(TransactionTable.COL5, newCategoryId);

        getActivity().getContentResolver().update(DataProvider.TRANSACTION_URI, values, selection, selectionArgs);
    }

    private void updateDetail(String newName, double newAmount) {
        // Aktualizacja szczegółów kategorii
        ContentValues values = new ContentValues();
        values.put(InCategoryTable.COL2, newName);
        values.put(InCategoryTable.COL3, String.valueOf(newAmount));

        getActivity().getContentResolver().update(DataProvider.IN_CATEGORY_URI, values, filter, null);
        mListener.onDataChanged();
        Toast.makeText(getActivity(), "Category detail changed", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    private void deleteCategory() {
        // Usuwanie kategorii
        getActivity().getContentResolver().delete(DataProvider.IN_CATEGORY_URI, filter, null);
        Toast.makeText(getActivity(), "Category deleted", Toast.LENGTH_SHORT).show();
        mListener.onDataChanged();
        dismiss();
    }

    public interface DataChangedListener {
        // Interfejs słuchacza zmian danych
        void onDataChanged();
    }

}
