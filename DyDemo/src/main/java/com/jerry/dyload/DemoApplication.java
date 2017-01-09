package com.jerry.dyload;

import android.app.Application;

import com.jerry.dyloadlib.dyload.pm.DyHelper;

/**
 * Created by wubinqi on 17-1-6.
 */
public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DyHelper.getInstance().applicationOnCreate(this);
    }
}
