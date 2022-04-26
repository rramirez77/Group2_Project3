package android.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;
/*
-----------------------------------------------------------
   -----------------------------------------------------------
   Moved to Main Activity
   -----------------------------------------------------------
-----------------------------------------------------------

 */
public class StepActivity extends MainActivity implements SensorEventListener {
    SensorManager sensorMng = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    Sensor trigger = sensorMng.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    boolean moving = false;
    float previousTotalSteps = 0;
    @Override
    public void onResume() {
        super.onResume();
        moving = true;
        Sensor stepSensor = sensorMng.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor == null) {
            Toast.makeText(this, "No Sensor Found", Toast.LENGTH_SHORT).show();
        } else {
            sensorMng.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(moving){
            int totalSteps = (int)sensorEvent.values[0];
            int CurrentSteps = (int) (totalSteps - previousTotalSteps);
            /*Set to text view
                here

             */
        }

    }
    private void resetSteps(){


    }
    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("key", previousTotalSteps);
        editor.apply();
    }
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        float savedSteps = sharedPreferences.getFloat("key", 0);
        Log.d("StepActivity", "$savedNumber");
        previousTotalSteps = savedSteps;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
