package com.mealarts;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.mealarts.Adapters.AddOnsMenuAdapter;
import com.mealarts.Adapters.BottomMenuAdapter;
import com.mealarts.Adapters.CategoryAdapter;
import com.mealarts.Adapters.DrawerAdapter;
import com.mealarts.Adapters.MenuAdapter;
import com.mealarts.Adapters.VendorAdapter;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CheckExtras;
import com.mealarts.Helpers.ChefChainedComparator;
import com.mealarts.Helpers.CustomToast;
import com.mealarts.Helpers.PermissionChecker;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.Helpers.Utils.BottomMenuUtils;
import com.mealarts.Helpers.Utils.CategoryUtils;
import com.mealarts.Helpers.Utils.MenuUtils;
import com.mealarts.Helpers.Utils.VendorUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressLint("SetTextI18n")
public class MenuListActivity extends AppCompatActivity{

    boolean addonClicked;

    RecyclerView rcDrawerMenu, rcLowerMenu, rcCategory, rcVendor, rcMenu;
    Spinner spMenuTimer;
    ImageView ivLoader, ivToggleMenu, ivInfo, ivCloseInfo;
    LinearLayout layoutLoader, layoutNoItems, layoutInfo, layoutCartAlert, layoutLocationAlert;
    public static LinearLayout layoutCartTotal;
    TextView tvUser, tvContact, tvCategoryTiming, tvCurrentAddress, tvBrkfastInfo, tvLunchInfo, tvSnacksInfo, tvDinnerInfo,
            tvClearCart, tvAlertMessage, tvFlatNo, tvLandmark, tvSetAddress, tvLoadError;
    public static TextView tvAddCartTotal;
    Button btnViewCart;
    public static DrawerLayout drawer;

    //------AddOns-----//
    LinearLayout layoutAddOns, menuCounter;
    ImageView ivCloseAddOn, ivMenuImg, ivVegType, ivGrid, ivList,ivVegSwitch;
    TextView tvOfferPer, tvPrepTime, tvMenuName, tvDescription, tvMenuOrgPrice, tvMenuPrice,
            tvMenuQty, tvMenuTotalPrice, tvContinueAddOns,tvVegSwitch;
    public static TextView tvDecQty, tvIncQty, tvMenuTotal;
    RecyclerView rcAddOns;
    Switch switchVegType;

    ArrayList<BottomMenuUtils> bottomMenuArray;
    Integer[] icons = {R.drawable.home, R.drawable.menu_card, R.drawable.account, R.drawable.cart};
    Integer[] selectPosition = {R.drawable.home_select, R.drawable.menu_card_select,
            R.drawable.account_select, R.drawable.cart_select};

    Boolean isFirst = true, FromWhere = false;
    int CartCounter = 0;
    public static int getMenuPosition = 0;
    HashMap<String, String> postData;
    public static String DeliveryDate;
    String CurrentLat="", CurrentLong="", PackingCharge, DeliveryCharge, DistanceKM, VendorID="", VendorLat,
            VendorLong, BrkfastMsg, LunchMsg, SnacksMsg, DinnerMsg,isGST,gstPercent;
    ArrayList<VendorUtils> VendorArray;
    ArrayList<MenuUtils> MenuArray,tempMenu;
    ArrayList<JSONObject> AddOnsList;

    public static BottomMenuAdapter bottomMenuAdapter;
    ArrayAdapter<String> menuTimerAdapter;
    VendorAdapter vendorAdapter;
    MenuAdapter menuAdapter;
    AddOnsMenuAdapter addOnsMenuAdapter;
    DrawerAdapter drawerAdapter;

