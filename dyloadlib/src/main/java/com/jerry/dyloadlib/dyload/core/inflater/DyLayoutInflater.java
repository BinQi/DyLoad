package com.jerry.dyloadlib.dyload.core.inflater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jerry.dyloadlib.dyload.util.log.Logger;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author wubinqi
 */
public class DyLayoutInflater extends LayoutInflater {
    static final String TAG = "DyLayoutInflater";
    private LayoutInflater mParent;
    private LayoutInflater mNow;

    public DyLayoutInflater(LayoutInflater original, LayoutInflater now, Context newContext) {
        super(now, newContext);
        mParent = original;
        mNow = now;
    }

    @Override
    public View inflate(int resource, ViewGroup root) {
        try {
            return mNow.inflate(resource, root);
        } catch (Exception e) {
            Logger.w(TAG, "插件找不到该resouce:" + resource);
            return mParent.inflate(resource, root);
        }
    }

    @Override
    public View inflate(XmlPullParser parser, ViewGroup root) {
        try {
            return mNow.inflate(parser, root);
        } catch (Exception e) {
            return mParent.inflate(parser, root);
        }
    }

    @Override
    public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        try {
            return mNow.inflate(resource, root, attachToRoot);
        } catch (Exception e) {
            Logger.w(TAG, "插件找不到该resouce:" + resource);
            return mParent.inflate(resource, root, attachToRoot);
        }
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return mNow.cloneInContext(newContext);
    }

    public static DyLayoutInflater create(LayoutInflater object, Context context) {
        // 注意不同android版本兼容
        LayoutInflater layoutInflater = object.cloneInContext(context);
        InflaterFactory factory = new InflaterFactory(layoutInflater.getFactory(), context.getClassLoader());
        layoutInflater.setFactory(factory);
        // 解决toast找不到
        return new DyLayoutInflater((LayoutInflater) object, layoutInflater, context);
    }
}
