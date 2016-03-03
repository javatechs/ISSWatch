package com.belithco.iss.isswatch;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ISSMapActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String ISSWATCH = "ISSWatch:";
    private static final String TAG = ISSWATCH + ISSMapActivity.class.getSimpleName();

    private GoogleMap mMap = null;
    public static Handler handler = null;
    private BitmapDescriptor issIcon = null;
    private BitmapDescriptor dotIcon = null;
    Marker markerPrv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issmap);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Start our polling service
        ISSLocationService.startServiceTriggering(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        issIcon = BitmapDescriptorFactory.fromResource(R.drawable.rocket64);
        dotIcon = BitmapDescriptorFactory.fromResource(R.drawable.dot);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle reply = msg.getData();
                double latitude = reply.getDouble(ISSLocationService.LATITUDE);
                double longitude = reply.getDouble(ISSLocationService.LONGITUDE);
                Log.d(TAG, "Lat: "+latitude+" long: "+longitude);
                // Add a marker in Sydney and move the camera
                LatLng issPosition = new LatLng(latitude, longitude);
                if (null!=issIcon) {
                    if (null!=markerPrv) {
                        markerPrv.setIcon(dotIcon);
                    }
                    MarkerOptions markerOpt = new MarkerOptions().position(issPosition).title("ISS Location").icon(issIcon);
                    markerPrv = mMap.addMarker(markerOpt);
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(issPosition));
            }
        };
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}