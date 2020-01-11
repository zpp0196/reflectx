package reflectx;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import reflectx.annotations.Initialized;
import reflectx.utils.IReflectUtils;
import reflectx.utils.ProxyWrapper;

/**
 * @author zpp0196
 * @see ProxyFactory
 */
@SuppressWarnings("WeakerAccess")
public class ProxyBuilder {

    @Nonnull
    private Class<? extends IProxy> proxy;
    @Nullable
    private Object original;
    @Nonnull
    private ClassLoader loader;
    private boolean initialize;

    private ProxyBuilder(@Nonnull Class<? extends IProxy> proxy) {
        this.proxy = proxy;
        this.loader = Reflectx.getProxyClassLoader();
    }

    /**
     * @param proxy The proxy interface.
     * @return {@link ProxyBuilder} instance.
     */
    public static ProxyBuilder proxy(@Nonnull Class<? extends IProxy> proxy) {
        return new ProxyBuilder(proxy);
    }

    /**
     * @param original The object being proxied.
     * @return {@code this}
     */
    public ProxyBuilder original(@Nonnull Object original) {
        this.original = original;
        return this;
    }

    /**
     * @param loader Proxy class's classloader.
     * @return {@code this}
     * @see Reflectx#setProxyClassLoader(ClassLoader)
     */
    public ProxyBuilder loader(@Nonnull ClassLoader loader) {
        this.loader = loader;
        return this;
    }

    /**
     * @param initialize Whether to initialize the proxied class.
     * @return {@code this}
     * @see Initialized
     */
    public ProxyBuilder initialize(boolean initialize) {
        this.initialize = initialize;
        return this;
    }

    /**
     * Create an instance of the original Class and wrap it as a proxy interface.
     *
     * @param <P>  The proxy interface's type.
     * @param args The arguments passed in by the constructor when the proxy object is created.
     * @return Proxy interface with original instance.
     * @see Constructor#newInstance(Object...)
     */
    public <P> P instance(Object... args) {
        Object instance = null;
        Class<?> sourceClass = Reflectx.getSourceClass(proxy, initialize, loader);
        for (Constructor<?> constructor : sourceClass.getDeclaredConstructors()) {
            if (ProxyWrapper.unwrap(constructor.getParameterTypes(), args)) {
                try {
                    instance = IReflectUtils.get().accessible(constructor).newInstance(args);
                    break;
                } catch (Exception ignored) {
                }
            }
        }
        if (instance == null) {
            throw new IllegalArgumentException("failed to create " + proxy);
        }
        return original(instance).proxy0();
    }

    /**
     * @param <P> The proxy interface's type.
     * @return Proxy interface instance with {@link Object}
     */
    public <P> P object() {
        Class<?> originalClass = getOriginal().getClass();
        if (originalClass.isPrimitive()) {
            throw new ReflectxException("proxy class cannot be primitive");
        }
        return proxy0();
    }

    /**
     * @param <P> The proxy interface's type.
     * @return Proxy interface instance with {@link Class}
     */
    public <P> P clazz() {
        original(Reflectx.getSourceClass(proxy, initialize, loader));
        return proxy0();
    }

    /**
     * @return Proxy callback interface instance.
     * @see Proxy#newProxyInstance(ClassLoader, Class[], InvocationHandler)
     */
    public Object callback() {
        try {
            Class<?> sourceInterface = Reflectx.getSourceClass(proxy, initialize, loader);
            ProxyCallbackHandler handler = new ProxyCallbackHandler(getOriginal());
            ClassLoader classLoader = sourceInterface.getClassLoader();
            return Proxy.newProxyInstance(classLoader, new Class[]{sourceInterface}, handler);
        } catch (Throwable th) {
            throw new ReflectxException(th);
        }
    }

    @SuppressWarnings("unchecked")
    private <P> P proxy0() {
        Object original = getOriginal();
        Class<? extends BaseProxyClass> proxyClass = Reflectx.getProxyImpl(proxy);
        try {
            return (P) proxyClass.newInstance().init(original, proxy);
        } catch (Exception e) {
            throw new ReflectxException(e);
        }
    }

    private Object getOriginal() {
        if (original == null) {
            throw new ReflectxException("please set the object being proxied");
        }
        return original;
    }
}
