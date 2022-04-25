package android.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class StepActivity extends MainActivity implements SensorEventListener {
    SensorManager sensorMng = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    Sensor trigger = sensorMng.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    private long steps = 0;
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
    Sensor sensor = sensorEvent.sensor;
    float value = -1;
    float[] values = sensorEvent.values;
    if (values.length > 0){
        value = (float)values[0];
    }
    if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
        steps++;
    }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
