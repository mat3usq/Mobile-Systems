package com.mobile.bankapp.tools;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class DateFormatConverter {
    // Tablica zawierająca skróty miesięcy w języku angielskim
    public static final String[] MONTH_STRINGS =
            {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    // Metoda konwertująca datę na format ISO (yyyy-MM-dd)
    public static String convertDateToISO(String dateString) {
        // Rozdzielenie wejściowego ciągu znaków na składowe elementy daty
        String[] dateStrings = dateString.split(" ");
        int year = Integer.parseInt(dateStrings[2]);
        String monthString = dateStrings[0];
        int day = Integer.parseInt(dateStrings[1]);
        int month = 0;

        // Zamiana nazwy miesiąca na odpowiedni numer miesiąca
        for (int i = 0; i < MONTH_STRINGS.length; i++) {
            if (monthString.equals(MONTH_STRINGS[i])) {
                month = i;
                break;
            }
        }

        // Użycie odpowiedniej metody formatowania w zależności od wersji Androida
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            // Formatowanie daty przy użyciu klasy LocalDate (dostępnej od Android Oreo)
            return DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.of(year, month + 1, day));
        } else {
            // Starsza metoda formatowania dla wersji przed Android Oreo
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.format(calendar.getTime());
        }
    }

    // Metoda konwertująca datę z formatu ISO na format 'Miesiąc dzień rok'
    public static String convertDateToCustom(String dateString) {
        // Rozdzielenie wejściowego ciągu znaków na składowe elementy daty
        String[] dateStrings = dateString.split("-");
        int year = Integer.parseInt(dateStrings[0]);
        int month = Integer.parseInt(dateStrings[1]);
        int day = Integer.parseInt(dateStrings[2]);
        String monthString = "";

        // Zamiana numeru miesiąca na nazwę miesiąca
        monthString = MONTH_STRINGS[month - 1];

        // Zwrócenie sformatowanej daty
        return monthString + " " + day + " " + year;
    }
}
