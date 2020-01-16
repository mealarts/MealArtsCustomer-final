package com.mealarts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mealarts.Adapters.AddressTypeAdapter;
import com.mealarts.Adapters.BottomMenuAdapter;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CheckExtras;
import com.mealarts.Helpers.CustomToast;
import com.mealarts.Helpers.PermissionChecker;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.Helpers.Utils.AddressUtils;
import com.mealarts.Helpers.Utils.BottomMenuUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DeliveryAddressActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    EditText edtFindLocation, edtLocation, edtHouseFlatNo, edtLandmark;
    Button btnDeliveryLocation;
    LinearLayout layoutLoader, layoutLocation;
    ImageView ivMarkerLoader, ivLoader, ivBack, ivSearch, ivCurrentLoc;

    Boolean isHome = false, isWork = false;
    String fromWhere, CurrentLat="", CurrentLong="", HomeAddress, WorkAddress, AddressType="Other",
            Action;

    RecyclerView rcLowerMenu, rcAddressTypes;
    ArrayList<BottomMenuUtils> bottomMenuArray;
    public static BottomMenuAdapter bottomMenuAdapter;
    Integer[] icons = {R.drawable.home,
            R.drawable.menu_card,
            R.drawable.account,
            R.drawable.cart};
    CustomToast customToast = new CustomToast();
    SharedPref sharedPref;
    SSLCertification sslCertification = new SSLCertification(DeliveryAddressActivity.this);
    CheckExtras connection;

    String[] type = {"Home" , "Work", "Other"};
    ArrayList<AddressUtils> AddressTypeList;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private GoogleMap mMap;
    MarkerOptions markerOptions;
    LatLng latLng;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    PermissionChecker permissionChecker;
    boolean isFirst = false;
    int locationCount = 0;
    float CurrentDistance= 7.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_address);

        Action = getIntent().getStringExtra("Action"); //insert, edit, delete
        fromWhere = getIntent().getStringExtra("fromWhere");

        sharedPref = new SharedPref(DeliveryAddressActivity.this);
        connection = new CheckExtras(DeliveryAddressActivity.this);
        permissionChecker = new PermissionChecker(DeliveryAddressActivity.this);

        layoutLoader = findViewById(R.id.layoutLoader);
        ivLoader = findViewById(R.id.ivLoader);
        Glide.with(DeliveryAddressActivity.this).asGif().load(R.drawable.mealarts_loader).into(ivLoader);
        layoutLoader.setVisibility(View.VISIBLE);

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> onBackPressed());

        ivCurrentLoc = findViewById(R.id.ivCurrentLoc);
        ivSearch = findViewById(R.id.ivSearch);

        edtFindLocation = findViewById(R.id.edtFindLocation);
        ivSearch.setOnClickListener(v -> {
            // Getting user input location
            String location = edtFindLocation.getText().toString();

            if(!location.equals("")){
                new GeoCoderTask().execute(location);
            }else edtFindLocation.setError("Search For Area, Street name...");
        });

        ivCurrentLoc.setOnClickListener(v -> {
            Location location = mMap.getMyLocation();

            if (location != null) {

                LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition position = mMap.getCameraPosition();

                CameraPosition.Builder builder = new CameraPosition.Builder();
                builder.zoom(mMap.getMaxZoomLevel());
                builder.target(target);

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));

            }
        });

        rcAddressTypes = findViewById(R.id.rcAddressTypes);
        rcAddressTypes.setHasFixedSize(true);
        rcAddressTypes.setLayoutManager(new GridLayoutManager(DeliveryAddressActivity.this, 3));
        AddressTypeList = new ArrayList<>();
        for(int i = 0; i < type.length ; i++){
            AddressUtils utils = new AddressUtils();
            utils.setAddressType(type[i]);
            utils.setSelected(getIntent().getStringExtra("AddressType") != null ?
                    type[i].equals(getIntent().getStringExtra("AddressType")) : i == type.length - 1);
            AddressTypeList.add(utils);
        }
        AddressTypeAdapter addressTypeAdapter = new AddressTypeAdapter(DeliveryAddressActivity.this, AddressTypeList);
        rcAddressTypes.setAdapter(addressTypeAdapter);

        if(Action.equals("edit"))
            rcAddressTypes.setVisibility(View.GONE);
        else rcAddressTypes.setVisibility(View.VISIBLE);

        addressTypeAdapter.setListener((tvAddressType, position) -> {
            AddressType = tvAddressType.getText().toString().trim();

            tvAddressType.setBackgroundColor(getResources().getColor(R.color.colorOrangeFade));
            AddressTypeList.get(position).setSelected(true);
            for(int i = 0 ; i < AddressTypeList.size() ; i++){
                if(i != position)
                    AddressTypeList.get(i).setSelected(false);
            }
            addressTypeAdapter.notifyDataSetChanged();
        });

