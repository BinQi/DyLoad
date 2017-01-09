package com.jerry.dyloadlib.dyload.util;

/**
 * 简单转换类
 * @author wubinqi
 */
public class EncryptUtil {

    public static String simpleEncryption(String packageName) {
        if (packageName == null) {
            return "" + packageName;
        }
        return packageName.hashCode() + "";
    }
}
