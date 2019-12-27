package com.mealarts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CheckExtras;
import com.mealarts.Helpers.CustomToast;
import com.mealarts.Helpers.PermissionChecker;
import com.mealarts.Helpers.SharedPref;
import com.onesignal.OSPermissionSubscriptionState;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VisitorRegActivity extends AppCompatActivity {

    EditText edtVisContact;
    TextView tvVisLogin;

    SharedPref sharedPrefs;
    CheckExtras connection;
    SSLCertification sslCertification = new SSLCertification(VisitorRegActivity.this);
    PermissionChecker permissionChecker;
    CustomToast customToast = new CustomToast();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_reg);

        sharedPrefs = new SharedPref(VisitorRegActivity.this);
        connection = new CheckExtras(VisitorRegActivity.this);
        permissionChecker = new PermissionChecker(VisitorRegActivity.this);

        edtVisContact = findViewById(R.id.edtVisContact);
        tvVisLogin = findViewById(R.id.tvVisLogin);

        tvVisLogin.setOnClickListener(v -> {
            if(edtVisContact.getText().toString().trim().isEmpty()){
                edtVisContact.setError("Required");
                edtVisContact.requestFocus();
            } else if(edtVisContact.getText().toString().trim().length() < 10){
                edtVisContact.setError("Invalid");
                edtVisContact.requestFocus();
            }else VisitorReg();
        });
    }

    Map<String, String> postData;
    public void VisitorReg(){
        postData = new HashMap<>();
        postData.put("mobile_no", edtVisContact.getText().toString().trim());
        postData.put("otp","");

        RequestQueue requestQueue = Volley.newRequestQueue(VisitorRegActivity.this,sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.VisitorReg, response -> {
            Log.e("VisitorResp", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String Status = jsonObject.getString("status");
                if(Status.equals("0")){
                    JSONObject cartObj = new JSONObject(sharedPrefs.getUserCart());
                    JSONObject userObj;
                    if(cartObj.has(getResources().getString(R.string.UserJSON))) {
                        userObj = cartObj.getJSONObject(getResources().getString(R.string.UserJSON));
                        userObj.put("Mobile", edtVisContact.getText().toString().trim());
                    }else {
                        userObj = new JSONObject();
                        userObj.put("Mobile", edtVisContact.getText().toString().trim());
                        cartObj.put(getResources().getString(R.string.UserJSON), userObj);
                    }

                    sharedPrefs.setUserCart(cartObj.toString());
                    sharedPrefs.setVisitor(true);
                    startActivity(new Intent(VisitorRegActivity.this, MainActivity.class)
                            .putExtra("FromSplash", true));
                    finish();
                }else {
                    String OTP = jsonObject.getString("otp");
                    Intent intent = new Intent(VisitorRegActivity.this, OTPActivity.class);
                    intent.putExtra("mobile", edtVisContact.getText().toString().trim());
                    intent.putExtra("otp", OTP);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("VisitorResp", error.getMessage()+"_");
            Log.e("VisitorResp", error.toString()+"_");
            error.printStackTrace();
        }){
            @Override
            protected Map<String, String> getParams() {
                return postData;
            }
        };
        requestQueue.add(stringRequest);
        Log.e("VisitorResp", stringRequest.getUrl()+"_"+postData);
    }
}
