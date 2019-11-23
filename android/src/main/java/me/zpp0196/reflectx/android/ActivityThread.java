package me.zpp0196.reflectx.android;

import android.app.Application;

import androidx.annotation.Nullable;

import me.zpp0196.reflectx.proxy.IProxyClass;
import me.zpp0196.reflectx.proxy.Source;

/**
 * @author zpp0196
 */
@Source("android.app.ActivityThread")
public interface ActivityThread extends IProxyClass {

    ActivityThread currentActivityThread();

    @Nullable
    String currentPackageName();

    @Nullable
    String currentProcessName();

    @Nullable
    Application currentApplication();
}