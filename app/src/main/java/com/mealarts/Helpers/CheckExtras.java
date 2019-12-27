package com.mealarts.Helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;


public class CheckExtras {

    private boolean gps_enabled = false, network_enabled = false;

    public CheckExtras(Context context) {
        isConnected(context);
        isLocationEnable(context);
    }

    private void isConnected(Context context) {
    ConnectivityManager cm =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                .setMessage("No Internet Connection Available !!")
                .setPositiveButton("Settings", (paramDialogInterface, paramInt) ->
                        context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS)));
        AlertDialog alert = dialog.create();
        alert.setCancelable(false);
        if(activeNetwork == null ||
                !activeNetwork.isConnectedOrConnecting()){
            alert.show();
        }else alert.dismiss();
    }

    private void isLocationEnable(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) { ex.printStackTrace(); }

        AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                .setMessage("Your GPS or Location is not enable ! Please enable to fetch location.")
                .setPositiveButton("Enable", (paramDialogInterface, paramInt) ->
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)));
        AlertDialog alert = dialog.create();
        alert.setCancelable(false);
        if(!gps_enabled && !network_enabled){
            alert.show();
        }else alert.dismiss();
    }
}
