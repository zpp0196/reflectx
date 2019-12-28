package proxy.android.app;

import me.zpp0196.reflectx.proxy.IProxyClass;
import me.zpp0196.reflectx.proxy.ProxyGetter;
import me.zpp0196.reflectx.proxy.Source;
import proxy.android.util.ISingleton;

@Source("android.app.ActivityManagerNative")
public interface IActivityManagerNative extends IProxyClass {
    @ProxyGetter
    ISingleton gDefault();
}
