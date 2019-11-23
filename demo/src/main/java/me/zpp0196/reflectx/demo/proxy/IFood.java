package me.zpp0196.reflectx.demo.proxy;

import me.zpp0196.reflectx.proxy.IProxyCallback;
import me.zpp0196.reflectx.proxy.ProxyGetter;
import me.zpp0196.reflectx.proxy.ProxySetter;
import me.zpp0196.reflectx.proxy.Source;

/**
 * @author zpp0196
 */
@Source("me.zpp0196.reflectx.demo.original.Food")
public interface IFood {
    @ProxyGetter
    String name();

    @ProxySetter
    IFood name(String name);

    @Source("me.zpp0196.reflectx.demo.original.Food$EatingListener")
    interface EatingListener extends IProxyCallback {
        void onFinishedEating(String name);
    }
}
