package com.mobile.bankapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectDateFragment extends DialogFragment {
    // Interfejs callback do komunikacji z aktywnością, która używa tego fragmentu.
    private FromCallBack activity;
    private ListView dateList;
    private ArrayAdapter<String> adapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Przypisanie aktywności do zmiennej interfejsu callback.
        this.activity = (FromCallBack) activity;
    }

    public SelectDateFragment() {
    }

    // Metoda onCreateView jest wywoływana do stworzenia widoku fragmentu.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating (ładowanie) layoutu fragmentu.
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.select_payment_date_fragment, null);

        // Inicjalizacja ListView i ArrayAdaptera.
        dateList = view.findViewById(R.id.date_listView);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.dates));

        // Pobranie argumentu przekazanego do fragmentu i wybór odpowiedniej akcji.
        int value = getArguments().getInt(AddingCreditActivity.SELECT_DATE);

        switch (value) {
            case 1:
                // Ustawienie adaptera i listenera dla ListView dla daty płatności.
                sendDueDate();
                break;
            case 2:
                // Ustawienie adaptera i listenera dla ListView dla daty rozpoczęcia cyklu.
                sendStartDate();
                break;
            case 3:
                // Ustawienie adaptera i listenera dla ListView dla daty zakończenia cyklu.
                sendEndDate();
                break;
        }

        return view;
    }

    // Metoda do obsługi wyboru daty płatności.
    private void sendDueDate() {
        dateList.setAdapter(adapter);
        dateList.setOnItemClickListener((adapterView, view, i, id) -> {
            // Wywołanie metody callback z wybraną datą i zamknięcie fragmentu.
            activity.payDaySelected((int) id + 1);
            dismiss();
        });
    }

    // Metoda do obsługi wyboru daty rozpoczęcia cyklu.
    private void sendStartDate() {
        dateList.setAdapter(adapter);
        dateList.setOnItemClickListener((adapterView, view, i, id) -> {
            // Wywołanie metody callback z wybranym początkiem cyklu i zamknięcie fragmentu.
            activity.cycleStartSelected((int) id + 1);
            dismiss();
        });
    }

    // Metoda do obsługi wyboru daty zakończenia cyklu.
    private void sendEndDate() {
        dateList.setAdapter(adapter);
        dateList.setOnItemClickListener((adapterView, view, i, id) -> {
            // Wywołanie metody callback z wybranym końcem cyklu i zamknięcie fragmentu.
            activity.cycleEndSelected((int) id + 1);
            dismiss();
        });
    }

    // Interfejs FromCallBack służący do komunikacji z aktywnością hostującą fragment.
    public interface FromCallBack {
        void payDaySelected(int date);

        void cycleStartSelected(int start);

        void cycleEndSelected(int end);
    }
}
