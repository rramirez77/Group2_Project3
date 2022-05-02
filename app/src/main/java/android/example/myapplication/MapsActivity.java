package android.example.myapplication;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.example.myapplication.databinding.ActivityMapsBinding;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient client;
    private LinkedList<Location> listOfLocations;
    private LocationCallback locationCallback;
    private FirebaseDatabase  firebaseDatabase;
    private LocalDate date;


    private Location currentLocation;
    private Button stopTracking;
    private Button startTracking;
    private String routeID;





    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        client = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        listOfLocations = new LinkedList<Location>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/uuuu");
        date = LocalDate.now();
        String formattedDate = dtf.format(date);
        routeID = "sd48i45g590";


       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

       //make this everytime the app runs
        //databaseReference.child("Journeys").child(formattedDated).setValue("Testing");



        //databaseReference.child("Journeys").child("RouteID").child("Date").setValue(formattedDate);








        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //if permission granted
            getLocation();


        }else{
            //permission denied
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        }


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ADD to list
                    //databaseReference.getReference("Location").child("Location").setValue("Lat: " + location.getLongitude());

                   listOfLocations.add(location);

                   Log.d("TAG", "onLocationResult: " + location.getLatitude() + "," + location.getLongitude());




                }
            }
        };

        startTracking = findViewById(R.id.StartButton);
        startTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTracking();
            }
        });



        stopTracking = findViewById(R.id.StopButton);
        stopTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //stop tracking
                client.removeLocationUpdates(locationCallback);
                //draw points on Map
                drawPoints();
            }
        });








        /****NEW CODE*****/


    }//end of onCreate


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //show user's location
        showUserLocation();
        //get location every 5s






        /*LatLng cupertino = new LatLng(37, -122);
        mMap.addMarker(new MarkerOptions().position(cupertino).title("Cupertino"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cupertino, 16f));*/

    }//END OF onMapRead



    //shows user's current location on the map
    public void showUserLocation(){
        if(ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            mMap.setMyLocationEnabled(true);



        }else{
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

    }

    //TODO: zoom into the map
    //TODO: track locations every 5s
    //TODO: add locations to list
    //TODO: display polylines




    //set up a location listener to track location
    private void locationListener() {

        //if location is enabled
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

              LocationRequest locationRequest = LocationRequest.create();
              locationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
              locationRequest.setInterval(10000);

              client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());


            }

        else{
            //ask for permission first
            Toast.makeText(this, ":/", Toast.LENGTH_SHORT).show();

        }

    }//end of method

    private void drawPoints(){
        Location tempLocation;
        LatLng latLng;
        if(listOfLocations.isEmpty()){
            Toast.makeText(this, "Location list is empty", Toast.LENGTH_SHORT).show();


        }else{

            Toast.makeText(this, "ListSize: " + listOfLocations.size(), Toast.LENGTH_SHORT).show();
            PolylineOptions route = new PolylineOptions();
            LatLng latlng;



            for(int i =0; i<listOfLocations.size(); i++){
                latlng = new LatLng(listOfLocations.get(i).getLatitude(), listOfLocations.get(i).getLongitude());
                route.add(latlng);

            }

            mMap.addPolyline(route);
            LatLng tempLatLng = new LatLng(listOfLocations.getFirst().getLatitude(), listOfLocations.getFirst().getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tempLatLng, 14f));


            /*
            tempLocation = listOfLocations.getFirst();
            latLng = new LatLng(tempLocation.getLatitude(), tempLocation.getLongitude());

            mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f));*/


        }
    }

    private void startTracking(){
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationListener();

        }
    }

    private void getLocation() {
        client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

            }
        });

    }






}//END OF CLASS




/*****OLD******/
/*
    public void getLocation(){
        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
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

                newLocation.set(location);

                getCurrentLocation(newLocation);


            }
        });
    }*/