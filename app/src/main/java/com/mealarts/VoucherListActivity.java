package com.mealarts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mealarts.Adapters.VoucherItemAdapter;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CheckExtras;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.Helpers.Utils.VendorUtils;
import com.mealarts.Helpers.Utils.VoucherUtils;

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
import java.util.Map;

public class VoucherListActivity extends AppCompatActivity {

    ImageView ivBack;
    RecyclerView rcVoucherList;
    public static LinearLayout layoutVoucher, layoutNoVoucher;
    public static TextView tvVoucherCode, tvVoucherMsg, tvChangeVoucher, tvGrabVoucher;
    SSLCertification sslCertification = new SSLCertification(VoucherListActivity.this);
    ArrayList<VoucherUtils> voucherList;
    SharedPref sharedPref;
    CheckExtras connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_list);

        sharedPref = new SharedPref(VoucherListActivity.this);
        connection = new CheckExtras(VoucherListActivity.this);

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> {
            onBackPressed();
        });

        layoutNoVoucher = findViewById(R.id.layoutNoVoucher);
        layoutVoucher = findViewById(R.id.layoutVoucher);
        tvVoucherCode = findViewById(R.id.tvVoucherCode);
        tvVoucherMsg = findViewById(R.id.tvVoucherMsg);
        tvChangeVoucher = findViewById(R.id.tvChangeVoucher);
        tvGrabVoucher = findViewById(R.id.tvGrabVoucher);
        rcVoucherList = findViewById(R.id.rcVoucherList);
        rcVoucherList.setHasFixedSize(true);
        rcVoucherList.setLayoutManager(new LinearLayoutManager(VoucherListActivity.this));

        tvGrabVoucher.setOnClickListener(v -> {
            layoutVoucher.setVisibility(View.GONE);
            onBackPressed();
        });

        tvChangeVoucher.setOnClickListener(v -> {
            try {
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                JSONObject cartJsonObj = cartObj.getJSONObject(getResources().getString(R.string.CartJsonObj));
                cartJsonObj.put("Voucher", "");
                cartJsonObj.put("VoucherDisc", "0");
                cartJsonObj.put("DiscType", "");
                cartJsonObj.put("DiscValue", "0");
                sharedPref.setUserCart(String.valueOf(cartObj));

                layoutVoucher.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        VoucherList();
    }

    Map<String, String> postData;
    public void VoucherList(){
        postData = new HashMap<>();
        postData.put("customer_id", sharedPref.getUserId().isEmpty() ? "0" : sharedPref.getUserId());

        RequestQueue requestQueue = Volley.newRequestQueue(VoucherListActivity.this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.OfferList, response -> {
            Log.e("VoucherResp", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String Status = jsonObject.getString("status");
                if(Status.equals("1")){
                    layoutNoVoucher.setVisibility(View.GONE);
                    JSONArray voucherArray = jsonObject.getJSONArray("offerlist");
                    voucherList = new ArrayList<>();
                    for(int i = 0 ; i < voucherArray.length() ; i++){
                        JSONObject voucherObj = voucherArray.getJSONObject(i);
                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        Date FromDate = inputFormat.parse(voucherObj.getString("from_date"));
                        Date ToDate = inputFormat.parse(voucherObj.getString("to_date"));
                        VoucherUtils voucherUtils = new VoucherUtils();
                        voucherUtils.setVoucherId(voucherObj.getInt("vouchr_id"));
                        voucherUtils.setVoucherCode(voucherObj.getString("voucher_no"));
                        voucherUtils.setDiscountType(voucherObj.getString("discount_type"));
                        voucherUtils.setDiscountValue(voucherObj.getInt("discount_value"));
                        voucherUtils.setVoucherFromDate(dateFormat.format(FromDate));
                        voucherUtils.setVoucherToDate(dateFormat.format(ToDate));
                        voucherUtils.setVoucherMinValue(voucherObj.getInt("min_order_amount"));
                        voucherUtils.setDiscountUpTo(voucherObj.getInt("max_discount_amount"));

                        voucherList.add(voucherUtils);
                    }
                    VoucherItemAdapter voucherItemAdapter = new VoucherItemAdapter(VoucherListActivity.this, voucherList);
                    rcVoucherList.setAdapter(voucherItemAdapter);
                }else layoutNoVoucher.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
                layoutNoVoucher.setVisibility(View.VISIBLE);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("VoucherList", error.toString()+"_");
            Log.e("VoucherList", error.getMessage()+"_");
            error.printStackTrace();
            layoutNoVoucher.setVisibility(View.VISIBLE);
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
