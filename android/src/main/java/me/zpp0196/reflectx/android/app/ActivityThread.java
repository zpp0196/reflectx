package me.zpp0196.reflectx.android.app;

import android.app.Application;

import androidx.annotation.Nullable;

import me.zpp0196.reflectx.proxy.IProxyClass;
import me.zpp0196.reflectx.proxy.ProxyFactory;
import me.zpp0196.reflectx.proxy.Source;

/**
 * @author zpp0196
 */
@Source("android.app.ActivityThread")
public interface ActivityThread extends IProxyClass {

    ActivityThread PROXY = ProxyFactory.proxyClass(ActivityThread.class);

    static ActivityThread proxy() {
        return PROXY;
    }

    ActivityThread currentActivityThread();

    @Nullable
    String currentPackageName();

    @Nullable
    String currentProcessName();

    @Nullable
    Application currentApplication();
}