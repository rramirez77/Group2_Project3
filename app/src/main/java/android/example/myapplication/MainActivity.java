package android.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Address;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.provider.Settings;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import java.util.*;


import android.example.myapplication.databinding.ActivityMainBinding;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager=null;
    private LocationListener locationListener=null;
    private ActivityMainBinding binding;
    private EditText editLocation = null;
    private static final String TAG = "Debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }
     /*
    private Boolean GpsStatus() {
        ContentResolver contentResolver = getBaseContext().getContentResolver();


        boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver,
                LocationManager.GPS_PROVIDER);
        return gpsStatus;


    }
    */

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

}


