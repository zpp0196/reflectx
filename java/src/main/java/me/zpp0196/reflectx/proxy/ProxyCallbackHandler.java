package me.zpp0196.reflectx.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import me.zpp0196.reflectx.util.ReflectUtils;

/**
 * @author zpp0196
 */
class ProxyCallbackHandler implements InvocationHandler {

    @Nonnull
    private Object mProxyImpl;

    ProxyCallbackHandler(@Nonnull Object impl) {
        mProxyImpl = impl;
    }

    @Override
    public Object invoke(Object stub, Method stubMethod, @Nullable Object[] args) throws Throwable {
        Method[] methods = mProxyImpl.getClass().getMethods();
        for (Method proxyMethod : methods) {
            String methodName = ProxyClass.getSourceName(proxyMethod);
            if (!stubMethod.getName().equals(methodName)) {
                continue;
            }
            ProxyWrapper wrapper = new ProxyWrapper(stubMethod, proxyMethod, args);
            if (wrapper.wrapper()) {
                return ReflectUtils.get().accessible(proxyMethod).invoke(mProxyImpl, args);
            }
        }
        return stubMethod.invoke(mProxyImpl, args);
    }
}
