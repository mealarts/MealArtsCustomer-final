package com.mealarts;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mealarts.Adapters.CustomerReviewAdapter;
import com.mealarts.AsyncTask.SSLCertification;
import com.mealarts.Helpers.BlurBuilder;
import com.mealarts.Helpers.CheckExtras;
import com.mealarts.Helpers.Utils.ReviewUtils;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.CustomRatingBar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChefDetailActivity extends AppCompatActivity {

    RecyclerView rcReview;
    FloatingActionButton fab;
    AppBarLayout app_bar;
    Toolbar mToolbar;
    CollapsingToolbarLayout toolbar_layout;
    ImageView ivToolVendor, ivKitchenBlur, ivVendorKitchen;
    LinearLayout layoutNoReview;
    TextView tvCity, tvArea, tvVendorCat, tvVendorCuisines, tvTotalNoRating;
    CustomRatingBar layoutChefRating;
    SSLCertification sslCertification = new SSLCertification(ChefDetailActivity.this);
    ArrayList<ReviewUtils> CustomerReviewList;
    String VendorId;

    CheckExtras connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_detail);
        VendorId = getIntent().getStringExtra("VendorId");

        connection = new CheckExtras(ChefDetailActivity.this);

        layoutNoReview = findViewById(R.id.layoutNoReview);
        rcReview = findViewById(R.id.rcReview);
        rcReview.setHasFixedSize(true);
        rcReview.setLayoutManager(new LinearLayoutManager(ChefDetailActivity.this));

        ivVendorKitchen = findViewById(R.id.ivVendorKitchen);
        ivKitchenBlur = findViewById(R.id.ivKitchenBlur);
        tvCity = findViewById(R.id.tvCity);
        tvArea = findViewById(R.id.tvArea);
        tvVendorCat = findViewById(R.id.tvVendorCat);
        tvVendorCuisines = findViewById(R.id.tvVendorCuisines);
        tvTotalNoRating = findViewById(R.id.tvTotalNoRating);
        layoutChefRating = findViewById(R.id.layoutChefRating);
        mToolbar = findViewById(R.id.toolbar);
        toolbar_layout = findViewById(R.id.toolbar_layout);
        app_bar = findViewById(R.id.app_bar);
        ivToolVendor = findViewById(R.id.ivToolVendor);
        fab = findViewById(R.id.fab);
        setSupportActionBar(mToolbar);
        Drawable drawable= getResources().getDrawable(R.drawable.back_arrow);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable newDrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 40, 40, true));
        mToolbar.setNavigationIcon(newDrawable);
        mToolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        app_bar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            //  Vertical offset == 0 indicates appBar is fully  expanded.
            //Log.e("verticalOffset", String.valueOf(Math.abs(verticalOffset)));
            if (appBarLayout.getTotalScrollRange() - Math.abs(verticalOffset) > 92) {
                ivToolVendor.setVisibility(View.GONE);
                ChefDetailActivity.this.invalidateOptionsMenu();
            } else {
                ivToolVendor.setVisibility(View.VISIBLE);
                ChefDetailActivity.this.invalidateOptionsMenu();
            }
        });

        getVendorDetail();
    }

    Map<String, String> postData;
    public void getVendorDetail(){
        postData = new HashMap<>();
        postData.put("vendor_id", VendorId);

        RequestQueue requestQueue = Volley.newRequestQueue(ChefDetailActivity.this, sslCertification.getHurlStack() );
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLServices.SingleVendorDetail, response -> {
            Log.e("VendorDetail", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String Status = jsonObject.getString("status");
                if(Status.equals("1")){
                    String VendorId = jsonObject.getString("vendor_id");
                    String VendorName = jsonObject.getString("vender_name");
                    float TotalReview = Float.parseFloat(jsonObject.getString("rating"));
                    String SpecialFood = jsonObject.getString("special_food");
                    String Cuisines = jsonObject.getString("cuisines");
                    String VendorImg = jsonObject.getString("vendor_img").replace(" ", "%20");
                    String KitchenImg = jsonObject.getString("kitchen_img").replace(" ", "%20");
                    String City = jsonObject.getString("city");
                    String Area = jsonObject.getString("area");
                    JSONArray reviewArray = jsonObject.getJSONArray("review");
                    CustomerReviewList = new ArrayList<>();
                    for(int i = 0 ; i < reviewArray.length() ; i++){
                        JSONObject reviewObj = reviewArray.getJSONObject(i);
                        float Rating = (float) reviewObj.getInt("rating");
                        String Review = reviewObj.getString("review");
                        String ReviewDate = reviewObj.getString("review_date");
                        String CustName = reviewObj.getString("cust_name");

                        ReviewUtils reviewUtils = new ReviewUtils();
                        reviewUtils.setRating(Rating);
                        reviewUtils.setReview(Review);
                        reviewUtils.setRatingDate(ReviewDate);
                        reviewUtils.setCustomerName(CustName);
                        CustomerReviewList.add(reviewUtils);
                    }
                    CustomerReviewAdapter customerReviewAdapter = new CustomerReviewAdapter(ChefDetailActivity.this, CustomerReviewList);
                    rcReview.setAdapter(customerReviewAdapter);

                    if(reviewArray.length() == 0)
                        layoutNoReview.setVisibility(View.VISIBLE);
                    else layoutNoReview.setVisibility(View.GONE);

                    tvCity.setText(City);
                    tvArea.setText(Area);
                    tvVendorCat.setText(SpecialFood);
                    tvVendorCuisines.setText(Cuisines);
                    tvTotalNoRating.setText("( " + reviewArray.length() + " )");
                    layoutChefRating.setScore(TotalReview);
                    mToolbar.setTitle(VendorName);

                    toolbar_layout.setTitle(VendorName);
                    toolbar_layout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(), "font/Nunito-Regular.ttf"));
                    toolbar_layout.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(), "font/Nunito-BoldItalic.ttf"));
                    toolbar_layout.setExpandedTitleColor(getResources().getColor(R.color.colorWhite));

                    if(!KitchenImg.isEmpty()) {
                        Glide.with(ChefDetailActivity.this).load(URLServices.VendorImg + KitchenImg).into(ivVendorKitchen);
                        Glide.with(ChefDetailActivity.this).load(URLServices.VendorImg + KitchenImg)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Log.e("PicassoError", Objects.requireNonNull(e).getMessage());
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        Bitmap bitmap = ((BitmapDrawable)resource).getBitmap();
                                        bitmap = BlurBuilder.blur( ChefDetailActivity.this, bitmap);
                                        ivKitchenBlur.setImageBitmap(bitmap);
                                        return true;
                                    }
                                })
                                .into(ivKitchenBlur);
                    }else Glide.with(ChefDetailActivity.this).load(R.drawable.mealarts_icon).into(ivVendorKitchen);

                    if(!VendorImg.isEmpty()) {
                        Glide.with(ChefDetailActivity.this).load(URLServices.VendorImg + VendorImg)
                                .apply(RequestOptions.circleCropTransform()).into(fab);
                        Glide.with(ChefDetailActivity.this).load(URLServices.VendorImg + VendorImg)
                                .apply(RequestOptions.circleCropTransform()).into(ivToolVendor);
                    } else {
                        Glide.with(ChefDetailActivity.this).load(R.drawable.mealarts_icon).into(fab);
                        Glide.with(ChefDetailActivity.this).load(R.drawable.mealarts_icon).into(ivToolVendor);
                    }


                }else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("VendorDetails", error.toString()+"_");
            Log.e("VendorDetails", error.getMessage()+"_");
            error.printStackTrace();
        }){
            @Override
            protected Map<String, String> getParams() {
                return postData;
            }
        };
        requestQueue.add(stringRequest);
    }

}
