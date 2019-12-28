package proxy.android.app;

import android.app.Application;

import androidx.annotation.Nullable;

import me.zpp0196.reflectx.proxy.IProxyClass;
import me.zpp0196.reflectx.proxy.ProxyFactory;
import me.zpp0196.reflectx.proxy.Source;

/**
 * @author zpp0196
 */
@Source("android.app.ActivityThread")
public interface IActivityThread extends IProxyClass {

    IActivityThread PROXY = ProxyFactory.proxyClass(IActivityThread.class);

    static IActivityThread proxy() {
        return PROXY;
    }

    IActivityThread currentActivityThread();

    @Nullable
    String currentPackageName();

    @Nullable
    String currentProcessName();

    @Nullable
    Application currentApplication();
}