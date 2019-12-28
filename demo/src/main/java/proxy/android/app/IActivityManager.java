package proxy.android.app;

import android.app.ActivityManager;

import me.zpp0196.reflectx.proxy.IProxyClass;
import me.zpp0196.reflectx.proxy.ProxyGetter;
import me.zpp0196.reflectx.proxy.SourceClass;
import proxy.android.util.ISingleton;

@SourceClass(ActivityManager.class)
public interface IActivityManager extends IProxyClass {
    @ProxyGetter
    ISingleton IActivityManagerSingleton();
}
