package reflectx;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author zpp0196
 * @see ProxyBuilder
 */
@SuppressWarnings("WeakerAccess")
public class ProxyFactory {
    /**
     * Create a proxy callback interface instance.
     *
     * @param proxy The proxy callback interface.
     * @param impl  The proxy interface's impl.
     * @return Proxy callback instance.
     */
    public static Object callback(@Nonnull Class<? extends IProxy> proxy, @Nonnull Object impl) {
        return ProxyBuilder.proxy(proxy).original(impl).callback();
    }

    /**
     * Create an instance of the original Class and wrap it as a proxy interface.
     *
     * @param <P>   The proxy interface's type.
     * @param proxy The proxy interface.
     * @param args  The arguments passed in by the constructor when the proxy object is created.
     * @return Proxy interface instance.
     */
    public static <P extends IProxy> P create(@Nonnull Class<P> proxy, @Nullable Object... args) {
        return ProxyBuilder.proxy(proxy).loader(Reflectx.getProxyClassLoader()).instance(args);
    }

    /**
     * Create a proxy interface's instance with original {@link Class}
     *
     * @param <P>   The proxy interface's type.
     * @param proxy The proxy interface.
     * @return Proxy interface instance.
     */
    public static <P extends IProxy> P proxy(@Nonnull Class<P> proxy) {
        return ProxyBuilder.proxy(proxy).loader(Reflectx.getProxyClassLoader()).clazz();
    }


    /**
     * Create a proxy interface's instance with original {@link Object}
     *
     * @param <P>      The proxy interface's type.
     * @param proxy    The proxy interface.
     * @param original The object being proxied.
     * @return Proxy interface instance.
     */
    public static <P extends IProxy> P proxy(@Nonnull Class<P> proxy, @Nonnull Object original) {
        return ProxyBuilder.proxy(proxy).original(original).object();
    }
}
