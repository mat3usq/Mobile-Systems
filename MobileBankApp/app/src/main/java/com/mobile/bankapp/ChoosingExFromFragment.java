package com.mobile.bankapp;


import android.app.Activity;
import android.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mobile.bankapp.adapter.AccountsAdapter;
import com.mobile.bankapp.adapter.CreditAdapter;
import com.mobile.bankapp.database.AccountsTable;
import com.mobile.bankapp.database.CreditTable;
import com.mobile.bankapp.database.DataProvider;
import com.mobile.bankapp.database.DataSource;
import com.mobile.bankapp.model.Account;
import com.mobile.bankapp.model.CreditCard;

import java.util.ArrayList;

import static android.view.View.GONE;

public class ChoosingExFromFragment extends DialogFragment {

    private ListView acc_list, credit_list;
    private FromCallBack activity;
    private Cursor cursor;
    private DataSource mDataSource;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = (FromCallBack) activity;
        mDataSource = new DataSource(getActivity());
    }

    public ChoosingExFromFragment() {}
    //Na wybor konta w expense
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_choosing_accounts, container, false);
        acc_list = view.findViewById(R.id.account_listView);

        credit_list = view.findViewById(R.id.credit_listView);

        Bundle b = getArguments();

        if(b!=null){
            LinearLayout creditLine = view.findViewById(R.id.credit_line);
            creditLine.setVisibility(GONE);
        }else{
            setCreditAdapter();
        }

        setAccountAdapter();

        return view;
    }
    //Zamenia obiekt z bazy danych na
    private void setAccountAdapter(){
        cursor = getActivity().getContentResolver().query(DataProvider.ACCOUNTS_URI,
                null, null, null, AccountsTable.COL2);
        AccountsAdapter adapter = new AccountsAdapter(getActivity(), cursor, 0);
        acc_list.setAdapter(adapter);
        acc_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                ArrayList<Account> accounts = mDataSource.getAllAccounts();

                Account account=null;
                for(int i =0; i<accounts.size(); i++){
                    if(id == accounts.get(i).getId()){
                        account = accounts.get(i);
                    }
                }

                activity.fromAccountSelected(account);

                dismiss();
            }
        });
    }
    //Zamienia karty z bazy na obiekt
    public void setCreditAdapter(){
        cursor = getActivity().getContentResolver().query(DataProvider.CREDIT_URI, CreditTable.ALL_COLS,
                null, null, CreditTable.COL1);

        CreditAdapter adapter = new CreditAdapter(getContext(), cursor, 0);
        credit_list.setAdapter(adapter);

        credit_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ArrayList<CreditCard> cards = mDataSource.getAllCreditCards();

                CreditCard card=null;

                for(int i=0; i<cards.size(); i++){
                    if(cards.get(i).getId()==id){
                        card = cards.get(i);
                    }
                }

                activity.fromCreditSelected(card);

                dismiss();
            }
        });
    }

    public interface FromCallBack{
        void fromAccountSelected(Account account);
        void fromCreditSelected(CreditCard card);
    }
}
