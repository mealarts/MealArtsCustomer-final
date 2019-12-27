package com.mealarts.Helpers;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mealarts.R;

public class CustomToast {

    public void showCustomToast(Context context, String Message) {
        View toastView = ((Activity)context).getLayoutInflater().inflate(R.layout.layout_toast, null);
        TextView tv = toastView.findViewById(R.id.tvToast);
        tv.setText(Message);
        // Initiate the Toast instance.
        Toast toast = new Toast(context.getApplicationContext());
        // Set custom view in toast.
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0,0);
        toast.show();
    }
}
