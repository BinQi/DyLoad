package com.jerry.dyloadlib.dyload.pm.sdks;

import android.app.Application;
import android.content.Context;

import com.jerry.dyloadlib.dyload.pm.ParamsManager;
import com.jerry.dyloadlib.dyload.pm.client.ClientParam;
import com.jerry.dyloadlib.dyload.util.ProcessUtil;
import com.jerry.dyloadlib.dyload.util.log.Logger;
import com.jerry.dyloadlib.dyload.util.thread.CustomThreadExecutorProxy;

/**
 * Created by wubinqi on 16-11-16.
 */
public class SdksManager implements ParamsManager.IParamsObserver {

    private static SdksManager sInstance = null;
    private Boolean mParamLoadedOnceLock = false;
    private Context mContext;

    private SdksManager() {
    }

    public static SdksManager getInstance() {
        if (null == sInstance) {
            synchronized (SdksManager.class) {
                if (null == sInstance) {
                    sInstance = new SdksManager();
                }
            }
        }
        return sInstance;
    }

    public void initLocalSdks(final Application application) {
        Logger.d("wbq", "initLocalSdks...");
        // TODO init other local SDKs
    }

    /**
     * may block current thread
     *
     * @param context
     */
    public void tryInitPluginSdks(final Context context) {
        if (!ProcessUtil.isDyServiceProcess(context)) {
            return;
        }
        synchronized (mParamLoadedOnceLock) {
            if (!mParamLoadedOnceLock) {
                Logger.d("wbq", "tryInitPluginSdks wait....");
                try {
                    mParamLoadedOnceLock.wait(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Logger.d("wbq", "tryInitPluginSdks continued");
            CustomThreadExecutorProxy.getInstance().runOnAsyncThread(new Runnable() {
                @Override
                public void run() {
                    initPluginSdks(context);
                }
            }, 1000);
        }
    }

    private void initPluginSdks(Context context) {
        Logger.d("wbq", "initPluginSdks...");
        // TODO init other plugin SDKs
    }

    @Override
    public void onAllLoaded() {
        synchronized (mParamLoadedOnceLock) {
            mParamLoadedOnceLock = true;
            try {
                mParamLoadedOnceLock.notifyAll();
            } catch (IllegalMonitorStateException e) {
            }
        }
    }

    @Override
    public void onParamChanged(ClientParam param) {
    }
}
