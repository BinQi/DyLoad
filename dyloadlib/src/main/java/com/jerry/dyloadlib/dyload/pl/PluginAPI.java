package com.jerry.dyloadlib.dyload.pl;

import com.jerry.dyloadlib.dyload.util.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by wubinqi on 16-10-26.
 */
public abstract class PluginAPI {

    public final Object invokeMethod(String methodStr, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return MethodUtils.invokeMethod(this, methodStr, args);
    }
}
