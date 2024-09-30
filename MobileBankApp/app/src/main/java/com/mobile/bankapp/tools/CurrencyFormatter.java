package com.mobile.bankapp.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mobile.bankapp.MainActivity;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter {
    // Metoda formatująca wartość liczbową na format walutowy
    public static String format(Context context, String number) {
        // Pobranie preferencji ustawień zapisanych w SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Pobranie kodu waluty z preferencji (domyślnie "CANADA")
        String currencyCode = prefs.getString(MainActivity.CURRENCY_KEY, "CANADA");

        // Utworzenie formatu liczbowego zgodnego z lokalizacją, której kod waluty jest użyty
        NumberFormat format = NumberFormat.getInstance(new Locale(currencyCode));

        // Konwersja przekazanej liczby typu String na typ double
        double doubleNumber = Double.parseDouble(number);

        // Formatowanie liczby jako wartości walutowej i zwrócenie jej jako łańcucha znaków
        return format.format(doubleNumber);
    }
}
