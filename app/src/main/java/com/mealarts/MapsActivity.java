package com.mealarts;

import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PictureInPictureParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.Rational;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mealarts.Adapters.BottomMenuAdapter;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CheckExtras;
import com.mealarts.Helpers.CustomToast;
import com.mealarts.Helpers.DirectionsJSONParser;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.Helpers.Utils.BottomMenuUtils;
import com.mealarts.Helpers.Utils.MenuUtils;
import com.onesignal.OneSignal;

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
import java.util.Map;
import java.util.Objects;

@SuppressLint("SetTextI18n")
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Polyline mPolyline;
    ArrayList<LatLng> mMarkerPoints;

    ImageView ivBack, ivLoader, ivDBoy, ivRefresh;
    LinearLayout layoutLoader, layoutBeforeDelivery;
    FrameLayout layoutHeader;
    RecyclerView rcLowerMenu;
    LinearLayout layoutLocation, layoutDBoy;
    ImageView ivStatus;
    TextView tvOrderId, tvDeliverySlot, tvOrderStatus, tvTotalItem, tvOrderTotal, tvDBoyName, tvDBoyContact, tvOTP;

    ArrayList<BottomMenuUtils> bottomMenuArray;
    public static BottomMenuAdapter bottomMenuAdapter;
    Integer[] icons = {R.drawable.home, R.drawable.menu_card, R.drawable.account, R.drawable.cart};
    String CheckoutId = "0";
    SharedPref sharedPref;
    CheckExtras connection;
    SSLCertification sslCertification = new SSLCertification(MapsActivity.this);
    LatLng VendorLatLng, OrderLatLng, DeliveryBoyLatLng;
    CustomToast customToast = new CustomToast();
    public static final String NOTIFY_ACTIVITY_ACTION = "notify_activity";

    LocationManager locationManager;
    boolean gps_enabled = false;
    boolean network_enabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        sharedPref = new SharedPref(MapsActivity.this);
        connection = new CheckExtras(MapsActivity.this);
        CheckoutId = getIntent().getStringExtra("CheckoutId");
        sharedPref.setCurrentOrderId(CheckoutId);

        OneSignal.startInit(MapsActivity.this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .init();

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> onBackPressed());
        layoutLoader = findViewById(R.id.layoutLoader);
        layoutHeader = findViewById(R.id.layoutHeader);
        ivLoader = findViewById(R.id.ivLoader);
        Glide.with(MapsActivity.this).asGif().load(R.drawable.mealarts_loader).into(ivLoader);
        layoutLoader.setVisibility(View.VISIBLE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
        mMarkerPoints = new ArrayList<>();

        ivRefresh = findViewById(R.id.ivRefresh);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvDeliverySlot = findViewById(R.id.tvDeliverySlot);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvTotalItem = findViewById(R.id.tvTotalItem);
        tvOrderTotal = findViewById(R.id.tvOrderTotal);
        layoutLocation = findViewById(R.id.layoutLocation);
        layoutDBoy = findViewById(R.id.layoutDBoy);
        ivDBoy = findViewById(R.id.ivDBoy);
        tvDBoyName = findViewById(R.id.tvDBoyName);
        tvDBoyContact = findViewById(R.id.tvDBoyContact);
        tvOTP = findViewById(R.id.tvOTP);

        layoutLocation.setOnClickListener(v -> {
            Intent intent = new Intent(MapsActivity.this, SingleOrderDetailActivity.class);
            intent.putExtra("CheckoutId", CheckoutId);
            startActivity(intent);
        });
//----------------------------------Bottom Tab Menu----------------------------------//
        rcLowerMenu = findViewById(R.id.rcLowerMenu);
        rcLowerMenu.setHasFixedSize(true);
        rcLowerMenu.setLayoutManager(new GridLayoutManager(MapsActivity.this, 4));
        setBottomMenu();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {ex.printStackTrace();}

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) { ex.printStackTrace();}

        if(!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(MapsActivity.this)
                    .setMessage("Your GPS or Location is not enable ! Please enable to fetch location.")
                    .setPositiveButton("Enable", (paramDialogInterface, paramInt) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))).show();
        }
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                if(!tvOrderStatus.getText().equals("delivered")) {
                    layoutLoader.setVisibility(View.VISIBLE);
                    mMap.clear();
                    getSingleOrder(false);
                    handler.postDelayed(this, 60000);
                }
            }
        };
        layoutBeforeDelivery = findViewById(R.id.layoutBeforeDelivery);
        ivStatus = findViewById(R.id.ivStatus);

        layoutLoader.setVisibility(View.VISIBLE);
        getSingleOrder(false);
        handler.postDelayed(r, 60000);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(NOTIFY_ACTIVITY_ACTION));

        ivRefresh.setOnClickListener(v -> {
            if(!tvOrderStatus.getText().equals("delivered")) {
                layoutLoader.setVisibility(View.VISIBLE);
                mMap.clear();
                getSingleOrder(false);
            }
        });
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            CheckoutId = intent.getStringExtra("CheckoutId");
            Log.e("receiver", "Got message: " + CheckoutId);
            layoutLoader.setVisibility(View.VISIBLE);
            getSingleOrder(false);
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
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

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
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
        bottomMenuAdapter = new BottomMenuAdapter(MapsActivity.this, bottomMenuArray);
        rcLowerMenu.setAdapter(bottomMenuAdapter);
        bottomMenuAdapter.setListener((position, imgView) -> {
            sharedPref.setPos(position);
            if (position == 0){
                startActivity(new Intent(MapsActivity.this, MainActivity.class).putExtra("FromSplash", false));
                finish();
            }
            else if (position == 1){
                startActivity(new Intent(MapsActivity.this, MenuListActivity.class));
                finish();
            }
            else if (position == 2) {
                if(sharedPref.getUserId().equals(""))
                    startActivity(new Intent(MapsActivity.this, LogInActivity.class));
                else startActivity(new Intent(MapsActivity.this, ProfileActivity.class));
                finish();
            }else if (position == 3){
                startActivity(new Intent(MapsActivity.this, AddToCartActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(isTaskRoot()){
            startActivity(new Intent(MapsActivity.this,MainActivity.class).putExtra("FromSplash", false));
            // using finish() is optional, use it if you do not want to keep currentActivity in stack
        }else {
            super.onBackPressed();
        }
        finish();
    }

    ArrayList<MenuUtils> menuList;
    HashMap<String, String> postData;
    public void getSingleOrder(Boolean PIP){
        postData = new HashMap<>();
        postData.put("checkout_id", CheckoutId);

        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.SingleOrderDetails, response -> {
            Log.e("OrderRespTrack_response", response);
            try {
                JSONObject orderObj = new JSONObject(response);
                tvOrderId.setText("order #"+orderObj.getString("checkout_id"));
                String CustomerOtp = orderObj.getString("customer_otp");
                if(CustomerOtp.equals("0"))
                    tvOTP.setVisibility(View.GONE);
                else tvOTP.setVisibility(View.VISIBLE);
                tvOTP.setText("OTP : " + CustomerOtp);
                String OrderStatus = orderObj.getString("order_status");
                if(OrderStatus.contains("Paid Payment")) { //Paid Payment, Confirmed, Preparing, Prepared, Packaging, Accepted, Picked up, Delivered.
                    tvOrderStatus.setText("Placed Successfully");
                }else if(OrderStatus.contains("Confirmed") || OrderStatus.contains("Preparing")
                        || OrderStatus.contains("Prepared") || OrderStatus.contains("Packaging")) {
                    tvOrderStatus.setText(OrderStatus);
                }else if(OrderStatus.contains("Accepted")) {
                    tvOrderStatus.setText(OrderStatus + (orderObj.has("dboy_name") ? " by " + orderObj.getString("dboy_name") : ""));
                }else if(OrderStatus.contains("Picked up")) {
                    tvOrderStatus.setText(OrderStatus + (orderObj.has("dboy_name") ? " by " + orderObj.getString("dboy_name") : ""));
                }else if(OrderStatus.contains("Delivered")) {
                    sharedPref.setCurrentOrderId("0");
                    tvOrderStatus.setText("Your Order has been "+OrderStatus +" Successfully.");
                }
                tvOrderTotal.setText("₹ "+ orderObj.getString("grand_total"));
                tvDeliverySlot.setText("Your Order will be delivered by" + orderObj.getString("time_slot").substring(orderObj.getString("time_slot").lastIndexOf(" ")));
                VendorLatLng = new LatLng(Double.parseDouble(orderObj.getString("vendor_lat")),
                        Double.parseDouble(orderObj.getString("vendor_long")));
                OrderLatLng = new LatLng(Double.parseDouble(orderObj.getString("order_lat")),
                        Double.parseDouble(orderObj.getString("order_long")));
                String DeliveryBoyStatus = orderObj.getString("dboy_status");
                Marker DeliveryBoyMarker = null;
                if(DeliveryBoyStatus.equals("1")) {
                    layoutBeforeDelivery.setVisibility(View.GONE);
                    layoutDBoy.setVisibility(View.VISIBLE);
                    if(orderObj.has("dboy_lat") && orderObj.has("dboy_long")) {
                        try{
                            DeliveryBoyLatLng = new LatLng(Double.parseDouble(orderObj.getString("dboy_lat")),
                                    Double.parseDouble(orderObj.getString("dboy_long"))); /*new LatLng(18.552274, 73.881057);*/
                            tvDBoyName.setText(orderObj.getString("dboy_name"));
                            tvDBoyContact.setText(orderObj.getString("dboy_mobile"));
                            Glide.with(getApplicationContext()).load(URLServices.DBoyImg + orderObj.getString("dboy_img"))
                                    .error(R.drawable.user_select).into(ivDBoy);
                            mMarkerPoints.add(DeliveryBoyLatLng);
                            DeliveryBoyMarker = mMap.addMarker(new MarkerOptions()
                                    .position(DeliveryBoyLatLng)
                                    .title("Delivery Boy Location")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.dboy_north))
                                    .flat(true));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }else{
                    layoutBeforeDelivery.setVisibility(View.VISIBLE);
                    layoutDBoy.setVisibility(View.GONE);
                    layoutLoader.setVisibility(View.GONE);

                    Glide.with(MapsActivity.this.getApplicationContext()).asGif().load(R.drawable.gif5boiling).into(ivStatus);
                }

                mMarkerPoints.add(VendorLatLng);
                mMarkerPoints.add(OrderLatLng);

                Marker vendorMarker = mMap.addMarker(new MarkerOptions()
                        .position(VendorLatLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker2))
                        .title("Chef Location"));
                Marker orderMarker = mMap.addMarker(new MarkerOptions()
                        .position(OrderLatLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                        .title("Your Address"));
                Location vendorLocation = new Location("");//provider name is unnecessary
                vendorLocation.setLatitude(Double.parseDouble(orderObj.getString("vendor_lat")));
                vendorLocation.setLongitude(Double.parseDouble(orderObj.getString("vendor_long")));

                Location custLocation = new Location("");//provider name is unnecessary
                custLocation.setLatitude(Double.parseDouble(orderObj.getString("order_lat")));
                custLocation.setLongitude(Double.parseDouble(orderObj.getString("order_long")));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(vendorMarker.getPosition());
                builder.include(orderMarker.getPosition());
                if(DeliveryBoyLatLng != null && DeliveryBoyMarker != null) {
                    Location DBoyLocation = new Location("");//provider name is unnecessary
                    custLocation.setLatitude(Double.parseDouble(orderObj.getString("dboy_lat")));
                    custLocation.setLongitude(Double.parseDouble(orderObj.getString("dboy_long")));

                    builder.include(DeliveryBoyMarker.getPosition());
                    if(custLocation.bearingTo(DBoyLocation) >= 90f)
                        DeliveryBoyMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.dboy_east));
                    else if(custLocation.bearingTo(DBoyLocation) >= 0f)
                        DeliveryBoyMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.dboy_south));
                    else if(custLocation.bearingTo(DBoyLocation) <= -90f)
                        DeliveryBoyMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.dboy_west));
                    else if(custLocation.bearingTo(DBoyLocation) <= 0f)
                        DeliveryBoyMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.dboy_north));

                    Log.e("Bearing", String.valueOf(custLocation.bearingTo(DBoyLocation)));
                }
                //orderMarker.setRotation(custLocation.bearingTo(vendorLocation));
                Log.e("Bearing", String.valueOf(vendorLocation.bearingTo(custLocation)));
                LatLngBounds bounds = builder.build();
                int padding = 200;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                if(PIP){
                    if(DeliveryBoyLatLng != null && DeliveryBoyMarker != null)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DeliveryBoyLatLng, mMap.getMaxZoomLevel()));
                    else mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(VendorLatLng, mMap.getMaxZoomLevel()));
                }else {
                    mMap.moveCamera(cu);
                    mMap.animateCamera(cu);
                }

                if(mMarkerPoints.size() >= 2){
                    drawRoute();
                }

                JSONArray menuArray = orderObj.getJSONArray("Menu");
                menuList = new ArrayList<>();
                for(int i = 0 ; i < menuArray.length() ; i++){
                    JSONObject menuObj = menuArray.getJSONObject(i);
                    MenuUtils menuUtils = new MenuUtils();
                    menuUtils.setProductName(menuObj.getString("prod_name"));
                    menuUtils.setVegType(menuObj.getString("veg_type"));
                    menuUtils.setQty(menuObj.getString("quantity"));
                    menuUtils.setSellingPrice(menuObj.getString("price"));
                    menuList.add(menuUtils);
                }
                tvTotalItem.setText(menuArray.length()+ " Items");
            } catch (JSONException e) {
                Log.e("OrderRespTrack_je", e.toString()+"_");
                e.printStackTrace();
            }
            layoutLoader.setVisibility(View.GONE);
        }, error -> {
            Log.e("OrderRespTrack_verror", error.toString()+"_");
            Log.e("OrderRespTrack_verror", error.getMessage()+"_");
            error.printStackTrace();
        }){
            @Override
            protected Map<String, String> getParams()  {
                return postData;
            }
        };
        requestQueue.add(stringRequest);
        Log.e("OrderRespTrack_url_pdta", stringRequest+"_"+postData);
    }

    private void drawRoute(){

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(DeliveryBoyLatLng != null ? DeliveryBoyLatLng : VendorLatLng, OrderLatLng);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Key
        String key = "key=" + getString(R.string.google_maps_key);

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+key;

        // Output format
        String output = "json";

        // Building the url to the web service

        return "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
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

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Directions in JSON format */
    @SuppressLint("StaticFieldLeak")
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                    double lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                if(mPolyline != null){
                    mPolyline.remove();
                }
                mPolyline = mMap.addPolyline(lineOptions);

            }else
                customToast.showCustomToast(getApplicationContext(),"No route is found");
        }
    }

    @Override
    protected void onUserLeaveHint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Rational aspectRatio = new Rational(
                    300, 300);
            PictureInPictureParams params = new PictureInPictureParams.Builder()
                    .setAspectRatio(aspectRatio)
                    .build();
            enterPictureInPictureMode(params);
        }
    }

    @Override
    public void onPictureInPictureModeChanged (boolean isInPictureInPictureMode, Configuration newConfig) {
        if (isInPictureInPictureMode) {
            getSingleOrder(true);
            layoutHeader.setVisibility(View.GONE);
            layoutLocation.setVisibility(View.GONE);
        } else {
            getSingleOrder(false);
            layoutHeader.setVisibility(View.VISIBLE);
            layoutLocation.setVisibility(View.VISIBLE);
        }
    }
}
