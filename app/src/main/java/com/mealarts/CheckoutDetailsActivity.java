package com.mealarts;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CheckExtras;
import com.mealarts.Helpers.CustomToast;
import com.mealarts.Helpers.SharedPref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckoutDetailsActivity extends AppCompatActivity {

    RadioGroup rgSlotTime;
    ImageView ivBack, ivLoader;
    Button btnContinue;
    TextView tvCustomerName, tvAddressType, tvFullAddress, tvEmail, tvContact, tvCategory;
    LinearLayout layoutLoader;
    SSLCertification sslCertification = new SSLCertification(CheckoutDetailsActivity.this);
    SharedPref sharedPref;
    CheckExtras connection;
    CustomToast customToast = new CustomToast();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_details);

        sharedPref = new SharedPref(CheckoutDetailsActivity.this);
        connection = new CheckExtras(CheckoutDetailsActivity.this);

        rgSlotTime = findViewById(R.id.rgSlotTime);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvEmail = findViewById(R.id.tvEmail);
        tvContact = findViewById(R.id.tvContact);
        tvFullAddress = findViewById(R.id.tvFullAddress);
        tvAddressType = findViewById(R.id.tvAddressType);
        tvCategory = findViewById(R.id.tvCategory);
        btnContinue = findViewById(R.id.btnContinue);
        ivBack = findViewById(R.id.ivBack);
        ivLoader = findViewById(R.id.ivLoader);
        layoutLoader = findViewById(R.id.layoutLoader);

        ivBack.setOnClickListener(v -> onBackPressed());

        try {
            JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
            JSONObject userObj = cartObj.getJSONObject(getResources().getString(R.string.UserJSON));
            tvCustomerName.setText(userObj.getString("UserName"));
            tvEmail.setText(userObj.getString("Email"));
            tvContact.setText("Mobile:" + userObj.getString("Mobile"));
            JSONObject locationObj = cartObj.getJSONObject(getResources().getString(R.string.LocationJson));
            tvFullAddress.setText(locationObj.getString("FlatHouseNo") +", "
                    + (locationObj.has("Landmark") ? locationObj.getString("Landmark") : "") + ", "
                    + locationObj.getString("Location")
                    + ((locationObj.has("Pincode")
                    && !locationObj.getString("Location").contains(locationObj.getString("Pincode")))
                    ? ", " + locationObj.getString("Pincode") : ""));
            tvAddressType.setText(locationObj.getString("AddressType"));

            JSONObject cartJsonObject = cartObj.getJSONObject(getResources().getString(R.string.CartJsonObj));
            if(cartJsonObject.has("CategoryId"))
                getDeliverySlot();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnContinue.setOnClickListener(v -> {
            Log.e("RGSelected", String.valueOf(rgSlotTime.getCheckedRadioButtonId()));
            if(Math.abs(rgSlotTime.getCheckedRadioButtonId()) < 100)
                customToast.showCustomToast(CheckoutDetailsActivity.this, "Please Select Any One Delivery Slot Time");
            else {
                try {
                    JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                    JSONObject userObj = cartObj.getJSONObject(getResources().getString(R.string.LocationJson));
                    int selectedId = rgSlotTime .getCheckedRadioButtonId();

                    RadioButton rbTimeSlot = findViewById(selectedId);
                    userObj.put("TimeSlot", rbTimeSlot.getText());
                    Log.e("RGSelected", String.valueOf(rbTimeSlot.getText()));
                    sharedPref.setUserCart(cartObj.toString());
                    Log.e("Cart", sharedPref.getUserCart());

                    startActivity(new Intent(CheckoutDetailsActivity.this, CheckoutActivity.class));
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    HashMap<String, String> postData;
    ArrayList<String> DeliverySlot;
    @SuppressLint("SetTextI18n")
    public void getDeliverySlot(){
        postData = new HashMap<>();
        try {
            postData.put("category_id",new JSONObject(sharedPref.getUserCart())
                    .getJSONObject(getResources().getString(R.string.CartJsonObj))
                    .getString("CategoryId"));

            switch (new JSONObject(sharedPref.getUserCart())
                    .getJSONObject(getResources().getString(R.string.CartJsonObj))
                    .getString("CategoryId")) {
                case "1":
                    tvCategory.setText("( Breakfast ) : ");
                    break;
                case "2":
                    tvCategory.setText("( Lunch ) : ");
                    break;
                case "3":
                    tvCategory.setText("( Snacks ) : ");
                    break;
                case "4":
                    tvCategory.setText("( Dinner ) : ");
                    break;
            }

            RequestQueue requestQueue = Volley.newRequestQueue(CheckoutDetailsActivity.this, sslCertification.getHurlStack());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.DeliverySlot, response -> {
                try {
                    JSONObject jsonSlotObject = new JSONObject(response);
                    JSONArray jsonSlotArray = jsonSlotObject.getJSONArray("slots");
                    DeliverySlot = new ArrayList<>();
                    for(int i = 0 ; i < jsonSlotArray.length() ; i++){
                        JSONObject jsonObject = jsonSlotArray.getJSONObject(i);
                        DeliverySlot.add(jsonObject.getString("slot"));
                    }
                    createRadioButton(DeliverySlot);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }, error -> {
                Log.e("DeliverySlot", error.toString()+"_");
                Log.e("DeliverySlot", error.getMessage()+"_");
                error.printStackTrace();
            }){
                @Override
                protected Map<String, String> getParams() {
                    return postData;
                }
            };
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        layoutLoader.setVisibility(View.GONE);
    }

    private void createRadioButton(ArrayList<String> DeliverySlot) {
        final RadioButton[] rb = new RadioButton[DeliverySlot.size()];
        rgSlotTime.setOrientation(RadioGroup.VERTICAL);
        for(int i=0; i<DeliverySlot.size(); i++){
            rb[i]  = new RadioButton(this);
            rb[i].setText(DeliverySlot.get(i));
            rb[i].setId(i + 100);
            rgSlotTime.addView(rb[i]);
        }

    }
}
