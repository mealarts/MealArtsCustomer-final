package com.mealarts.Helpers.CustomWidget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView_Black extends TextView {

    public CustomTextView_Black(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomTextView_Black(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextView_Black(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/Nunito-Black.ttf");
        setTypeface(tf ,Typeface.NORMAL);

    }
}