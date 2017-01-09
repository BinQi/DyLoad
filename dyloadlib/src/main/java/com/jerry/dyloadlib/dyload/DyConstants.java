package com.jerry.dyloadlib.dyload;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import com.jerry.dyloadlib.dyload.util.EncryptUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wubinqi
 */
public class DyConstants {

    public static final int VERSION_CODE = 1; // 插件框架版本号

    private static String sCOMMON_FILE_PATH = null;

    public static final String LOCAL_PKG_NAME = "pluginpkg";

    public static final String EXTRA_CLASS = "extra.class";
    public static final String EXTRA_PACKAGE = "extra.package";
    static final String PATH_PREFIX = "/DyLoad/runtime";

    public static String getYourHostDexPath(Context context) {
        if (sCOMMON_FILE_PATH == null) {
            String externalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            List<String> sdList = getAllSDPaths(context);
            int index = sdList.indexOf(externalPath);
            if (index >= 0) { // 外置sd卡优先
                sCOMMON_FILE_PATH = externalPath + PATH_PREFIX;
            } else if (sdList.size() > 0) {
                sCOMMON_FILE_PATH = sdList.get(0) + PATH_PREFIX;
            } else {
                sCOMMON_FILE_PATH = externalPath + PATH_PREFIX;
            }
        }

        String packageName = context.getPackageName();
        packageName = EncryptUtil.simpleEncryption(packageName);
        String path = sCOMMON_FILE_PATH + "/" + packageName;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static List<String> getAllSDPaths(Context context) {
        List<String> files = new ArrayList<String>();
        StorageManager sm = (StorageManager) context
                .getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            String[] paths = (String[]) getVolumePathsMethod.invoke(sm, params);

            for (int i = 0; i < paths.length; i++) {
                String status = (String) sm.getClass()
                        .getMethod("getVolumeState", String.class)
                        .invoke(sm, paths[i]);
                if (status.equals(Environment.MEDIA_MOUNTED)) {
                    files.add(paths[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return files;
        }
        return files;
    }
}
