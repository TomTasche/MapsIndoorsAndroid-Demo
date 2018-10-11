package com.mapsindoors;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.customlbs.library.Indoors;
import com.customlbs.library.IndoorsException;
import com.customlbs.library.IndoorsFactory;
import com.customlbs.library.IndoorsLocationListener;
import com.customlbs.library.LocalizationParameters;
import com.customlbs.library.callbacks.IndoorsServiceCallback;
import com.customlbs.library.callbacks.LoadingBuildingStatus;
import com.customlbs.library.model.Building;
import com.customlbs.library.model.Zone;
import com.customlbs.shared.Coordinate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.mapsindoors.mapssdk.Location;
import com.mapsindoors.mapssdk.MPPositionResult;
import com.mapsindoors.mapssdk.MapControl;
import com.mapsindoors.mapssdk.MapsIndoors;
import com.mapsindoors.mapssdk.Point;
import com.mapsindoors.mapssdk.PositionResult;
import com.mapsindoors.mapssdk.dbglog;


public class MainActivity extends AppCompatActivity
{
	public static final String TAG = MainActivity.class.getSimpleName();

    SupportMapFragment mapFragment;
    GoogleMap          mGoogleMap;
    MapControl         myMapControl;

    static final LatLng MAPSPEOPLE_CORPORATE_HQ_LOCATION = new LatLng( 48.2005373, 16.3679884 );



	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		setContentView( R.layout.activity_main );

		setTitle( R.string.app_long_name );

		// Enable MapsIndoors debug messages (console)
		{
			dbglog.useDebug( true );
			dbglog.setCustomTagPrefix( TAG + "_" );
		}


		// Initialize the MapsIndoors SDK here by providing:
	    // - The application context
	    // - The MapsIndoors API key
        MapsIndoors.initialize(
                getApplicationContext(),
		        getString( R.string.mapsindoors_api_key )
        );

		// Your Google Maps API key
		MapsIndoors.setGoogleAPIKey( getString( R.string.google_maps_key ) );

	    // Get a reference to Google Map's fragment
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.map_fragment ));

        // Get the Google Map object
        mapFragment.getMapAsync( googleMap -> {

            mGoogleMap = googleMap;

            // Set the camera to a known location (in our case, our Corporate headquarters)
            mGoogleMap.moveCamera( CameraUpdateFactory.newLatLngZoom( MAPSPEOPLE_CORPORATE_HQ_LOCATION, 13.0f ) );

            // Setup MapsIndoors's MapControl
            setupMapsIndoors();
        } );
	}

	void setupMapsIndoors()
	{
		// Create a new MapControl instance
		myMapControl = new MapControl( this );

		myMapControl.setGoogleMap( mGoogleMap, mapFragment.getView() );

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
					myMapControl.selectFloor( 0 );

					// Animate the camera closer
					mGoogleMap.animateCamera( CameraUpdateFactory.newLatLngZoom( MAPSPEOPLE_CORPORATE_HQ_LOCATION, 19f ) );

					MapsIndoors.setPositionProvider(new IndoorsPositionProvider(this));

					myMapControl.showUserPosition(true);
				});
			}
		});
    }


}
