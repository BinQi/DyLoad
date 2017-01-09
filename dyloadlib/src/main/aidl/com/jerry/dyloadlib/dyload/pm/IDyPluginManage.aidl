// IDyPluginManage.aidl
package com.jerry.dyloadlib.dyload.pm;

// Declare any non-default types here with import statements

interface IDyPluginManage {
    /**
     * communication between Host and Plugin
     * through Bundle
     */
    Bundle invokePluginAPIMethod(String pluginPkgName, String methodStr, in Bundle params);
    /**
     * logState
     */
    void setLogState(String pluginPkgName, boolean isLog);
    /**
     * isTestServer
     */
    void setServer(String pluginPkgName, boolean isTestServer);
}
