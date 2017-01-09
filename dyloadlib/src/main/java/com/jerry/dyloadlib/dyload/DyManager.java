package com.jerry.dyloadlib.dyload;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.jerry.dyloadlib.dyload.core.DyIntent;
import com.jerry.dyloadlib.dyload.core.mod.DyPluginInfo;
import com.jerry.dyloadlib.dyload.core.mod.PluginSimpleFactory;
import com.jerry.dyloadlib.dyload.core.proxy.activity.DyActivityPlugin;
import com.jerry.dyloadlib.dyload.core.proxy.activity.StandarProxyActivity;
import com.jerry.dyloadlib.dyload.update.UpdateProcessor;
import com.jerry.dyloadlib.dyload.util.DexUtil;
import com.jerry.dyloadlib.dyload.util.FileUtil;
import com.jerry.dyloadlib.dyload.util.ProcessUtil;
import com.jerry.dyloadlib.dyload.util.log.Logger;
import com.jerry.dyloadlib.dyload.util.reflect.FieldUtils;
import com.jerry.dyloadlib.dyload.util.reflect.MethodUtils;
import com.jerry.dyloadlib.dyload.util.thread.CustomThreadExecutorProxy;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wubinqi on 16-9-20.
 */
public class DyManager {
    /**
     * return value of {@link #startPluginActivity(Context, DyIntent)} start
     * success
     */
    public static final int START_RESULT_SUCCESS = 0;

    /**
     * return value of {@link #startPluginActivity(Context, DyIntent)} package
     * not found
     */
    public static final int START_RESULT_NO_PKG = 1;

    /**
     * return value of {@link #startPluginActivity(Context, DyIntent)} class
     * not found
     */
    public static final int START_RESULT_NO_CLASS = 2;

    /**
     * return value of {@link #startPluginActivity(Context, DyIntent)} class
     * type error
     */
    public static final int START_RESULT_TYPE_ERROR = 3;

    private static DyManager sInstance = null;
    private Context mHostContext;
    private IDyObserver mIDyObserver;
    private Map<String, DyPluginInfo> mPackagesHolder;
    private UpdateProcessor mUpdateProcessor;
    private String mLastNotInformPkg = null;

    public static DyManager getInstance(Context context) {
        if (null == sInstance) {
            synchronized (DyManager.class) {
                if (null == sInstance) {
                    sInstance = new DyManager(context);
                }
            }
        }
        return sInstance;
    }

    private DyManager(Context context) {
        mHostContext = context.getApplicationContext();
        mPackagesHolder = new ConcurrentHashMap<String, DyPluginInfo>();
        install(null);
    }

