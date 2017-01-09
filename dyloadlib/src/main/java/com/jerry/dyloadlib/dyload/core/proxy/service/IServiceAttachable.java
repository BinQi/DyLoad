package com.jerry.dyloadlib.dyload.core.proxy.service;

import com.jerry.dyloadlib.dyload.core.DyContext;

/**
 * Created by wubinqi on 16-9-21.
 */
public interface IServiceAttachable {
    void attach(DyServicePlugin proxyActivity, DyContext dyContext);
}
