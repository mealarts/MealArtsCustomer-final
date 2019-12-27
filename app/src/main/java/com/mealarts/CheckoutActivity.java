package com.mealarts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CheckExtras;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.Helpers.Utils.BottomMenuUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    ImageView ivBack, ivLoader, ivCancelVoucher;
    LinearLayout layoutLoader, layoutAppliedVoucher;
    RecyclerView rcLowerMenu;
    ArrayList<BottomMenuUtils> bottomMenuArray;
    public static BottomMenuAdapter bottomMenuAdapter;
    Integer[] icons = {R.drawable.home, R.drawable.menu_card, R.drawable.account, R.drawable.cart};

    TextView tvCartAmount, tvGstAmount, tvPackingCharge, tvDeliveryCharge, tvOfferDiscount, tvTotalAmount,
            tvVoucher, tvAppliedVoucher, tvOfferPer;
    Button btnFinalCheckout;
    EditText edtChefMessage, edtVoucher;
    HashMap<String, String> postData;
    float PayableAmount = 0;

    SharedPref sharedPref;
    CheckExtras connection;
    SSLCertification sslCertification = new SSLCertification(CheckoutActivity.this);

    DecimalFormat df;//hgd

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        df = new DecimalFormat("0.00");//hgd

        sharedPref = new SharedPref(CheckoutActivity.this);
        connection = new CheckExtras(CheckoutActivity.this);

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> onBackPressed());

        layoutLoader = findViewById(R.id.layoutLoader);
        ivLoader = findViewById(R.id.ivLoader);
        Glide.with(CheckoutActivity.this).asGif().load(R.drawable.mealarts_loader).into(ivLoader);
        layoutLoader.setVisibility(View.VISIBLE);

//----------------------------------Bottom Tab Menu----------------------------------//
        rcLowerMenu = findViewById(R.id.rcLowerMenu);
        rcLowerMenu.setHasFixedSize(true);
        rcLowerMenu.setLayoutManager(new GridLayoutManager(CheckoutActivity.this, 4));
        setBottomMenu();

