package com.jerry.dyloadlib.dyload.pm;

import android.content.Context;

import com.jerry.dyloadlib.dyload.DyManager;
import com.jerry.dyloadlib.dyload.pm.sdks.SdksManager;
import com.jerry.dyloadlib.dyload.util.thread.CustomThreadExecutorProxy;

/**
 * Created by wubinqi on 16-11-8.
 */
public class DyObserver implements DyManager.IDyObserver {

    private Context mContext;

    public DyObserver(Context context) {
        mContext = context;
    }
    /**
     * 插件加载完成回调
     *
     * @param pluginPkgName 插件包名
     */
    @Override
    public void onPluginLoaded(String pluginPkgName) {
        CustomThreadExecutorProxy.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                SdksManager.getInstance().tryInitPluginSdks(mContext);
            }
        });
    }
}
