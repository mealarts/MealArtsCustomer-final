package com.mealarts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.mealarts.Adapters.BottomMenuAdapter;
import com.mealarts.Adapters.DrawerAdapter;
import com.mealarts.Adapters.SliderAdapter;
import com.mealarts.Adapters.VendorAdapterHome;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.AutoScrollViewPager;
import com.mealarts.Helpers.CheckExtras;
import com.mealarts.Helpers.ChefChainedComparator;
import com.mealarts.Helpers.CustomRatingBar;
import com.mealarts.Helpers.CustomToast;
import com.mealarts.Helpers.PermissionChecker;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.Helpers.Utils.BottomMenuUtils;
import com.mealarts.Helpers.Utils.CategoryUtils;
import com.mealarts.Helpers.Utils.VendorUtils;
import com.mealarts.Helpers.Utils.VoucherUtils;
import com.onesignal.OSPermissionObserver;
import com.onesignal.OSPermissionStateChanges;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity implements
        OSSubscriptionObserver, OSPermissionObserver {

    RecyclerView rcDrawerMenu, rcVendorList;
    RelativeLayout rlPopup1;
    AutoScrollViewPager vpOffers;
    FrameLayout layoutDefault;
    TextView tvUser, tvContact, tvCurrentLocation, tvCurrentAddress, tvCurrentOrderStatus, tvCurrentVendor,
            tvDeliveryBoyStatus, tvAlertMessage, tvAlertMessage1, tvClearCart, tvGoWithCart, tvClearCart1, tvNoChef,
            tvLoadError, tvOTP;
    ImageView ivToggleMenu, ivLoader, ivReviewClose, ivCardCancel, ivOffer;
    LinearLayout layoutLoader, layoutBreakfast, layoutLunch, layoutSnacks, layoutDinner, layoutCurrentOrder,
            layoutRateCurrentOrder, layoutCartAlert, layoutLocationAlert;
    Button btnOrderNow, btnSubmitReview;
    EditText edtOrderReview;
//    CustomRatingBar layoutOrderRating;
    private RatingBar layoutOrderRating;
    public static DrawerLayout drawer;
    DrawerAdapter drawerAdapter;
    CardView cardThanks;

    RecyclerView rcLowerMenu;
    ArrayList<BottomMenuUtils> bottomMenuArray;                                                     // bottom menu array list
    Integer[] icons = {R.drawable.home,
            R.drawable.menu_card,
            R.drawable.account,
            R.drawable.cart};                                                                       // bottom menu icons
    Integer[] selectPosition = {R.drawable.home_select,
            R.drawable.menu_card_select,
            R.drawable.account_select,
            R.drawable.cart_select};                                                                // bottom menu selection colored icon

    public static BottomMenuAdapter bottomMenuAdapter;                                              // bottom menu adapter
    SharedPref sharedPref;                                                                          // shared preference
    SSLCertification sslCertification = new SSLCertification(MainActivity.this);            // SSL(https) Certification
    CheckExtras connection;                                                                         // check internet connection and location enabled
    PermissionChecker permissionChecker;                                                            // check permissions of app
    CustomToast customToast = new CustomToast();                                                    // custom toast design class
    boolean FromSplash = false;                                                                     // check if app is resumed(false) or created(true)
    //float CurrentDistance;

    FusedLocationProviderClient fusedLocationProviderClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    //int locationCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OneSignal.addPermissionObserver(MainActivity.this);                                         //one signal permission observer
        FromSplash = getIntent().getBooleanExtra("FromSplash", false);
        sharedPref = new SharedPref(MainActivity.this);
        connection = new CheckExtras(MainActivity.this);
        permissionChecker = new PermissionChecker(MainActivity.this);
        layoutLoader = findViewById(R.id.layoutLoader);                                             // show layout between request and response.
        ivLoader = findViewById(R.id.ivLoader);                                                     //loader image
        tvLoadError = findViewById(R.id.tvLoadError);                                               // error text to check where app is sticking in between.
        tvOTP = findViewById(R.id.tvOTP);                                                           // OTP view shown to current order layout

        rlPopup1 = findViewById(R.id.rlPopup1);                                                     //popup for current location set direction.
        tvCurrentLocation = findViewById(R.id.tvCurrentLocation);                                   //current location view with upper popup

        tvUser = findViewById(R.id.tvUser);                                                         //user name from preference shown to drawer menu after login
        tvContact = findViewById(R.id.tvContact);                                                   //registered number from preference shown to drawer menu after login

        layoutDefault = findViewById(R.id.layoutDefault);                                           //frame layout to hide upper coupon slider or if coupons not available them show this layout.
        vpOffers = findViewById(R.id.vpOffers);                                                     //view pager to show coupon image list
        ivOffer = findViewById(R.id.ivOffer);                                                       //Offer Image

        layoutBreakfast = findViewById(R.id.layoutBreakfast);                                       //static views for category - user recyclerview to make it dynamic
        layoutLunch = findViewById(R.id.layoutLunch);
        layoutSnacks = findViewById(R.id.layoutSnacks);
        layoutDinner = findViewById(R.id.layoutDinner);                                             //

        tvNoChef = findViewById(R.id.tvNoChef);                                                     //no chef available in area layout
        rcVendorList = findViewById(R.id.rcVendorList);                                             // vendor list recycler layout
        rcVendorList.setHasFixedSize(true);
        rcVendorList.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        rcDrawerMenu = findViewById(R.id.rcDrawerMenu);                                             //recycler layout for drawer menu
        rcDrawerMenu.setHasFixedSize(true);
        rcDrawerMenu.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        drawerAdapter = new DrawerAdapter(MainActivity.this);                               //drawer menu adapter
        rcDrawerMenu.setAdapter(drawerAdapter);

        ivToggleMenu = findViewById(R.id.ivToggleMenu);                                             //toggle drawer menu image
        ivToggleMenu.setOnClickListener(v -> {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);                                            //close drawer menu if open
            } else {
                drawer.openDrawer(GravityCompat.START);                                             //open drawer menu if close
            }
        });

        drawer = findViewById(R.id.drawer_layout);                                                  // drawer layout to set drawer menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

