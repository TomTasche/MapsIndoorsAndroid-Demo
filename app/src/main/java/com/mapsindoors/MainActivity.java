package com.mapsindoors;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.mapsindoors.mapssdk.Location;
import com.mapsindoors.mapssdk.MapControl;
import com.mapsindoors.mapssdk.MapsIndoors;
import com.mapsindoors.mapssdk.dbglog;


public class MainActivity extends AppCompatActivity
{
	public static final String TAG = MainActivity.class.getSimpleName();

	public static final int REQUEST_CODE_PERMISSIONS = 34168; //Random request code, use your own
	public static final int REQUEST_CODE_LOCATION = 58774; //Random request code, use your own

	SupportMapFragment mapFragment;
    GoogleMap          mGoogleMap;
    MapControl         myMapControl;

	IndoorsPositionProvider indoorsPositionProvider;

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

		requestPermissionsFromUser();
	}

	private void requestPermissionsFromUser() {
		/**
		 * Since API level 23 we need to request permissions for so called dangerous permissions from the user.
		 *
		 * You can see a full list of needed permissions in the Manifest File.
		 */
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			int permissionCheckForLocation = ContextCompat.checkSelfPermission(
					MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

			if (permissionCheckForLocation != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(
						new String[] {
								Manifest.permission.ACCESS_COARSE_LOCATION
						},
						REQUEST_CODE_PERMISSIONS);
			} else {
				//If permissions were already granted,
				// we can go on to check if Location Services are enabled.
				checkLocationIsEnabled();
			}
		} else {
			//Continue loading Indoors if we don't need user-settable-permissions.
			// In this case we are pre-Marshmallow.
			continueLoading();
		}
	}

	private void continueLoading() {
		indoorsPositionProvider = new IndoorsPositionProvider(this);

		// Initialize the MapsIndoors SDK here by providing:
		// - The application context
		// - The MapsIndoors API key
		MapsIndoors.initialize(
				getApplicationContext(),
				getString(R.string.mapsindoors_api_key)
		);

		// Your Google Maps API key
		MapsIndoors.setGoogleAPIKey(getString(R.string.google_maps_key));

		// Get a reference to Google Map's fragment
		mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment));

		// Get the Google Map object
		mapFragment.getMapAsync(googleMap -> {

			mGoogleMap = googleMap;

			// Set the camera to a known location (in our case, our Corporate headquarters)
			mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MAPSPEOPLE_CORPORATE_HQ_LOCATION, 13.0f));

			// Setup MapsIndoors's MapControl
			setupMapsIndoors();
		});
	}

	private void checkLocationIsEnabled() {
		// On android Marshmallow we also need to have active Location Services (GPS or Network based)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			boolean isNetworkLocationProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			boolean isGPSLocationProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			if (!isGPSLocationProviderEnabled && !isNetworkLocationProviderEnabled) {
				// Only if both providers are disabled we need to ask the user to do something
				Toast.makeText(this, "Location is off, enable it in system settings.", Toast.LENGTH_LONG).show();
				Intent locationInSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				this.startActivityForResult(locationInSettingsIntent, REQUEST_CODE_LOCATION);
			} else {
				continueLoading();
			}
		} else {
			continueLoading();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_LOCATION) {
			// Check if the user has really enabled Location services.
			checkLocationIsEnabled();
		}
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

					MapsIndoors.setPositionProvider(indoorsPositionProvider);

					myMapControl.showUserPosition(true);

					indoorsPositionProvider.startPositioning(null);
				});
			}
		});
    }

	@Override
	protected void onStop() {
		super.onStop();

		if (indoorsPositionProvider != null) {
			indoorsPositionProvider.stopPositioning(null);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (indoorsPositionProvider != null) {
			indoorsPositionProvider.terminate();
			MapsIndoors.setPositionProvider(null);
			indoorsPositionProvider = null;
		}
	}
}
