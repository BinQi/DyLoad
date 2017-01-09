package com.jerry.dyloadlib.dyload.core.mod;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.jerry.dyloadlib.dyload.core.DyContext;
import com.jerry.dyloadlib.dyload.core.proxy.activity.DyActivityContext;
import com.jerry.dyloadlib.dyload.core.proxy.activity.DyActivityPlugin;
import com.jerry.dyloadlib.dyload.core.proxy.service.DyServiceContext;
import com.jerry.dyloadlib.dyload.core.proxy.service.DyServicePlugin;
import com.jerry.dyloadlib.dyload.util.EncryptUtil;
import com.jerry.dyloadlib.dyload.util.FileUtil;

import java.io.File;
import java.lang.reflect.Constructor;

/**
 * Created by wubinqi on 16-9-20.
 *
 * @param <E>
 */
public abstract class DyPluginInfo<E> {
    public static final String TAG = "DyPluginInfo";
    protected String mFileAbsolutePath;
    /**
     * 包名
     */
    protected String mPackageName;
    /**
     * 版本号
     */
    protected int mVersionCode;
    /**
     * 版本名
     */
    protected String mVersionName;
    /**
     * 类加载器
     */
    protected ClassLoader mClassLoader;
    /**
     * 资源
     */
    protected Resources mResource;
    protected PackageInfo mPackageInfo;
    protected DyContext mContext;
    protected E mEntrance = null;

    public DyPluginInfo(Context context, File file) {
        context = context.getApplicationContext();
        mFileAbsolutePath = file.getAbsolutePath();
        buildInfo(context, file);
        if (!TextUtils.isEmpty(getPackageName())) {
            loadEntrance();
        }
    }

    protected abstract void buildInfo(Context context, File file);

    /**
     * 加载入口类
     */
    protected abstract void loadEntrance();

    public abstract void restart();

    protected String getOptimizedDirectory(Context context) {
        String optimizedDirectory = "dex" + EncryptUtil.simpleEncryption(mPackageName);
        File dexOutputDir = context.getDir(optimizedDirectory, Context.MODE_PRIVATE);
        if (!dexOutputDir.exists()) {
            dexOutputDir.mkdirs();
        }
        return dexOutputDir.getAbsolutePath();
    }

    protected void clearOptimizedDex(Context context) {
        File file = new File(getOptimizedDirectory(context));
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            if (subFiles != null) {
                for (File sub : subFiles) {
                    FileUtil.delFile(sub.getAbsolutePath());
                }
            }
        } else {
            FileUtil.delFile(file.getAbsolutePath());
        }
    }

    public String getFileAbsolutePath() {
        return mFileAbsolutePath;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public int getVersionCode() {
        return mVersionCode;
    }

    public String getVersionName() {
        return mVersionName;
    }

    public ClassLoader getClassLoader() {
        return mClassLoader;
    }

    public Resources getResource() {
        return mResource;
    }

    public PackageInfo getPackageInfo() {
        return mPackageInfo;
    }

    public DyContext getContext() {
        return mContext;
    }

    public E getEntrance() {
        return mEntrance;
    }

    public DyServicePlugin loadDyServicePlugin(String className, Service service) {
        try {
            Class<?> clazz = mClassLoader.loadClass(className);
            Constructor con = clazz.getConstructor(new Class[] {DyServiceContext.class});
            return (DyServicePlugin) con.newInstance(new DyServiceContext(getContext(), service));
        } catch (Throwable e) {
            Log.w("wbq", "loadDyServicePlugin", e);
        }
        return null;
    }

    public DyActivityPlugin loadDyActivityPlugin(String className, Activity activity) {
        try {
            Class<?> clazz = mClassLoader.loadClass(className);
            Constructor con = clazz.getConstructor(new Class[] {DyActivityContext.class});
            return (DyActivityPlugin) con.newInstance(new DyActivityContext(getContext(), activity));
        } catch (Throwable e) {
            Log.w("wbq", "loadDyActivityPlugin", e);
        }
        return null;
    }

    public void destroy(Context hostContext) {
        clearOptimizedDex(hostContext);
    }
}
