package com.example.pbmobilnezadanie7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class SensorDetailsActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);

        textView = findViewById(R.id.sensor_info);
        textView.setTextColor(Color.BLACK);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        int index = getIntent().getIntExtra("sensor_index", -1);
        if (index == 6 || index == 7) {
            sensor = sensorManager.getSensorList(Sensor.TYPE_ALL).get(index);
            switch (sensor.getType()) {
                case Sensor.TYPE_LIGHT: {
                    textView.setText(getResources().getString(R.string.light_sensor_label));
                    break;
                }
                case Sensor.TYPE_PRESSURE: {
                    textView.setText(getResources().getString(R.string.pressure_sensor_label));
                    break;
                }
            }
        }
        else {
            sensor = null;
            textView.setText(R.string.missing_sensor);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sensor != null)
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        float currentValue = sensorEvent.values[0];
        switch (sensorType) {
            case Sensor.TYPE_LIGHT: {
                textView.setText(getResources().getString(R.string.light_sensor_label, currentValue));
                textView.setBackgroundColor(Color.rgb((255*(int)currentValue/40000), (255*(int)currentValue/40000), (255*(int)currentValue/40000)));
                if(currentValue < 20000) {
                    textView.setTextColor(Color.WHITE);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                break;
            }
            case Sensor.TYPE_PRESSURE: {
                textView.setText(getResources().getString(R.string.pressure_sensor_label, currentValue));
                textView.setBackgroundColor(Color.rgb((255*(int)currentValue/1100), 0, 0));
                textView.setTextColor(Color.WHITE);
                break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        System.out.println("onAccuracyChanged");
    }
}