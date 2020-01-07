package com.mealarts.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mealarts.AddToCartActivity;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CustomToast;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddOnsCartAdapter extends RecyclerView.Adapter<AddOnsCartAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<JSONObject> CartAddOnsList;
    private SharedPref sharedPref;
    private CustomToast customToast;
    private int QtyCount = 0;

    AddOnsCartAdapter(Context context, ArrayList<JSONObject> CartAddOnsList) {
        this.context = context;
        this.CartAddOnsList = CartAddOnsList;
        sharedPref = new SharedPref(context);
        customToast = new CustomToast();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.add_ons_list_layout, parent,false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvAddCart.setVisibility(View.GONE);
        try {
            if(!CartAddOnsList.get(position).getString("image").isEmpty()) {
                Glide.with(context).load(URLServices.AddOnImg
                        + CartAddOnsList.get(position).getString("image")).into(holder.ivAddOnImg);
            }
            else Glide.with(context).load(R.drawable.mealarts_icon).into(holder.ivAddOnImg);

            holder.tvAddOnTitle.setText(CartAddOnsList.get(position).getString("addon_name"));
            holder.tvAddOnPrice.setText("₹ "+CartAddOnsList.get(position).getString("sell_price")+"/-");
            if(CartAddOnsList.get(position).getDouble("offer") == 0)
                holder.tvAddOnOrgPrice.setVisibility(View.GONE);
            else{
                holder.tvAddOnOrgPrice.setVisibility(View.VISIBLE);
                holder.tvAddOnOrgPrice.setText("₹ "+CartAddOnsList.get(position).getString("adon_total")+"/-");
            }

            try {
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                JSONArray cartJsonArray = cartObj.getJSONArray(context.getResources().getString(R.string.CartJsonArray));
                if(cartJsonArray.length() != 0){
                    for (int i = 0; i < cartJsonArray.length(); i++) {
                        JSONObject cartJsonItemObj = cartJsonArray.getJSONObject(i);
                        if (CartAddOnsList.get(position).getString("MenuId").equals(cartJsonItemObj.getString("MenuId"))) {
                            if (cartJsonItemObj.has(context.getResources().getString(R.string.AddOnsJsonArray))) {
                                JSONArray addOnsArray = cartJsonItemObj.getJSONArray(context.getResources().getString(R.string.AddOnsJsonArray));
                                for (int j = 0; j < addOnsArray.length(); j++) {
                                    JSONObject addOnObj = addOnsArray.getJSONObject(j);
                                    if (addOnObj.getString("addon_id").equals(CartAddOnsList.get(position).getString("addon_id"))) {
                                        holder.tvQty.setText(addOnObj.getString("Quantity"));
                                        holder.tvTotalPrice.setText("₹ "+addOnObj.getString("QtyPrice")+"/-");
                                        holder.tvTotalPrice.setVisibility(View.VISIBLE);
                                        holder.productCounter.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    }
                    setCartTotal();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            holder.tvIncQty.setOnClickListener(v -> {
                Log.d("/*abc_addons_cart","inc:"+QtyCount);
                int TotalPrice;
                try{
                    JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                    JSONArray cartJsonArray = cartObj.getJSONArray(context.getResources().getString(R.string.CartJsonArray));
                    for(int i = 0 ; i < cartJsonArray.length() ; i++){
                        JSONObject cartItemObj = cartJsonArray.getJSONObject(i);
                        JSONArray addOnsArray = cartItemObj.getJSONArray(context.getResources().getString(R.string.AddOnsJsonArray));
                        for(int l = 0 ; l < addOnsArray.length(); l++){
                            JSONObject addOnsObj = addOnsArray.getJSONObject(l);
                            if(addOnsObj.getString("addon_id").equals(CartAddOnsList.get(position).getString("addon_id"))){
                                QtyCount = addOnsObj.getInt("Quantity");
                                QtyCount++;
                                holder.tvQty.setText(String.valueOf(QtyCount));
                                TotalPrice = addOnsObj.getInt("sell_price") * QtyCount;
                                holder.tvTotalPrice.setText("₹ "+ TotalPrice +"/-");
                                CartAddOnsList.get(position).put("TotalPackAmt", CartAddOnsList.get(position).getDouble("addon_pack_charge") * QtyCount);
                                addOnsObj.put("Quantity", QtyCount);
                                addOnsObj.put("QtyPrice", TotalPrice);
                                sharedPref.setUserCart(String.valueOf(cartObj));
                                Log.e("Cart_Add", sharedPref.getUserCart());
                            }
                        }
                    }
                    setCartTotal();
                }catch (JSONException e){
                    Log.d("/*abc_addons_cart","inc catch "+e.toString());
                    e.printStackTrace();
                }
            });

            holder.tvDecQty.setOnClickListener(v -> {
                Log.d("/*abc_addons_cart","dec clicked");
                int TotalPrice;
                try{
                    JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                    JSONArray cartJsonArray = cartObj.getJSONArray(context.getResources().getString(R.string.CartJsonArray));
                    for(int i = 0 ; i < cartJsonArray.length() ; i++){
                        JSONObject cartItemObj = cartJsonArray.getJSONObject(i);
                        JSONArray addOnsTempArray = cartItemObj.getJSONArray(context.getResources().getString(R.string.AddOnsJsonArray));
                        for(int l = 0 ; l < addOnsTempArray.length(); l++) {
                            JSONObject addOnsObj = addOnsTempArray.getJSONObject(l);
                            try{
                                if (addOnsObj.getString("addon_id").equals(CartAddOnsList.get(position).getString("addon_id"))) {
                                    QtyCount = addOnsObj.getInt("Quantity");
                                    if (QtyCount > 0) {
                                        Log.d("/*abc","dec:"+QtyCount+">0");
                                        QtyCount--;
                                        if(QtyCount > 0){
                                            Log.d("/*abc","dec:"+QtyCount);
                                            holder.tvQty.setText(String.valueOf(QtyCount));
                                            TotalPrice = addOnsObj.getInt("sell_price") * QtyCount;
                                            holder.tvTotalPrice.setText("₹ "+ TotalPrice +"/-");
                                            CartAddOnsList.get(position).put("TotalPackAmt", CartAddOnsList.get(position).getDouble("addon_pack_charge") * QtyCount);
                                            addOnsObj.put("Quantity", QtyCount);
                                            addOnsObj.put("QtyPrice", TotalPrice);
                                        }else {
                                            Log.d("/*abc","dec:"+QtyCount+"<0");
                                            holder.tvTotalPrice.setVisibility(View.INVISIBLE);
                                            holder.productCounter.setVisibility(View.GONE);
                                            CartAddOnsList.get(position).put("Quantity",String.valueOf(QtyCount));
                                            final ArrayList<JSONObject> result = new ArrayList<>(addOnsTempArray.length());
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                addOnsTempArray.remove(l);
                                                CartAddOnsList.remove(l);
                                                customToast.showCustomToast(context, "Removed successfully !");
                                            } else {
                                                for (int n = 0; n < result.size(); n++) {
                                                    if (result.get(n).getString("addon_id").equals(CartAddOnsList.get(position).getString("addon_id"))) {
                                                        result.remove(n);
                                                        CartAddOnsList.remove(n);
                                                        customToast.showCustomToast(context, "Removed successfully !");
                                                    }
                                                }
                                                for (final JSONObject obj : result) {
                                                    addOnsTempArray.put(obj);
                                                }
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                            catch (Exception e){
                                Log.d("/*abc_addons_cart","dec catch "+e.toString());
                            }
                        }
                    }
                    sharedPref.setUserCart(String.valueOf(cartObj));
                    Log.e("Cart_Add", sharedPref.getUserCart());
                    notifyDataSetChanged();

                    setCartTotal();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return CartAddOnsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAddOnImg, ivVegType;
        TextView tvAddOnTitle, tvAddOnOrgPrice, tvAddOnPrice, tvAddCart,tvDecQty, tvQty, tvIncQty, tvTotalPrice;
        LinearLayout productCounter;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAddOnImg = itemView.findViewById(R.id.ivAddOnImg);
            ivVegType = itemView.findViewById(R.id.ivVegType);
            tvAddOnTitle = itemView.findViewById(R.id.tvAddOnTitle);
            tvAddOnOrgPrice = itemView.findViewById(R.id.tvAddOnOrgPrice);
            tvAddOnOrgPrice.setPaintFlags(tvAddOnOrgPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvAddOnPrice = itemView.findViewById(R.id.tvAddOnPrice);
            tvAddCart = itemView.findViewById(R.id.tvAddCart);
            tvDecQty = itemView.findViewById(R.id.tvDecQty);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvIncQty = itemView.findViewById(R.id.tvIncQty);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            productCounter = itemView.findViewById(R.id.productCounter);

        }
    }

    @SuppressLint("SetTextI18n")
    private void setCartTotal() throws JSONException {
        float cartTotal = 0, gstTotal=0, PackingCharge=0;
        JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
        JSONObject cartJSONObj = cartObj.getJSONObject(context.getResources().getString(R.string.CartJsonObj));
        JSONArray cartArray = cartObj.getJSONArray(context.getResources().getString(R.string.CartJsonArray));
        for(int i = 0 ; i < cartArray.length(); i++){
            JSONObject cartItemObjs = cartArray.getJSONObject(i) ;
            cartTotal += Float.parseFloat(cartItemObjs.getString("QtyPrice"));
            PackingCharge += /*(Float.parseFloat(cartItemObjs.getString("Quantity"))
                    **/ Float.parseFloat(cartItemObjs.getString("p_charge"))/*)*/;

            Log.d("/*adp",cartItemObjs.getString("QtyPrice")+", "+cartItemObjs.getString("gstp"));

            if(cartItemObjs.getString("isgst").equals("yes"))
                gstTotal+=Float.parseFloat(cartItemObjs.getString("QtyPrice"))
                        *(Float.parseFloat(cartItemObjs.getString("gstp"))/100);

            if(cartItemObjs.has(context.getResources().getString(R.string.AddOnsJsonArray))){
                JSONArray addOnsArray = cartItemObjs.getJSONArray(context.getResources().getString(R.string.AddOnsJsonArray));
                if(addOnsArray.length() != 0){
                    for(int j = 0 ; j < addOnsArray.length() ; j++){
                        JSONObject addOnsObj = addOnsArray.getJSONObject(j) ;
                        cartTotal += Float.parseFloat(addOnsObj.getString("QtyPrice"));
                        PackingCharge += Float.parseFloat(addOnsObj.getString("TotalPackAmt"));
                    }
                }
            }
        }
        cartJSONObj.put("TotalAmount", String.valueOf(cartTotal));
        cartJSONObj.put("totalPackCharge", PackingCharge);
        cartJSONObj.put("totalGST",String.valueOf(gstTotal));
        sharedPref.setUserCart(String.valueOf(cartObj));
        AddToCartActivity.tvCartTotal.setText("₹ " + cartTotal+"/-");
        Log.d("/*abc_menulistactivity","addons cart adapter: cartotal="+cartTotal);
    }
}
