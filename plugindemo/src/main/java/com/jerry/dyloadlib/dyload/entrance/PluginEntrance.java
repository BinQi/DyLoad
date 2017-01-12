package com.jerry.dyloadlib.dyload.entrance;

import android.content.ComponentName;

import com.jerry.dyloadlib.dyload.core.DyContext;
import com.jerry.dyloadlib.dyload.pl.AbsEntrance;
import com.jerry.dyloadlib.dyload.pl.PluginAPI;

/**
 * Created by wubinqi on 17-1-6.
 */
public class PluginEntrance extends AbsEntrance {

    private PluginDemoApi mApi;

    public PluginEntrance(DyContext context) {
        super(context);
        mApi = new PluginDemoApi(context);
    }

    /**
     * 创建
     */
    @Override
    public void onCreate() {

    }

    /**
     * 销毁
     */
    @Override
    public void onDestroy() {

    }

    /**
     * @return 获取插件API类
     */
    @Override
    public PluginAPI getAPI() {
        return mApi;
    }

    /**
     * 获取需要特殊代理的类
     *
     * @param proxyComponent@return
     */
    @Override
    public String getSpecialClazz(ComponentName proxyComponent) {
        return null;
    }
}
