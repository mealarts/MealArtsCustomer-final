package com.mealarts.Adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mealarts.Helpers.Utils.AddressUtils;
import com.mealarts.R;

import java.util.ArrayList;

public class AddressTypeAdapter extends RecyclerView.Adapter<AddressTypeAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<AddressUtils> AddressTypeList;
    private OnAddressTypeClickListener mListener;
    public void setListener(OnAddressTypeClickListener listener) {
        mListener = listener;
    }
    public interface OnAddressTypeClickListener{
        void onAddressTypeClick(TextView tvAddressType, int position);
    }
    public AddressTypeAdapter(Context context, ArrayList<AddressUtils> AddressTypeList) {
        this.context = context;
        this.AddressTypeList = AddressTypeList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_address_types, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if(AddressTypeList.get(position).getSelected())
            holder.tvAddressType.setBackgroundResource(R.drawable.selected_address_back);
        else holder.tvAddressType.setBackgroundResource(R.drawable.spinner_back);

        if(position == 0)
            holder.layoutAddressType.setGravity(Gravity.START);
        if(position == AddressTypeList.size()-1)
            holder.layoutAddressType.setGravity(Gravity.END);

        holder.tvAddressType.setText(AddressTypeList.get(position).getAddressType());

        holder.tvAddressType.setOnClickListener(v -> {
            mListener.onAddressTypeClick(holder.tvAddressType, position);
        });
    }

    @Override
    public int getItemCount() {
        return AddressTypeList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvAddressType;
        LinearLayout layoutAddressType;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAddressType = itemView.findViewById(R.id.tvAddressType);
            layoutAddressType = itemView.findViewById(R.id.layoutAddressType);
        }
    }
}
