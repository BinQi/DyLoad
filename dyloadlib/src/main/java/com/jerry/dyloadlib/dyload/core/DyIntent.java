package com.jerry.dyloadlib.dyload.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.jerry.dyloadlib.dyload.DyConstants;
import com.jerry.dyloadlib.dyload.DyManager;

import java.io.Serializable;

/**
 * @author wubinqi
 */
public class DyIntent extends Intent {
    private String mPluginPackage;
    private String mPluginClass;

//    public DyIntent() {
//        super();
//    }
//
//    public DyIntent(String pluginPackage) {
//        super();
//        this.mPluginPackage = pluginPackage;
//    }

    public DyIntent(String pluginPackage, String pluginClass) {
        super();
        this.mPluginPackage = pluginPackage;
        this.mPluginClass = pluginClass;
    }

    public DyIntent(String pluginPackage, Class<?> clazz) {
        super();
        this.mPluginPackage = pluginPackage;
        this.mPluginClass = clazz.getName();
    }

    public String getPluginPackage() {
        return mPluginPackage;
    }

    public void setPluginPackage(String pluginPackage) {
        this.mPluginPackage = pluginPackage;
    }

    public String getPluginClass() {
        return mPluginClass;
    }

    public void setPluginClass(String pluginClass) {
        this.mPluginClass = pluginClass;
    }

    public void setPluginClass(Class<?> clazz) {
        this.mPluginClass = clazz.getName();
    }

    @Override
    public Intent putExtra(String name, Parcelable value) {
        setupExtraClassLoader(value);
        return super.putExtra(name, value);
    }

    @Override
    public Intent putExtra(String name, Serializable value) {
        setupExtraClassLoader(value);
        return super.putExtra(name, value);
    }

    private void setupExtraClassLoader(Object value) {
        ClassLoader pluginLoader = value.getClass().getClassLoader();
//        DLConfigs.sPluginClassloader = pluginLoader;
        setExtrasClassLoader(pluginLoader);
    }

    public static DyIntent createIntent(Context context,  String pluginPackage, Class<?> clazz) {
        Class<? extends Activity> activityClass = DyManager.getProxyActivityClass(clazz);
        if (activityClass == null) {
            return null;
        }
        DyIntent dlIntent = new DyIntent(pluginPackage, clazz);
        dlIntent.putExtra(DyConstants.EXTRA_CLASS, clazz.getName());
        dlIntent.putExtra(DyConstants.EXTRA_PACKAGE, pluginPackage);
        dlIntent.setClass(context, activityClass);
        return dlIntent;
    }
}
