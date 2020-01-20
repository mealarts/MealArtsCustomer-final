package com.mealarts.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.mealarts.Helpers.CustomToast;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.Helpers.Utils.MenuUtils;
import com.mealarts.MenuListActivity;
import com.mealarts.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<MenuUtils> MenuArray;
    private int QtyCount = 0;
    private float QtyPrice = 0;
    private SharedPref sharedPref;
    private CustomToast customToast;
    private String CategoryId;

    public MenuAdapter(Context context, ArrayList<MenuUtils> MenuArray, String CategoryId) {
        this.context = context;
        this.MenuArray = MenuArray;
        sharedPref = new SharedPref(context);
        customToast = new CustomToast();
        this.CategoryId = CategoryId;
    }

    private OnMenuItemClickListener mListener;
    private OnMenuItemIncClickListener mIncListener;
    private OnMenuItemDecClickListener mDecListener;
    private ShowAddOnsClickListener mShowAddOnsListener;

    public void setAddListener(OnMenuItemClickListener listener) {
        mListener = listener;
    }
    public void setIncListener(OnMenuItemIncClickListener listener) {
        mIncListener = listener;
    }
    public void setDecListener(OnMenuItemDecClickListener listener) {
        mDecListener = listener;
    }
    public void showAddOnsListener(ShowAddOnsClickListener listener) {
        mShowAddOnsListener = listener;
    }

    public interface OnMenuItemClickListener {
        void onMenuItemClick(int position, int QtyCount, TextView tvQty, LinearLayout layoutAddOnsClick, TextView tvTotalPrice, TextView tvAddCart, LinearLayout productCounter);
    }
    public interface OnMenuItemIncClickListener {
        void onItemIncClick(int position, int QtyCount, TextView tvQty, LinearLayout layoutAddOnsClick, TextView tvTotalPrice, TextView tvAddCart, LinearLayout productCounter);
    }
    public interface OnMenuItemDecClickListener {
        void onItemDecClick(int position, int QtyCount, TextView tvQty, LinearLayout layoutAddOnsClick, TextView tvTotalPrice, TextView tvAddCart, LinearLayout productCounter);
    }
    public interface ShowAddOnsClickListener {
        void onShowAddOnsClick(int position, Boolean hasAddOns, ArrayList<JSONObject> AddOnsArray);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_menu, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if(!MenuArray.get(position).getProductImg().isEmpty()) {
            Glide.with(context).load(URLServices.MenuImg + MenuArray.get(position).getProductImg()).into(holder.ivMenuImg);
            Log.d("/*menu_menu",URLServices.MenuImg + MenuArray.get(position).getProductImg()+"");
        }
        else if(!MenuArray.get(position).getProductImgDefault().isEmpty()) {
            Glide.with(context).load(URLServices.MenuDefaultImg + MenuArray.get(position).getProductImgDefault()).into(holder.ivMenuImg);
            Log.d("/*menu_products",URLServices.MenuDefaultImg + MenuArray.get(position).getProductImgDefault()+"");
        }
        else Glide.with(context).load(R.drawable.mealarts_icon).into(holder.ivMenuImg);

        holder.tvMenuName.setText(MenuArray.get(position).getProductName().trim());
        holder.tvDescription.setText(MenuArray.get(position).getMenuDesc());
        if(MenuArray.get(position).getOfferPer() > 0)
            holder.tvOfferPer.setVisibility(View.VISIBLE);
        else holder.tvOfferPer.setVisibility(View.GONE);
        holder.tvOfferPer.setText(MenuArray.get(position).getOfferPer()+"%");
        //Log.d("/*menu_adapter","offerperc:"+MenuArray.get(position).getOfferPer());
        if(MenuArray.get(position).getOfferPer()==0){
            holder.tvMenuOrgPrice.setVisibility(View.GONE);
        }
        holder.tvPrepTime.setText(MenuArray.get(position).getMenuPrepTime()+" min");
        holder.tvMenuOrgPrice.setText("₹ "+MenuArray.get(position).getOriginalPrice());
        holder.tvMenuPrice.setText("₹ "+MenuArray.get(position).getSellingPrice());
        if(MenuArray.get(position).getVegType().toLowerCase().equals("veg"))
            Glide.with(context).load(R.drawable.veg)
                    .placeholder(R.drawable.mealarts_loader).into(holder.ivVegType);
        else Glide.with(context).load(R.drawable.nonveg)
                .placeholder(R.drawable.mealarts_loader).into(holder.ivVegType);

        holder.tvQty.setText("0");
        holder.layoutAddOnsClick.setVisibility(View.GONE);
        holder.tvAddCart.setVisibility(View.VISIBLE);
        holder.productCounter.setVisibility(View.GONE);

        if(MenuArray.get(position).getAddOns())
            holder.tvAddOns.setVisibility(View.VISIBLE);
        else holder.tvAddOns.setVisibility(View.INVISIBLE);

        Log.e("Cart_madp1", sharedPref.getUserCart()+"_");
        try {
            JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
            if(cartObj.has(context.getResources().getString(R.string.CartJsonObj))) {
                JSONObject cartJsonObj = cartObj.getJSONObject(context.getResources().getString(R.string.CartJsonObj));
                JSONArray cartArray = cartObj.getJSONArray(context.getResources().getString(R.string.CartJsonArray));
                Log.d("/*abc","cartJsonObj:"+cartJsonObj.toString());
                Log.d("/*abc","cartArray:"+cartArray.toString());
                if (cartArray.length() != 0) {
                    for (int i = 0; i < cartArray.length(); i++) {
                        JSONObject cartItemObj = cartArray.getJSONObject(i);
                        if (cartJsonObj.has("VendorId")
                                && cartJsonObj.getString("VendorId").equals(MenuArray.get(position).getVendorId())
                                && cartItemObj.getString("ProductId").equals(MenuArray.get(position).getProductId())
                                && cartJsonObj.getString("DeliveryDate").equals(MenuListActivity.DeliveryDate)
                                && cartJsonObj.getString("CategoryId").equals(CategoryId)) {
                                    holder.tvQty.setText(cartItemObj.getString("Quantity"));
                                    holder.tvTotalPrice.setText("₹ " + cartItemObj.getInt("QtyPrice"));
                                    holder.layoutAddOnsClick.setVisibility(View.VISIBLE);
                                    holder.tvAddCart.setVisibility(View.GONE);
                                    holder.productCounter.setVisibility(View.VISIBLE);
                        }else Log.e("ErrorMsg", cartJsonObj.getString("CategoryId")+".equals "+(CategoryId));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//----------------------------------Set Vendor Menu----------------------------------//

        holder.tvAddCart.setOnClickListener(v -> {
            MenuListActivity.getMenuPosition = position;
            Log.e("PosItem", String.valueOf(MenuListActivity.getMenuPosition));
            QtyCount = 0;
            if(mListener!=null) {
                mListener.onMenuItemClick(position, QtyCount, holder.tvQty, holder.layoutAddOnsClick, holder.tvTotalPrice, holder.tvAddCart, holder.productCounter);
            }
            else{
                Log.d("/*abc","mlistener empty");
            }
        });

        holder.tvIncQty.setOnClickListener(v -> {
            MenuListActivity.getMenuPosition = position;
            Log.e("PosItem", String.valueOf(MenuListActivity.getMenuPosition));
            if(mIncListener!=null)
                mIncListener.onItemIncClick(position, QtyCount, holder.tvQty, holder.layoutAddOnsClick, holder.tvTotalPrice, holder.tvAddCart, holder.productCounter);
            else
                Log.d("/*abc","mIncListener empty");
        });

        MenuListActivity.tvIncQty.setOnClickListener(v -> {
            Log.e("PosItem", String.valueOf(MenuListActivity.getMenuPosition));
            try {
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                JSONArray cartArray = cartObj.getJSONArray(context.getResources().getString(R.string.CartJsonArray));
                for (int i = 0; i < cartArray.length(); i++) {
                    if (cartArray.getJSONObject(i).getString("MenuId").equals(MenuArray.get(MenuListActivity.getMenuPosition).getMenuId())) {
                        mIncListener.onItemIncClick(MenuListActivity.getMenuPosition, QtyCount, holder.tvQty, holder.layoutAddOnsClick, holder.tvTotalPrice, holder.tvAddCart, holder.productCounter);
                        break;
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        });

        holder.tvDecQty.setOnClickListener(v -> {
            MenuListActivity.getMenuPosition = position;
            Log.e("PosItem", String.valueOf(MenuListActivity.getMenuPosition));
            if(mDecListener!=null)
                mDecListener.onItemDecClick(position, QtyCount, holder.tvQty, holder.layoutAddOnsClick, holder.tvTotalPrice, holder.tvAddCart, holder.productCounter);
            else
                Log.d("/*abc","mDecListener empty");
        });

        MenuListActivity.tvDecQty.setOnClickListener(v -> {
            Log.e("PosItem", String.valueOf(MenuListActivity.getMenuPosition));
            try {
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                JSONArray cartArray = cartObj.getJSONArray(context.getResources().getString(R.string.CartJsonArray));
                for(int i = 0; i < cartArray.length(); i++) {
                    if (cartArray.getJSONObject(i).getString("MenuId").equals(MenuArray.get(MenuListActivity.getMenuPosition).getMenuId())) {
                        mDecListener.onItemDecClick(MenuListActivity.getMenuPosition, QtyCount, holder.tvQty, holder.layoutAddOnsClick, holder.tvTotalPrice, holder.tvAddCart, holder.productCounter);
                        break;
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        });

        holder.tvAddOns.setOnClickListener(v -> {
            Log.d("/*abc_menuadapter","addons clicked");
            MenuListActivity.getMenuPosition = position;
            if(mShowAddOnsListener!=null)
                mShowAddOnsListener.onShowAddOnsClick(position, MenuArray.get(position).getAddOns(), MenuArray.get(position).getAddOnsList());
            else
                Log.d("/*abc","mShowAddOnsListener empty");
        });
    }

    @Override
    public int getItemCount() {
        return MenuArray.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView ivMenuImg, ivVegType;
        TextView tvAddCart, tvMenuName,tvDescription, tvMenuOrgPrice, tvPrepTime, tvOfferPer,
                tvMenuPrice, tvTotalPrice, tvDecQty, tvQty, tvIncQty, tvAddOns;
        LinearLayout productCounter, layoutMenu, layoutAddOnsClick;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutAddOnsClick = itemView.findViewById(R.id.layoutAddOnsClick);
            layoutMenu = itemView.findViewById(R.id.layoutMenu);
            productCounter = itemView.findViewById(R.id.productCounter);
            ivMenuImg = itemView.findViewById(R.id.ivMenuImg);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivVegType = itemView.findViewById(R.id.ivVegType);
            tvMenuName = itemView.findViewById(R.id.tvMenuName);
            tvAddCart = itemView.findViewById(R.id.tvAddCart);
            tvOfferPer = itemView.findViewById(R.id.tvOfferPer);
            tvPrepTime = itemView.findViewById(R.id.tvPrepTime);
            tvMenuOrgPrice = itemView.findViewById(R.id.tvMenuOrgPrice);
            tvMenuOrgPrice.setPaintFlags(tvMenuOrgPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvMenuPrice = itemView.findViewById(R.id.tvMenuPrice);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvDecQty = itemView.findViewById(R.id.tvDecQty);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvIncQty = itemView.findViewById(R.id.tvIncQty);
            tvAddOns = itemView.findViewById(R.id.tvAddOns);
            tvAddOns.setPaintFlags(tvAddOns.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
    }
}
