package com.jerry.dyloadlib.dyload.pl;

import android.content.Context;

import com.jerry.dyloadlib.dyload.DyManager;


/**
 * Created by wubinqi on 16-10-9.
 */
public class ConvenientDyObserver implements DyManager.IDyObserver {

    private Context mContext;

    public ConvenientDyObserver(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 插件加载完成回调
     *
     * @param pluginPkgName 插件包名
     */
    @Override
    public void onPluginLoaded(String pluginPkgName) {
        // 可重新初始化对应插件
    }
}
