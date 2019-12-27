package com.mealarts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
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

public class RegistrationActivity extends AppCompatActivity {

    EditText edtFullName, edtEmail, edtContact/*, edtPassword, edtConfPassword*/;
    TextView tvRegister, tvTnC, tvChangeContact, tvLogin;
    ImageView ivBack, ivLoader;
    LinearLayout layoutLoader;

    CustomToast customToast = new CustomToast();
    SharedPref sharedPref;
    CheckExtras connection;
    SSLCertification sslCertification = new SSLCertification(RegistrationActivity.this);
    PermissionChecker permissionChecker;
    OSPermissionSubscriptionState status;
    Boolean FromCart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

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

        //Log.e("Token", status.getSubscriptionStatus().getUserId());

        sharedPref = new SharedPref(RegistrationActivity.this);
        connection = new CheckExtras(RegistrationActivity.this);
        permissionChecker = new PermissionChecker(RegistrationActivity.this);
        FromCart = getIntent().getBooleanExtra("FromCart", false);

        layoutLoader = findViewById(R.id.layoutLoader);
        ivLoader = findViewById(R.id.ivLoader);
        Glide.with(RegistrationActivity.this).asGif().load(R.drawable.mealarts_loader).into(ivLoader);

        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtContact = findViewById(R.id.edtContact);
        /*edtPassword = findViewById(R.id.edtPassword);
        edtConfPassword = findViewById(R.id.edtConfPassword);*/
        tvTnC = findViewById(R.id.tvTnC);
        SpannableString spannableString = new SpannableString(getString(R.string.textTandC));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mealarts.com/Cust_json/terms.php")));
            }
        };
        SpannableString spannableString1 = new SpannableString(getString(R.string.textPP));
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mealarts.com/Cust_json/policy.php")));
            }
        };

        spannableString.setSpan(clickableSpan, 0,
                spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString1.setSpan(clickableSpan1, 0,
                spannableString1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTnC.setText(TextUtils.concat(getResources().getString(R.string.TandC)," ", spannableString, " and ", spannableString1, "."), TextView.BufferType.SPANNABLE);
        tvTnC.setMovementMethod(LinkMovementMethod.getInstance());
        tvChangeContact = findViewById(R.id.tvChangeContact);
        tvRegister = findViewById(R.id.tvRegister);
        tvLogin = findViewById(R.id.tvLogin);

        try {
            JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
            JSONObject userCart = cartObj.getJSONObject(getResources().getString(R.string.UserJSON));
            edtContact.setText(userCart.getString("Mobile"));
            edtContact.setFocusableInTouchMode(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tvRegister.setOnClickListener(v -> getValidate());

        tvChangeContact.setOnClickListener(v -> {
            startActivity(new Intent(RegistrationActivity.this, VisitorRegActivity.class));
            finish();
        });

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, LogInActivity.class);
            intent.putExtra("FromCart", FromCart);
            startActivity(intent);
            finish();
        });
    }

    public void getValidate() {

        String FullName = edtFullName.getText().toString().trim();
        String Email = edtEmail.getText().toString().trim();
        String Contact = edtContact.getText().toString().trim();
        /*String Password = edtPassword.getText().toString().trim();
        String ConfirmPassword = edtConfPassword.getText().toString().trim();*/

        if (FullName.equals("")) {
            edtFullName.requestFocus();
            edtFullName.setError("Required");
            customToast.showCustomToast(RegistrationActivity.this, "Full Name Required");
        } else if (Email.equals("")) {
            edtEmail.requestFocus();
            edtEmail.setError("Required");
            customToast.showCustomToast(RegistrationActivity.this, "Email Required");
        } else if(!edtEmail.getText().toString().trim().isEmpty()
                && !edtEmail.getText().toString().trim().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
            edtEmail.requestFocus();
            edtEmail.setError("Incorrect Email Format");                                         //*//
        }else if (Contact.equals("")) {
            edtContact.requestFocus();
            edtContact.setError("Required");
            customToast.showCustomToast(RegistrationActivity.this, "Contact Number Required");
        }else if(Contact.length()<10) {
            edtContact.requestFocus();
            edtContact.setError("Invalid Mobile Number");
            customToast.showCustomToast(RegistrationActivity.this, "Invalid Mobile Number");
        }/*else if(Password.equals("")) {
            edtPassword.requestFocus();
            edtPassword.setError("Required");
            customToast.showCustomToast(RegistrationActivity.this, "Password Required");
        }else if(ConfirmPassword.equals("")) {
            edtConfPassword.requestFocus();
            edtConfPassword.setError("Required");
            customToast.showCustomToast(RegistrationActivity.this, "Confirm Password Required");
        }else if(!Password.equals(ConfirmPassword)) {
            edtConfPassword.requestFocus();
            edtConfPassword.setError("Password mismatch");
            customToast.showCustomToast(RegistrationActivity.this, "Password mismatch with Confirm Password");
        }*/else{
            layoutLoader.setVisibility(View.VISIBLE);
            getRegistered(FullName, Email, Contact/*, Password*/);
        }

    }

    HashMap<String, String> postData;
    public void getRegistered(final String FullName, String Email, String Contact/*, final String Password*/){
        postData = new HashMap<>();
        postData.put("cust_name", FullName);
        postData.put("email", Email);
        postData.put("mobile", Contact);
        //postData.put("password", Password);
        postData.put("token", status.getSubscriptionStatus().getUserId());

        RequestQueue requestQueue = Volley.newRequestQueue(this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.Register, response -> {
            Log.e("Resp", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String Status = jsonObject.getString("status");
                if(Status.equals("1")){
                    String CustomerId = jsonObject.getString("customer_id");
                    String CustName = jsonObject.getString("cust_name");
                    String Email1 = jsonObject.getString("email");
                    String Contact1 = jsonObject.getString("mobile");
                    String token = jsonObject.getString("token");

                    sharedPref.setUserId(CustomerId);

                    JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                    if(cartObj.has(getResources().getString(R.string.UserJSON))) {
                        JSONObject userObj = cartObj.getJSONObject(getResources().getString(R.string.UserJSON));
                        userObj.put("UserName", CustName);
                        userObj.put("Email", Email1);
                        userObj.put("Mobile", Contact1);
                        userObj.put("Token", token);
                    }else {
                        JSONObject userObj = new JSONObject();
                        userObj.put("UserName", CustName);
                        userObj.put("Email", Email1);
                        userObj.put("Mobile", Contact1);
                        userObj.put("Token", token);
                        cartObj.put(getResources().getString(R.string.UserJSON),userObj);
                    }
                    sharedPref.setUserCart(cartObj.toString());

                    customToast.showCustomToast(RegistrationActivity.this, "Registered Successfully");
                    onBackPressed();
                    finish();
                }else {
                    String Message = jsonObject.getString("message");
                    customToast.showCustomToast(RegistrationActivity.this, Message);
                }
                layoutLoader.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("Registration", error.toString()+"_");
            Log.e("Registration", error.getMessage()+"_");
            error.printStackTrace();
        }) {
            @Override
            protected Map<String, String> getParams() {

                return postData;
            }
        };
        requestQueue.add(stringRequest);
        Log.e("Resp", stringRequest.getUrl()+"_"+postData);
    }
}
