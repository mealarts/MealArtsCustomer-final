package com.mealarts.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.mealarts.Helpers.Utils.MenuUtils;
import com.mealarts.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<MenuUtils> orderItemList;

    public OrderItemAdapter(Context context, ArrayList<MenuUtils> orderItemList) {
        this.context = context;
        this.orderItemList = orderItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_order_item, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if(orderItemList.get(position).getVegType().toLowerCase().equals("veg"))
            Glide.with(context).load(R.drawable.veg).into(holder.ivVegType);
        else if(orderItemList.get(position).getVegType().toLowerCase().equals("nonveg"))
            Glide.with(context).load(R.drawable.nonveg).into(holder.ivVegType);
        else Glide.with(context).load(R.drawable.veg).into(holder.ivVegType);

        holder.tvOrderItem.setText(orderItemList.get(position).getProductName());
        holder.tvItemQty.setText(orderItemList.get(position).getQty());

        float ItemPrice = Float.parseFloat(orderItemList.get(position).getQty())
                * Float.parseFloat(orderItemList.get(position).getSellingPrice());
        holder.tvItemPrice.setText("₹ "+Math.round(ItemPrice));
        //hgd
        if(orderItemList.get(position).getGST_Perc().equals("0"))
            holder.tvItemGST.setText("-");
        else
            holder.tvItemGST.setText(holder.df.format(Float.parseFloat(orderItemList.get(position).getGst_price()))+"\n("+orderItemList.get(position).getGST_Perc()+"%)");

        holder.tvItemRate.setText(orderItemList.get(position).getSellingPrice());

        if(orderItemList.get(position).getAddOns()) {
            holder.llAddOnTitle.setVisibility(View.VISIBLE);
            AddOnsInvoiceAdapter addOnsInvoiceAdapter = new AddOnsInvoiceAdapter(context, orderItemList.get(position).getAddOnsList());
            holder.rcOrderAddonItems.setAdapter(addOnsInvoiceAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivVegType;
        private TextView tvOrderItem, tvItemQty, tvItemPrice,tvItemGST,tvItemRate,tvAddOnTitle;
        DecimalFormat df;//hgd
        RecyclerView rcOrderAddonItems;
        LinearLayout llAddOnTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            df = new DecimalFormat("0.00");//hgd

            ivVegType = itemView.findViewById(R.id.ivVegType);
            tvOrderItem = itemView.findViewById(R.id.tvOrderItem);
            tvItemQty = itemView.findViewById(R.id.tvItemQty);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            tvItemGST = itemView.findViewById(R.id.tvItemGST);
            tvItemRate = itemView.findViewById(R.id.tvItemRate);
            llAddOnTitle = itemView.findViewById(R.id.llAddOnTitle);

            rcOrderAddonItems = itemView.findViewById(R.id.rcOrderAddonItems);
            rcOrderAddonItems.setHasFixedSize(true);
            rcOrderAddonItems.setLayoutManager(new LinearLayoutManager(context));
        }
    }
}
