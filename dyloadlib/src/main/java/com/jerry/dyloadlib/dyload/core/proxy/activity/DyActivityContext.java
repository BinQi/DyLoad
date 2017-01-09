package com.jerry.dyloadlib.dyload.core.proxy.activity;

import android.app.Activity;
import android.content.ContextWrapper;
import android.view.LayoutInflater;

import com.jerry.dyloadlib.dyload.core.DyContext;
import com.jerry.dyloadlib.dyload.core.inflater.DyLayoutInflater;
import com.jerry.dyloadlib.dyload.util.log.Logger;

import java.lang.ref.WeakReference;

/**
 * Created by wubinqi on 16-9-22.
 */
public class DyActivityContext extends ContextWrapper {

    private LayoutInflater mLayoutInflater;
    private DyContext mDyContext;
    private WeakReference<Activity> mActivityRef;

    public DyActivityContext(DyContext base, Activity activity) {
        super(base);
        mDyContext = base;
        mActivityRef = new WeakReference<Activity>(activity);
    }

    /**
     * @return 代理的Activity
     */
    public Activity getActivity() {
        return mActivityRef.get();
    }

    /**
     * @return 插件的Context
     */
    public DyContext getDyContext() {
        return mDyContext;
    }

    @Override
    public Object getSystemService(String name) {
        Object object = super.getSystemService(name);
        if (object instanceof LayoutInflater) {
            Logger.d(DyContext.TAG, "getSystemService:LayoutInflater");
            if (mLayoutInflater == null) {
                mLayoutInflater = DyLayoutInflater.create((LayoutInflater) object, this);
            }
            return mLayoutInflater;
        }
        return object;
    }
}
