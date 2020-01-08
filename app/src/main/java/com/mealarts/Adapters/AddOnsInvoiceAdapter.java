package com.mealarts.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mealarts.Helpers.Utils.MenuUtils;
import com.mealarts.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AddOnsInvoiceAdapter extends RecyclerView.Adapter<AddOnsInvoiceAdapter.MyViewHolder>{

        private Context context;
        private ArrayList<JSONObject> orderItemList;

        public AddOnsInvoiceAdapter(Context context, ArrayList<JSONObject> orderItemList) {
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

            try{
                if(orderItemList.get(position).getString("veg_type").equals("veg"))
                    Glide.with(context).load(R.drawable.veg).into(holder.ivVegType);
                else if(orderItemList.get(position).getString("veg_type").equals("nonveg"))
                    Glide.with(context).load(R.drawable.nonveg).into(holder.ivVegType);
                else Glide.with(context).load(R.drawable.veg).into(holder.ivVegType);

                holder.tvAddonItem.setText(orderItemList.get(position).getString("addon_name"));
                holder.tvAddonQty.setText(orderItemList.get(position).getString(""));

                float ItemPrice = Float.parseFloat(orderItemList.get(position).getString(""))
                        * Float.parseFloat(orderItemList.get(position).getString("sell_price"));
                holder.tvAddonPrice.setText("₹ "+Math.round(ItemPrice));
                //hgd
                if(orderItemList.get(position).getString("addon_gst_perc").equals("0"))
                    holder.tvAddonGST.setText("-");
                else;
                    //holder.tvAddonGST.setText(holder.df.format(Float.parseFloat(orderItemList.get(position).getGst_price()))+"\n("+orderItemList.get(position).getGST_Perc()+"%)");

                holder.tvAddonRate.setText(orderItemList.get(position).getString("sell_price"));
            }
            catch (JSONException je){
                Log.d("/*addoninvoice","je:"+je.toString());
            }

        }

        @Override
        public int getItemCount() {
            return orderItemList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            private ImageView ivVegType;
            private TextView tvAddonItem, tvAddonQty, tvAddonPrice,tvAddonGST,tvAddonRate;
            DecimalFormat df;//hgd

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                df = new DecimalFormat("0.00");//hgd

                ivVegType = itemView.findViewById(R.id.ivVegType);
                tvAddonItem = itemView.findViewById(R.id.tvOrderItem);
                tvAddonQty = itemView.findViewById(R.id.tvItemQty);
                tvAddonPrice = itemView.findViewById(R.id.tvItemPrice);
                tvAddonGST = itemView.findViewById(R.id.tvItemGST);
                tvAddonRate = itemView.findViewById(R.id.tvItemRate);
            }
        }
    }