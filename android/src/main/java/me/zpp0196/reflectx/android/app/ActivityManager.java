package me.zpp0196.reflectx.android.app;

import me.zpp0196.reflectx.android.util.Singleton;
import me.zpp0196.reflectx.proxy.IProxyClass;
import me.zpp0196.reflectx.proxy.ProxyGetter;
import me.zpp0196.reflectx.proxy.Source;

@Source("android.app.ActivityManager")
public interface ActivityManager extends IProxyClass {
    @ProxyGetter
    Singleton IActivityManagerSingleton();
}
