package com.jerry.plugindemo;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.widget.Toast;

import com.jerry.dyloadlib.dyload.core.proxy.service.DyServiceContext;
import com.jerry.dyloadlib.dyload.core.proxy.service.DyServicePlugin;

/**
 * Created by wubinqi on 17-1-5.
 */
public class MainService extends DyServicePlugin {

    public MainService(DyServiceContext that) {
        super(that);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart(Intent intent, int startId) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(mThat, "Plugin service is running", Toast.LENGTH_SHORT).show();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onTrimMemory(int level) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public void onRebind(Intent intent) {

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

    }

    @Override
    public void setLogShow(boolean show) {

    }
}
