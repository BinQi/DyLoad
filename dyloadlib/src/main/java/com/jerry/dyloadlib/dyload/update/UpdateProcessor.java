package com.jerry.dyloadlib.dyload.update;

import android.content.Context;
import android.text.TextUtils;

import com.jerry.dyloadlib.dyload.DyManager;
import com.jerry.dyloadlib.dyload.util.log.Logger;
import com.jerry.dyloadlib.dyload.util.thread.CustomThreadExecutorProxy;

import java.io.File;

/**
 * Created by wubinqi on 16-9-29.
 */
public class UpdateProcessor implements IUpdate {
    private Context mContext;

    public UpdateProcessor(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public void onChanged(final String path) {
        Logger.d("wbq", "UpdateProcessor onChanged..");
        if (TextUtils.isEmpty(path)) {
            Logger.d("wbq", "UpdateProcessor path empty");
            return;
        }
        CustomThreadExecutorProxy.getInstance().runOnAsyncThread(new Runnable() {
            @Override
            public void run() {
                File file = new File(path);
                DyManager.getInstance(mContext).updatePlugin(file);
            }
        });
    }
}
