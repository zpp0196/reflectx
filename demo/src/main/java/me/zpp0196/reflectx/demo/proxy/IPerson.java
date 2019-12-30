package me.zpp0196.reflectx.demo.proxy;

import java.util.List;

import me.zpp0196.reflectx.proxy.IProxyClass;
import me.zpp0196.reflectx.proxy.MethodParameters;
import me.zpp0196.reflectx.proxy.RunWithCatch;
import me.zpp0196.reflectx.proxy.Source;

/**
 * @author zpp0196
 */
@Source("me.zpp0196.reflectx.demo.original.Person")
public interface IPerson extends IProxyClass {
    void feed(IAnimal animal, IFood food, IFood.EatingListener listener);

    @RunWithCatch
    IPerson addPet(IAnimal animal);

    @Source("addPet")
    @MethodParameters(IAnimal.class)
    void addBirdPet(IBird bird);

//    List<IAnimal> getPets2(); // 不支持
    @RunWithCatch
    List getPets();
}