//----------------------------------Bottom Tab Menu----------------------------------//
        rcLowerMenu = findViewById(R.id.rcLowerMenu);
        rcLowerMenu.setHasFixedSize(true);
        rcLowerMenu.setLayoutManager(new GridLayoutManager(DeliveryAddressActivity.this, 4));
        setBottomMenu();

        ivMarkerLoader = findViewById(R.id.ivMarkerLoader);
        layoutLocation = findViewById(R.id.layoutLocation);
        edtLocation = findViewById(R.id.edtLocation);
        edtHouseFlatNo = findViewById(R.id.edtHouseFlatNo);
        edtLandmark = findViewById(R.id.edtLandmark);
        btnDeliveryLocation = findViewById(R.id.btnDeliveryLocation);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.setAddressMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnDeliveryLocation.setOnClickListener(v -> {
            try {
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                JSONObject cartJsonObj = cartObj.getJSONObject(getResources().getString(R.string.CartJsonObj));
                if(cartObj.has(getResources().getString(R.string.CartJsonArray))
                        && cartObj.getJSONArray(getResources().getString(R.string.CartJsonArray)).length() == 0) {
                    String Location = edtLocation.getText().toString().trim();
                    String FlatHouseNo = edtHouseFlatNo.getText().toString().trim();
                    String Landmark = edtLandmark.getText().toString().trim();

                    if(Location.isEmpty())
                        edtLocation.setError("Required");
                    else if(FlatHouseNo.isEmpty())
                        edtHouseFlatNo.setError("Required");
                    else if(Landmark.isEmpty())
                        edtLandmark.setError("Required");
                    else{
                        layoutLoader.setVisibility(View.VISIBLE);
                        setLocation(Location, FlatHouseNo, Landmark, CurrentLat, CurrentLong);
                    }
                }else {
                    drawRoute(Double.parseDouble(CurrentLat), Double.parseDouble(CurrentLong), Double.parseDouble(cartJsonObj.getString("VendorLat")), Double.parseDouble(cartJsonObj.getString("VendorLong")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        /*try {
            JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
            JSONObject locationObj = cartObj.getJSONObject(getResources().getString(R.string.LocationJson));

            if(getIntent().getStringExtra("AddressType") != null) {
                AddressType = getIntent().getStringExtra("AddressType");
                if(AddressType.equals("Home"))
                    HomeAddress = getIntent().getStringExtra("Location");
                else if(AddressType.equals("Work"))
                    WorkAddress = getIntent().getStringExtra("Location");
            }

            if(getIntent().getStringExtra("FlatHouseNo") != null)
                edtHouseFlatNo.setText(getIntent().getStringExtra("FlatHouseNo"));
            else if (locationObj.has("FlatHouseNo"))
                edtHouseFlatNo.setText(locationObj.getString("FlatHouseNo"));

            if(getIntent().getStringExtra("Landmark") != null)
                edtLandmark.setText(getIntent().getStringExtra("Landmark"));
            else if(locationObj.has("Landmark"))
                edtLandmark.setText(locationObj.getString("Landmark"));

            if(getIntent().getStringExtra("Location") != null)
                edtLocation.setText(getIntent().getStringExtra("Location"));
            else if(locationObj.has("Location"))
                edtLocation.setText(locationObj.getString("Location"));

            if(getIntent().getStringExtra("Latitude") != null)
                CurrentLat = getIntent().getStringExtra("Latitude");
            else if (locationObj.has("Latitude"))
                CurrentLat = locationObj.getString("Latitude");

            if(getIntent().getStringExtra("Longitude") != null)
                CurrentLong = getIntent().getStringExtra("Longitude");
            else if (locationObj.has("Longitude"))
                CurrentLong = locationObj.getString("Longitude");

        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        Glide.with(DeliveryAddressActivity.this).asGif().load(R.drawable.ripple).into(ivMarkerLoader);

        hideKeyboard(DeliveryAddressActivity.this);
        //fetchAddress();
    }

    @Override
    public void onBackPressed() {
        switch (fromWhere) {
            case "Home":
                startActivity(new Intent(DeliveryAddressActivity.this, MainActivity.class)
                        .putExtra("FromSplash", false));
                finish();
                break;
            case "Menu":
                startActivity(new Intent(DeliveryAddressActivity.this, MenuListActivity.class)
                        .putExtra("FromWhere", false));
                finish();
                break;
            case "Cart":
                startActivity(new Intent(DeliveryAddressActivity.this, CheckoutDetailsActivity.class));
                finish();
                break;
            case "ManageAddress":
                super.onBackPressed();
                break;
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //----------------------------------Bottom Tab Menu----------------------------------//
    public void setBottomMenu() {
        bottomMenuArray = new ArrayList<>();
        for (Integer icon : icons) {
            BottomMenuUtils bottomMenuUtils = new BottomMenuUtils();
            bottomMenuUtils.setBottomTab(icon);
            bottomMenuUtils.setSelected(false);
            bottomMenuArray.add(bottomMenuUtils);
        }
        bottomMenuAdapter = new BottomMenuAdapter(DeliveryAddressActivity.this, bottomMenuArray);
        rcLowerMenu.setAdapter(bottomMenuAdapter);
        bottomMenuAdapter.setListener((position, imgView) -> {
            sharedPref.setPos(position);
            if (position == 0) startActivity(new Intent(DeliveryAddressActivity.this, MainActivity.class).putExtra("FromSplash", false));
            else if (position == 1) startActivity(new Intent(DeliveryAddressActivity.this, MenuListActivity.class));
            else if (position == 2) {
                if(sharedPref.getUserId().equals(""))
                    startActivity(new Intent(DeliveryAddressActivity.this, LogInActivity.class));
                else startActivity(new Intent(DeliveryAddressActivity.this, ProfileActivity.class));
            }else if (position == 3) startActivity(new Intent(DeliveryAddressActivity.this, AddToCartActivity.class));

            finish();
        });
    }

    // An AsyncTask class for accessing the GeoCoding Web Service
    public class GeoCoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(DeliveryAddressActivity.this, Locale.getDefault());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if(addresses==null || addresses.size()==0){
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }

            // Clears all the existing markers on the map
            mMap.clear();

            // Adding Markers on Google Map for each matching address
            for(int i = 0; i< Objects.requireNonNull(addresses).size(); i++){

                Address address = addresses.get(i);

                // Creating an instance of GeoPoint, to display in Google Map
                latLng = new LatLng(address.getLatitude(), address.getLongitude());

                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);
                mMap.addMarker(markerOptions);

                String subLocality = addresses.get(0).getSubLocality();
                edtLocation.setText(String.valueOf(addresses.get(0).getAddressLine(0)));
                edtLandmark.setText(subLocality);

                edtHouseFlatNo.setText("");

                // Locate the first location
                if(i==0)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(true);

        mMap.setOnCameraChangeListener(cameraPosition -> {
            if (isFirst) {
                mMap.clear();
                LatLng latLng = mMap.getCameraPosition().target;
                CurrentLat = String.valueOf(latLng.latitude);
                CurrentLong = String.valueOf(latLng.longitude);

                Geocoder geocoder = new Geocoder(DeliveryAddressActivity.this, Locale.getDefault());
                try {
                    List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude,
                            latLng.longitude, 1);
                    if (listAddresses != null && listAddresses.size() > 0) {
                        Log.e("Landmark", listAddresses.get(0).getSubLocality() +"_");
                        Log.e("PostalCode", listAddresses.get(0).getPostalCode()+"_");
                        Log.e("locality", listAddresses.get(0).getLocality()+"_");
                        Log.e("area", listAddresses.get(0).getSubLocality()+"_");
                        Log.e("address", listAddresses.get(0).getAddressLine(0)+"_");
                        Log.e("Lat", latLng.latitude+"_");
                        Log.e("Lng", latLng.longitude+"_");

                        // Here we are finding , whatever we want our marker to show when click
                        //String state = listAddresses.get(0).getAdminArea();
                        //String country = listAddresses.get(0).getCountryName();
                        String subLocality = listAddresses.get(0).getSubLocality();

                        edtLocation.setText(String.valueOf(listAddresses.get(0).getAddressLine(0)));
                        edtHouseFlatNo.setText("");
                        edtLandmark.setText(subLocality);
                        locationCount = 0;
                        if(layoutLoader.getVisibility() == View.VISIBLE)
                            layoutLoader.setVisibility(View.GONE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.e("isFirst", String.valueOf(isFirst));
        });

        mMap.setOnCameraMoveListener(() -> isFirst = true);

        mMap.setOnMapClickListener(latLng -> isFirst = true);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("GoogleApiClient_", String.valueOf(bundle));
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if(mGoogleApiClient.hasConnectedApi(LocationServices.API)) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }else {
                Log.e("GoogleApiClient_", "Not Connected");
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("GoogleApiClient_", String.valueOf(connectionResult.getErrorMessage()));
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("GoogleApiClient_", String.valueOf(i));
    }

    @Override
    public void onLocationChanged(Location location) {
        if(locationCount == 0) {
            locationCount++;
            Log.e("GoogleApiClient", "Connected");
            mMap.clear();
            mLastLocation = location;
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }
            //Showing Current Location Marker on Map
            LatLng latLng;
            try {
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                JSONObject locationObj = cartObj.getJSONObject(getResources().getString(R.string.LocationJson));
                if(getIntent().getStringExtra("AddressType") != null) {
                    AddressType = getIntent().getStringExtra("AddressType");
                }

                if(getIntent().getStringExtra("FlatHouseNo") != null)
                    edtHouseFlatNo.setText(getIntent().getStringExtra("FlatHouseNo"));
                else if (locationObj.has("FlatHouseNo"))
                    edtHouseFlatNo.setText(locationObj.getString("FlatHouseNo"));

                if(getIntent().getStringExtra("Landmark") != null)
                    edtLandmark.setText(getIntent().getStringExtra("Landmark"));
                else if(locationObj.has("Landmark"))
                    edtLandmark.setText(locationObj.getString("Landmark"));

                if(getIntent().getStringExtra("Location") != null) {
                    edtLocation.setText(getIntent().getStringExtra("Location"));
                    if(AddressType.equals("Home"))
                        HomeAddress = getIntent().getStringExtra("Location");
                    else if(AddressType.equals("Work"))
                        WorkAddress = getIntent().getStringExtra("Location");
                }else if(locationObj.has("Location"))
                    edtLocation.setText(locationObj.getString("Location"));

                if(getIntent().getStringExtra("Latitude") != null)
                    CurrentLat = getIntent().getStringExtra("Latitude");
                else if (locationObj.has("Latitude"))
                    CurrentLat = locationObj.getString("Latitude");

                if(getIntent().getStringExtra("Longitude") != null)
                    CurrentLong = getIntent().getStringExtra("Longitude");
                else if (locationObj.has("Longitude"))
                    CurrentLong = locationObj.getString("Longitude");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (CurrentLat.isEmpty() && CurrentLong.isEmpty()) {
                CurrentLat = String.valueOf(location.getLatitude());
                CurrentLong = String.valueOf(location.getLongitude());
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                latLng = new LatLng(Double.parseDouble(CurrentLat), Double.parseDouble(CurrentLong));
            }
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
            mCurrLocationMarker = mMap.addMarker(markerOptions);
            mMap.clear();
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mMap.getMaxZoomLevel()));
            //mMap.animateCamera(CameraUpdateFactory.zoomIn());
            mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.getMaxZoomLevel()), 2000, null);
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            String provider = locationManager.getBestProvider(new Criteria(), true);
            if (ActivityCompat.checkSelfPermission(DeliveryAddressActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(DeliveryAddressActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                permissionChecker = new PermissionChecker(DeliveryAddressActivity.this);
                customToast.showCustomToast(DeliveryAddressActivity.this, "Please Allow Our Location Permission to Access Your Location !! ");
// TODO: Consider calling
// ActivityCompat#requestPermissions
// here to request the missing permissions, and then overriding
// public void onRequestPermissionsResult(int requestCode, String[]permissions,
// int[] grantResults)
// to handle the case where the user grants the permission. See the documentation
// for ActivityCompat#requestPermissions for more details.
                return;
            }
            Geocoder geocoder = new Geocoder(DeliveryAddressActivity.this, Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude,
                        latLng.longitude, 5);
                if (null != listAddresses && listAddresses.size() > 0) {
                    for(int i = 0 ; i < listAddresses.size() ; i++){
                        if(listAddresses.get(i).getSubLocality() != null){
                            // Here we are finding , whatever we want our marker to show when clicked
                            //String state = listAddresses.get(i).getAdminArea();
                            //String country = listAddresses.get(i).getCountryName();
                            String subLocality = listAddresses.get(i).getSubLocality();

                            //edtLocation.setText(String.valueOf(listAddresses.get(0).getAddressLine(0)));
                            //edtLandmark.setText(subLocality);

                            edtLocation.setText(String.valueOf(listAddresses.get(i).getAddressLine(0)));
                            edtLandmark.setText(subLocality);
                            layoutLoader.setVisibility(View.GONE);
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

//this code stops location updates
            if (mGoogleApiClient != null) {
                if(mGoogleApiClient.hasConnectedApi(LocationServices.API) )
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                else {
                    Log.e("GoogleApiClient_", "Not Connected");
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                layoutLoader.setVisibility(View.VISIBLE);
                buildGoogleApiClient();
            }else{
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale( this,Manifest.permission.ACCESS_FINE_LOCATION );
                if (! showRationale) {
                    new AlertDialog.Builder(DeliveryAddressActivity.this)
                            .setMessage("Are You Sure to Not Allow Us This Permissions ? Because We Will Not Be Able To Show You Chef From Your Area Without Permissions.")
                            .setPositiveButton("Allow", (paramDialogInterface, paramInt) -> startActivity(new Intent().setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setData(Uri.fromParts("package", getPackageName(), null)))).show();
                } else {
                    permissionChecker = new PermissionChecker(DeliveryAddressActivity.this);
                }
            }
        }
    }

    private void drawRoute(double lat_a, double lng_a, double lat_b, double lng_b ){

        // Getting URL to the Google Directions API
        LatLng OrderLatLng = new LatLng(lat_a, lng_a);
        LatLng VendLatLng = new LatLng(lat_b, lng_b);
        String url = getDirectionsUrl(OrderLatLng, VendLatLng);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Key
        String key = "key=" + getString(R.string.google_maps_key);

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&mode=walking"+"&"+key;

        // Output format
        String output = "json";

        // Building the url to the web service

        return "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
    }

    /** A class to download data from Google Directions URL */
    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask","DownloadTask : " + data);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject distanceJsonObj = new JSONObject(result);
                JSONArray routeArray = distanceJsonObj.getJSONArray("routes");
                JSONObject routeObj = routeArray.getJSONObject(0);
                JSONArray legsArray = routeObj.getJSONArray("legs");
                JSONObject legsObj = legsArray.getJSONObject(0);
                JSONObject distanceObj = legsObj.getJSONObject("distance");
                CurrentDistance = Float.parseFloat(distanceObj.getString("text").substring(0,distanceObj.getString("text").length()-3));
                Log.e("CurrentDistance", CurrentDistance+"_");

                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                if (CurrentDistance > 6.0) {
                    new AlertDialog.Builder(DeliveryAddressActivity.this)
                        .setMessage("Your Selected Area is not Serviceable with Current Chef !! ")
                        .setPositiveButton("Clear Cart", (paramDialogInterface, paramInt) -> {
                            try {
                                cartObj.put(getResources().getString(R.string.CartJsonObj), new JSONObject());
                                cartObj.put(getResources().getString(R.string.CartJsonArray), new JSONArray());

                                sharedPref.setUserCart(cartObj.toString());
                                bottomMenuAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }).setNegativeButton("Discard Changes", (dialog, which) -> {
                            try {
                                JSONObject userObj = cartObj.getJSONObject(getResources().getString(R.string.LocationJson));
                                if (userObj.has("Location"))
                                    edtLocation.setText(userObj.getString("Location"));
                                if (userObj.has("FlatHouseNo"))
                                    edtHouseFlatNo.setText(userObj.getString("FlatHouseNo"));
                                if (userObj.has("Landmark"))
                                    edtLandmark.setText(userObj.getString("Landmark"));
                                if (userObj.has("Latitude"))
                                    CurrentLat = userObj.getString("Latitude");
                                if (userObj.has("Longitude"))
                                    CurrentLong = userObj.getString("Longitude");

                                latLng = new LatLng(Double.parseDouble(CurrentLat), Double.parseDouble(CurrentLong));
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }).show();
                }else {
                    String Location = edtLocation.getText().toString().trim();
                    String FlatHouseNo = edtHouseFlatNo.getText().toString().trim();
                    String Landmark = edtLandmark.getText().toString().trim();

                    if(Location.isEmpty())
                        edtLocation.setError("Required");
                    else if(FlatHouseNo.isEmpty())
                        edtHouseFlatNo.setError("Required");
                    else if(Landmark.isEmpty())
                        edtLandmark.setError("Required");
                    else{
                        layoutLoader.setVisibility(View.VISIBLE);
                        setLocation(Location, FlatHouseNo, Landmark, CurrentLat, CurrentLong);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("Result", result);
        }
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb  = new StringBuilder();

            String line;
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception on download", e.toString());
        }finally{
            Objects.requireNonNull(iStream).close();
            urlConnection.disconnect();
        }
        return data;
    }

    public void setLocation(String Location, String FlatHouseNo, String Landmark,
                            String Latitude, String Longitude){

        if(!Location.equals("") && !FlatHouseNo.equals("")
                && !Landmark.equals("") && !Latitude.equals("")
                && !Longitude.equals("")) {

            Map<String, String> postData = new HashMap<>();
            postData.put("customer_id", sharedPref.getUserId());
            postData.put("action", Action);
            postData.put("add_id", Action.equals("insert") ? "" : getIntent().getStringExtra("AddressId"));//AddressId
            postData.put("address_type", AddressType);
            postData.put("latitude", Latitude);
            postData.put("longitude", Longitude);
            postData.put("location", Location);
            postData.put("flat", FlatHouseNo);
            postData.put("landmark", Landmark);

            RequestQueue requestQueue = Volley.newRequestQueue(DeliveryAddressActivity.this, sslCertification.getHurlStack());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.AddNewAddress, response -> {
                Log.e("AddNewAddress", "resp:"+response);
                try {
                    JSONObject addressObj = new JSONObject(response);
                    String Status = addressObj.getString("status");
                    String Message = addressObj.getString("message");
                    customToast.showCustomToast(DeliveryAddressActivity.this, Message);
                    if(Status.equals("1")){
                        if(!fromWhere.equals("Profile") && !fromWhere.equals("ManageAddress")){
                            JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                            JSONObject locationObj = cartObj.getJSONObject(getResources().getString(R.string.LocationJson));
                            locationObj.put("Location", Location);
                            locationObj.put("FlatHouseNo", FlatHouseNo);
                            locationObj.put("Landmark", Landmark);
                            locationObj.put("Latitude", Latitude);
                            locationObj.put("Longitude", Longitude);
                            locationObj.put("AddressType", AddressType);
                            locationObj.put("FullAddress", FlatHouseNo+", "+Landmark+","+Location);

                            sharedPref.setUserCart(cartObj.toString());
                        }
                        isFirst = false;
                        customToast.showCustomToast(DeliveryAddressActivity.this, "Location Set Successfully !!");
                        Log.e("Cart", sharedPref.getUserCart());
                        onBackPressed();
                    }
                } catch (JSONException je) {
                    je.printStackTrace();
                    Log.e("AddNewAddress", "je:"+je.toString());
                }
            }, error -> {
                Log.e("AddNewAddress", "ve:"+error.toString());
            }){
                @Override
                protected Map<String, String> getParams() {
                    return postData;
                }
            };
            requestQueue.add(stringRequest);
            Log.e("DeliveryAdd", stringRequest.getUrl());
        }else{
            if(Location.equals(""))
                customToast.showCustomToast(DeliveryAddressActivity.this, "Location is not available");
            else if(FlatHouseNo.equals(""))
                customToast.showCustomToast(DeliveryAddressActivity.this, "Flat/House No Required");
            else if(Landmark.equals(""))
                customToast.showCustomToast(DeliveryAddressActivity.this, "Landmark Required");
            else if(Latitude.equals("") && Longitude.equals(""))
                customToast.showCustomToast(DeliveryAddressActivity.this, "Latitude/Longitude invalid");
        }
        layoutLoader.setVisibility(View.GONE);
    }
}
