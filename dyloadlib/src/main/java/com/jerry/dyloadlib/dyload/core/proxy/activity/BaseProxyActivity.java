package com.jerry.dyloadlib.dyload.core.proxy.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.jerry.dyloadlib.dyload.DyConstants;
import com.jerry.dyloadlib.dyload.DyManager;
import com.jerry.dyloadlib.dyload.core.DyContext;
import com.jerry.dyloadlib.dyload.core.mod.DyPluginInfo;

/**
 * Created by wubinqi on 16-9-23.
 */
public abstract class BaseProxyActivity extends Activity implements IActivityAttachable {

    private DyActivityPlugin mRemoteActivity = null;
    private DyContext mDyContext = null; // = DyManager.getInstance(this).getDyPluginInfo(DyConstants.CHARGE_LOCKER_PKG).getContext();

    @Override
    public void attach(DyActivityPlugin proxyActivity, DyContext dyContext) {
        mRemoteActivity = proxyActivity;
        mDyContext = dyContext;
        if (null == mRemoteActivity
                || null == mDyContext) {
            finish();
            Log.e("wbq", " proxy invalid finish activity-" + getClass().getName());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String mPackageName = intent.getStringExtra(DyConstants.EXTRA_PACKAGE);
        String mClass = intent.getStringExtra(DyConstants.EXTRA_CLASS);
        Log.d("wbq", " proxyPkg=" + mPackageName + "proxyClass=" + mClass);
        DyActivityPlugin proxyActivity = null;
        DyContext dyContext = null;
        DyPluginInfo info = DyManager.getInstance(this).getDyPluginInfo(mPackageName);
        if (info != null) {
            proxyActivity = info.loadDyActivityPlugin(mClass, this);
            dyContext = info.getContext();
        }
        attach(proxyActivity, dyContext);
        super.onCreate(savedInstanceState);
        if (mRemoteActivity != null) {
            mRemoteActivity.onCreate(savedInstanceState);
        }
    }

    @Override
    public AssetManager getAssets() {
//        if (mDyContext != null) {
//            return mDyContext.getAssets();
//        }
        return super.getAssets();
    }

    @Override
    public Resources getResources() {
//        if (mDyContext != null) {
//            return mDyContext.getResources();
//        }
        return super.getResources();
    }

    @Override
    public Resources.Theme getTheme() {
//        if (mDyContext != null) {
//            return mDyContext.getTheme();
//        }
        return super.getTheme();
    }

    @Override
    public ClassLoader getClassLoader() {
        if (mDyContext != null) {
            return mDyContext.getClassLoader();
        }
        return super.getClassLoader();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mRemoteActivity != null) {
            mRemoteActivity.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        if (mRemoteActivity != null) {
            mRemoteActivity.onStart();
        }
        super.onStart();
    }

    @Override
    protected void onRestart() {
        if (mRemoteActivity != null) {
            mRemoteActivity.onRestart();
        }
        super.onRestart();
    }

    @Override
    protected void onResume() {
        if (mRemoteActivity != null) {
            mRemoteActivity.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mRemoteActivity != null) {
            mRemoteActivity.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mRemoteActivity != null) {
            mRemoteActivity.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mRemoteActivity != null) {
            mRemoteActivity.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mRemoteActivity != null) {
            mRemoteActivity.onSaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (mRemoteActivity != null) {
            mRemoteActivity.onRestoreInstanceState(savedInstanceState);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mRemoteActivity != null) {
            mRemoteActivity.onNewIntent(intent);
        }
        super.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        if (mRemoteActivity != null) {
            mRemoteActivity.onBackPressed();
        }
//        super.onBackPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean b = super.onTouchEvent(event);
        if (mRemoteActivity != null) {
            return mRemoteActivity.onTouchEvent(event);
        }
        return b;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean b = super.onKeyUp(keyCode, event);
        if (mRemoteActivity != null) {
            return mRemoteActivity.onKeyUp(keyCode, event);
        }
        return b;
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        if (mRemoteActivity != null) {
            mRemoteActivity.onWindowAttributesChanged(params);
        }
        super.onWindowAttributesChanged(params);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (mRemoteActivity != null) {
            mRemoteActivity.onWindowFocusChanged(hasFocus);
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mRemoteActivity != null) {
            mRemoteActivity.onCreateOptionsMenu(menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mRemoteActivity != null) {
            mRemoteActivity.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }

    @Override
    public void finish() {
        if (mRemoteActivity != null) {
            mRemoteActivity.finish();
        }
        super.finish();
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return mDyContext != null ? LayoutInflater.from(mDyContext) : super.getLayoutInflater();
    }
}
