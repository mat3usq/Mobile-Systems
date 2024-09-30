package com.mobile.bankapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mobile.bankapp.adapter.AccountsAdapter;
import com.mobile.bankapp.database.DataProvider;
import com.mobile.bankapp.database.DataSource;

//wyświetlanie listy kont
public class ShowingAccountsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private DataSource mDataSource;
    private AccountsAdapter accountsAdapter;
    private static final int ADD_OK = 1;
    private static final int DETAIL = 2;
    public static final String ACCOUNT_DETAIL = "detail";

    //inicjalizacja
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showing_accounts);

        mDataSource = new DataSource(this);
        mDataSource.open();

        ListView accountListView = findViewById(R.id.account_listView);
        accountsAdapter = new AccountsAdapter(this, null, 0);

        accountListView.setAdapter(accountsAdapter);

        accountListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
               Intent accountDetailIntent = new Intent(ShowingAccountsActivity.this,
                        SettingAccountsActivity.class);
               Uri uri = Uri.parse(DataProvider.ACCOUNTS_URI+"/"+id);
               accountDetailIntent.putExtra(ACCOUNT_DETAIL, uri);
               startActivityForResult(accountDetailIntent, DETAIL);
            }
        });

        FloatingActionButton addAccount = findViewById(R.id.account_add_button);
        addAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingAccountIntent = new Intent(ShowingAccountsActivity.this, SettingAccountsActivity.class);
                startActivityForResult(settingAccountIntent, ADD_OK);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    //metoda inicjalizująca Loader, aby załadować najnowsze dane kont
    private void reload(){
        getLoaderManager().restartLoader(0, null, this);
    }

    //po zakończeniu aktywności podrzędnej
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == ADD_OK) {
            if(resultCode == RESULT_OK) {
                reload();
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        mDataSource.close();
    }

    @Override
    public void onResume(){
        super.onResume();
        mDataSource.open();
        reload();
    }

    //utworzenie nowego CursorLoader do ładowania danych z bazy danych
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, DataProvider.ACCOUNTS_URI,
                null, null, null, null);
    }

    //wywoływana, gdy ładowanie danych jest zakończone, aktualizuje adapter danych
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        accountsAdapter.swapCursor(cursor);
    }

    //wywoływana, gdy Loader jest resetowany, czyści adapter danych
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        accountsAdapter.swapCursor(null);
    }
}
