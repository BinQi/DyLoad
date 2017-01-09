package com.jerry.dyloadlib.dyload.core.proxy.service;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

import com.jerry.dyloadlib.dyload.DyManager;
import com.jerry.dyloadlib.dyload.core.DyIntent;

/**
 * Created by wubinqi on 16-9-21.
 */
public abstract class DyServicePlugin {

    protected DyServiceContext mThat;

    public DyServicePlugin(DyServiceContext that) {
        mThat = that;
    }

    public abstract void onCreate();

    public abstract void onStart(Intent intent, int startId);
    
    public abstract int onStartCommand(Intent intent, int flags, int startId);
    
    public abstract void onDestroy();
    
    public abstract void onConfigurationChanged(Configuration newConfig);
    
    public abstract void onLowMemory();
    
    public abstract void onTrimMemory(int level);
    
    public abstract IBinder onBind(Intent intent);
    
    public abstract boolean onUnbind(Intent intent);
    
    public abstract void onRebind(Intent intent);
    
    public abstract void onTaskRemoved(Intent rootIntent);

    public Context getApplicationContext() {
        return mThat;
    }

    public Application getApplication() {
        return mThat.getService().getApplication();
    }

    public int startPluginActivity(DyIntent dyIntent) {
        return DyManager.getInstance(mThat.getService()).startPluginActivity(mThat.getService(), dyIntent);
    }

    public abstract void setLogShow(boolean show);
}
