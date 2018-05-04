package com.app.pashmak;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.app.pashmak.Adapter.MyMarkerInfoWindowAdapter;
import com.app.pashmak.Model.Billboard;
import com.app.pashmak.Utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBillboardsMap extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {


    private static final int ERROR_DIALOG_REQUEST = 9001;


    private View view;
    private Context context;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final float DEFAULT_ZOOM = 15f;

    private boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private GoogleApiClient mLocationClient;
    private LocationListener mListener;
    private Marker marker;
    private MyMapView mapView;
    private MyNestedScrollView nestedScrollView;
    private ViewPager viewPagerBillboards;
    private MySupportMapFragment mapFragment;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private EditText etSearch;
    private FragmentActivity activity;
    private ImageButton ibGPS;
    private ArrayList<Marker> markers;
    private boolean mapIsReady = false;
    private MyMarkerInfoWindowAdapter myInfoWindowAdapter;

    public FragmentBillboardsMap() {
        // Required empty public constructor
        setHasOptionsMenu(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_billboards_map, container, false);
        context = view.getContext();
        activity = getActivity();

        nestedScrollView = (MyNestedScrollView) container.getParent();
        mapView = view.findViewById(R.id.mapView);
        viewPagerBillboards = container.findViewById(R.id.viewPagerBillboards);
        mapView.onCreate(savedInstanceState);
        etSearch = activity.findViewById(R.id.etSearch);
        ibGPS = view.findViewById(R.id.ibGPS);

        getLocationPermissions();

        init();
//        isServicesOk();
        try {
            MapsInitializer.initialize(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mapIsReady = true;
        mMap = googleMap;

//        if (mLocationPermissionsGranted) {
//            getMyLocation();
//        }
        setFarsiMap();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Utils.mToast(context, "لطفا اجازه دسترسی را تایید کنید", Toast.LENGTH_SHORT);
            getLocationPermissions();
            return;
        }
        myInfoWindowAdapter = new MyMarkerInfoWindowAdapter(context);

        mMap.setInfoWindowAdapter(myInfoWindowAdapter);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
//        init();

        viewSerachedMarkers(FragmentBillboardsList.billboards);
    }

    private void init() {

        nestedScrollView.addInterceptScrollView(mapView);


        markers = new ArrayList<Marker>();


        ibGPS.setOnClickListener(this);
    }

    private void geoLocate() {
        String searchString = etSearch.getText().toString();
        Geocoder geocoder = new Geocoder(context);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (list.size() > 0) {
            Log.d(MainActivity.TAG, "geoLocate: " + list.size());
            Address address = list.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            moveCamera(latLng, DEFAULT_ZOOM);
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(address.getLocality());
            mMap.addMarker(options);
        } else {
            Toast.makeText(context, "نتیجه ای یافت نشد", Toast.LENGTH_SHORT).show();
        }
        Utils.hideSoftKeyboard(activity);
    }

    private void getMyLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        try {
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM);
                                mMap.setMyLocationEnabled(true);
                                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            } else {
                                Utils.mToast(context, "لطفا لوکیشن دستگاه را روشن کنید", Toast.LENGTH_SHORT);
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                            }
                        } else {
                            Utils.mToast(context, "خطا در یافتن موقعیت فعلی", Toast.LENGTH_SHORT);

                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(MainActivity.TAG, "get my location exception: " + e.getMessage());

        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void moveCamera(LatLngBounds bounds) {
//        int width = getResources().getDisplayMetrics().widthPixels;
//        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = 100;
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

    private void setFarsiMap() {
        Locale locale = new Locale("fa");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {

            config.setLocale(locale);
            context.createConfigurationContext(config);
        } else { //deprecated
            config.locale = locale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
    }

    private void initMap() {
//        MySupportMapFragment mSupportMapFragment;
//        mSupportMapFragment = (MySupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        mapView.getMapAsync(this);

    }


    private void getLocationPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                        COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionsGranted = true;
            initMap();
        } else {
            ActivityCompat.requestPermissions(activity, permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {

                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionsGranted = false;
                        return;
                    }
                }
                mLocationPermissionsGranted = true;

                initMap();

            }
        }
    }

    private boolean isServicesOk() {

//        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        }
//        else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable))
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(activity, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(context, "can't connect to mapping service", Toast.LENGTH_SHORT).show();
        }

        return false;

    }


//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_billboards_map, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(activity);
                //add parent in child activity ( in manifest)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibGPS:
                getMyLocation();
                break;
        }
    }

    public void viewSerachedMarkers(ArrayList<Billboard> billboards) {
//  set id of billboard in title for access billboard from infoWindowAdapter
        if (!mapIsReady) {
            return;
        }
        if (billboards.size() == 0) {
            mMap.clear();
            return;
        }
        mMap.clear();

        myInfoWindowAdapter.setBillboards(billboards);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int i = 0;
        for (Billboard billboard : billboards) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(billboard.getLat(), billboard.getLng()))
                    .title(String.valueOf(i)));

            builder.include(marker.getPosition());
            markers.add(marker);
            i++;
        }
        markers.size();

        LatLngBounds bounds = builder.build();

        moveCamera(bounds);


    }
}
