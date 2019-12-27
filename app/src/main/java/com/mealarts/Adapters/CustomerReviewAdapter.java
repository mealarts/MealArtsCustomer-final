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

import com.mealarts.Helpers.CustomRatingBar;
import com.mealarts.Helpers.Utils.ReviewUtils;
import com.mealarts.R;

import java.util.ArrayList;

public class CustomerReviewAdapter extends RecyclerView.Adapter<CustomerReviewAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<ReviewUtils> customerRatingList;

    public CustomerReviewAdapter(Context context, ArrayList<ReviewUtils> customerRatingList) {
        this.context = context;
        this.customerRatingList = customerRatingList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_customer_review, parent,false );
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvCustomerName.setText(customerRatingList.get(position).getCustomerName());
        holder.tvReviewDate.setText(customerRatingList.get(position).getRatingDate());
        holder.tvReview.setText(customerRatingList.get(position).getReview());
        holder.layoutRating.setScore(customerRatingList.get(position).getRating());
    }

    @Override
    public int getItemCount() {
        return customerRatingList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCustomerName, tvReview, tvReviewDate;
        private CustomRatingBar layoutRating;
        private ImageView ivCustomerImg;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCustomerImg = itemView.findViewById(R.id.ivCustomerImg);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            tvReview = itemView.findViewById(R.id.tvReview);
            layoutRating = itemView.findViewById(R.id.layoutRating);
        }
    }
}
