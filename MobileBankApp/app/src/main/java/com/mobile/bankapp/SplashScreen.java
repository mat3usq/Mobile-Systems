package com.mobile.bankapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;


//ekran powitalny aplikacji oraz logowania
public class SplashScreen extends AppCompatActivity{

    private final static long DELAY = 3000;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedStateInstance){
        super.onCreate(savedStateInstance);
        setContentView(R.layout.splash);

        new Handler().postDelayed(new Runnable(){

            @Override
            public void run(){

                //sprawdź preferencje użytkownika co do logowania
                prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                boolean login_enabled = prefs.getBoolean(LoginActivity.ENABLE_LOGIN, true);

                //jeśli nie
                if(login_enabled){
                    Intent loginIntent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                //jeśli takt
                }else{
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, DELAY);

    }
}
