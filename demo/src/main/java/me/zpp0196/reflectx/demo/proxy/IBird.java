package me.zpp0196.reflectx.demo.proxy;

import me.zpp0196.reflectx.proxy.ProxyGetter;
import me.zpp0196.reflectx.proxy.ProxySetter;
import me.zpp0196.reflectx.proxy.Source;

/**
 * 默认继承 {@link IAnimal}，这时如果继承了别的接口 {@link IFlightableProxy}
 * 也需要在该接口中重写其他接口的方法 {@link IBird#fly()} )}
 *
 * @author zpp0196
 */
@Source("me.zpp0196.reflectx.demo.original.Bird")
public interface IBird extends IAnimal, IFlightableProxy {

    @Override
    @ProxyGetter("childName")
    String getName();

    @ProxyGetter("childName")
    String getChildName();

    @Override
    @ProxySetter("childName")
    void setName(String name);

    @Override
    void fly();

    @ProxyGetter
    IWing wing();
}
