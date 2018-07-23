package com.example.android.voicedin.helper_classes;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by abhin on 7/23/2018.
 */

public class CustomLocationListener implements LocationListener {
     private TextView gpsView;

    public void setGpsView(TextView gpsView) {
        this.gpsView = gpsView;
    }

    @Override
    public void onLocationChanged(Location location) {
        gpsView.setText("Latitude: " + location.getLatitude() + "\n" + "Longitude: " + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
