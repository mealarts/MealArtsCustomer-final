package com.mealarts.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.MenuListActivity;
import com.mealarts.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.mealarts.MenuListActivity.layoutCartTotal;
import static com.mealarts.MenuListActivity.tvAddCartTotal;

public class AddOnsMenuAdapter extends RecyclerView.Adapter<AddOnsMenuAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<JSONObject> MenuAddOnsList;
    private String Type;
    private SharedPref sharedPref;
    private int QtyCount = 0;

    public AddOnsMenuAdapter(Context context, ArrayList<JSONObject> MenuAddOnsList, String Type) {
        this.context = context;
        this.MenuAddOnsList = MenuAddOnsList;
        this.Type = Type;
        sharedPref = new SharedPref(context);
    }

    private OnAddOnClickListener mListener;
    private OnAddOnIncClickListener mIncListener;
    private OnAddOnDecClickListener mDecListener;

    public void setListener(OnAddOnClickListener listener) {
        mListener = listener;
    }
    public void setIncListener(OnAddOnIncClickListener listener) {
        mIncListener = listener;
    }
    public void setDecListener(OnAddOnDecClickListener listener) {
        mDecListener = listener;
    }

    public interface OnAddOnClickListener {
        void onAddOnClick(int position, int QtyCount, ArrayList<JSONObject> MenuAddOnsList, TextView tvQty, TextView tvTotalPrice, TextView tvAddCart, LinearLayout productCounter);
    }public interface OnAddOnIncClickListener {
        void onAddOnIncClick(int position, int QtyCount, ArrayList<JSONObject> MenuAddOnsList, TextView tvQty, TextView tvTotalPrice, TextView tvAddCart, LinearLayout productCounter);
    }public interface OnAddOnDecClickListener {
        void onAddOnDecClick(int position, int QtyCount, ArrayList<JSONObject> MenuAddOnsList, TextView tvQty, TextView tvTotalPrice, TextView tvAddCart, LinearLayout productCounter);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate( Type.equals("Grid") ? R.layout.add_ons_grid_layout
                : R.layout.add_ons_list_layout, parent,false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            if(!MenuAddOnsList.get(position).getString("image").isEmpty()) {
                Glide.with(context).load(URLServices.AddOnImg
                        + MenuAddOnsList.get(position).getString("image")).into(holder.ivAddOnImg);
            }
            else Glide.with(context).load(R.drawable.mealarts_icon).into(holder.ivAddOnImg);

            holder.tvAddOnTitle.setText(MenuAddOnsList.get(position).getString("addon_name"));
            holder.tvAddOnPrice.setText("₹ "+MenuAddOnsList.get(position).getString("sell_price")+"/-");
            if(MenuAddOnsList.get(position).getDouble("offer") == 0)
                holder.tvAddOnOrgPrice.setVisibility(View.GONE);
            else{
                holder.tvAddOnOrgPrice.setVisibility(View.VISIBLE);
                holder.tvAddOnOrgPrice.setText("₹ "+MenuAddOnsList.get(position).getString("adon_total")+"/-");
            }

            try {
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                JSONArray cartJsonArray = cartObj.getJSONArray(context.getResources().getString(R.string.CartJsonArray));
                if(cartJsonArray.length() != 0){
                    for (int i = 0; i < cartJsonArray.length(); i++) {
                        JSONObject cartJsonItemObj = cartJsonArray.getJSONObject(i);
                        if (MenuAddOnsList.get(position).getString("MenuId").equals(cartJsonItemObj.getString("MenuId"))) {
                            if (cartJsonItemObj.has(context.getResources().getString(R.string.AddOnsJsonArray))) {
                                JSONArray addOnsArray = cartJsonItemObj.getJSONArray(context.getResources().getString(R.string.AddOnsJsonArray));
                                for (int j = 0; j < addOnsArray.length(); j++) {
                                    JSONObject addOnObj = addOnsArray.getJSONObject(j);
                                    if (addOnObj.getString("addon_id").equals(MenuAddOnsList.get(position).getString("addon_id"))) {
                                        holder.tvQty.setText(addOnObj.getString("Quantity"));
                                        holder.tvTotalPrice.setText("₹ "+addOnObj.getString("QtyPrice")+"/-");
                                        holder.tvTotalPrice.setVisibility(View.VISIBLE);
                                        holder.tvAddCart.setVisibility(View.GONE);
                                        holder.productCounter.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    }
                    setCartTotal();
                   // setAddOnsTempTotal();
                }else {
                    cartObj.put(context.getResources().getString(R.string.AddOnsJsonArray), new JSONArray());
                    sharedPref.setUserCart(String.valueOf(cartObj));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            holder.tvAddCart.setOnClickListener(v -> {
                mListener.onAddOnClick(position, QtyCount, MenuAddOnsList, holder.tvQty, holder.tvTotalPrice,
                        holder.tvAddCart, holder.productCounter);
            });

            holder.tvIncQty.setOnClickListener(v -> {
                mIncListener.onAddOnIncClick(position, QtyCount, MenuAddOnsList, holder.tvQty, holder.tvTotalPrice,
                        holder.tvAddCart, holder.productCounter);
            });

            holder.tvDecQty.setOnClickListener(v -> {
                mDecListener.onAddOnDecClick(position, QtyCount, MenuAddOnsList, holder.tvQty, holder.tvTotalPrice,
                        holder.tvAddCart, holder.productCounter);
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return MenuAddOnsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAddOnImg, ivVegType;
        TextView tvAddOnTitle, tvAddOnOrgPrice, tvAddOnPrice, tvAddCart, tvDecQty, tvQty, tvIncQty, tvTotalPrice;
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
    public void setCartTotal() throws JSONException {
        float cartTotal = 0, gstTotal=0, PackingCharge=0;
        int menuItem=0,addOnItem=0,totalItem=0;
        JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
        JSONObject cartJSONObj = cartObj.getJSONObject(context.getResources().getString(R.string.CartJsonObj));
        JSONArray cartArray = cartObj.getJSONArray(context.getResources().getString(R.string.CartJsonArray));
        for(int i = 0 ; i < cartArray.length(); i++){
            JSONObject cartItemObjs = cartArray.getJSONObject(i) ;
            menuItem++;
            totalItem+=cartItemObjs.getInt("Quantity");
            cartTotal += Float.parseFloat(cartItemObjs.getString("QtyPrice"));
            PackingCharge += (Float.parseFloat(cartItemObjs.getString("Quantity"))
                    * Float.parseFloat(cartItemObjs.getString("p_charge")));

            Log.d("/*adp",cartItemObjs.getString("QtyPrice")+", "+cartItemObjs.getString("gstp"));

            if(cartItemObjs.getString("isgst").equals("yes"))
                gstTotal+=Float.parseFloat(cartItemObjs.getString("QtyPrice"))
                        *(Float.parseFloat(cartItemObjs.getString("gstp"))/100);

            if(cartItemObjs.has(context.getResources().getString(R.string.AddOnsJsonArray))){
                JSONArray addOnsArray = cartItemObjs.getJSONArray(context.getResources().getString(R.string.AddOnsJsonArray));
                if(addOnsArray.length() != 0){
                    for(int j = 0 ; j < addOnsArray.length() ; j++){
                        JSONObject addOnsObj = addOnsArray.getJSONObject(j) ;
                        addOnItem++;
                        totalItem+=addOnsObj.getInt("Quantity");
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
        tvAddCartTotal.setText("₹ " + Math.round(cartTotal) +"/-");
        MenuListActivity.tvMenuTotal.setText(menuItem + " Menus, "+addOnItem+" AddOns, "+totalItem+ " Items   ₹ " + Math.round(cartTotal) + "/-");
        Log.d("/*addonsmenuadp",(menuItem+addOnItem) + " Items " + "₹ " + Math.round(cartTotal) + "/-");
    }

    @SuppressLint("SetTextI18n")
    private void setAddOnsTempTotal() throws JSONException{
        Log.d("/*addonsmenuadp","in setAddOnsTempTotal");
//        int TotalPerMenu = 0;
//        int TotalQtyCount = 0;
//        JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
//        JSONArray cartJsonArray = cartObj.getJSONArray(context.getResources().getString(R.string.CartJsonArray));
//        for (int k = 0; k < cartJsonArray.length(); k++) {
//            JSONObject cartItemObj = cartJsonArray.getJSONObject(k);
//            TotalPerMenu = cartItemObj.getInt("QtyPrice");
//            TotalQtyCount = Integer.parseInt(cartItemObj.getString("Quantity"));
//
//            if(cartItemObj.has(context.getResources().getString(R.string.AddOnsJsonArray))
//                    && cartItemObj.getJSONArray(context.getResources().getString(R.string.AddOnsJsonArray)).length() != 0){
//                Log.d("/*addonsmenuadp","in cartItemObj.has(AddOnsJsonArray))");
//                JSONArray addOnsArray = cartItemObj.getJSONArray(context.getResources().getString(R.string.AddOnsJsonArray));
//                for(int j = 0 ; j < addOnsArray.length() ; j++){
//                    JSONObject addOnsObj = addOnsArray.getJSONObject(j) ;
//                    if(addOnsObj.getString("MenuId").equals(cartItemObj.getString("MenuId"))) {
//                        TotalPerMenu += addOnsObj.getInt("QtyPrice");
//                        TotalQtyCount += Integer.parseInt(addOnsObj.getString("Quantity"));
//                    }
//                }
//            }else if(cartObj.has("AddOnsJsonArray")) {
//                Log.d("/*addonsmenuadp","not in cartItemObj.has(AddOnsJsonArray))");
//                JSONArray addOnArray = cartObj.getJSONArray(context.getResources().getString(R.string.AddOnsJsonArray));
//                for (int j = 0; j < addOnArray.length(); j++) {
//                    JSONObject addOnObj = addOnArray.getJSONObject(j);
//                    if(addOnObj.getString("MenuId").equals(cartItemObj.getString("MenuId"))) {
//                        TotalPerMenu += addOnObj.getInt("QtyPrice");
//                        TotalQtyCount += Integer.parseInt(addOnObj.getString("Quantity"));
//                    }
//                }
//            }
//        }

        JSONObject cartObj;
        float cartTotal = 0, gstTotal=0, PackingCharge=0,itemCount=0;
        cartObj = new JSONObject(sharedPref.getUserCart());
        JSONObject cartJSONObj = cartObj.getJSONObject(context.getResources().getString(R.string.CartJsonObj));
        JSONArray cartArray = cartObj.getJSONArray(context.getResources().getString(R.string.CartJsonArray));

        for(int i = 0 ; i < cartArray.length(); i++){
            JSONObject cartItemObjs = cartArray.getJSONObject(i) ;
            itemCount++;
            cartTotal += Float.parseFloat(cartItemObjs.getString("QtyPrice"));
            PackingCharge += /*(Float.parseFloat(cartItemObjs.getString("Quantity")) * )*/
                    Float.parseFloat(cartItemObjs.getString("p_charge"));

            Log.d("/*adp",cartItemObjs.getString("QtyPrice")+", "+cartItemObjs.getString("gstp"));

            if(cartItemObjs.getString("isgst").equals("yes"))
                gstTotal+=Float.parseFloat(cartItemObjs.getString("QtyPrice"))
                        *(Float.parseFloat(cartItemObjs.getString("gstp"))/100);

            if(cartItemObjs.has(context.getResources().getString(R.string.AddOnsJsonArray))){
                JSONArray addOnsArray = cartItemObjs.getJSONArray(context.getResources().getString(R.string.AddOnsJsonArray));
                if(addOnsArray.length() != 0){
                    for(int j = 0 ; j < addOnsArray.length() ; j++){
                        JSONObject addOnsObj = addOnsArray.getJSONObject(j) ;
                        itemCount++;
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
        tvAddCartTotal.setText("₹ " + Math.round(cartTotal) +"/-");
        MenuListActivity.tvMenuTotal.setText(itemCount + " Items " + "₹ " + Math.round(cartTotal) + "/-");
        Log.d("/*addonsmenuadp",itemCount + " Items " + "₹ " + Math.round(cartTotal) + "/-");
    }
}
