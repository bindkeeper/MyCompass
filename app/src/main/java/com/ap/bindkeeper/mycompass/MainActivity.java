package com.ap.bindkeeper.mycompass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private SensorManager mSensorManager;
    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];
    private TextView bearingTxtOut ;
    private TextView vectorAzimuthOut ;

    private TextView xOut ;
    private TextView yOut ;
    private TextView zOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        bearingTxtOut = (TextView) findViewById(R.id.txtBearingOut);
        vectorAzimuthOut = (TextView) findViewById(R.id.vectorAzimuth);
        xOut = (TextView) findViewById(R.id.outX);
        yOut = (TextView) findViewById(R.id.outY);
        zOut = (TextView) findViewById(R.id.outZ);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Don't receive any more updates from either sensor.
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] orientation = new float[3];
            float[] rMat = new float[16];

            SensorManager.getRotationMatrixFromVector( rMat, event.values );

            int azimuth = (int) ( Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[0] ) + 360 ) % 360;
            vectorAzimuthOut.setText(String.valueOf(azimuth));
        }else {

            float degree = Math.round(event.values[0]);
            bearingTxtOut.setText(String.valueOf(degree));
        }

        updateOrientationAngles();
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        mSensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);

        // "mRotationMatrix" now has up-to-date information.

        mSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);

        // "mOrientationAngles" now has up-to-date information.

        int azimuth;
        int pitch;
        int roll;

        if (mOrientationAngles[0] > 0) {
            azimuth =  ((int) (mOrientationAngles[0] * 180/Math.PI));
        } else {
            azimuth = 360 + ((int)(mOrientationAngles[0] * 180/Math.PI));
        }

        if (mOrientationAngles[0] > 0) {
            pitch =  ((int) (mOrientationAngles[1] * 180/Math.PI));
        } else {
            pitch = 360 + ((int)(mOrientationAngles[1] * 180/Math.PI));
        }

        if (mOrientationAngles[0] > 0) {
            roll =  ((int) (mOrientationAngles[2] * 180/Math.PI));
        } else {
            roll = 360 + ((int)(mOrientationAngles[2] * 180/Math.PI));
        }



        xOut.setText(String.valueOf(azimuth));
        yOut.setText(String.valueOf(pitch));
        zOut.setText(String.valueOf(roll));
    }
}
