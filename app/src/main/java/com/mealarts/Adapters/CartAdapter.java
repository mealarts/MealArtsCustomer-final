package com.mealarts.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mealarts.AddToCartActivity;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CustomToast;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.Helpers.Utils.CartUtils;
import com.mealarts.MenuListActivity;
import com.mealarts.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<CartUtils> cartArrayList;
    private int QtyCount = 0;
    private float QtyPrice = 0;
    private SharedPref sharedPrefs;
    private CustomToast customToast;

    public CartAdapter(Context context,  ArrayList<CartUtils> cartArrayList) {
        this.context = context;
        this.cartArrayList = cartArrayList;
        sharedPrefs = new SharedPref(context);
        customToast = new CustomToast();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_cart_list, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvAddCart.setVisibility(View.GONE);
        holder.tvMenuOrgPrice.setVisibility(View.GONE);
        holder.tvPrepTime.setVisibility(View.GONE);

        if(cartArrayList.get(position).getVegType().toLowerCase().equals("veg")) {
            Glide.with(context).load(R.drawable.veg)
                    .placeholder(R.drawable.mealarts_loader).into(holder.ivVegType);
            holder.tvAddCart.setTextColor(Color.parseColor("#00aa00"));
            holder.tvAddCart.setBackgroundResource(R.drawable.add_cart_veg);
            holder.productCounter.setBackgroundResource(R.drawable.add_cart_back_veg);
        }
        else {
            Glide.with(context).load(R.drawable.nonveg)
                    .placeholder(R.drawable.mealarts_loader).into(holder.ivVegType);
            holder.tvAddCart.setTextColor(Color.parseColor("#E00000"));
            holder.tvAddCart.setBackgroundResource(R.drawable.add_cart);
            holder.productCounter.setBackgroundResource(R.drawable.add_cart_back);
        }

        if(!cartArrayList.get(position).getProductImg().isEmpty()) {
            Glide.with(context).load(URLServices.MenuImg + cartArrayList.get(position).getProductImg()).into(holder.ivMenuImg);
            Log.d("/*menu_menu",URLServices.MenuImg + cartArrayList.get(position).getProductImg()+"");
        }
        else if(!cartArrayList.get(position).getProductImgDefault().isEmpty()) {
            Glide.with(context).load(URLServices.MenuDefaultImg + cartArrayList.get(position).getProductImgDefault()).into(holder.ivMenuImg);
            Log.d("/*menu_products",URLServices.MenuDefaultImg + cartArrayList.get(position).getProductImgDefault()+"");
        }
        else Glide.with(context).load(R.drawable.mealarts_icon).into(holder.ivMenuImg);

        holder.tvMenuName.setText(cartArrayList.get(position).getProductName());
        holder.tvMenuPrice.setText("₹ " + cartArrayList.get(position).getSellingPrice());
        if(cartArrayList.get(position).getVegType().toLowerCase().equals("veg"))
            Glide.with(context).load(R.drawable.veg)
                    .placeholder(R.drawable.mealarts_loader).into(holder.ivVegType);
        else Glide.with(context).load(R.drawable.nonveg)
                .placeholder(R.drawable.mealarts_loader).into(holder.ivVegType);

        if(cartArrayList.get(position).getOfferPer() > 0)
            holder.tvOfferPer.setVisibility(View.VISIBLE);
        else holder.tvOfferPer.setVisibility(View.GONE);

        QtyCount = Integer.parseInt(cartArrayList.get(position).getQuantity());
        Log.e("Qty", cartArrayList.get(position).getQuantity());

        holder.tvQty.setText(String.valueOf(QtyCount));
        QtyPrice = Math.round(QtyCount * Integer.parseInt(cartArrayList.get(position).getSellingPrice()));
        holder.tvTotalPrice.setText("₹ " + QtyPrice);
        holder.tvTotalPrice.setVisibility(View.VISIBLE);
        holder.productCounter.setVisibility(View.VISIBLE);

        holder.tvIncQty.setOnClickListener(v -> {
            Log.d("/*abc","Cart adapter Inc");
            try {

                JSONObject cartObj = new JSONObject(sharedPrefs.getUserCart());
                JSONArray cartArray = cartObj.getJSONArray(context.getResources().getString(R.string.CartJsonArray));
                for(int i = 0 ; i < cartArray.length() ; i++){
                    JSONObject cartItemObj = cartArray.getJSONObject(i);
                    QtyCount = cartItemObj.getInt("Quantity");
                    if(cartItemObj.getString("ProductId").equals(cartArrayList.get(position).getProductId())){
                        QtyCount ++;
                        cartItemObj.put("Quantity", QtyCount);
                        cartItemObj.put("QtyPrice", Math.round(Integer.parseInt(cartArrayList.get(position).getSellingPrice())*QtyCount));
                        //hgd
                        if(cartArrayList.get(position).getIsGST().equals("yes"))
                            cartItemObj.put("gst_amt",(Math.round(Integer.parseInt(cartArrayList.get(position).getSellingPrice()) * QtyCount))*Float.parseFloat(cartArrayList.get(position).getGST_Perc())/100);
                        else
                            cartItemObj.put("gst_amt",0);

                        //15novhgd
                        cartItemObj.put("p_charge",Math.round(Float.parseFloat(cartArrayList.get(position).getPack_charge()) * QtyCount));

//                        if(cartItemObj.getString("isgst").equals("yes"))
//                            gstTotal+=Float.parseFloat(cartItemObj.getString("QtyPrice"))*(Float.parseFloat(cartItemObj.getString("gstp"))/100);
                       // Log.d("/*ca","gstp "+cartArrayList.get(position).getGST_Perc());
                       // cartItemObj.put("gst",Math.round((Integer.parseInt(cartArrayList.get(position).getSellingPrice())*QtyCount)*(Float.parseFloat(cartArrayList.get(position).getGST_Perc())/100)));
                        //cartArray.put(cartItemObj);
                        holder.tvQty.setText(String.valueOf(QtyCount));
                        QtyPrice = Math.round(QtyCount * Integer.parseInt(cartArrayList.get(position).getSellingPrice()));
                        holder.tvTotalPrice.setText("₹ " + QtyPrice);
                        holder.tvTotalPrice.setVisibility(View.VISIBLE);
                        AddToCartActivity.bottomMenuAdapter.notifyDataSetChanged();
                        cartArrayList.get(position).setQuantity(String.valueOf(QtyCount));
                        //cartObj.put(context.getResources().getString(R.string.CartJsonArray),cartArray);

                        sharedPrefs.setUserCart(cartObj.toString());
                        //Log.e("Cart_adpt", sharedPrefs.getUserCart()+"_");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            float cartTotal = 0;
            float gstTotal=0; //hgd
            float pChargeTotal=0; //hgd

            try {
                JSONObject cartObj = new JSONObject(sharedPrefs.getUserCart());
                //hgd
                JSONObject cartJSONObj = cartObj.getJSONObject(context.getResources().getString(R.string.CartJsonObj));
                JSONArray cartArray = cartObj.getJSONArray(context.getResources().getString(R.string.CartJsonArray));
                for(int i = 0 ; i < cartArray.length(); i++){
                    JSONObject cartItemObjs = cartArray.getJSONObject(i) ;
                    cartTotal += Float.parseFloat(cartItemObjs.getString("QtyPrice"));

                    //hgd
                    pChargeTotal += Float.parseFloat(cartItemObjs.getString("p_charge"));

                    if(cartItemObjs.getString("isgst").equals("yes"))
                        gstTotal+=Float.parseFloat(cartItemObjs.getString("QtyPrice"))*(Float.parseFloat(cartItemObjs.getString("gstp"))/100);

                    if(cartItemObjs.has(context.getResources().getString(R.string.AddOnsJsonArray))){
                        JSONArray addOnsArray = cartItemObjs.getJSONArray(context.getResources().getString(R.string.AddOnsJsonArray));
                        if(addOnsArray.length() != 0){
                            for(int j = 0 ; j < addOnsArray.length() ; j++){
                                JSONObject addOnsObj = addOnsArray.getJSONObject(j) ;
                                cartTotal += Float.parseFloat(addOnsObj.getString("QtyPrice"));
                                pChargeTotal += Float.parseFloat(addOnsObj.getString("TotalPackAmt"));
                            }
                        }
                    }
                }
                cartJSONObj.put("TotalAmount", String.valueOf(cartTotal));
                cartJSONObj.put("totalGST",String.valueOf(gstTotal));
                //15nov19 hgd
                cartJSONObj.put("totalPackCharge",String.valueOf(pChargeTotal));

                sharedPrefs.setUserCart(String.valueOf(cartObj));
                Log.e("Cart_adpt", sharedPrefs.getUserCart()+"_");
                AddToCartActivity.tvCartTotal.setText("₹ " + cartTotal+"/-");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        holder.tvDecQty.setOnClickListener(v -> {
            try {
                Log.d("/*abc","Cart adapter Dec");

                JSONObject cartObj = new JSONObject(sharedPrefs.getUserCart());

                JSONArray cartArray = cartObj.getJSONArray(context.getResources().getString(R.string.CartJsonArray));
                for (int i = 0; i < cartArray.length(); i++) {
                    JSONObject cartItemObj = cartArray.getJSONObject(i);
                    QtyCount = cartItemObj.getInt("Quantity");
                    if (cartItemObj.getString("ProductId").equals(cartArrayList.get(position).getProductId())) {
                        if (QtyCount != 0 && QtyCount > 0) {
                            QtyCount--;
                            holder.tvQty.setText(String.valueOf(QtyCount));
                            QtyPrice = Math.round(QtyCount * Integer.parseInt(cartArrayList.get(position).getSellingPrice()));
                            holder.tvTotalPrice.setText("₹ " + QtyPrice);
                            if (QtyCount != 0 && QtyCount > 0) {
                                cartItemObj.put("Quantity", QtyCount);
                                cartItemObj.put("QtyPrice", Math.round(Integer.parseInt(cartArrayList.get(position).getSellingPrice()) * QtyCount));
                                //hgd
                                if(cartArrayList.get(position).getIsGST().equals("yes"))
                                    cartItemObj.put("gst_amt",(Math.round(Integer.parseInt(cartArrayList.get(position).getSellingPrice()) * QtyCount))*Float.parseFloat(cartArrayList.get(position).getGST_Perc())/100);
                                else
                                    cartItemObj.put("gst_amt",0);
                                //15novhgd
                                cartItemObj.put("p_charge",Math.round(Integer.parseInt(cartArrayList.get(position).getPack_charge()) * QtyCount));

//                                if(cartItemObj.getString("isgst").equals("yes"))
//                                    gstTotal+=Float.parseFloat(cartItemObj.getString("QtyPrice"))*(Float.parseFloat(cartItemObj.getString("gstp"))/100);
                               // Log.d("/*ca","gstp "+cartArrayList.get(position).getGST_Perc());
                                //cartItemObj.put("gst",Math.round((Integer.parseInt(cartArrayList.get(position).getSellingPrice())*QtyCount)*(Float.parseFloat(cartArrayList.get(position).getGST_Perc())/100)));
                                //cartArray.put(cartItemObj);

                                holder.tvTotalPrice.setVisibility(View.VISIBLE);
                                holder.productCounter.setVisibility(View.VISIBLE);
                                cartArrayList.get(position).setQuantity(String.valueOf(QtyCount));

                                sharedPrefs.setUserCart(cartObj.toString());
                            } else {
                                holder.tvTotalPrice.setVisibility(View.INVISIBLE);
                                holder.productCounter.setVisibility(View.GONE);
                                cartArrayList.get(position).setQuantity(String.valueOf(QtyCount));
                                final ArrayList<JSONObject> result = new ArrayList<>(cartArray.length());
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    cartArray.remove(i);
                                    cartArrayList.remove(position);
                                    customToast.showCustomToast(context, "Removed from cart successfully !");
                                } else {
                                    for (int j = 0; j < cartArray.length(); j++) {
                                        final JSONObject obj = cartArray.optJSONObject(j);
                                        if (obj != null) {
                                            result.add(obj);
                                        }
                                    }
                                    for (int n = 0; n < result.size(); n++) {
                                        if (result.get(n).getString("ProductId").equals(cartArrayList.get(position).getProductId())) {
                                            result.remove(n);
                                            cartArrayList.remove(position);
                                            customToast.showCustomToast(context, "Removed from cart successfully !");
                                        }
                                    }
                                    for (final JSONObject obj : result) {
                                        cartArray.put(obj);
                                    }
                                }
                                AddToCartActivity.cartAdapter.notifyDataSetChanged();
                                sharedPrefs.setUserCart(cartObj.toString());
                                if(cartArray.length() == 0) {
                                    AddToCartActivity.layoutNoItems.setVisibility(View.VISIBLE);
                                    try {
                                        JSONObject newCartObj = new JSONObject(sharedPrefs.getUserCart());
                                        newCartObj.put(context.getResources().getString(R.string.CartJsonObj), new JSONObject());
                                        newCartObj.put(context.getResources().getString(R.string.CartJsonArray), new JSONArray());
                                        sharedPrefs.setUserCart(newCartObj.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    /*Intent intent = new Intent(context, MenuListActivity.class);
                                    context.startActivity(intent);
                                    ((Activity)context).finish();*/
                                }
                                //cartObj.put(context.getResources().getString(R.string.CartJsonArray),cartArray);
                            }
                            //Log.e("Cart_adpt", sharedPrefs.getUserCart()+"_");
                            AddToCartActivity.bottomMenuAdapter.notifyDataSetChanged();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            float cartTotal = 0;
            float gstTotal=0; //hgd
            float pChargeTotal=0; //hgd

            try {
                JSONObject cartObj = new JSONObject(sharedPrefs.getUserCart());
                //hgd
                JSONObject cartJSONObj = cartObj.getJSONObject(context.getResources().getString(R.string.CartJsonObj));
                JSONArray cartArray = cartObj.getJSONArray(context.getResources().getString(R.string.CartJsonArray));
                for(int i = 0 ; i < cartArray.length(); i++){
                    JSONObject cartItemObjs = cartArray.getJSONObject(i) ;
                    cartTotal += Float.parseFloat(cartItemObjs.getString("QtyPrice"));
                    //hgd
                    pChargeTotal += Float.parseFloat(cartItemObjs.getString("p_charge"));
                    if(cartItemObjs.getString("isgst").equals("yes"))
                        gstTotal+=Float.parseFloat(cartItemObjs.getString("QtyPrice"))*(Float.parseFloat(cartItemObjs.getString("gstp"))/100);

                    if(cartItemObjs.has(context.getResources().getString(R.string.AddOnsJsonArray))){
                        JSONArray addOnsArray = cartItemObjs.getJSONArray(context.getResources().getString(R.string.AddOnsJsonArray));
                        if(addOnsArray.length() != 0){
                            for(int j = 0 ; j < addOnsArray.length() ; j++){
                                JSONObject addOnsObj = addOnsArray.getJSONObject(j) ;
                                cartTotal += Float.parseFloat(addOnsObj.getString("QtyPrice"));
                                pChargeTotal += Float.parseFloat(addOnsObj.getString("TotalPackAmt"));
                            }
                        }
                    }
                }
                cartJSONObj.put("TotalAmount", String.valueOf(cartTotal));
                cartJSONObj.put("totalGST",String.valueOf(gstTotal));
                //15nov19 hgd
                cartJSONObj.put("totalPackCharge",String.valueOf(pChargeTotal));

                sharedPrefs.setUserCart(String.valueOf(cartObj));
                Log.e("Cart_adpt", sharedPrefs.getUserCart()+"_");
                AddToCartActivity.tvCartTotal.setText("₹ " + cartTotal+"/-");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        if(cartArrayList.get(position).getAddOns()){
            holder.layoutCartAddOns.setVisibility(View.VISIBLE);
            AddOnsCartAdapter addOnsCartAdapter = new AddOnsCartAdapter(context, cartArrayList.get(position).getAddOnsList());
            holder.rcCartAddOns.setAdapter(addOnsCartAdapter);
        }else holder.layoutCartAddOns.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return cartArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView ivMenuImg, ivVegType;
        TextView tvAddCart, tvMenuName, tvMenuPrice, tvMenuOrgPrice, tvPrepTime, tvTotalPrice, tvDecQty, tvQty, tvIncQty, tvOfferPer;
        LinearLayout productCounter, layoutMenu, layoutCartAddOns;
        RecyclerView rcCartAddOns;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutCartAddOns = itemView.findViewById(R.id.layoutCartAddOns);
            layoutMenu = itemView.findViewById(R.id.layoutMenu);
            productCounter = itemView.findViewById(R.id.productCounter);
            ivMenuImg = itemView.findViewById(R.id.ivMenuImg);
            ivVegType = itemView.findViewById(R.id.ivVegType);
            tvMenuName = itemView.findViewById(R.id.tvMenuName);
            tvAddCart = itemView.findViewById(R.id.tvAddCart);
            tvPrepTime = itemView.findViewById(R.id.tvPrepTime);
            tvMenuPrice = itemView.findViewById(R.id.tvMenuPrice);
            tvMenuOrgPrice = itemView.findViewById(R.id.tvMenuOrgPrice);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvDecQty = itemView.findViewById(R.id.tvDecQty);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvIncQty = itemView.findViewById(R.id.tvIncQty);
            tvOfferPer = itemView.findViewById(R.id.tvOfferPer);
            rcCartAddOns = itemView.findViewById(R.id.rcCartAddOns);
            rcCartAddOns.setHasFixedSize(true);
            rcCartAddOns.setLayoutManager(new LinearLayoutManager(context));
        }
    }
}

