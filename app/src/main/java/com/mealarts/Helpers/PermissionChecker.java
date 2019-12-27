package com.mealarts.Helpers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by owner on 06-Feb-17.
 */
public class PermissionChecker {

    private Activity context;
    private String[] per = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_SMS,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECEIVE_SMS
    };
    private static final int PERMISSION_REQUEST_CODE = 1;

    public PermissionChecker(Activity context) {
        super();
        this.context = context;
        if (Build.VERSION.SDK_INT > 22)
            if (!checkPermission())
                requestPermission();
    }

    private boolean checkPermission() {
        int count = 0;
        int[] res = new int[per.length];
        for (int i = 0 ; i < per.length ; i ++ )
            res[i] = ContextCompat.checkSelfPermission(context, per[i]);

        for (int re : res)
            if (re == PackageManager.PERMISSION_GRANTED)
                count++;

        return count == per.length;
    }

    private void requestPermission() {
            ActivityCompat.requestPermissions(context,
                    per, PERMISSION_REQUEST_CODE);
    }
}
