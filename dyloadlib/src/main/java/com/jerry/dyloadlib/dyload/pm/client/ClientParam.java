package com.jerry.dyloadlib.dyload.pm.client;

import android.content.Context;
import com.jerry.dyloadlib.dyload.util.thread.CustomThreadExecutorProxy;

/**
 * Created by wubinqi on 16-11-8.
 */
public abstract class ClientParam<T> {
    private T mValue;
    private boolean mHadStartLoad = false;
    private boolean mIsSuccessLoaded;
    private Context mContext;
    private IClientParam mListener;

    public ClientParam(Context context, IClientParam listener) {
        mContext = context.getApplicationContext();
        mListener = listener;
    }

    final public void loadParam() {
        if (isValueLoaded()) {
            if (mListener != null) {
                mListener.onParamLoaded(true, ClientParam.this);
            }
            return;
        }
        CustomThreadExecutorProxy.getInstance().runOnAsyncThread(new Runnable() {
            @Override
            public void run() {
                assignValue(mListener);
            }
        }); //, "ClientParam-loadParam");
    }

    public T getValue() {
        if (!isValueLoaded()) {
            CustomThreadExecutorProxy.getInstance().runOnAsyncThread(new Runnable() {
                @Override
                public void run() {
                    loadParam();
                }
            });
        }
        return mValue;
    }

    public boolean hadStartLoad() {
        return mHadStartLoad;
    }

    private void assignValue(final IClientParam listener) {
        loadValue(new ICPValue<T>() {
            @Override
            public void onValueLoaded(boolean success, T value) {
                if (!mHadStartLoad) {
                    mHadStartLoad = true;
                }
                mValue = success ? value : mValue;
                mIsSuccessLoaded = mIsSuccessLoaded ? mIsSuccessLoaded : success;
                if (listener != null) {
                    listener.onParamLoaded(success, ClientParam.this);
                }
            }
        });
    }

    protected boolean isValueLoaded() {
        return mIsSuccessLoaded && (mValue != null);
    }

    protected void setValueLoaded(T value) {
        if (value != null) {
            mIsSuccessLoaded = true;
            mHadStartLoad = true;
            mValue = value;
        }
    }

    public Context getContext() {
        return mContext;
    }

    protected abstract void loadValue(final ICPValue cpv);

    /**
     * Created by wubinqi
     */
    interface ICPValue<E> {
        void onValueLoaded(boolean success, E value);
    }
    /**
     * Created by wubinqi
     */
    public interface IClientParam {
        void onParamLoaded(final boolean success, final ClientParam param);
    }
}
