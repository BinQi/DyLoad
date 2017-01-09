package com.jerry.dyloadlib.dyload.update;

/**
 * Created by wubinqi on 17-1-4.
 */
public interface IUpdate {
    /**
     * @param path 插件包路径
     */
    void onChanged(final String path);
}
