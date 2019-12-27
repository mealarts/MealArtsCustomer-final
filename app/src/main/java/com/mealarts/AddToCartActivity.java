package com.mealarts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mealarts.Adapters.AddOnsCartAdapter;
import com.mealarts.Adapters.AddOnsMenuAdapter;
import com.mealarts.Adapters.BottomMenuAdapter;
import com.mealarts.Adapters.CartAdapter;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CheckExtras;
import com.mealarts.Helpers.CustomToast;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.Helpers.Utils.BottomMenuUtils;
import com.mealarts.Helpers.Utils.CartUtils;
import com.mealarts.Helpers.Utils.CategoryUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddToCartActivity extends AppCompatActivity {

    public static RecyclerView rcLowerMenu, rcCart;
    public static LinearLayout layoutNoItems;
    LinearLayout layoutCartAlert, layoutMenuAlert, layoutCheckout;
    ImageView ivBack;
    public static TextView tvCartTotal;
    TextView tvAlertMessage, tvClearCart, tvAlertMessage1, tvClearCart1;
    Button btnCheckout, btnClearCart, btnOrderNow;

    ArrayList<CartUtils> cartArrayList;
    ArrayList<BottomMenuUtils> bottomMenuArray;
    public static BottomMenuAdapter bottomMenuAdapter;
    Integer[] icons = {R.drawable.home, R.drawable.menu_card, R.drawable.account, R.drawable.cart};
    Integer[] selectPosition = {R.drawable.home_select, R.drawable.menu_card_select,
            R.drawable.account_select, R.drawable.cart_select};

    SharedPref sharedPref;
    CheckExtras connection;
    public static CartAdapter cartAdapter;
    CustomToast customToast = new CustomToast();
    SSLCertification sslCertification = new SSLCertification(AddToCartActivity.this);

    float cartTotal = 0,gst=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);

        sharedPref = new SharedPref(AddToCartActivity.this);
        connection = new CheckExtras(AddToCartActivity.this);

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> onBackPressed());

//----------------------------------Bottom Tab Menu----------------------------------//
        rcLowerMenu = findViewById(R.id.rcLowerMenu);
        rcLowerMenu.setHasFixedSize(true);
        rcLowerMenu.setLayoutManager(new GridLayoutManager(AddToCartActivity.this, 4));
        sharedPref.setPos(3);
        setBottomMenu();

