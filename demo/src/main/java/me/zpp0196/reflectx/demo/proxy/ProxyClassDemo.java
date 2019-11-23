package me.zpp0196.reflectx.demo.proxy;

import androidx.annotation.NonNull;

import me.zpp0196.reflectx.proxy.BaseProxyClass;
import me.zpp0196.reflectx.proxy.ProxyClassImpl;

/**
 * @author zpp0196
 */
@ProxyClassImpl
public class ProxyClassDemo extends BaseProxyClass {

    public ProxyClassDemo(@NonNull Object original, Class<?> clazz) {
        super(original, clazz);
    }

    @Override
    protected <T> T get(Class<?> type, String name) {
        return super.get(type, name);
    }

    @Override
    protected void set(Class<?> type, String name, Object value) {
        super.set(type, name, value);
    }

    @Override
    protected <T> T call(Class<?> returnType, String name, Object... args) {
        return super.call(returnType, name, args);
    }
}
