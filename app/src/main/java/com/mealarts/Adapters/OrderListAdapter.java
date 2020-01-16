package com.mealarts.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CustomRatingBar;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.Helpers.Utils.OrderUtils;
import com.mealarts.MapsActivity;
import com.mealarts.ProfileActivity;
import com.mealarts.R;
import com.mealarts.SingleOrderDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<OrderUtils> orderList;
    private SharedPref sharedPref;
    private SSLCertification sslCertification;

    public OrderListAdapter(Context context, ArrayList<OrderUtils> orderList) {
        this.context = context;
        this.orderList = orderList;
        sharedPref = new SharedPref(context);
        sslCertification = new SSLCertification(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_order_list, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if(orderList.get(position).getOrderStatus().equals("paid_payment"))
            holder.btnCancelOrder.setVisibility(View.VISIBLE);
        else holder.btnCancelOrder.setVisibility(View.GONE);

        if(orderList.get(position).getOrderStatus().equals("delivered") && !orderList.get(position).getRated())
            holder.btnReview.setVisibility(View.VISIBLE);
        else holder.btnReview.setVisibility(View.GONE);

        if(orderList.get(position).getOrderStatus().equals("confirmed")
                || orderList.get(position).getOrderStatus().equals("start_prepare")
                || orderList.get(position).getOrderStatus().equals("prepared")
                || orderList.get(position).getOrderStatus().equals("packaging")
                || orderList.get(position).getOrderStatus().equals("accept")
                || orderList.get(position).getOrderStatus().equals("pickup")){
            holder.btnTrackOrder.setVisibility(View.VISIBLE);
        }else holder.btnTrackOrder.setVisibility(View.GONE);

        holder.tvChef.setText(orderList.get(position).getVendorName());
        holder.tvCurrentVendor.setText(orderList.get(position).getVendorName());
        holder.tvOrderId.setText("ORDER #"+orderList.get(position).getOrderId());
        holder.tvChefLocation.setText(orderList.get(position).getVendorLocation());
        holder.tvOrderTotal.setText("₹ "+orderList.get(position).getOrderTotal()+"/-");
        holder.tvOrderItems.setText(orderList.get(position).getOrderItems());
        holder.tvOrderDate.setText("Order "+orderList.get(position).getOrderStatus().replaceAll("_"," ")+" By Date : "+orderList.get(position).getOrderDate());

        holder.layoutOrder.setOnClickListener(v -> {
            Intent intent = new Intent(context, SingleOrderDetailActivity.class);
            intent.putExtra("CheckoutId", orderList.get(position).getOrderId());
            context.startActivity(intent);
        });

        holder.btnReview.setOnClickListener(v -> holder.layoutRateOrder.setVisibility(View.VISIBLE));

        holder.ivReviewClose.setOnClickListener(v -> holder.layoutRateOrder.setVisibility(View.GONE));

        holder.btnCancelOrder.setOnClickListener(v -> {
            ProfileActivity.layoutLoader.setVisibility(View.VISIBLE);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure to delete this order ?")
                    .setPositiveButton("YES", (dialog, which) -> {
                        dialog.dismiss();
                        cancelOrder(orderList.get(position).getOrderId(), position);
                    }).setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    });
            AlertDialog dialog1 = builder.create();
            dialog1.show();
        });

        holder.btnTrackOrder.setOnClickListener(v -> {
            Intent intent = new Intent(context, MapsActivity.class);
            intent.putExtra("CheckoutId", orderList.get(position).getOrderId());
            context.startActivity(intent);
        });

        holder.btnSubmitReview.setOnClickListener(v -> {
            ProfileActivity.layoutLoader.setVisibility(View.VISIBLE);
            submitReview(String.valueOf(holder.layoutOrderRating.getRating()), holder.edtOrderReview.getText().toString().trim(),
                    orderList.get(position).getOrderId(), orderList.get(position).getVendorId(), holder);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout layoutOrder, layoutRateOrder;
        private TextView tvChef, tvChefLocation, tvOrderTotal, tvOrderItems, tvOrderDate,
                tvCurrentVendor, tvOrderId;
        private EditText edtOrderReview;
//        private CustomRatingBar layoutOrderRating;
        private RatingBar layoutOrderRating;
        private Button btnReview, btnCancelOrder, btnTrackOrder, btnSubmitReview;
        private ImageView ivReviewClose;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutOrder = itemView.findViewById(R.id.layoutOrder);
            tvChef = itemView.findViewById(R.id.tvChef);
            tvChefLocation = itemView.findViewById(R.id.tvChefLocation);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            btnReview = itemView.findViewById(R.id.btnReview);
            btnCancelOrder = itemView.findViewById(R.id.btnCancelOrder);
            btnTrackOrder = itemView.findViewById(R.id.btnTrackOrder);
            btnSubmitReview = itemView.findViewById(R.id.btnSubmitReview);

            layoutRateOrder = itemView.findViewById(R.id.layoutRateOrder);
            tvCurrentVendor = itemView.findViewById(R.id.tvCurrentVendor);
            edtOrderReview = itemView.findViewById(R.id.edtOrderReview);
            layoutOrderRating = itemView.findViewById(R.id.layoutOrderRating);
            ivReviewClose = itemView.findViewById(R.id.ivReviewClose);
        }
    }

    private HashMap<String, String> postData;
    private void submitReview(String Rating, String Review, String OrderId, String VendorId, MyViewHolder holder){
        postData = new HashMap<>();
        postData.put("rating", Rating);
        postData.put("review", Review);
        postData.put("checkout_id", OrderId);
        postData.put("customer_id", sharedPref.getUserId());
        postData.put("vendor_id", VendorId);

        RequestQueue requestQueue = Volley.newRequestQueue(context, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.GiveReview, response -> {
            Log.e("ReviewResp", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equals("1")){
                    holder.layoutRateOrder.setVisibility(View.GONE);
                    holder.btnReview.setVisibility(View.GONE);
                    sharedPref.setCurrentOrderId("0");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ProfileActivity.layoutLoader.setVisibility(View.GONE);
        }, error -> {
            Log.e("GiveReview", error.toString()+"_");
            Log.e("GiveReview", error.getMessage()+"_");
            error.printStackTrace();
        }){
            @Override
            protected Map<String, String> getParams() {
                return postData;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void cancelOrder(String OrderId, int position){
        postData = new HashMap<>();
        postData.put("checkout_id", OrderId);

        RequestQueue requestQueue = Volley.newRequestQueue(context, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.CustomerCancelOrder, response -> {
            Log.e("CancelResp", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String Status = jsonObject.getString("status");
                if (Status.equals("1")) {
                    Toast.makeText(context, "Order Cancelled Successfully !!", Toast.LENGTH_LONG).show();
                    orderList.get(position).setOrderStatus("Cancelled");
                    //orderList.remove(position);
                    ProfileActivity.orderListAdapter.notifyDataSetChanged();
                    if(sharedPref.getCurrentOrderId().equals(OrderId))
                        sharedPref.setCurrentOrderId("0");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ProfileActivity.layoutLoader.setVisibility(View.GONE);
        }, error -> {
            Log.e("CustomerCancelOrder", error.toString()+"_");
            Log.e("CustomerCancelOrder", error.getMessage()+"_");
            error.printStackTrace();
        }) {
            @Override
            protected Map<String, String> getParams() {
                return postData;
            }
        };
        requestQueue.add(stringRequest);
        Log.e("CancelOrder", stringRequest.getUrl()+"_"+postData.toString());
    }
}
