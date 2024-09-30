package com.mobile.bankapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

// Fragment, który służy jako dialog do wyboru pytania zabezpieczającego.
public class ChoosingSecurityQuestionFragment extends DialogFragment {

    // Interfejs callback, który będzie używany do komunikacji z aktywnością, która wywołała ten fragment.
    private ChoosingSecurityQuestionFragment.FromCallBack activity;

    // Widok listy do wyświetlania pytań zabezpieczających.
    private ListView securityQuestionList;

    // Tablica przechowująca pytania zabezpieczające.
    private String[] securityQuestions;

    public ChoosingSecurityQuestionFragment() {
    }

    // Metoda wywoływana, gdy fragment jest dołączany do aktywności.
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = (ChoosingSecurityQuestionFragment.FromCallBack) activity;
    }

    // Metoda tworząca widok dla fragmentu.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating (rozwijanie) layoutu dla tego fragmentu.
        View view =  inflater.inflate(R.layout.fragment_choosing_security_question, container, false);

        // Znajdowanie ListView w layoucie.
        securityQuestionList = view.findViewById(R.id.security_question_listView);

        // Pobieranie pytań zabezpieczających z zasobów i ustawienie adaptera dla ListView.
        securityQuestions = getActivity().getResources().getStringArray(R.array.security_question);
        securityQuestionList.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1,
                securityQuestions));

        // Ustawienie listenera dla kliknięć na elemencie listy.
        securityQuestionList.setOnItemClickListener((adapterView, view1, position, id) -> {
            activity.securityQuestionSelected((int) id, securityQuestions[(int) id]);
            dismiss();
        });

        return view;
    }

    // Interfejs wykorzystywany do komunikacji z aktywnością, która używa tego fragmentu.
    public interface FromCallBack{
        void securityQuestionSelected(int securityQuestionIndex, String securityQuestion);
    }
}
