package com.mealarts.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.mealarts.AsyncTask.URLServices;
import com.mealarts.Helpers.GlideApp;
import com.mealarts.Helpers.Utils.VoucherUtils;
import com.mealarts.MenuListActivity;
import com.mealarts.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SliderAdapter extends PagerAdapter {

    private ArrayList<VoucherUtils> sliderImageList;
        private Context context;

        public SliderAdapter(Context context, ArrayList<VoucherUtils> sliderImageList) {
            this.context = context;
            this.sliderImageList = sliderImageList;
        }

        @Override
        public int getCount() {
            return sliderImageList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.layout_offer_slider, container, false);

            try {
                ImageView ivSlider = view.findViewById(R.id.slider);
                Button btnOrderNow = view.findViewById(R.id.btnOrderNow);
//                Glide.with(context).load(URLServices.PromoOfferImg + sliderImageList.get(position).getVoucherImg())
//                        .error(R.drawable.mealarts_icon).into(ivSlider);
                Glide.with(context).load(sliderImageList.get(position).getVoucherImg())
                        .error(R.drawable.mealarts_icon).into(ivSlider);
                ViewPager vp = (ViewPager) container;
                vp.addView(view, 0);

                btnOrderNow.setOnClickListener(v -> {
                    context.startActivity(new Intent(context, MenuListActivity.class));
                });

                ivSlider.setOnClickListener(v -> context.startActivity(new Intent(context, MenuListActivity.class)));
            } catch (Exception E) {
                E.printStackTrace();
            }return view;


        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            ViewPager viewPager = (ViewPager) container;
            View view = (View) object;
            viewPager.removeView(view);
        }
    }