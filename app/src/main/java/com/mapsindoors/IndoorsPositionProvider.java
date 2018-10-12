package com.mapsindoors;

import android.Manifest;
import android.content.Context;
import android.location.Location;

import com.customlbs.library.Indoors;
import com.customlbs.library.IndoorsException;
import com.customlbs.library.IndoorsFactory;
import com.customlbs.library.IndoorsLocationListener;
import com.customlbs.library.LocalizationParameters;
import com.customlbs.library.callbacks.IndoorsServiceCallback;
import com.customlbs.library.callbacks.LoadingBuildingStatus;
import com.customlbs.library.model.Building;
import com.customlbs.library.model.Zone;
import com.customlbs.library.util.IndoorsCoordinateUtil;
import com.customlbs.shared.Coordinate;
import com.mapsindoors.mapssdk.OnPositionUpdateListener;
import com.mapsindoors.mapssdk.Point;
import com.mapsindoors.mapssdk.PositionProvider;
import com.mapsindoors.mapssdk.MPPositionResult;
import com.mapsindoors.mapssdk.OnStateChangedListener;
import com.mapsindoors.mapssdk.PermissionsAndPSListener;
import com.mapsindoors.mapssdk.PositionResult;

import java.util.List;

public class IndoorsPositionProvider implements PositionProvider, IndoorsLocationListener, IndoorsServiceCallback {

    private Context context;
    private Indoors indoors;
    private Building building;
    private OnPositionUpdateListener onPositionUpdateListener;
    private float lastOrientation;

    public IndoorsPositionProvider(Context context) {
        this.context = context;
    }

    @Override
    public String[] getRequiredPermissions() {
        return new String[] {Manifest.permission.ACCESS_COARSE_LOCATION};
    }

    @Override
    public boolean isPSEnabled() {
        return true;
    }

    @Override
    public void startPositioning(String s) {
        if (indoors == null) {
            IndoorsFactory.createInstance(context, "6926cf09-5351-4de6-8da1-4f9a80ee7db7", this);
        } else {
            indoors.registerLocationListener(this);
        }
    }

    @Override
    public void stopPositioning(String s) {
        indoors.removeLocationListener(this);
    }

    @Override
    public boolean isRunning() {
        return indoors != null;
    }

    @Override
    public void addOnPositionUpdateListener(OnPositionUpdateListener onPositionUpdateListener) {
        this.onPositionUpdateListener = onPositionUpdateListener;
    }

    @Override
    public void removeOnPositionUpdateListener(OnPositionUpdateListener onPositionUpdateListener) {
        this.onPositionUpdateListener = null;
    }

    @Override
    public void setProviderId(String s) {
    }

    @Override
    public void addOnstateChangedListener(OnStateChangedListener onStateChangedListener) {
    }

    @Override
    public void removeOnstateChangedListener(OnStateChangedListener onStateChangedListener) {
    }

    @Override
    public void checkPermissionsAndPSEnabled(PermissionsAndPSListener permissionsAndPSListener) {
    }

    @Override
    public String getProviderId() {
        return "indoo.rs";
    }

    @Override
    public PositionResult getLatestPosition() {
        return null;
    }

    @Override
    public void startPositioningAfter(int i, String s) {

    }

    @Override
    public void terminate() {
        IndoorsFactory.releaseInstance(this);
    }

    @Override
    public void connected() {
        indoors = IndoorsFactory.getInstance();

        indoors.enableEvaluationMode();

        indoors.registerLocationListener(this);

        indoors.setLocatedCloudBuilding(1388411283, new LocalizationParameters(), true);
    }

    @Override
    public void loadingBuilding(LoadingBuildingStatus loadingBuildingStatus) {
    }

    @Override
    public void buildingLoaded(Building building) {
        this.building = building;
    }

    @Override
    public void leftBuilding(Building building) {
    }

    @Override
    public void buildingReleased(Building building) {
    }

    @Override
    public void positionUpdated(Coordinate coordinate, int accuracy) {
        Location location = IndoorsCoordinateUtil.toGeoLocation(coordinate, building);

        MPPositionResult position = new MPPositionResult(new Point(location.getLatitude(), location.getLongitude()),
                accuracy,
                lastOrientation
        );

        int floorLevel = coordinate.z * 10;
        position.setFloor(floorLevel);

        onPositionUpdateListener.onPositionUpdate(position);
    }

    @Override
    public void orientationUpdated(float orientation) {
        lastOrientation = orientation;
    }

    @Override
    public void changedFloor(int i, String s) {
    }

    @Override
    public void enteredZones(List<Zone> list) {
    }

    @Override
    public void buildingLoadingCanceled() {
    }

    @Override
    public void onError(IndoorsException e) {
    }
}
