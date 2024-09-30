package com.mobile.bankapp.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;

public class BigDecimalCalculator {
    // Metoda do zaokrąglania wartości z uwzględnieniem specyficznego kraju/regionu
    public static double roundValue(double value, String countryCode) {
        Locale locale;

        // Wybór lokalizacji na podstawie podanego kodu kraju
        switch (countryCode) {
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
                locale = Locale.CANADA; // Domyslna lokalizacja
        }

        // Pobranie waluty dla danej lokalizacji
        Currency currency = Currency.getInstance(locale);

        // Zaokrąglanie wartości do liczby miejsc po przecinku zdefiniowanej dla danej waluty
        return Double.parseDouble(BigDecimal.valueOf(value).setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP).toString());
    }

    // Metoda do odejmowania dwóch wartości reprezentowanych jako łańcuchy znaków
    public static BigDecimal subtract(String value1, String value2) {
        return new BigDecimal(value1).subtract(new BigDecimal(value2));
    }

    // Metoda do dodawania dwóch wartości reprezentowanych jako łańcuchy znaków
    public static BigDecimal add(String value1, String value2) {
        return new BigDecimal(value1).add(new BigDecimal(value2));
    }
}
