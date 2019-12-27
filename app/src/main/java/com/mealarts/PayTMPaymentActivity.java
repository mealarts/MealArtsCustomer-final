package com.mealarts;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CheckExtras;
import com.mealarts.Helpers.CustomToast;
import com.mealarts.Helpers.SharedPref;
import com.onesignal.OneSignal;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@SuppressLint("SetTextI18n")
public class PayTMPaymentActivity extends AppCompatActivity {

    TextView lblStatus;
    ImageView iconStatus, ivLoader;
    TextView statusMessage;
    TextView responseTitle;
    LinearLayout layoutOrderPlaced;

    String OrderId, PayTMOrderId, TotalAmount, VendorId, DeliveryCharge, PackingCharge, Voucher, VoucherDisc,totalGST;
    SharedPref sharedPref;
    CheckExtras connection;
    SSLCertification sslCertification = new SSLCertification(PayTMPaymentActivity.this);
    HashMap<String, String> paramMap;
    CustomToast customToast = new CustomToast();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytm_payment);

        ivLoader = findViewById(R.id.ivLoader);
        Glide.with(PayTMPaymentActivity.this).asGif().load(R.drawable.mealarts_loader).into(ivLoader);

        OrderId = getIntent().getStringExtra("OrderId");
        DeliveryCharge = getIntent().getStringExtra("DeliveryCharge");
        PackingCharge = getIntent().getStringExtra("totalPackCharge");
        TotalAmount = getIntent().getStringExtra("TotalAmount");
        VendorId = getIntent().getStringExtra("VendorId");
        Voucher = getIntent().getStringExtra("Voucher");
        VoucherDisc = getIntent().getStringExtra("VoucherDisc");
        totalGST=getIntent().getStringExtra("totalGST");

        sharedPref = new SharedPref(PayTMPaymentActivity.this);
        connection = new CheckExtras(PayTMPaymentActivity.this);

        lblStatus = findViewById(R.id.lbl_status);
        iconStatus = findViewById(R.id.icon_status);
        statusMessage = findViewById(R.id.status_message);
        responseTitle = findViewById(R.id.title_status);
        layoutOrderPlaced = findViewById(R.id.layout_order_placed);

        lblStatus.setText("Preparing your order");
        paramMap = preparePayTmParams();
        getChecksum(paramMap);
    }

    public HashMap<String, String> preparePayTmParams() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        PayTMOrderId = simpleDateFormat.format(new Date()) + System.currentTimeMillis();/*generateRandom(0,999999).toString()*/
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("CALLBACK_URL", URLServices.IS_PAYTM_STAGING ? URLServices.PAYTM_CALLBACK_TEST
                : URLServices.PAYTM_CALLBACK_URL + PayTMOrderId);
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("CUST_ID", sharedPref.getUserId());
        paramMap.put("INDUSTRY_TYPE_ID", "Retail");
        paramMap.put("MID", URLServices.IS_PAYTM_STAGING ? "dQiocg77637434005517" : "TXvBrr67765655395690");
        paramMap.put("WEBSITE", "DEFAULT");
        paramMap.put("ORDER_ID", PayTMOrderId);
        paramMap.put("TXN_AMOUNT", TotalAmount);
        //paramMap.put("TYPE", "Transaction");
        return paramMap;
    }

    public void getChecksum(HashMap<String, String> params){
        lblStatus.setText("Getting checksum value from the server");
        RequestQueue requestQueue = Volley.newRequestQueue(this, sslCertification.getHurlStack());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.IS_PAYTM_STAGING ? URLServices.PAYTM_CHECKSUM_TEST
                : URLServices.PAYTM_CHECKSUM, response -> {
            if (!response.equals("")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Checksum = jsonObject.getString("CHECKSUMHASH");
                    String Order_Id = jsonObject.getString("ORDER_ID");
                    String paytStatus = jsonObject.getString("payt_STATUS");

                    Log.e("Checksum_Received: ", jsonObject.toString());
                    // Add the checksum to existing params list and send them to PayTM
                    if (Order_Id != null) {
                        params.put("CHECKSUMHASH", Checksum);
                        placeOrder(params);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else Log.e("Network Fail", "Network call failed");
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void placeOrder(HashMap<String, String> params) {
        lblStatus.setText("Redirecting to PayTM gateway");

        // choosing between PayTM staging and production
        PaytmPGService pgService = URLServices.IS_PAYTM_STAGING ? PaytmPGService.getStagingService() : PaytmPGService.getProductionService();

        PaytmOrder Order = new PaytmOrder(params);
        Log.e("PayTM_Params", String.valueOf(params));

        pgService.initialize(Order, null);

        pgService.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        Log.e("someUIErrorOccurred: ", inErrorMessage);
                        cancelOrder("Some UI Error Occurred");
                    }

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Log.e("PayTM Trans Resp", inResponse.toString());
                        String TXNStatus = inResponse.getString("STATUS");
                        String orderId = inResponse.getString("ORDERID");
                        String TXNId = inResponse.getString("TXNID");
                        //{STATUS=TXN_SUCCESS,
                        // CHECKSUMHASH=a9IJ3PFAVIq9ZorF7N5YL0UUiqi67vDLkJJmB9XqF/SCh3VLwCM8+ejuO77zZ51QNyTvlZof2+W6rBw3JFlS43AJvT282dDQg7Z9YP73V+o=,
                        // BANKNAME=ALLIED IRISH BANK- PLC,
                        // ORDERID=510009,
                        // TXNAMOUNT=111.00,
                        // TXNDATE=2019-06-26 16:18:34.0,
                        // MID=aFUJlI50194989721405,
                        // TXNID=20190626111212800110168094200613038,
                        // RESPCODE=01,
                        // PAYMENTMODE=DC,
                        // BANKTXNID=777001708503913,
                        // CURRENCY=INR,
                        // GATEWAYNAME=HDFC,
                        // RESPMSG=Txn Success}]
                        String status;
                        if(Objects.requireNonNull(TXNStatus).toLowerCase().contains("failure")){
                            status = "Transaction Declined!";
                        }else if(Objects.requireNonNull(TXNStatus).toLowerCase().contains("success")){
                            status = "Transaction Successful!";
                        }else if(Objects.requireNonNull(TXNStatus).toLowerCase().contains("aborted")){
                            status = "Transaction Cancelled!";
                        }else{
                            status = "Status Not Known!";
                        }
                        Log.d("paytmstatus",status+" "+orderId+" "+TXNId);
                        verifyTransactionStatus(status, orderId, TXNId);
                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        Log.e("networkNotAvailable","Network Not Available");
                        cancelOrder("Network Not Available");
                        // available, then this
                        // method gets called.
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        Log.e("clientAuthFailed: ", inErrorMessage);
                        Intent intent = new Intent(getApplicationContext(), AddToCartActivity.class);
                        startActivity(intent);
                        finish();
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {
                        Log.e("onErrorLoadingWebPage:", iniErrorCode+"\n"+inErrorMessage+"\n"+inFailingUrl);
                        cancelOrder(inErrorMessage);
                    }

                    @Override
                    public void onBackPressedCancelTransaction() {
                        customToast.showCustomToast(PayTMPaymentActivity.this, "Back pressed. Transaction cancelled");
                        cancelOrder("Back pressed. Transaction cancelled");
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Log.e("onTransactionCancel:", inErrorMessage+"\n"+inResponse);
                        cancelOrder(inErrorMessage);
                    }
                });
    }

    private void verifyTransactionStatus(String TransactionStatus, String orderId, String TXNId) {
        lblStatus.setText("Verifying transaction status on server");
        if(TransactionStatus.contains("Transaction Successful!")) {
            HashMap<String, String> postData = new HashMap<>();
            postData.put("checkout_id", OrderId);
            postData.put("grand_total", TotalAmount);
            postData.put("del_charge", DeliveryCharge);
            postData.put("packing_charge", PackingCharge);
            postData.put("transaction_id", TXNId);
            postData.put("paytm_order_id", orderId);
            postData.put("vendor_id", VendorId);
            postData.put("voucher_no", Voucher);
            postData.put("voucher_discount", VoucherDisc);
            postData.put("total_gst",totalGST);//hgd

            RequestQueue requestQueue = Volley.newRequestQueue(PayTMPaymentActivity.this, sslCertification.getHurlStack());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.OrderPayment, response -> {
                Log.e("PaymentResp", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Status = jsonObject.getString("status");
                    if (Status.equals("success")) {
                        String VendorToken = jsonObject.getString("vendor_token");
                        try {
                            JSONObject cartObj = new JSONObject(sharedPref.getUserCart());
                                cartObj.put(getResources().getString(R.string.CartJsonObj), new JSONObject());
                                cartObj.put(getResources().getString(R.string.CartJsonArray), new JSONArray());
                                cartObj.put(getResources().getString(R.string.AddOnsJsonArray), new JSONArray());
                            sharedPref.setUserCart(cartObj.toString());
                            sharedPref.setCurrentOrderId(OrderId);
                            Log.e("Cart", sharedPref.getUserCart());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        customToast.showCustomToast(PayTMPaymentActivity.this, TransactionStatus);

                        try {
                            OneSignal.postNotification(new JSONObject(
                                    "{'contents':{'en':'You Have A New Order To Receive.'}," +
                                            "'include_player_ids':['" + VendorToken + "']," +
                                            "'app_id':'4110c699-d4ad-4d7f-99bc-b7f2a0ce624d'," +
                                            "'data' : {'subtext':'New Order','orderid':'"+OrderId+"'},"+
                                            "'headings':{'en':'Order No: #"+OrderId+"'}}"),
                                    new OneSignal.PostNotificationResponseHandler() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    Log.e("Notify", String.valueOf(response));
                                }

                                @Override
                                public void onFailure(JSONObject response) {
                                    Log.e("Notify", String.valueOf(response));
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Notify", e.toString());
                        }

                        Intent intent = new Intent(PayTMPaymentActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                Log.e("OrderPayment", error.toString()+"_");
                Log.e("OrderPayment", error.getMessage()+"_");
                error.printStackTrace();
            }) {
                @Override
                protected Map<String, String> getParams() {
                    return postData;
                }
            };
            requestQueue.add(stringRequest);
            Log.e("Payment", stringRequest.getUrl()+"_"+postData.toString());
        }else {
            cancelOrder(TransactionStatus);
        }
    }
    public void cancelOrder(String TransactionStatus){
        HashMap<String, String> postData = new HashMap<>();
        postData.put("checkout_id", OrderId);

        RequestQueue requestQueue = Volley.newRequestQueue(PayTMPaymentActivity.this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.CancelOrder, response -> {
            Log.e("CancelResp", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String Status = jsonObject.getString("status");
                if (Status.equals("1")) {

                   customToast.showCustomToast(PayTMPaymentActivity.this, TransactionStatus);
                    onBackPressed();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("CancelOrder", error.toString()+"_");
            Log.e("CancelOrder", error.getMessage()+"_");
            error.printStackTrace();
        }) {
            @Override
            protected Map<String, String> getParams() {
                return postData;
            }
        };
        requestQueue.add(stringRequest);
        Log.e("Payment", stringRequest.getUrl()+"_"+postData.toString());
    }
}
