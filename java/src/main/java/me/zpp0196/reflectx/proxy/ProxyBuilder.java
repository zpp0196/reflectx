package me.zpp0196.reflectx.proxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import me.zpp0196.reflectx.util.Reflect;
import me.zpp0196.reflectx.util.ReflectException;

/**
 * @author zpp0196
 * @see ProxyFactory
 */
public class ProxyBuilder {

    @Nonnull
    private Class<?> proxy;
    @Nullable
    private Object original;
    @Nonnull
    private ClassLoader loader;
    private boolean initialize;

    private ProxyBuilder(@Nonnull Class<?> proxy) {
        this.proxy = proxy;
        this.loader = ProxyClass.getClassLoader();
    }

    /**
     * 代理 Class 或 创建代理对象
     *
     * @param proxy 代理接口
     * @return {@link ProxyBuilder}
     */
    public static ProxyBuilder proxy(@Nonnull Class<?> proxy) {
        return new ProxyBuilder(proxy);
    }

    /**
     * 代理接口实现类
     *
     * @param original 用于代理 Class 时，该参数是被代理 Class.class
     *                 <p>用于代理回调接口时，该参数是代理接口的实现类
     *                 <p>用于代理对象时，该类是被代理对象
     * @return this
     */
    public ProxyBuilder original(@Nonnull Object original) {
        this.original = original;
        return this;
    }

    /**
     * @param loader 被代理类的 ClassLoader
     * @return this
     */
    public ProxyBuilder loader(@Nonnull ClassLoader loader) {
        this.loader = loader;
        return this;
    }

    /**
     * @param initialize 是否初始化被代理类
     * @return this
     */
    public ProxyBuilder initialize(boolean initialize) {
        this.initialize = initialize;
        return this;
    }

    /**
     * 创建一个代理对象
     * <p>创建一个代理接口对应 Class 的实例并包装成代理对象
     *
     * @param <P>  代理接口类型
     * @param args 被代理对象创建时构造方法传入的参数
     * @return 代理对象
     * @see java.lang.reflect.Constructor#newInstance(Object...)
     * @see #proxy(Class)
     */
    public <P> P instance(Object... args) {
        Object instance = null;
        try {
            Class<?> sourceClass = ProxyClass.findClass(proxy, initialize, loader);
            for (Constructor<?> constructor : sourceClass.getDeclaredConstructors()) {
                ProxyWrapper wrapper = new ProxyWrapper(constructor.getParameterTypes(), args);
                if (wrapper.unwrap()) {
                    constructor.setAccessible(true);
                    instance = constructor.newInstance(args);
                }
            }
            if (instance == null) {
                throw new IllegalArgumentException("failed to create " + proxy);
            }
        } catch (Exception e) {
            throw new ReflectException(e);
        }
        return original(instance).proxy0();
    }

    public <P> P object() {
        Class<?> originalClass = getOriginal().getClass();
        if (originalClass.isPrimitive()) {
            throw new ReflectException("proxy class cannot be primitive");
        }
        return proxy0();
    }

    /**
     * 代理 Class 或 Object
     *
     * @param <P> 代理接口类型
     * @return 代理接口
     */
    public <P> P clazz() {
        original(ProxyClass.findClass(proxy, initialize, loader));
        return proxy0();
    }

    /**
     * 代理回调接口
     *
     * @return 被代理接口的代理实现
     */
    public Object callback() {
        try {
            Class<?> sourceInterface = ProxyClass.findClass(proxy, initialize, loader);
            ProxyCallbackHandler handler = new ProxyCallbackHandler(getOriginal());
            ClassLoader classLoader = sourceInterface.getClassLoader();
            return Proxy.newProxyInstance(classLoader, new Class[]{sourceInterface}, handler);
        } catch (Throwable th) {
            throw new ReflectException(th);
        }
    }

    @SuppressWarnings("unchecked")
    private <P> P proxy0() {
        Object original = getOriginal();
        Class<?> proxyClass = ProxyClass.getProxyImpl(proxy);
        return (P) Reflect.on(proxyClass).create(original, proxy).get();
    }

    private Object getOriginal() {
        if (original == null) {
            throw new ReflectException("please set the object being proxied");
        }
        return original;
    }
}
