package com.mobile.bankapp.tools;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrencyApiClient {

    public static double getExchangeRate(String currencyCode) {
        try {
            // Tworzenie URL z podanym kodem waluty
            String apiUrl = "https://api.nbp.pl/api/exchangerates/rates/a/" + currencyCode + "/?format=json";
            URL url = new URL(apiUrl);

            // Ustawienie połączenia HTTP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Pobranie odpowiedzi z serwera
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            // Zamknięcie połączenia
            reader.close();
            connection.disconnect();

            // Analiza JSON i pobranie kursu
            return parseExchangeRate(response.toString());

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }



    private static double parseExchangeRate(String jsonResponse) {
        try {
            JSONObject json = new JSONObject(jsonResponse);
            return json.getJSONArray("rates").getJSONObject(0).getDouble("mid");
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}