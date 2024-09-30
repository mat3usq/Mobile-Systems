package com.mobile.bankapp.tools;

import java.text.DecimalFormat;

public class CurrencyOperator {
    // Metoda do pobierania kursu wymiany między dwoma walutami
    static public double getExchangeRateCode(String currencyCode1, String currencyCode2) {
        // Pobranie kursu wymiany dla obu walut
        double Rate1 = CurrencyApiClient.getExchangeRate(currencyCode1);
        double Rate2 = CurrencyApiClient.getExchangeRate(currencyCode2);

        // Obliczenie i zwrócenie kursu wymiany
        return getExchangeRate(Rate1, Rate2);
    }

    // Metoda do obliczania kursu wymiany na podstawie dwóch kursów
    static public double getExchangeRate(double Rate1, double Rate2) {
        // Obliczenie kursu wymiany
        double doublevalue = Rate1 / Rate2;

        // Zaokrąglenie do dwóch miejsc po przecinku i zwrócenie wyniku
        return roundToTwoDecimalPlaces(doublevalue);
    }

    // Prywatna metoda do zaokrąglania liczby do dwóch miejsc po przecinku
    private static double roundToTwoDecimalPlaces(double value) {
        // Utworzenie formatu dziesiętnego z dwoma miejscami po przecinku
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        // Formatowanie wartości
        String formattedValue = decimalFormat.format(value);

        // Zamiana przecinka na kropkę w celu obsługi formatu liczbowego
        String modifiedString = formattedValue.replace(',', '.');

        // Konwersja łańcucha znaków z powrotem na liczbę i jej zwrócenie
        return Double.parseDouble(modifiedString);
    }
}