//----------------------------------Current Location----------------------------------//
        tvCurrentAddress = findViewById(R.id.tvCurrentAddress);                                     // current location of user to show on top
        layoutLocationAlert = findViewById(R.id.layoutLocationAlert);                               //location alert box to show if user changed location more than 6 km with items in cart
        tvAlertMessage1 = findViewById(R.id.tvAlertMessage1);                                       //location alert static msg
        tvClearCart1 = findViewById(R.id.tvClearCart1);                                             //clear cart and set location to current location
        tvGoWithCart = findViewById(R.id.tvGoWithCart);                                             //continue with old cart items and location

        try {
            JSONObject cartObj = new JSONObject(sharedPref.getUserCart());                          //Set and Check Cart JSON if have objects or not.
            if(!cartObj.has(getResources().getString(R.string.UserJSON))) {                                                   //User Cart Object to save all value of customer after login and update profile for further use.
                cartObj.put(getResources().getString(R.string.UserJSON), new JSONObject());
            }else if(!sharedPref.getUserId().equals("")){
                tvUser.setText(cartObj.getJSONObject(getResources().getString(R.string.UserJSON)).getString("UserName"));
                tvContact.setText(cartObj.getJSONObject(getResources().getString(R.string.UserJSON)).getString("Mobile"));
            }
            if(!cartObj.has(getResources().getString(R.string.LocationJson))) {
                cartObj.put(getResources().getString(R.string.LocationJson), new JSONObject());                                      //Location Object to save all value of delivery location from home page and delivery location screen in shared pref for further user.
            }

            if(!cartObj.has(getResources().getString(R.string.CartJsonObj))) {
                cartObj.put(getResources().getString(R.string.CartJsonObj), new JSONObject());                                       //Cart Json Object to save vendor detail and other common details of cart depends on quantity of items
            }
            if(!cartObj.has(getResources().getString(R.string.CartJsonArray))) {
                cartObj.put(getResources().getString(R.string.CartJsonArray), new JSONArray());                                      //Cart Json Array to save required details of menu item which is added to cart
            }
            if(!cartObj.has(getResources().getString(R.string.AddOnsJsonArray))) {
                cartObj.put(getResources().getString(R.string.AddOnsJsonArray), new JSONArray());                                      //AddOns Json Array to save required details of AddOns item temperory which will be added to cart
            }
            sharedPref.setUserCart(cartObj.toString());                                             // Save Cart JSON to shared pref .
            Log.e("Cart_m1", sharedPref.getUserCart());
        }catch (JSONException e){
            e.printStackTrace();
        }

        ivOffer.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MenuListActivity.class));
        });

        tvCurrentAddress.setOnClickListener(v -> {                                                  // Redirect to Delivery Address Screen (Above "Location->" Layout)
            if(sharedPref.getUserId().equals("")){
                startActivity(new Intent(MainActivity.this, LogInActivity.class));
            }else {
                Intent intent = new Intent(MainActivity.this, AddressListActivity.class);
                intent.putExtra("fromWhere", "Home");                                  //to check redirection of activity.
                startActivity(intent);
                finish();
            }
        });

        tvGoWithCart.setOnClickListener(v -> {                                                      // Alert comes when address changed, Positive Button to Continue with old address and cart.
            try {
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                if(!cartObj.has(getResources().getString(R.string.LocationJson))) {                                                  // check if Cart JSON has not have Location Object means user did not set address previously.
                    cartObj.put(getResources().getString(R.string.LocationJson), new JSONObject());
                } else {                                                                            // check if Cart JSON has Location Object means user already set address previously.
                    if(cartObj.getJSONObject(getResources().getString(R.string.LocationJson)).has("FullAddress")) {
                        tvCurrentAddress.setText(cartObj.getJSONObject(getResources().getString(R.string.LocationJson)).getString("AddressType"));
                        tvCurrentLocation.setText(cartObj.getJSONObject(getResources().getString(R.string.LocationJson)).getString("FullAddress"));
                    }if(cartObj.getJSONObject(getResources().getString(R.string.LocationJson)).has("Latitude"))
                        CurrentLat = cartObj.getJSONObject(getResources().getString(R.string.LocationJson)).getString("Latitude");
                    if(cartObj.getJSONObject(getResources().getString(R.string.LocationJson)).has("Longitude"))
                        CurrentLong = cartObj.getJSONObject(getResources().getString(R.string.LocationJson)).getString("Longitude");
                }
                layoutLocationAlert.setVisibility(View.GONE);
            }catch (JSONException e){
                e.printStackTrace();
            }
        });

//----------------------------------Check Cart By Time----------------------------------//
        tvClearCart = findViewById(R.id.tvClearCart);                                               //clear cart if item added to cart is out of time.
        tvAlertMessage = findViewById(R.id.tvAlertMessage);                                         //Alert Message to show if category of item added to cart is out of its time.
        layoutCartAlert = findViewById(R.id.layoutCartAlert);                                       //custom Alert layout

        tvClearCart.setOnClickListener(v -> {
            try {
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                cartObj.put(getResources().getString(R.string.CartJsonObj), new JSONObject());                                       //Clear Cart Json Object
                cartObj.put(getResources().getString(R.string.CartJsonArray), new JSONArray());                                      //Clear Cart Array
                cartObj.put(getResources().getString(R.string.AddOnsJsonArray), new JSONArray());                                      //Clear Cart Array
                sharedPref.setUserCart(cartObj.toString());
                layoutCartAlert.setVisibility(View.GONE);
                bottomMenuAdapter.notifyDataSetChanged();                                           //Notify bottom menu adapter to set o to cart item count
                getServerTime();                                                                    //Call Server time service
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

//----------------------------------Bottom Tab Menu----------------------------------//
        rcLowerMenu = findViewById(R.id.rcLowerMenu);
        rcLowerMenu.setHasFixedSize(true);
        rcLowerMenu.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));//set Bottom Menu layout to grid with 4 count.
        sharedPref.setPos(0);                                                                       //set current position of bottom menu.
        setBottomMenu();                                                                            //set bottom menu

        layoutBreakfast.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MenuListActivity.class);
            intent.putExtra("FromWhere", false);
            startActivity(intent);
            finish();
        });

        layoutLunch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MenuListActivity.class);
            intent.putExtra("FromWhere", false);
            startActivity(intent);
            finish();
        });

        layoutSnacks.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MenuListActivity.class);
            intent.putExtra("FromWhere", false);
            startActivity(intent);
            finish();
        });

        layoutDinner.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MenuListActivity.class);
            intent.putExtra("FromWhere", false);
            startActivity(intent);
            finish();
        });

//----------------------------------Order Now Slider Button----------------------------------//
        btnOrderNow = findViewById(R.id.btnOrderNow);
        btnOrderNow.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MenuListActivity.class)));
        layoutDefault.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MenuListActivity.class)));

