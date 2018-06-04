package com.mapsindoors;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.mapsindoors.mapssdk.Location;
import com.mapsindoors.mapssdk.MapControl;
import com.mapsindoors.mapssdk.MapsIndoors;


public class MainActivity extends AppCompatActivity
{

    SupportMapFragment mapFragment;
    GoogleMap          mGoogleMap;
    MapControl         myMapControl;

    final LatLng mapsPeopleCorporateHQLocation = new LatLng( 57.05813067, 9.95058065 );



	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		setContentView( R.layout.activity_main );

		setTitle( R.string.app_long_name );

        // Initialize the MapsIndoors SDK here by providing:
	    // - The application context
	    // - The MapsIndoors API key
        MapsIndoors.initialize(
                getApplicationContext(),
                getString(R.string.mapsindoors_api_key)
        );

		// Your Google Maps API key
		MapsIndoors.setGoogleAPIKey( getString( R.string.google_maps_key ) );

	    // Get a reference to Google Map's fragment
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.map_fragment ));

        // Get the Google Map object
        mapFragment.getMapAsync( googleMap -> {

            mGoogleMap = googleMap;

            // Set the camera to a known location (in our case, our Corporate headquarters)
            mGoogleMap.moveCamera( CameraUpdateFactory.newLatLngZoom( mapsPeopleCorporateHQLocation, 13.0f ) );

            // Setup MapsIndoors's MapControl
            setupMapsIndoors();
        } );
    }

	void setupMapsIndoors()
	{
		// Create a new MapControl instance
		myMapControl = new MapControl( this, mapFragment, mGoogleMap );

		// Add a marker click listener. We'll just show an info window with the POI's name
		myMapControl.setOnMarkerClickListener( marker -> {

			final Location loc = myMapControl.getLocation( marker );
			if( loc != null )
			{
				marker.showInfoWindow();
			}

			return true;
		});

		// Initialize MapControl to get the locations on the map, etc.
		myMapControl.init( error -> {
			if( error == null )
			{
				runOnUiThread( () -> {

					// Once MapControl has been initialized, set a floor
					myMapControl.selectFloor( 1 );

					// Animate the camera closer
					mGoogleMap.animateCamera( CameraUpdateFactory.newLatLngZoom( mapsPeopleCorporateHQLocation, 19f ) );
				});
			}
		});
    }
}
