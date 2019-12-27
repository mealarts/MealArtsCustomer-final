package com.mealarts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mealarts.Adapters.AddressItemAdapter;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.Helpers.Utils.AddressUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageAddressActivity extends AppCompatActivity {

    RecyclerView rcAddressList;
    TextView tvAddNewAddress;
    ImageView ivBack;

    ArrayList<AddressUtils> AddressList;
    String fromWhere;

    SSLCertification sslCertification = new SSLCertification(ManageAddressActivity.this);
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_address);

        sharedPref = new SharedPref(ManageAddressActivity.this);
        fromWhere = getIntent().getStringExtra("fromWhere");

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> onBackPressed());

        tvAddNewAddress = findViewById(R.id.tvAddNewAddress);

        rcAddressList = findViewById(R.id.rcAddressList);
        rcAddressList.setHasFixedSize(true);
        rcAddressList.setLayoutManager(new LinearLayoutManager(ManageAddressActivity.this));

        tvAddNewAddress.setOnClickListener(v -> {
            Intent intent = new Intent(ManageAddressActivity.this, DeliveryAddressActivity.class);
            intent.putExtra("fromWhere", "ManageAddress");                             //to check redirection of activity.
            intent.putExtra("Action", "edit");                                         //to check redirection of activity.
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getAddressList();
    }

    Map<String, String> postData;
    public void getAddressList(){
        postData = new HashMap<>();
        postData.put("customer_id", sharedPref.getUserId());

        RequestQueue requestQueue = Volley.newRequestQueue(ManageAddressActivity.this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.AddressList, response -> {
            Log.e("AddressResp", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String Status = jsonObject.getString("status");
                if(Status.equals("1")){
                    JSONArray addressArray = jsonObject.getJSONArray("List");
                    AddressList = new ArrayList<>();
                    for(int i = 0 ; i < addressArray.length() ; i++){
                        JSONObject addressObj = addressArray.getJSONObject(i);
                        AddressUtils utils = new AddressUtils();
                        utils.setAddressId(addressObj.getString("add_id"));
                        utils.setAddressType(addressObj.getString("address_type"));
                        utils.setLandmark(addressObj.getString("landmark"));
                        utils.setFlat(addressObj.getString("flat"));
                        utils.setLocation(addressObj.getString("location"));
                        utils.setLatitude(addressObj.getString("latitude"));
                        utils.setLongitude(addressObj.getString("longitude"));
                        AddressList.add(utils);
                    }
                    AddressItemAdapter addressItemAdapter = new AddressItemAdapter(ManageAddressActivity.this, AddressList);
                    rcAddressList.setAdapter(addressItemAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

        }){
            @Override
            protected Map<String, String> getParams() {
                return postData;
            }
        };
        requestQueue.add(stringRequest);
        Log.e("AddressReq", stringRequest.getUrl());
    }
}
