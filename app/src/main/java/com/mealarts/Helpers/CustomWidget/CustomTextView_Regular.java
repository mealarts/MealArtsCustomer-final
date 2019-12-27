package com.mealarts.Helpers.CustomWidget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CustomTextView_Regular extends TextView {

    public CustomTextView_Regular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomTextView_Regular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextView_Regular(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/Nunito-Regular.ttf");
        setTypeface(tf ,Typeface.NORMAL);

    }
}