package com.jerry.dyloadlib.dyload.core;

import android.annotation.TargetApi;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;

import com.jerry.dyloadlib.dyload.core.inflater.DyLayoutInflater;
import com.jerry.dyloadlib.dyload.util.log.Logger;

/**
 * Created by wubinqi
 */
public class DyContext extends ContextWrapper {
    public static final String TAG = "DyContext";
    private ClassLoader mClassLoader;
    private Resources mResource;
    private AssetManager mAssetManager;
    private Resources.Theme mTheme;
    private String mPluginPackageName;
    private LayoutInflater mLayoutInflater;

    public DyContext(String packageName, Context hostContext, ClassLoader classLoader, Resources resources) {
        super(hostContext);
        mClassLoader = classLoader;
        mResource = resources;
        mAssetManager = resources.getAssets();
        mTheme = resources.newTheme();
        mTheme.setTo(hostContext.getTheme());
        mPluginPackageName = packageName;
    }

    @Override
    public Resources getResources() {
        return mResource;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        getBaseContext().registerComponentCallbacks(callback);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        getBaseContext().unregisterComponentCallbacks(callback);
    }

    @Override
    public Object getSystemService(String name) {
        Logger.d(TAG, "getSystemService:" + name);
        Object object = super.getSystemService(name);
        if (object instanceof LayoutInflater) {
            Logger.d(TAG, "getSystemService:LayoutInflater");
            if (mLayoutInflater == null) {
                mLayoutInflater = DyLayoutInflater.create((LayoutInflater) object, this);
            }
            return mLayoutInflater;
        }
        return object;
    }

    @Override
    public Context getApplicationContext() {
        return this;
    }

    @Override
    public AssetManager getAssets() {
        return mAssetManager;
    }

    @Override
    public Resources.Theme getTheme() {
        return mTheme;
    }


    @Override
    public String getPackageName() {
        return getBaseContext().getPackageName();
    }

    @Override
    public ClassLoader getClassLoader() {
        return mClassLoader;
    }

    /**
     * @return 宿主Context
     */
    public Context getHostContext() {
        return getBaseContext();
    }

    /**
     * @return 获取插件的包名
     */
    public String getPluginPackageName() {
        return mPluginPackageName;
    }
}
