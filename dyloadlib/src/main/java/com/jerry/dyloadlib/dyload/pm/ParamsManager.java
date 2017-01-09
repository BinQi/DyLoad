package com.jerry.dyloadlib.dyload.pm;

import android.content.Context;

import com.jerry.dyloadlib.dyload.pm.client.ClientParam;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wubinqi on 16-11-8.
 */
public class ParamsManager implements ClientParam.IClientParam {
    private static ParamsManager sInstance;
    private Context mContext;
    private IParamsObserver mIParamsObserver;
    private boolean mHadCallAllLoaded = false;
    private Map<String, ClientParam> mClientParams;

    private ParamsManager(Context context) {
        mContext = context.getApplicationContext() != null ? context.getApplicationContext() : context;

        mClientParams = new ConcurrentHashMap<String, ClientParam>();
    }

    public static ParamsManager getInstance(Context context) {
        if (null == sInstance) {
            synchronized (ParamsManager.class) {
                if (null == sInstance) {
                    sInstance = new ParamsManager(context);
                }
            }
        }
        return sInstance;
    }

    void loadParams(IParamsObserver observer) {
        if (mIParamsObserver != observer) {
            mIParamsObserver = observer;
            mHadCallAllLoaded = false;
        }
        for (ClientParam cp : mClientParams.values()) {
            cp.loadParam();
        }
    }

    @Override
    public void onParamLoaded(boolean success, ClientParam param) {
        if (mHadCallAllLoaded) {
            if (success && mIParamsObserver != null) {
                mIParamsObserver.onParamChanged(param);
            }
        } else {
            boolean isAllLoaded = true;
            for (ClientParam cp : mClientParams.values()) {
                if (!cp.hadStartLoad()) {
                    isAllLoaded = false;
                    break;
                }
            }
            if (isAllLoaded && mIParamsObserver != null) {
                mHadCallAllLoaded = true;
                mIParamsObserver.onAllLoaded();
            }
        }
    }

    /**
     * Created by wubinqi
     */
    public interface IParamsObserver {
        void onAllLoaded();
        void onParamChanged(ClientParam changedParam);
    }
}
