package com.mealarts.Helpers.CustomWidget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

public class CustomAutoCompleteTextView_Bold extends AutoCompleteTextView {

    public CustomAutoCompleteTextView_Bold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomAutoCompleteTextView_Bold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomAutoCompleteTextView_Bold(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/Nunito-Bold.ttf");
        setTypeface(tf ,Typeface.NORMAL);

    }
}