package reflectx.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import reflectx.IProxy;
import reflectx.IProxyCallback;
import reflectx.IProxyClass;
import reflectx.ProxyFactory;
import reflectx.Reflectx;

/**
 * @author zpp0196
 */
public class ProxyWrapper {

    @Nonnull
    private Class<?>[] originalTypes;
    @Nonnull
    private Class<?>[] proxyTypes;
    @Nonnull
    private Object[] args;

    /**
     * @param originalTypes The original types.
     * @param proxyTypes    The proxy types.
     * @param args          The arguments.
     */
    private ProxyWrapper(@Nullable Class<?>[] originalTypes, @Nullable Class<?>[] proxyTypes,
            @Nullable Object[] args) {
        if (originalTypes == null) {
            originalTypes = new Class[0];
        }
        if (proxyTypes == null) {
            proxyTypes = new Class[0];
        }
        if (args == null) {
            args = new Object[0];
        }
        this.originalTypes = originalTypes;
        this.proxyTypes = proxyTypes;
        this.args = args;
    }

    /**
     * Wrap the original parameters as proxy parameters.
     *
     * @return Whether the parameter types match.
     */
    public static boolean wrapper(@Nullable Class<?>[] originalTypes,
            @Nullable Class<?>[] proxyTypes, @Nullable Object[] originalArgs) {
        return new ProxyWrapper(originalTypes, proxyTypes, originalArgs)
                .convert((src, proxy, arg) -> {
                    Class<? extends IProxy> proxyClass = Reflectx.getProxyClass(proxy);
                    return proxyClass == null ? arg : ProxyFactory.proxy(proxyClass, arg);
                });
    }

    /**
     * Unbox the proxy parameters.
     *
     * @return Whether the parameter types match.
     */
    public static boolean unwrap(@Nullable Class<?>[] original, @Nullable Object[] proxyArgs) {
        return new ProxyWrapper(original, Reflectx.getProxyTypes(proxyArgs), proxyArgs)
                .convert((src, proxy, arg) -> {
                    if (arg instanceof IProxyCallback) {
                        return ((IProxyCallback) arg).proxy();
                    }
                    if (arg instanceof IProxyClass) {
                        return ((IProxyClass) arg).get();
                    }
                    return arg;
                });
    }

    private interface IConverter {
        Object convert(Class<?> src, Class<?> proxy, Object arg);
    }

    private boolean convert(IConverter converter) {
        if (originalTypes.length != proxyTypes.length || proxyTypes.length != args.length) {
            return false;
        }
        for (int i = 0; i < originalTypes.length; i++) {
            Class<?> srcType = originalTypes[i];
            Class<?> proxyType = proxyTypes[i];
            Object arg = args[i];

            if (!srcType.isAssignableFrom(proxyType) || srcType.isPrimitive()) {
                return false;
            }
            args[i] = converter.convert(srcType, proxyType, arg);
        }
        return true;
    }
}
