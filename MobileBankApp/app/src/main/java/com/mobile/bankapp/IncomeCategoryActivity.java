package com.mobile.bankapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.bankapp.adapter.InCategoryAdapter;
import com.mobile.bankapp.database.DataProvider;
import com.mobile.bankapp.database.DataSource;
import com.mobile.bankapp.database.InCategoryTable;

public class IncomeCategoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        EarningPlansAddFragment.DataEnterListener, InCategoryDetailFragment.DataChangedListener {

    // Deklaracja zmiennych klasy
    private InCategoryAdapter adapter;
    private EarningPlansAddFragment addFragment;
    private DataSource mDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ustawienie layoutu dla tej aktywności
        setContentView(R.layout.activity_budget);

        // Inicjalizacja źródła danych i otwarcie połączenia
        mDataSource = new DataSource(this);
        mDataSource.open();

        // Znalezienie ListView i ustawienie adaptera
        ListView in_category_lv = findViewById(R.id.category_choose_listView);
        adapter = new InCategoryAdapter(this, null, 0);
        in_category_lv.setAdapter(adapter);

        // Ustawienie tytułu dla aktywności
        TextView title = findViewById(R.id.category_activity_title);
        title.setText(R.string.earning_plans);

        // Obsługa kliknięć na elementy ListView
        in_category_lv.setOnItemClickListener((adapterView, view, position, id) -> {
            // Przekazanie ID kategorii do fragmentu szczegółów
            Bundle b = new Bundle();
            b.putLong("IN_ID", id);

            InCategoryDetailFragment detailFragment = new InCategoryDetailFragment();
            detailFragment.setArguments(b);
            detailFragment.show(getFragmentManager(), "DETAIL_FRAGMENT");
        });

        // Ustawienie przycisku dodawania nowej kategorii
        FloatingActionButton fab = findViewById(R.id.budget_fab);
        fab.setOnClickListener(view -> {
            // Wyświetlenie fragmentu dodawania kategorii
            addFragment = new EarningPlansAddFragment();
            addFragment.setCancelable(false);
            addFragment.show(getFragmentManager(), "DIALOG_FRAGMENT");
        });

        // Inicjalizacja LoaderManager
        getLoaderManager().initLoader(0, null, this);
    }

    // Tworzenie loadera do asynchronicznego ładowania danych
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, DataProvider.IN_CATEGORY_URI,
                null, null, null, InCategoryTable.COL1);
    }

    // Aktualizacja danych w adapterze po zakończeniu ładowania
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    // Resetowanie loadera
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    // Obsługa zakończenia wprowadzania danych
    @Override
    public void onDataEnterComplete() {
        // Ponowne ładowanie danych i wyświetlenie powiadomienia
        reload();
        Toast.makeText(this, "Category added!", Toast.LENGTH_SHORT).show();
    }

    // Ponowne otwarcie źródła danych po wznowieniu aktywności
    @Override
    public void onResume() {
        super.onResume();
        mDataSource.open();
    }

    // Zamknięcie źródła danych podczas pauzowania aktywności
    @Override
    public void onPause() {
        super.onPause();
        mDataSource.close();
    }

    // Obsługa zmiany danych
    @Override
    public void onDataChanged() {
        reload();
    }

    // Metoda do ponownego ładowania danych
    private void reload() {
        getLoaderManager().restartLoader(0, null, this);
    }
}
