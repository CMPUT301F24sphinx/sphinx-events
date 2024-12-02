/*
 * Class Name: LocationManager
 * Date: 2024-11-25
 *
 * Description:
 * This class manages location services, including checking for location permissions,
 * requesting them if necessary, and retrieving the device's last known location.
 * It uses Google's FusedLocationProviderClient to obtain location data and handles
 * location permission requests for Android devices.
 */

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

/**
 * This class manages location services, including checking for location permissions,
 * requesting them if necessary, and retrieving the device's last known location.
 * It uses Google's FusedLocationProviderClient to obtain location data and handles
 * location permission requests for Android devices.
 */
public class LocationManager {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;

    /**
     * Interface to handle location callbacks.
     * Implement this interface to receive location updates or handle errors.
     */
    public interface OnLocationReceivedListener {
        /**
         * Called when the location is successfully received.
         * @param location the received location object
         */
        void onLocationReceived(Location location);

        /**
         * Called when there is an error in retrieving the location.
         */
        void onLocationError();
    }

    /**
     * Checks if location permissions are granted.
     * @param context the context in which the permission check is performed
     * @return true if location permissions are granted, false otherwise
     */
    public static boolean isLocationPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Requests location permissions from the user.
     * If permissions are not granted, it prompts the user to grant location access.
     * @param activity the activity from which the request is made
     */
    public static void requestLocationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isLocationPermissionGranted(activity)) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    /**
     * Retrieves the device's last known location.
     * Uses the FusedLocationProviderClient to fetch the most recent location and sends it to the listener.
     * @param context the context from which the method is called
     * @param listener the listener that receives the location or an error
     */
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