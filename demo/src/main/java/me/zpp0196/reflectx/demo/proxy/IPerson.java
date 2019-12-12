package me.zpp0196.reflectx.demo.proxy;

import java.util.List;

import me.zpp0196.reflectx.proxy.IProxyClass;
import me.zpp0196.reflectx.proxy.MemberParameters;
import me.zpp0196.reflectx.proxy.Source;

/**
 * @author zpp0196
 */
@Source("me.zpp0196.reflectx.demo.original.Person")
public interface IPerson extends IProxyClass {
    void feed(IAnimal animal, IFood food, IFood.EatingListener listener);

    IPerson addPet(IAnimal animal);

    @Source("addPet")
    @MemberParameters(IAnimal.class)
    void addBirdPet(IBird bird);

//    List<IAnimal> getPets2(); // 不支持
    List getPets();
}
