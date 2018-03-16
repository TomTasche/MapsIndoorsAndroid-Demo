package com.amine.naimi.mapsindoortest;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mapspeople.data.listeners.OnLoadingDataReadyListener;
import com.mapspeople.debug.dbglog;
import com.mapspeople.info.MPErrorCodes;
import com.mapspeople.mapcontrol.MapControl;
import com.mapspeople.models.AppConfig;

import com.mapspeople.models.LocationDisplayRule;
import com.mapspeople.models.LocationDisplayRules;
import com.mapspeople.models.Point;
import com.mapspeople.models.Solution;
import com.mapspeople.models.VenueCollection;



import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    MapControl myMapControl;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbglog.useDebug(true);


        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment));

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(50.8591041, 4.4508542), 19.5f));
                setupMapIfNeeded();
            }
        });
    }


    void setupMapIfNeeded() {
        MapControl.setSolutionId("36f22726b8bf4fa78eb4c5e6");

// init a mapControl
        myMapControl = new MapControl(this, mapFragment, mMap);

        //adding display rule here will make the SDK crash
        LocationDisplayRules displayRules = new LocationDisplayRules();

        final LocationDisplayRule ruleA = new LocationDisplayRule.Builder("Office").setBitmapIcon(R.drawable.misdk_dot_black).setShowLabel(false).setZOn(17).build();

        displayRules.add(ruleA);

        myMapControl.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });


        myMapControl.addDisplayRules(displayRules);

        myMapControl.init(errorCode -> {
            if (errorCode == MPErrorCodes.NO_ERROR) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });


    }

}
