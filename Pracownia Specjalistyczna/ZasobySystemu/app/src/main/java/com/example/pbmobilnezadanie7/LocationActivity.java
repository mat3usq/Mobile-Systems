package com.example.pbmobilnezadanie7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class LocationActivity extends AppCompatActivity {
    private Button buttonLocation;

    private Button buttonAddress;
    private TextView textViewLocation;
    private TextView textViewAddress;
    private Location lastLocation;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        buttonLocation = findViewById(R.id.button_location);
        buttonLocation.setOnClickListener(v -> getLocation());

        buttonAddress = findViewById(R.id.button_address);
        buttonAddress.setOnClickListener(v -> executeGeocoding());

        textViewLocation = findViewById(R.id.textViewLocation);
        textViewAddress = findViewById(R.id.textViewAddress);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void getLocation() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if(location != null) {
                    lastLocation = location;
                    textViewLocation.setText(getString(R.string.location_text,
                            location.getLatitude(),
                            location.getLongitude(),
                            location.getTime()));
                } else {
                    textViewLocation.setText(R.string.no_location);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case 1:
                if(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this, R.string.location_permission_denied,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void executeGeocoding() {
        if(lastLocation != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> returnedAddress = executor.submit(() -> locationGeocoding(getApplicationContext(), lastLocation));
            try {
                String result = returnedAddress.get();
                textViewAddress.setText(getString(R.string.address_text,
                        result, System.currentTimeMillis()));
            } catch (ExecutionException | InterruptedException e) {
                Log.e("Geocoder Error", e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private String locationGeocoding(Context context, Location location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addressList = null;
        String resultMessage = "";

        try {
            addressList = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException ioException) {
            resultMessage = context.getString(R.string.service_not_available);
            Log.e("Geocoder Error", resultMessage, ioException);
        }

        if(addressList == null || addressList.isEmpty()) {
            if(resultMessage.isEmpty()) {
                resultMessage = context.getString(R.string.no_addresses_found);
                Log.e("Geocoder Error", resultMessage);
            }
        } else {
            Address address = addressList.get(0);
            List<String> addressParts = new ArrayList<>();

            for(int i=0; i<=address.getMaxAddressLineIndex(); i++) {
                addressParts.add(address.getAddressLine(i));
            }
            resultMessage = TextUtils.join("\n", addressParts);
        }

        return resultMessage;
    }
}