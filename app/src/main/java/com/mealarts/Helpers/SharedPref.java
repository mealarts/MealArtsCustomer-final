package com.mealarts.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.mealarts.SplashActivity;

/**
 * Created by  on 29-05-2016.
 */
public class SharedPref {

    private Context context;
    private String MEALARTS = "MealArts";

    private String USER_ID = "User_Id",WELCOME = "Welcome", VISITED = "Visited", USER = "User", CART = "Cart",
            POS = "Pos", CurrentOrderId = "0", SERVER_TIME = "ServerTime";

    public void setPos(Integer Pos) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                MEALARTS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(POS, Pos);
        editor.apply();
    }

    public Integer getPos() {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                MEALARTS, Context.MODE_PRIVATE);
        return sharedpreferences.getInt(POS, 0);
    }

    public SharedPref(Context context) {
        this.context = context;
    }

    public void setUserId(String userId) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                MEALARTS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(USER_ID, userId);
        editor.apply();
    }

    public String getUserId() {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                MEALARTS, Context.MODE_PRIVATE);
        return sharedpreferences.getString(USER_ID, "");
    }

    public void setWelcome(Boolean Welcome) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                MEALARTS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(WELCOME, Welcome);
        editor.apply();
    }

    public Boolean getWelcome() {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                MEALARTS, Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean(WELCOME, false);
    }

    public void setVisitor(Boolean Visited) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                MEALARTS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(VISITED, Visited);
        editor.apply();
    }

    public Boolean getVisitor() {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                MEALARTS, Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean(VISITED, false);
    }

    public void setUserCart(String userCart) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                MEALARTS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(CART, userCart);
        editor.apply();
    }

    public String getUserCart() {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                MEALARTS, Context.MODE_PRIVATE);
        return sharedpreferences.getString(CART, "{}");
    }

    public void setCurrentOrderId(String OrderId) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                MEALARTS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(CurrentOrderId, OrderId);
        editor.apply();
    }

    public String getCurrentOrderId() {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                MEALARTS, Context.MODE_PRIVATE);
        return sharedpreferences.getString(CurrentOrderId, "0");
    }

    public void setServerTime(String ServerTime) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                MEALARTS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(SERVER_TIME, ServerTime);
        editor.apply();
    }

    public String getServerTime() {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                MEALARTS, Context.MODE_PRIVATE);
        return sharedpreferences.getString(SERVER_TIME, "");
    }

    public boolean isLogin ()
    {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(MEALARTS, Context.MODE_PRIVATE);
        String User_Id = sharedPreferences.getString("User_Id", "D");
        return !User_Id.equals("D");
    }

    public void logoutApp ()
    {
//        MainActivityCustomer.drawer.closeDrawer(GravityCompat.START);
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(MEALARTS, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        setWelcome(true);

        Intent intent = new Intent(context, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
            Intent.FLAG_ACTIVITY_CLEAR_TASK |
            Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
        ((Activity) context).finish();
    }
}
