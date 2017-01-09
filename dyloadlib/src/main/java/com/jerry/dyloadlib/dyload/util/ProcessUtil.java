package com.jerry.dyloadlib.dyload.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import com.jerry.dyloadlib.dyload.DyService;
import com.jerry.dyloadlib.dyload.util.log.Logger;

import java.util.List;

/**
 * Created by wubinqi on 16-9-27.
 */
public class ProcessUtil {

    private static String sCurrentProcessName = null;
    private static Object sGetCurrentProcessNameLock = new Object();

    /**
     * kill current process
     */
    public static void killSelf() {
        Process.killProcess(Process.myPid());
    }

    /**
     * kill process whose name is packageName:suffix
     * @param context
     * @param suffix
     */
    public static void killProcessWithSuffix(Context context, String suffix) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> rsinfos = am
                    .getRunningServices(Integer.MAX_VALUE);
            for (ActivityManager.RunningServiceInfo info : rsinfos) {
                String process = info.process;
                Logger.d("wbq", "service process:" + process);
                if (process != null && process.trim().equals(getFullProcessNameWithSuffix(context, suffix))) {
                    Logger.d("wbq", "kill process:" + process);
                    Process.killProcess(info.pid);
                    return;
                }
            }
        } catch (Exception e) {
            Logger.w("wbq", "isOthersRunning Error:", e);
        }
    }

    /**
     * @param context
     * @param suffix
     * @return packageName:suffix
     */
    public static String getFullProcessNameWithSuffix(Context context, String suffix) {
        return context.getPackageName() + ":" + suffix;
    }

    /**
     * @param context
     * @return null or process name
     */
    public static String getCurrentProcessName(Context context) {
        if (context == null) {
            return sCurrentProcessName;
        }
        if (null == sCurrentProcessName) {
            synchronized (sGetCurrentProcessNameLock) {
                if (null == sCurrentProcessName) {
                    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningAppProcessInfo> infos = activityManager.getRunningAppProcesses();
                    if (infos == null) {
                        return null;
                    }
                    for (ActivityManager.RunningAppProcessInfo info : infos) {
                        if (info.pid == Process.myPid()) {
                            sCurrentProcessName = info.processName;
                            return sCurrentProcessName;
                        }
                    }
                }
            }
        }
        return sCurrentProcessName;
    }

    /**
     * @return whether current process is main process
     */
    public static boolean isMainProcess(Context context) {
        return context.getApplicationContext().getPackageName().equals(getCurrentProcessName(context));
    }

    /**
     * @return whether current process is DyService's process
     */
    public static boolean isDyServiceProcess(Context context) {
        return getFullProcessNameWithSuffix(context, DyService.PROCESS_SUFFIX).equals(getCurrentProcessName(context));
    }
}
