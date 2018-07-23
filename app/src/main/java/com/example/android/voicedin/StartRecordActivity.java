package com.example.android.voicedin;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.android.voicedin.helper_classes.CustomLocationListener;

public class StartRecordActivity extends AppCompatActivity {

    private static final String TAG = "StartRecordActivity" ;
    private static final int PERMISSION_REQUEST_CODE = 1;
    TextView gpsView;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_record);

        // Start Location Management Activities
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        gpsView = (TextView) findViewById(R.id.gps_view);

        if (!gpsEnabled) {
            enableLocationSettings();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "System Permissions not Present");
                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
            LocationProvider provider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
            CustomLocationListener listener = new CustomLocationListener();
            listener.setGpsView(gpsView);
            locationManager.requestLocationUpdates(provider.getName(), 100000, 10, listener);
        }
    }



    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }
}
