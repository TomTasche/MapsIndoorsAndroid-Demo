package com.mapsindoors;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.mapspeople.Location;
import com.mapspeople.LocationDisplayRule;
import com.mapspeople.MapControl;
import com.mapspeople.MapsIndoors;
import com.mapspeople.dbglog;
import com.mapspeople.errors.MIError;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    MapControl myMapControl;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // MapsIndoors SDK debug setup
        {
            // Enable/disable internal debugging ASAP
            dbglog.useDebug( BuildConfig.DEBUG  );

            // Add a log tag prefix to the MI SDK logs
            dbglog.setCustomTagPrefix( BuildConfig.FLAVOR + "_" );
        }

        // Initialize MapsIndoors Here
        MapsIndoors.initialize(
                getApplicationContext(),
                getString(R.string.mapsindoors_api_key),
                getString( R.string.google_maps_key )
        );

        //
	    MapsIndoors.synchronizeContent( errorCode -> {
		    if(dbglog.isDebugMode())
		    {
			    dbglog.LogI( TAG, "MapsIndoors.synchronizeContent -> errorCode: " + errorCode );
		    }
	    });

        //
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.map_fragment ));

        mapFragment.getMapAsync( googleMap -> {

            mMap = googleMap;
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( new LatLng( 50.8591041, 4.4508542 ), 19.5f ) );

            setupMapIfNeeded();
        } );
    }

    void setupMapIfNeeded() {

        myMapControl = new MapControl(this, mapFragment, mMap);

        //
//        LocationDisplayRules displayRules = new LocationDisplayRules();
//        final LocationDisplayRule ruleA = new LocationDisplayRule.Builder("Office").setBitmapIcon(R.drawable.misdk_dot_black).setShowLabel(false).setZOn(17).build();
//
//        displayRules.add(ruleA);
//        myMapControl.addDisplayRules( displayRules);


        myMapControl.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Location aLocation = myMapControl.getLocation( marker );
                if(aLocation!=null)
                {
                    String aa = aLocation.getId();
                    if(BuildConfig.DEBUG){}
                }
                return true;
            }
        });

        myMapControl.init( errorCode -> {
            if( errorCode == MIError.NO_ERROR )
            {
                runOnUiThread( new Runnable()
                {
                    @Override
                    public void run()
                    {

                    }
                });
            }
        });
    }
}
