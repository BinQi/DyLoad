package com.jerry.dyloadlib.dyload.core.inflater;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.jerry.dyloadlib.dyload.util.log.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by wubinqi on 16-8-11.
 */
public class InflaterFactory implements LayoutInflater.Factory {

    private LayoutInflater.Factory mBaseFactory;
    private ClassLoader mClassLoader;

    public InflaterFactory(LayoutInflater.Factory base, ClassLoader classLoader) {
        if (null == classLoader) {
            throw new IllegalArgumentException("classLoader is null");
        }
        mBaseFactory = base;
        mClassLoader = classLoader;
    }

    @Override
    public View onCreateView(String s, Context context, AttributeSet attributeSet) {
        View v = null;
        try {
            if (s != null && s.contains(".")) {
                Class<?> clazz = mClassLoader.loadClass(s);
                Constructor c = clazz.getConstructor(Context.class, AttributeSet.class);
                v = (View) c.newInstance(context, attributeSet);
            }
        } catch (ClassNotFoundException e) {
            Logger.w("wbq", "LayoutInflater.Factory", e);
        } catch (NoSuchMethodException e) {
            Logger.w("wbq", "LayoutInflater.Factory", e);
        } catch (IllegalAccessException e) {
            Logger.w("wbq", "LayoutInflater.Factory", e);
        } catch (InstantiationException e) {
            Logger.w("wbq", "LayoutInflater.Factory", e);
        } catch (InvocationTargetException e) {
            Logger.w("wbq", "LayoutInflater.Factory", e);
        }
        return v != null ? v : (mBaseFactory != null ? mBaseFactory.onCreateView(s, context, attributeSet) : null);
    }
}
