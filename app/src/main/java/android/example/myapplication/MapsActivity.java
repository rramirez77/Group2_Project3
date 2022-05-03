package android.example.myapplication;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.example.myapplication.databinding.ActivityMapsBinding;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //Google map
    private GoogleMap mMap;
    //LatLng point to save in Polyline
    private LatLng latlng;
    //client for getting location
    private FusedLocationProviderClient client;
    //binding to display map
    private ActivityMapsBinding binding;
    //latitdue and longitude
    private LatLng latLng;
    //LocationCallback for tracking locaiton
    private LocationCallback locationCallback;
    //Stop tracking button
    private Button stopTracking;
    //Start tracking button
    private Button startTracking;
    //Database instance
    private DatabaseReference databaseReference;
    //polylineOptions to set up the polyline
    PolylineOptions polylineOptions;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding for google map
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //initialize client to obtain location
        client = LocationServices.getFusedLocationProviderClient(MapsActivity.this);

        //initialize datareference to user that's logged in
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //polyline = new Polyline(polylineOptions);
        polylineOptions = new PolylineOptions();


        //initilize Start tracking button
        startTracking = findViewById(R.id.StartButton);
        startTracking.setVisibility(View.VISIBLE);
        startTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //When clicked
                polylineOptions = new PolylineOptions();
                getLocation();
                startTracking();

                //show user their location
                showUserLocation();
                startTracking.setVisibility(View.GONE);
                stopTracking.setVisibility(View.VISIBLE);
            }
        });

        //initialize stop tracking button
        stopTracking = findViewById(R.id.StopButton);
        stopTracking.setVisibility(View.GONE);

        stopTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //When clicked

                if(ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //stop showingLocation
                    mMap.setMyLocationEnabled(false);
                }
                //stop tracking
                client.removeLocationUpdates(locationCallback);

                //draw points on Map
                drawPoints();

                databaseReference.child("Journeys").child("LatLng").child("latitude").setValue(null);
                databaseReference.child("Journeys").child("LatLng").child("longitude").setValue(null);

                polylineOptions = new PolylineOptions();
                stopTracking.setVisibility(View.GONE);
                startTracking.setVisibility(View.VISIBLE);

            }
        });


        //bottom navigation
        Button mapBtn = findViewById(R.id.mapButton);
        mapBtn.setOnClickListener(view -> {
            Intent i = new Intent(MapsActivity.this, MapsActivity.class);
            startActivity(i);
            finish();
        });

        Button friendBtn = findViewById(R.id.friendButton);
        friendBtn.setOnClickListener(view -> {
            Intent i = new Intent(MapsActivity.this, FriendActivity.class);
            startActivity(i);
            finish();
        });

        Button homeBtn = findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(view -> {
            Intent i = new Intent(MapsActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        });



        //Ask user for location tracking permission
        if(ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){


        }else{
            //permission denied, ask again
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        }

        //location call back to get locations
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ADD to list

                    LatLng tempLatLng= new LatLng(location.getLatitude(), location.getLongitude());


                    databaseReference.child("Journeys").child("LatLng").setValue(tempLatLng);

                }

            }

        };


        //Takes a screenshot of the latitude and longitude in the database whenever there is a change
        databaseReference.child("Journeys").child("LatLng").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Double latitude = snapshot.child("latitude").getValue(Double.class);
                Double longitude = snapshot.child("longitude").getValue(Double.class);
                if(latitude != null && longitude != null) {
                    latlng = new LatLng(latitude, longitude);

                    getPoints(latlng);
                    Log.d("TAG", "onDataChange: " + "Lat:" + latitude + "Long:" + longitude);
                }
                else{
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    //Displays Map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }



    //shows user's live current location on the map
    public void showUserLocation(){
        if(ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }else{
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

    }


    //set up a location listener to track location
    private void locationListener() {

        //if location is enabled
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000);
            client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        }

    }


    //add lat and longitude points to the polylineOptions
    private void getPoints(LatLng latlng) {


        if(latlng != null) {
            polylineOptions.add(latlng);
        }
    }

    //draws the points on the map
    private void drawPoints(){
        mMap.addPolyline(polylineOptions);
        zoomIn();

    }

    //if permission is granted, calls location listener to start tracking
    private void startTracking(){
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationListener();

        }
    }

    //gets the location
    @SuppressLint("MissingPermission")
    private void getLocation() {

        client.getCurrentLocation(PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }

            @Override
            public boolean isCancellationRequested() {
                return false;
            }
        }).addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng temp = new LatLng(location.getLatitude(), location.getLongitude());
                    databaseReference.child("Journeys").child("LatLng").setValue(temp);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 16f));
                }




            }
        });



    }

    //zooms into the last location tracked to show in the map
    @SuppressLint("MissingPermission")
    private void zoomIn(){
        client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng temp = new LatLng(location.getLatitude(), location.getLongitude());

                }

            }
        });
    }


}