//----------------------------------Set Cart values----------------------------------//
        btnClearCart = findViewById(R.id.btnClearCart);
        btnCheckout = findViewById(R.id.btnCheckout);
        layoutNoItems = findViewById(R.id.layoutNoItems);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        rcCart = findViewById(R.id.rcCart);
        rcCart.setHasFixedSize(true);
        rcCart.setLayoutManager(new LinearLayoutManager(AddToCartActivity.this));
        rcCart.setNestedScrollingEnabled(false);
        setCart();

        //----------------------------------Check Cart By Time----------------------------------//
        tvClearCart = findViewById(R.id.tvClearCart);
        tvAlertMessage = findViewById(R.id.tvAlertMessage);
        layoutCartAlert = findViewById(R.id.layoutCartAlert);
        layoutCheckout = findViewById(R.id.layoutCheckout);

        try {
            JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
            JSONArray cartArray = cartObj.getJSONArray(getResources().getString(R.string.CartJsonArray));
            if(cartArray.length() != 0)
                getServerTime();
        }catch (JSONException e){
            e.printStackTrace();
        }

        tvClearCart.setOnClickListener(v -> {
            try {
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                cartObj.put(getResources().getString(R.string.CartJsonObj), new JSONObject());
                cartObj.put(getResources().getString(R.string.CartJsonArray), new JSONArray());
                cartObj.put(getResources().getString(R.string.AddOnsJsonArray), new JSONArray());
                sharedPref.setUserCart(cartObj.toString());
                layoutCartAlert.setVisibility(View.GONE);
                bottomMenuAdapter.notifyDataSetChanged();
                getServerTime();
                cartTotal = 0;
                setCart();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        //----------------------------------Check Cart By Time----------------------------------//
        tvClearCart1 = findViewById(R.id.tvClearCart1);
        tvAlertMessage1 = findViewById(R.id.tvAlertMessage1);
        layoutMenuAlert = findViewById(R.id.layoutMenuAlert);

        tvClearCart1.setOnClickListener(v -> {
            try {
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                cartObj.put(getResources().getString(R.string.CartJsonObj), new JSONObject());
                cartObj.put(getResources().getString(R.string.CartJsonArray), new JSONArray());
                cartObj.put(getResources().getString(R.string.AddOnsJsonArray), new JSONArray());
                sharedPref.setUserCart(cartObj.toString());
                layoutMenuAlert.setVisibility(View.GONE);
                bottomMenuAdapter.notifyDataSetChanged();
                cartTotal = 0;
                setCart();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        btnCheckout.setOnClickListener(v -> {
            try {
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                JSONObject userObj = cartObj.getJSONObject(getResources().getString(R.string.CartJsonObj));
                JSONArray cartArray = cartObj.getJSONArray(getResources().getString(R.string.CartJsonArray));
                Log.d("/*cart",cartArray.toString());
                cartTotal = 0;
             //gst=0; //hgd
                for(int i = 0 ; i < cartArray.length(); i++){
                    JSONObject cartItemObjs = cartArray.getJSONObject(i) ;
                    cartTotal += Float.parseFloat(cartItemObjs.getString("QtyPrice"));
//                    Log.d("/*cart",gst+"ok");
//                    gst+=Float.parseFloat(cartItemObjs.getString("gst"));//hgd
//                    Log.d("/*cart",gst+"gst");
                    if(cartItemObjs.has(getResources().getString(R.string.AddOnsJsonArray))){
                        JSONArray addOnsArray = cartItemObjs.getJSONArray(getResources().getString(R.string.AddOnsJsonArray));
                        if(addOnsArray.length() != 0){
                            for(int j = 0 ; j < addOnsArray.length() ; j++){
                                JSONObject addOnsObj = addOnsArray.getJSONObject(j) ;
                                cartTotal += Float.parseFloat(addOnsObj.getString("QtyPrice"));
                            }
                        }
                    }
                }

                userObj.put("TotalAmount", cartTotal);
                sharedPref.setUserCart(cartObj.toString());

                if(sharedPref.getUserId().equals("")){
                    Intent intent = new Intent(AddToCartActivity.this, LogInActivity.class);
                    intent.putExtra("FromCart", true);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(AddToCartActivity.this, CheckoutDetailsActivity.class);
                    startActivity(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        btnClearCart.setOnClickListener(v -> {
            try {
                JSONObject newCartObj = new JSONObject(sharedPref.getUserCart());
                newCartObj.put(getResources().getString(R.string.CartJsonObj), new JSONObject());
                newCartObj.put(getResources().getString(R.string.CartJsonArray), new JSONArray());
                newCartObj.put(getResources().getString(R.string.AddOnsJsonArray), new JSONArray());
                sharedPref.setUserCart(newCartObj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            cartTotal = 0;
            setCart();

            /*Intent intent = new Intent(AddToCartActivity.this, MenuListActivity.class);
            startActivity(intent);
            finish();*/
        });

        btnOrderNow = findViewById(R.id.btnOrderNow);
        btnOrderNow.setOnClickListener(v -> {
            Intent intent = new Intent(AddToCartActivity.this, MenuListActivity.class);
            startActivity(intent);
            finish();
        });
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
        bottomMenuAdapter = new BottomMenuAdapter(AddToCartActivity.this, bottomMenuArray);
        rcLowerMenu.setAdapter(bottomMenuAdapter);
        //BottomMenuAdapter.MyViewHolder.tvCartCount.setText(CartCounter);
        bottomMenuAdapter.setListener((position, imgView) -> {
            if (position == 0){
                startActivity(new Intent(AddToCartActivity.this, MainActivity.class).putExtra("FromSplash", false));
                finish();
            }
            else if (position == 1){
                startActivity(new Intent(AddToCartActivity.this, MenuListActivity.class));
                finish();
            }
            else if (position == 2) {
                if(sharedPref.getUserId().equals(""))
                    startActivity(new Intent(AddToCartActivity.this, LogInActivity.class));
                else{
                    startActivity(new Intent(AddToCartActivity.this, ProfileActivity.class));
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
    protected void onRestart() {
        super.onRestart();
        sharedPref.setPos(3);
        setBottomMenu();
        setCart();
    }

//----------------------------------Set Cart Adapter----------------------------------//
    @SuppressLint("SetTextI18n")
    public void setCart(){
        try {
            JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
            if(!cartObj.toString().equals("{}")) {
                JSONArray cartArray = cartObj.getJSONArray(getResources().getString(R.string.CartJsonArray));

                if (cartArray.length() != 0) {
                    JSONObject cartJsonObj = cartObj.getJSONObject(getResources().getString(R.string.CartJsonObj));
                    String CategoryId = cartJsonObj.getString("CategoryId");
                    String VendorId = cartJsonObj.getString("VendorId");

                    String MenuIds = "";
                    for(int j = 0 ; j < cartArray.length() ; j++) {
                        JSONObject cartItemObj = cartArray.getJSONObject(j);
                        MenuIds = MenuIds + cartItemObj.getString("MenuId") + ",";
                    }
                    getCart(CategoryId, VendorId, MenuIds, cartObj);

                } else{
                    bottomMenuAdapter.notifyDataSetChanged();
                    layoutNoItems.setVisibility(View.VISIBLE);
                }
            }else layoutNoItems.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getCart(String CategoryId, String VendorId, String MenuIds, JSONObject cartObj){
        Map<String, String> postData = new HashMap<>();
        postData.put("vendor_id",VendorId);
        postData.put("category_id",CategoryId);
        postData.put("menu_id", MenuIds.substring(0, MenuIds.lastIndexOf(",")));

        RequestQueue requestQueue = Volley.newRequestQueue(AddToCartActivity.this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.GetCart, response -> {
            Log.e("GetCartRequest", response);
            try {
                JSONObject setCartObj = new JSONObject(response);
                String VendorOnOff = setCartObj.getString("on_off");
                    JSONArray menuArray = setCartObj.getJSONArray("menu");

                    JSONArray cartArray = cartObj.getJSONArray(getResources().getString(R.string.CartJsonArray));
                    cartTotal = 0;
                    cartArrayList = new ArrayList<>();
                    for (int i = 0; i < cartArray.length(); i++) {
                        JSONObject cartItemObj = cartArray.getJSONObject(i);
                        String MenuId = cartItemObj.getString("MenuId");
                        String ProductId = cartItemObj.getString("ProductId");
                        String MenuOnOff="";
                        for (int j = 0; j < menuArray.length(); j++) {
                            JSONObject menuObj = menuArray.getJSONObject(j);
                            if (MenuId.equals(menuObj.getString("menu_id"))) {
                                MenuOnOff = menuObj.getString("menu_onoff");
                                int OfferPer = menuObj.getString("offer_percent").isEmpty() ? 0 :
                                        Integer.parseInt(menuObj.getString("offer_percent"));
                                String MenuImg = menuObj.getString("menu_img").replaceAll(" ", "_");
                                String ProductImg = menuObj.getString("prod_img").replaceAll(" ", "_");

                                JSONArray AddOnsArray = menuObj.getJSONArray("addon");
                                ArrayList<JSONObject> AddOnsList = new ArrayList<>();
                                JSONArray addOnCartArray = cartItemObj.getJSONArray(getResources().getString(R.string.AddOnsJsonArray));
                                if(AddOnsArray.length() != 0) {
                                    for(int a = 0 ; a < addOnCartArray.length() ; a++){
                                        JSONObject addOnCartObj = addOnCartArray.getJSONObject(a);
                                        for (int l = 0; l < AddOnsArray.length(); l++) {
                                            JSONObject AddOnsObj = AddOnsArray.getJSONObject(l);
                                            if(addOnCartObj.getString("addon_id").equals(AddOnsObj.getString("addon_id"))){
                                                AddOnsObj.put("MenuId", MenuId);
                                                float GSTPer = Float.parseFloat(AddOnsObj.getString("addon_gst_perc").equals("") ? "0"
                                                        : AddOnsObj.getString("addon_gst_perc"));
                                                float GSTPrice = Float.parseFloat(String.valueOf((GSTPer * AddOnsObj.getDouble("sell_price"))/100));
                                                AddOnsObj.put("GSTPrice", GSTPrice);
                                                AddOnsObj.put("TotalPackAmt", AddOnsObj.getDouble("addon_pack_charge") * addOnCartObj.getInt("Quantity"));
                                                AddOnsObj.put("Quantity", addOnCartObj.getString("Quantity"));
                                                AddOnsObj.put("QtyPrice", addOnCartObj.getInt("Quantity") * AddOnsObj.getInt("sell_price"));
                                                AddOnsList.add(AddOnsObj);

                                                cartTotal += Float.parseFloat(String.valueOf(addOnCartObj.getInt("Quantity") * AddOnsObj.getInt("sell_price")));
                                            }
                                        }
                                    }
                                }

                                CartUtils utils = new CartUtils();
                                utils.setVendorName(setCartObj.getString("vender_name"));
                                utils.setMenuId(menuObj.getString("menu_id"));
                                utils.setProductId(ProductId);
                                utils.setProductName(menuObj.getString("prod_name"));
                                utils.setSellingPrice(menuObj.getString("selling_price"));
                                utils.setOfferPer(OfferPer);
                                utils.setProductImg(MenuImg);
                                utils.setProductImgDefault(ProductImg);
                                utils.setUnit(menuObj.getString("unit_name"));
                                utils.setVegType(menuObj.getString("veg_type"));
                                utils.setProdIngredients(menuObj.getString("prod_ingredients"));
                                utils.setGST_Perc(menuObj.getString("gst_percent"));
                                utils.setPack_charge(menuObj.getString("packaging_charge").equals("") ? "0" : menuObj.getString("packaging_charge"));
                                utils.setIsGST(menuObj.getString("is_gst"));
                                utils.setAddOns(AddOnsList.size() != 0);
                                utils.setAddOnsList(AddOnsList);

                                utils.setQuantity(cartItemObj.getString("Quantity"));
                                int QtyPrice = Math.round(Float.parseFloat(cartItemObj.getString("Quantity"))
                                        * Float.parseFloat(menuObj.getString("selling_price")));
                                utils.setQtyPrice(String.valueOf(QtyPrice));
                                cartArrayList.add(utils);

                                cartTotal += QtyPrice;
                            }
                        }
                        if (VendorOnOff.equals("off") || MenuOnOff.equals("off")) {
                            layoutMenuAlert.setVisibility(View.VISIBLE);
                            layoutCheckout.setVisibility(View.GONE);
                        }else {
                            layoutMenuAlert.setVisibility(View.GONE);
                            layoutCheckout.setVisibility(View.VISIBLE);
                        }

                        cartAdapter = new CartAdapter(AddToCartActivity.this, cartArrayList);
                        rcCart.setAdapter(cartAdapter);
                        layoutNoItems.setVisibility(View.GONE);
                        bottomMenuAdapter.notifyDataSetChanged();
                        //cartAdapter.notifyDataSetChanged();
                        tvCartTotal.setText("₹ " + Math.round(cartTotal) + "/-");
                    }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("GetCartRequest", response);
        }
        , error -> {
            Log.e("GetCartError", error.toString()+"_");
            Log.e("GetCartError", error.getMessage()+"_");
            error.printStackTrace();
        }){
            @Override
            protected Map<String, String> getParams() {
                return postData;
            }
        };
        requestQueue.add(stringRequest);
        Log.e("GetCartRequest", stringRequest.getUrl()+"_"+postData);
    }

    long timeInMilliseconds = 0;
    public void getServerTime(){
        SharedPref sharedPref = new SharedPref(AddToCartActivity.this);
        RequestQueue requestQueue = Volley.newRequestQueue(AddToCartActivity.this, sslCertification.getHurlStack());
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
                            sharedPref.setServerTime((String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)).length() == 1 ? "0"+calendar.get(Calendar.HOUR_OF_DAY) : calendar.get(Calendar.HOUR_OF_DAY)) + ":" + (String.valueOf(calendar.get(Calendar.MINUTE)).length() == 1 ? "0"+calendar.get(Calendar.MINUTE) : calendar.get(Calendar.MINUTE)));
                            setCategory();
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
        }, error -> {
            Log.e("ServiceTime", error.toString());
            Log.e("ServiceTime", error.getMessage());
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
                    EndOrderSlot = EndOrderSlot.substring(EndOrderSlot.lastIndexOf("- ")+1);

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
                            calendar.add(Calendar.DAY_OF_YEAR, 1);
                            if( DeliveryDate.equals(dateFormat.format(calendar.getTime()))) {
                                layoutCartAlert.setVisibility(View.VISIBLE);
                                layoutCheckout.setVisibility(View.GONE);
                                tvAlertMessage.setText("Sorry for Inconvenience !!\nOrder Placed time for today is Over for Breakfast, We have to clear your cart !!");
                                calendar.add(Calendar.DAY_OF_YEAR, -1);
                            }
                        } else if (current.after(inputParser.parse(categoryArray.get(1).getEndOrderSlot())) && CategoryId.equals("2")
                                && DeliveryDate.equals(dateFormat.format(calendar.getTime()))) {
                            layoutCartAlert.setVisibility(View.VISIBLE);
                            layoutCheckout.setVisibility(View.GONE);
                            tvAlertMessage.setText("Sorry for Inconvenience !!\nOrder Placed time for today is Over for Lunch, We have to clear your cart !!");
                        } else if (current.after(inputParser.parse(categoryArray.get(2).getEndOrderSlot())) && CategoryId.equals("3")
                                && DeliveryDate.equals(dateFormat.format(calendar.getTime()))) {
                            layoutCartAlert.setVisibility(View.VISIBLE);
                            layoutCheckout.setVisibility(View.GONE);
                            tvAlertMessage.setText("Sorry for Inconvenience !!\nOrder Placed time for today is Over for Snacks, We have to clear your cart !!");
                        } else if (current.after(inputParser.parse(categoryArray.get(3).getEndOrderSlot())) && CategoryId.equals("4")
                                && DeliveryDate.equals(dateFormat.format(calendar.getTime()))) {
                            layoutCartAlert.setVisibility(View.VISIBLE);
                            layoutCheckout.setVisibility(View.GONE);
                            tvAlertMessage.setText("Sorry for Inconvenience !!\nOrder Placed time for today is Over for Dinner, We have to clear your cart !!");
                        } else {
                            layoutCartAlert.setVisibility(View.GONE);
                            layoutCheckout.setVisibility(View.VISIBLE);
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
        }, error -> {
            Log.e("GetCategory", error.toString());
            Log.e("GetCategory", error.getMessage());
            error.printStackTrace();
        });
        requestQueue.add(customRequest);
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {

        if(isTaskRoot()){
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            customToast.showCustomToast(AddToCartActivity.this, "Please click BACK again to exit");

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
        } else
            super.onBackPressed();
    }
}
