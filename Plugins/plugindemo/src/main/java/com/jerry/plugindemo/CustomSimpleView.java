package com.jerry.plugindemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by wubinqi on 17-1-12.
 */
public class CustomSimpleView extends TextView {
    public CustomSimpleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSimpleView(Context context) {
        super(context);
    }

    public CustomSimpleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomSimpleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setText("I am custom component");
    }
}
