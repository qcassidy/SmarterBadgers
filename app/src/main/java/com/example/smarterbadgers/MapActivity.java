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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.util.Listener;
import com.google.maps.android.SphericalUtil;

import org.json.JSONObject;

import java.text.DecimalFormat;


public class MapActivity extends FragmentActivity {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;
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

    //coordinates of libraries
    LatLng[] libraryCoords = {
            new LatLng(43.076757, -89.4034447),
            new LatLng(43.0745924, -89.4015308),
            new LatLng(43.076047, -89.4032047),
            new LatLng(43.0761127, -89.4019648),
            new LatLng(43.0739918, -89.4016058),
            new LatLng(43.0762592, -89.4152843),
            new LatLng(43.0742468, -89.4103867),
            new LatLng(43.074423, -89.4043027),
            new LatLng(43.0753703, -89.4000274),
            new LatLng(43.0754199, -89.4002731),
            new LatLng(43.0726553, -89.4021646),
            new LatLng(43.0738501, -89.40765),
            new LatLng(43.0765915, -89.4074134),
            new LatLng(43.0755446, -89.4157191),
            new LatLng(43.073458, -89.3895041),
            new LatLng(43.0730785, -89.3847733),
            new LatLng(43.0750216, -89.3820162),
            new LatLng(43.0659225, -89.4173755),
    };

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
            displayLibraries();
        });
    }

    //Get and display user's current location
    private void displayMyLocation() {
        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(this, task -> {
                        Location mLastKnownLocation = task.getResult();
                        LatLng position = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        if (task.isSuccessful() && mLastKnownLocation != null) {
                            mMap.addMarker(new MarkerOptions()
                                    .position(position)
                                    .title("Current Location")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        }
                        moveCamera(mLastKnownLocation);
                        getDistances(position);
                    });
        }
    }

    //get distance in miles from currLocation to every library
    private void getDistances(LatLng currPosition) {
        double distanceBetween = 0.0;

        for (int i = 0; i < libraryCoords.length; i++) {
            distanceBetween = SphericalUtil.computeDistanceBetween(currPosition, libraryCoords[i]);
            distanceBetween *= 0.000621371;
            String distance = String.format("%.2f", distanceBetween);
            libraryList[i] = libraryList[i] + ": " + distance + " mi";
        }
        libListAdapter.notifyDataSetChanged();
    }

    //zoom camera to current location
    public void moveCamera(Location currLocation) {
        if (currLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currLocation.getLatitude(), currLocation.getLongitude()), 15));
        }
    }

    //coords of libraries in madison, displays markers
    private void displayLibraries() {
        for (int i = 0; i < libraryList.length; i++) {
            mMap.addMarker(new MarkerOptions()
                    .position(libraryCoords[i])
                    .title(libraryList[i]));
        }
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

    // back button functionality
    public void goToTimer(View view) {
        this.finish();
    }
}