//----------------------------------Checkout Cart----------------------------------//

        tvCartAmount = findViewById(R.id.tvCartAmount);
        tvGstAmount = findViewById(R.id.tvGstAmount);
        tvPackingCharge = findViewById(R.id.tvPackingCharge);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvOfferDiscount = findViewById(R.id.tvOfferDiscount);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        edtChefMessage = findViewById(R.id.edtChefMessage);
        tvVoucher = findViewById(R.id.tvVoucher);
        layoutAppliedVoucher = findViewById(R.id.layoutAppliedVoucher);
        tvAppliedVoucher = findViewById(R.id.tvAppliedVoucher);
        tvOfferPer = findViewById(R.id.tvOfferPer);
        ivCancelVoucher = findViewById(R.id.ivCancelVoucher);
        btnFinalCheckout = findViewById(R.id.btnFinalCheckout);

        try {
            JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
            JSONObject cartJsonObj = cartObj.getJSONObject(getResources().getString(R.string.CartJsonObj));
            cartJsonObj.put("Voucher", "");
            cartJsonObj.put("VoucherDisc", "0");
            //cartJsonObj.put("TotalAmount", Math.round(total));
            sharedPref.setUserCart(String.valueOf(cartObj));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tvVoucher.setOnClickListener(v -> {
            startActivity(new Intent(CheckoutActivity.this, VoucherListActivity.class));
        });

        ivCancelVoucher.setOnClickListener(v -> {
            try {
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                JSONObject cartJsonObj = cartObj.getJSONObject(getResources().getString(R.string.CartJsonObj));
                cartJsonObj.put("Voucher", "");
                cartJsonObj.put("VoucherDisc", "0");
                cartJsonObj.put("DiscType", "");
                cartJsonObj.put("DiscValue", "0");
                //cartJsonObj.put("TotalAmount", Math.round(total));
                sharedPref.setUserCart(String.valueOf(cartObj));

                onResume();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        hideKeyboard(CheckoutActivity.this);
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
        bottomMenuAdapter = new BottomMenuAdapter(CheckoutActivity.this, bottomMenuArray);
        rcLowerMenu.setAdapter(bottomMenuAdapter);
        bottomMenuAdapter.setListener((position, imgView) -> {
            sharedPref.setPos(position);
            if (position == 0) {
                startActivity(new Intent(CheckoutActivity.this, MainActivity.class).putExtra("FromSplash", false));
            }
            else if (position == 1) startActivity(new Intent(CheckoutActivity.this, MenuListActivity.class));
            else if (position == 2) {
                if(sharedPref.getUserId().equals(""))
                    startActivity(new Intent(CheckoutActivity.this, LogInActivity.class));
                else startActivity(new Intent(CheckoutActivity.this, ProfileActivity.class));
            }else if (position == 3) startActivity(new Intent(CheckoutActivity.this, AddToCartActivity.class));

            finish();
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        try {
            JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
            JSONObject userObj = cartObj.getJSONObject(getResources().getString(R.string.UserJSON));
            String UserName = userObj.getString("UserName");
            String Email = userObj.getString("Email");
            String Mobile = userObj.getString("Mobile");

            JSONObject locationJsonObj = cartObj.getJSONObject(getResources().getString(R.string.LocationJson));
            String Latitude = locationJsonObj.getString("Latitude");
            String Longitude = locationJsonObj.getString("Longitude");
            String Location = locationJsonObj.getString("Location");
            //String Pincode = locationJsonObj.getString("Pincode");
            String FlatHouseNo = locationJsonObj.getString("FlatHouseNo");
            String Landmark = locationJsonObj.getString("Landmark");
            String AddressType = locationJsonObj.getString("AddressType");
            String DetailAddress = FlatHouseNo +" , " + Landmark;
            //String City = locationJsonObj.getString("City");
            //String Area = locationJsonObj.getString("Area");
            String TimeSlot = locationJsonObj.getString("TimeSlot");

            JSONObject cartJsonObj = cartObj.getJSONObject(getResources().getString(R.string.CartJsonObj));
            String VendorId = cartJsonObj.getString("VendorId");
            String CategoryId = cartJsonObj.getString("CategoryId");
            String DeliveryDate = cartJsonObj.getString("DeliveryDate");
//            String PackingCharge = cartJsonObj.getString("PackingCharge");
            String PackingCharge = String.valueOf(Math.round(Float.parseFloat(cartJsonObj.getString("totalPackCharge"))));
            tvPackingCharge.setText("₹ " + PackingCharge+"/-");
            String DeliveryCharge = cartJsonObj.getString("DeliveryCharge");
            tvDeliveryCharge.setText("₹ " + DeliveryCharge+"/-");
            String DistanceKM = cartJsonObj.getString("DistanceKM");
            String TotalAmount = cartJsonObj.getString("TotalAmount");
            //hgd
            String gstTotal=cartJsonObj.getString("totalGST");
            tvGstAmount.setText("₹ " + df.format(Float.parseFloat(gstTotal))+"/-");

            String VoucherDisc = cartJsonObj.has("VoucherDisc") ? cartJsonObj.getString("VoucherDisc") : "0";
            String Voucher = cartJsonObj.has("Voucher") ? cartJsonObj.getString("Voucher") : "";
            if(cartJsonObj.has("VoucherDisc") && cartJsonObj.getInt("VoucherDisc") > 0){
                layoutAppliedVoucher.setVisibility(View.VISIBLE);
                tvAppliedVoucher.setText(Voucher);
            }else {
                layoutAppliedVoucher.setVisibility(View.GONE);
                tvAppliedVoucher.setText("");
            }
            tvOfferDiscount.setText("- ₹ " + VoucherDisc+"/-");
            String DiscountType = cartJsonObj.has("DiscType") ? cartJsonObj.getString("DiscType") : "";
            String DiscountValue = cartJsonObj.has("DiscValue")
                    ? (DiscountType.equals("Discount") ? cartJsonObj.getString("DiscValue") +"%"
                        : "₹ "+cartJsonObj.getString("DiscValue") + "/-")
                    : "0";
            tvOfferPer.setText("( "+DiscountValue + " OFF )");

            tvCartAmount.setText("₹ " + TotalAmount+"/-");

            PayableAmount = Integer.parseInt(TotalAmount) + Float.parseFloat(PackingCharge)+Float.parseFloat(gstTotal)
                    + Integer.parseInt(DeliveryCharge) - Float.parseFloat(VoucherDisc);
            tvTotalAmount.setText("₹ " + Math.round(PayableAmount)+"/-");

            StringBuilder MenuIds = new StringBuilder();
            StringBuilder Price = new StringBuilder();
            StringBuilder ProdUnit = new StringBuilder();
            StringBuilder Qty = new StringBuilder();
            StringBuilder ProdIds = new StringBuilder();
            StringBuilder ProdName = new StringBuilder();
            StringBuilder ProdImg = new StringBuilder();
            //hgd
            StringBuilder gstPrice=new StringBuilder();

            StringBuilder AddOnsId = new StringBuilder();
            StringBuilder AddOnsPrice = new StringBuilder();
            StringBuilder AddOnsProdUnit = new StringBuilder();
            StringBuilder AddOnsQuantity = new StringBuilder();
            StringBuilder AddOnsName = new StringBuilder();
            StringBuilder AddOnsImg = new StringBuilder();
            StringBuilder AddOnsGstPrice = new StringBuilder();

            JSONArray cartArray = cartObj.getJSONArray(getResources().getString(R.string.CartJsonArray));
            for(int i = 0 ; i < cartArray.length() ; i++){
                JSONObject cartItemObj = cartArray.getJSONObject(i);
                //String VendorName = cartItemObj.getString("VendorName");
                String MenuId = cartItemObj.getString("MenuId");
                String ProductId = cartItemObj.getString("ProductId");
                String ProductName = cartItemObj.getString("ProductName");
                String SellingPrice = cartItemObj.getString("SellingPrice");
                String ProductImg = cartItemObj.getString("ProductImg");
                String Unit = cartItemObj.getString("Unit");
                //String VegType = cartItemObj.getString("VegType");
                String Quantity = cartItemObj.getString("Quantity");
                String QtyPrice = cartItemObj.getString("QtyPrice");
                String gstp=cartItemObj.getString("gst_amt");

                JSONArray addOnsArray = cartItemObj.getJSONArray(getResources().getString(R.string.AddOnsJsonArray));
                for(int a = 0 ; a < addOnsArray.length() ; a++){
                    JSONObject addOnsObj = addOnsArray.getJSONObject(a);

                    AddOnsId.append(addOnsObj.getString("addon_id")).append(",");
                    AddOnsPrice.append(addOnsObj.getString("QtyPrice")).append(",");
                    AddOnsProdUnit.append(addOnsObj.getString("addon_unit")).append(",");
                    AddOnsQuantity.append(addOnsObj.getString("Quantity")).append(",");
                    AddOnsName.append(addOnsObj.getString("addon_name")).append(",");
                    AddOnsImg.append(addOnsObj.getString("image")).append(",");
                    AddOnsGstPrice.append(addOnsObj.getString("GSTPrice")).append(",");
                }

                MenuIds.append(MenuId).append(",");
                Price.append(SellingPrice).append(",");
                ProdUnit.append(Unit).append(",");
                Qty.append(Quantity).append(",");
                ProdIds.append(ProductId).append(",");
                ProdName.append(ProductName).append(",");
                ProdImg.append(ProductImg).append(",");
                gstPrice.append(gstp).append(",");
            }
            layoutLoader.setVisibility(View.GONE);

            btnFinalCheckout.setOnClickListener(v -> {
                layoutLoader.setVisibility(View.VISIBLE);
                postData = new HashMap<>();
                postData.put("cust_name", UserName);
                postData.put("mobile", Mobile);
                postData.put("email", Email);
                postData.put("detail_address", DetailAddress);
                postData.put("address", Location);
                postData.put("address_type", AddressType);
                postData.put("msg_for_chef", edtChefMessage.getText().toString().trim());
                postData.put("category_id", CategoryId);
                postData.put("distance", DistanceKM);
                postData.put("latitude", Latitude);
                postData.put("longitude", Longitude);
                postData.put("pincode", "");
                postData.put("city", "");
                postData.put("time_slot", TimeSlot);
                postData.put("customer_id", sharedPref.getUserId());
                postData.put("guest_id","0");
                postData.put("total_amount", TotalAmount);
                postData.put("order_delivery_date", DeliveryDate);
                postData.put("menu_id", MenuIds.substring(0,MenuIds.lastIndexOf(",")));
                postData.put("price", Price.substring(0,Price.lastIndexOf(",")));
                postData.put("prod_unit", ProdUnit.substring(0,ProdUnit.lastIndexOf(",")));
                postData.put("quantity", Qty.substring(0,Qty.lastIndexOf(",")));
                postData.put("prod_name", ProdName.substring(0,ProdName.lastIndexOf(",")).trim());
                postData.put("prod_img",ProdImg.substring(0,ProdImg.lastIndexOf(",")));
                postData.put("vendor_id", VendorId);
                postData.put("gst_price",gstPrice.substring(0,gstPrice.lastIndexOf(",")));
                postData.put("addon_id",AddOnsId.substring(0,AddOnsId.lastIndexOf(",")));
                postData.put("addon_name",AddOnsName.substring(0,AddOnsName.lastIndexOf(",")));
                postData.put("addon_price",AddOnsPrice.substring(0,AddOnsPrice.lastIndexOf(",")));
                postData.put("addon_quantity",AddOnsQuantity.substring(0,AddOnsQuantity.lastIndexOf(",")));
                postData.put("addon_gst_price",AddOnsGstPrice.substring(0,AddOnsGstPrice.lastIndexOf(",")));
                postData.put("addon_img",AddOnsImg.substring(0,AddOnsImg.lastIndexOf(",")));

                RequestQueue requestQueue = Volley.newRequestQueue(CheckoutActivity.this, sslCertification.getHurlStack());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.CheckoutOrder, response -> {
                    Log.e("CheckoutResp", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String Status = jsonObject.getString("status");
                        if(Status.equals("success")){
                            layoutLoader.setVisibility(View.GONE);
                            String CheckoutId = jsonObject.getString("checkout_id");
                            Intent intent = new Intent(CheckoutActivity.this, PayTMPaymentActivity.class);
                            intent.putExtra("OrderId", CheckoutId);
                            intent.putExtra("DeliveryCharge", DeliveryCharge);
                            intent.putExtra("totalPackCharge", PackingCharge);
                            intent.putExtra("TotalAmount", String.valueOf(Math.round(PayableAmount)));
                            intent.putExtra("VendorId", VendorId);
                            intent.putExtra("Voucher", Voucher);
                            intent.putExtra("VoucherDisc", VoucherDisc);
                            intent.putExtra("totalGST",gstTotal);//hgd
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    Log.e("Checkout", error.toString()+"_");
                    Log.e("Checkout", error.getMessage()+"_");
                    error.printStackTrace();
                }){
                    @Override
                    protected Map<String, String> getParams() {
                        return postData;
                    }
                };
                requestQueue.add(stringRequest);
                Log.e("Checkout", stringRequest.getUrl()+"_"+postData.toString());
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
