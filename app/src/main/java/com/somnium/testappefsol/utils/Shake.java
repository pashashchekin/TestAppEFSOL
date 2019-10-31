package com.somnium.testappefsol.utils;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Shake implements SensorEventListener {

    private SensorManager mySensorManager;

    private float xAccel;
    private float yAccel;
    private float zAccel;

    private float xPreviousAccel;
    private float yPreviousAccel;
    private float zPreviousAccel;

    private boolean firstUpdate = true;

    private final float shakeThreshold = 1.0f;

    private boolean shakeInitiated = false;

    private OnShakeListener listener;

    public void setOnShakeListener(OnShakeListener shakeListener) {
        listener = shakeListener;
    }

    public interface OnShakeListener {
        void onShakeDetected();
        void onShakeStopped();
    }

    private void updateAccelParameters(float xNewAccel, float yNewAccel, float zNewAccel) {

        if (firstUpdate) {
            xPreviousAccel = xNewAccel;
            yPreviousAccel = yNewAccel;
            zPreviousAccel = zNewAccel;
            firstUpdate = false;
        } else {
            xPreviousAccel = xAccel;
            yPreviousAccel = yAccel;
            zPreviousAccel = zAccel;
        }
        xAccel = xNewAccel;
        yAccel = yNewAccel;
        zAccel = zNewAccel;
    }

    private boolean isAccelerationChanged() {
        float deltaX = Math.abs(xPreviousAccel - xAccel);
        float deltaY = Math.abs(yPreviousAccel - yAccel);
        float deltaZ = Math.abs(zPreviousAccel - zAccel);
        return (deltaX > shakeThreshold && deltaY > shakeThreshold)
                || (deltaX > shakeThreshold && deltaZ > shakeThreshold)
                || (deltaY > shakeThreshold && deltaZ > shakeThreshold);
    }

    @Override
    public void onSensorChanged(SensorEvent se) {
        updateAccelParameters(se.values[0], se.values[1], se.values[2]);
        if ((!shakeInitiated) && isAccelerationChanged()) {
            shakeInitiated = true;
        } else if ((shakeInitiated) && isAccelerationChanged()) {
            listener.onShakeDetected();
        } else if ((shakeInitiated) && (!isAccelerationChanged())) {
            listener.onShakeStopped();
            shakeInitiated = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
