package me.zpp0196.reflectx.demo.proxy;

import me.zpp0196.reflectx.proxy.IProxyClass;
import me.zpp0196.reflectx.proxy.Source;

/**
 * @author zpp0196
 */
@Source("me.zpp0196.reflectx.demo.original.IFlightable")
public interface IFlightableProxy extends IProxyClass {
    void fly();
}
