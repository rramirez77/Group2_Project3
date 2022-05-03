package android.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StepActivity extends AppCompatActivity implements SensorEventListener {
    SensorManager sensorMng;
    Sensor trigger;
    boolean moving = false;
    float previousTotalSteps = 0;
    TextView tv_stepsTaken;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        //Back button to return to home acativity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Sets Text vie wto tv_StepsTaken
        tv_stepsTaken = (TextView)findViewById(R.id.tv_stepsTaken);

        //Call loadData function
        loadData();
        //Call resetsteps function
        resetSteps();

        //setup sensor management to get the sensor to work
        sensorMng = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        trigger = sensorMng.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Boolean set to true once they start the activity
        moving = true;
        //Step sensor called to get information
        Sensor stepSensor = sensorMng.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        //if step sensor is not present display sensor not found error otherwise start listening
        if (stepSensor == null) {
            Toast.makeText(this, "No Sensor Found", Toast.LENGTH_SHORT).show();
        } else {
            sensorMng.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        //once stopped, sets boolean to false and doesnt track steps anymore
        moving = false;
        sensorMng.unregisterListener(this);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // if boolean is true, set textview to the value/number of steps taken
        if(moving){
            tv_stepsTaken.setText(String.valueOf(sensorEvent.values[0]));
        }

    }
    private void resetSteps(){


    }
    private void saveData(){
        //store data and refrence it later
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("key", previousTotalSteps);
        editor.apply();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("StepActivity");

    }
    private void loadData(){
        //loads stored data
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        float savedSteps = sharedPreferences.getFloat("key", 0);
        Log.d("StepActivity", "$savedNumber");
        previousTotalSteps = savedSteps;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
