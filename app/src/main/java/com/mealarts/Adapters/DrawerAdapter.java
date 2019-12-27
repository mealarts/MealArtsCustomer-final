package com.mealarts.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mealarts.AboutUsActivity;
import com.mealarts.AllChefActivity;
import com.mealarts.ContactUsActivity;
import com.mealarts.Helpers.SharedPref;
import com.mealarts.LogInActivity;
import com.mealarts.MainActivity;
import com.mealarts.MenuListActivity;
import com.mealarts.R;
import com.mealarts.PolicyTermsActivity;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.MyViewHolder>{

    private Context context;
    private SharedPref sharedPref;
    private String[] drawerList;
    private Integer[] drawerIconList;

    public DrawerAdapter(Context context) {
        this.context = context;
        sharedPref = new SharedPref(context);
        if(sharedPref.getUserId().equals("")){
            //offers(after all chef menu) removed 6th nov 19 hgd
            drawerList = new String[]{"All Chef", "About US", "Privacy Policy", "Terms & Conditions", "Contact Us", "Sign In"};
            //icon R.drawable.offers removed 6th nov 19 hgd
            drawerIconList = new Integer[]{R.drawable.our_chef,  R.drawable.about_us,
                            R.drawable.privacy_policy, R.drawable.tnc, R.drawable.contact_us, R.drawable.signin};
        }else {
            //offers(after all chef menu) removed 6th nov 19 hgd
            drawerList = new String[]{"All Chef", "About US", "Privacy Policy", "Terms & Conditions", "Contact Us", "Log Out"};
            //icon R.drawable.offers removed 6th nov 19 hgd
            drawerIconList = new Integer[]{R.drawable.our_chef, R.drawable.about_us,
                            R.drawable.privacy_policy, R.drawable.tnc, R.drawable.contact_us, R.drawable.log_out};
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_drawer, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvDrawer.setText(drawerList[position]);
        holder.ivDrawer.setImageResource(drawerIconList[position]);

        holder.layoutDrawer.setOnClickListener(v -> {
            if(MainActivity.drawer != null && MainActivity.drawer.isDrawerOpen(GravityCompat.START))
                MainActivity.drawer.closeDrawer(GravityCompat.START);
            if(MenuListActivity.drawer != null && MenuListActivity.drawer.isDrawerOpen(GravityCompat.START))
                MenuListActivity.drawer.closeDrawer(GravityCompat.START);
            Intent intent;
            switch (position) {
                case 0:
                    intent = new Intent(context, AllChefActivity.class);
                    context.startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(context, AboutUsActivity.class);
                    context.startActivity(intent);
                    break;
                case 2:
                    intent = new Intent(context, PolicyTermsActivity.class);
                    intent.putExtra("Content", "Policy");
                    context.startActivity(intent);
                    break;
                case 3:
                    intent = new Intent(context, PolicyTermsActivity.class);
                    intent.putExtra("Content", "Terms");
                    context.startActivity(intent);
                    break;
                case 4:
                    intent = new Intent(context, ContactUsActivity.class);
                    context.startActivity(intent);
                    break;
                case 5:
                    if(sharedPref.getUserId().equals(""))
                        context.startActivity(new Intent(context, LogInActivity.class));
                    else sharedPref.logoutApp();
                    break;
                case 6:

                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return drawerList.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutDrawer;
        TextView tvDrawer;
        ImageView ivDrawer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutDrawer = itemView.findViewById(R.id.layoutDrawer);
            tvDrawer = itemView.findViewById(R.id.tvDrawer);
            ivDrawer = itemView.findViewById(R.id.ivDrawer);
        }
    }
}
