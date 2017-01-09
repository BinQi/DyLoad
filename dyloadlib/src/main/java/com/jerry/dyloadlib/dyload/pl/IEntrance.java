package com.jerry.dyloadlib.dyload.pl;

import android.content.ComponentName;

/**
 * Created by wubinqi on 16-10-26.
 */
public interface IEntrance {

    /**
     * 创建
     */
    void onCreate();

    /**
     * 销毁
     */
    void onDestroy();

    /**
     * @return 获取插件API类
     */
    PluginAPI getAPI();

    /**
     * 获取需要特殊代理的类
     * @param component
     * @return
     */
    String getSpecialClazz(ComponentName proxyComponent);
}
