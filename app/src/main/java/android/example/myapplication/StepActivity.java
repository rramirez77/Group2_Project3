package android.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
/*
-----------------------------------------------------------
   -----------------------------------------------------------
   Moved to Main Activity
   -----------------------------------------------------------
-----------------------------------------------------------

 */
public class StepActivity extends MainActivity implements SensorEventListener {
    SensorManager sensorMng;
    Sensor trigger;
    boolean moving = false;
    float previousTotalSteps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        //setup sensor management
        sensorMng = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        trigger = sensorMng.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

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
