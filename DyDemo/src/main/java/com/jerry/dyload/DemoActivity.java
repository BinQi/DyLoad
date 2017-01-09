package com.jerry.dyload;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;

import com.jerry.dyloadlib.dyload.pm.DyHelper;

/**
 * @author wubinqi
 */
public class DemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openPlugin(View v) {
        try {
            DyHelper.getInstance().invokePluginAPIMethod("com.jerry.plugindemo" ,"startMainActivity", null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
