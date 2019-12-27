package com.mealarts.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.Helpers.Utils.BottomMenuUtils;
import com.mealarts.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BottomMenuAdapter extends RecyclerView.Adapter<BottomMenuAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<BottomMenuUtils> bottomMenuArray;
    private Integer[] selectPosition = {R.drawable.home_select, R.drawable.menu_card_select,
            R.drawable.account_select, R.drawable.cart_select};
    private String[] menuList = {"Home", "Menu", "Profile", "Cart"};
    private SharedPref sharedPref;

    public BottomMenuAdapter(Context context, ArrayList<BottomMenuUtils> bottomMenuArray) {

        this.context = context;
        this.bottomMenuArray = bottomMenuArray;
        sharedPref = new SharedPref(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_bottom_menu, parent, false);
        return new MyViewHolder(view);
    }

    private OnBottomTabClickListener mListener;

    public void setListener(OnBottomTabClickListener listener) {
        mListener = listener;
    }

    public interface OnBottomTabClickListener {
        void onBottomTabClick(int position, ImageView imgView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        Picasso.get().load(bottomMenuArray.get(position).getBottomTab()).into(holder.bottom_tab);
        holder.tvMenu.setText(menuList[position]);

        if(position == 3)
            holder.tvCartCount.setVisibility(View.VISIBLE);
        else holder.tvCartCount.setVisibility(View.GONE);

        try {
            JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
            if(cartObj.has(context.getResources().getString(R.string.CartJsonArray))) {
                JSONArray cartArray = cartObj.getJSONArray(context.getResources().getString(R.string.CartJsonArray));
                if (cartArray.length() != 0) {
                    holder.tvCartCount.setText(cartArray.length() + "");
                } else holder.tvCartCount.setText("0");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (bottomMenuArray.get(position).getSelected()) {
            Picasso.get().load(selectPosition[position]).into(holder.bottom_tab);
            holder.tvMenu.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }else {
            Picasso.get().load(bottomMenuArray.get(position).getBottomTab()).into(holder.bottom_tab);
            holder.tvMenu.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }

        holder.bottom_tab.setOnClickListener(v -> mListener.onBottomTabClick(position, holder.bottom_tab));

    }

    @Override
    public int getItemCount() {
        return bottomMenuArray.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView bottom_tab;
        TextView tvCartCount, tvMenu;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            bottom_tab = itemView.findViewById(R.id.bottom_tab);
            tvCartCount = itemView.findViewById(R.id.tvCartCount);
            tvMenu = itemView.findViewById(R.id.tvMenu);
        }
    }
}
