package com.mealarts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.mealarts.Adapters.BottomMenuAdapter;
import com.mealarts.Adapters.OrderListAdapter;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CheckExtras;
import com.mealarts.Helpers.CustomToast;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.Helpers.Utils.BottomMenuUtils;
import com.mealarts.Helpers.Utils.OrderUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("SetTextI18n")
public class ProfileActivity extends AppCompatActivity {

    SwipeRefreshLayout layoutSwipeRef;
    RecyclerView rcLowerMenu, rcOrderList;
    TextView tvContactDetails, tvUserName, tvEditProfile, tvChangePass, tvManageAddress, tvLogOut, tvContact, tvViewMore;
    EditText edtCustName, edtEmail;
    Button btnUpdateUser;
    LinearLayout layoutUpdateUser, layoutOrder;
    public static LinearLayout layoutLoader;
    ImageView ivLoader, ivBack;

    ArrayList<BottomMenuUtils> bottomMenuArray;
    Integer[] icons = {R.drawable.home, R.drawable.menu_card, R.drawable.account, R.drawable.cart};
    Integer[] selectPosition = {R.drawable.home_select, R.drawable.menu_card_select,
            R.drawable.account_select, R.drawable.cart_select};

    CustomToast customToast = new CustomToast();
    SharedPref sharedPrefs;
    CheckExtras connection;
    SSLCertification sslCertification = new SSLCertification(ProfileActivity.this);
    ArrayList<OrderUtils> orderList, AllOrders;
    public static OrderListAdapter orderListAdapter;

    public static final String NOTIFY_ACTIVITY_ACTION = "notify_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPrefs = new SharedPref(ProfileActivity.this);
        connection = new CheckExtras(ProfileActivity.this);

        layoutSwipeRef = findViewById(R.id.layoutSwipeRef);
        layoutLoader = findViewById(R.id.layoutLoader);
        ivLoader = findViewById(R.id.ivLoader);
        Glide.with(ProfileActivity.this).asGif().load(R.drawable.mealarts_loader).into(ivLoader);
        layoutLoader.setVisibility(View.VISIBLE);

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> onBackPressed());

//----------------------------------Bottom Tab Menu----------------------------------//
        rcLowerMenu = findViewById(R.id.rcLowerMenu);
        rcLowerMenu.setHasFixedSize(true);
        rcLowerMenu.setLayoutManager(new GridLayoutManager(ProfileActivity.this, 4));
        sharedPrefs.setPos(2);
        setBottomMenu();

//----------------------------------Set Profile Data----------------------------------//
        tvUserName = findViewById(R.id.tvUserName);
        tvContactDetails = findViewById(R.id.tvContactDetails);
        getProfile();
        layoutSwipeRef.setOnRefreshListener(this::getProfile);

//----------------------------------Edit Profile Data----------------------------------//
        tvEditProfile = findViewById(R.id.tvEditProfile);
        tvEditProfile.setOnClickListener(v -> {
            Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
            layoutUpdateUser.setAnimation(slide_up);
            layoutUpdateUser.setVisibility(View.VISIBLE);
        });

//----------------------------------Update Profile Data----------------------------------//
        layoutUpdateUser = findViewById(R.id.layoutUpdateUser);
        edtCustName = findViewById(R.id.edtCustName);
        tvContact = findViewById(R.id.tvContact);
        edtEmail = findViewById(R.id.edtEmail);
        btnUpdateUser = findViewById(R.id.btnUpdateUser);
        btnUpdateUser.setOnClickListener(v -> validateProfile());

//----------------------------------Change Password----------------------------------//
        tvChangePass = findViewById(R.id.tvChangePass);
        tvChangePass.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, PasswordActivity.class);
            intent.putExtra("FromCart", false);
            intent.putExtra("Procedure", "change");
            intent.putExtra("Contact", tvContact.getText().toString().trim());
            startActivity(intent);
        });

        layoutOrder = findViewById(R.id.layoutOrder);
        tvLogOut = findViewById(R.id.tvLogOut);
        tvLogOut.setOnClickListener(v -> sharedPrefs.logoutApp());

