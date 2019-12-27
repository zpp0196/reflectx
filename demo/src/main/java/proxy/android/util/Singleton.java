package proxy.android.util;

import me.zpp0196.reflectx.proxy.IProxyClass;
import me.zpp0196.reflectx.proxy.ProxyGetter;
import me.zpp0196.reflectx.proxy.ProxySetter;
import me.zpp0196.reflectx.proxy.Source;

@Source("android.util.Singleton")
public interface Singleton extends IProxyClass {
    @ProxyGetter("mInstance")
    Object getInstance();
    @ProxySetter
    void setInstance(Object mInstance);
}
