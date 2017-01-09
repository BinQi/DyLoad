package com.jerry.dyloadlib.dyload.pm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;

import com.jerry.dyloadlib.dyload.DyManager;
import com.jerry.dyloadlib.dyload.core.mod.DefaultPluginInfo;
import com.jerry.dyloadlib.dyload.core.mod.DyPluginInfo;
import com.jerry.dyloadlib.dyload.pl.PluginAPI;
import com.jerry.dyloadlib.dyload.pm.sdks.SdksManager;
import com.jerry.dyloadlib.dyload.util.log.Logger;

import java.lang.ref.WeakReference;

/**
 * Created by wubinqi on 16-11-1.
 */
public class IDyPluginManagerImpl extends IDyPluginManage.Stub {

    private Context mHostContext;
    private WeakReference<Service> mDyService;
    private DyObserver mDyObserver;

    public IDyPluginManagerImpl(Context hostContext) {
        mHostContext = hostContext.getApplicationContext();
        initDy();
    }

    private void initDy() {
        if (null == mDyObserver) {
            mDyObserver = new DyObserver(mHostContext);
        }
        DyManager.getInstance(mHostContext).init(mDyObserver);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SdksManager.getInstance().tryInitPluginSdks(mHostContext);
            }
        }).start();
    }

    public void setService(Service service) {
        if (service != null) {
            mDyService = new WeakReference<Service>(service);
        }
    }

    public int onStartCommand(int superResult, Intent intent, int flags, int startId) {
        return superResult;
    }

    public void destroy() {
        mDyService.clear();
        mDyService = null;
    }

    @Override
    public Bundle invokePluginAPIMethod(String pkgName, String methodStr, Bundle params) throws RemoteException {
        Logger.d("wbq", "invokePluginAPIMethod->pkg=" + pkgName + " method=" + methodStr);
        DyPluginInfo dyInfo = DyManager.getInstance(mHostContext).getDyPluginInfo(pkgName);
        if (dyInfo instanceof DefaultPluginInfo) {
            DefaultPluginInfo dfInfo = (DefaultPluginInfo) dyInfo;
            PluginAPI api = dfInfo != null ? dfInfo.getEntrance().getAPI() : null;
            if (null == api) {
                Logger.w("wbq", "PluginAPI not found");
                return null;
            }
            Object result;
            try {
                result = params != null ? api.invokeMethod(methodStr, params) : api.invokeMethod(methodStr);
            } catch (Throwable e) {
                Logger.w("wbq", "invokePluginAPIMethod", e);
                return null;
            }
            if (result instanceof Bundle) {
                return (Bundle) result;
            }
            if (null == result) {
                Logger.d("wbq", "invokePluginAPIMethod:returnType void");
            } else {
                Logger.w("wbq", "invokePluginAPIMethod:returnType error!");
            }
        } else {
            Logger.w("wbq", "invokePluginAPIMethod:can not find plugin=" + pkgName);
        }
        return null;
    }

    @Override
    public void setLogState(String pluginPkgName, boolean isLog) throws RemoteException {
        // TODO test code
        SdksManager.getInstance().tryInitPluginSdks(mHostContext);
    }

    @Override
    public void setServer(String pluginPkgName, boolean isTestServer) throws RemoteException {
        // TODO test code
        SdksManager.getInstance().tryInitPluginSdks(mHostContext);
    }
}
