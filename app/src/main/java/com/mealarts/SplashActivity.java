package com.mealarts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CheckExtras;
import com.mealarts.Helpers.SharedPref;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {

    SharedPref sharedPref;
    ImageView ivLogo;
    LinearLayout layoutOffline,llUpdate;
    TextView tvOfflineMessage,tvUpdateDescription;
    Button btnExit,btnUpdate;
    SSLCertification sslCertification = new SSLCertification(SplashActivity.this);
    private boolean gps_enabled = false, network_enabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Fabric.with(this, new Crashlytics());

        // Start animating the image
        ivLogo = findViewById(R.id.ivLogo);
        layoutOffline = findViewById(R.id.layoutOffline);
        llUpdate = findViewById(R.id.llUpdate);
        tvOfflineMessage = findViewById(R.id.tvOfflineMessage);
        tvUpdateDescription = findViewById(R.id.tvUpdateDescription);

        btnExit=findViewById(R.id.btnExit);
        btnUpdate=findViewById(R.id.btnUpdate);

        btnExit.setOnClickListener(v -> finish());

        btnUpdate.setOnClickListener(v -> {
            try {
                sharedPref.logoutApp();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.mealarts")));
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.gradeone.shop.findbest")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.mealarts")));
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.gradeone.shop.findbest")));
            }
        });
    }

    public void checkOffline(){ //Check App Status if On or Off
        RequestQueue requestQueue = Volley.newRequestQueue(SplashActivity.this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLServices.Offline, response -> {
            Log.e("Resp_Splash", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject offlineObj = jsonObject.getJSONObject("Offline Status");
                String offlineStatus = offlineObj.getString("offline");
                String appDescription = offlineObj.getString("app description");
                String updateDescription = offlineObj.getString("update_description");
                String serverVersion = offlineObj.getString("version");
                String appVersion=Integer.toString(BuildConfig.VERSION_CODE);

                if(offlineStatus.equals("no")){
                    if(Double.parseDouble(serverVersion)>Double.parseDouble(appVersion)) {
                        alertShow("Update", "Please update new version of MealArts",updateDescription);
                    }else {
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                if (sharedPref.getWelcome()) { // if Old member.
                                    if(sharedPref.getVisitor()) { //if Visited before.
                                        startActivity(new Intent(SplashActivity.this, MainActivity.class).putExtra("FromSplash", true));
                                        finish();
                                    }else { // if New Visitor
                                        startActivity(new Intent(SplashActivity.this, VisitorRegActivity.class));
                                        finish();
                                    }
                                } else { // if New user to App - introduce how it works screens.
                                    startActivity(new Intent(SplashActivity.this, WelcomeActivity.class)
                                            .putExtra("FromSplash", true));
                                    finish();
                                }
                            }
                        };
                        Timer timer = new Timer("splash_timer");
                        timer.schedule(timerTask, 1000);//Screen time out
                    }
                }else {
                    layoutOffline.setVisibility(View.VISIBLE);
                    tvOfflineMessage.setText(appDescription.isEmpty() ? "Application Is Under Maintenance" : appDescription);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("CheckStatus", error.getMessage()+"_");
            Log.e("CheckStatus", error.toString()+"_");
            error.printStackTrace();
        });
        requestQueue.add(stringRequest);
    }

    @SuppressLint("SetTextI18n")
    private void alertShow(String strTitle, String strMsg, String updateDesc) { //Alert to show if update(New Version) is available.
//        AlertDialog.Builder adb=new AlertDialog.Builder(SplashActivity.this);
        llUpdate.setVisibility(View.VISIBLE);
        ivLogo.setVisibility(View.GONE);
        tvUpdateDescription.setText(updateDesc+"\n\n"+strMsg);
//        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        adb.addContentView(dialog,params);
//        adb.setCancelable(false);

//        adb.setMessage(strMsg);
//        adb.setNegativeButton("Exit", (dialog, which) -> finish());
//        if(strTitle.equals("Update")) {
           //adb.setTitle("New Update is available");
//            adb.setMessage(strMsg+"\n"+updateDesc);
//            adb.setPositiveButton("Update", (dialog, which) -> {
//                try {
//                    sharedPref.logoutApp();
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.mealarts")));
////                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.gradeone.shop.findbest")));
//                } catch (android.content.ActivityNotFoundException anfe) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.mealarts")));
////                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.gradeone.shop.findbest")));
//                }
//            });
//        }
//        adb.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        OneSignal.startInit(SplashActivity.this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                /*.setNotificationReceivedHandler(new OSNotificationReceivedHandler())
                .setNotificationOpenedHandler(new OSNotificationOpenedHandler(SplashActivity.this))*/
                //.unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        sharedPref = new SharedPref(SplashActivity.this);
        if(isConnected())
            if(isLocationEnable())
                checkOffline();
    }

    private boolean isConnected() { // Check if connected to internet or not.
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this)
                .setMessage("No Internet Connection Available !!")
                .setPositiveButton("Settings", (paramDialogInterface, paramInt) ->
                        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS)));
        AlertDialog alert = dialog.create();
        alert.setCancelable(false);
        if(activeNetwork == null ||
                !activeNetwork.isConnectedOrConnecting()){
            alert.show();
            return false;
        }else{
            alert.dismiss();
            return true;
        }
    }

    private boolean isLocationEnable(){ // Check if Location or GPS is enable or not.
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            /*network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);*/
        } catch (Exception ex) { ex.printStackTrace(); }

        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(SplashActivity.this)
                .setMessage("Your GPS or Location is not enable ! Please enable to fetch location.")
                .setPositiveButton("Enable", (paramDialogInterface, paramInt) ->
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)));
        android.app.AlertDialog alert = dialog.create();
        alert.setCancelable(false);
        if(!gps_enabled/* && !network_enabled*/){
            alert.show();
            return false;
        }else{
            alert.dismiss();
            return true;
        }
    }
}
