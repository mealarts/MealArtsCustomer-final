package com.mealarts.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mealarts.Helpers.Utils.CategoryUtils;
import com.mealarts.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<CategoryUtils> categoryArray;

    public CategoryAdapter(Context context, ArrayList<CategoryUtils> categoryArray) {
        this.context = context;
        this.categoryArray = categoryArray;
    }

    private OnCategoryClickListener mListener;

    public void setListener(OnCategoryClickListener listener) {
        mListener = listener;
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(int position, TextView tvCategory);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_category, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.tvCategory.setText(categoryArray.get(position).getCategoryText());
        if(categoryArray.get(position).getSelected()) {
            holder.tvCategory.setBackgroundResource(R.drawable.cat_back_menu);
            holder.tvCategory.setTextColor(context.getResources().getColor(R.color.colorWhite));
        }
        else {
            holder.tvCategory.setBackgroundResource(R.drawable.cat_click_back);
            holder.tvCategory.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }
        holder.tvCategory.setOnClickListener(v -> mListener.onCategoryClick(position, holder.tvCategory));
    }

    @Override
    public int getItemCount() {
        return categoryArray.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvCategory;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}
