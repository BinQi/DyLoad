package com.jerry.dyloadlib.dyload.core.mod;

import android.content.Context;
import android.content.pm.PackageInfo;

import java.io.File;

/**
 * Created by wubinqi on 16-10-26.
 */
public class PluginSimpleFactory {

    public static DyPluginInfo create(Context hostContext, PackageInfo pkgInfo, File pluginFile) {
        return new DefaultPluginInfo(hostContext, pluginFile);
    }
}
