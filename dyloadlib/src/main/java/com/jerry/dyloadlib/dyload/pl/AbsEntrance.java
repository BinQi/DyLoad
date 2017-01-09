package com.jerry.dyloadlib.dyload.pl;

import com.jerry.dyloadlib.dyload.core.DyContext;

/**
 * Created by wubinqi on 16-10-26.
 */
public abstract class AbsEntrance implements IEntrance {

    private DyContext mDyContext;

    public AbsEntrance(DyContext context) {
        mDyContext = context;
        onCreate();
    }

    protected DyContext getDyContext() {
        return mDyContext;
    }
}
