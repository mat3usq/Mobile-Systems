package com.mobile.bankapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.mobile.bankapp.database.AccountsTable;
import com.mobile.bankapp.database.CreditTable;
import com.mobile.bankapp.database.DataProvider;
import com.mobile.bankapp.database.DataSource;
import com.mobile.bankapp.database.TransactionTable;
import com.mobile.bankapp.model.Account;
import com.mobile.bankapp.model.CreditCard;
import com.mobile.bankapp.model.ExCategory;
import com.mobile.bankapp.tools.BigDecimalCalculator;
import com.mobile.bankapp.tools.DateFormatConverter;

import java.math.BigDecimal;
import java.util.Calendar;

public class AddingExpenseActivity extends AppCompatActivity implements ChoosingExCategory.CategorySelected,
ChoosingExFromFragment.FromCallBack{

    private TextView date, expense_category, from;
    private EditText amount, description;
    private DataSource mDataSource;
    private int year, month, todayDate;
    private boolean switchable;
    private static final int FROM_ADDING_ACTIVITY = 1002;

    private boolean paidByCredit;

    private Account account;
    private ExCategory exCategory;
    private CreditCard card;

    private BigDecimal newBalance;
    //Widok pojedynczego wydatku
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_expense);

        mDataSource = new DataSource(this);
        mDataSource.open();

        if(getIntent().getExtras()!=null){
            switchable = true;
        }

        date = findViewById(R.id.expense_date);
        description = findViewById(R.id.expense_desc);
        amount = findViewById(R.id.expense_amount);
        from = findViewById(R.id.expense_from);
        expense_category = findViewById(R.id.expense_category);

        setTodayDate();

        setDefaultValues();

        date.setOnClickListener(listener);
        from.setOnClickListener(listener);
        expense_category.setOnClickListener(listener);

    }
    //Ustawia dzisiejsza date
    private void setTodayDate(){
        Calendar mCurrentDate = Calendar.getInstance();
        year = mCurrentDate.get(Calendar.YEAR);
        month = mCurrentDate.get(Calendar.MONTH)+1;
        todayDate = mCurrentDate.get(Calendar.DAY_OF_MONTH);

        //set the date as the format(ex: Jan dd yyyy) using customized class
        date.setText(DateFormatConverter.convertDateToCustom(year+"-"+month+"-"+todayDate));
    }

    //Ustawia others w kategorii wydatku jako default i cash jako domyslna metode platnosci wydatku
    private void setDefaultValues() {

        exCategory = mDataSource.getExCategory(1);

        expense_category.setText(exCategory.getName());

        account = mDataSource.getAccount(1);

        from.setText(account.getName());

    }
    //Nacisniecie na jakis element wywoluje to i w zaleznosic na co aktywuje co innego
    View.OnClickListener listener = new View.OnClickListener(){
        @Override
        public void onClick(View v){

            switch(v.getId()){
                case R.id.expense_date:
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddingExpenseActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int thisYear, int monthOfYear, int day) {
                                    monthOfYear = monthOfYear+1;
                                    //set date using customized class
                                    date.setText(DateFormatConverter.
                                            convertDateToCustom(thisYear+"-"+monthOfYear+"-"+day));
                                }
                            }, year, month-1, todayDate);
                    datePickerDialog.show();
                    break;
                case R.id.expense_from:
                    ChoosingExFromFragment choosingExFromFragment = new ChoosingExFromFragment();
                    choosingExFromFragment.show(getFragmentManager(), "CHOOSING_FROM");
                    break;
                case R.id.expense_category:
                    showChooseCategoryFragment();
                    break;
            }
        }
    };
    //Wyswietla menu np wyboru kategorri wydatku
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.adding_expense_activity, menu);
        MenuItem switch_button = menu.findItem(R.id.income_switch_button);
        if(!switchable) {
            switch_button.setVisible(false);
            return true;
        }
        return true;
    }
    //na wybor opcji w wybranym menu reakcja
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()){
            case R.id.income_switch_button:
                Intent incomeSwitchIntent = new Intent(this, AddingIncomeActivity.class);
                incomeSwitchIntent.putExtra("FROM_ADDING_ACTIVITY", FROM_ADDING_ACTIVITY);
                startActivity(incomeSwitchIntent);
                finish();
                return true;
            case R.id.expense_save:
                saveExpense();
                return true;
        }

        return false;
    }
    //Otwarcie fragmenu wyboru kategorii
    private void showChooseCategoryFragment(){

        DialogFragment chooseCategoryFragment = new ChoosingExCategory();
        chooseCategoryFragment.show(getFragmentManager(), "CHOOSE_EX_CATEGORY");

    }
    //Reakcja na wybranie categorii w menu
    @Override
    public void onCategorySelected(ExCategory category){

        this.exCategory = category;
        expense_category.setText(category.getName());
    }
    //Reakcja na wybor konta
    @Override
    public void fromAccountSelected(Account account){

        this.paidByCredit = false;

        this.account = account;
        //to prevent null pointer exception
        if(account!=null){
            from.setText(account.getName());
        }else{
            from.setText("");
        }
    }
    //Na wybor karty
    @Override
    public void fromCreditSelected(CreditCard card){

        this.paidByCredit = true;
        this.card = card;
        if(card!=null){
            from.setText(card.getName());
        }else{
            from.setText("");
        }
    }
    //Zapis wydatku
    private void saveExpense(){
        String exDate, exDesc;
        double exAmount;

        exDate = DateFormatConverter.convertDateToISO(date.getText().toString());

        exDesc = description.getText().toString();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String currencyCode = prefs.getString("CURRENCY", "Canada");

        //if the amount is empty
        if(amount.getText().toString().trim().length()==0){
            exAmount = BigDecimalCalculator.roundValue(0.0, currencyCode);
        }else{
            try {
                exAmount = BigDecimalCalculator.roundValue(Double.parseDouble(amount.getText().toString()), currencyCode);
            }catch(NumberFormatException e){
                exAmount = BigDecimalCalculator.roundValue(0.0, currencyCode);
            }
        }

        if(exAmount==0.0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please enter amount").setPositiveButton("OK", null).create().show();
            amount.setFocusable(true);
            return;
        }

        ContentValues values = new ContentValues();

        values.put(TransactionTable.COL2, exDate);
        values.put(TransactionTable.COL3, String.valueOf(exAmount));
        values.put(TransactionTable.COL4, exDesc);
        values.put(TransactionTable.COL5, exCategory.get_id());

        if(!paidByCredit) {
            values.put(TransactionTable.COL6, account.getId());
        }else{
            values.put(TransactionTable.COL7, card.getId());
        }

        values.put(TransactionTable.COL8, TransactionTable.TRANS_TYPE1);

        getContentResolver().insert(DataProvider.TRANSACTION_URI, values);

        //deduct the amount from the account
        if(!paidByCredit) {
            deductFromAccount(account, exAmount);
        }else{
            addDebtToCredit(card, exAmount);
        }
        if(switchable){
            Intent intent = new Intent(this, ShowingExpensesActivity.class);
            startActivity(intent);
        }
        setResult(RESULT_OK);
        finish();
    }
    //Wyciagniecie kasy z konta
    private void deductFromAccount(Account account, double amount){
        String filter = AccountsTable.COL1 + "=" + account.getId();

        ContentValues values = new ContentValues();
        newBalance = BigDecimalCalculator.subtract(account.getCurrent_balance(), String.valueOf(amount));
        values.put(AccountsTable.COL5, newBalance.toString());

        getContentResolver().update(DataProvider.ACCOUNTS_URI, values, filter, null);
    }
    //Dodanie dlugu do karty
    private void addDebtToCredit(CreditCard card, double amount){
        String filter = CreditTable.COL1+"="+card.getId();

        ContentValues values = new ContentValues();
        newBalance = BigDecimalCalculator.add(card.getAmount(), String.valueOf(amount));
        values.put(CreditTable.COL5, newBalance.toString());

        getContentResolver().update(DataProvider.CREDIT_URI, values, filter, null);
    }

    @Override
    public void onResume(){
        super.onResume();
        mDataSource.open();
    }

    @Override
    public void onPause(){
        super.onPause();
        mDataSource.close();
    }
}