//----------------------------------Current Single Order----------------------------------//
        btnOrderNow = findViewById(R.id.btnOrderNow);
        ivReviewClose = findViewById(R.id.ivReviewClose);
        layoutCurrentOrder = findViewById(R.id.layoutCurrentOrder);
        layoutRateCurrentOrder = findViewById(R.id.layoutRateCurrentOrder);
        layoutOrderRating = findViewById(R.id.layoutOrderRating);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        edtOrderReview = findViewById(R.id.edtOrderReview);
        tvCurrentOrderStatus = findViewById(R.id.tvCurrentOrderStatus);
        tvDeliveryBoyStatus = findViewById(R.id.tvDeliveryBoyStatus);
        tvCurrentVendor = findViewById(R.id.tvCurrentVendor);

        VoucherList();                                                                              // set voucher list service
        if(!sharedPref.getCurrentOrderId().equals("0")){                                            //check current order id , if it is not 0
            layoutCurrentOrder.setVisibility(View.VISIBLE);                                         //then show current order layout
            getSingleOrder(sharedPref.getCurrentOrderId());                                         //call single order detail service using user id stored in shared pref after login
            Log.d("/*order_status","353 not 0 cid:"+sharedPref.getCurrentOrderId());
        }else {
            layoutCurrentOrder.setVisibility(View.GONE);                                            //if current order id is 0 then hide current order layout
            Log.d("/*order_status","356 0 cid:"+sharedPref.getCurrentOrderId());
        }

        layoutCurrentOrder.setOnClickListener(v -> {
            Intent intent=new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra("CheckoutId",sharedPref.getCurrentOrderId());
            startActivity(intent);
        });

        ivReviewClose.setOnClickListener(v -> {
            layoutCurrentOrder.setVisibility(View.GONE);
            layoutRateCurrentOrder.setVisibility(View.GONE);
        });

        cardThanks = findViewById(R.id.cardThanks);
        ivCardCancel = findViewById(R.id.ivCardCancel);

        if(!sharedPref.getVisitor() && sharedPref.getUserId().equals(""))
            cardThanks.setVisibility(View.VISIBLE);
        else
            cardThanks.setVisibility(View.GONE);

        final Handler handler = new Handler();
        final Runnable r = () -> {
            if(cardThanks.getVisibility() == View.VISIBLE)
                ivCardCancel.performClick();
        };
        handler.postDelayed(r, 2000);

        if(FromSplash)
            rlPopup1.setVisibility(View.VISIBLE);
        final Runnable run = () -> {
            if(rlPopup1.getVisibility() == View.VISIBLE)
                rlPopup1.setVisibility(View.GONE);
        };
        handler.postDelayed(run, 10000);

        rlPopup1.setOnClickListener(v -> {
            if(rlPopup1.getVisibility() == View.VISIBLE)
                rlPopup1.setVisibility(View.GONE);
        });

        ivCardCancel.setOnClickListener(v -> {
            cardThanks.setVisibility(View.GONE);
            sharedPref.setVisitor(true);
        });
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            layoutLoader.setVisibility(View.VISIBLE);
            tvLoadError.setText("getSingleOrder()");
            getSingleOrder(sharedPref.getCurrentOrderId());
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
        if (!stateChanges.getFrom().getSubscribed() &&
                stateChanges.getTo().getSubscribed()) {
            /*new AlertDialog.Builder(this)
                    .setMessage("You've successfully subscribed to push notifications!")
                    .show();*/
            // get player ID
            stateChanges.getTo().getUserId();
        }
    }

