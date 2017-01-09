package com.jerry.dyloadlib.dyload.core.proxy.activity;

import com.jerry.dyloadlib.dyload.core.DyContext;

/**
 * Created by wubinqi on 16-9-21.
 */
public interface IActivityAttachable {
    void attach(DyActivityPlugin proxyActivity, DyContext dyContext);
}