    SharedPref sharedPref;
    CheckExtras connection;
    SSLCertification sslCertification = new SSLCertification(MenuListActivity.this);
    JSONObject cartObj;
    PermissionChecker permissionChecker;
    CustomToast customToast = new CustomToast();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);
        FromWhere = getIntent().getBooleanExtra("FromWhere", false);
        if(FromWhere)
            VendorID = getIntent().getStringExtra("VendorId");

        addonClicked=false;

        sharedPref = new SharedPref(MenuListActivity.this);
        connection = new CheckExtras(MenuListActivity.this);
        permissionChecker = new PermissionChecker(MenuListActivity.this);
        layoutLoader = findViewById(R.id.layoutLoader);
        tvLoadError = findViewById(R.id.tvLoadError);
        ivLoader = findViewById(R.id.ivLoader);
        Glide.with(MenuListActivity.this).asGif().load(R.drawable.mealarts_loader).into(ivLoader);
        layoutLoader.setVisibility(View.VISIBLE);
        tvLoadError.setText("MenuList_OnCreate()");

        tvUser = findViewById(R.id.tvUser);
        tvContact = findViewById(R.id.tvContact);

        rcDrawerMenu = findViewById(R.id.rcDrawerMenu);
        rcDrawerMenu.setHasFixedSize(true);
        rcDrawerMenu.setLayoutManager(new LinearLayoutManager(MenuListActivity.this));

        drawerAdapter = new DrawerAdapter(MenuListActivity.this);
        rcDrawerMenu.setAdapter(drawerAdapter);

        ivInfo = findViewById(R.id.ivInfo);
        ivCloseInfo = findViewById(R.id.ivCloseInfo);
        ivToggleMenu = findViewById(R.id.ivToggleMenu);
        ivVegSwitch = findViewById(R.id.ivVegSwitch);
        layoutInfo = findViewById(R.id.layoutInfo);
        tvBrkfastInfo = findViewById(R.id.tvBrkfastInfo);
        tvLunchInfo = findViewById(R.id.tvLunchInfo);
        tvSnacksInfo = findViewById(R.id.tvSnacksInfo);
        tvDinnerInfo = findViewById(R.id.tvDinnerInfo);
        tvCategoryTiming = findViewById(R.id.tvCategoryTiming);
        layoutCartTotal = findViewById(R.id.layoutCartTotal);
        tvAddCartTotal = findViewById(R.id.tvAddCartTotal);
        btnViewCart = findViewById(R.id.btnViewCart);

        //----------------------------AddOns--------------------------------//
        switchVegType = findViewById(R.id.switchVegType);
        layoutAddOns = findViewById(R.id.layoutAddOns);
        menuCounter = findViewById(R.id.menuCounter);
        ivCloseAddOn = findViewById(R.id.ivCloseAddOn);
        ivMenuImg = findViewById(R.id.ivMenuImg);
        ivVegType = findViewById(R.id.ivVegType);
        ivGrid = findViewById(R.id.ivGrid);
        ivList = findViewById(R.id.ivList);
        tvOfferPer = findViewById(R.id.tvOfferPer);
        tvPrepTime = findViewById(R.id.tvPrepTime);
        tvMenuName = findViewById(R.id.tvMenuName);
        tvDescription = findViewById(R.id.tvDescription);
        tvMenuOrgPrice = findViewById(R.id.tvMenuOrgPrice);
        tvMenuOrgPrice.setPaintFlags(tvMenuOrgPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tvMenuPrice = findViewById(R.id.tvMenuPrice);
        tvDecQty = findViewById(R.id.tvDecQty);
        tvMenuQty = findViewById(R.id.tvMenuQty);
        tvIncQty = findViewById(R.id.tvIncQty);
        tvMenuTotalPrice = findViewById(R.id.tvMenuTotalPrice);
        tvContinueAddOns = findViewById(R.id.tvContinueAddOns);
        tvMenuTotal = findViewById(R.id.tvMenuTotal);
        tvVegSwitch = findViewById(R.id.tvVegSwitch);
        rcAddOns = findViewById(R.id.rcAddOns);
        rcAddOns.setHasFixedSize(true);
        rcAddOns.setLayoutManager(new LinearLayoutManager(MenuListActivity.this));

        ColorStateList state=new ColorStateList(new int[][]
                {new int[]{android.R.attr.state_checked},new int[]{}},
                new int[]{Color.parseColor("#00aa00"),Color.parseColor("#E00000")});
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switchVegType.setThumbTintList(state);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           // switchVegType.getThumbDrawable().setTintList(state);
        }

        tempMenu=new ArrayList<>();
        switchVegType.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tempMenu.clear();
            if(isChecked){
                tvVegSwitch.setText("Veg");
                ivVegSwitch.setImageResource(R.drawable.veg);
                for(int i=0;i< MenuArray.size();i++) {
                    //Log.d("/*menuarray", MenuArray.get(i).getVegType());
                    if(MenuArray.get(i).getVegType().trim().equals("veg"))
                        tempMenu.add(MenuArray.get(i));
                }
                menuAdapter = new MenuAdapter(MenuListActivity.this,tempMenu, CategoryID);
                rcMenu.setAdapter(menuAdapter);
            }
            else {
                tvVegSwitch.setText("Non-Veg");
                ivVegSwitch.setImageResource(R.drawable.nonveg);
                for(int i=0;i< MenuArray.size();i++) {
                    //Log.d("/*menuarray", MenuArray.get(i).getVegType());
                    if(MenuArray.get(i).getVegType().trim().equals("nonveg"))
                        tempMenu.add(MenuArray.get(i));
                }
                menuAdapter = new MenuAdapter(MenuListActivity.this,tempMenu, CategoryID);
                rcMenu.setAdapter(menuAdapter);
            }
        });

        ivGrid.setOnClickListener(v -> {
            rcAddOns.setLayoutManager(new GridLayoutManager(MenuListActivity.this, 2));
            addOnsMenuAdapter = new AddOnsMenuAdapter(MenuListActivity.this, AddOnsList, "Grid");
            rcAddOns.setAdapter(addOnsMenuAdapter);
            ivGrid.setBackgroundColor(getResources().getColor(R.color.colorGrayFade));
            ivList.setBackground(getResources().getDrawable(R.drawable.common_button_back));
        });

        ivList.setOnClickListener(v -> {
            rcAddOns.setLayoutManager(new LinearLayoutManager(MenuListActivity.this));
            addOnsMenuAdapter = new AddOnsMenuAdapter(MenuListActivity.this, AddOnsList, "List");
            rcAddOns.setAdapter(addOnsMenuAdapter);
            ivList.setBackgroundColor(getResources().getColor(R.color.colorGrayFade));
            ivGrid.setBackground(getResources().getDrawable(R.drawable.common_button_back));
        });

        ivCloseAddOn.setOnClickListener(v -> {
            try {
                JSONObject newCartObj = new JSONObject(sharedPref.getUserCart());
                newCartObj.put(getResources().getString(R.string.AddOnsJsonArray), new JSONArray());
                sharedPref.setUserCart(newCartObj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Animation slide_down = AnimationUtils.loadAnimation(MenuListActivity.this, R.anim.slide_down_300);
            layoutAddOns.setAnimation(slide_down);
            layoutAddOns.setVisibility(View.GONE);
        });

        ivToggleMenu.setOnClickListener(v -> {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        ivInfo.setOnClickListener(v -> layoutInfo.setVisibility(View.VISIBLE));

        ivCloseInfo.setOnClickListener(v -> layoutInfo.setVisibility(View.GONE));

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        btnViewCart.setOnClickListener(v -> {
            try {
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                JSONObject locationJson = cartObj.getJSONObject(getResources().getString(R.string.LocationJson));
                if(!locationJson.has("FlatHouseNo")
                        || (locationJson.has("FlatHouseNo")
                        && locationJson.getString("FlatHouseNo").isEmpty()
                        && cartObj.has(getResources().getString(R.string.CartJsonArray))
                        && cartObj.getJSONArray(getResources().getString(R.string.CartJsonArray)).length() != 0)){
                    Intent intent = new Intent(MenuListActivity.this, AddressListActivity.class);
                    intent.putExtra("fromWhere", "Menu");
                    startActivity(intent);
                    finish();
                }else {
                    startActivity(new Intent(MenuListActivity.this, AddToCartActivity.class));
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        //----------------------------------Check Cart By Time----------------------------------//
        tvClearCart = findViewById(R.id.tvClearCart);
        tvAlertMessage = findViewById(R.id.tvAlertMessage);
        layoutCartAlert = findViewById(R.id.layoutCartAlert);

        tvClearCart.setOnClickListener(v -> {
            try {
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                cartObj.put(getResources().getString(R.string.CartJsonObj), new JSONObject());
                cartObj.put(getResources().getString(R.string.CartJsonArray), new JSONArray());
                sharedPref.setUserCart(cartObj.toString());
                layoutCartAlert.setVisibility(View.GONE);
                menuAdapter.notifyDataSetChanged();
                bottomMenuAdapter.notifyDataSetChanged();
                tvAddCartTotal.setText("0" +"/-");
                layoutCartTotal.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

//----------------------------------Bottom Tab Menu----------------------------------//
        rcLowerMenu = findViewById(R.id.rcLowerMenu);
        rcLowerMenu.setHasFixedSize(true);
        rcLowerMenu.setLayoutManager(new GridLayoutManager(MenuListActivity.this, 4));

//----------------------------------Current Location----------------------------------//
        Glide.with(MenuListActivity.this).asGif().load(R.drawable.mealarts_loader).into(ivLoader);
        layoutLoader.setVisibility(View.VISIBLE);
        tvLoadError.setText("CurrentLocation");

        layoutLocationAlert = findViewById(R.id.layoutLocationAlert);
        tvFlatNo = findViewById(R.id.tvFlatNo);
        tvLandmark = findViewById(R.id.tvLandmark);
        tvSetAddress = findViewById(R.id.tvSetAddress);

        tvCurrentAddress = findViewById(R.id.tvCurrentAddress);
        tvCurrentAddress.setOnClickListener(v -> {
            if(sharedPref.getUserId().equals("")){
                startActivity(new Intent(MenuListActivity.this, LogInActivity.class));
            }else {
                Intent intent = new Intent(MenuListActivity.this, AddressListActivity.class);
                intent.putExtra("fromWhere", "Menu");                                  //to check redirection of activity.
                startActivity(intent);
                finish();
            }
        });

//----------------------------------Menu Timer Spinner----------------------------------//
        spMenuTimer = findViewById(R.id.spMenuTimer);

//----------------------------------Set Category----------------------------------//
        rcCategory = findViewById(R.id.rcCategory);
        rcCategory.setHasFixedSize(true);
        rcCategory.setLayoutManager(new GridLayoutManager(MenuListActivity.this, 4));

//----------------------------------Set Vendor Menu----------------------------------//
        layoutNoItems = findViewById(R.id.layoutNoItems);
        rcVendor = findViewById(R.id.rcVendor);
        rcVendor.setHasFixedSize(true);
        rcVendor.setLayoutManager(new LinearLayoutManager(MenuListActivity.this, RecyclerView.HORIZONTAL, false));

        rcMenu = findViewById(R.id.rcMenu);
        rcMenu.setHasFixedSize(true);
        rcMenu.setLayoutManager(new LinearLayoutManager(MenuListActivity.this));
        rcMenu.setNestedScrollingEnabled(false);
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if(addonClicked){
            layoutAddOns.setVisibility(View.GONE);
            addonClicked=false;
        }else if(isTaskRoot()){
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            customToast.showCustomToast(this, "Please click BACK again to exit");

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
        }else {
            super.onBackPressed();
        }
    }

//----------------------------------Bottom Tab Menu----------------------------------//
    public void setBottomMenu() {
        bottomMenuArray = new ArrayList<>();
        for (int i = 0; i < icons.length; i++) {
            BottomMenuUtils bottomMenuUtils = new BottomMenuUtils();
            bottomMenuUtils.setBottomTab(icons[i]);
            bottomMenuUtils.setSelected(i == sharedPref.getPos());
            bottomMenuArray.add(bottomMenuUtils);
        }
        bottomMenuAdapter = new BottomMenuAdapter(MenuListActivity.this, bottomMenuArray);
        rcLowerMenu.setAdapter(bottomMenuAdapter);
        //BottomMenuAdapter.MyViewHolder.tvCartCount.setText(CartCounter);
        bottomMenuAdapter.setListener((position, imgView) -> {
            if (position == 0){
                startActivity(new Intent(MenuListActivity.this, MainActivity.class).putExtra("FromSplash", false));
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
                        Intent intent = new Intent(MenuListActivity.this, AddressListActivity.class);
                        intent.putExtra("fromWhere", "Menu");
                        startActivity(intent);
                        finish();
                    }else {
                        startActivity(new Intent(MenuListActivity.this, AddToCartActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if (position == 2) {
                if(sharedPref.getUserId().equals(""))
                    startActivity(new Intent(MenuListActivity.this, LogInActivity.class));
                else{
                    startActivity(new Intent(MenuListActivity.this, ProfileActivity.class));
                    finish();
                }
            }
            for (int i = 0; i < bottomMenuArray.size(); i++) {
                if (position != i) {
                    bottomMenuArray.get(i).setSelected(false);
                    Picasso.get().load(bottomMenuArray.get(i).getBottomTab()).into(imgView);
                }
            }
            sharedPref.setPos(position);
            bottomMenuArray.get(position).setSelected(true);
            Picasso.get().load(selectPosition[position]).into(imgView);
            bottomMenuAdapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPref.setPos(1);
        setBottomMenu();
        drawerAdapter = new DrawerAdapter(MenuListActivity.this);
        rcDrawerMenu.setAdapter(drawerAdapter);
        try {
            JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
            if(!sharedPref.getUserId().equals("")){
                tvUser.setText(cartObj.getJSONObject(getResources().getString(R.string.UserJSON)).getString("UserName"));
                tvContact.setText(cartObj.getJSONObject(getResources().getString(R.string.UserJSON)).getString("Mobile"));
            }
            if(!cartObj.has(getResources().getString(R.string.LocationJson))) {
                cartObj.put(getResources().getString(R.string.LocationJson), new JSONObject());
            }else {
                JSONObject locationJsonObj = cartObj.getJSONObject(getResources().getString(R.string.LocationJson));
                CurrentLat = cartObj.getJSONObject(getResources().getString(R.string.LocationJson)).getString("Latitude");
                CurrentLong = cartObj.getJSONObject(getResources().getString(R.string.LocationJson)).getString("Longitude");
                if (locationJsonObj.has("FullAddress"))
                    tvCurrentAddress.setText(locationJsonObj.getString("FullAddress"));
                else if (locationJsonObj.has("Location"))
                    tvCurrentAddress.setText(locationJsonObj.getString("Location"));
                if (locationJsonObj.has("Latitude"))
                    CurrentLat = locationJsonObj.getString("Latitude");
                if (locationJsonObj.has("Longitude"))
                    CurrentLong = locationJsonObj.getString("Longitude");
            }
            if(!cartObj.has(getResources().getString(R.string.CartJsonObj))) {
                cartObj.put(getResources().getString(R.string.CartJsonObj), new JSONObject());
            }
            if(!cartObj.has(getResources().getString(R.string.CartJsonArray))) {
                cartObj.put(getResources().getString(R.string.CartJsonArray), new JSONArray());
            }else {
                JSONArray cartArray = cartObj.getJSONArray(getResources().getString(R.string.CartJsonArray));
                CartCounter = cartArray.length();
                if(CartCounter > 0 ) {
                    float cartTotal = 0;
                    for (int i = 0; i < cartArray.length(); i++) {
                        JSONObject cartItemObjs = cartArray.getJSONObject(i);
                        cartTotal += Float.parseFloat(cartItemObjs.getString("QtyPrice"));
                    }
                    tvAddCartTotal.setText(cartTotal +"/-");
                    layoutCartTotal.setVisibility(View.VISIBLE);
                }else layoutCartTotal.setVisibility(View.GONE);
            }
            sharedPref.setUserCart(cartObj.toString());
            Log.e("Cart_menulist", sharedPref.getUserCart());
            if(VendorArray != null) {
                vendorAdapter.notifyDataSetChanged();
                menuAdapter.notifyDataSetChanged();
            }
            getServerTime();
            setCategory();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getServerTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm", Locale.getDefault());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                sharedPref.setServerTime((String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)).length() == 1 ? "0"+calendar.get(Calendar.HOUR_OF_DAY) : calendar.get(Calendar.HOUR_OF_DAY)) + ":" + (String.valueOf(calendar.get(Calendar.MINUTE)).length() == 1 ? "0"+calendar.get(Calendar.MINUTE) : calendar.get(Calendar.MINUTE)));
                try {
                    JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                    JSONObject cartJsonObj = cartObj.getJSONObject(getResources().getString(R.string.CartJsonObj));
                    if(cartJsonObj.has("CategoryId")) {
                        String DeliveryDate = cartJsonObj.getString("DeliveryDate");
                        String CategoryId = cartJsonObj.getString("CategoryId");
                        try {
                            Date current = inputParser.parse(sharedPref.getServerTime());
                            if (current.after(inputParser.parse(categoryArray.get(0).getEndOrderSlot())) && CategoryId.equals("1")){
                                calendar.add(Calendar.DAY_OF_YEAR, 1);
                                if( DeliveryDate.equals(dateFormat.format(calendar.getTime()))) {
                                    layoutCartAlert.setVisibility(View.VISIBLE);
                                    tvAlertMessage.setText("Sorry for Inconvenience !!\nOrder Placed time for today is Over for Breakfast, We have to clear your cart !!");
                                    calendar.add(Calendar.DAY_OF_YEAR, -1);
                                }
                            } else if (current.after(inputParser.parse(categoryArray.get(1).getEndOrderSlot())) && CategoryId.equals("2")
                                    && DeliveryDate.equals(dateFormat.format(calendar.getTime()))) {
                                layoutCartAlert.setVisibility(View.VISIBLE);
                                tvAlertMessage.setText("Sorry for Inconvenience !!\nOrder Placed time for today is Over for Lunch, We have to clear your cart !!");
                            } else if (current.after(inputParser.parse(categoryArray.get(2).getEndOrderSlot()))
                                    && CategoryId.equals("3")&& DeliveryDate.equals(dateFormat.format(calendar.getTime()))) {
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                handler.postDelayed(this, 60000);
                Log.e("ServiceTime", sharedPref.getServerTime());

            }
        };
        handler.postDelayed(r, 60000);
    }
    
//----------------------------------Set Category----------------------------------//
    ArrayList<CategoryUtils> categoryArray;
    String CategoryID="", LastOrderSlot="";
    Calendar now = Calendar.getInstance();
    public void setCategory(){
        layoutLoader.setVisibility(View.VISIBLE);
        tvLoadError.setText("SetCategory()_InnerLoop");
        RequestQueue requestQueue = Volley.newRequestQueue(this, sslCertification.getHurlStack());
        StringRequest customRequest = new StringRequest(Request.Method.GET, URLServices.GetCategory, response -> {
            Log.e("CatResp", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                categoryArray = new ArrayList<>();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                JSONArray categoryJsonArray = jsonObject.getJSONArray("category");
                for(int i = 0 ; i < categoryJsonArray.length() ; i++){
                    JSONObject catJsonObj = categoryJsonArray.getJSONObject(i);
                    String CategoryId = catJsonObj.getString("category_id");
                    String CategoryName = catJsonObj.getString("cat_name");
                    String EndOrderSlot = catJsonObj.getString("order_slot");
                    String StartOrderSlot = EndOrderSlot.substring(0, EndOrderSlot.indexOf(" -"));
                    EndOrderSlot = EndOrderSlot.substring(EndOrderSlot.lastIndexOf("- ")+1);
                    JSONArray deliverySlotArray = catJsonObj.getJSONArray("delivery_slots");
                    String DeliverySlot = deliverySlotArray.getJSONObject(0).getString("slots");
                    DeliverySlot = DeliverySlot.substring(0,DeliverySlot.indexOf(" "));
                    /*String DeliverySlot2 = deliverySlotArray.getJSONObject(deliverySlotArray.length()-1).getString("slots");
                    DeliverySlot2 = DeliverySlot2.substring(DeliverySlot2.indexOf(" "));

                    DeliverySlot = DeliverySlot + DeliverySlot2;
                    Log.e("DeliverySlot", DeliverySlot);*/

                    CategoryUtils categoryUtils = new CategoryUtils();
                    categoryUtils.setCategoryText(CategoryName);
                    categoryUtils.setCategoryId(CategoryId);
                    categoryUtils.setStartOrderSlot(StartOrderSlot);
                    categoryUtils.setEndOrderSlot(EndOrderSlot);
                    categoryUtils.setDeliverySlot(DeliverySlot);
                    categoryUtils.setSelected(false);

                    try {
                        String LastSlot = Integer.parseInt(EndOrderSlot.substring(0,EndOrderSlot.indexOf(":")).trim()) > 12
                                ? new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(EndOrderSlot))
                                : new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new SimpleDateFormat("hh:mm", Locale.getDefault()).parse(EndOrderSlot));
                        if(i == 0) {
                            BrkfastMsg = "Delivery starts "+DeliverySlot+", Order Before "+LastSlot+" previous day";
                            tvBrkfastInfo.setText(BrkfastMsg);
                        } else if(i == 1) {
                            LunchMsg = "Delivery starts "+DeliverySlot+", Order Before "+LastSlot;
                            tvLunchInfo.setText(LunchMsg);
                        }else if(i == 2) {
                            SnacksMsg = "Delivery starts "+DeliverySlot+", Order Before "+LastSlot;
                            tvSnacksInfo.setText(SnacksMsg);
                        }else if(i == 3) {
                            DinnerMsg = "Delivery starts "+DeliverySlot+", Order Before "+LastSlot;
                            tvDinnerInfo.setText(DinnerMsg);
                            LastOrderSlot = EndOrderSlot;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    categoryArray.add(categoryUtils);
                }
                CategoryAdapter categoryAdapter = new CategoryAdapter(MenuListActivity.this, categoryArray);
                rcCategory.setAdapter(categoryAdapter);
                ArrayList<String> menuTimerArray = new ArrayList<>();
                menuTimerArray.add("Today's Menu");
                menuTimerArray.add("Tomorrow's Menu");
                menuTimerAdapter = new ArrayAdapter<>(MenuListActivity.this, android.R.layout.simple_list_item_1, menuTimerArray);
                spMenuTimer.setAdapter(menuTimerAdapter);
                SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm", Locale.getDefault());
                try {
                    Date current = inputParser.parse(sharedPref.getServerTime());
                    if (current.after(inputParser.parse(LastOrderSlot)))
                        spMenuTimer.setSelection(1);
                    else spMenuTimer.setSelection(0);

                    menuTimerAdapter.notifyDataSetChanged();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                spMenuTimer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        int hour = now.get(Calendar.HOUR_OF_DAY);
                        int minute = now.get(Calendar.MINUTE);
                        for(int i = 0 ; i < categoryArray.size() ; i++){
                            categoryArray.get(i).setSelected(false);
                        }
                        if(position == 0){
                            if(!isFirst){
                                try {
                                    Date current = inputParser.parse(hour + ":" + minute);
                                    if (current.after(inputParser.parse(LastOrderSlot))) {
                                        if(Integer.parseInt(LastOrderSlot.substring(0,LastOrderSlot.indexOf(":")).trim()) > 12)
                                            LastOrderSlot = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(LastOrderSlot));
                                        else LastOrderSlot = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new SimpleDateFormat("hh:mm", Locale.getDefault()).parse(LastOrderSlot));
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuListActivity.this);
                                        builder.setMessage("Order Placed time for today is Over on "+LastOrderSlot+".\nPlease choose Tomorrow's Menu to place Order for tomorrow.")
                                                .setNegativeButton("okay", (dialog, which) -> {
                                                    dialog.dismiss();
                                                    spMenuTimer.setSelection(1);
                                                    menuTimerAdapter.notifyDataSetChanged();
                                                });
                                        AlertDialog dialog1 = builder.create();
                                        dialog1.show();
                                    }else {
                                        now = Calendar.getInstance();
                                        Date today = now.getTime();
                                        DeliveryDate = dateFormat.format(today);
                                        Log.e("DeliveryDate",DeliveryDate);
                                        try {
                                            if (current.after(inputParser.parse(LastOrderSlot))
                                                    && current.before(inputParser.parse(categoryArray.get(0).getEndOrderSlot()))) {
                                                categoryArray.get(0).setSelected(true);
                                                CategoryID = categoryArray.get(0).getCategoryId();
                                                tvCategoryTiming.setText(categoryArray.get(0).getCategoryText() + " Timing :"+BrkfastMsg);
                                                spMenuTimer.setSelection(1);
                                                menuTimerAdapter.notifyDataSetChanged();
                                            }else if(current.after(inputParser.parse(categoryArray.get(0).getEndOrderSlot()))){
                                                categoryArray.get(1).setSelected(true);
                                                CategoryID = categoryArray.get(1).getCategoryId();
                                                tvCategoryTiming.setText(categoryArray.get(1).getCategoryText() + " Timing : "+LunchMsg);
                                                spMenuTimer.setSelection(1);
                                                menuTimerAdapter.notifyDataSetChanged();
                                            } else {
                                                for(int i = 1 ; i < categoryArray.size() ; i++){
                                                    if(current.after(inputParser.parse(categoryArray.get(i).getStartOrderSlot()))
                                                            && current.before(inputParser.parse(categoryArray.get(i).getEndOrderSlot()))){
                                                        categoryArray.get(i).setSelected(true);
                                                        CategoryID = categoryArray.get(i).getCategoryId();
                                                        if(i == 1)
                                                            tvCategoryTiming.setText(categoryArray.get(1).getCategoryText() + " Timing : "+LunchMsg);
                                                        else if(i == 2)
                                                            tvCategoryTiming.setText(categoryArray.get(2).getCategoryText() + " Timing : "+SnacksMsg);
                                                        else if(i == 3)
                                                            tvCategoryTiming.setText(categoryArray.get(3).getCategoryText() + " Timing : "+DinnerMsg);
                                                    }
                                                }
                                            }
                                            getMenu(CategoryID, CurrentLat, CurrentLong);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                now = Calendar.getInstance();
                                Date today = now.getTime();
                                DeliveryDate = dateFormat.format(today);
                                isFirst = false;

                                try {
                                    Date current = inputParser.parse(hour + ":" + minute);
                                    if (current.after(inputParser.parse(LastOrderSlot))
                                            && current.before(inputParser.parse(categoryArray.get(0).getEndOrderSlot()))) {
                                        categoryArray.get(0).setSelected(true);
                                        CategoryID = categoryArray.get(0).getCategoryId();
                                        tvCategoryTiming.setText(categoryArray.get(0).getCategoryText() + " Timing :"+BrkfastMsg);
                                        spMenuTimer.setSelection(1);
                                        menuTimerAdapter.notifyDataSetChanged();
                                    }else if(current.after(inputParser.parse(categoryArray.get(0).getEndOrderSlot()))){
                                        categoryArray.get(1).setSelected(true);
                                        CategoryID = categoryArray.get(1).getCategoryId();
                                        tvCategoryTiming.setText(categoryArray.get(1).getCategoryText() + " Timing : "+LunchMsg);
                                        spMenuTimer.setSelection(1);
                                        menuTimerAdapter.notifyDataSetChanged();
                                    }else {
                                        for(int i = 1 ; i < categoryArray.size() ; i++){
                                            if(current.after(inputParser.parse(categoryArray.get(i).getStartOrderSlot()))
                                                    && current.before(inputParser.parse(categoryArray.get(i).getEndOrderSlot()))){
                                                categoryArray.get(i).setSelected(true);
                                                CategoryID = categoryArray.get(i).getCategoryId();
                                                if(i == 1)
                                                    tvCategoryTiming.setText(categoryArray.get(1).getCategoryText() + " Timing : "+LunchMsg);
                                                else if(i == 2)
                                                    tvCategoryTiming.setText(categoryArray.get(2).getCategoryText() + " Timing : "+SnacksMsg);
                                                else if(i == 3)
                                                    tvCategoryTiming.setText(categoryArray.get(3).getCategoryText() + " Timing : "+DinnerMsg);
                                            }
                                        }
                                    }
                                    getMenu(CategoryID, CurrentLat, CurrentLong);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (position == 1) {
                            //calendar.roll(Calendar.DATE, true);
                            now = Calendar.getInstance();
                            now.add(Calendar.DAY_OF_YEAR, 1);
                            Date tomorrow = now.getTime();
                            DeliveryDate = dateFormat.format(tomorrow);
                            Log.e("DeliveryDate",DeliveryDate);
                            try {
                                Date current = inputParser.parse(hour + ":" + minute);
                                if(current.after(inputParser.parse(categoryArray.get(0).getEndOrderSlot()))) {
                                    categoryArray.get(1).setSelected(true);
                                    CategoryID = categoryArray.get(1).getCategoryId();
                                    tvCategoryTiming.setText(categoryArray.get(1).getCategoryText() + " Timing : "+LunchMsg);
                                }
                                else{
                                    categoryArray.get(0).setSelected(true);
                                    CategoryID = categoryArray.get(0).getCategoryId();
                                    tvCategoryTiming.setText(categoryArray.get(0).getCategoryText() + " Timing :"+BrkfastMsg);
                                }
                                getMenu(CategoryID, CurrentLat, CurrentLong);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        categoryAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                //select by default category as per time slots of category
                    try {
                        Date current = inputParser.parse(sharedPref.getServerTime());
                        if(spMenuTimer.getSelectedItemPosition() == 0) {
                            if (current.before(inputParser.parse(categoryArray.get(1).getEndOrderSlot()))) {
                                CategoryID = categoryArray.get(1).getCategoryId();
                                tvCategoryTiming.setText(categoryArray.get(1).getCategoryText() + " Timing : " + LunchMsg);
                            } else if (current.before(inputParser.parse(categoryArray.get(2).getEndOrderSlot()))) {
                                CategoryID = categoryArray.get(2).getCategoryId();
                                tvCategoryTiming.setText(categoryArray.get(2).getCategoryText() + " Timing : " + SnacksMsg);
                            } else if (current.before(inputParser.parse(categoryArray.get(3).getEndOrderSlot()))) {
                                CategoryID = categoryArray.get(3).getCategoryId();
                                tvCategoryTiming.setText(categoryArray.get(3).getCategoryText() + " Timing : " + DinnerMsg);
                            } else if (current.before(inputParser.parse(categoryArray.get(0).getEndOrderSlot()))) {
                                CategoryID = categoryArray.get(0).getCategoryId();
                                tvCategoryTiming.setText(categoryArray.get(0).getCategoryText() + " Timing : " + BrkfastMsg);
                            } else if (current.after(inputParser.parse(categoryArray.get(1).getEndOrderSlot()))) {
                                CategoryID = categoryArray.get(1).getCategoryId();
                                tvCategoryTiming.setText(categoryArray.get(1).getCategoryText() + " Timing : " + LunchMsg);
                            }
                        }else {
                            if (current.after(inputParser.parse(categoryArray.get(1).getEndOrderSlot()))) {
                                CategoryID = categoryArray.get(1).getCategoryId();
                                tvCategoryTiming.setText(categoryArray.get(1).getCategoryText() + " Timing : " + LunchMsg);
                            } else {
                                CategoryID = categoryArray.get(0).getCategoryId();
                                tvCategoryTiming.setText(categoryArray.get(0).getCategoryText() + " Timing : " + BrkfastMsg);
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                //getMenu(CategoryID, CurrentLat, CurrentLong);
                categoryAdapter.setListener((position, tvCategory) -> {
                    SimpleDateFormat inputParserSelect = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    Calendar nowSelected = Calendar.getInstance();
                    int hourSelect = nowSelected.get(Calendar.HOUR_OF_DAY);
                    int minuteSelect = nowSelected.get(Calendar.MINUTE);
                    try {
                        Date current = inputParserSelect.parse(hourSelect+":"+minuteSelect);
                        Log.e("CurrentTime", current +".before("+ inputParserSelect.parse(categoryArray.get(position).getEndOrderSlot())+")");

                        if(spMenuTimer.getSelectedItemPosition() == 0) {
                            if (current.before(inputParserSelect.parse(categoryArray.get(position).getEndOrderSlot()))
                                    && position != 0) {
                                for (int i = 0; i < categoryArray.size(); i++) {
                                    if (i != position) {
                                        categoryArray.get(i).setSelected(false);
                                        tvCategory.setBackgroundResource(R.drawable.common_button_back);
                                    }
                                }
                                categoryArray.get(position).setSelected(true);
                                CategoryID = categoryArray.get(position).getCategoryId();
                                if(position == 1)
                                    tvCategoryTiming.setText(categoryArray.get(1).getCategoryText() + " Timing : "+LunchMsg);
                                else if(position == 2)
                                    tvCategoryTiming.setText(categoryArray.get(2).getCategoryText() + " Timing : "+SnacksMsg);
                                else if(position == 3)
                                    tvCategoryTiming.setText(categoryArray.get(3).getCategoryText() + " Timing : "+DinnerMsg);
                                tvCategory.setBackgroundResource(R.drawable.cat_click_back);
                                categoryAdapter.notifyDataSetChanged();
                                getMenu(CategoryID, CurrentLat, CurrentLong);
                            } else {
                                String LastSlot = Integer.parseInt(categoryArray.get(position).getEndOrderSlot().substring(0,categoryArray.get(position).getEndOrderSlot().indexOf(":")).trim()) > 12
                                        ? new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(categoryArray.get(position).getEndOrderSlot()))
                                        : new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new SimpleDateFormat("hh:mm", Locale.getDefault()).parse(categoryArray.get(position).getEndOrderSlot()));
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setMessage("Order Placed time for " + tvCategory.getText().toString().trim()+" is Over by "+LastSlot+ " for today.\nPlease choose Tomorrow's Menu to place Order for tomorrow.")
                                        .setNegativeButton("okay", (dialog, which) -> dialog.dismiss());
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }else {
                            if(position == 0 /*&& current.after(inputParserSelect.parse(LastOrderSlot))*/
                                    && current.after(inputParserSelect.parse(categoryArray.get(position).getEndOrderSlot()))){
                                String LastSlot = Integer.parseInt(categoryArray.get(position).getEndOrderSlot().substring(0,categoryArray.get(position).getEndOrderSlot().indexOf(":")).trim()) > 12
                                        ? new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(categoryArray.get(position).getEndOrderSlot()))
                                        : new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new SimpleDateFormat("hh:mm", Locale.getDefault()).parse(categoryArray.get(position).getEndOrderSlot()));
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setMessage("Order Placed time for " + tvCategory.getText().toString().trim()+" is Over by "+LastSlot+ " for today.\nPlease choose Tomorrow's Menu to place Order for tomorrow.")
                                        .setNegativeButton("okay", (dialog, which) -> dialog.dismiss());
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                            else /*if (current.before(inputParserSelect.parse(categoryArray.get(position).getEndOrderSlot()))) */{
                                for (int i = 0; i < categoryArray.size(); i++) {
                                    if (i != position) {
                                        categoryArray.get(i).setSelected(false);
                                        tvCategory.setBackgroundResource(R.drawable.common_button_back);
                                    }
                                }
                                categoryArray.get(position).setSelected(true);
                                CategoryID = categoryArray.get(position).getCategoryId();
                                if(position == 0)
                                    tvCategoryTiming.setText(categoryArray.get(0).getCategoryText() + " Timing :"+BrkfastMsg);
                                else if(position == 1)
                                    tvCategoryTiming.setText(categoryArray.get(1).getCategoryText() + " Timing : "+LunchMsg);
                                else if(position == 2)
                                    tvCategoryTiming.setText(categoryArray.get(2).getCategoryText() + " Timing : "+SnacksMsg);
                                else if(position == 3)
                                    tvCategoryTiming.setText(categoryArray.get(3).getCategoryText() + " Timing : "+DinnerMsg);
                                tvCategory.setBackgroundResource(R.drawable.cat_click_back);
                                categoryAdapter.notifyDataSetChanged();
                                getMenu(CategoryID, CurrentLat, CurrentLong);
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
            layoutLoader.setVisibility(View.GONE);
        }, error -> {
            Log.e("GetCategory", error.toString()+"_");
            Log.e("GetCategory", error.getMessage()+"_");
            error.printStackTrace();
        });
        requestQueue.add(customRequest);
    }

//----------------------------------Set Vendor Menu----------------------------------//
    public void getMenu(String CategoryId, String CurrentLat, String CurrentLong){
        postData = new HashMap<>();
        postData.put("category_id", CategoryId);
        postData.put("latitude", CurrentLat);
        postData.put("longitude", CurrentLong);
        Log.d("/*menulist","catid lat lon "+CategoryId+", "+CurrentLat+", "+CurrentLong);

        /*postData.put("latitude", "18.5204");
        postData.put("longitude", "73.8567");*/
        layoutLoader.setVisibility(View.VISIBLE);
        tvLoadError.setText("GetMenu()_InnerLoop");
        RequestQueue requestQueue = Volley.newRequestQueue(this, sslCertification.getHurlStack());
        StringRequest customRequest = new StringRequest(Request.Method.POST, URLServices.GetMenu, response -> {
            Log.e("/*MenuResponse","response:"+response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.has("vendor")) {
                    JSONArray vendorJsonArray = jsonObject.getJSONArray("vendor");
                    if (vendorJsonArray.length() != 0) {
                        rcVendor.setVisibility(View.VISIBLE);
                        rcMenu.setVisibility(View.VISIBLE);
                        layoutNoItems.setVisibility(View.GONE);
                        VendorArray = new ArrayList<>();
                        for (int i = 0; i < vendorJsonArray.length(); i++) {
                            JSONObject vendorJsonObj = vendorJsonArray.getJSONObject(i);
                            String VendorId = vendorJsonObj.getString("vendor_id");
                            String VendorName = vendorJsonObj.getString("vender_name");
                            String VendorImg = vendorJsonObj.getString("vendor_img");
                            //PackingCharge = vendorJsonObj.getString("pack_charge");
                            DeliveryCharge = vendorJsonObj.getString("delivery_charges");
                            DistanceKM = vendorJsonObj.getString("distance_km");
                            String Rate = vendorJsonObj.getString("rating");
                            VendorLat = vendorJsonObj.getString("lattitude");
                            VendorLong = vendorJsonObj.getString("longitude");

                            tvLoadError.setText("Vendor_ForLoop");

                            JSONArray menuJsonArray = vendorJsonObj.getJSONArray("menu");
                            MenuArray = new ArrayList<>();
                            for (int j = 0; j < menuJsonArray.length(); j++) {
                                JSONObject menuJsonObj = menuJsonArray.getJSONObject(j);
                                String MenuID = menuJsonObj.getString("menu_id");
                                String ProductId = menuJsonObj.getString("product_id");
                                String ProductName = menuJsonObj.getString("prod_name");
                                String OriginalPrice = menuJsonObj.getString("original_price");
                                int OfferPer = menuJsonObj.getString("offer_percent").isEmpty() ? 0 :
                                        Integer.parseInt(menuJsonObj.getString("offer_percent"));
                                String SellingPrice = menuJsonObj.getString("selling_price");
                                String ProductImg = menuJsonObj.getString("menu_img").replace(" ","_");
                                String ProductImgDefault = menuJsonObj.getString("prod_img").replace(" ","_");
                                String Unit = menuJsonObj.getString("unit_name");
                                String MenuDesc = menuJsonObj.getString("menu_description");
                                String VegType = menuJsonObj.getString("veg_type");
                                String ProdIngredients = menuJsonObj.getString("prod_ingredients");
                                String PreparationTime = menuJsonObj.getString("preparation_time");
                                String MenuOnOff = menuJsonObj.getString("menu_onoff");
                                //added 4nov19 hgd
                                isGST=menuJsonObj.getString("is_gst");
                                gstPercent=menuJsonObj.getString("gst_percent");
                                //added 15nov19 hgd
                                PackingCharge=menuJsonObj.getString("packaging_charge");
                                Log.d("/*cart_menu",PackingCharge);

                                JSONArray AddOnsArray = menuJsonObj.getJSONArray("addon");
                                AddOnsList = new ArrayList<>();
                                if(AddOnsArray.length() != 0) {
                                    for (int a = 0; a < AddOnsArray.length(); a++) {
                                        JSONObject AddOnsObj = AddOnsArray.getJSONObject(a);
                                        float GSTPer = Float.parseFloat(AddOnsObj.getString("addon_gst_perc").equals("") ? "0"
                                                : AddOnsObj.getString("addon_gst_perc"));
                                        float GSTPrice = Float.parseFloat(String.valueOf((GSTPer * AddOnsObj.getDouble("sell_price"))/100));
                                        AddOnsObj.put("addon_pack_charge", AddOnsObj.getString("addon_pack_charge").equals("") ? "0"
                                                : AddOnsObj.getString("addon_pack_charge"));
                                        AddOnsObj.put("GSTPrice", GSTPrice);
                                        AddOnsObj.put("MenuId", MenuID);
                                        AddOnsList.add(AddOnsObj);
                                    }
                                }addOnsMenuAdapter = new AddOnsMenuAdapter(MenuListActivity.this, AddOnsList, "List");
                                rcAddOns.setAdapter(addOnsMenuAdapter);
                                addOnsAdapterListener(addOnsMenuAdapter);

                                MenuUtils menuUtils = new MenuUtils();
                                menuUtils.setVendorId(VendorId);
                                menuUtils.setVendorName(VendorName);
                                menuUtils.setMenuId(MenuID);
                                menuUtils.setProductId(ProductId);
                                menuUtils.setProductName(ProductName);
                                menuUtils.setOriginalPrice(OriginalPrice);
                                menuUtils.setOfferPer(OfferPer);
                                menuUtils.setSellingPrice(SellingPrice);
                                menuUtils.setProductImg(ProductImg);
                                menuUtils.setProductImgDefault(ProductImgDefault);
                                menuUtils.setUnit(Unit);
                                menuUtils.setMenuDesc(MenuDesc);
                                menuUtils.setVegType(VegType);
                                menuUtils.setProdIngredients(ProdIngredients);
                                menuUtils.setMenuPrepTime(PreparationTime);
                                //added 4nov19 hgd
                                menuUtils.setIsGST(isGST);
                                menuUtils.setGST_Perc(gstPercent);
                                //added 15nov19 hgd
                                menuUtils.setPack_charge(PackingCharge);
                                menuUtils.setAddOnsList(AddOnsList);
                                menuUtils.setAddOns(AddOnsList.size()!=0);
                                if(MenuOnOff.equals("on"))
                                    MenuArray.add(menuUtils);
                            }
                            Collections.sort(MenuArray, (lhs, rhs) -> lhs.getOfferPer() - rhs.getOfferPer());

                            VendorUtils vendorUtils = new VendorUtils();
                            vendorUtils.setVendorId(VendorId);
                            vendorUtils.setVendorName(VendorName);
                            vendorUtils.setVendorImg(VendorImg);
                            //vendorUtils.setPackingCharge(PackingCharge);
                            //vendorUtils.setSelected(i==0);//set selected vendor position
                            vendorUtils.setSelected(false);
                            vendorUtils.setVendorLat(VendorLat);
                            vendorUtils.setVendorLong(VendorLong);
                            vendorUtils.setDistanceKm(DistanceKM);
                            vendorUtils.setRating(Rate);
                            vendorUtils.setDeliveryCharge(DeliveryCharge);
                            vendorUtils.setMenuArray(MenuArray);
                            //added 4nov19 hgd
                            vendorUtils.setIsGST(isGST);
                            vendorUtils.setGST_Perc(gstPercent);
                            //added 15nov19 hgd
                            vendorUtils.setPack_charge(PackingCharge);
                            VendorArray.add(vendorUtils);
                        }
                        Collections.sort(VendorArray, new ChefChainedComparator(new Comparator<VendorUtils>() {
                            @Override
                            public int compare(VendorUtils lhs, VendorUtils rhs) {
                                tvLoadError.setText("Ascending_Vendor_Dist");
                                return extractInt(lhs.getDistanceKm()) - extractInt(rhs.getDistanceKm());
                            }

                            int extractInt(String s) {
                                String num = s.replaceAll("\\D", "");
                                // return 0 if no digits found
                                return num.isEmpty() ? 0 : Integer.parseInt(num);
                            }
                        }/*, new Comparator<VendorUtils>() {
                            @Override
                            public int compare(VendorUtils lhs, VendorUtils rhs) {
                                return extractInt(lhs.getDistanceKm()) - extractInt(rhs.getDistanceKm());
                            }

                            int extractInt(String s) {
                                String num = s.replaceAll("\\D", "");
                                // return 0 if no digits found
                                return num.isEmpty() ? 0 : Integer.parseInt(num);
                            }
                        }*/, new Comparator<VendorUtils>() {
                            @Override
                            public int compare(VendorUtils lhs, VendorUtils rhs) {
                                tvLoadError.setText("Ascending_Vendor_Rate");
                                return extractInt(rhs.getRating()) - extractInt(lhs.getRating());
                            }

                            int extractInt(String s) {
                                String num = s.replaceAll("\\D", "");
                                // return 0 if no digits found
                                return num.equals("0") ? 0 : Math.round(Float.parseFloat(num)*10);
                            }
                        }));
                        VendorArray.get(0).setSelected(true);
                        vendorAdapter = new VendorAdapter(MenuListActivity.this, VendorArray);
                        rcVendor.setAdapter(vendorAdapter);

                        JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                        JSONArray cartArray = null;
                        JSONObject cartJsonObj = null;
                        if(cartObj.has(getResources().getString(R.string.CartJsonObj)))
                            cartJsonObj = cartObj.getJSONObject(getResources().getString(R.string.CartJsonObj));
                        if(cartObj.has(getResources().getString(R.string.CartJsonArray)))
                            cartArray = cartObj.getJSONArray(getResources().getString(R.string.CartJsonArray));
                        tvLoadError.setText("SetVendor_Selected");
                        if(!VendorID.equals("")) { //redirected here from home by selecting vendor
                            for(int i = 0 ; i < VendorArray.size() ; i++){
                                if(VendorArray.get(i).getVendorId().equals(VendorID)) {
                                    VendorArray.get(i).setSelected(true);
                                    //PackingCharge = VendorArray.get(i).getPackingCharge();
                                    DeliveryCharge = VendorArray.get(i).getDeliveryCharge();
                                    DistanceKM = VendorArray.get(i).getDistanceKm();
                                    //rcVendor.smoothScrollToPosition(i);
                                    rcVendor.scrollToPosition(i);
                                    vendorAdapter.notifyDataSetChanged();
                                    MenuArray = VendorArray.get(i).getMenuArray();// get menu of selected vendor
                                    Log.d("/*menuarray","menuarray updated(redirected here from home by selecting vendor)");
                                    if(VendorArray.get(i).getMenuArray().size() == 0) {
                                        layoutNoItems.setVisibility(View.VISIBLE);
                                    }else {
                                        layoutNoItems.setVisibility(View.GONE);
                                    }

                                    //set switch for veg and filter veg menu
                                    switchVegType.setChecked(true);
                                    tempMenu.clear();
                                    tvVegSwitch.setText("Veg");
                                    ivVegSwitch.setImageResource(R.drawable.veg);
                                    for(int j=0;j< MenuArray.size();j++) {
                                      //  Log.d("/*menuarray", MenuArray.get(j).getVegType());
                                        if(MenuArray.get(j).getVegType().trim().equals("veg"))
                                            tempMenu.add(MenuArray.get(j));
                                    }
                                    menuAdapter = new MenuAdapter(MenuListActivity.this,tempMenu, CategoryID);
                                    rcMenu.setAdapter(menuAdapter);

                                    //original
//                                    menuAdapter = new MenuAdapter(MenuListActivity.this,VendorArray.get(i).getMenuArray(), CategoryId);
//                                    rcMenu.setAdapter(menuAdapter);
                                }else VendorArray.get(i).setSelected(false);
                            }
                        }else if(cartArray != null && cartArray.length() != 0 && cartJsonObj != null){
                            for(int i = 0 ; i < VendorArray.size() ; i++){
                                if(VendorArray.get(i).getVendorId().equals(cartJsonObj.getString("VendorId"))) {
                                    VendorArray.get(i).setSelected(true);
                                    VendorID = VendorArray.get(i).getVendorId();
                                    VendorLat = VendorArray.get(i).getVendorLat();
                                    VendorLong = VendorArray.get(i).getVendorLong();
                                    //PackingCharge = VendorArray.get(i).getPackingCharge();
                                    DeliveryCharge = VendorArray.get(i).getDeliveryCharge();
                                    DistanceKM = VendorArray.get(i).getDistanceKm();
                                    //rcVendor.smoothScrollToPosition(i);
                                    rcVendor.scrollToPosition(i);
                                    vendorAdapter.notifyDataSetChanged();
                                    MenuArray = VendorArray.get(i).getMenuArray();// get menu of selected vendor
                                    Log.d("/*menuarray","menuarray updated(get menu of selected vendor)");
                                    if(VendorArray.get(i).getMenuArray().size() == 0) {
                                        layoutNoItems.setVisibility(View.VISIBLE);
                                    }else {
                                        layoutNoItems.setVisibility(View.GONE);
                                    }

                                    //set switch for veg and filter veg menu
                                    switchVegType.setChecked(true);
                                    tempMenu.clear();
                                    tvVegSwitch.setText("Veg");
                                    ivVegSwitch.setImageResource(R.drawable.veg);
                                    for(int j=0;j< MenuArray.size();j++) {
                                        //  Log.d("/*menuarray", MenuArray.get(j).getVegType());
                                        if(MenuArray.get(j).getVegType().trim().equals("veg"))
                                            tempMenu.add(MenuArray.get(j));
                                    }
                                    menuAdapter = new MenuAdapter(MenuListActivity.this,tempMenu, CategoryID);
                                    rcMenu.setAdapter(menuAdapter);

//                                    menuAdapter = new MenuAdapter(MenuListActivity.this,VendorArray.get(i).getMenuArray(), CategoryId);
//                                    rcMenu.setAdapter(menuAdapter);
                                }else{
                                    VendorArray.get(i).setSelected(false);
                                }
                            }
                        } else {// redirected here from drawer or bottom menu
                            VendorID = VendorArray.get(0).getVendorId();
                            VendorLat = VendorArray.get(0).getVendorLat();
                            VendorLong = VendorArray.get(0).getVendorLong();
                            //PackingCharge = VendorArray.get(0).getPackingCharge();
                            DeliveryCharge = VendorArray.get(0).getDeliveryCharge();
                            DistanceKM = VendorArray.get(0).getDistanceKm();
                            Log.d("/*menuarray","menuarray updated(redirected here from drawer or bottom menu)");
                            MenuArray = VendorArray.get(0).getMenuArray();// get menu of selected vendor
                            if(VendorArray.get(0).getMenuArray().size() == 0) {
                                layoutNoItems.setVisibility(View.VISIBLE);
                            }else {
                                layoutNoItems.setVisibility(View.GONE);
                            }

                            //set switch for veg and filter veg menu
                            switchVegType.setChecked(true);
                            tempMenu.clear();
                            tvVegSwitch.setText("Veg");
                            ivVegSwitch.setImageResource(R.drawable.veg);
                            for(int j=0;j< MenuArray.size();j++) {
                                //  Log.d("/*menuarray", MenuArray.get(j).getVegType());
                                if(MenuArray.get(j).getVegType().trim().equals("veg"))
                                    tempMenu.add(MenuArray.get(j));
                            }
                            menuAdapter = new MenuAdapter(MenuListActivity.this,tempMenu, CategoryID);
                            rcMenu.setAdapter(menuAdapter);

//                            menuAdapter = new MenuAdapter(MenuListActivity.this,VendorArray.get(0).getMenuArray(), CategoryId);
//                            rcMenu.setAdapter(menuAdapter);
                        }
                        if(menuAdapter == null){
                            try {
                                cartObj = new JSONObject(sharedPref.getUserCart());
                                cartObj.put(getResources().getString(R.string.CartJsonObj), new JSONObject());
                                cartObj.put(getResources().getString(R.string.CartJsonArray), new JSONArray());
                                sharedPref.setUserCart(cartObj.toString());
                                bottomMenuAdapter.notifyDataSetChanged();
                                tvAddCartTotal.setText("0" +"/-");
                                layoutCartTotal.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            VendorArray.get(0).setSelected(true);
                            menuAdapter = new MenuAdapter(MenuListActivity.this,VendorArray.get(0).getMenuArray(), CategoryId);
                            rcMenu.setAdapter(menuAdapter);
                        }menuAdapterListener(menuAdapter, CategoryId);

                        vendorAdapter.setListener((position, cardVendor) -> {
                            for(int i = 0 ; i < VendorArray.size() ; i++){
                                if(i != position) {
                                    VendorArray.get(i).setSelected(false);
                                    cardVendor.setCardBackgroundColor(getResources().getColor(R.color.colorTrans));
                                }
                            }
                            VendorArray.get(position).setSelected(true);
                            //PackingCharge = VendorArray.get(position).getPackingCharge();
                            DeliveryCharge = VendorArray.get(position).getDeliveryCharge();
                            DistanceKM = VendorArray.get(position).getDistanceKm();
                            VendorID = VendorArray.get(position).getVendorId();
                            VendorLat = VendorArray.get(position).getVendorLat();
                            VendorLong = VendorArray.get(position).getVendorLong();
                            cardVendor.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            vendorAdapter.notifyDataSetChanged();

                            //set menu of selected vendor
                            if(VendorArray.get(position).getMenuArray().size() == 0) {
                                layoutNoItems.setVisibility(View.VISIBLE);
                            }else {
                                layoutNoItems.setVisibility(View.GONE);
                            }
                            MenuArray = VendorArray.get(position).getMenuArray();// get menu of selected vendor
                            Log.d("/*menuarray","menuarray updated(123)");

                            //set switch for veg and filter veg menu
                            switchVegType.setChecked(true);
                            tempMenu.clear();
                            tvVegSwitch.setText("Veg");
                            ivVegSwitch.setImageResource(R.drawable.veg);
                            for(int i=0;i< MenuArray.size();i++) {
                               // Log.d("/*menuarray", MenuArray.get(i).getVegType());
                                if(MenuArray.get(i).getVegType().trim().equals("veg"))
                                    tempMenu.add(MenuArray.get(i));
                            }

                            menuAdapter = new MenuAdapter(MenuListActivity.this,tempMenu, CategoryID);
                            rcMenu.setAdapter(menuAdapter);

                            //original
//                            menuAdapter = new MenuAdapter(MenuListActivity.this, MenuArray, CategoryId);
//                            rcMenu.setAdapter(menuAdapter);

                            menuAdapterListener(menuAdapter, CategoryId);
                        });

                    } else {
                        /*rcVendor.setVisibility(View.GONE);
                        rcMenu.setVisibility(View.GONE);*/
                        layoutNoItems.setVisibility(View.VISIBLE);
                    }
                }else {
                    /*rcVendor.setVisibility(View.GONE);
                    rcMenu.setVisibility(View.GONE);*/
                    layoutNoItems.setVisibility(View.VISIBLE);
                }

            } catch (JSONException e) {
                Log.e("/*MenuResponse","je:"+e.toString());
                e.printStackTrace();
            }
            layoutLoader.setVisibility(View.GONE);
        }, error -> {
            layoutLoader.setVisibility(View.GONE);
            Log.e("/*MenuResponse","ve:"+error.toString());
            Log.e("Menu", error.toString()+"_");
            Log.e("Menu", error.getMessage()+"_");
            error.printStackTrace();
        }){
            @Override
            protected Map<String, String> getParams() {

                return postData;
            }
        };
        requestQueue.add(customRequest);
        Log.e("Request", customRequest.toString()+"_"+postData.toString());
    }

    //when "Add+" button is clicked in menu adapter
    public void menuAdapterListener(MenuAdapter menuAdapter, String CategoryId){

        menuAdapter.setListener((menuPosition, QtyCount, tvQty, layoutAddOnsClick, tvTotalPrice, tvAddCart, productCounter) -> {
            try {
                cartObj = new JSONObject(sharedPref.getUserCart());
                Log.d("/*abc_add_clicked","cartObj:"+cartObj.toString());
//                for(int i=0;i<MenuArray.size();i++)
//                    Log.d("/*abc_menuarray","menuaaray:"+MenuArray.get(i).getProductName());
                JSONObject locationJson = cartObj.getJSONObject(getResources().getString(R.string.LocationJson)); //get location json of user location details
                if(!locationJson.has("FlatHouseNo") || (locationJson.has("FlatHouseNo")
                        && locationJson.getString("FlatHouseNo").isEmpty())){
                    Log.d("/*menu","address empty");
                    Intent intent = new Intent(MenuListActivity.this, AddressListActivity.class);
                    intent.putExtra("fromWhere", "Menu");
                    startActivity(intent);
                    finish();
                }else {
                    layoutLoader.setVisibility(View.VISIBLE);
                    JSONObject cartJsonObj = cartObj.getJSONObject(getResources().getString(R.string.CartJsonObj));

                    //checking whether cart contains dishes from another chef or category
                    if (!cartObj.getJSONArray(getResources().getString(R.string.CartJsonArray)).toString().equals("[]")
                            && (!cartJsonObj.getString("CategoryId").equals(CategoryId)
                            || !cartJsonObj.getString("DeliveryDate").equals(DeliveryDate)
                            || !cartJsonObj.getString("VendorId").equals(VendorID))) {
                        Log.d("/*menu","another chef/category dishes");
                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuListActivity.this);
                        builder.setMessage("Your Cart contains dishes from another Chef or Category.\nAre You sure to discard the previous selection and add new dishes from current Chef or Category?")
                                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    try {
                                        cartObj = new JSONObject(sharedPref.getUserCart());
                                        cartObj.put(getResources().getString(R.string.CartJsonObj), new JSONObject());
                                        JSONArray cartArray = new JSONArray();
                                        cartObj.put(getResources().getString(R.string.CartJsonArray), cartArray);
                                        sharedPref.setUserCart(cartObj.toString());

                                        vendorAdapter.notifyDataSetChanged();
                                        menuAdapter.notifyDataSetChanged();
                                        bottomMenuAdapter.notifyDataSetChanged();
                                        tvAddCartTotal.setText("0" + "/-");
                                        layoutCartTotal.setVisibility(View.GONE);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        if(!MenuArray.get(menuPosition).getProductImg().isEmpty()) {
                            Glide.with(MenuListActivity.this).load(URLServices.MenuImg + MenuArray.get(menuPosition).getProductImg()).into(ivMenuImg);
                            //Log.d("/*menu_menu",URLServices.MenuImg + MenuArray.get(menuPosition).getProductImg()+"");
                        }
                        else if(!MenuArray.get(menuPosition).getProductImgDefault().isEmpty()) {
                            Glide.with(MenuListActivity.this).load(URLServices.MenuDefaultImg + MenuArray.get(menuPosition).getProductImgDefault()).into(ivMenuImg);
                            //Log.d("/*menu_products",URLServices.MenuDefaultImg + MenuArray.get(menuPosition).getProductImgDefault()+"");
                        }
                        else Glide.with(MenuListActivity.this).load(R.drawable.mealarts_icon).into(ivMenuImg);

                        if(MenuArray.get(menuPosition).getVegType().toLowerCase().equals("veg"))
                            Glide.with(MenuListActivity.this).load(R.drawable.veg)
                                    .placeholder(R.drawable.mealarts_loader).into(ivVegType);
                        else Glide.with(MenuListActivity.this).load(R.drawable.nonveg)
                                .placeholder(R.drawable.mealarts_loader).into(ivVegType);

                        if(MenuArray.get(menuPosition).getOfferPer() > 0)
                            tvOfferPer.setVisibility(View.VISIBLE);
                        else tvOfferPer.setVisibility(View.GONE);

                        tvOfferPer.setText(MenuArray.get(menuPosition).getOfferPer()+"%");
                        tvPrepTime.setText(MenuArray.get(menuPosition).getMenuPrepTime()+" min");
                        tvMenuOrgPrice.setText("₹ "+MenuArray.get(menuPosition).getOriginalPrice());
                        tvMenuPrice.setText("₹ "+MenuArray.get(menuPosition).getSellingPrice());
                        tvMenuName.setText(MenuArray.get(menuPosition).getProductName().trim());
                        tvDescription.setText(MenuArray.get(menuPosition).getMenuDesc());

                        //tvDecQty, tvIncQty, tvContinueAddOns;

                        //tvAddCart.setEnabled(false);
                        Log.d("/*menu","final else");
                        cartJsonObj.put("VendorId", VendorID);
                        cartJsonObj.put("VendorLat", VendorLat);
                        cartJsonObj.put("VendorLong", VendorLong);
                        cartJsonObj.put("CategoryId", CategoryId);
                        cartJsonObj.put("DeliveryDate", DeliveryDate);
                        cartJsonObj.put("DeliveryCharge", DeliveryCharge);
                        cartJsonObj.put("DistanceKM", DistanceKM);

                        QtyCount = 0;
                        QtyCount++;
                        Log.d("/*abc_Menulist","QtyCount:"+QtyCount);
                        tvQty.setText(String.valueOf(QtyCount));
                        tvMenuQty.setText(String.valueOf(QtyCount));
                        tvTotalPrice.setText("₹ " + Math.round(QtyCount * Integer.parseInt(MenuArray.get(menuPosition).getSellingPrice())));
                        layoutAddOnsClick.setVisibility(View.VISIBLE);
                        tvMenuTotalPrice.setText("₹ " + Math.round(QtyCount * Integer.parseInt(MenuArray.get(menuPosition).getSellingPrice())));
                        tvMenuTotalPrice.setVisibility(View.VISIBLE);
                        tvAddCart.setVisibility(View.GONE);
                        productCounter.setVisibility(View.VISIBLE);

                        JSONArray cartArray = cartObj.getJSONArray(getResources().getString(R.string.CartJsonArray));
                        JSONObject cartItemObj = new JSONObject();
                        cartItemObj.put("VendorName", MenuArray.get(menuPosition).getVendorName());
                        cartItemObj.put("MenuId", MenuArray.get(menuPosition).getMenuId());
                        cartItemObj.put("ProductId", MenuArray.get(menuPosition).getProductId());
                        cartItemObj.put("ProductName", MenuArray.get(menuPosition).getProductName());
                        cartItemObj.put("SellingPrice", MenuArray.get(menuPosition).getSellingPrice());
                        cartItemObj.put("OfferPer", MenuArray.get(menuPosition).getOfferPer());
                        cartItemObj.put("Unit", MenuArray.get(menuPosition).getUnit());

                        //Log.d("/*menulist","img: "+MenuArray.get(menuPosition).getProductImg()+", "+MenuArray.get(menuPosition).getProductImgDefault());
                        if(MenuArray.get(menuPosition).getProductImg().isEmpty()){
                            //Log.d("/*menu","null");
                            cartItemObj.put("ProductImg", MenuArray.get(menuPosition).getProductImgDefault());
                        }
                        else {
                            cartItemObj.put("ProductImg", MenuArray.get(menuPosition).getProductImg());
                        }

                        cartItemObj.put("VegType", MenuArray.get(menuPosition).getVegType());
                        cartItemObj.put("ProdIngredients", MenuArray.get(menuPosition).getProdIngredients());
                        cartItemObj.put("Quantity", QtyCount);
                        cartItemObj.put("QtyPrice", Math.round(Integer.parseInt(MenuArray.get(menuPosition).getSellingPrice()) * QtyCount));
                        //hgd
//                        cartItemObj.put("isgst",isGST);
//                        cartItemObj.put("gstp",gstPercent);
                        cartItemObj.put("isgst",MenuArray.get(menuPosition).getIsGST());
                        cartItemObj.put("gstp",MenuArray.get(menuPosition).getGST_Perc());
                        if(MenuArray.get(menuPosition).getIsGST().equals("yes"))
                            cartItemObj.put("gst_amt",(Math.round(Integer.parseInt(MenuArray.get(menuPosition).getSellingPrice()) * QtyCount))*Float.parseFloat(MenuArray.get(menuPosition).getGST_Perc())/100);
                        else
                            cartItemObj.put("gst_amt",0);

                        cartItemObj.put("p_charge",Math.round(Float.parseFloat(MenuArray.get(menuPosition).getPack_charge()) * QtyCount));

                        cartItemObj.put(getResources().getString(R.string.AddOnsJsonArray), new JSONArray());
                        cartArray.put(cartItemObj);
                        sharedPref.setUserCart(cartObj.toString());
                        bottomMenuAdapter.notifyDataSetChanged();
                        Log.e("Cart_menulistactivity", sharedPref.getUserCart() + "_");
                        tvMenuTotal.setText(QtyCount + " Items " +"₹ " + Math.round(Integer.parseInt(MenuArray.get(menuPosition).getSellingPrice()) * QtyCount) +"/-");

                        if(MenuArray.get(menuPosition).getAddOns()) {
                            Animation slide_up = AnimationUtils.loadAnimation(MenuListActivity.this, R.anim.slide_up_300);
                            layoutAddOnsClick.setAnimation(slide_up);
                            layoutAddOnsClick.setVisibility(View.VISIBLE);
                            addOnsMenuAdapter = new AddOnsMenuAdapter(MenuListActivity.this, MenuArray.get(menuPosition).getAddOnsList(), "List");
                            rcAddOns.setAdapter(addOnsMenuAdapter);
                            AddOnsList = MenuArray.get(menuPosition).getAddOnsList();
                            addOnsAdapterListener(addOnsMenuAdapter);
                        }
                        setCartTotal();
                        setAddOnsTempTotal();
                    }
                    layoutLoader.setVisibility(View.GONE);
                }
            }catch (JSONException e){
                Log.d("/*menu",""+e.toString());
                e.printStackTrace();
            }
        });

        menuAdapter.setIncListener((position, QtyCount, tvQty, layoutAddOnsClick, tvTotalPrice, tvAddCart, productCounter) -> {
            try {
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                JSONArray cartArray = cartObj.getJSONArray(getResources().getString(R.string.CartJsonArray));
                for(int i = 0 ; i < cartArray.length() ; i++){
                    JSONObject cartItemObj = cartArray.getJSONObject(i);
                    QtyCount = cartItemObj.getInt("Quantity");
                    if(cartItemObj.getString("ProductId").equals(MenuArray.get(position).getProductId())){
                        if(!MenuArray.get(position).getProductImg().isEmpty()) {
                            Glide.with(MenuListActivity.this).load(URLServices.MenuImg + MenuArray.get(position).getProductImg()).into(ivMenuImg);
                            //Log.d("/*menu_menu",URLServices.MenuImg + MenuArray.get(position).getProductImg()+"");
                        }
                        else if(!MenuArray.get(position).getProductImgDefault().isEmpty()) {
                            Glide.with(MenuListActivity.this).load(URLServices.MenuDefaultImg + MenuArray.get(position).getProductImgDefault()).into(ivMenuImg);
                            //Log.d("/*menu_products",URLServices.MenuDefaultImg + MenuArray.get(position).getProductImgDefault()+"");
                        }
                        else Glide.with(MenuListActivity.this).load(R.drawable.mealarts_icon).into(ivMenuImg);

                        if(MenuArray.get(position).getVegType().toLowerCase().equals("veg"))
                            Glide.with(MenuListActivity.this).load(R.drawable.veg)
                                    .placeholder(R.drawable.mealarts_loader).into(ivVegType);
                        else Glide.with(MenuListActivity.this).load(R.drawable.nonveg)
                                .placeholder(R.drawable.mealarts_loader).into(ivVegType);

                        if(MenuArray.get(position).getOfferPer() > 0)
                            tvOfferPer.setVisibility(View.VISIBLE);
                        else tvOfferPer.setVisibility(View.GONE);

                        tvOfferPer.setText(MenuArray.get(position).getOfferPer()+"%");
                        tvPrepTime.setText(MenuArray.get(position).getMenuPrepTime()+" min");
                        tvMenuOrgPrice.setText("₹ "+MenuArray.get(position).getOriginalPrice());
                        tvMenuPrice.setText("₹ "+MenuArray.get(position).getSellingPrice());
                        tvMenuName.setText(MenuArray.get(position).getProductName().trim());
                        tvDescription.setText(MenuArray.get(position).getMenuDesc());

                        QtyCount ++;
                        cartItemObj.put("Quantity", QtyCount);
                        cartItemObj.put("QtyPrice", Math.round(Integer.parseInt(MenuArray.get(position).getSellingPrice())
                                * QtyCount));
//                        //hgd
                        if(MenuArray.get(position).getIsGST().equals("yes")) {
                            cartItemObj.put("gst_amt", (Math.round(Integer.parseInt(MenuArray.get(position).getSellingPrice()) * QtyCount)) * Float.parseFloat(MenuArray.get(position).getGST_Perc()) / 100);
                            Log.d("/*ma",""+(Math.round(Integer.parseInt(MenuArray.get(position).getSellingPrice())
                                    * QtyCount)) * Float.parseFloat(MenuArray.get(position).getGST_Perc()) / 100);
                        }
                        else
                            cartItemObj.put("gst_amt",0);

                        //15novhgd
                        cartItemObj.put("p_charge",Math.round(Integer.parseInt(MenuArray.get(position).getPack_charge()) * QtyCount));
//                        if(cartItemObj.getString("isgst").equals("yes"))
//                            gstTotal+=Float.parseFloat(cartItemObj.getString("QtyPrice"))*(Float.parseFloat(cartItemObj.getString("gstp"))/100);
                        //cartItemObj.put("gst",Math.round((Integer.parseInt(MenuArray.get(position).getSellingPrice())*QtyCount)*(Float.parseFloat(MenuArray.get(position).getGST_Perc())/100)));
//                        Log.d("/*ma_gst",QtyCount+", "+MenuArray.get(position).getGST_Perc()+", "
//                                + Math.round((Integer.parseInt(MenuArray.get(position).getSellingPrice())*QtyCount)*(Float.parseFloat(MenuArray.get(position).getGST_Perc())/100)));
                        //cartArray.put(cartItemObj);
                        tvQty.setText(String.valueOf(QtyCount));
                        tvMenuQty.setText(String.valueOf(QtyCount));
                        tvTotalPrice.setText("₹ " + Math.round(QtyCount * Integer.parseInt(MenuArray.get(position).getSellingPrice())));
                        tvMenuTotalPrice.setText("₹ " + Math.round(QtyCount * Integer.parseInt(MenuArray.get(position).getSellingPrice())));
                        layoutAddOnsClick.setVisibility(View.VISIBLE);
                        bottomMenuAdapter.notifyDataSetChanged();
                        menuAdapter.notifyDataSetChanged();

                        //cartObj.put(context.getResources().getString(R.string.CartJsonArray),cartArray);
                        sharedPref.setUserCart(cartObj.toString());
                        if(MenuArray.get(position).getAddOns() && layoutAddOns.getVisibility() == View.GONE) {
                            /*Animation slide_up = AnimationUtils.loadAnimation(MenuListActivity.this, R.anim.slide_up_300);
                            layoutAddOns.setAnimation(slide_up);
                            layoutAddOns.setVisibility(View.VISIBLE);*/
                            addOnsMenuAdapter = new AddOnsMenuAdapter(MenuListActivity.this, MenuArray.get(position).getAddOnsList(), "List");
                            rcAddOns.setAdapter(addOnsMenuAdapter);
                            AddOnsList = MenuArray.get(position).getAddOnsList();
                            addOnsAdapterListener(addOnsMenuAdapter);
                        }
                        //Log.e("Cart_madp2", sharedPref.getUserCart()+"_");
                    }
                    Log.e("Ids", cartItemObj.getString("ProductId")+"_"+(MenuArray.get(position).getProductId())+"_"+position);
                }
                setCartTotal();
                setAddOnsTempTotal();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        menuAdapter.setDecListener((position, QtyCount, tvQty, layoutAddOnsClick, tvTotalPrice, tvAddCart, productCounter) -> {
            try {
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                JSONArray cartArray = cartObj.getJSONArray(getResources().getString(R.string.CartJsonArray));
                for (int i = 0; i < cartArray.length(); i++) {
                    JSONObject cartItemObj = cartArray.getJSONObject(i);
                    QtyCount = cartItemObj.getInt("Quantity");
                    if (cartItemObj.getString("ProductId").equals(MenuArray.get(position).getProductId())) {
                        if(!MenuArray.get(position).getProductImg().isEmpty()) {
                            Glide.with(MenuListActivity.this).load(URLServices.MenuImg + MenuArray.get(position).getProductImg()).into(ivMenuImg);
                            //Log.d("/*menu_menu",URLServices.MenuImg + MenuArray.get(position).getProductImg()+"");
                        }
                        else if(!MenuArray.get(position).getProductImgDefault().isEmpty()) {
                            Glide.with(MenuListActivity.this).load(URLServices.MenuDefaultImg + MenuArray.get(position).getProductImgDefault()).into(ivMenuImg);
                            //Log.d("/*menu_products",URLServices.MenuDefaultImg + MenuArray.get(position).getProductImgDefault()+"");
                        }
                        else Glide.with(MenuListActivity.this).load(R.drawable.mealarts_icon).into(ivMenuImg);

                        if(MenuArray.get(position).getVegType().toLowerCase().equals("veg"))
                            Glide.with(MenuListActivity.this).load(R.drawable.veg)
                                    .placeholder(R.drawable.mealarts_loader).into(ivVegType);
                        else Glide.with(MenuListActivity.this).load(R.drawable.nonveg)
                                .placeholder(R.drawable.mealarts_loader).into(ivVegType);

                        if(MenuArray.get(position).getOfferPer() > 0)
                            tvOfferPer.setVisibility(View.VISIBLE);
                        else tvOfferPer.setVisibility(View.GONE);

                        tvOfferPer.setText(MenuArray.get(position).getOfferPer()+"%");
                        tvPrepTime.setText(MenuArray.get(position).getMenuPrepTime()+" min");
                        tvMenuOrgPrice.setText("₹ "+MenuArray.get(position).getOriginalPrice());
                        tvMenuPrice.setText("₹ "+MenuArray.get(position).getSellingPrice());
                        tvMenuName.setText(MenuArray.get(position).getProductName().trim());
                        tvDescription.setText(MenuArray.get(position).getMenuDesc());
                        if (QtyCount > 0) {
                            QtyCount--;
                            tvQty.setText(String.valueOf(QtyCount));
                            tvMenuQty.setText(String.valueOf(QtyCount));
                            tvTotalPrice.setText("₹ " + Math.round(QtyCount * Integer.parseInt(MenuArray.get(position).getSellingPrice())));
                            tvMenuTotalPrice.setText("₹ " + Math.round(QtyCount * Integer.parseInt(MenuArray.get(position).getSellingPrice())));
                            if (QtyCount > 0) {
                                cartItemObj.put("Quantity", QtyCount);
                                cartItemObj.put("QtyPrice", Math.round(Integer.parseInt(MenuArray.get(position).getSellingPrice()) * QtyCount));
                                //hgd
                                if(MenuArray.get(position).getIsGST().equals("yes"))
                                    cartItemObj.put("gst_amt",(Math.round(Integer.parseInt(MenuArray.get(position).getSellingPrice()) * QtyCount))*Float.parseFloat(MenuArray.get(position).getGST_Perc())/100);
                                else
                                    cartItemObj.put("gst_amt",0);

                                //15novhgd
                                cartItemObj.put("p_charge",Math.round(Integer.parseInt(MenuArray.get(position).getPack_charge()) * QtyCount));

//                                    if(cartItemObj.getString("isgst").equals("yes"))
//                                        gstTotal+=Float.parseFloat(cartItemObj.getString("QtyPrice"))*(Float.parseFloat(cartItemObj.getString("gstp"))/100);
                                // cartItemObj.put("gst",Math.round((Integer.parseInt(MenuArray.get(position).getSellingPrice())*QtyCount)*(Float.parseFloat(MenuArray.get(position).getGST_Perc())/100)));
//                                    Log.d("/*ma_gst",QtyCount+", "+MenuArray.get(position).getGST_Perc()+", "
//                                                  + Math.round((Integer.parseInt(MenuArray.get(position).getSellingPrice())*QtyCount)*(Float.parseFloat(MenuArray.get(position).getGST_Perc())/100)));
                                //cartArray.put(cartItemObj);

                                layoutAddOnsClick.setVisibility(View.VISIBLE);
                                tvAddCart.setVisibility(View.GONE);
                                productCounter.setVisibility(View.VISIBLE);
                                if(MenuArray.get(position).getAddOns() && layoutAddOns.getVisibility() == View.GONE) {
                                   /* Animation slide_up = AnimationUtils.loadAnimation(MenuListActivity.this, R.anim.slide_up_300);
                                    layoutAddOns.setAnimation(slide_up);
                                    layoutAddOns.setVisibility(View.VISIBLE);*/
                                    addOnsMenuAdapter = new AddOnsMenuAdapter(MenuListActivity.this, MenuArray.get(position).getAddOnsList(), "List");
                                    rcAddOns.setAdapter(addOnsMenuAdapter);
                                    AddOnsList = MenuArray.get(position).getAddOnsList();
                                    addOnsAdapterListener(addOnsMenuAdapter);
                                }
                            } else {
                                layoutAddOnsClick.setVisibility(View.GONE);
                                tvAddCart.setVisibility(View.VISIBLE);
                                //tvAddCart.setEnabled(true);
                                productCounter.setVisibility(View.GONE);

                                final ArrayList<JSONObject> result = new ArrayList<>(cartArray.length());
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    cartArray.remove(i);
                                    customToast.showCustomToast(MenuListActivity.this, "Removed from cart successfully !");
                                } else {
                                    for (int j = 0; j < cartArray.length(); j++) {
                                        final JSONObject obj = cartArray.optJSONObject(j);
                                        if (obj != null) {
                                            result.add(obj);
                                        }
                                    }
                                    for (int n = 0; n < result.size(); n++) {
                                        if (result.get(n).getString("ProductId").equals(MenuArray.get(position).getProductId())) {
                                            result.remove(n);
                                            customToast.showCustomToast(MenuListActivity.this, "Removed from cart successfully !");
                                        }
                                    }
                                    for (final JSONObject obj : result) {
                                        cartArray.put(obj);
                                    }
                                }

                                if(MenuArray.get(position).getAddOns()) {
                                    try {
                                        JSONArray addOnsTempArray = cartObj.getJSONArray(getResources().getString(R.string.AddOnsJsonArray));
                                        final ArrayList<JSONObject> addOnsTempResult = new ArrayList<>(addOnsTempArray.length());
                                        for (int l = 0; l < addOnsTempArray.length(); l++) {
                                            JSONObject addOnObj = addOnsTempArray.getJSONObject(l);
                                            if (addOnObj.getString("MenuId").equals(cartItemObj.getString("MenuId"))) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                    addOnsTempArray.remove(l);
                                                    //customToast.showCustomToast(MenuListActivity.this, "Removed successfully !");
                                                } else {
                                                    addOnsTempResult.remove(l);
                                                    for (final JSONObject obj : addOnsTempResult) {
                                                        addOnsTempArray.put(obj);
                                                    }
                                                }
                                            }
                                        }

                                        if (cartItemObj.has(getResources().getString(R.string.AddOnsJsonArray))) {
                                            cartItemObj.put(getResources().getString(R.string.AddOnsJsonArray), new JSONArray());
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if(cartArray.length() == 0){
                                    try {
                                        JSONObject newCartObj = new JSONObject(sharedPref.getUserCart());
                                        newCartObj.put(getResources().getString(R.string.CartJsonObj), new JSONObject());
                                        newCartObj.put(getResources().getString(R.string.CartJsonArray), new JSONArray());
                                        newCartObj.put(getResources().getString(R.string.AddOnsJsonArray), new JSONArray());
                                        sharedPref.setUserCart(newCartObj.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                //cartObj.put(context.getResources().getString(R.string.CartJsonArray),cartArray);
                                if(MenuArray.get(position).getAddOns() && layoutAddOns.getVisibility() == View.VISIBLE) {
                                    Animation slide_down = AnimationUtils.loadAnimation(MenuListActivity.this, R.anim.slide_down_300);
                                    layoutAddOns.setAnimation(slide_down);
                                    layoutAddOns.setVisibility(View.GONE);
                                }
                            }

                            sharedPref.setUserCart(cartObj.toString());
                            //Log.e("Cart_madp3", sharedPref.getUserCart() + "_");
                            MenuListActivity.bottomMenuAdapter.notifyDataSetChanged();
                            menuAdapter.notifyDataSetChanged();
                        }
                    }
                    Log.e("Ids", cartItemObj.getString("ProductId")+"_"+(MenuArray.get(position).getProductId()));
                }
                setCartTotal();
                setAddOnsTempTotal();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        menuAdapter.showAddOnsListener((position, hasAddOns, AddOnsArray) -> {

            if(!MenuArray.get(position).getProductImg().isEmpty()) {
                Glide.with(MenuListActivity.this).load(URLServices.MenuImg + MenuArray.get(position).getProductImg()).into(ivMenuImg);
                //Log.d("/*menu_menu",URLServices.MenuImg + MenuArray.get(position).getProductImg()+"");
            }
            else if(!MenuArray.get(position).getProductImgDefault().isEmpty()) {
                Glide.with(MenuListActivity.this).load(URLServices.MenuDefaultImg + MenuArray.get(position).getProductImgDefault()).into(ivMenuImg);
                //Log.d("/*menu_products",URLServices.MenuDefaultImg + MenuArray.get(position).getProductImgDefault()+"");
            }
            else Glide.with(MenuListActivity.this).load(R.drawable.mealarts_icon).into(ivMenuImg);

            if(MenuArray.get(position).getVegType().toLowerCase().equals("veg"))
                Glide.with(MenuListActivity.this).load(R.drawable.veg)
                        .placeholder(R.drawable.mealarts_loader).into(ivVegType);
            else Glide.with(MenuListActivity.this).load(R.drawable.nonveg)
                    .placeholder(R.drawable.mealarts_loader).into(ivVegType);

            if(MenuArray.get(position).getOfferPer() > 0)
                tvOfferPer.setVisibility(View.VISIBLE);
            else tvOfferPer.setVisibility(View.GONE);

            tvOfferPer.setText(MenuArray.get(position).getOfferPer()+"%");
            tvPrepTime.setText(MenuArray.get(position).getMenuPrepTime()+" min");
            tvMenuOrgPrice.setText("₹ "+MenuArray.get(position).getOriginalPrice());
            tvMenuPrice.setText("₹ "+MenuArray.get(position).getSellingPrice());
            tvMenuName.setText(MenuArray.get(position).getProductName().trim());
            tvDescription.setText(MenuArray.get(position).getMenuDesc());

            if(hasAddOns) {
                addonClicked=true;
                Animation slide_up = AnimationUtils.loadAnimation(MenuListActivity.this, R.anim.slide_up_300);
                layoutAddOns.setAnimation(slide_up);
                layoutAddOns.setVisibility(View.VISIBLE);
                AddOnsMenuAdapter addOnsMenuAdapter = new AddOnsMenuAdapter(MenuListActivity.this, AddOnsArray, "List");
                rcAddOns.setAdapter(addOnsMenuAdapter);
                addOnsAdapterListener(addOnsMenuAdapter);
            }
        });
    }

    public void addOnsAdapterListener(AddOnsMenuAdapter addOnsMenuAdapter){

        addOnsMenuAdapter.setListener((position, QtyCount, MenuAddOnsList, tvQty, tvTotalPrice, tvAddCart, productCounter) -> {
            int TotalPrice;
            QtyCount = 0;
            try {
                cartObj = new JSONObject(sharedPref.getUserCart());
                JSONArray addOnsArray;
                if(!cartObj.has(getResources().getString(R.string.AddOnsJsonArray))){
                    addOnsArray = new JSONArray();
                    cartObj.put(getResources().getString(R.string.AddOnsJsonArray), addOnsArray);
                }else
                    addOnsArray = cartObj.getJSONArray(getResources().getString(R.string.AddOnsJsonArray));

                QtyCount ++;
                tvQty.setText(String.valueOf(QtyCount));
                TotalPrice = MenuAddOnsList.get(position).getInt("sell_price") * QtyCount;
                tvTotalPrice.setText("₹ "+ TotalPrice +"/-");
                tvTotalPrice.setVisibility(View.VISIBLE);
                MenuAddOnsList.get(position).put("TotalPackAmt", MenuAddOnsList.get(position).getDouble("addon_pack_charge") * QtyCount);
                MenuAddOnsList.get(position).put("Quantity", QtyCount);
                MenuAddOnsList.get(position).put("QtyPrice", TotalPrice);
                addOnsArray.put(MenuAddOnsList.get(position));
                tvAddCart.setVisibility(View.GONE);
                productCounter.setVisibility(View.VISIBLE);
                sharedPref.setUserCart(String.valueOf(cartObj));
                Log.e("Cart_Add", sharedPref.getUserCart());

                layoutLoader.setVisibility(View.GONE);

                setAddOnsTempTotal();
            }catch (JSONException e){
                Log.d("/*menu",""+e.toString());
                e.printStackTrace();
            }
        });

        addOnsMenuAdapter.setIncListener((position, QtyCount, MenuAddOnsList, tvQty, tvTotalPrice, tvAddCart, productCounter) -> {
            int TotalPrice;
            try{
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                JSONArray addOnsArray = cartObj.getJSONArray(getResources().getString(R.string.AddOnsJsonArray));
                for(int l = 0 ; l < addOnsArray.length(); l++){
                    JSONObject addOnsObj = addOnsArray.getJSONObject(l);
                    if(addOnsObj.getString("addon_id").equals(MenuAddOnsList.get(position).getString("addon_id"))){
                        QtyCount = addOnsObj.getInt("Quantity");
                        QtyCount++;
                        tvQty.setText(String.valueOf(QtyCount));
                        TotalPrice = addOnsObj.getInt("sell_price") * QtyCount;
                        tvTotalPrice.setText("₹ "+ TotalPrice +"/-");
                        addOnsObj.put("TotalPackAmt", addOnsObj.getDouble("addon_pack_charge") * QtyCount);
                        addOnsObj.put("Quantity", QtyCount);
                        addOnsObj.put("QtyPrice", TotalPrice);
                        sharedPref.setUserCart(String.valueOf(cartObj));
                        Log.e("Cart_Add", sharedPref.getUserCart());

                        layoutLoader.setVisibility(View.GONE);
                    }
                }
                setAddOnsTempTotal();
            }catch (JSONException e){
                e.printStackTrace();
            }
        });

        addOnsMenuAdapter.setDecListener((position, QtyCount, MenuAddOnsList, tvQty, tvTotalPrice, tvAddCart, productCounter) -> {
            int TotalPrice;
            try{
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                JSONArray addOnsTempArray = cartObj.getJSONArray(getResources().getString(R.string.AddOnsJsonArray));
                for(int i = 0 ; i < addOnsTempArray.length(); i++) {
                    JSONObject addOnsObj = addOnsTempArray.getJSONObject(i);
                    if (addOnsObj.getString("addon_id").equals(MenuAddOnsList.get(position).getString("addon_id"))) {
                        QtyCount = addOnsObj.getInt("Quantity");
                        if (QtyCount > 0) {
                            QtyCount--;
                            if(QtyCount > 0){
                                tvQty.setText(String.valueOf(QtyCount));
                                TotalPrice = addOnsObj.getInt("sell_price") * QtyCount;
                                tvTotalPrice.setText("₹ "+ TotalPrice +"/-");
                                addOnsObj.put("TotalPackAmt", addOnsObj.getDouble("addon_pack_charge") * QtyCount);
                                addOnsObj.put("Quantity", QtyCount);
                                addOnsObj.put("QtyPrice", TotalPrice);
                            }else {
                                tvTotalPrice.setVisibility(View.INVISIBLE);
                                tvAddCart.setVisibility(View.VISIBLE);
                                productCounter.setVisibility(View.GONE);

                                final ArrayList<JSONObject> result = new ArrayList<>(addOnsTempArray.length());
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    addOnsTempArray.remove(i);
                                    //customToast.showCustomToast(MenuListActivity.this, "Removed successfully !");
                                } else {
                                    for (int n = 0; n < result.size(); n++) {
                                        if (result.get(n).getString("addon_id").equals(MenuAddOnsList.get(position).getString("addon_id"))) {
                                            result.remove(n);
                                            //customToast.showCustomToast(MenuListActivity.this, "Removed successfully !");
                                        }
                                    }
                                    for (final JSONObject obj : result) {
                                        addOnsTempArray.put(obj);
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
                sharedPref.setUserCart(String.valueOf(cartObj));
                Log.e("Cart_Add", sharedPref.getUserCart());
                addOnsMenuAdapter.notifyDataSetChanged();

                layoutLoader.setVisibility(View.GONE);

                setAddOnsTempTotal();
            }catch (JSONException e){
                e.printStackTrace();
            }
        });

        tvContinueAddOns.setOnClickListener(v -> {
            try {
                cartObj = new JSONObject(sharedPref.getUserCart());
                JSONArray cartJsonArray = cartObj.getJSONArray(getResources().getString(R.string.CartJsonArray));
                JSONArray tempAddOnsArray = cartObj.getJSONArray(getResources().getString(R.string.AddOnsJsonArray));
                for(int j = 0 ; j < cartJsonArray.length() ; j++){
                    JSONObject cartItemObj = cartJsonArray.getJSONObject(j);
                    for(int i = 0 ; i < tempAddOnsArray.length() ; i++){
                        JSONObject tempAddOnObj = tempAddOnsArray.getJSONObject(i);
                        String MenuId = tempAddOnObj.getString("MenuId");
                        JSONArray addOnsArray;
                        if(MenuId.equals(cartItemObj.getString("MenuId"))){
                            addOnsArray = tempAddOnsArray;
                            cartItemObj.put(getResources().getString(R.string.AddOnsJsonArray), addOnsArray);
                        }
                    }
                }
                //cartObj.put(getResources().getString(R.string.AddOnsJsonArray), new JSONArray());
                sharedPref.setUserCart(String.valueOf(cartObj));
                Log.e("Cart_Add", sharedPref.getUserCart());

                Animation slide_down = AnimationUtils.loadAnimation(MenuListActivity.this, R.anim.slide_down_300);
                layoutAddOns.setAnimation(slide_down);
                layoutAddOns.setVisibility(View.GONE);

                setCartTotal();
                setAddOnsTempTotal();
            }catch (JSONException e){
                e.printStackTrace();
            }
        });

    }

    public void setCartTotal() throws JSONException {
        float cartTotal = 0, gstTotal=0, PackingCharge=0;
        cartObj = new JSONObject(sharedPref.getUserCart());
        JSONObject cartJSONObj = cartObj.getJSONObject(getResources().getString(R.string.CartJsonObj));
        JSONArray cartArray = cartObj.getJSONArray(getResources().getString(R.string.CartJsonArray));
        if(cartArray.length() == 1 && layoutCartTotal.getVisibility() != View.VISIBLE) {
            Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_300);
            layoutCartTotal.setAnimation(slide_up);
            layoutCartTotal.setVisibility(View.VISIBLE);
        }else if(cartArray.length() == 0){
            Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_300);
            layoutCartTotal.setAnimation(slide_down);
            layoutCartTotal.setVisibility(View.GONE);
        }
        for(int i = 0 ; i < cartArray.length(); i++){
            JSONObject cartItemObjs = cartArray.getJSONObject(i) ;
            cartTotal += Float.parseFloat(cartItemObjs.getString("QtyPrice"));
            PackingCharge += /*(Float.parseFloat(cartItemObjs.getString("Quantity")) * )*/
                    Float.parseFloat(cartItemObjs.getString("p_charge"));

            Log.d("/*adp",cartItemObjs.getString("QtyPrice")+", "+cartItemObjs.getString("gstp"));

            if(cartItemObjs.getString("isgst").equals("yes"))
                    gstTotal+=Float.parseFloat(cartItemObjs.getString("QtyPrice"))
                            *(Float.parseFloat(cartItemObjs.getString("gstp"))/100);

            if(cartItemObjs.has(getResources().getString(R.string.AddOnsJsonArray))){
                JSONArray addOnsArray = cartItemObjs.getJSONArray(getResources().getString(R.string.AddOnsJsonArray));
                if(addOnsArray.length() != 0){
                    for(int j = 0 ; j < addOnsArray.length() ; j++){
                        JSONObject addOnsObj = addOnsArray.getJSONObject(j) ;
                        cartTotal += Float.parseFloat(addOnsObj.getString("QtyPrice"));
                        PackingCharge += Float.parseFloat(addOnsObj.getString("TotalPackAmt"));
                    }
                }
            }
        }
        cartJSONObj.put("TotalAmount", String.valueOf(cartTotal));
        cartJSONObj.put("totalPackCharge", PackingCharge);
        cartJSONObj.put("totalGST",String.valueOf(gstTotal));
        sharedPref.setUserCart(String.valueOf(cartObj));
        tvAddCartTotal.setText("₹ " + Math.round(cartTotal) +"/-");
    }

    public void setAddOnsTempTotal() throws JSONException{
        int TotalPerMenu = 0;
        int TotalQtyCount = 0;
        cartObj = new JSONObject(sharedPref.getUserCart());
        JSONArray cartJsonArray = cartObj.getJSONArray(getResources().getString(R.string.CartJsonArray));
        for (int k = 0; k < cartJsonArray.length(); k++) {
            JSONObject cartItemObj = cartJsonArray.getJSONObject(k);
            TotalPerMenu = cartItemObj.getInt("QtyPrice");
            TotalQtyCount = Integer.parseInt(cartItemObj.getString("Quantity"));

            if(cartObj.has("AddOnsJsonArray")) {
                JSONArray addOnArray = cartObj.getJSONArray(getResources().getString(R.string.AddOnsJsonArray));
                for (int j = 0; j < addOnArray.length(); j++) {
                    JSONObject addOnObj = addOnArray.getJSONObject(j);
                    if(addOnObj.getString("MenuId").equals(cartItemObj.getString("MenuId"))) {
                        TotalPerMenu += addOnObj.getInt("QtyPrice");
                        TotalQtyCount += Integer.parseInt(addOnObj.getString("Quantity"));
                    }
                }
            } else if(cartItemObj.has(getResources().getString(R.string.AddOnsJsonArray))
                    && cartItemObj.getJSONArray(getResources().getString(R.string.AddOnsJsonArray)).length() != 0){
                JSONArray addOnsArray = cartItemObj.getJSONArray(getResources().getString(R.string.AddOnsJsonArray));
                for(int j = 0 ; j < addOnsArray.length() ; j++){
                    JSONObject addOnsObj = addOnsArray.getJSONObject(j) ;
                    if(addOnsObj.getString("MenuId").equals(cartItemObj.getString("MenuId"))) {
                        TotalPerMenu += addOnsObj.getInt("QtyPrice");
                        TotalQtyCount += Integer.parseInt(addOnsObj.getString("Quantity"));
                    }
                }
            }
        }
        MenuListActivity.tvMenuTotal.setText(TotalQtyCount + " Items " + "₹ " + Math.round(TotalPerMenu) + "/-");
    }
}