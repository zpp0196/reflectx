package me.zpp0196.reflectx.demo.proxy;

import me.zpp0196.reflectx.proxy.ProxyFactory;
import me.zpp0196.reflectx.proxy.ProxySetter;
import me.zpp0196.reflectx.proxy.Source;

/**
 * @author zpp0196
 */
@Source("android.os.Build")
public interface IBuild {

    static IBuild proxy() {
        return ProxyFactory.proxyClass(IBuild.class);
    }

    @ProxySetter
    IBuild setBrand(String BRAND);
}
