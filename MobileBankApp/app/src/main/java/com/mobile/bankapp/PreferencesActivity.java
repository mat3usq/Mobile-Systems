package com.mobile.bankapp;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

// Ta klasa jest odpowiedzialna za wyświetlanie aktywności ustawień w aplikacji.
public class PreferencesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ustawienie layoutu dla tej aktywności.
        setContentView(R.layout.activity_preferences);

        // Rozpoczęcie transakcji fragmentu i dodanie instancji SettingsFragment
        getFragmentManager().beginTransaction().add(R.id.prefs_content, new SettingsFragment()).commit();
    }

    // Klasa ta jest odpowiedzialna za wyświetlanie i zarządzanie preferencjami użytkownika.
    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Ładowanie ustawień z zasobu XML. Plik XML określa strukturę preferencji.
            addPreferencesFromResource(R.xml.settings);
        }
    }
}
