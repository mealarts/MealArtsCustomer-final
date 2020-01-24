package com.mealarts.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.GlideApp;
import com.mealarts.Helpers.Utils.VendorUtils;
import com.mealarts.MainActivity;
import com.mealarts.MenuListActivity;
import com.mealarts.R;

import java.util.ArrayList;

public class VendorAdapterHome extends RecyclerView.Adapter<VendorAdapterHome.MyViewHolder>{

    private Context context;
    private ArrayList<VendorUtils> VendorArray;

    public VendorAdapterHome(Context context, ArrayList<VendorUtils> VendorArray) {
        this.context = context;
        this.VendorArray = VendorArray;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_vendor_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Log.e("VendorImg",URLServices.VendorImg + VendorArray.get(position).getVendorImg());
        Glide.with(context).load(URLServices.VendorImg + VendorArray.get(position).getVendorImg())
                    .error(R.drawable.mealarts_icon).into(holder.ivVendorImg);

        holder.tvVendorName.setText(VendorArray.get(position).getVendorName());
        holder.tvVendorCat.setText(VendorArray.get(position).getVendorCat());
        holder.tvVendorSpeciality.setText(VendorArray.get(position).getSpeciality());
        holder.tvVendorCuisines.setText(VendorArray.get(position).getCuisines());
        holder.tvVendorRating.setText(VendorArray.get(position).getRating());

        float rating = Float.parseFloat(VendorArray.get(position).getRating());
        float ratingCount = Float.parseFloat(VendorArray.get(position).getRatingCount());
        if(ratingCount == 0) {
            holder.ivNewVendor.setVisibility(View.VISIBLE);
            holder.tvVendorRating.setVisibility(View.GONE);
        }else {
            holder.ivNewVendor.setVisibility(View.GONE);
            holder.tvVendorRating.setVisibility(View.VISIBLE);
            if (rating == 0.0)
                holder.tvVendorRating.setBackgroundResource(R.drawable.rating_badge_0);
            else if (rating > 0.0 && rating <= 1.0)
                holder.tvVendorRating.setBackgroundResource(R.drawable.rating_badge_1);
            else if (rating > 1.0 && rating <= 2.0)
                holder.tvVendorRating.setBackgroundResource(R.drawable.rating_badge_2);
            else if (rating > 2.0 && rating <= 3.0)
                holder.tvVendorRating.setBackgroundResource(R.drawable.rating_badge_3);
            else if (rating > 3.0 && rating <= 4.0)
                holder.tvVendorRating.setBackgroundResource(R.drawable.rating_badge_4);
            else if (rating > 4.0 || rating <= 5.0)
                holder.tvVendorRating.setBackgroundResource(R.drawable.rating_badge_5);
        }

        holder.layoutVendor.setOnClickListener(v -> {
            Intent intent = new Intent(context, MenuListActivity.class);
            intent.putExtra("FromWhere", true);
            intent.putExtra("VendorId", VendorArray.get(position).getVendorId());
            context.startActivity(intent);
            ((Activity)context).finish();
        });
    }

    @Override
    public int getItemCount() {
        return VendorArray.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView ivVendorImg, ivNewVendor;
        TextView tvVendorName, tvVendorRating, tvVendorCat, tvVendorSpeciality, tvVendorCuisines;
        CardView cardVendor;
        LinearLayout layoutVendor;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutVendor = itemView.findViewById(R.id.layoutVendor);
            ivVendorImg = itemView.findViewById(R.id.ivVendorImg);
            ivNewVendor = itemView.findViewById(R.id.ivNewVendor);
            tvVendorName = itemView.findViewById(R.id.tvVendorName);
            tvVendorRating = itemView.findViewById(R.id.tvVendorRating);
            tvVendorCat = itemView.findViewById(R.id.tvVendorCat);
            tvVendorSpeciality = itemView.findViewById(R.id.tvVendorSpeciality);
            tvVendorCuisines = itemView.findViewById(R.id.tvVendorCuisines);
            cardVendor = itemView.findViewById(R.id.cardVendor);
        }
    }
}
