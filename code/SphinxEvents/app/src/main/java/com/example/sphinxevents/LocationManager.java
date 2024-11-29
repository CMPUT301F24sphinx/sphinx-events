
package com.example.sphinxevents;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationManager {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;

    // Interface to handle location callbacks
    public interface OnLocationReceivedListener {
        void onLocationReceived(Location location);
        void onLocationError();
    }

    // Method to check location permission
    public static boolean isLocationPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // Method to request location permission
    public static void requestLocationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isLocationPermissionGranted(activity)) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    // Method to get the current location
    public static void getLastLocation(Context context, OnLocationReceivedListener listener) {
        // Check if location permission is granted
        if (isLocationPermissionGranted(context)) {
            try {
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    listener.onLocationReceived(location);
                                } else {
                                    listener.onLocationError();
                                }
                            }
                        });
            } catch (SecurityException e) {
                // Handle the exception when permission is missing or revoked
                listener.onLocationError();
                Toast.makeText(context, "Location permissions are required", Toast.LENGTH_SHORT).show();
            }
        } else {
            // If permissions are not granted, inform the listener
            listener.onLocationError();
            Toast.makeText(context, "Location permissions are not granted", Toast.LENGTH_SHORT).show();
        }
    }
}