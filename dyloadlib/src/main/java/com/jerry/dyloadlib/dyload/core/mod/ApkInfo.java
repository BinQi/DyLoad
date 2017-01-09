package com.jerry.dyloadlib.dyload.core.mod;

import android.content.Context;

import com.jerry.dyloadlib.dyload.core.DyContext;
import com.jerry.dyloadlib.dyload.util.DexUtil;
import com.jerry.dyloadlib.dyload.util.log.Logger;

import java.io.File;

/**
 * Created by wubinqi on 16-9-20.
 * @param <E>
 */
public abstract class ApkInfo<E> extends DyPluginInfo<E> {

    public ApkInfo(Context context, File file) {
        super(context, file);
    }

    @Override
    protected void buildInfo(Context context, File file) {
        Logger.d(TAG, file.getName());
        mPackageInfo = DexUtil.getDexPackageInfo(context, file.getAbsolutePath());
        if (mPackageInfo != null) {
            mPackageName = mPackageInfo.packageName;
            mVersionName = mPackageInfo.versionName;
            mVersionCode = mPackageInfo.versionCode;
        }
        mClassLoader = DexUtil.createDexClassLoader(context, getOptimizedDirectory(context), file.getAbsolutePath());
        mResource = DexUtil.createResource(context, file.getAbsolutePath());
        mContext = new DyContext(mPackageName, context, mClassLoader, mResource);
    }
}
