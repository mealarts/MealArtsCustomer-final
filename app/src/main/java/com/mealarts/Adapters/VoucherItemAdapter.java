package com.mealarts.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.Helpers.Utils.VoucherUtils;
import com.mealarts.R;
import com.mealarts.VoucherListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VoucherItemAdapter extends RecyclerView.Adapter<VoucherItemAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<VoucherUtils> voucherList;
    private SharedPref sharedPref;
    private SSLCertification sslCertification;

    public VoucherItemAdapter(Context context, ArrayList<VoucherUtils> voucherList) {
        this.context = context;
        this.voucherList = voucherList;
        sharedPref = new SharedPref(context);
        sslCertification = new SSLCertification(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_voucher_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        try {
            JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
            JSONObject cartJSONObj = cartObj.getJSONObject(context.getResources().getString(R.string.CartJsonObj));
            float TotalAmt = Math.round(Float.parseFloat(cartJSONObj.getString("TotalAmount")));
            if((voucherList.get(position).getDiscountType().equals("Flat")
                    && voucherList.get(position).getDiscountValue() > TotalAmt)
                    || (voucherList.get(position).getDiscountType().equals("Discount")
                    && voucherList.get(position).getVoucherMinValue() > TotalAmt)) {
                holder.tvErrorMessage.setVisibility(View.VISIBLE);
                holder.tvErrorMessage.setText("*Total Amount Should Be Greater Than ₹" + voucherList.get(position).getVoucherMinValue()+"/-");
                holder.btnApplyVoucher.setEnabled(false);
                holder.btnApplyVoucher.setTextColor(context.getResources().getColor(R.color.colorGray));
            }else {
                holder.tvErrorMessage.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.tvVoucherCode.setText(voucherList.get(position).getVoucherCode());
        holder.tvVoucherTitle.setText("Get " +(voucherList.get(position).getDiscountType().equals("Discount")
                ? (voucherList.get(position).getDiscountValue()+"% ")
                : (" ₹"+voucherList.get(position).getDiscountValue()+"/- "))+"Discount");

        holder.tvVoucherDesc.setText("Use " + voucherList.get(position).getVoucherCode()
                + " & Get " +(voucherList.get(position).getDiscountType().equals("Discount")
                ? (voucherList.get(position).getDiscountValue()+"% ")
                : (" ₹"+voucherList.get(position).getDiscountValue()+"/- "))
                +(voucherList.get(position).getDiscountUpTo() > 0 ? ("Discount up to ₹"
                + voucherList.get(position).getDiscountUpTo()+"/-") : "Discount"));

        holder.tvVoucherDate.setText("Applicable "+(voucherList.get(position).getVoucherMinValue() > 0
                ? ("on order above ₹" + voucherList.get(position).getVoucherMinValue())+" " : "")
                + "till " + voucherList.get(position).getVoucherToDate());

        holder.btnApplyVoucher.setOnClickListener(v -> {
            try {
                JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                JSONObject cartJSONObj = cartObj.getJSONObject(context.getResources().getString(R.string.CartJsonObj));
                float TotalAmt = Math.round(Float.parseFloat(cartJSONObj.getString("TotalAmount")));
                ApplyVoucher(voucherList.get(position).getVoucherCode(), String.valueOf((int) TotalAmt), holder.tvErrorMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvVoucherCode, tvVoucherTitle, tvVoucherDesc, tvErrorMessage, tvVoucherDate;
        Button btnApplyVoucher;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvVoucherCode = itemView.findViewById(R.id.tvVoucherCode);
            tvVoucherTitle = itemView.findViewById(R.id.tvVoucherTitle);
            tvVoucherDesc = itemView.findViewById(R.id.tvVoucherDesc);
            tvErrorMessage = itemView.findViewById(R.id.tvErrorMessage);
            tvVoucherDate = itemView.findViewById(R.id.tvVoucherDate);
            btnApplyVoucher = itemView.findViewById(R.id.btnApplyVoucher);
        }
    }

    private Map<String, String> postData;
    private void ApplyVoucher(String VoucherCode, String TotalAmt, TextView tvErrorMessage){
        postData = new HashMap<>();
        postData.put("voucher_no", VoucherCode);
        postData.put("customer_id", sharedPref.getUserId());
        postData.put("total_amount", TotalAmt);

        RequestQueue requestQueue = Volley.newRequestQueue(context, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.Promocode, response -> {
            Log.e("VoucherResp", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject voucherObj = jsonObject.getJSONObject("voucher");
                String Status = voucherObj.getString("status");
                if(Status.equals("1")) {
                    tvErrorMessage.setVisibility(View.GONE);
                    String voucher_no = voucherObj.getString("voucher_no");
                    String discount_type = voucherObj.getString("discount_type");
                    String discount_value = voucherObj.getString("discount_value");
                    String min_order_amount = voucherObj.getString("min_order_amount");
                    String max_discount_amount = voucherObj.getString("max_discount_amount");

                    float total = Integer.parseInt(TotalAmt);
                    float discount;
                    if(!discount_type.equals("Discount")) {
                        discount = Integer.parseInt(discount_value);
                        total = total - discount;
                    }else{
                        discount = (total*Integer.parseInt(discount_value))/100;
                        total = total - discount;
                    }

                    if(Integer.parseInt(max_discount_amount) != 0
                            && Integer.parseInt(max_discount_amount) < discount)
                        discount = Integer.parseInt(max_discount_amount);

                    JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                    JSONObject cartJsonObj = cartObj.getJSONObject(context.getResources().getString(R.string.CartJsonObj));
                    cartJsonObj.put("Voucher", voucher_no);
                    cartJsonObj.put("VoucherDisc", discount);
                    cartJsonObj.put("DiscType", discount_type);
                    cartJsonObj.put("DiscValue", discount_value);
                    //cartJsonObj.put("TotalAmount", Math.round(total));
                    sharedPref.setUserCart(String.valueOf(cartObj));

                    VoucherListActivity.layoutVoucher.setVisibility(View.VISIBLE);
                    VoucherListActivity.tvVoucherCode.setText(voucher_no);
                    VoucherListActivity.tvVoucherMsg.setText("Yay !!\nYou got ₹ " + discount +" discount !!");
                }else {
                    String Message = voucherObj.getString("message");
                    tvErrorMessage.setVisibility(View.VISIBLE);
                    tvErrorMessage.setText("*"+Message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("VoucherResp", error.toString() +"_");
            Log.e("VoucherResp", error.getMessage()+"_");
            error.printStackTrace();
        }){
            @Override
            protected Map<String, String> getParams() {
                return postData;
            }
        };
        requestQueue.add(stringRequest);
        Log.e("VoucherResp", stringRequest.getUrl()+"_"+postData);
    }
}
