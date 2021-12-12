package com.example.smarterbadgers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.util.Listener;

import org.json.JSONObject;


public class MapActivity extends FragmentActivity {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;
    private final int PROXIMITY_RADIUS = 40;
    private final String API_KEY = "AIzaSyAuO8edi3V3n93ZxeIdGJfFp80oRPIM39g";
    private Button backButton;
    private ListView libraries;
    private ArrayAdapter<String> libListAdapter;
    private String[] libraryList = {"College Library",
            "Memorial Library",
            "Arthur H Robinson Map Library",
            "Information School Library",
            "Kohler Art Library",
            "Steenbock Memorial Library",
            "Social Work Library",
            "Law School Library",
            "Digital Collections Center",
            "Mills Music Library",
            "Nieman Grant Journalism Room",
            "Astronomy, Mathematics, and Physics Library",
            "Center for Demography Library",
            "Plant Pathology Library",
            "Madison Public Library - Central",
            "Wisconsin State Law Library",
            "Public Instruction Library",
            "Madison Public Library - Monroe Street"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this::goToTimer);

        libraries = findViewById(R.id.libraries);
        libListAdapter = new ArrayAdapter<String>(this, R.layout.map_list_item, R.id.MapItemTextView, libraryList);
        libraries.setAdapter(libListAdapter);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            displayMyLocation();
            //moveCamera();
            displayLibraries();
        });


    }

    //Get and display user's current location
    private void displayMyLocation() {
        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if(permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        else {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(this, task -> {
                        Location mLastKnownLocation = task.getResult();
                        if(task.isSuccessful() && mLastKnownLocation != null) {
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
                                    .title("Current Location"));
                        }
                    });
        }
    }

    /*
    public void moveCamera() {
        if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), 5));
        }
    }*/

    //coords of libraries in madison, displays markers
    private void displayLibraries() {
        LatLng collegeLibrary = new LatLng(43.076757, -89.4034447);
        LatLng memorialLibrary = new LatLng(43.0745924, -89.4015308);
        LatLng mapLibrary = new LatLng(43.076047, -89.4032047);
        LatLng infoLibrary = new LatLng(43.0761127, -89.4019648);
        LatLng artLibrary = new LatLng(43.0739918, -89.4016058);
        LatLng steenbockLibrary = new LatLng(43.0762592, -89.4152843);
        LatLng socialWorkLibrary = new LatLng(43.0742468, -89.4103867);
        LatLng lawLibrary = new LatLng(43.074423, -89.4043027);
        LatLng digitalLibrary = new LatLng(43.0753703, -89.4000274);
        LatLng musicLibrary = new LatLng(43.0754199, -89.4002731);
        LatLng journalismLibrary = new LatLng(43.0726553, -89.4021646);
        LatLng ampLibrary = new LatLng(43.0738501, -89.40765);
        LatLng demographyLibrary = new LatLng(43.0765915, -89.4074134);
        LatLng plantLibrary = new LatLng(43.0755446, -89.4157191);
        LatLng centralLibrary = new LatLng(43.073458, -89.3895041);
        LatLng stateLawLibrary = new LatLng(43.0730785, -89.3847733);
        LatLng instructionLibrary = new LatLng(43.0750216, -89.3820162);
        LatLng monroeLibrary = new LatLng(43.0659225, -89.4173755);

        mMap.addMarker(new MarkerOptions()
                .position(collegeLibrary)
                .title("College Library"));
        mMap.addMarker(new MarkerOptions()
                .position(memorialLibrary)
                .title("Memorial Library"));
        mMap.addMarker(new MarkerOptions()
                .position(mapLibrary)
                .title("Arthur H Robinson Map Library"));
        mMap.addMarker(new MarkerOptions()
                .position(infoLibrary)
                .title("Information School Library"));
        mMap.addMarker(new MarkerOptions()
                .position(artLibrary)
                .title("Kohler Art Library"));
        mMap.addMarker(new MarkerOptions()
                .position(steenbockLibrary)
                .title("Steenbock Memorial Library"));
        mMap.addMarker(new MarkerOptions()
                .position(socialWorkLibrary)
                .title("Social Work Library"));
        mMap.addMarker(new MarkerOptions()
                .position(lawLibrary)
                .title("Law School Library"));
        mMap.addMarker(new MarkerOptions()
                .position(digitalLibrary)
                .title("Digital Collections Center"));
        mMap.addMarker(new MarkerOptions()
                .position(musicLibrary)
                .title("Mills Music Library"));
        mMap.addMarker(new MarkerOptions()
                .position(journalismLibrary)
                .title("Nieman Grant Journalism Room"));
        mMap.addMarker(new MarkerOptions()
                .position(ampLibrary)
                .title("Astronomy, Mathematics, and Physics Library"));
        mMap.addMarker(new MarkerOptions()
                .position(demographyLibrary)
                .title("Center For Demography Library"));
        mMap.addMarker(new MarkerOptions()
                .position(plantLibrary)
                .title("Plant Pathology Library"));
        mMap.addMarker(new MarkerOptions()
                .position(plantLibrary)
                .title("Plant Pathology Library"));
        mMap.addMarker(new MarkerOptions()
                .position(centralLibrary)
                .title("Madison Public Library - Central"));
        mMap.addMarker(new MarkerOptions()
                .position(stateLawLibrary)
                .title("Wisconsin State Law Library"));
        mMap.addMarker(new MarkerOptions()
                .position(instructionLibrary)
                .title("Public Instruction Pro Library"));
        mMap.addMarker(new MarkerOptions()
                .position(monroeLibrary)
                .title("Madison Public Library - Monroe Street"));
    }

    //change drawable vector to bitmap for marker icon
    private BitmapDescriptor BitmapFromVector(Context context, int vectorId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void goToTimer(View view) {
        this.finish();
        //Intent intent = new Intent(this, TimerFragment.class);
        //startActivity(intent);
    }

    /*
    private void loadLibraries(double longitude, double latitude) {
        Intent intent = getIntent();
        String type = "library";

        StringBuilder googlePlacesUrl =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append(PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=").append(type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + API_KEY);

        JsonObjectRequest request = new JsonObjectRequest(googlePlacesUrl.toString(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {

                        Log.i(TAG, "onResponse: Result= " + result.toString());
                        parseLocationResult(result);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                });

        AppController.getInstance().addToRequestQueue(request);
    }
*/
}