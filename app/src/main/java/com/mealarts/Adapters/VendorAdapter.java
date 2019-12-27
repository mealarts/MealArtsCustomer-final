package com.mealarts.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.Utils.VendorUtils;
import com.mealarts.R;

import java.util.ArrayList;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<VendorUtils> VendorArray;

    public VendorAdapter(Context context, ArrayList<VendorUtils> VendorArray) {
        this.context = context;
        this.VendorArray = VendorArray;

    }

    private OnVendorClickListener mListener;

    public void setListener(OnVendorClickListener listener) {
        mListener = listener;
    }

    public interface OnVendorClickListener {
        void onVendorClick(int position, CardView cardVendor);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_chef_menu, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if(VendorArray.get(position).getSelected())
            holder.cardVendor.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        else holder.cardVendor.setCardBackgroundColor(context.getResources().getColor(R.color.colorTrans));

        Glide.with(context).load(URLServices.VendorImg + VendorArray.get(position).getVendorImg())
                .apply(RequestOptions.circleCropTransform()).error(R.drawable.mealarts_icon).into(holder.ivVendorImg);

        holder.tvVendorName.setText(VendorArray.get(position).getVendorName());
        holder.tvVendorRating.setText(VendorArray.get(position).getRating());
        holder.tvVendorInfo.setText(/*VendorArray.get(position).getDistanceKm()+"Km - */"₹ "+VendorArray.get(position).getDeliveryCharge());

        holder.ivVendorImg.setOnClickListener(v -> mListener.onVendorClick(position, holder.cardVendor));
    }

    @Override
    public int getItemCount() {
        return VendorArray.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView ivVendorImg;
        TextView tvVendorName, tvVendorInfo, tvVendorRating;
        CardView cardVendor;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivVendorImg = itemView.findViewById(R.id.ivVendorImg);
            tvVendorName = itemView.findViewById(R.id.tvVendorName);
            tvVendorInfo = itemView.findViewById(R.id.tvVendorInfo);
            tvVendorRating = itemView.findViewById(R.id.tvVendorRating);
            cardVendor = itemView.findViewById(R.id.cardVendor);
        }
    }
}
