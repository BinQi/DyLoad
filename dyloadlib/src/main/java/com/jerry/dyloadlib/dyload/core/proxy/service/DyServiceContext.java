package com.jerry.dyloadlib.dyload.core.proxy.service;

import android.app.Service;
import android.content.ContextWrapper;
import android.view.LayoutInflater;

import com.jerry.dyloadlib.dyload.core.DyContext;
import com.jerry.dyloadlib.dyload.core.inflater.DyLayoutInflater;
import com.jerry.dyloadlib.dyload.util.log.Logger;

import java.lang.ref.WeakReference;

/**
 * Created by wubinqi on 16-9-22.
 */
public class DyServiceContext extends ContextWrapper {

    private LayoutInflater mLayoutInflater;
    private DyContext mDyContext;
    private WeakReference<Service> mServiceRef;

    public DyServiceContext(DyContext base, Service service) {
        super(base);
        mDyContext = base;
        mServiceRef = new WeakReference<Service>(service);
    }

    /**
     * @return 代理的Service
     */
    public Service getService() {
        return mServiceRef.get();
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
            Logger.d(DyContext.TAG, "DyServiceContext-getSystemService:LayoutInflater");
            if (mLayoutInflater == null) {
                mLayoutInflater = DyLayoutInflater.create((LayoutInflater) object, this);
            }
            return mLayoutInflater;
        }
        return object;
    }
}
