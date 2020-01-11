package reflectx;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import reflectx.annotations.Initialized;
import reflectx.annotations.Source;
import reflectx.annotations.SourceClass;
import reflectx.annotations.Sources;
import reflectx.mapping.IProguardMapping;
import reflectx.mapping.IProxyClassMapping;

/**
 * @author zpp0196
 */
@SuppressWarnings("WeakerAccess")
public class Reflectx {

    private static ClassLoader sProxyClassLoader = Reflectx.class.getClassLoader();
    private static IProxyClassMapping sProxyClassMapping;
    private static IProguardMapping sProguardMapping;
    private static long sCurrentVersion = -1;
    private static Map<Class<? extends IProxy>, Class<?>> sProxyClassMappingMap = new HashMap<>();

    /**
     * If you need to support multiple versions, use {@link Sources}
     * or set up the {@link IProguardMapping} implementation class here.
     *
     * @param proguardMapping Global proguard mapping.
     */
    public static void setProguardMapping(IProguardMapping proguardMapping) {
        sProguardMapping = proguardMapping;
    }

    /**
     * @param version Current proguard version.
     */
    public static void setProguardVersion(long version) {
        sCurrentVersion = version;
    }

    public static long getProguardVersion() {
        return sCurrentVersion;
    }

    /**
     * Set the default proxied class's classLoader.
     *
     * @param classLoader Global default classloader.
     * @see #getProxyClassLoader()
     */
    public static void setProxyClassLoader(ClassLoader classLoader) {
        sProxyClassLoader = classLoader;
    }

    /**
     * Set the {@link IProxyClassMapping} implementation class.
     *
     * @param proxyClassMapping Implementation class.
     */
    public static void setProxyClassMapping(IProxyClassMapping proxyClassMapping) {
        sProxyClassMapping = proxyClassMapping;
    }

    /**
     * Find a class with the global proxy interface classloader.
     *
     * @param className The class name.
     * @return A reference to the class.
     * @throws ReflectxException The class cannot be found.
     */
    @Nonnull
    public static Class<?> findClass(@Nonnull String className) throws ReflectxException {
        try {
            return getProxyClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new ReflectxException(e);
        }
    }

    /**
     * Find class if it exists.
     *
     * @param className The class name.
     * @return A reference to the class, or {@code null} if it doesn't exist.
     */
    @Nullable
    public static Class<?> findClassIfExists(@Nonnull String className) {
        try {
            return findClass(className);
        } catch (ReflectxException e) {
            return null;
        }
    }

    @Nonnull
    public static ClassLoader getProxyClassLoader() {
        if (sProxyClassLoader == null) {
            sProxyClassLoader = Thread.currentThread().getContextClassLoader();
        }
        if (sProxyClassLoader == null) {
            sProxyClassLoader = Reflectx.class.getClassLoader();
        }
        return sProxyClassLoader;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static Class<? extends IProxy> getProxyClass(Class<?> clazz) {
        return isProxyClass(clazz) ? (Class<? extends IProxy>) clazz : null;
    }

    /**
     * Gets the type array of the proxy parameters.
     *
     * @param args The proxy interface method's arguments.
     * @return The original parameter types.
     */
    public static Class<?>[] getProxyTypes(@Nullable Object... args) {
        Class<?>[] result = new Class<?>[0];
        if (args == null) {
            return result;
        }
        List<Class<?>> classes = new ArrayList<>();
        for (Object proxyArg : args) {
            if (proxyArg instanceof IProxy) {
                classes.add(((IProxy) proxyArg).requireSourceClass());
            } else {
                classes.add(proxyArg.getClass());
            }
        }
        return classes.toArray(result);
    }

    /**
     * If you have a way to get the original class at runtime, you can save it here.
     *
     * @param proxy The proxy interface.
     * @param clazz The original interface.
     * @see #getSourceClass(Class, boolean, ClassLoader)
     */
    public static void putSourceClass(Class<? extends IProxy> proxy, Class<?> clazz) {
        sProxyClassMappingMap.put(proxy, clazz);
    }

    /**
     * @param proxy The proxy interface.
     * @return The class being proxied.
     */
    @Nonnull
    public static Class<?> getSourceClass(@Nonnull Class<? extends IProxy> proxy) {
        return getSourceClass(proxy, getProxyClassLoader());
    }

    /**
     * @param proxy  The proxy interface.
     * @param loader The proxied class's classloader.
     * @return The class being proxied.
     */
    @Nonnull
    public static Class<?> getSourceClass(@Nonnull Class<? extends IProxy> proxy,
            @Nonnull ClassLoader loader) {
        return getSourceClass(proxy, proxy.isAnnotationPresent(Initialized.class), loader);
    }

    /**
     * Gets the specified proxy interface's source class
     *
     * @param proxy      The proxy interface.
     * @param initialize Whether to initialize the proxied class.
     * @param loader     The proxied class's classloader.
     * @return The class being proxied.
     * @throws ReflectxException If the class doesn't exist.
     * @see Class#forName(String, boolean, ClassLoader)
     */
    @Nonnull
    public static Class<?> getSourceClass(@Nonnull Class<? extends IProxy> proxy,
            boolean initialize, @Nonnull ClassLoader loader) {
        if (proxy.isAnnotationPresent(SourceClass.class)) {
            return proxy.getAnnotation(SourceClass.class).value();
        }
        if (sProxyClassMappingMap.containsKey(proxy)) {
            return sProxyClassMappingMap.get(proxy);
        }
        String className = getSourceName(proxy);
        try {
            return Class.forName(className, initialize, loader);
        } catch (ClassNotFoundException e) {
            throw new ReflectxException(e);
        }
    }

    @Nullable
    public static String getSourceName(@Nonnull Method method) {
        return getSourceName0(method);
    }

    @Nullable
    public static String getSourceName(@Nonnull Class<? extends IProxy> clazz) {
        return getSourceName0(clazz);
    }

    @Nullable
    private static String getSourceName0(@Nonnull AnnotatedElement element) {
        Sources sources = element.getAnnotation(Sources.class);
        Source source = null;
        if (sources != null) {
            long latestVersion = -1;
            for (Source s : sources.value()) {
                if (s.version() >= latestVersion) {
                    latestVersion = s.version();
                    source = s;
                }
                if (s.version() == sCurrentVersion) {
                    return s.value();
                }
            }
        }
        if (source == null) {
            source = element.getAnnotation(Source.class);
        }
        if (source == null) {
            return null;
        }
        if (sProguardMapping == null) {
            return source.value();
        }
        return sProguardMapping.getSourceName(element, source.value(), source.version());
    }

    /**
     * @param clazz Class to judge.
     * @return Judge result.
     */
    public static boolean isProxyClass(@Nonnull Class<?> clazz) {
        return IProxy.class.isAssignableFrom(clazz);
    }

    @Nonnull
    static Class<? extends BaseProxyClass> getProxyImpl(@Nonnull Class<? extends IProxy> proxy) {
        if (sProxyClassMapping == null) {
            throw new IllegalArgumentException("no proxy class mapping was found");
        }
        Class<? extends BaseProxyClass> original = sProxyClassMapping.get(proxy);
        if (original != null) {
            return original;
        }
        throw new IllegalArgumentException(proxy + "$Proxy was not found");
    }
}