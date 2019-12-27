package com.mealarts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.mealarts.Adapters.ChefReviewAdapter;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CheckExtras;
import com.mealarts.Helpers.Utils.VendorUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AllChefActivity extends AppCompatActivity {

    RecyclerView rcChefReview;
    LinearLayout layoutLoader;
    ImageView ivBack, ivLoader;

    ArrayList<VendorUtils> chefRatingArray;
    CheckExtras connection;
    SSLCertification sslCertification = new SSLCertification(AllChefActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chef);

        connection = new CheckExtras(AllChefActivity.this);

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> onBackPressed());

        layoutLoader = findViewById(R.id.layoutLoader);
        ivLoader = findViewById(R.id.ivLoader);
        Glide.with(AllChefActivity.this).asGif().load(R.drawable.mealarts_loader).into(ivLoader);
        layoutLoader.setVisibility(View.VISIBLE);

        rcChefReview = findViewById(R.id.rcChefReview);
        rcChefReview.setHasFixedSize(true);
        rcChefReview.setLayoutManager(new LinearLayoutManager(AllChefActivity.this));

        getAllChef();
    }

    public void getAllChef(){

        RequestQueue requestQueue = Volley.newRequestQueue(AllChefActivity.this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLServices.AllChef, response -> {
            Log.e("RatingList", response);
            try {
                JSONObject chefListObj = new JSONObject(response);
                String Status = chefListObj.getString("status");
                if(Status.equals("1")){
                    JSONArray chefArray = chefListObj.getJSONArray("vendor_ratings");
                    chefRatingArray = new ArrayList<>();
                    for(int i = 0 ; i < chefArray.length() ; i++){
                        JSONObject chefObj = chefArray.getJSONObject(i);
                        VendorUtils vendorUtils = new VendorUtils();
                        vendorUtils.setVendorId(chefObj.getString("vendor_id"));
                        vendorUtils.setVendorName(chefObj.getString("vender_name"));
                        vendorUtils.setVendorImg(chefObj.getString("vendor_img"));
                        vendorUtils.setRating(chefObj.getString("rating"));
                        vendorUtils.setCity(chefObj.getString("vendor_city"));
                        vendorUtils.setArea(chefObj.getString("vendor_area"));
                        chefRatingArray.add(vendorUtils);
                    }
                    ChefReviewAdapter chefReviewAdapter = new ChefReviewAdapter(AllChefActivity.this, chefRatingArray);
                    rcChefReview.setAdapter(chefReviewAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            layoutLoader.setVisibility(View.GONE);
        }, error -> {
            Log.e("AllChef", error.toString()+"_");
            Log.e("AllChef", error.getMessage()+"_");
            error.printStackTrace();
        });
        requestQueue.add(stringRequest);
    }
}
