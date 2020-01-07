package com.mealarts.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.DeliveryAddressActivity;
import com.mealarts.Helpers.CustomToast;
import com.mealarts.Helpers.Utils.AddressUtils;
import com.mealarts.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddressItemAdapter extends RecyclerView.Adapter<AddressItemAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<AddressUtils> AddressList;
    private SSLCertification sslCertification;
    private CustomToast customToast;

    public AddressItemAdapter(Context context, ArrayList<AddressUtils> AddressList) {
        this.context = context;
        this.AddressList = AddressList;
        sslCertification = new SSLCertification(context);
        customToast = new CustomToast();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_address_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tvAddressType.setText(AddressList.get(position).getAddressType());
        if(AddressList.get(position).getAddressType().equals("Home"))
            holder.ivAddressType.setImageResource(R.drawable.home_add);
        if(AddressList.get(position).getAddressType().equals("Work"))
            holder.ivAddressType.setImageResource(R.drawable.work_add);
        if(AddressList.get(position).getAddressType().equals("Other"))
            holder.ivAddressType.setImageResource(R.drawable.other_add);

        holder.tvFullAddress.setText(AddressList.get(position).getFlat() + ","
                + AddressList.get(position).getLandmark() + ", "
                + AddressList.get(position).getLocation());

        holder.ivEditAddress.setOnClickListener(v -> {
            Intent intent = new Intent(context, DeliveryAddressActivity.class);
            intent.putExtra("fromWhere", "ManageAddress");                               //to check redirection of activity.
            intent.putExtra("Action", "edit");
            intent.putExtra("AddressId", AddressList.get(position).getAddressId());
            intent.putExtra("Latitude", AddressList.get(position).getLatitude());
            intent.putExtra("Longitude", AddressList.get(position).getLongitude());
            intent.putExtra("AddressType", AddressList.get(position).getAddressType());
            intent.putExtra("Location", AddressList.get(position).getLocation());
            intent.putExtra("Landmark", AddressList.get(position).getLandmark());
            intent.putExtra("FlatHouseNo", AddressList.get(position).getFlat());
            context.startActivity(intent);
        });

        holder.ivDeleteAddress.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setMessage("Are You Sure to Delete Address ?")
                    .setPositiveButton("Yes", (paramDialogInterface, paramInt) ->
                            deleteLocation(position, "delete", AddressList.get(position).getAddressId()))
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    }).show();
        });
    }

    @Override
    public int getItemCount() {
        return AddressList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvAddressType, tvFullAddress;
        ImageView ivAddressType, ivEditAddress, ivDeleteAddress;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAddressType = itemView.findViewById(R.id.tvAddressType);
            tvFullAddress = itemView.findViewById(R.id.tvFullAddress);
            ivEditAddress = itemView.findViewById(R.id.ivEditAddress);
            ivDeleteAddress = itemView.findViewById(R.id.ivDeleteAddress);
            ivAddressType = itemView.findViewById(R.id.ivAddressType);
        }
    }

    public void deleteLocation(int position, String Action, String AddressId){

        Map<String, String> postData = new HashMap<>();
        postData.put("action", Action);
        postData.put("add_id", Action.equals("insert") ? "" : AddressId);//AddressId

        RequestQueue requestQueue = Volley.newRequestQueue(context, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.AddNewAddress, response -> {
            Log.e("DeleteAddress", response);
            try {
                JSONObject addressObj = new JSONObject(response);
                String Status = addressObj.getString("status");
                String Message = addressObj.getString("message");
                customToast.showCustomToast(context, Message);
                if(Status.equals("1")){
                    customToast.showCustomToast(context, Message);
                    AddressList.remove(position);
                    notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

        }){
            @Override
            protected Map<String, String> getParams() {
                return postData;
            }
        };
        requestQueue.add(stringRequest);
    }
}