//----------------------------------Manage Address----------------------------------//
        tvManageAddress = findViewById(R.id.tvManageAddress);
        tvManageAddress.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, ManageAddressActivity.class));
        });

        tvViewMore = findViewById(R.id.tvViewMore);
        SpannableString string = new SpannableString("View More");
        string.setSpan(new UnderlineSpan(), 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        rcOrderList = findViewById(R.id.rcOrderList);
        rcOrderList.setHasFixedSize(true);
        rcOrderList.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
        rcOrderList.setNestedScrollingEnabled(false);

        tvViewMore.setOnClickListener(v -> {
            int lastPos = orderList.size();
            if(AllOrders.size() > orderList.size() && AllOrders.size() > orderList.size() + 3 ) {
                for (int i = lastPos - 1 ; i < lastPos + 3 ; i++) {
                    orderList.add(AllOrders.get(i));
                }
            }else {
                for(int i = lastPos ; i < AllOrders.size() ; i++){
                    orderList.add(AllOrders.get(i));
                }
                tvViewMore.setVisibility(View.GONE);
            }

            orderListAdapter = new OrderListAdapter(ProfileActivity.this, orderList);
            rcOrderList.setAdapter(orderListAdapter);
        });
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Glide.with(ProfileActivity.this).asGif().load(R.drawable.mealarts_loader).into(ivLoader);
            layoutLoader.setVisibility(View.VISIBLE);
            getProfile();
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

//----------------------------------Bottom Tab Menu----------------------------------//
    public void setBottomMenu() {
        bottomMenuArray = new ArrayList<>();
        for (int i = 0; i < icons.length; i++) {
            BottomMenuUtils bottomMenuUtils = new BottomMenuUtils();
            bottomMenuUtils.setBottomTab(icons[i]);
            bottomMenuUtils.setSelected(i == sharedPrefs.getPos());
            bottomMenuArray.add(bottomMenuUtils);
        }
        final BottomMenuAdapter bottomMenuAdapter = new BottomMenuAdapter(ProfileActivity.this, bottomMenuArray);
        rcLowerMenu.setAdapter(bottomMenuAdapter);
        bottomMenuAdapter.setListener((position, imgView) -> {
            if (position == 0){
                startActivity(new Intent(ProfileActivity.this, MainActivity.class).putExtra("FromSplash", false));
                finish();
            }
            else if (position == 1){
                startActivity(new Intent(ProfileActivity.this, MenuListActivity.class));
                finish();
            }
            else if (position == 3) {
                try {
                    JSONObject cartObj = new JSONObject(sharedPrefs.getUserCart());
                    if((!cartObj.getJSONObject(getResources().getString(R.string.LocationJson))
                            .has("FlatHouseNo") || cartObj.getJSONObject(getResources().getString(R.string.LocationJson))
                            .getString("FlatHouseNo").isEmpty())
                            && cartObj.has(getResources().getString(R.string.CartJsonArray))
                            && cartObj.getJSONArray(getResources().getString(R.string.CartJsonArray)).length() != 0){
                        customToast.showCustomToast(ProfileActivity.this, "Please Set Your Delivery Address from Home !!");
                    }else {
                        startActivity(new Intent(ProfileActivity.this, AddToCartActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < bottomMenuArray.size(); i++) {
                if (position != i) {
                    bottomMenuArray.get(i).setSelected(false);
                    Picasso.get().load(bottomMenuArray.get(i).getBottomTab()).into(imgView);
                }
            }
            sharedPrefs.setPos(position);
            bottomMenuArray.get(position).setSelected(true);
            Picasso.get().load(selectPosition[position]).into(imgView);
            bottomMenuAdapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sharedPrefs.setPos(2);
        setBottomMenu();
    }

//----------------------------------Get Profile Data----------------------------------//
    HashMap<String, String> postData;
    public void getProfile(){
        postData = new HashMap<>();
        postData.put("customer_id", sharedPrefs.getUserId());

        RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.UserProfile, response -> {
            Log.e("ProfileRes", response);
            if(layoutSwipeRef.isRefreshing())
                layoutSwipeRef.setRefreshing(false);
            try {
                JSONObject userObj = new JSONObject(response);
                tvUserName.setText(userObj.getString("cust_name"));
                tvContactDetails.setText(userObj.getString("mobile")+" , "+userObj.getString("email"));

                edtCustName.setText(userObj.getString("cust_name"));
                tvContact.setText(userObj.getString("mobile"));
                edtEmail.setText(userObj.getString("email"));

                JSONObject cartObj = new JSONObject(sharedPrefs.getUserCart());
                if(cartObj.has(getResources().getString(R.string.UserJSON))) {
                    JSONObject userCartObj = cartObj.getJSONObject(getResources().getString(R.string.UserJSON));
                    userCartObj.put("UserName", userObj.getString("cust_name"));
                    userCartObj.put("Email", userObj.getString("email"));
                    userCartObj.put("Mobile", userObj.getString("mobile"));
                }else {
                    JSONObject userCartObj = new JSONObject();
                    userObj.put("UserName", userObj.getString("cust_name"));
                    userObj.put("Email", userObj.getString("email"));
                    userObj.put("Mobile", userObj.getString("mobile"));
                    cartObj.put(getResources().getString(R.string.UserJSON),userCartObj);
                }
                sharedPrefs.setUserCart(cartObj.toString());

                JSONArray orderArray = userObj.getJSONArray("orders");
                Log.e("length", String.valueOf(orderArray.length()));
                if(orderArray.length() != 0) {
                    AllOrders = new ArrayList<>();
                    orderList = new ArrayList<>();
                    for (int i = 0; i < orderArray.length(); i++) {
                        JSONObject orderObj = orderArray.getJSONObject(i);
                        OrderUtils orderUtils = new OrderUtils();
                        orderUtils.setOrderId(orderObj.getString("checkout_id"));
                        orderUtils.setVendorName(orderObj.getString("vender_name"));
                        orderUtils.setVendorLocation(orderObj.getString("vendor_area") + ", " + orderObj.getString("vendor_city"));
                        orderUtils.setOrderTotal(orderObj.getString("grand_total"));
                        orderUtils.setOrderDate(orderObj.getString("order_place_date"));
                        orderUtils.setRated(!orderObj.getString("order_rating").equals(""));
                        orderUtils.setOrderStatus(orderObj.getString("order_status"));

                        StringBuilder OrderItems = new StringBuilder();
                        JSONArray menuArray = orderObj.getJSONArray("Menu");
                        JSONObject obj = menuArray.getJSONObject(0);
                        String vendorId = obj.getString("vendor_id");
                        for (int j = 0; j < menuArray.length(); j++) {
                            JSONObject menuObj = menuArray.getJSONObject(j);
                            OrderItems.append(menuObj.getString("prod_name"));
                            OrderItems.append(" x ").append(menuObj.getString("quantity")).append(", ");
                        }
                        orderUtils.setOrderItems(OrderItems.substring(0, OrderItems.lastIndexOf(",")));
                        orderUtils.setVendorId(vendorId);
                        AllOrders.add(orderUtils);
                    }
                    Collections.sort(AllOrders, new Comparator<OrderUtils>() {
                        @Override
                        public int compare(OrderUtils lhs, OrderUtils rhs) {
                            return extractInt(rhs.getOrderId()) - extractInt(lhs.getOrderId());
                        }

                        int extractInt(String s) {
                            String num = s.replaceAll("\\D", "");
                            // return 0 if no digits found
                            return num.isEmpty() ? 0 : Integer.parseInt(num);
                        }
                    });
                    if (AllOrders.size() > 3)
                        tvViewMore.setVisibility(View.VISIBLE);
                    else tvViewMore.setVisibility(View.GONE);

                    if (AllOrders.size() > orderList.size() && AllOrders.size() > orderList.size() + 3) {
                        for (int i = 0; i < 3; i++) {
                            orderList.add(AllOrders.get(i));
                        }
                    } else {
                        orderList.addAll(AllOrders);
                    }

                    orderListAdapter = new OrderListAdapter(ProfileActivity.this, orderList);
                    rcOrderList.setAdapter(orderListAdapter);
                }else {
                    layoutOrder.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            layoutLoader.setVisibility(View.GONE);
        }, error -> {
            Log.e("GetProfile", error.toString()+"_");
            Log.e("GetProfile", error.getMessage()+"_");
            error.printStackTrace();
        }){
            @Override
            protected Map<String, String> getParams(){
                return postData;
            }
        };
        requestQueue.add(stringRequest);
    }

//----------------------------------Update Profile Data----------------------------------//

    public void validateProfile(){
        String FullName = edtCustName.getText().toString().trim();
        String Contact = tvContact.getText().toString().trim();
        String Email = edtEmail.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(FullName.isEmpty()) {
            edtCustName.requestFocus();
            edtCustName.setError("Required");
            customToast.showCustomToast(ProfileActivity.this, "Full Name Required");
        }else if(Email.isEmpty()) {
            edtEmail.requestFocus();
            customToast.showCustomToast(ProfileActivity.this, "Email ID Required");
            edtEmail.setError("Required");
        }else if(!Email.matches(emailPattern)) {
            edtEmail.requestFocus();
            edtEmail.setError("Invalid Email Formation");
            customToast.showCustomToast(ProfileActivity.this, "Invalid Email Formation");
        }else {
            layoutLoader.setVisibility(View.VISIBLE);
            updateProfile(FullName, Contact, Email);
        }
    }

    public void updateProfile(String FullName, String Contact, String Email){
        postData = new HashMap<>();
        postData.put("customer_id", sharedPrefs.getUserId());
        postData.put("cust_name", FullName);
        postData.put("email", Email);
        postData.put("mobile", Contact);

        RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.UpdateProfile, response -> {
            Log.e("UpdateRes", response);
            try {
                JSONObject userObj = new JSONObject(response);
                String Status = userObj.getString("success");
                if(Status.toLowerCase().contains("success")){
                    Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                    layoutUpdateUser.setAnimation(slide_down);
                    layoutUpdateUser.setVisibility(View.GONE);
                    customToast.showCustomToast(ProfileActivity.this, Status);
                    getProfile();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            layoutLoader.setVisibility(View.GONE);
        }, error -> {
            Log.e("UpdateProfile", error.toString()+"_");
            Log.e("UpdateProfile", error.getMessage()+"_");
            error.printStackTrace();
        }){
            @Override
            protected Map<String, String> getParams() {
                return postData;
            }
        };
        requestQueue.add(stringRequest);
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {

        if(layoutUpdateUser.getVisibility() == View.VISIBLE) {
            Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
            layoutUpdateUser.setAnimation(slide_down);
            layoutUpdateUser.setVisibility(View.GONE);
        }else if(isTaskRoot()){
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            customToast.showCustomToast(this, "Please click BACK again to exit");

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
        } else super.onBackPressed();
    }
}
