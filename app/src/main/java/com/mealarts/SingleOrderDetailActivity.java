package com.mealarts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.mealarts.Adapters.OrderItemAdapter;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CheckExtras;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.Helpers.Utils.MenuUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("SetTextI18n")
public class SingleOrderDetailActivity extends AppCompatActivity {

    TextView tvOrderId, tvOrderStatus, tvTotalItem, tvOrderTotal, tvOrderDate, tvDeliveryDate, tvAddressType, tvAddress,
            tvItemTotal, tvDeliveryCharge, tvPackingCharge, tvVoucherDisc, tvPaidType, tvGrandTotal,tvGstAmount;
    LinearLayout layoutLoader;
    RecyclerView rcOrderItems;
    ImageView ivBack, ivLoader;
    SSLCertification sslCertification = new SSLCertification(SingleOrderDetailActivity.this);
    CheckExtras connection;

    DecimalFormat df;//hgd
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_order_detail);

        df = new DecimalFormat("0.00");//hgd
        sharedPref = new SharedPref(SingleOrderDetailActivity.this);

        connection = new CheckExtras(SingleOrderDetailActivity.this);

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> onBackPressed());

        layoutLoader = findViewById(R.id.layoutLoader);
        ivLoader = findViewById(R.id.ivLoader);
        Glide.with(SingleOrderDetailActivity.this).asGif().load(R.drawable.mealarts_loader).into(ivLoader);
        layoutLoader.setVisibility(View.VISIBLE);

        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvTotalItem = findViewById(R.id.tvTotalItem);
        tvOrderTotal = findViewById(R.id.tvOrderTotal);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvDeliveryDate = findViewById(R.id.tvDeliveryDate);
        tvAddressType = findViewById(R.id.tvAddressType);
        tvAddress = findViewById(R.id.tvAddress);
        tvItemTotal = findViewById(R.id.tvItemTotal);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvPackingCharge = findViewById(R.id.tvPackingCharge);
        tvVoucherDisc = findViewById(R.id.tvVoucherDisc);
        tvPaidType = findViewById(R.id.tvPaidType);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);
        tvGstAmount = findViewById(R.id.tvGstAmount);

        rcOrderItems = findViewById(R.id.rcOrderItems);
        rcOrderItems.setHasFixedSize(true);
        rcOrderItems.setLayoutManager(new LinearLayoutManager(SingleOrderDetailActivity.this));

        getSingleOrder();
    }

    ArrayList<MenuUtils> menuList;
    HashMap<String, String> postData;
    public void getSingleOrder(){
        postData = new HashMap<>();
        postData.put("checkout_id", getIntent().getStringExtra("CheckoutId"));

        RequestQueue requestQueue = Volley.newRequestQueue(SingleOrderDetailActivity.this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.SingleOrderDetails, response -> {
            Log.e("OrderResp", response);

            try {
                JSONObject orderObj = new JSONObject(response);
                tvOrderId.setText("order #"+orderObj.getString("checkout_id"));
                String upperString = orderObj.getString("order_status").substring(0,1).toUpperCase() + orderObj.getString("order_status").substring(1);
                tvOrderStatus.setText(upperString);
                /*if(!sharedPref.getCurrentOrderId().equals(getIntent().getStringExtra("CheckoutId")))
                    sharedPref.setCurrentOrderId(getIntent().getStringExtra("CheckoutId"));*/
                tvOrderTotal.setText("₹ "+ orderObj.getString("grand_total")+" /-");
                tvOrderDate.setText("Order placed on "+ orderObj.getString("order_place_date"));
                tvDeliveryDate.setText("Order delivered by "+ orderObj.getString("order_delivery_date") +" between "+ orderObj.getString("time_slot"));
                tvAddressType.setText(orderObj.getString("address_type"));
                tvAddress.setText(orderObj.getString("detail_address")+", "+orderObj.getString("address"));
                tvItemTotal.setText("₹ "+orderObj.getString("total_amount")+" /-");
                tvDeliveryCharge.setText("₹ "+orderObj.getString("del_charge")+" /-");
                tvPackingCharge.setText("₹ "+orderObj.getString("pack_charge")+" /-");
                tvVoucherDisc.setText("- ₹ "+orderObj.getString("voucher_discount")+" /-");
                tvGrandTotal.setText("₹ "+orderObj.getString("grand_total")+" /-");
                tvPaidType.setText("Paid Via "+orderObj.getString("payment_type"));
                if(orderObj.getString("total_gst").isEmpty())
                    tvGstAmount.setText("₹ 0 /-");
                else
                    tvGstAmount.setText("₹ "+df.format(Float.parseFloat(orderObj.getString("total_gst")))+" /-");


                JSONArray menuArray = orderObj.getJSONArray("Menu");
                menuList = new ArrayList<>();
                for(int i = 0 ; i < menuArray.length() ; i++){
                    JSONObject menuObj = menuArray.getJSONObject(i);
                    MenuUtils menuUtils = new MenuUtils();
                    menuUtils.setProductName(menuObj.getString("prod_name"));
                    menuUtils.setVegType(menuObj.getString("veg_type"));
                    menuUtils.setQty(menuObj.getString("quantity"));
                    menuUtils.setSellingPrice(menuObj.getString("price"));
                    menuUtils.setGst_price(menuObj.getString("gst_price"));
                    menuUtils.setGST_Perc(menuObj.getString("gst_percent"));
                    menuList.add(menuUtils);
                }
                OrderItemAdapter orderItemAdapter = new OrderItemAdapter(SingleOrderDetailActivity.this, menuList);
                rcOrderItems.setAdapter(orderItemAdapter);
                tvTotalItem.setText(menuArray.length()+ " Items");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            layoutLoader.setVisibility(View.GONE);
        }, error -> {
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
}
