package com.jerry.dyloadlib.dyload.pm;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.jerry.dyloadlib.dyload.DyService;
import com.jerry.dyloadlib.dyload.pm.sdks.SdksManager;
import com.jerry.dyloadlib.dyload.util.log.Logger;

/**
 * Facade for integration
 * Created by wubinqi on 16-11-1.
 */
public class DyHelper implements ServiceConnection {
    private static DyHelper sInstance = new DyHelper();
    private Context mHostContext;
    private IDyPluginManage mManagerImpl;

    private DyHelper() { }

    public static DyHelper getInstance() {
        return sInstance;
    }

    /**
     * called in {@linkplain Application#onCreate()}
     * @param application
     */
    public void applicationOnCreate(final Application application) {
        mHostContext = application;

        ParamsManager.getInstance(mHostContext);
        SdksManager.getInstance().initLocalSdks(application);
        ParamsManager.getInstance(mHostContext).loadParams(SdksManager.getInstance());
        connect2Service();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mManagerImpl = IDyPluginManage.Stub.asInterface(service);
        Logger.i("wbq", "onServiceConnected DyService is connected");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Logger.i("wbq", "onServiceDisconnected DyService is disconnected");
        mManagerImpl = null;
        connect2Service();
    }

    /**
     * {@linkplain IDyPluginManage#invokePluginAPIMethod(String, String, Bundle)}
     */
    public Bundle invokePluginAPIMethod(String pluginPkgName, String methodStr, Bundle params) throws RemoteException {
        if (mManagerImpl != null) {
            return mManagerImpl.invokePluginAPIMethod(pluginPkgName, methodStr, params);
        }
        return new Bundle();
    }
    /**
     * {@linkplain IDyPluginManage#setLogState(String, boolean)}
     */
    public void setLogState(String pluginPkgName, boolean isLog) throws RemoteException {
        if (mManagerImpl != null) {
            mManagerImpl.setLogState(pluginPkgName, isLog);
        }
    }
    /**
     * {@linkplain IDyPluginManage#setServer(String, boolean)}
     */
    public void setServer(String pluginPkgName, boolean isLog) throws RemoteException {
        if (mManagerImpl != null) {
            mManagerImpl.setServer(pluginPkgName, isLog);
        }
    }

    private void connect2Service() {
        if (null == mManagerImpl) {
            try {
                Intent intent = new Intent(mHostContext, DyService.class);
                intent.setPackage(mHostContext.getPackageName());
                mHostContext.startService(intent);
                mHostContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
            } catch (Exception e) {
                Logger.w("wbq", "connect2Service", e);
            }
        }
    }
}