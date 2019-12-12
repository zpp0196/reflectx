package me.zpp0196.reflectx.demo.proxy;

import androidx.annotation.NonNull;

import me.zpp0196.reflectx.proxy.BaseProxyClass;
import me.zpp0196.reflectx.proxy.ProxyClass;
import me.zpp0196.reflectx.proxy.ProxyClassImpl;
import me.zpp0196.reflectx.util.IReflectUtils;

/**
 * @author zpp0196
 */
@ProxyClassImpl
public class ProxyClassDemo extends BaseProxyClass {

    @NonNull
    @Override
    public IReflectUtils getReflectUtils() {
        return super.getReflectUtils();
    }

    @Override
    protected Class<?> getOriginalClass(Class<?> proxy) {
        return ProxyClass.findClass(proxy, mClass.getClassLoader());
    }
}
