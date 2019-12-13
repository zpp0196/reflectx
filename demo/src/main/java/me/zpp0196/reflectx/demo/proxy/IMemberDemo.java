package me.zpp0196.reflectx.demo.proxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import me.zpp0196.reflectx.proxy.ConstructorGetter;
import me.zpp0196.reflectx.proxy.FieldGetter;
import me.zpp0196.reflectx.proxy.MethodGetter;
import me.zpp0196.reflectx.proxy.ProxyFactory;
import me.zpp0196.reflectx.proxy.Source;

@Source("me.zpp0196.reflectx.demo.original.MemberDemo")
public interface IMemberDemo {

    static IMemberDemo proxy() {
        return ProxyFactory.proxyClass(IMemberDemo.class);
    }

    @ConstructorGetter
    Constructor ctor_def();

    @ConstructorGetter(int.class)
    Constructor ctor_withI();

    @FieldGetter(type = String.class, value = "TAG")
    Field field_TAG();

    @FieldGetter(value = "tag")
    Field field_tag();

    @MethodGetter(value = "fun")
    Method method_fun();

    @MethodGetter(returnType = int.class, value = "add", parameterTypes = {int.class, int.class})
    Method method_add();
}
