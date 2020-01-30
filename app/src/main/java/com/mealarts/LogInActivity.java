package com.mealarts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.google.gson.JsonObject;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CheckExtras;
import com.mealarts.Helpers.CustomToast;
import com.mealarts.Helpers.PermissionChecker;
import com.mealarts.Helpers.SharedPref;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity {

    EditText edtUserName/*, edtPassword*/;
    TextView tvLogin, tvCreateAccount, tvForgetPassword;
    Boolean FromCart = false;
    LinearLayout layoutLoader;
    ImageView ivLoader;

    SharedPref sharedPrefs;
    CheckExtras connection;
    SSLCertification sslCertification = new SSLCertification(LogInActivity.this);
    PermissionChecker permissionChecker;
    OSPermissionSubscriptionState status;
    CustomToast customToast = new CustomToast();
    String fromWhere="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        try {
            status = OneSignal.getPermissionSubscriptionState();
            status.getPermissionStatus().getEnabled();

            status.getSubscriptionStatus().getSubscribed();
            status.getSubscriptionStatus().getUserSubscriptionSetting();
            status.getSubscriptionStatus().getUserId();
            status.getSubscriptionStatus().getPushToken();
        }catch (Exception e){
            e.printStackTrace();
        }
//
//        Log.e("Token", status.getSubscriptionStatus().getPushToken());
//        Log.e("Token", status.getSubscriptionStatus().getUserId());

        FromCart = getIntent().getBooleanExtra("FromCart", false);
        fromWhere=getIntent().getStringExtra("fromWhere");

        sharedPrefs = new SharedPref(LogInActivity.this);
        connection = new CheckExtras(LogInActivity.this);
        permissionChecker = new PermissionChecker(LogInActivity.this);

        layoutLoader = findViewById(R.id.layoutLoader);
        ivLoader = findViewById(R.id.ivLoader);
        Glide.with(LogInActivity.this).asGif().load(R.drawable.mealarts_loader).into(ivLoader);

        edtUserName = findViewById(R.id.edtUserName);
        //edtPassword = findViewById(R.id.edtPassword);
        tvLogin = findViewById(R.id.tvLogin);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        tvForgetPassword = findViewById(R.id.tvForgetPassword);

        try {
            JSONObject cartObj = new JSONObject(sharedPrefs.getUserCart());
            JSONObject userCart = cartObj.getJSONObject(getResources().getString(R.string.UserJSON));
            edtUserName.setText(userCart.getString("Mobile"));
            //edtUserName.setFocusableInTouchMode(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tvLogin.setOnClickListener(v -> getValidate());

        tvCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LogInActivity.this, RegistrationActivity.class);
            intent.putExtra("FromCart", FromCart);
            startActivity(intent);
            finish();
        });

        tvForgetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LogInActivity.this, PasswordActivity.class);
            intent.putExtra("FromCart", FromCart);
            intent.putExtra("Procedure", "forget");
            startActivity(intent);
            finish();
        });
    }

    public void getValidate(){

        String UserName = edtUserName.getText().toString();
        //String Password = edtPassword.getText().toString();

        if(UserName.equals("")) {
            edtUserName.requestFocus();
            edtUserName.setError("Required");
            customToast.showCustomToast(LogInActivity.this, "User Name Required");
        }else if(UserName.length() < 10) {
            edtUserName.requestFocus();
            edtUserName.setError("Invalid");
            customToast.showCustomToast(LogInActivity.this, "Invalid Mobile Number");
        }/*else if(Password.equals("")) {
            edtPassword.requestFocus();
            edtPassword.setError("Required", getResources().getDrawable(R.drawable.required_error));
            customToast.showCustomToast(LogInActivity.this, "Password Required");
        }*/else{
            layoutLoader.setVisibility(View.VISIBLE);
            getLogin(UserName/*, Password*/);
        }
    }

    HashMap<String, String> postData;
    public void getLogin(String UserName/*, String Password*/){
        postData = new HashMap<>();
        postData.put("mobile", UserName);
        //postData.put("password", Password);
        postData.put("token", status.getSubscriptionStatus().getUserId());

        RequestQueue requestQueue = Volley.newRequestQueue(this, sslCertification.getHurlStack());
        StringRequest customRequest = new StringRequest(Request.Method.POST, URLServices.Login, response -> {
            Log.e("response", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String Status = jsonObject.getString("status");
                if(Status.equals("1")) {
                    String CustomerId = jsonObject.getString("customer_id");
                    if(jsonObject.has("checkout_id"))
                        sharedPrefs.setCurrentOrderId(jsonObject.getString("checkout_id"));
                    else sharedPrefs.setCurrentOrderId("0");
                    String UserName1 = jsonObject.getString("cust_name");
                    String Email = jsonObject.getString("email");
                    String Mobile = jsonObject.getString("mobile");
                    String token = jsonObject.getString("token");

                    sharedPrefs.setUserId(CustomerId);

                    JSONObject cartObj = new JSONObject(sharedPrefs.getUserCart());
                    if(cartObj.has(getResources().getString(R.string.UserJSON))) {
                        JSONObject userObj = cartObj.getJSONObject(getResources().getString(R.string.UserJSON));
                        userObj.put("UserName", UserName1);
                        userObj.put("Email", Email);
                        userObj.put("Mobile", Mobile);
                        userObj.put("Token", token);
                    }else {
                        JSONObject userObj = new JSONObject();
                        userObj.put("UserName", UserName1);
                        userObj.put("Email", Email);
                        userObj.put("Mobile", Mobile);
                        userObj.put("Token", token);
                        cartObj.put(getResources().getString(R.string.UserJSON),userObj);
                    }
                    sharedPrefs.setUserCart(cartObj.toString());
                    customToast.showCustomToast(LogInActivity.this, "Login Successfully");
                    Log.d("/*lia","fwhe"+fromWhere);
                    if (FromCart) {
                        Intent intent = new Intent(LogInActivity.this, CheckoutDetailsActivity.class);
                        intent.putExtra("fromWhere", "Cart");
                        startActivity(intent);
                        finish();
                    }
                    else if(fromWhere!=null){
                        if(fromWhere.equals("Menu")){
                            Intent intent = new Intent(LogInActivity.this, AddressListActivity.class);
                            intent.putExtra("fromWhere", "Menu");
                            startActivity(intent);
                        }
                    }
                    else {
                        onBackPressed();
                        finish();
                    }
                }else {
                    String Message = jsonObject.getString("message");
                    customToast.showCustomToast(LogInActivity.this, Message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            layoutLoader.setVisibility(View.GONE);
        }, error -> {
            Log.e("Login", error.toString()+"_");
            Log.e("Login", error.getMessage()+"_");
            error.printStackTrace();
        }){
            @Override
            protected Map<String, String> getParams() {

                return postData;
            }
        };
        requestQueue.add(customRequest);
    }
}
