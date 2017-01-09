package com.jerry.dyloadlib.dyload.core;

import android.os.Build;

import dalvik.system.DexClassLoader;

/**
 * Created by wubinqi on 16-9-22.
 */
public class DyClassLoader extends DexClassLoader {

    public DyClassLoader(String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, libraryPath, parent);
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = findLoadedClass(className);

        if (clazz == null) {
            ClassNotFoundException suppressed = null;

            try {
                clazz = findClass(className);
            } catch (ClassNotFoundException e) {
                suppressed = e;
            }

            if (clazz == null) {
                try {
                    clazz = getParent().loadClass(className);
                } catch (ClassNotFoundException e) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        e.addSuppressed(suppressed);
                    }
                    throw e;
                }
            }
        }
        return clazz;
    }
}
