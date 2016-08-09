package com.ap.bindkeeper.mycompass;

import android.content.Context;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
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
    private ImageView compassImg;
    private ImageView side;
    private ImageView front;
    int lastVectorAzimuth;

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
        compassImg = (ImageView) findViewById(R.id.compassView);
        side = (ImageView) findViewById(R.id.side);
        front = (ImageView) findViewById(R.id.front);
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
            float[] rMat = new float[9];

            SensorManager.getRotationMatrixFromVector( rMat, event.values );
            SensorManager.getOrientation( rMat, orientation);

            int azimuth = (int) ( Math.toDegrees( orientation[0] ) + 360 ) % 360;
            int incline = (int) ( Math.toDegrees( orientation[1] ) + 360 ) % 360;
            int roll = (int) ( Math.toDegrees( orientation[2] ) + 360 ) % 360;

            // taken from http://stackoverflow.com/questions/15649684/how-should-i-calculate-azimuth-pitch-orientation-when-my-android-device-isnt
            //float dazimuth =(float) Math.toDegrees(Math.atan((double)(rMat[1]-rMat[3])/(rMat[0]+rMat[4]))+ 360) % 360;
            vectorAzimuthOut.setText(String.valueOf(azimuth));

            compassImg.setRotation(360-azimuth);
            side.setRotation(incline);
            front.setRotation(roll);
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
