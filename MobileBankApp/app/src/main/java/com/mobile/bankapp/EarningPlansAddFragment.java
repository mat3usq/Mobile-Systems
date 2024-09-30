package com.mobile.bankapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.bankapp.database.DataProvider;
import com.mobile.bankapp.database.InCategoryTable;
import com.mobile.bankapp.tools.BigDecimalCalculator;

public class EarningPlansAddFragment extends DialogFragment {
    // Listener do komunikacji z aktywnością wywołującą fragment
    private DataEnterListener mListener;

    // Pola tekstowe dla nazwy kategorii i kwoty
    EditText category_name;
    EditText category_amount;

    // Przycisk anulowania działania
    Button cancel;

    // Ikony dla akcji usuwania i edycji
    ImageView delete, edit;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        // Przypisanie aktywności do listenera
        mListener = (DataEnterListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflating (rozwijanie) layoutu dla tego fragmentu
        View v = inflater.inflate(R.layout.fragment_category_add, container, false);

        // Ustawienie tytułu dialogu
        TextView title = v.findViewById(R.id.category_dialog_title);

        // Inicjalizacja ikon usuwania i edycji
        delete = v.findViewById(R.id.category_delete);
        edit = v.findViewById(R.id.category_edit);
        delete.setVisibility(View.INVISIBLE);
        edit.setColorFilter(R.color.colorPrimaryDark);

        // Inicjalizacja i ustawienie listenerów dla przycisków
        cancel = v.findViewById(R.id.category_detail_dialog_cancel);
        edit.setOnClickListener(listener);
        cancel.setOnClickListener(listener);

        // Ustawienie tekstu tytułu
        title.setText(R.string.add_category);

        // Inicjalizacja pól tekstowych dla nazwy kategorii i kwoty
        category_name = v.findViewById(R.id.category_dialog_name);
        category_amount = v.findViewById(R.id.category_dialog_amount);

        return v;
    }

    // Listener dla przycisków w dialogu
    View.OnClickListener listener = new View.OnClickListener(){
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v){
            int id = v.getId();
            switch(id){
                case R.id.category_detail_dialog_cancel:
                    // Zamknij dialog przy anulowaniu
                    dismiss();
                    break;
                case R.id.category_edit:
                    // Przetwarzanie i zapisywanie danych po edycji
                    String name = category_name.getText().toString();
                    double amount;

                    // Pobranie ustawień waluty
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String currencyCode = prefs.getString("CURRENCY", "Canada");

                    // Przetwarzanie i zaokrąglanie kwoty
                    if(category_amount.getText().toString().trim().length()==0) {
                        amount = BigDecimalCalculator.roundValue(0.0, currencyCode);
                    }
                    else{
                        try {
                            amount = BigDecimalCalculator.roundValue(Double.parseDouble
                                    (category_amount.getText().toString()), currencyCode);
                        }catch(Exception e){
                            amount = BigDecimalCalculator.roundValue(0.0, currencyCode);
                        }
                    }
                    // Tworzenie ContentValues i zapisywanie danych do bazy
                    ContentValues values = new ContentValues();
                    values.put(InCategoryTable.COL2, name);
                    values.put(InCategoryTable.COL3, String.valueOf(amount));
                    getActivity().getContentResolver().insert(DataProvider.IN_CATEGORY_URI, values);

                    // Informowanie aktywności o zakończeniu wprowadzania danych
                    mListener.onDataEnterComplete();
                    dismiss();
                    break;
            }
        }
    };

    // Interfejs do komunikacji z aktywnością wywołującą fragment
    public interface DataEnterListener{
        void onDataEnterComplete();
    }
}
