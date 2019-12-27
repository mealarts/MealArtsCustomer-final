package com.mealarts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mealarts.Adapters.AddressListItemAdapter;
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

public class AddressListActivity extends AppCompatActivity {

    RecyclerView rcAddressList;
    LinearLayout layoutSetAddress;
    ImageView ivBack;

    String fromWhere ="Home";
    ArrayList<AddressUtils> AddressList;
    SSLCertification sslCertification = new SSLCertification(AddressListActivity.this);
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        sharedPref = new SharedPref(AddressListActivity.this);
        fromWhere = getIntent().getStringExtra("fromWhere");

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> onBackPressed());

        layoutSetAddress = findViewById(R.id.layoutSetAddress);
        rcAddressList = findViewById(R.id.rcAddressList);
        rcAddressList.setHasFixedSize(true);
        rcAddressList.setLayoutManager(new LinearLayoutManager(AddressListActivity.this));

        layoutSetAddress.setOnClickListener(v -> {
            Intent intent = new Intent(AddressListActivity.this, DeliveryAddressActivity.class);
            intent.putExtra("fromWhere", "ListAddress");                               //to check redirection of activity.
            intent.putExtra("Action", "insert");
            intent.putExtra("fromWhere", fromWhere);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getAddressList();
    }

    @Override
    public void onBackPressed() {
        switch (fromWhere) {
            case "Home":
                startActivity(new Intent(AddressListActivity.this, MainActivity.class)
                        .putExtra("FromSplash", false));
                finish();
                break;
            case "Menu":
                startActivity(new Intent(AddressListActivity.this, MenuListActivity.class)
                        .putExtra("FromWhere", false));
                finish();
                break;
            case "Cart":
                startActivity(new Intent(AddressListActivity.this, CheckoutDetailsActivity.class));
                finish();
                break;
        }
    }

    Map<String, String> postData;
    public void getAddressList(){
        postData = new HashMap<>();
        postData.put("customer_id", sharedPref.getUserId());

        RequestQueue requestQueue = Volley.newRequestQueue(AddressListActivity.this, sslCertification.getHurlStack());
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
                    AddressListItemAdapter addressListItemAdapter = new AddressListItemAdapter(AddressListActivity.this, AddressList);
                    rcAddressList.setAdapter(addressListItemAdapter);
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
