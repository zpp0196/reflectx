package me.zpp0196.reflectx.demo.proxy;

import me.zpp0196.reflectx.proxy.IProxyClass;
import me.zpp0196.reflectx.proxy.ProxyGetter;
import me.zpp0196.reflectx.proxy.ProxySetter;
import me.zpp0196.reflectx.proxy.RunWithCatch;
import me.zpp0196.reflectx.proxy.Source;

/**
 * @author zpp0196
 */
@Source("me.zpp0196.reflectx.demo.original.Animal")
public interface IAnimal extends IProxyClass {
    @ProxySetter
    void setName(String name);

    @ProxyGetter("name")
    @RunWithCatch(stringValue = "sb")
    String getName();

    @ProxySetter
    @RunWithCatch
    IAnimal name(String name);

    @ProxyGetter
    String name();

    void eat(IFood food);

    void say(String msg);
}