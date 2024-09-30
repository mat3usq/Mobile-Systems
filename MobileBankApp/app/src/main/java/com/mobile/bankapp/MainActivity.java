package com.mobile.bankapp;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.mobile.bankapp.database.AccountsTable;
import com.mobile.bankapp.database.DataProvider;
import com.mobile.bankapp.database.DataSource;
import com.mobile.bankapp.database.ExCategoryTable;
import com.mobile.bankapp.database.InCategoryTable;
import com.mobile.bankapp.database.TransactionTable;
import com.mobile.bankapp.model.Account;
import com.mobile.bankapp.model.ExCategory;
import com.mobile.bankapp.model.InCategory;
import com.mobile.bankapp.tools.BigDecimalCalculator;
import com.mobile.bankapp.tools.CurrencyFormatter;
import com.mobile.bankapp.tools.CurrencyOperator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {
    private DataSource mDataSource; // Zmienna do zarządzania źródłem danych.
    private static final String CHECK_IF = "IS_FROM_MAIN_FAB"; // Stała do sprawdzania, czy akcja pochodzi z głównego przycisku akcji.
    private static final String FIRST_START = "FIRST_START"; // Stała do sprawdzania, czy to pierwsze uruchomienie aplikacji.
    public static final String CURRENCY_KEY = "CURRENCY"; // Stały klucz do preferencji waluty.

    private static final int REQUEST_CODE = 1; // Kod żądania dla wyników aktywności.
    private static final int IS_FROM_MAIN_FAB = 1001; // Kod wskazujący działanie z głównego przycisku akcji.
    private Cursor cursor; // Kursor do wyników zapytań bazy danych.

    private BigDecimal totalMonthlyIn; // Zmienna przechowująca całkowity dochód miesięczny.
    private BigDecimal totalMonthlyEx; // Zmienna przechowująca całkowity wydatek miesięczny.

    private SharedPreferences prefs; // Do przechowywania preferencji użytkownika.
    String currencyCode; // Przechowuje wybrany kod waluty.

    String currentcurrency; // Przechowuje aktualny kod waluty.

    private final Map<String, TextView> currencyTextViewMap = new HashMap<>(); // Mapuje kody walut do TextView.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Ustawia układ dla aktywności.

        mDataSource = new DataSource(this); // Inicjuje źródło danych.
        mDataSource.open(); // Otwiera źródło danych.

        // Pobiera preferencje lub ustawia wartości domyślne.
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstStart = prefs.getBoolean(FIRST_START, true);
        currencyCode = prefs.getString(CURRENCY_KEY, "Canada");

        // Określa locale na podstawie zapisanego kodu waluty.
        Locale locale;
        switch (currencyCode) {
            // Przypadki dla różnych krajów, domyślnie dla Kanady.
            case "Canada":
                locale = Locale.CANADA;
                break;
            case "US":
                locale = Locale.US;
                break;
            case "China":
                locale = Locale.CHINA;
                break;
            case "France":
                locale = Locale.FRANCE;
                break;
            case "Germany":
                locale = Locale.GERMANY;
                break;
            case "Japan":
                locale = Locale.JAPAN;
                break;
            case "Korea":
                locale = Locale.KOREA;
                break;
            default:
                locale = Locale.CANADA;
        }

        Currency currencyObj = Currency.getInstance(locale); // Pobiera instancję waluty dla locale.
        currentcurrency = currencyObj.getCurrencyCode(); // Pobiera kod waluty.

        // Lista obsługiwanych walut.
        List<String> supportedCurrencies = new ArrayList<>();
        supportedCurrencies.add("USD");
        supportedCurrencies.add("CNY");
        supportedCurrencies.add("EUR");
        supportedCurrencies.add("JPY");
        supportedCurrencies.add("KRW");
        supportedCurrencies.add("CAD");

        // Mapuje kody walut na TextView na podstawie identyfikatorów zasobów.
        for (String currency : supportedCurrencies) {
            int viewId = getResources().getIdentifier(currency, "id", getPackageName());
            TextView textView = findViewById(viewId);
            currencyTextViewMap.put(currency, textView);
        }

        // Wykonuje AsyncTask do pobierania kursów wymiany.
        new FetchExchangeRatesAsyncTask().execute(currentcurrency, supportedCurrencies);

        // Początkowa konfiguracja dla pierwszego uruchomienia.
        if (firstStart) {
            setDefaultCurrencySetting();
            setDefaultExCategory();
            setDefaultInCategory();
        }

        // Konfiguruje pasek narzędzi, przycisk akcji, szufladę i widok nawigacji.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setContentForMain();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(listener);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getLoaderManager().initLoader(0, null, this);
    }

    // AsyncTask do pobierania kursów wymiany w tle.
    @SuppressLint("StaticFieldLeak")
    private class FetchExchangeRatesAsyncTask extends AsyncTask<Object, Void, Map<String, Double>> {
        @Override
        protected Map<String, Double> doInBackground(Object... params) {
            // Pobiera kursy wymiany.
            String baseCurrency = (String) params[0];
            List<String> targetCurrencies = (List<String>) params[1];

            Map<String, Double> exchangeRates = new HashMap<>();

            for (String targetCurrency : targetCurrencies) {
                if (!targetCurrency.equals(baseCurrency)) {

                    Double exchangeRate = CurrencyOperator.getExchangeRateCode(baseCurrency, targetCurrency);
                    exchangeRates.put(targetCurrency, exchangeRate);
                }
            }
            return exchangeRates;
        }

        @Override
        protected void onPostExecute(Map<String, Double> exchangeRates) {
            // Aktualizuje interfejs użytkownika pobranymi kursami wymiany.
            if (exchangeRates != null) {
                updateTextViews(exchangeRates);
            } else {
                Log.e("AsyncTask", "Error fetching exchange rates");
            }
        }
    }

    private void updateTextViews(Map<String, Double> exchangeRates) {
        // Aktualizuje każdy TextView odpowiadającym mu kursem wymiany.
        for (Map.Entry<String, Double> entry : exchangeRates.entrySet()) {
            String targetCurrency = entry.getKey();
            Double exchangeRate = entry.getValue();
            String symbol = "";
            if ("KRW" == targetCurrency) {
                symbol = "₩";
            } else if ("JPY" == targetCurrency) {
                symbol = "¥";
            } else if ("USD" == targetCurrency) {
                symbol = "$";
            } else if ("CAD" == targetCurrency) {
                symbol = "$";
            } else if ("CNY" == targetCurrency) {
                symbol = "¥";
            } else if ("EUR" == targetCurrency) {
                symbol = "€";
            }
            TextView textView = currencyTextViewMap.get(targetCurrency);
            if (textView != null) {
                if (targetCurrency.equals(currentcurrency)) {
                    textView.setText("This is your currency");
                } else {
                    String exchangeRateString = (exchangeRate != null) ? exchangeRate + symbol : "Error";
                    textView.setText(exchangeRateString);
                }
            }
        }
    }

    // Metoda do ustawiania domyślnej waluty.
    private void setDefaultCurrencySetting() {
        // Tworzy budowniczego dla okna dialogowego.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Pobiera tablicę znaków walut z zasobów.
        final CharSequence[] items = getResources().getStringArray(R.array.currency);

        // Ustawia tytuł okna dialogowego i opcje wyboru z tablicy walut.
        builder.setTitle("Choose Currency")
                .setItems(items, (dialogInterface, position) -> {
                    // Kiedy użytkownik wybierze walutę, zapisuje wybraną walutę do preferencji.
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("CURRENCY", items[position].toString()).apply();

                    // Wywołuje kolejne okno dialogowe do ustawienia domyślnego konta po wybraniu waluty.
                    showDefaultAccountSettingDialog();
                })
                // Ustawia przycisk "OK" bez żadnej akcji (zamknie dialog).
                .setPositiveButton("OK", null)
                // Uniemożliwia zamknięcie okna dialogowego przez użytkownika bez wyboru opcji.
                .setCancelable(false)
                // Tworzy i wyświetla okno dialogowe.
                .create().show();
    }

    // Wyświetla okno dialogowe do ustawienia domyślnego konta.
    private void showDefaultAccountSettingDialog() {
        final View view = getLayoutInflater().inflate(R.layout.set_default_account, null);

        // Znajduje EditText w rozwiniętym layout, który posłuży do wprowadzenia domyślnej kwoty salda.
        final EditText defaultAmount = view.findViewById(R.id.default_balance);

        // Tworzy budowniczego AlertDialog, który umożliwi konfigurację i wyświetlenie okna dialogowego.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Default Account Setting"); // Ustawia tytuł okna dialogowego.

        // Ustawia wiadomość dla użytkownika z instrukcjami dotyczącymi salda konta.
        builder.setMessage("Please set your Cash balance. If not provided, it will be set to 0");
        builder.setView(view); // Ustawia widok dla okna dialogowego, przygotowany wcześniej.

        // Konfiguruje przycisk "OK" w oknie dialogowym wraz z jego działaniem.
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
                    // Ustawia domyślną wartość salda na 0 i zaokrągla ją zgodnie z kodem waluty.
                    defaultAmount.setText(String.valueOf(BigDecimalCalculator.roundValue(0, currencyCode)));

                    double amount_double; // Zmienna na kwotę w formacie double.

                    try {
                        // Próbuje sparsować wprowadzoną przez użytkownika wartość do double.
                        amount_double = Double.parseDouble(defaultAmount.getText().toString());
                    } catch (Exception e) {
                        // W przypadku błędu (np. nieprawidłowego formatu liczby) ustawia kwotę na 0.0.
                        amount_double = BigDecimalCalculator.roundValue(0.0, currencyCode);
                    }

                    // Zaokrągla wprowadzoną kwotę zgodnie z kodem waluty.
                    String amount = String.valueOf(BigDecimalCalculator.roundValue(amount_double, currencyCode));

                    // Ustawia domyślne konto z podaną nazwą, typem i zaokrągloną kwotą.
                    setDefaultAccount("Cash", "default", amount);
                    dialogInterface.dismiss(); // Zamyka okno dialogowe.

                    // Zapisuje w preferencjach informację, że pierwsze uruchomienie zostało zakończone.
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(FIRST_START, false);
                    editor.apply();

                }).setCancelable(false) // Nie pozwala użytkownikowi anulować okna dialogowego bez wyboru.
                .create().show(); // Tworzy i wyświetla okno dialogowe.
    }

    private void setBudgetSummaryPieChart(BigDecimal totalMonthlyEx) {
        // Wykonaj zapytanie, aby uzyskać dane dotyczące wydatków dla kategorii
        /*
        Zapytanie = SELECT category_id, SUM(CAST(amount AS NUMBER(10))) FROM Transaction
        WHERE category_id IS NOT NULL GROUP BY category_id ORDER BY category_id
        */

        // Zdefiniuj kolumny dla zapytania
        String[] columns = {TransactionTable.COL2, TransactionTable.COL5, "SUM(CAST(" + TransactionTable.COL3 + " AS REAL))"};
        String[] selectionArgs = {String.valueOf(TransactionTable.TRANS_TYPE1)};

        // Zapytaj bazę danych, aby pobrać dane dotyczące wydatków
        cursor = mDataSource.query(TransactionTable.TABLE_NAME, columns, TransactionTable.COL8 + "=? AND " +
                        TransactionTable.COL2 + "<=date('now', 'start of month', '+1 month', '-1 day') AND " +
                        TransactionTable.COL2 + " >= date('now', 'start of month')",
                selectionArgs, TransactionTable.COL5, null, TransactionTable.COL5);

        // Pobierz kategorie wydatków z źródła danych
        ArrayList<ExCategory> exCategories = mDataSource.getAllExCategories();

        try {
            while (cursor.moveToNext()) {
                // Dopasuj category_id z wyniku zapytania z kategorią w exCategories i ustaw totalMonthlySpent
                for (int i = 0; i < exCategories.size(); i++) {
                    if (cursor.getInt(cursor.getColumnIndex(TransactionTable.COL5)) == exCategories.get(i).get_id()) {
                        exCategories.get(i).setTotalMonthlySpent(cursor.getFloat(cursor.getColumnIndex(columns[2])));
                    }
                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        // Zapytaj dostawcę treści o kategorie wydatków
        cursor = getContentResolver().query(DataProvider.EX_CATEGORY_URI, ExCategoryTable.ALL_COLS,
                null, null, ExCategoryTable.COL1);

        // Inicjalizuj wykres kołowy (PieChart)
        PieChart budgetSummary = findViewById(R.id.budget_piechart);
        budgetSummary.setUsePercentValues(false);
        budgetSummary.getDescription().setEnabled(false);
        budgetSummary.setExtraOffsets(5, 0, 5, 5);
        budgetSummary.setDragDecelerationFrictionCoef(0.95f);
        budgetSummary.setDrawHoleEnabled(false);

        // Przygotuj dane do wykresu kołowego (PieChart)
        ArrayList<PieEntry> yValues = new ArrayList<>();

        // Wypełnij dane wykresu kołowego (PieChart) kategoriami wydatków
        for (int i = 0; i < exCategories.size(); i++) {
            ExCategory exCategory = exCategories.get(i);

            if (exCategory.getTotalMonthlySpent() != 0.0f) {
                yValues.add(new PieEntry(exCategory.getTotalMonthlySpent(), exCategory.getName()));
            }
        }

        // Obsłuż widoczność elementów na podstawie dostępności danych
        TextView noExpense = findViewById(R.id.show_no_expense);
        if (!yValues.isEmpty()) {
            noExpense.setVisibility(GONE);
        } else {
            budgetSummary.setVisibility(GONE);
        }

        // Utwórz zestaw danych dla wykresu kołowego (PieChart) i skonfiguruj jego wygląd
        PieDataSet dataSet = new PieDataSet(yValues, null);
        dataSet.setSliceSpace(1.0f);
        dataSet.setSelectionShift(10f);
        dataSet.setColors(Color.rgb(244, 67, 54), Color.rgb(255, 193, 7), Color.rgb(3, 169, 244),
                Color.rgb(76, 175, 80), Color.rgb(121, 85, 72));

        // Utwórz dane dla wykresu kołowego (PieChart) i ustaw rozmiar i kolor tekstu
        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(getResources().getColor(R.color.lightPrimary));

        // Ustaw dane dla wykresu kołowego (PieChart)
        budgetSummary.setData(data);

        // Oblicz i wyświetl łączny budżet
        TextView budgetTotal = findViewById(R.id.budget_total_amount);
        BigDecimal exBudgetTotal = new BigDecimal(String.valueOf(BigDecimalCalculator.roundValue(0, currencyCode)));
        for (int i = 0; i < exCategories.size(); i++) {
            exBudgetTotal = BigDecimalCalculator.add(exBudgetTotal.toString(), exCategories.get(i).getAmount());
            budgetTotal.setText(CurrencyFormatter.format(this, exBudgetTotal.toString()));
        }

        // Wyświetl wydatki budżetu
        TextView budgetSpent = findViewById(R.id.budget_total_spent);

        if (totalMonthlyEx.doubleValue() > 0) {
            budgetSpent.setText(String.format("-%s", CurrencyFormatter.format(this, totalMonthlyEx.toString())));
        } else {
            budgetSpent.setText(CurrencyFormatter.format(this, totalMonthlyEx.toString()));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Obsługa wyniku aktywności
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Ponowne załadowanie danych po zakończeniu aktywności
            reLoad();
        }
    }

    private void reLoad() {
        // Ponowne uruchomienie loadera danych i aktualizacja zawartości głównego widoku
        getLoaderManager().restartLoader(0, null, this);
        setContentForMain();
    }

    @SuppressLint("SetTextI18n")
    private void setContentForMain() {
        // Ustawianie zawartości głównego widoku
        CardView netEarningMonthlyCardView = findViewById(R.id.netEarnings_monthly_cardView);

        TextView total7daysExpense = findViewById(R.id.expense_total_7days);
        TextView total7daysIncome = findViewById(R.id.income_total_7days);
        TextView totalMonthlyExpense = findViewById(R.id.expense_total_monthly);
        TextView totalMonthlyIncome = findViewById(R.id.income_total_monthly);
        TextView netEarning7days = findViewById(R.id.netEarnings_total_7days);
        TextView netEarningMonthly = findViewById(R.id.netEarnings_total_monthly);

        // Ustawianie kryteriów filtrowania transakcji na ostatnie 7 dni
        String selection1 = TransactionTable.COL8 + "=" + TransactionTable.TRANS_TYPE1 + " AND " +
                TransactionTable.COL2 + "<=" + "date('now') AND " +
                TransactionTable.COL2 + ">=" + "date('now', '-7 days')";
        String selection2 = TransactionTable.COL8 + "=" + TransactionTable.TRANS_TYPE2 + " AND " +
                TransactionTable.COL2 + "<=" + "date('now') AND " +
                TransactionTable.COL2 + ">=" + "date('now', '-7 days')";

        // Pobranie transakcji za ostatnie 7 dni
        cursor = getContentResolver().query(DataProvider.TRANSACTION_URI, TransactionTable.ALL_COLS,
                selection1, null, null);

        BigDecimal total7daysEx = new BigDecimal(String.valueOf(BigDecimalCalculator.roundValue(0.0, currencyCode)));
        try {
            while (cursor.moveToNext()) {
                total7daysEx = BigDecimalCalculator.add(total7daysEx.toString(),
                        cursor.getString(cursor.getColumnIndex(TransactionTable.COL3)));
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        // Pobranie przychodów za ostatnie 7 dni
        cursor = getContentResolver().query(DataProvider.TRANSACTION_URI, TransactionTable.ALL_COLS,
                selection2, null, null);

        BigDecimal total7daysIn = new BigDecimal(String.valueOf(BigDecimalCalculator.roundValue(0, currencyCode)));
        try {
            while (cursor.moveToNext()) {
                total7daysIn = BigDecimalCalculator.add(total7daysIn.toString(),
                        cursor.getString(cursor.getColumnIndex(TransactionTable.COL3)));
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        // Wyświetlenie sumy wydatków i przychodów za ostatnie 7 dni
        total7daysExpense.setText(CurrencyFormatter.format(this, total7daysEx.toString()));
        total7daysIncome.setText(CurrencyFormatter.format(this, total7daysIn.toString()));

        // Obliczenie i wyświetlenie saldo za ostatnie 7 dni
        BigDecimal bigNetEarning7days = BigDecimalCalculator.subtract(total7daysIn.toString(),
                total7daysEx.toString());

        if (bigNetEarning7days.doubleValue() > 0) {
            netEarning7days.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (bigNetEarning7days.doubleValue() < 0) {
            netEarning7days.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        netEarning7days.setText(CurrencyFormatter.format(this, bigNetEarning7days.toString()));

        // Obliczenie i wyświetlenie miesięcznego salda
        BigDecimal bigNetEarningMonthly = getMonthlyNetEarnings(0);

        totalMonthlyExpense.setText(CurrencyFormatter.format(this, totalMonthlyEx.toString()));
        totalMonthlyIncome.setText(CurrencyFormatter.format(this, totalMonthlyIn.toString()));

        if (bigNetEarningMonthly.doubleValue() > 0) {
            netEarningMonthly.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (bigNetEarningMonthly.doubleValue() < 0) {
            netEarningMonthly.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        netEarningMonthly.setText(CurrencyFormatter.format(this, bigNetEarningMonthly.toString()));

        // Ustawienie wykresu aktywów
        setAssetsPieChart();

        // Ustawienie postępu przychodów
        setEarningProgress(totalMonthlyIn);

        // Ustawienie podsumowania budżetu w postaci wykresu kołowego
        setBudgetSummaryPieChart(totalMonthlyEx);

        // Dodanie nasłuchiwacza kliknięć na karcie miesięcznego zarobku
        netEarningMonthlyCardView.setOnClickListener(listener);
    }

    private void setAssetsPieChart() {
        // Inicjalizacja wykresu kołowego z aktywami
        PieChart assetsPieChart = findViewById(R.id.assets_piechart);

        // Ustawienie opcji wykresu
        assetsPieChart.setDrawHoleEnabled(true);

        // Przygotowanie danych dla wykresu
        ArrayList<PieEntry> yValues = new ArrayList<>();

        // Pobranie listy kont z bazy danych
        ArrayList<Account> accounts = mDataSource.getAllAccounts();

        // Obliczenie łącznej wartości aktywów
        BigDecimal assetTotal = new BigDecimal(String.valueOf(BigDecimalCalculator.roundValue(0, currencyCode)));

        for (int i = 0; i < accounts.size(); i++) {
            // Dodawanie aktywów do wykresu tylko jeśli ich saldo jest większe od zera
            if (Float.parseFloat(accounts.get(i).getCurrent_balance()) > 0.0f) {
                assetTotal = BigDecimalCalculator.add(assetTotal.toString(), accounts.get(i).getCurrent_balance());
            }

            if (Float.parseFloat(accounts.get(i).getCurrent_balance()) > 0.0f) {
                yValues.add(new PieEntry(Float.parseFloat(accounts.get(i).getCurrent_balance()),
                        accounts.get(i).getName()));
            }
        }

        // Konfiguracja wyświetlania środka wykresu
        assetsPieChart.setCenterText(String.format("Łącznie\n%s", assetTotal.toString()));
        assetsPieChart.setCenterTextColor(Color.rgb(63, 81, 181));
        assetsPieChart.setCenterTextSize(12);
        assetsPieChart.setExtraOffsets(0, 5, 0, 0);
        assetsPieChart.setUsePercentValues(true);
        assetsPieChart.getDescription().setEnabled(false);
        assetsPieChart.setEntryLabelTextSize(10);
        assetsPieChart.setEntryLabelColor(Color.rgb(62, 39, 35));

        // Konfiguracja zestawu danych dla wykresu
        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(1.0f);
        dataSet.setSelectionShift(10f);

        // Ustalenie kolorów dla sektorów wykresu
        dataSet.setColors(Color.rgb(205, 220, 57), Color.rgb(121, 85, 72), Color.rgb(63, 81, 181), Color.rgb(3, 169, 244),
                Color.rgb(255, 193, 7), Color.rgb(244, 67, 54));

        // Przygotowanie danych do wyświetlenia
        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.rgb(62, 39, 35));

        // Konfiguracja formatowania wartości procentowych
        data.setValueFormatter(new PercentFormatter());

        // Ustawienie danych dla wykresu
        assetsPieChart.setData(data);

        // Konfiguracja promienia wewnętrznego i zewnętrznego okręgu wykresu
        assetsPieChart.setHoleRadius(35);
        assetsPieChart.setTransparentCircleRadius(0);
    }

    private BigDecimal getMonthlyNetEarnings(int offSet) {
        String selection1;
        String selection2;

        /**
         * Zbuduj zdanie selekcyjne, aby znaleźć wydatki i przychody w określonym okresie (1 miesiąc)
         * Na przykład, jeśli offSet wynosi 0, oznacza to, że ta metoda ma zwrócić saldo za bieżący miesiąc.
         */

        if (offSet == 0) {
            // Selekcja dla bieżącego miesiąca
            selection1 = TransactionTable.COL8 + "=" + TransactionTable.TRANS_TYPE1 + " AND " +
                    TransactionTable.COL2 + "<=" + "date('now', 'start of month', '+1 month', '-1 day') AND " +
                    TransactionTable.COL2 + ">=" + "date('now', 'start of month')";
            selection2 = TransactionTable.COL8 + "=" + TransactionTable.TRANS_TYPE2 + " AND " +
                    TransactionTable.COL2 + "<=" + "date('now', 'start of month', '+1 month', '-1 day') AND " +
                    TransactionTable.COL2 + ">=" + "date('now', 'start of month')";
        } else {
            // Selekcja dla okresu z przesunięciem wstecz
            selection1 = TransactionTable.COL8 + "=" + TransactionTable.TRANS_TYPE1 + " AND " +
                    TransactionTable.COL2 + "<=" + "date('now', 'start of month', '-" + (offSet - 1) + " month', '-1 day') AND " +
                    TransactionTable.COL2 + ">=" + "date('now', 'start of month', '-" + offSet + " month')";
            selection2 = TransactionTable.COL8 + "=" + TransactionTable.TRANS_TYPE2 + " AND " +
                    TransactionTable.COL2 + "<=" + "date('now', 'start of month', '-" + (offSet - 1) + " month', '-1 day') AND " +
                    TransactionTable.COL2 + ">=" + "date('now', 'start of month', '-" + offSet + " month')";
        }

        // Pobierz wydatki za okres
        cursor = getContentResolver().query(DataProvider.TRANSACTION_URI, TransactionTable.ALL_COLS,
                selection1, null, null);

        totalMonthlyEx = new BigDecimal(String.valueOf(BigDecimalCalculator.roundValue(0, currencyCode)));
        try {
            while (cursor.moveToNext()) {
                totalMonthlyEx = BigDecimalCalculator.add(totalMonthlyEx.toString(),
                        cursor.getString(cursor.getColumnIndex(TransactionTable.COL3)));
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        // Pobierz przychody za okres
        cursor = getContentResolver().query(DataProvider.TRANSACTION_URI, TransactionTable.ALL_COLS,
                selection2, null, null);

        totalMonthlyIn = new BigDecimal(String.valueOf(BigDecimalCalculator.roundValue(0, currencyCode)));
        try {
            while (cursor.moveToNext()) {
                totalMonthlyIn = BigDecimalCalculator.add(totalMonthlyIn.toString(),
                        cursor.getString(cursor.getColumnIndex(TransactionTable.COL3)));
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        // Oblicz i zwróć saldo netto za okres
        return BigDecimalCalculator.subtract(totalMonthlyIn.toString(),
                totalMonthlyEx.toString());
    }

    @SuppressLint("SetTextI18n")
    private void setEarningProgress(BigDecimal totalMonthlyIn) {
        // Ustawienie widoczności widoku postępu przychodów
        CardView earningProgressView = findViewById(R.id.earning_progress_view);
        earningProgressView.setOnClickListener(listener);

        // Inicjalizacja elementów widoku postępu przychodów
        TextView expectedEarning = findViewById(R.id.earning_expectation);
        TextView earningStatus = findViewById(R.id.earning_status);

        // Pobranie listy kategorii przychodów z bazy danych
        ArrayList<InCategory> inCategories = mDataSource.getAllInCategories();

        // Obliczenie łącznej oczekiwanej kwoty przychodów
        BigDecimal totalExpectedEarning = new BigDecimal(String.valueOf(BigDecimalCalculator.roundValue(0, currencyCode)));
        for (int i = 0; i < inCategories.size(); i++) {
            InCategory inCategory = inCategories.get(i);
            totalExpectedEarning = BigDecimalCalculator.add(totalExpectedEarning.toString(), inCategory.getAmount());
        }

        // Wyświetlenie oczekiwanej kwoty przychodów
        expectedEarning.setText(CurrencyFormatter.format(this, totalExpectedEarning.toString()));

        // Inicjalizacja zmiennej dla procentowego postępu przychodów
        BigDecimal earningPercent = new BigDecimal("0.00");

        // Inicjalizacja elementów do wyświetlenia komunikatów
        TextView show_no_earnings = findViewById(R.id.show_no_earnings);
        ProgressBar earningProgressBar = findViewById(R.id.earning_progress_bar);

        try {
            // Jeśli możliwe do obliczenia, ustaw widoczność paska postępu i statusu
            earningProgressBar.setVisibility(VISIBLE);
            earningStatus.setVisibility(VISIBLE);
            show_no_earnings.setVisibility(GONE);
            earningPercent = totalMonthlyIn.divide(totalExpectedEarning, 3, RoundingMode.HALF_UP).
                    multiply(new BigDecimal("100"));
        } catch (ArithmeticException e) {
            // W przypadku błędu obliczeń, ukryj pasek postępu i status oraz pokaż komunikat
            earningProgressBar.setVisibility(INVISIBLE);
            earningStatus.setVisibility(INVISIBLE);
            show_no_earnings.setVisibility(VISIBLE);
        }

        // Wyświetlenie procentowego postępu przychodów
        earningStatus.setText(String.format("%s%%", earningPercent.setScale(2)));

        // Animacja zmiany wartości paska postępu
        ObjectAnimator anim = ObjectAnimator.ofInt(earningProgressBar, "progress", 0, earningPercent.intValue());

        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(2000);
        anim.start();
    }

    private void setDefaultAccount(String name, String type, String balance) {
        // Tworzenie obiektu ContentValues do przechowywania danych
        ContentValues values = new ContentValues();
        values.put(AccountsTable.COL2, name);  // Dodawanie nazwy konta
        values.put(AccountsTable.COL3, type);  // Dodawanie typu konta
        values.put(AccountsTable.COL4, balance);  // Dodawanie aktualnego salda
        values.put(AccountsTable.COL5, balance);  // Dodawanie początkowego salda
        // Wstawianie danych do tabeli kont za pomocą ContentResolver
        getContentResolver().insert(DataProvider.ACCOUNTS_URI, values);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Zamykanie źródła danych (DataSource) w przypadku pauzy aplikacji
        mDataSource.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Otwieranie źródła danych (DataSource) i ponowne wczytywanie danych
        mDataSource.open();
        reLoad();
    }

    // Ustawia domyślną kategorię wydatków w bazie danych.
    private void setDefaultExCategory() {
        ContentValues values = new ContentValues(); // Tworzy nowy obiekt ContentValues do przechowywania par klucz-wartość.
        values.put(ExCategoryTable.COL2, "Others"); // Dodaje nazwę kategorii jako "Others".
        // Dodaje zaokrągloną wartość 0 do kolumny, korzystając z kodu waluty do zaokrąglenia.
        values.put(ExCategoryTable.COL3, String.valueOf(BigDecimalCalculator.roundValue(0, currencyCode)));
        // Wstawia utworzone wartości do tabeli kategorii wydatków przez ContentProvider.
        getContentResolver().insert(DataProvider.EX_CATEGORY_URI, values);
    }

    // Ustawia domyślną kategorię przychodów w bazie danych.
    private void setDefaultInCategory() {
        ContentValues values = new ContentValues(); // Tworzy nowy obiekt ContentValues.
        values.put(InCategoryTable.COL2, "Others"); // Dodaje nazwę kategorii jako "Others".
        // Dodaje zaokrągloną wartość 0 do kolumny, analogicznie jak w kategorii wydatków.
        values.put(InCategoryTable.COL3, String.valueOf(BigDecimalCalculator.roundValue(0, currencyCode)));
        // Wstawia utworzone wartości do tabeli kategorii przychodów przez ContentProvider.
        getContentResolver().insert(DataProvider.IN_CATEGORY_URI, values);
    }

    @Override
    public void onBackPressed() {
        // Pobranie referencji do głównego widoku szuflady nawigacyjnej (DrawerLayout)
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            // Jeśli szuflada nawigacyjna jest otwarta, zamknij ją
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // W przeciwnym razie, jeśli nie ma otwartej szuflady, wykonaj domyślne zachowanie (np. zamknięcie aplikacji)
            super.onBackPressed();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Obsługa wybranych opcji z szuflady nawigacyjnej
        switch (item.getItemId()) {
            case R.id.nav_main:
                // Przejście do głównego ekranu (MainActivity)
                Intent mainIntent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish(); // Zamknięcie obecnej aktywności po przejściu do nowej
                break;
            case R.id.nav_expense:
                // Przejście do aktywności wyświetlającej wydatki (ShowingExpensesActivity)
                Intent expenseIntent = new Intent(MainActivity.this, ShowingExpensesActivity.class);
                startActivity(expenseIntent);
                break;
            case R.id.nav_income:
                // Przejście do aktywności wyświetlającej przychody (ShowingIncomeActivity)
                Intent incomeIntent = new Intent(MainActivity.this, ShowingIncomeActivity.class);
                startActivity(incomeIntent);
                break;
            case R.id.nav_accounts:
                // Przejście do aktywności wyświetlającej konta (ShowingAccountsActivity)
                Intent accountIntent = new Intent(MainActivity.this, ShowingAccountsActivity.class);
                startActivity(accountIntent);
                break;
            case R.id.nav_credit:
                // Przejście do aktywności wyświetlającej kredyty (ShowingCreditActivity)
                Intent creditIntent = new Intent(MainActivity.this, ShowingCreditActivity.class);
                startActivity(creditIntent);
                break;
            case R.id.nav_budget:
                // Przejście do aktywności zarządzania budżetem (BudgetActivity)
                Intent budgetIntent = new Intent(MainActivity.this, BudgetActivity.class);
                startActivity(budgetIntent);
                break;
            case R.id.nav_income_category:
                // Przejście do aktywności zarządzania kategoriami przychodów (IncomeCategoryActivity)
                Intent incomeCategoryIntent = new Intent(MainActivity.this, IncomeCategoryActivity.class);
                startActivity(incomeCategoryIntent);
                break;
            case R.id.nav_transfer:
                // Przejście do aktywności dokonywania przelewów (TransferActivity)
                Intent transferActivity = new Intent(MainActivity.this, TransferActivity.class);
                startActivity(transferActivity);
                break;
            case R.id.nav_settings:
                // Przejście do aktywności ustawień (PreferencesActivity)
                Intent prefsActivity = new Intent(MainActivity.this, PreferencesActivity.class);
                startActivity(prefsActivity);
                break;
            case R.id.nav_exit:
                // Wyświetlenie potwierdzenia wyjścia z aplikacji
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Czy na pewno chcesz wyjść?");
                builder.setPositiveButton("Tak", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    finish(); // Zamknięcie aplikacji
                });
                builder.setNegativeButton("Nie", (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }

        // Zamknięcie szuflady nawigacyjnej po wyborze opcji
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Tworzenie i zwracanie obiektu CursorLoader do ładowania danych z dostawcy zawartości (ContentProvider)
        return new CursorLoader(this, DataProvider.TRANSACTION_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @SuppressLint("NonConstantResourceId")
    View.OnClickListener listener = view -> {
        // Obsługa kliknięć przycisków i widoków
        switch (view.getId()) {
            case R.id.fab:
                // Obsługa kliknięcia przycisku FAB (Floating Action Button)
                Intent addIntent = new Intent(MainActivity.this, AddingExpenseActivity.class);
                addIntent.putExtra(CHECK_IF, IS_FROM_MAIN_FAB);
                startActivityForResult(addIntent, REQUEST_CODE);
                break;
            case R.id.netEarnings_monthly_cardView:
                // Obsługa kliknięcia karty wykresu miesięcznego salda netto
                MonthlyNetEarningsFragment fragment = new MonthlyNetEarningsFragment();
                Bundle b = new Bundle();
                double[] chart_yValues = new double[6];

                for (int i = 0; i < 6; i++) {
                    chart_yValues[i] = getMonthlyNetEarnings(i).doubleValue();
                }

                b.putDoubleArray("Y_VALUES", chart_yValues);
                fragment.setArguments(b);
                fragment.setCancelable(true);
                fragment.show(getFragmentManager(), "CHART");
                break;
            case R.id.earning_progress_view:
                // Obsługa kliknięcia widoku postępu przychodów
                Intent incomeIntent = new Intent(MainActivity.this, ShowingIncomeActivity.class);
                startActivity(incomeIntent);
                break;
        }
    };
}
