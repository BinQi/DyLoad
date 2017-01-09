package com.jerry.dyloadlib.dyload;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.jerry.dyloadlib.dyload.pm.IDyPluginManagerImpl;

/**
 * Created by wubinqi on 16-10-28.
 */
public class DyService extends Service {

    public static final String PROCESS_SUFFIX = "com.jerry.dyload";

    private static IDyPluginManagerImpl sIDyPluginManage = null;

    @Override
    public IBinder onBind(Intent intent) {
        return sIDyPluginManage;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        keepAlive();
        if (null == sIDyPluginManage) {
            sIDyPluginManage = new IDyPluginManagerImpl(this);
        }
        sIDyPluginManage.setService(this);
    }

    private void keepAlive() {
        try {
            Notification notification = new Notification();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            startForeground(0, notification); // 设置为前台服务避免kill，Android4.3及以上需要设置id为0时通知栏才不显示该通知；
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int ret = super.onStartCommand(intent, flags, startId);
        return sIDyPluginManage.onStartCommand(ret, intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sIDyPluginManage.destroy();
        Intent intent = new Intent(this, DyService.class);
        intent.setPackage(this.getPackageName());
        startService(intent);
    }
}
