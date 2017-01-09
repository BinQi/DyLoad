package com.jerry.dyloadlib.dyload.core.proxy.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.IBinder;

import com.jerry.dyloadlib.dyload.DyManager;
import com.jerry.dyloadlib.dyload.core.DyContext;
import com.jerry.dyloadlib.dyload.core.mod.DefaultPluginInfo;
import com.jerry.dyloadlib.dyload.util.log.Logger;

/**
 * Created by wubinqi on 16-9-21.
 */
public abstract class BaseProxyService extends Service implements IServiceAttachable {
    public static final String TAG = "BPService";
    protected DyServicePlugin mRemoteService;
    private DyContext mDyContext;

    @Override
    public void attach(DyServicePlugin proxyService, DyContext dyContext) {
        mRemoteService = proxyService;
        mDyContext = dyContext;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //判断是否存在插件Service，如果存在，则不进行Service插件的构造工作
//        if (mRemoteService == null) {
//            mDyContext.init(intent);
//        }
        return mRemoteService != null ? mRemoteService.onBind(intent) : null;
    }

    protected abstract void exitService();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    protected void defaultOnCreate(String pluginPkgName) {
        Logger.d(TAG, getClass().getSimpleName() + " onCreate");
        DefaultPluginInfo info = (DefaultPluginInfo) DyManager.getInstance(this).getDyPluginInfo(pluginPkgName);
        if (null == info) {
            Logger.w(TAG, getClass().getSimpleName() + " pluginInfo is null, exit service");
            exitService();
            return;
        }
        String clazzName = info.getEntrance().getSpecialClazz(new ComponentName(this, getClass()));
        attach(info.loadDyServicePlugin(clazzName, this), info.getContext());
        if (mRemoteService != null) {
            mRemoteService.onCreate();
        } else {
            Logger.w(TAG, getClass().getSimpleName() + " remoteService is null, exit service");
            exitService();
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Logger.d(TAG, "onStart");
        if (mRemoteService != null) {
            mRemoteService.onStart(intent, startId);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "onStartCommand");
        //判断是否存在插件Service，如果存在，则不进行Service插件的构造工作
//        if (mRemoteService == null) {
//            mDyContext.init(intent);
//        }
        int ret = super.onStartCommand(intent, flags, startId);
        return mRemoteService != null ? mRemoteService.onStartCommand(intent, flags, startId) : ret;
    }

    @Override
    public void onDestroy() {
        if (mRemoteService != null) {
            mRemoteService.onDestroy();
        }
        super.onDestroy();
        Logger.d(TAG, "onDestroy");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mRemoteService != null) {
            mRemoteService.onConfigurationChanged(newConfig);
        }
        super.onConfigurationChanged(newConfig);
        Logger.d(TAG, "onConfigurationChanged");
    }

    @Override
    public void onLowMemory() {
        if (mRemoteService != null) {
            mRemoteService.onLowMemory();
        }
        super.onLowMemory();
        Logger.d(TAG, "onLowMemory");
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTrimMemory(int level) {
        if (mRemoteService != null) {
            mRemoteService.onTrimMemory(level);
        }
        super.onTrimMemory(level);
        Logger.d(TAG, "onTrimMemory");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.d(TAG, "onUnbind");
        boolean ret = super.onUnbind(intent);
        return mRemoteService != null ? mRemoteService.onUnbind(intent) : ret;
    }

    @Override
    public void onRebind(Intent intent) {
        if (mRemoteService != null) {
            mRemoteService.onRebind(intent);
        }
        super.onRebind(intent);
        Logger.d(TAG, "onRebind");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (mRemoteService != null) {
            mRemoteService.onTaskRemoved(rootIntent);
        }
        super.onTaskRemoved(rootIntent);
        Logger.d(TAG, "onTaskRemoved");
    }
}