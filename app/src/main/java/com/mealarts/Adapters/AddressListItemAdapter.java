package com.mealarts.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CustomToast;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.Helpers.Utils.AddressUtils;
import com.mealarts.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddressListItemAdapter extends RecyclerView.Adapter<AddressListItemAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<AddressUtils> AddressList;
    private SharedPref sharedPref;

    public AddressListItemAdapter(Context context, ArrayList<AddressUtils> AddressList) {
        this.context = context;
        this.AddressList = AddressList;
        sharedPref = new SharedPref(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_address_listitem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tvAddressType.setText(AddressList.get(position).getAddressType());
        if(AddressList.get(position).getAddressType().equals("Home"))
            holder.ivAddressType.setImageResource(R.drawable.home_add);
        if(AddressList.get(position).getAddressType().equals("Work"))
            holder.ivAddressType.setImageResource(R.drawable.work_add);
        if(AddressList.get(position).getAddressType().equals("Other"))
            holder.ivAddressType.setImageResource(R.drawable.other_add);

        if(AddressList.get(position).getLocation().toLowerCase().contains(AddressList.get(position).getLandmark().toLowerCase())) {
            holder.tvFullAddress.setText(AddressList.get(position).getFlat() + ", "
                    + AddressList.get(position).getLocation());
        }
        else {
            holder.tvFullAddress.setText(AddressList.get(position).getFlat() + ", "
                    + AddressList.get(position).getLandmark() + ", "
                    + AddressList.get(position).getLocation());
        }

        holder.layoutAddress.setOnClickListener(v -> {
            try {
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                JSONObject locationObj = cartObj.getJSONObject(context.getResources().getString(R.string.LocationJson));
                locationObj.put("Location", AddressList.get(position).getLocation());
                locationObj.put("FlatHouseNo", AddressList.get(position).getFlat());
                locationObj.put("Landmark", AddressList.get(position).getLandmark());
                locationObj.put("Latitude", AddressList.get(position).getLatitude());
                locationObj.put("Longitude", AddressList.get(position).getLongitude());
                locationObj.put("AddressType", AddressList.get(position).getAddressType());
                locationObj.put("FullAddress", AddressList.get(position).getFlat()+", "
                        +AddressList.get(position).getLandmark()+", "+AddressList.get(position).getLocation());

                sharedPref.setUserCart(cartObj.toString());
                ((Activity) context).onBackPressed();
            }catch (JSONException e){
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return AddressList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutAddress;
        TextView tvAddressType, tvFullAddress;
        ImageView ivAddressType;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutAddress = itemView.findViewById(R.id.layoutAddress);
            tvAddressType = itemView.findViewById(R.id.tvAddressType);
            tvFullAddress = itemView.findViewById(R.id.tvFullAddress);
            ivAddressType = itemView.findViewById(R.id.ivAddressType);
        }
    }
}