    private void install(PackageInfo updatePkgInfo) {
        Logger.d("wbq", "DyManager install..");
        File[] files = scanPluginPath();
        if (files == null || 0 == files.length) {
            CustomThreadExecutorProxy.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    writeLocalPkg();
                    installLocal(null);
                }
            });
            return;
        }
        installLocal(updatePkgInfo);
    }

    private void installLocal(PackageInfo updatePkgInfo) {
        File[] files = scanPluginPath();
        if (null == files) {
            return;
        }
        boolean hadStatic = false;
        for (File file : files) {
            PackageInfo pkgInfo = DexUtil.getDexPackageInfo(mHostContext, file.getAbsolutePath());
            if (!checkPkgInfo(pkgInfo)) {
                if (isSamePkgInfo(pkgInfo, updatePkgInfo) && !hadStatic) {
                    hadStatic = true;
                }
                continue;
            }
            DyPluginInfo p = mPackagesHolder.get(pkgInfo.packageName);
            if (p != null) {
                p.destroy(mHostContext);
            }
            p = PluginSimpleFactory.create(mHostContext, pkgInfo, file);
            if (TextUtils.isEmpty(p.getPackageName())
                    || null == p.getEntrance()) {
                p.destroy(mHostContext);
                FileUtil.delFile(p.getFileAbsolutePath());
                if (isSamePkgInfo(pkgInfo, updatePkgInfo) && !hadStatic) {
                    hadStatic = true;
                }
                continue;
            }
            clearReflectCache();
            mPackagesHolder.put(p.getPackageName(), p);
            Logger.d("wbq", "install plugin=" + p.getPackageName());
            if (isSamePkgInfo(pkgInfo, updatePkgInfo) && !hadStatic) {
                hadStatic = true;
            }
            if (mIDyObserver != null) {
                final String pkgName = p.getPackageName();
                CustomThreadExecutorProxy.getInstance().runOnAsyncThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mIDyObserver != null) {
                            mLastNotInformPkg = null;
                            mIDyObserver.onPluginLoaded(pkgName);
                        }
                    }
                });
            } else {
                mLastNotInformPkg = p.getPackageName();
            }
        }
    }

    private void clearReflectCache() {
        Logger.d("wbq", "clear Reflect Cache");
        MethodUtils.clearCache();
        FieldUtils.clearCache();
    }

    private boolean isSamePkgInfo(PackageInfo pkgInfo, PackageInfo pkgInfo1) {
        return pkgInfo != null && pkgInfo1 != null
                && pkgInfo.versionCode == pkgInfo1.versionCode
                && pkgInfo.versionName != null && pkgInfo.versionName.equals(pkgInfo1.versionName);
    }

    public void init(IDyObserver observer) {
        Logger.d("wbq", "DyManager init");
        setIDyObserver(observer);
        if (ProcessUtil.isDyServiceProcess(mHostContext)) {
            mUpdateProcessor = (null == mUpdateProcessor) ? new UpdateProcessor(mHostContext) : mUpdateProcessor;
            // TODO 从服务器下载插件,UpdateProcessor为监听器
        }
    }

    public DyPluginInfo getDyPluginInfo(String pkgName) {
        return mPackagesHolder.get(pkgName);
    }

    /**
     * 更新插件
     */
    public void updatePlugin(File newFile) {
        if (null == newFile || !newFile.exists()) {
            Logger.d("wbq", "updatePlugin pluginFile not exist");
            return;
        }
        PackageInfo pkgInfo = DexUtil.getDexPackageInfo(mHostContext, newFile.getAbsolutePath());
        if (!checkPkgInfo(pkgInfo)) {
            Logger.d("wbq", "updatePlugin pkgInfo not valid");
            return;
        }
        Logger.d("wbq", "DyManager updatePlugin..");
        DyPluginInfo oldInfo = mPackagesHolder.get(pkgInfo.packageName);
        String oldFilePath = null;
        if (oldInfo != null) {
            oldInfo.destroy(mHostContext);
            oldFilePath = oldInfo.getFileAbsolutePath();
        }
        String dexHoldPath = DyConstants.getYourHostDexPath(mHostContext);
        if (!dexHoldPath.equals(newFile.getParent())) {
            FileUtil.copyFile2Dir(newFile.getAbsolutePath(), dexHoldPath + File.separator);
        }
        if (oldFilePath != null && !newFile.getAbsolutePath().equals(oldFilePath)) {
            FileUtil.delFile(oldFilePath);
        }
        if (oldInfo != null) {
            oldInfo.restart();
        }

        install(pkgInfo);
    }

    /**
     * {@link IDyObserver}
     */
    public void setIDyObserver(IDyObserver observer) {
        mIDyObserver = observer;
        if (mLastNotInformPkg != null && mIDyObserver != null) {
            mIDyObserver.onPluginLoaded(mLastNotInformPkg);
        }
    }

    /**
     * @return 插件框架版本号
     */
    public int getDyVersionCode() {
        return DyConstants.VERSION_CODE;
    }

    private void writeLocalPkg() {
        try {
            Logger.d("wbq", "writeLocalPkg..");
            String path = DyConstants.getYourHostDexPath(mHostContext);
            InputStream is = mHostContext.getAssets().open(DyConstants.LOCAL_PKG_NAME);
            FileUtil.saveInputStreamToSDFile(is, path + File.separator + "meal.jar");
        } catch (Throwable e) {
            Logger.w("wbq", "writeLocalPkg", e);
        }
    }

    private boolean checkPkgInfo(PackageInfo pkgInfo) {
        return pkgInfo != null && !TextUtils.isEmpty(pkgInfo.packageName);
    }

    /**
     * 扫描指定的插件目录
     *
     * @return
     */
    private File[] scanPluginPath() {
        String path = DyConstants.getYourHostDexPath(mHostContext);
        return getValidFile(path);
    }

    private File[] getValidFile(String path) {
        File files = new File(path);
        File[] vaildList = files.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                try {
                    PackageInfo packageInfo = mHostContext.getPackageManager()
                            .getPackageArchiveInfo(
                                    pathname.getAbsolutePath(),
                                    PackageManager.GET_ACTIVITIES
                                            | PackageManager.GET_SERVICES);
                    return packageInfo != null && !TextUtils.isEmpty(packageInfo.packageName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        return vaildList;
    }

    public int startPluginActivity(Context context, DyIntent dlIntent) {
        return startPluginActivityForResult(context, dlIntent, -1);
    }

    public int startPluginActivityForResult(Context context, DyIntent dlIntent, int requestCode) {
        String packageName = dlIntent.getPluginPackage();
        if (TextUtils.isEmpty(packageName)) {
            throw new NullPointerException("disallow null packageName.");
        }
        DyPluginInfo pluginPackage = mPackagesHolder.get(packageName);
        if (pluginPackage == null) {
            return START_RESULT_NO_PKG;
        }

        final String className = getPluginActivityFullPath(dlIntent, pluginPackage);
        Class<?> clazz = loadPluginClass(pluginPackage.getClassLoader(), className);
        if (clazz == null) {
            return START_RESULT_NO_CLASS;
        }

        Class<? extends Activity> activityClass = getProxyActivityClass(clazz);
        if (activityClass == null) {
            return START_RESULT_TYPE_ERROR;
        }

        dlIntent.putExtra(DyConstants.EXTRA_CLASS, className);
        dlIntent.putExtra(DyConstants.EXTRA_PACKAGE, packageName);
        dlIntent.setClass(mHostContext, activityClass);
        performStartActivityForResult(context, dlIntent, requestCode);
        return START_RESULT_SUCCESS;
    }

    private String getPluginActivityFullPath(DyIntent dlIntent, DyPluginInfo pluginPackage) {
        String className = dlIntent.getPluginClass();
        if (className.startsWith(".")) {
            className = dlIntent.getPluginPackage() + className;
        }
        return className;
    }

    private Class<?> loadPluginClass(ClassLoader classLoader, String className) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return clazz;
    }

    /**
     * get the proxy activity class, the proxy activity will delegate the plugin
     * activity
     *
     * @param clazz target activity's class
     * @return
     */
    public static Class<? extends Activity> getProxyActivityClass(Class<?> clazz) {
        Class<? extends Activity> activityClass = null;
        if (DyActivityPlugin.class.isAssignableFrom(clazz)) {
            boolean singleInstance = false; // TODO
//            activityClass = singleInstance ? ChargeBatteryProxyActivity.class : StandarProxyActivity.class;
            activityClass = StandarProxyActivity.class;
        }
        return activityClass;
    }

    private void performStartActivityForResult(Context context, DyIntent dyIntent, int requestCode) {
        Logger.d("wbq", "launch " + dyIntent.getPluginClass());
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(dyIntent, requestCode);
        } else {
            context.startActivity(dyIntent);
        }
    }

    /**
     * 回调接口
     */
    public interface IDyObserver {
        /**
         * 插件加载完成回调
         *
         * @param pluginPkgName 插件包名
         */
        void onPluginLoaded(String pluginPkgName);
    }
}
