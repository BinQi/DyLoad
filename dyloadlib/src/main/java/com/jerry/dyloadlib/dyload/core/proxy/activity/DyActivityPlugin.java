package com.jerry.dyloadlib.dyload.core.proxy.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager.LayoutParams;

import com.jerry.dyloadlib.dyload.DyManager;
import com.jerry.dyloadlib.dyload.core.DyIntent;

/**
 * wubinqi
 */
public abstract class DyActivityPlugin {

    protected DyActivityContext mThat;

    public DyActivityPlugin(DyActivityContext that) {
        mThat = that;
    }

    public abstract void onCreate(Bundle savedInstanceState);
    public abstract void onStart();
    public abstract void onRestart();
    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);
    public abstract void onResume();
    public abstract void onPause();
    public abstract void onStop();
    public abstract void onDestroy();
    public abstract void onSaveInstanceState(Bundle outState);
    public abstract void onNewIntent(Intent intent);
    public abstract void onRestoreInstanceState(Bundle savedInstanceState);
    public abstract boolean onTouchEvent(MotionEvent event);
    public abstract boolean onKeyUp(int keyCode, KeyEvent event);
    public abstract void onWindowAttributesChanged(LayoutParams params);
    public abstract void onWindowFocusChanged(boolean hasFocus);
    public abstract void onBackPressed();
    public abstract boolean onCreateOptionsMenu(Menu menu);
    public abstract boolean onOptionsItemSelected(MenuItem item);
    public abstract void finish();

    public Context getApplicationContext() {
        return mThat;
    }

    public Application getApplication() {
        return mThat.getActivity().getApplication();
    }

    public int startPluginActivity(DyIntent dyIntent) {
        return this.startPluginActivityForResult(dyIntent, -1);
    }

    public int startPluginActivityForResult(DyIntent dyIntent, int requestCode) {
        return DyManager.getInstance(mThat.getActivity()).startPluginActivityForResult(mThat.getActivity(), dyIntent, requestCode);
    }
}