//----------------------------------Bottom Tab Menu----------------------------------//
    public void setBottomMenu() {
        bottomMenuArray = new ArrayList<>();                                                        //bottom array list
        for (int i = 0; i < icons.length; i++) {
            BottomMenuUtils bottomMenuUtils = new BottomMenuUtils();                                //Bottom Menu Util getter setter Class to set combine values at single place.
            bottomMenuUtils.setBottomTab(icons[i]);                                                 //set icons as many as required - change grid count as per requirement.
            bottomMenuUtils.setSelected(i == sharedPref.getPos());                                  //set selected position from shared pref - default is 0 for home page
            bottomMenuArray.add(bottomMenuUtils);                                                   //add this utils to array as array object.
        }
        bottomMenuAdapter = new BottomMenuAdapter(MainActivity.this, bottomMenuArray);      //set bottom menu array to custom adapter.
        rcLowerMenu.setAdapter(bottomMenuAdapter);                                                  //set adapter to recycler view with grid layout.
        bottomMenuAdapter.setListener((position, imgView) -> {                                      //Custom Bottom menu item click listener to set selected position, selected position icon and redirect to related activity
            if (position == 1){
                startActivity(new Intent(MainActivity.this, MenuListActivity.class));
                finish();
            }
            else if (position == 3){
                try {
                    JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                    JSONObject locationJson = cartObj.getJSONObject(getResources().getString(R.string.LocationJson));
                    if(!locationJson.has("FlatHouseNo")
                            || (locationJson.has("FlatHouseNo")
                            && locationJson.getString("FlatHouseNo").isEmpty()
                            && cartObj.has(getResources().getString(R.string.CartJsonArray))
                            && cartObj.getJSONArray(getResources().getString(R.string.CartJsonArray)).length() != 0)){
                        Intent intent = new Intent(MainActivity.this, AddressListActivity.class);
                        intent.putExtra("fromWhere", "Home");
                        startActivity(intent);
                        finish();
                        //customToast.showCustomToast(MainActivity.this, "Please Set Your Delivery Address from Above !!");
                    }else {
                        startActivity(new Intent(MainActivity.this, AddToCartActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if (position == 2) {
                if(sharedPref.getUserId().equals(""))
                    startActivity(new Intent(MainActivity.this, LogInActivity.class));
                else{
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    finish();
                }
            }
            for (int i = 0; i < bottomMenuArray.size(); i++) {                                      //loop of bottom menu array items
                if (position != i) {                                                                //selected position is not same as position of i
                    bottomMenuArray.get(i).setSelected(false);                                      //set util of that item selected = false
                    Picasso.get().load(bottomMenuArray.get(i).getBottomTab()).into(imgView);        //set regular icons(gray color)
                }
            }
            sharedPref.setPos(position);                                                            //set selected item position to shared pref
            bottomMenuArray.get(position).setSelected(true);                                        //set selected item position util selected = true
            Picasso.get().load(selectPosition[position]).into(imgView);                             //set selected item position icon(red color)
            bottomMenuAdapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("/*mainAct_restart","in onrestart");
        //sharedPref = new SharedPref(MainActivity.this);
        connection = new CheckExtras(MainActivity.this);                                    //check connection and location when activity restarts.
        permissionChecker = new PermissionChecker(MainActivity.this);

        sharedPref.setPos(0);                                                                       //set selected bottom menu position to 0 for home activity.
        //locationCount = 0;
        setBottomMenu();                                                                            //set bottom menu to set selection of current activity

        //fusedLocationProviderClient = new FusedLocationProviderClient(MainActivity.this);
        Glide.with(MainActivity.this).asGif().load(R.drawable.mealarts_loader).into(ivLoader); //loader image to show between service request and response.
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            layoutLoader.setVisibility(View.VISIBLE);                                               //visible loader before getting last location of user.
            tvLoadError.setText("getLastLocation()");
            //buildGoogleApiClient();
            getLastLocation();                                                                      //get last location using FusedLocationProviderClient.
        }

        VoucherList();                                                                              //voucher list to show to view pager as per user
        if(!sharedPref.getCurrentOrderId().equals("0")){                                            //check current order id , if it is not 0
            layoutCurrentOrder.setVisibility(View.VISIBLE);                                         //then show current order layout
            getSingleOrder(sharedPref.getCurrentOrderId());                                         //call single order detail service using user id stored in shared pref after login
            Log.d("/*mainAct_restart","517 not 0 cid:"+sharedPref.getCurrentOrderId());
        }
        else {
            layoutCurrentOrder.setVisibility(View.GONE);                                          //if current order id is 0 then hide current order layout
            Log.d("/*mainAct_restart","521 0 cid:"+sharedPref.getCurrentOrderId());
        }
        drawerAdapter = new DrawerAdapter(MainActivity.this);                               //set drawer adapter
        rcDrawerMenu.setAdapter(drawerAdapter);                                                     //ser drawer adapter to recycler view
        try {
            JSONObject cartObj = new JSONObject(sharedPref.getUserCart());                          //get cart Json from shared pref
            if(!cartObj.has(getResources().getString(R.string.UserJSON))) {                                                   //check User Json Object whether it is available or not
                cartObj.put(getResources().getString(R.string.UserJSON), new JSONObject());                                   //if not then put Object to Cart Json
            } else if(!sharedPref.getUserId().equals("")){                                          //check if user id is available
                tvUser.setText(cartObj.getJSONObject(getResources().getString(R.string.UserJSON)).getString("UserName")); //to fetch user name and
                tvContact.setText(cartObj.getJSONObject(getResources().getString(R.string.UserJSON)).getString("Mobile")); //contact to set it to drawer
            }
            sharedPref.setUserCart(cartObj.toString());                                             //set Cart Json to shared pref
            Log.e("/*mainAct_restart_Cart", sharedPref.getUserCart());
            /*if((CurrentLat != null && CurrentLong != null) && (!CurrentLat.isEmpty() && !CurrentLong.isEmpty()))
                getVendorList(CurrentLat, CurrentLong);*/

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    boolean doubleBackToExitPressedOnce = false;                                                    //Double click to Exit from application
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);                                     //Define drawer layout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(isTaskRoot()){                                                                     //True if this is the root activity
            if (doubleBackToExitPressedOnce) {                                                      //check user pressed backpress if yes => true or false
                super.onBackPressed();                                                              //Called when the activity has detected the user's press of the back
                return;
            }
            this.doubleBackToExitPressedOnce = true;                                                //set true if pressed once
            customToast.showCustomToast(MainActivity.this, "Please click BACK again to exit");

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);     //set false if user did not pressed back again within 2 sec
        }else {
            super.onBackPressed();                                                                  //backpress if this is not root activity
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    String CurrentLat="", CurrentLong="";
    public void getLastLocation(){
        fusedLocationProviderClient = new FusedLocationProviderClient(MainActivity.this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            //locationCount++;
            //Log.e("_GoogleApiClient", mGoogleApiClient == null ? "true" : "false");
            if (location != null) {
                Log.e("GoogleApiClient", "Connected");
                //getServerTime();
                CurrentLat = String.valueOf(location.getLatitude());                                //get current latitude
                CurrentLong = String.valueOf(location.getLongitude());                              //get current longitude
                Log.e("CurrentLat_CurrentLong", CurrentLat + "_" + CurrentLong);

                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    permissionChecker = new PermissionChecker(MainActivity.this);
                    // TODO: Consider calling
                    // ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    // public void onRequestPermissionsResult(int requestCode, String[]permissions,
                    // int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(),
                            location.getLongitude(), 1);
                    if (listAddresses != null && listAddresses.size() > 0) {
                        String subLocality = listAddresses.get(0).getSubLocality();
                        String pincode = listAddresses.get(0).getPostalCode();
                        String locality = listAddresses.get(0).getLocality();
                        String area = listAddresses.get(0).getSubLocality();
                        Log.e("Address", listAddresses.get(0).getAddressLine(0)+"_");
                        try {
                            JSONObject cartObj = new JSONObject(sharedPref.getUserCart());          //get User Cart Json From Shared Pref
                            JSONObject locationObj = cartObj.getJSONObject(MainActivity.this.getResources().getString(R.string.LocationJson));         //get Location Object from USer Cart Json
                            JSONObject cartJsonObj = cartObj.getJSONObject(MainActivity.this.getResources().getString(R.string.CartJsonObj));          //get Cart Json Object from User Cart JSON

                            if (!cartObj.has(MainActivity.this.getResources().getString(R.string.CartJsonObj))) {
                                cartObj.put(MainActivity.this.getResources().getString(R.string.CartJsonObj), new JSONObject());                       //check if object is available or not
                            } else {
                                getServerTime();                                                    //Call Server Time service to get current timing from server.
                            }

                            if (!cartObj.has(MainActivity.this.getResources().getString(R.string.CartJsonArray))) {
                                cartObj.put(MainActivity.this.getResources().getString(R.string.CartJsonArray), new JSONArray());                      //check Cart Json Array is available or not
                            }

                            if (locationObj.has("Latitude") && locationObj.has("Longitude")) { // check if latlng is already available or not
                                if(cartObj.has(MainActivity.this.getResources().getString(R.string.CartJsonArray))                                     //Check Cart Json Array is available or not
                                        && cartObj.getJSONArray(MainActivity.this.getResources().getString(R.string.CartJsonArray)).length() != 0) {   //check if menu items is available in cart or not so check length of Cart Array Json
                                    getDistance(Double.parseDouble(CurrentLat),                     //find distance between current location and previously set del location
                                            Double.parseDouble(CurrentLong),
                                            Double.parseDouble(cartJsonObj.getString("VendorLat")),
                                            Double.parseDouble(cartJsonObj.getString("VendorLong")),
                                            cartObj, locationObj, listAddresses, 0, subLocality,
                                            pincode, locality, area);
                                }else {
                                    Log.d("/*23jan_checkdist","checkdistance_cart is empty");
                                    checkDistance(0.2f, cartObj, locationObj, listAddresses,
                                            0, subLocality);                                     // if cart Json has no length then check distance between previously set location and current location
                                }
                            } else {                                                                // check if latlng is not available
                                locationObj.put("Latitude", CurrentLat);                      //set new values to Location Object
                                locationObj.put("Longitude", CurrentLong);
                                locationObj.put("Landmark", subLocality);
                                locationObj.put("Location", listAddresses.get(0).getAddressLine(0));
                                tvCurrentAddress.setText(listAddresses.get(0).getAddressLine(0));
                                tvCurrentLocation.setText(listAddresses.get(0).getAddressLine(0));
                                sharedPref.setUserCart(cartObj.toString());
                                Log.e("Cart_m3", sharedPref.getUserCart());
                                if ((CurrentLat != null && CurrentLong != null) &&
                                        (!CurrentLat.isEmpty() && !CurrentLong.isEmpty()))
                                    getVendorList(CurrentLat, CurrentLong);                         //get Vendor List as per latlng
                            }

                            tvClearCart1.setOnClickListener(v -> {                                  //clear cart if location distance is more than 6 km and user don't want to continue with that location
                                CurrentLat = String.valueOf(location.getLatitude());                //set latlng from addresses
                                CurrentLong = String.valueOf(location.getLongitude());
                                try {
                                    JSONObject cartObj1 = new JSONObject(sharedPref.getUserCart()); //get User Cart Json from shared pref
                                    cartObj1.put(getResources().getString(R.string.CartJsonObj), new JSONObject());                  //put new Cart Json Obj
                                    cartObj1.put(getResources().getString(R.string.CartJsonArray), new JSONArray());                 //put new Cart Json Array

                                    JSONObject newLocationObj = new JSONObject();                   //Put new Location Obj with all new values of current location
                                    newLocationObj.put("Landmark", subLocality);
                                    newLocationObj.put("Location", listAddresses.get(0).getAddressLine(0));
                                    newLocationObj.put("Latitude", CurrentLat);
                                    newLocationObj.put("Longitude", CurrentLong);
                                    cartObj1.put(getResources().getString(R.string.LocationJson), newLocationObj);
                                    tvCurrentAddress.setText(listAddresses.get(0).getAddressLine(0)); // current location
                                    tvCurrentLocation.setText(listAddresses.get(0).getAddressLine(0));  // current location

                                    sharedPref.setUserCart(cartObj1.toString());
                                    layoutLocationAlert.setVisibility(View.GONE);                   //hide location alert which shown bcz of distance < 6
                                    bottomMenuAdapter.notifyDataSetChanged();                       //Notify bottom menu adapter

                                    if ((CurrentLat != null && CurrentLong != null) &&
                                            (!CurrentLat.isEmpty() && !CurrentLong.isEmpty()))
                                        getVendorList(CurrentLat, CurrentLong);                     //get vendor list as per latlng

                                    Log.e("Cart_m4", sharedPref.getUserCart());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    Map<String, String> postData;
    private void getDistance(double lat_a, double lng_a, double lat_b, double lng_b,
                             JSONObject cartObj, JSONObject locationObj, List<Address> listAddresses,
                             int i, String subLocality, String pincode, String locality, String area){
        // Getting URL to the Google Directions API
        LatLng OrderLatLng = new LatLng(lat_a, lng_a);
        LatLng VendLatLng = new LatLng(lat_b, lng_b);
        String url = getDirectionsUrl(OrderLatLng, VendLatLng);
        Log.e("DirectionUrl", url);

        DownloadTask downloadTask = new DownloadTask(cartObj, locationObj, listAddresses, i,
                subLocality, pincode, locality, area);

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;                         //current latlng

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;                          //old latlng

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

        JSONObject cartObj, locationObj;
        List<Address> listAddresses;
        int i;
        String subLocality, pincode, locality, area;

        DownloadTask(JSONObject cartObj, JSONObject locationObj, List<Address> listAddresses, int i,
                     String subLocality, String pincode, String locality, String area) {
            this.cartObj = cartObj;
            this.locationObj = locationObj;
            this.listAddresses = listAddresses;
            this.i = i;
            this.subLocality = subLocality;
            this.pincode = pincode;
            this.locality = locality;
            this.area = area;
        }

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
                float CurrentDistance = Float.parseFloat(distanceObj.getString("text")
                        .substring(0,distanceObj.getString("text").length()-3));
                Log.e("CurrentDistance", CurrentDistance+"_");
                Log.d("/*23jan_checkdist","checkdistance_in post execute of download task");
                checkDistance(CurrentDistance, cartObj, locationObj, listAddresses, i, subLocality);                                                   // if cart Json has no length then check distance between previously set location and current location
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkDistance(float CurrentDistance, JSONObject cartObj, JSONObject locationObj,
                               List<Address> listAddresses, int i, String subLocality) {

        Log.d("/*23jan_checkdist","checkdistance");

        try {
            //locationObj = cartObj.getJSONObject(getResources().getString(R.string.LocationJson));
            if (FromSplash && CurrentDistance > 6.0) {                                              //if current distance between 2 locations is more then 6 or not
                Log.e("Cart_m5","from splash n crntDist>6");
                if (!locationObj.has("Location"))                                             //check if location object is available within Location Json Obj or not
                    locationObj.put("Location", listAddresses.get(i).getAddressLine(0)); // if not then put it with location

                if(locationObj.has("FullAddress")){
                    Log.e("Cart_m5","location obj has full address");
                    tvCurrentAddress.setText(locationObj.getString("FullAddress"));           //set new address to current address view
                    tvCurrentLocation.setText(locationObj.getString("FullAddress"));          //set new location to popup of location guide view
                }else {
                    Log.e("Cart_m5","locationobj doesn't has fulladdress");
                    tvCurrentAddress.setText(locationObj.getString("Location"));              //set new address to current address view
                    tvCurrentLocation.setText(locationObj.getString("Location"));             //set new location to popup of location guide view
                }

                layoutLocationAlert.setVisibility(View.VISIBLE);                                    //show alert about new address and vendor is not available within this area and clear cart or continue with old location
                CurrentLat = locationObj.getString("Latitude");                              //set current lat from location json obj
                CurrentLong = locationObj.getString("Longitude");                            //set current long from location json obj
            } else {                                                                                //if current distance between 2 locations is less then 6
                Log.e("Cart_m5","not (from splash and crntDist>6)");
                if (FromSplash && CurrentDistance > 0.1) {                                          //if current distance between 2 locations is less then 0.1
                    Log.e("Cart_m5","fromsplash and cdist>0.1");
                    //if(!locationObj.has("FullAddress"))
                        cartObj.put(getResources().getString(R.string.LocationJson), new JSONObject());                                  //replace old with new location json obj
                    sharedPref.setUserCart(cartObj.toString());                                     //save to shared pref
                    cartObj = new JSONObject(sharedPref.getUserCart());                             //get latest User Cart object from shared pref
                    locationObj = cartObj.getJSONObject(getResources().getString(R.string.LocationJson));                            //get Location Json Obj from User Cart Obj
                }else {
                    Log.e("Cart_m5","not (from splash and cdist>0.1)");
                    CurrentLat = locationObj.getString("Latitude");                          //continue with old latitude and longitude from location obj
                    CurrentLong = locationObj.getString("Longitude");
                }

                if(locationObj.has("FullAddress")){
                    Log.e("Cart_m5","locationobj has fulladd");
                    tvCurrentAddress.setText(locationObj.getString("FullAddress"));           //set new address to current address view
                    tvCurrentLocation.setText(locationObj.getString("FullAddress"));
                } else if (locationObj.has("Location")) {
                    Log.e("Cart_m5","locationobj has location");
                    tvCurrentAddress.setText(locationObj.getString("Location"));              //set new address to current address view
                    tvCurrentLocation.setText(locationObj.getString("Location"));             //set new location to popup of location guide view
                } else {
                    Log.e("Cart_m5","no fulladdress and location");
                    locationObj.put("Location", listAddresses.get(i).getAddressLine(0)); //
                    tvCurrentAddress.setText(listAddresses.get(i).getAddressLine(0));
                    tvCurrentLocation.setText(listAddresses.get(i).getAddressLine(0));
                }

                locationObj.put("Latitude", CurrentLat);
                locationObj.put("Longitude", CurrentLong);

                if (locationObj.has("Landmark"))                                              //check if has object
                    subLocality = locationObj.getString("Landmark");                          //then get the value
                else locationObj.put("Landmark", subLocality);                                //else put the value to object
            }
            sharedPref.setUserCart(cartObj.toString());                                             //save to shared pref
            Log.e("Cart_m5", sharedPref.getUserCart());
            if ((CurrentLat != null && CurrentLong != null) && (!CurrentLat.isEmpty() && !CurrentLong.isEmpty()))
                getVendorList(CurrentLat, CurrentLong);                                             //get vendor list as per latlng
        }catch (JSONException e){
            e.printStackTrace();
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

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                layoutLoader.setVisibility(View.VISIBLE);
                tvLoadError.setText("getLastLocation()_1");
                getLastLocation();                                                                  //get current location if permission allowed by user
            }else{
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                        this,Manifest.permission.ACCESS_FINE_LOCATION );
                if (! showRationale) {                                                              //show rational if user decline permission with "never ask"
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Are You Sure to Not Allow Us This Permissions ? Because We " +
                                    "Will Not Be Able To Show You Chef From Your Area Without Permissions.")
                            .setPositiveButton("Allow", (paramDialogInterface, paramInt) ->
                                    startActivity(new Intent().setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setData(Uri.fromParts("package", getPackageName(),
                                            null)))).show();
                } else {
                    permissionChecker = new PermissionChecker(MainActivity.this);           //check permissions are allowed by user or not
                }
            }
        }
    }

    @Override
    public void onOSPermissionChanged(OSPermissionStateChanges stateChanges) {

    }

    //----------------------------------Vendor List By 5 km Range----------------------------------//
    ArrayList<VendorUtils> VendorArrayList;
    public void getVendorList(String CurrentLat, String CurrentLong){
        postData = new HashMap<>();
        postData.put("latitude", CurrentLat);
        postData.put("longitude", CurrentLong);

        Glide.with(MainActivity.this).asGif().load(R.drawable.mealarts_loader).into(ivLoader); //show loader img
        layoutLoader.setVisibility(View.VISIBLE);                                                   //visible loader between service request and response
        tvLoadError.setText("getVendorList()_InnerLoop");

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.VendorList, response -> {
            Log.e("VendorList", response);
            /*response = response.replace(",] }","]}");
            Log.e("VendorListEdit", response);*/
            layoutLoader.setVisibility(View.GONE);                                                  //hide loader after response
            try {
                JSONObject vendorObj = new JSONObject(response);
                String Status = vendorObj.getString("status");
                if(Status.equals("1")){
                    JSONArray vendorArray = vendorObj.getJSONArray("vendor");
                    if(vendorArray.length()!= 0) {
                        rcVendorList.setVisibility(View.VISIBLE);                                   //show recyclerview layout if vendor list is != 0
                        tvNoChef.setVisibility(View.GONE);                                          //hide no chef layout
                        VendorArrayList = new ArrayList<>();
                        for (int i = 0; i < vendorArray.length(); i++) {
                            JSONObject vendorObjItem = vendorArray.getJSONObject(i);
                            VendorUtils vendorUtils = new VendorUtils();                            //vendor utils getter setter class to set values to single object
                            vendorUtils.setVendorId(vendorObjItem.getString("vendor_id"));
                            vendorUtils.setVendorName(vendorObjItem.getString("vender_name"));
                            vendorUtils.setVendorImg(vendorObjItem.getString("vendor_img"));
                            vendorUtils.setVendorCat(vendorObjItem.getString("category"));
                            vendorUtils.setSpeciality(vendorObjItem.getString("special_food"));
                            vendorUtils.setCuisines(vendorObjItem.getString("cuisines"));
                            vendorUtils.setRating(vendorObjItem.getString("rating"));
                            vendorUtils.setRatingCount(vendorObjItem.getString("rating_count"));
                            vendorUtils.setCity(vendorObjItem.getString("city_name"));
                            vendorUtils.setArea(vendorObjItem.getString("area_name"));
                            vendorUtils.setDistanceKm(vendorObjItem.getString("distance_km"));
                            VendorArrayList.add(vendorUtils);
                        }

                        Collections.sort(VendorArrayList, new ChefChainedComparator(new Comparator<VendorUtils>() { // multiple descending function
                            @Override
                            public int compare(VendorUtils lhs, VendorUtils rhs) {                  //descending order by distance
                                return extractInt(lhs.getDistanceKm()) - extractInt(rhs.getDistanceKm());
                            }

                            int extractInt(String s) {
                                String num = s.replaceAll("\\D", "");
                                // return 0 if no digits found
                                return num.isEmpty() ? 0 : Integer.parseInt(num);
                            }
                        }, new Comparator<VendorUtils>() {
                            @Override
                            public int compare(VendorUtils lhs, VendorUtils rhs) {                  //descending order by rating
                                return extractInt(rhs.getRating()) - extractInt(lhs.getRating());
                            }

                            int extractInt(String s) {
                                String num = s.replaceAll("\\D", "");
                                // return 0 if no digits found
                                return num.equals("0") ? 0 : Math.round(Float.parseFloat(num) * 10);
                            }
                        }));
                        VendorAdapterHome vendorAdapterHome = new VendorAdapterHome(MainActivity.this, VendorArrayList);
                        rcVendorList.setAdapter(vendorAdapterHome);
                    }else {
                        rcVendorList.setVisibility(View.GONE);
                        tvNoChef.setVisibility(View.VISIBLE);
                    }
                }else layoutLoader.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("VendorList", error.toString()+"_");
            Log.e("VendorList", error.getMessage()+"_");
            error.printStackTrace();
        }){
            @Override
            protected Map<String, String> getParams() {
                return postData;
            }
        };
        stringRequest.setRetryPolicy(new
                DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
        Log.e("VendorList", stringRequest.getUrl()+"_"+postData);
    }

    public void getSingleOrder(String CheckoutId){
        Log.d("/*order_status","getsingleorder(): checkoutid:"+ CheckoutId);
        postData = new HashMap<>();
        postData.put("checkout_id", CheckoutId);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.SingleOrderDetails, response -> {
            Log.e("OrderResp","response:"+ response);

            //if response, show the bottom bar
            layoutCurrentOrder.setVisibility(View.VISIBLE);
            try {
                JSONObject orderObj = new JSONObject(response);
                String OrderId = orderObj.getString("checkout_id");
                String CustomerOtp = orderObj.getString("customer_otp");
                String OrderStatus = orderObj.getString("order_status");
                String vendorId = orderObj.getString("vendor_id");
                String vendorName = orderObj.getString("vender_name");
                String reviewStatus=orderObj.getString("review_status");

                tvCurrentVendor.setText(vendorName);
                if (CustomerOtp.equals("0"))
                    tvOTP.setVisibility(View.GONE);
                else tvOTP.setVisibility(View.VISIBLE);
                tvOTP.setText("OTP : " + CustomerOtp);
                if (OrderStatus.contains("Paid Payment")) //Paid Payment, Confirmed, Preparing, Prepared, Packaging, Accepted, Picked up, Delivered.
                    tvCurrentOrderStatus.setText("Your Order will be accepted within 10 minutes. (#" + OrderId + ")");
                else if (OrderStatus.contains("Confirmed"))
                    tvCurrentOrderStatus.setText("Your Order is " + OrderStatus + ". (#" + OrderId + ")");
                else if (OrderStatus.contains("Preparing"))
                    tvCurrentOrderStatus.setText("Chef " + vendorName + " is " + OrderStatus
                            + " Your Order. (#" + OrderId + ")");
                else if (OrderStatus.contains("Prepared"))
                    tvCurrentOrderStatus.setText("Your Order is "+OrderStatus+". (#" +OrderId+ ")");
                else if (OrderStatus.contains("Packaging"))
                    tvCurrentOrderStatus.setText("Your Order is under " + OrderStatus
                            + ". (#" + OrderId + ")");
                else if (OrderStatus.contains("Accepted"))
                    tvCurrentOrderStatus.setText("Your Order is " + OrderStatus
                            + " by Delivery Boy. (#" + OrderId + ")");
                else if (OrderStatus.contains("Picked up"))
                    tvCurrentOrderStatus.setText("Your Order is " + OrderStatus
                            + " by Delivery Boy. (#" + OrderId + ")");
                else if (OrderStatus.contains("Delivered")){
                    sharedPref.setCurrentOrderId("0");                                              //set current order id if order status get delivered
                    tvCurrentOrderStatus.setText("Your Order has been " + OrderStatus
                            + " Successfully. " + "(#" + OrderId + ")");
                }
                String DeliveryBoyStatus = orderObj.getString("dboy_status");
                if(DeliveryBoyStatus.equals("1")) {
                    tvDeliveryBoyStatus.setVisibility(View.VISIBLE);
                    tvDeliveryBoyStatus.setText("Delivery Boy Assigned to You : "+
                            orderObj.getString("dboy_name")+" "+orderObj.getString("dboy_mobile"));
                }
                else
                    tvDeliveryBoyStatus.setVisibility(View.GONE);

                if(OrderStatus.contains("Delivered")) {
                    Log.d("/*order_status","review status "+reviewStatus);
                    layoutCurrentOrder.setVisibility(View.GONE);
                    if(!reviewStatus.equals("submited")){
                        layoutRateCurrentOrder.setVisibility(View.VISIBLE);
                        Log.d("/*order_status","orderstatus: delivered & review not submitted: "+OrderStatus);
                    }
                    else
                        Log.d("/*order_status","orderstatus: delivered & review submitted: "+OrderStatus);
                }
                else if(OrderStatus.equalsIgnoreCase("cancel") || OrderStatus.equalsIgnoreCase("Cancel Payment")){
                    Log.d("/*order_status","orderstatus:"+OrderStatus);
                    //hide the bottom order status bar
                    layoutCurrentOrder.setVisibility(View.GONE);
                }else {
                    Log.d("/*order_status","orderstatus: other "+OrderStatus);
                    layoutCurrentOrder.setVisibility(View.VISIBLE);
                    layoutRateCurrentOrder.setVisibility(View.GONE);
                }

                btnSubmitReview.setOnClickListener(v -> {
                    layoutLoader.setVisibility(View.VISIBLE);                                       //show loader
                    tvLoadError.setText("submitReview()");
                    submitReview(String.valueOf(layoutOrderRating.getRating()),                      //Service call for review
                            edtOrderReview.getText().toString().trim(), OrderId, vendorId);
                });

            } catch (JSONException e) {
                layoutCurrentOrder.setVisibility(View.GONE);
                Log.d("/*OrderResp","je: "+e.toString());
                e.printStackTrace();
            }
            layoutLoader.setVisibility(View.GONE);
        }, error -> {
            layoutCurrentOrder.setVisibility(View.GONE);
            Log.d("/*OrderResp","ve: "+error.toString());
            Log.e("SingleOrder", error.toString()+"_");
            Log.e("SingleOrder", error.getMessage()+"_");

            error.printStackTrace();
        }){
            @Override
            protected Map<String, String> getParams()  {
                return postData;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void submitReview(String Rating, String Review, String OrderId, String VendorId){
        postData = new HashMap<>();
        postData.put("rating", Rating);
        postData.put("review", Review);
        postData.put("checkout_id", OrderId);
        postData.put("customer_id", sharedPref.getUserId());
        postData.put("vendor_id", VendorId);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.GiveReview, response -> {
            Log.e("ReviewResp", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equals("1")){
                    layoutRateCurrentOrder.setVisibility(View.GONE);                                //hide layout after response
                    layoutCurrentOrder.setVisibility(View.GONE);                                    //hide current order layout
                    sharedPref.setCurrentOrderId("0");                                              //set current order id to 0
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            layoutLoader.setVisibility(View.GONE);
        }, error -> {
            error.printStackTrace();
            Log.e("Review", error.toString()+"_");
            Log.e("Review", error.getMessage()+"_");
        }){
            @Override
            protected Map<String, String> getParams() {
                return postData;
            }
        };
        requestQueue.add(stringRequest);
    }

    long timeInMilliseconds = 0;
    public void getServerTime(){
        SharedPref sharedPref = new SharedPref(MainActivity.this);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLServices.ServerTime, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject serverTimeObj = new JSONObject(response);
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    SimpleDateFormat parseDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String ServerTime = serverTimeObj.getString("server_time");
                    Date serverDateTime = parseDateTime.parse(ServerTime);
                    long timeInMilli = serverDateTime.getTime();
                    calendar.setTimeInMillis(timeInMilli);
                    Date serverTime = inputParser.parse(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
                    sharedPref.setServerTime(inputParser.format(serverTime));
                    setCategory();
                    Log.e("ServiceTime_", sharedPref.getServerTime());

                    final Handler handler = new Handler();
                    timeInMilliseconds = serverTime.getTime();
                    final Runnable r = new Runnable() {
                        public void run() {
                            timeInMilliseconds = timeInMilliseconds + 60000;
                            // The change is in this line)
                            calendar.setTimeInMillis(timeInMilliseconds);
                            sharedPref.setServerTime((String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)).length() == 1
                                    ? "0"+calendar.get(Calendar.HOUR_OF_DAY) : calendar.get(Calendar.HOUR_OF_DAY))  //append 0 if hour is in single digit
                                    + ":" + (String.valueOf(calendar.get(Calendar.MINUTE)).length() == 1            //append hour and minute with :
                                    ? "0"+calendar.get(Calendar.MINUTE) : calendar.get(Calendar.MINUTE)));         //append 0 if minute is in single digit
                            setCategory();                                                          //set category as per current server time.
                            handler.postDelayed(this, 60000);
                            Log.e("ServiceTime", sharedPref.getServerTime());

                        }
                    };
                    handler.postDelayed(r, 60000);
                    /*Intent mIntent = new Intent(MainActivity.this, LocalService.class);
                    LocalService.enqueueWork(MainActivity.this, mIntent);*/

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, error ->{
            Log.e("ServiceTime", error.toString()+"_");
            Log.e("ServiceTime", error.getMessage()+"_");
            error.printStackTrace();
        });
        requestQueue.add(stringRequest);
    }

    ArrayList <CategoryUtils> categoryArray;
    public void setCategory(){
        RequestQueue requestQueue = Volley.newRequestQueue(this, sslCertification.getHurlStack());
        StringRequest customRequest = new StringRequest(Request.Method.GET, URLServices.GetCategory, response -> {
            Log.e("CatResp", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                categoryArray = new ArrayList<>();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                JSONArray categoryJsonArray = jsonObject.getJSONArray("category");
                for(int i = 0 ; i < categoryJsonArray.length() ; i++){
                    JSONObject catJsonObj = categoryJsonArray.getJSONObject(i);
                    String CategoryId = catJsonObj.getString("category_id");
                    String EndOrderSlot = catJsonObj.getString("order_slot");
                    String StartOrderSlot = EndOrderSlot.substring(0, EndOrderSlot.indexOf(" -"));
                    EndOrderSlot = EndOrderSlot.substring(EndOrderSlot.lastIndexOf("- ")+1);    //maintain format 00:00 - 00:00 and split as per requirement

                    CategoryUtils categoryUtils = new CategoryUtils();
                    categoryUtils.setCategoryId(CategoryId);
                    categoryUtils.setStartOrderSlot(StartOrderSlot);
                    categoryUtils.setEndOrderSlot(EndOrderSlot);
                    categoryArray.add(categoryUtils);
                }

                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                JSONObject cartJsonObj = cartObj.getJSONObject(getResources().getString(R.string.CartJsonObj));
                if(cartJsonObj.has("CategoryId")) {
                    String DeliveryDate = cartJsonObj.getString("DeliveryDate");
                    String CategoryId = cartJsonObj.getString("CategoryId");
                    try {
                        Date current = inputParser.parse(sharedPref.getServerTime());
                        if (current.after(inputParser.parse(categoryArray.get(0).getEndOrderSlot())) && CategoryId.equals("1")){
                            calendar.add(Calendar.DAY_OF_YEAR, 1);                          // date for tomorrow
                            if( DeliveryDate.equals(dateFormat.format(calendar.getTime()))) {
                                layoutCartAlert.setVisibility(View.VISIBLE);
                                tvAlertMessage.setText("Sorry for Inconvenience !!\nOrder Placed time for today is Over for Breakfast, We have to clear your cart !!");
                                calendar.add(Calendar.DAY_OF_YEAR, -1);
                            }
                        } else if (current.after(inputParser.parse(categoryArray.get(1).getEndOrderSlot())) && CategoryId.equals("2")
                                && DeliveryDate.equals(dateFormat.format(calendar.getTime()))) {
                            layoutCartAlert.setVisibility(View.VISIBLE);
                            tvAlertMessage.setText("Sorry for Inconvenience !!\nOrder Placed time for today is Over for Lunch, We have to clear your cart !!");
                        } else if (current.after(inputParser.parse(categoryArray.get(2).getEndOrderSlot())) && CategoryId.equals("3")
                                && DeliveryDate.equals(dateFormat.format(calendar.getTime()))) {
                            layoutCartAlert.setVisibility(View.VISIBLE);
                            tvAlertMessage.setText("Sorry for Inconvenience !!\nOrder Placed time for today is Over for Snacks, We have to clear your cart !!");
                        } else if (current.after(inputParser.parse(categoryArray.get(3).getEndOrderSlot())) && CategoryId.equals("4")
                                && DeliveryDate.equals(dateFormat.format(calendar.getTime()))) {
                            layoutCartAlert.setVisibility(View.VISIBLE);
                            tvAlertMessage.setText("Sorry for Inconvenience !!\nOrder Placed time for today is Over for Dinner, We have to clear your cart !!");
                        } else {
                            layoutCartAlert.setVisibility(View.GONE);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    sharedPref.setUserCart(cartObj.toString());
                    Log.e("Cart_m6", sharedPref.getUserCart());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error ->{
            Log.e("GetCategory", error.getMessage()+"_");
            Log.e("GetCategory", error.toString()+"_");
            error.printStackTrace();
        });
        requestQueue.add(customRequest);
    }

    ArrayList<VoucherUtils> voucherList;
    public void VoucherList(){
        postData = new HashMap<>();
        postData.put("customer_id", sharedPref.getUserId().isEmpty() ? "0" : sharedPref.getUserId());
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.OfferList, response -> {
            Log.e("VoucherResp", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String Status = jsonObject.getString("status");
                String banner = jsonObject.getString("banner");
                Glide.with(MainActivity.this).load(banner).into(ivOffer);
                if(Status.equals("1")){
                    vpOffers.setVisibility(View.VISIBLE); // make viewpager visible
                    layoutDefault.setVisibility(View.GONE);  //make default banner invisible
                    JSONArray voucherArray = jsonObject.getJSONArray("offerlist");
                    voucherList = new ArrayList<>();
                    for(int i = 0 ; i < voucherArray.length() ; i++){
                        JSONObject voucherObj = voucherArray.getJSONObject(i);
                        VoucherUtils voucherUtils = new VoucherUtils();                             // Voucher Util getter setter class
                        voucherUtils.setVoucherId(voucherObj.getInt("vouchr_id"));
                        voucherUtils.setVoucherCode(voucherObj.getString("voucher_no"));
                        voucherUtils.setDiscountType(voucherObj.getString("discount_type"));
                        voucherUtils.setDiscountValue(voucherObj.getInt("discount_value"));
                        voucherUtils.setVoucherFromDate(voucherObj.getString("from_date"));
                        voucherUtils.setVoucherToDate(voucherObj.getString("to_date"));
                        voucherUtils.setVoucherMinValue(voucherObj.getInt("min_order_amount"));
                        voucherUtils.setDiscountUpTo(voucherObj.getInt("max_discount_amount"));
                        voucherUtils.setVoucherImg(URLServices.PromoOfferImg+voucherObj.getString("image"));
                        voucherList.add(voucherUtils);
                    }
                    Collections.sort(voucherList, (lhs, rhs) -> rhs.getDiscountValue() - lhs.getDiscountValue()); // sort by discount value
                    SliderAdapter sliderAdapter = new SliderAdapter(MainActivity.this, voucherList);
                    vpOffers.setAdapter(sliderAdapter);
                    vpOffers.setAutoScrollDurationFactor(15);                                       // auto scroll slide after 15f
                    vpOffers.startAutoScroll();
                }else {
//                    vpOffers.setVisibility(View.GONE);  //make viewpager invisible
//                    layoutDefault.setVisibility(View.VISIBLE);  //make default banner visible
                    vpOffers.setVisibility(View.VISIBLE); // make viewpager visible
                    layoutDefault.setVisibility(View.GONE);  //make default banner invisible

                    JSONArray voucherArray = jsonObject.getJSONArray("slider_list");
                    voucherList = new ArrayList<>();
                    for(int i = 0 ; i < voucherArray.length() ; i++){
                        JSONObject voucherObj = voucherArray.getJSONObject(i);
                        VoucherUtils voucherUtils = new VoucherUtils();                             // Voucher Util getter setter class
                        voucherUtils.setVoucherId(Integer.parseInt(voucherObj.getString("s_id")));
                        voucherUtils.setVoucherImg("https://www.mealarts.com/admin/assets/uploads/slider/"+voucherObj.getString("s_img"));
                        voucherList.add(voucherUtils);
                    }
//                    Collections.sort(voucherList, (lhs, rhs) -> rhs.getDiscountValue() - lhs.getDiscountValue()); // sort by discount value
                    SliderAdapter sliderAdapter = new SliderAdapter(MainActivity.this, voucherList);
                    vpOffers.setAdapter(sliderAdapter);
                    vpOffers.setAutoScrollDurationFactor(15);                                       // auto scroll slide after 15f
                    vpOffers.startAutoScroll();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("VoucherResp", error.toString()+"_");
            Log.e("VoucherResp", error.getMessage()+"_");
            error.printStackTrace();
        }){
            @Override
            protected Map<String, String> getParams() {
                return postData;
            }
        };
        requestQueue.add(stringRequest);
        Log.e("VoucherResp", stringRequest.getUrl()+"_"+postData);
    }
}
