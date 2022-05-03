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
import com.google.android.gms.tasks.OnSuccessListener;
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
import android.widget.Toast;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //Google map
    private GoogleMap mMap;
    //LatLng point to save in Polyline
    private LatLng latlng;
    //client for getting location
    private FusedLocationProviderClient client;
    //binding to display map
    private ActivityMapsBinding binding;

   LatLng latLng;
    //LocationCallback for tracking locaiton
    private LocationCallback locationCallback;
    //Stop tracking button
    private Button stopTracking;
    //Start tracking button
    private Button startTracking;
    //Database instance
    private DatabaseReference databaseReference;

    //polyline
    PolylineOptions polylineOptions;
    Polyline polyline;



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
       //initialize route2 polyline
        //route2 = new PolylineOptions();


        //polyline = new Polyline(polylineOptions);
        polylineOptions = new PolylineOptions();
        //initilize Start tracking button
        startTracking = findViewById(R.id.StartButton);
        startTracking.setVisibility(View.VISIBLE);
        startTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //When click
                //startTracking
                polylineOptions = new PolylineOptions();


                getLocation();
                startTracking();
                //zoomIn();
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
                //polylineOptions = new PolylineOptions();
                polylineOptions = new PolylineOptions();
                //polyline = mMap.addPolyline(polylineOptions);
                //polyline =mMap.addPolyline(polylineOptions);

                //delete points on polyline
                //
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
            //if permission granted
            //get the location
            //getLocation();


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
                    LatLng tempLatLng= new LatLng(location.getLatitude(), location.getLongitude());


                    databaseReference.child("Journeys").child("LatLng").setValue(tempLatLng);

                }

            }

        };

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

    }//end of onCreate
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //show user's location
        //getLocation();

    }//END OF onMapRead



    //shows user's current location on the map
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

        else{
            //ask for permission first
            Toast.makeText(this, ":/", Toast.LENGTH_SHORT).show();

        }

    }//end of method

    private void getPoints(LatLng latlng) {


        if(latlng != null) {
            polylineOptions.add(latlng);
        }
    }

    private void drawPoints(){
        mMap.addPolyline(polylineOptions);
        zoomIn();

    }

    private void startTracking(){
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationListener();

        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {

        client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    LatLng temp = new LatLng(location.getLatitude(), location.getLongitude());
                    databaseReference.child("Journeys").child("LatLng").setValue(temp);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 16f));
                }
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void zoomIn(){

        /*client.getCurrentLocation(PRIORITY_HIGH_ACCURACY, new CancellationToken() {
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
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 16f));
                }




            }
        });*/

        client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng temp = new LatLng(location.getLatitude(), location.getLongitude());

                }

            }
        });
    }






}//END OF CLASS




/*****OLD******/

/*LatLng cupertino = new LatLng(37, -122);
        mMap.addMarker(new MarkerOptions().position(cupertino).title("Cupertino"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cupertino, 16f));*/

/*
    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            //Post post = dataSnapshot.getValue(Post.class);
            // ..

            Log.d("TAG", "Latitude " + dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.d("TAG", "databaseError:onCancelled", databaseError.toException());
        }
    };*/

 /*
        databaseReference.child("Journeys").child("LatLng").child("Longitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("Longitude: " + snapshot.child("Longitude").getValue());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("DATABASE LONGITUDE ERROR");

            }
        });*/

/*
DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/uuuu");
        date = LocalDate.now();
        String formattedDate = dtf.format(date);

 */

/*
        databaseReference.child("Journeys").child("LatLng").child("latitude").addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot) {
        Double latitude = snapshot.getValue(Double.class);
        Log.d("TAG", "onDataChangeLat: " + latitude );
        snapshot.
        }

@Override
public void onCancelled(@NonNull DatabaseError error) {

        }
        });

        databaseReference.child("Journeys").child("LatLng").child("longitude").addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot) {
        Double longitude = snapshot.getValue(Double.class);
        Log.d("TAG", "onDataChangeLong: " + longitude);

        }

@Override
public void onCancelled(@NonNull DatabaseError error) {

        }
        });
        */

/*
        databaseReference.child("Journeys").child("LatLng").addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Double currentValue = snapshot.getValue(Double.class);

                Double latitude = snapshot.child("latitude").getValue(Double.class);
                Double longitude = snapshot.child("longitude").getValue(Double.class);
                //LatLng ll= new LatLng();
               // ll = new LatLng(latitude, longitude);


                Log.d("TAG", "onChildAdded: LAT" + latitude);
                Log.d("TAG", "onChildAdded LNG " + longitude);



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //double currentValue = snapshot.getValue(Double.class);
                //Log.d("TAG", "onChildChanged: " + currentValue);
                Double latitude = snapshot.child("latitude").getValue(Double.class);
                Double longitude = snapshot.child("longitude").getValue(Double.class);
                //LatLng ll= new LatLng();
                // ll = new LatLng(latitude, longitude);


                Log.d("TAG", "onChildAdded: LAT" + latitude);
                Log.d("TAG", "onChildAdded LNG " + longitude);

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

*/
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
/*
private void drawPoints(){
    Location tempLocation;
    LatLng latLng;
    //databaseReference.child("Journeys").child("LatLng").child("Latitude").addValueEventListener(postListener);
/*
        databaseReference.child("Journeys").child("LatLng").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               // Log.d("TAG", "LatitudeFromDBresults " + snapshot.getValue());
                //System.out.println("Latitude: " + snapshot.getValue());

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    latlang.new
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("DATABASE LATITUDE ERROR");

            }
        });***********


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
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f));*******


    }
}*/