package com.jerry.dyloadlib.dyload.core.proxy.activity;

import android.annotation.TargetApi;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * Created by wubinqi on 16-11-4.
 */
public class DyResources extends Resources {

    private Resources mSuperResources;

    public DyResources(Resources superRes, AssetManager assets) {
        super(assets, superRes.getDisplayMetrics(), superRes.getConfiguration());
        mSuperResources = superRes;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Drawable getDrawable(int id, Theme theme) throws NotFoundException {
        Drawable drawable = null;
        try {
            drawable = super.getDrawable(id, theme);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return drawable != null ? drawable : mSuperResources.getDrawable(id, theme);
    }

    @Override
    public Drawable getDrawable(int id) throws NotFoundException {
        Drawable drawable = null;
        try {
            drawable = super.getDrawable(id);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return drawable != null ? drawable : mSuperResources.getDrawable(id);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Drawable getDrawableForDensity(int id, int density, Theme theme) {
        Drawable drawable = null;
        try {
            drawable = super.getDrawableForDensity(id, density, theme);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return drawable != null ? drawable : mSuperResources.getDrawableForDensity(id, density, theme);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    @Override
    public Drawable getDrawableForDensity(int id, int density) throws NotFoundException {
        Drawable drawable = null;
        try {
            drawable = super.getDrawableForDensity(id, density);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return drawable != null ? drawable : mSuperResources.getDrawableForDensity(id, density);
    }
}
