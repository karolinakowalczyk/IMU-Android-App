package com.example.lab6;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {
    private Button toastBtn;
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor magnetometer;
    private TextView mTextSensorAccelerometer;
    private TextView mTextSensorGyroscope;
    private TextView mTextSensorMagnetometer;
    private TextView mShake;
    private float currentValueX;
    private float currentValueY;
    private float currentValueZ;
    private float lastValueX;
    private float lastValueY;
    private float lastValueZ;
    private float deltaX;
    private float deltaY;
    private float deltaZ;
    private Boolean ifIsNotFirstTime = false;
    private float shakeThreshold = 7f;
    private double sumVec;
    private Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        mTextSensorAccelerometer = (TextView) findViewById(R.id.accelerator_label);
        mTextSensorGyroscope = (TextView) findViewById(R.id.gyroscope_label);
        mTextSensorMagnetometer = (TextView)  findViewById(R.id.magnetometer_label);

        mShake = (TextView)  findViewById(R.id.shake_label);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        String sensor_error = getResources().getString(R.string.error_no_sensor);
        if (accelerometer == null) {
            mTextSensorAccelerometer.setText(sensor_error);
        }

        if (accelerometer == null) {
            mTextSensorGyroscope.setText(sensor_error);
        }

        if (accelerometer == null) {
            mTextSensorMagnetometer.setText(sensor_error);
        }

        toastBtn = (Button) findViewById(R.id.toast_button);
        toastBtn.setOnClickListener((View.OnClickListener) this);
    }

    @Override
    public void onClick(View v) {
        Context context = getApplicationContext();
        CharSequence text = "Toast!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();

        currentValueX = event.values[0];
        currentValueY = event.values[1];
        currentValueZ = event.values[2];

        switch(sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                mTextSensorAccelerometer.setText("Accelerator: X: " + currentValueX + "m/s^2 Y: " + currentValueY + "m/s^2 Z: " + currentValueZ + "m/s^2");
                sumVec = sqrt(pow((double) currentValueX, 2) + pow((double) currentValueY, 2) + pow((double) currentValueZ, 2));
                if (sumVec < 2.5) {
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                } else {
                    getWindow().getDecorView().setBackgroundColor(Color.WHITE);
                }
                if (ifIsNotFirstTime) {
                    deltaX = Math.abs(lastValueX - currentValueX);
                    deltaY = Math.abs(lastValueY - currentValueY);
                    deltaZ = Math.abs(lastValueZ - currentValueZ);

                    if ((deltaX > shakeThreshold && deltaY > shakeThreshold) || (deltaZ > shakeThreshold && deltaX > shakeThreshold) || (deltaY > shakeThreshold && deltaZ > shakeThreshold)) {
                        //vibrator.vibrate(500);
                        mShake.setText("SHAKE DETECTED!");
                    }
                    else{
                        mShake.setText("");
                    }

                }

                lastValueX = currentValueX;
                lastValueY = currentValueY;
                lastValueZ = currentValueZ;

                ifIsNotFirstTime = true;
                break;

            case Sensor.TYPE_GYROSCOPE:
                mTextSensorGyroscope.setText("Gyroscope: X: " + currentValueX + "rad/s Y: " + currentValueY + "rad/s Z: " + currentValueZ + "rad/s");

                if (currentValueZ > 1.0) {
                    Toast toast = Toast.makeText(this, "Left", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (currentValueZ < -1.0) {
                    Toast toast = Toast.makeText(this, "Right", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mTextSensorMagnetometer.setText("Magnetometer: X: " + currentValueX + "μT Y: " + currentValueY + "μT Z: " + currentValueZ + "μT");
                break;
            default:
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (accelerometer != null) {
            mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (gyroscope != null) {
            mSensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (magnetometer != null) {
            mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }
}