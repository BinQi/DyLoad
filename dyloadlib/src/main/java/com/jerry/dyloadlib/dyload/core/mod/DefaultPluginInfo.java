package com.jerry.dyloadlib.dyload.core.mod;

import android.content.Context;
import android.util.Log;

import com.jerry.dyloadlib.dyload.DyService;
import com.jerry.dyloadlib.dyload.pl.AbsEntrance;
import com.jerry.dyloadlib.dyload.pl.IEntrance;
import com.jerry.dyloadlib.dyload.util.ProcessUtil;
import com.jerry.dyloadlib.dyload.util.reflect.MethodUtils;

import java.io.File;

/**
 * Created by wubinqi on 16-10-26.
 */
public class DefaultPluginInfo extends ApkInfo<IEntrance> {

    static final String ENTRANCE_CLASS = "com.jerry.dyloadlib.dyload.entrance.PluginEntrance";

    public DefaultPluginInfo(Context context, File file) {
        super(context, file);
    }

    /**
     * 加载入口类
     */
    @Override
    protected void loadEntrance() {
        try {
            Class<?> clazz = mClassLoader.loadClass(ENTRANCE_CLASS);
            Object obj = MethodUtils.invokeConstructor(clazz, getContext());
            if (obj instanceof AbsEntrance) {
                mEntrance = (IEntrance) obj;
            } else {
                Log.w("wbq", "DefaultPluginInfo obj not AbsEntrance");
            }
        } catch (Throwable e) {
            Log.w("wbq", "DefaultPluginInfo loadEntrance", e);
        }
        if (null == mEntrance) {
            Log.w("wbq", "DefaultPluginInfo loadEntrance failed");
        }
    }

    @Override
    public void restart() {
        ProcessUtil.killProcessWithSuffix(mContext, DyService.PROCESS_SUFFIX);
    }

    @Override
    public void destroy(Context hostContext) {
        if (mEntrance != null) {
            mEntrance.onDestroy();
        }
        super.destroy(hostContext);
    }
}
