package me.zpp0196.reflectx.android.app;

import me.zpp0196.reflectx.android.util.Singleton;
import me.zpp0196.reflectx.proxy.IProxyClass;
import me.zpp0196.reflectx.proxy.ProxyGetter;
import me.zpp0196.reflectx.proxy.Source;

@Source("android.app.ActivityManagerNative")
public interface ActivityManagerNative extends IProxyClass {
    @ProxyGetter
    Singleton gDefault();
}
