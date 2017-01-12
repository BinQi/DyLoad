package com.jerry.dyloadlib.dyload.entrance;

import android.content.Intent;

import com.jerry.dyloadlib.dyload.DyManager;
import com.jerry.dyloadlib.dyload.core.DyContext;
import com.jerry.dyloadlib.dyload.core.DyIntent;
import com.jerry.dyloadlib.dyload.pl.PluginAPI;
import com.jerry.plugindemo.MainActivity;

/**
 * Created by wubinqi on 17-1-6.
 */
public class PluginDemoApi extends PluginAPI {

    private DyContext mContext;

    public PluginDemoApi(DyContext context) {
        mContext = context;
    }

    public void startMainActivity() {
        DyIntent dlIntent = new DyIntent(mContext.getPluginPackageName(), MainActivity.class);
        dlIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dlIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        DyManager.getInstance(mContext).startPluginActivity(mContext, dlIntent);
    }
}
