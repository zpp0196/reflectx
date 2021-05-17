package reflectx;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import reflectx.utils.IReflectUtils;
import reflectx.utils.ProxyWrapper;

/**
 * @author zpp0196
 */
class ProxyCallbackHandler implements InvocationHandler {

    @Nonnull
    private final Object mProxyImpl;

    ProxyCallbackHandler(@Nonnull Object impl) {
        mProxyImpl = impl;
    }

    @Override
    public Object invoke(Object stub, Method stubMethod, @Nullable Object[] args) throws Throwable {
        Method[] methods = mProxyImpl.getClass().getInterfaces()[0].getMethods();
        for (Method proxyMethod : methods) {
            String methodName = Reflectx.getSourceName(proxyMethod);
            if (!stubMethod.getName().equals(methodName)) {
                continue;
            }
            if (ProxyWrapper.wrapper(stubMethod.getParameterTypes(),
                    proxyMethod.getParameterTypes(), args)) {
                Method implMethod = IReflectUtils.get().findMethodExact(mProxyImpl.getClass(),
                        proxyMethod.getReturnType(), proxyMethod.getName(),
                        proxyMethod.getParameterTypes());
                return IReflectUtils.get().accessible(implMethod).invoke(mProxyImpl, args);
            }
        }
        return stubMethod.invoke(mProxyImpl, args);
    }
}
