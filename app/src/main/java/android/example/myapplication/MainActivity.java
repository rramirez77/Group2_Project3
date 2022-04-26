package android.example.myapplication;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.example.myapplication.databinding.ActivityMainBinding;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager=null;
    private LocationListener locationListener=null;
    private ActivityMainBinding binding;
    private EditText editLocation = null;
    private static final String TAG = "Debug";
    SensorManager sensorMng = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    Sensor trigger = sensorMng.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    boolean moving = false;
    float previousTotalSteps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);
       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.home2, R.id.stats, R.id.leaderBoard, R.id.msg)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        resetSteps();
        loadData();

       // locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }
/*
    private Boolean GpsStatus() {
        ContentResolver contentResolver = getBaseContext().getContentResolver();


        boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver,
                LocationManager.GPS_PROVIDER);
        return gpsStatus;


    }

 */

/*
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location gps) {

            editLocation.setText("");

            String longitude = "Longitude: " + gps.getLongitude();
            Log.v(TAG, longitude);
            String latitude = "Latitude: " + gps.getLatitude();
            Log.v(TAG, latitude);
            String city =null;
            Geocoder gcd = new Geocoder(getBaseContext(),
                    Locale.getDefault());
            List<Address>  addresses;
            try {
                addresses = gcd.getFromLocation(gps.getLatitude(), gps
                        .getLongitude(), 1);
                if (addresses.size() > 0)
                    System.out.println(addresses.get(0).getLocality());
                city=addresses.get(0).getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String s = longitude+"\n"+latitude +
                    "\n Currrent City: "+city;
            editLocation.setText(s);
        }
    }
*/
    @Override
    public void onResume() {
        super.onResume();
        moving = true;
        Sensor stepSensor = sensorMng.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor == null) {
            Toast.makeText(this, "No Sensor Found", Toast.LENGTH_SHORT).show();
        } else {
            sensorMng.registerListener((SensorEventListener) this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

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
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}


