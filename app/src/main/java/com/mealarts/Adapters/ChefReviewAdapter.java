package com.mealarts.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CustomRatingBar;
import com.mealarts.Helpers.Utils.VendorUtils;
import com.mealarts.R;
import com.mealarts.ChefDetailActivity;

import java.util.ArrayList;

public class ChefReviewAdapter extends RecyclerView.Adapter<ChefReviewAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<VendorUtils> chefRatingList;

    public ChefReviewAdapter(Context context, ArrayList<VendorUtils> chefRatingList) {
        this.context = context;
        this.chefRatingList = chefRatingList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_chef_review, parent,false );
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvChefName.setText(chefRatingList.get(position).getVendorName());

        Glide.with(context).load(URLServices.VendorImg + chefRatingList.get(position).getVendorImg())
                .error(R.drawable.mealarts_icon).into(holder.ivChef);

        holder.tvAreaCity.setText(chefRatingList.get(position).getArea()+", "+chefRatingList.get(position).getCity());
        holder.layoutChefRating.setScore(Float.parseFloat(chefRatingList.get(position).getRating()));

        holder.layoutChefDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChefDetailActivity.class);
            intent.putExtra("VendorId", chefRatingList.get(position).getVendorId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chefRatingList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvChefName, tvAreaCity;
        private CustomRatingBar layoutChefRating;
        private ImageView ivChef;
        private LinearLayout layoutChefDetail;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivChef = itemView.findViewById(R.id.ivChef);
            layoutChefDetail = itemView.findViewById(R.id.layoutChefDetail);
            tvChefName = itemView.findViewById(R.id.tvChefName);
            tvAreaCity = itemView.findViewById(R.id.tvAreaCity);
            layoutChefRating = itemView.findViewById(R.id.layoutChefRating);
        }
    }
}
