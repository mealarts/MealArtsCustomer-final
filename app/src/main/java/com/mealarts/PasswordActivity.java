package com.mealarts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PasswordActivity extends AppCompatActivity {

    Boolean FromCart = false, isProceed = false;
    String Procedure = "", Contact="";

    ImageView ivBack, ivLoader;
    LinearLayout layoutLoader;
    EditText edtRegContact, edtSetPassword;
    TextView tvVerifyContact, tvSetPassword;
    LinearLayout layoutVerifyContact, layoutSetPass;
    FrameLayout layoutTitle;
    CustomToast customToast = new CustomToast();
    SSLCertification sslCertification = new SSLCertification(PasswordActivity.this);
    CheckExtras connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        connection = new CheckExtras(PasswordActivity.this);

        FromCart = getIntent().getBooleanExtra("FromCart", false);
        Procedure = getIntent().getStringExtra("Procedure");
        if(Procedure.equals("change"))
            Contact = getIntent().getStringExtra("Contact");

        edtRegContact = findViewById(R.id.edtRegContact);
        tvVerifyContact = findViewById(R.id.tvVerifyContact);
        edtSetPassword = findViewById(R.id.edtSetPassword);
        tvSetPassword = findViewById(R.id.tvSetPassword);
        layoutTitle = findViewById(R.id.layoutTitle);
        layoutVerifyContact = findViewById(R.id.layoutVerifyContact);
        layoutSetPass = findViewById(R.id.layoutSetPass);

        if(Procedure.equals("forget")){
            layoutVerifyContact.setVisibility(View.VISIBLE);
            layoutTitle.setVisibility(View.GONE);
            layoutSetPass.setVisibility(View.GONE);
        }else if(Procedure.equals("change")){
            layoutSetPass.setVisibility(View.VISIBLE);
            layoutTitle.setVisibility(View.VISIBLE);
            layoutVerifyContact.setVisibility(View.GONE);
            edtRegContact.setText(Contact+"");
        }

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> onBackPressed());

        layoutLoader = findViewById(R.id.layoutLoader);
        ivLoader = findViewById(R.id.ivLoader);
        Glide.with(PasswordActivity.this).asGif().load(R.drawable.mealarts_loader).into(ivLoader);

        tvVerifyContact.setOnClickListener(v -> {
            String Contact = edtRegContact.getText().toString().trim();

            if(Contact.isEmpty()) {
                edtRegContact.requestFocus();
                edtRegContact.setError("Required");
                customToast.showCustomToast(PasswordActivity.this, "Registered Contact Number Required");
            }else if(Contact.length()<10) {
                edtRegContact.requestFocus();
                edtRegContact.setError("Invalid Mobile Number");
                customToast.showCustomToast(PasswordActivity.this, "Invalid Registered Contact Number");
            }else{
                layoutLoader.setVisibility(View.VISIBLE);
                verifyContact(Contact);
            }
        });

        tvSetPassword.setOnClickListener(v -> {
            String Password = edtSetPassword.getText().toString().trim();
            String Contact = edtRegContact.getText().toString().trim();
            if(Password.isEmpty()) {
                edtSetPassword.requestFocus();
                edtSetPassword.setError("Required");
                customToast.showCustomToast(PasswordActivity.this, "New Password Required");
            }else {
                layoutLoader.setVisibility(View.VISIBLE);
                setNewPassword(Contact, Password);
            }
        });
    }

    HashMap<String, String> postData;
    public void verifyContact(String Contact){
        postData = new HashMap<>();
        postData.put("mobile", Contact);

        RequestQueue requestQueue = Volley.newRequestQueue(PasswordActivity.this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.ForgetPassword, response -> {
            Log.e("ForgetResp", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String Status = jsonObject.getString("success");
                if(Status.equals("1")){
                    layoutLoader.setVisibility(View.GONE);
                    isProceed = true;
                    layoutVerifyContact.setVisibility(View.GONE);
                    layoutSetPass.setVisibility(View.VISIBLE);

                    customToast.showCustomToast(PasswordActivity.this, "Thank you for verify Contact");
                }else{
                    layoutLoader.setVisibility(View.GONE);
                    customToast.showCustomToast(PasswordActivity.this, "This Number is not Registered !!");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("VerifyContact", error.toString()+"_");
            Log.e("VerifyContact", error.getMessage()+"_");
            error.printStackTrace();
        }){
            @Override
            protected Map<String, String> getParams(){
                return postData;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void setNewPassword(String Contact, String Password){
        postData = new HashMap<>();
        postData.put("mobile", Contact);
        postData.put("new_pass", Password);

        RequestQueue requestQueue = Volley.newRequestQueue(PasswordActivity.this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.ChangePassword, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String Status = jsonObject.getString("success");
                if(Status.contains("successfully")){
                    layoutLoader.setVisibility(View.GONE);
                    isProceed = false;
                    customToast.showCustomToast(PasswordActivity.this, "Password Updated Successfully");
                    if(Procedure.equals("forget")){
                        Intent intent = new Intent(PasswordActivity.this, LogInActivity.class);
                        intent.putExtra("FromCart", FromCart);
                        startActivity(intent);
                        finish();
                    }else if(Procedure.equals("change")){
                        onBackPressed();
                    }
                }else customToast.showCustomToast(PasswordActivity.this, Status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("NewPass", error.toString()+"_");
            Log.e("NewPass", error.getMessage()+"_");
            error.printStackTrace();
        }){
            @Override
            protected Map<String, String> getParams()  {
                return postData;
            }
        };
        requestQueue.add(stringRequest);
        Log.e("PasswordReq", stringRequest.getUrl()+"_"+postData);

    }

    @Override
    public void onBackPressed() {
        if(isProceed) {
            if (Procedure.equals("forget")) {
                layoutVerifyContact.setVisibility(View.VISIBLE);

                layoutSetPass.setVisibility(View.GONE);
                isProceed = false;
            }
        }else {
            if(Procedure.equals("forget")){
                Intent intent = new Intent(PasswordActivity.this, LogInActivity.class);
                intent.putExtra("FromCart", FromCart);
                startActivity(intent);
                finish();
            }else if(Procedure.equals("change")){
                super.onBackPressed();
                finish();
            }
        }
    }
}
