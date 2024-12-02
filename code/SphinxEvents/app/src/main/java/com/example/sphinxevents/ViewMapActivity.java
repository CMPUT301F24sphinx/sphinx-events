/*
 * Class Name: ViewMapActivity
 * Date: 2024-11-28
 *
 * Description:
 * The ViewMapActivity is responsible for displaying a map of event participants' locations
 * and the event's facility. It uses the Google Maps API to show markers for the locations
 * of entrants and the facility, positioning the camera on the facility by default. The activity
 * retrieves the event and entrants' locations from the database and displays them on the map.
 *
 */

package com.example.sphinxevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * The ViewMapActivity is responsible for displaying a map of event participants' locations
 * and the event's facility. It uses the Google Maps API to show markers for the locations
 * of entrants and the facility, positioning the camera on the facility by default. The activity
 * retrieves the event and entrants' locations from the database and displays them on the map.
 */
public class ViewMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private DatabaseManager databaseManager;
    private Event event;  // The event to show the map for

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment == null) {
            Toast.makeText(getApplicationContext(), "Error loading map.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            mapFragment.getMapAsync(this);
        }

        // Obtains event from intent
        Intent intent = getIntent();
        if (intent != null) {
            event = (Event) intent.getSerializableExtra("event");
        }

        databaseManager = DatabaseManager.getInstance();

        // Back button clicked -> finish activity
        ImageButton backButton = findViewById(R.id.view_map_back_button);
        backButton.setOnClickListener(v -> {
            finish();
        });
    }

    /**
     * Called when the Google Map is ready to be used.
     * Adds markers for entrants' locations
     * Adds the facility location with the camera positioned on it
     *
     * @param googleMap The GoogleMap instance that is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Fetches location of entrants from database
        databaseManager.getUserLocations(event.getEventId(), new DatabaseManager.getUserLocationsCallback() {
            @Override
            public void onSuccess(ArrayList<UserLocation> locations) {

                // Display all user pings
                for (UserLocation location : locations) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Entrant"));
                }

                // Display where the facility is
                UserLocation facilityLocation = event.getFacilityLocation();
                LatLng facilityLatLng = new LatLng(facilityLocation.getLatitude(), facilityLocation.getLongitude());
                googleMap.addMarker(new MarkerOptions()
                        .position(facilityLatLng)
                        .title("Facility")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                // Move camera to facility location
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(facilityLatLng, 10));
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Error loading map.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}