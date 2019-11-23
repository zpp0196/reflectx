package me.zpp0196.reflectx.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author zpp0196
 */
public class ProxyWrapper {

    @Nullable
    private Class<?>[] originalTypes;
    @Nullable
    private Class<?>[] proxyTypes;
    @Nullable
    private Object[] args;

    /**
     * @param original  被代理的方法参数类型
     * @param proxyArgs 代理的方法参数
     */
    public ProxyWrapper(@Nullable Class<?>[] original, @Nullable Object[] proxyArgs) {
        this(original, null, proxyArgs);
    }

    /**
     * @param original 被代理方法
     * @param proxy    代理方法
     * @param args     方法参数
     */
    public ProxyWrapper(@Nonnull Method original, @Nonnull Method proxy, @Nullable Object[] args) {
        this(original.getParameterTypes(), proxy.getParameterTypes(), args);
    }

    /**
     * @param originalTypes 被代理的方法参数类型
     * @param proxyTypes    代理的方法参数类型
     * @param args          方法参数，可以是被代理的方法参数也可是代理的方法参数
     */
    public ProxyWrapper(@Nullable Class<?>[] originalTypes, @Nullable Class<?>[] proxyTypes,
            @Nullable Object[] args) {
        this.originalTypes = originalTypes;
        this.proxyTypes = proxyTypes;
        this.args = args;
    }

    /**
     * 将原来的参数包装成代理参数
     *
     * @return 法参数类型是否匹配
     */
    public boolean wrapper() {
        return convert((src, proxy, arg) -> {
            if (proxy.isInterface() && IProxyClass.class.isAssignableFrom(proxy)) {
                return ProxyFactory.proxyObject(proxy, arg);
            }
            return arg;
        });
    }

    /**
     * 将代理参数拆箱
     *
     * @return 参数类型是否匹配
     */
    public boolean unwrap() {
        return convert((src, proxy, arg) -> {
            if (arg instanceof IProxyCallback) {
                return ((IProxyCallback) arg).proxy();
            }
            if (arg instanceof IProxyClass) {
                return ((IProxyClass) arg).get();
            }
            return arg;
        });
    }

    private interface Converter {
        Object convert(Class<?> src, Class<?> proxy, Object arg);
    }

    private boolean convert(Converter converter) {
        if (proxyTypes == null) {
            proxyTypes = getProxyTypes(args);
        }
        if (originalTypes == null) {
            originalTypes = new Class[0];
        }
        if (args == null) {
            args = new Object[0];
        }
        if (originalTypes.length != proxyTypes.length || proxyTypes.length != args.length) {
            return false;
        }
        for (int i = 0; i < originalTypes.length; i++) {
            Class<?> srcType = originalTypes[i];
            Class<?> proxyType = proxyTypes[i];
            Object arg = args[i];

            if (proxyType != srcType || srcType.isPrimitive()) {
                return false;
            }
            args[i] = converter.convert(srcType, proxyType, arg);
        }
        return true;
    }

    /**
     * 获取代理参数的类型数组
     *
     * @param args 代理参数
     * @return 参数类型数组
     */
    public static Class<?>[] getProxyTypes(@Nullable Object... args) {
        Class<?>[] result = new Class<?>[0];
        if (args == null) {
            return result;
        }
        List<Class<?>> classes = new ArrayList<>();
        for (Object proxyArg : args) {
            if (proxyArg instanceof IProxy) {
                classes.add(((IProxy) proxyArg).getSourceClass());
            } else {
                classes.add(proxyArg.getClass());
            }
        }
        return classes.toArray(result);
    }
}
