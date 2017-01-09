package com.jerry.dyloadlib.dyload.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.jerry.dyloadlib.dyload.core.DyClassLoader;
import com.jerry.dyloadlib.dyload.core.proxy.activity.DyResources;

import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * dex处理工具类
 * @author wubinqi
 */
public class DexUtil {

    /**
     * 获取classloader
     *
     * @param context
     * @param dexPath
     * @return
     */
    public static DexClassLoader createDexClassLoader(Context context, String optimizedDirectory, String dexPath) {
        DexClassLoader loader = new DyClassLoader(dexPath, optimizedDirectory,
                null, context.getClassLoader());
        return loader;
    }

    public static PackageInfo getDexPackageInfo(Context context, String path) {
        PackageInfo packageInfo = context.getPackageManager()
                .getPackageArchiveInfo(
                        path,
                        PackageManager.GET_ACTIVITIES
                                | PackageManager.GET_SERVICES);
        if (packageInfo == null) {
            return null;
        }
        return packageInfo;
    }

    public static Resources createResource(Context context, String dexPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, dexPath);
            Resources superRes = context.getResources();
//            Resources newResource = new Resources(assetManager, superRes.getDisplayMetrics(),
//                    superRes.getConfiguration());
//            return newResource;
            return new DyResources(superRes, assetManager);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
