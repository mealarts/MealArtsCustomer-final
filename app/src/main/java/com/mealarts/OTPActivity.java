package com.mealarts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CheckExtras;
import com.mealarts.Helpers.CustomToast;
import com.mealarts.Helpers.SMSListener;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.Helpers.SmsReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OTPActivity extends AppCompatActivity {

    EditText edtOTP;
    TextView tvVerifyOTP, tvResend, tvResendTimer;
    ImageView ivLoader;
    LinearLayout layoutLoader;

    String Contact, OTP;
    SharedPref sharedPref;
    CheckExtras connection;
    SSLCertification sslCertification = new SSLCertification(OTPActivity.this);
    int otpCount = 0;
    CustomToast customToast = new CustomToast();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        sharedPref = new SharedPref(OTPActivity.this);
        connection = new CheckExtras(OTPActivity.this);

        layoutLoader = findViewById(R.id.layoutLoader);
        ivLoader = findViewById(R.id.ivLoader);
        Glide.with(OTPActivity.this).asGif().load(R.drawable.mealarts_loader).into(ivLoader);
        layoutLoader.setVisibility(View.VISIBLE);

        Contact = getIntent().getStringExtra("mobile");
        OTP = getIntent().getStringExtra("otp");

        edtOTP = findViewById(R.id.edtOTP);
        tvVerifyOTP = findViewById(R.id.tvVerifyOTP);
        tvResend = findViewById(R.id.tvResend);
        tvResendTimer = findViewById(R.id.tvResendTimer);

        receiveSMS();

        tvVerifyOTP.setOnClickListener(v -> getValidate());

        tvResend.setOnClickListener(v -> {
            otpCount = otpCount +1;
            if(otpCount>2){
                customToast.showCustomToast(OTPActivity.this, "Please try again after some time !");
                Intent intent = new Intent(getApplicationContext(), VisitorRegActivity.class);
                startActivity(intent);
            } else {
                layoutLoader.setVisibility(View.VISIBLE);
                getOtpVerified(Contact, "");
            }
        });
    }

    public void receiveSMS(){
        layoutLoader.setVisibility(View.GONE);
        SmsReceiver.bindListener(messageText -> {
            Pattern pattern = Pattern.compile("(\\d{4})");
            Matcher matcher = pattern.matcher(messageText);
            String OTP = "";
            if (matcher.find()) {
                OTP = matcher.group(0);
                edtOTP.setText(OTP);// 4 digit number
                tvResend.setVisibility(View.GONE);
                tvResendTimer.setVisibility(View.GONE);
                layoutLoader.setVisibility(View.GONE);
            }
        });

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvResend.setVisibility(View.GONE);
                tvResendTimer.setText("Request again for OTP after ( 00:" + millisUntilFinished / 1000 +" ) seconds");
            }

            public void onFinish() {
                tvResendTimer.setText("Request again for OTP after ( 00:00 ) seconds");
                tvResend.setVisibility(View.VISIBLE);
                tvResendTimer.setVisibility(View.GONE);
                layoutLoader.setVisibility(View.GONE);
            }
        }.start();
    }

    public void getValidate(){
        String etOTP = edtOTP.getText().toString().trim();

        if(etOTP.equals("")) {
            edtOTP.requestFocus();
            edtOTP.setError("Required");
            customToast.showCustomToast(OTPActivity.this, "OTP Required");
        }if(etOTP.length() != OTP.length()) {
            edtOTP.requestFocus();
            edtOTP.setError("Invalid");
            customToast.showCustomToast(OTPActivity.this, "Invalid OTP");
        }else{
            layoutLoader.setVisibility(View.VISIBLE);
            if(etOTP.equals(OTP))
                getOtpVerified(Contact, etOTP);
            else edtOTP.setError("Invalid");
        }
    }

    HashMap<String, String> postData;
    public void getOtpVerified(String Contact, final  String OTP){
        postData = new HashMap<>();
        postData.put("mobile_no", Contact);
        if(!OTP.equals(""))
            postData.put("otp", OTP);

        RequestQueue requestQueue = Volley.newRequestQueue(this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.VisitorReg, response -> {
            Log.e("Resp", response);
            layoutLoader.setVisibility(View.GONE);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String Status = jsonObject.getString("status");
                if(Status.equals("1")){
                    if(!OTP.equals("")) {

                        customToast.showCustomToast(OTPActivity.this, "Thank You.");

                        JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                        JSONObject userObj;
                        if(cartObj.has(getResources().getString(R.string.UserJSON)))
                            userObj = cartObj.getJSONObject(getResources().getString(R.string.UserJSON));
                        else {
                            userObj = new JSONObject();
                            cartObj.put(getResources().getString(R.string.UserJSON), userObj);
                        }
                        userObj.put("Mobile", Contact);
                        sharedPref.setUserCart(String.valueOf(cartObj));
                        startActivity(new Intent(OTPActivity.this, MainActivity.class)
                                .putExtra("FromSplash", true));
                        finish();
                    }else receiveSMS();
                }else {
                    String Message = jsonObject.getString("message");
                    customToast.showCustomToast(OTPActivity.this, Message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("OTP", error.toString()+"_");
            Log.e("OTP", error.getMessage()+"_");
            error.printStackTrace();
        }) {
            @Override
            protected Map<String, String> getParams() {
                return postData;
            }
        };
        requestQueue.add(stringRequest);
        Log.e("Request", stringRequest.getUrl()+"_"+postData.toString());
    }
}
