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
import reflectx.mapping.SourceMapping;

/**
 * @author zpp0196
 */
@SuppressWarnings("WeakerAccess")
public class Reflectx {

    private static ClassLoader sProxyClassLoader = Reflectx.class.getClassLoader();
    private static IProxyClassMapping sProxyClassMapping;
    private static IProguardMapping sProguardMapping;
    private static long sCurrentVersion = SourceMapping.DEFAULT_VERSION;
    private static Map<Class<? extends IProxy>, Class<?>> sSourceClassMap = new HashMap<>();

    /**
     * If you need to support multiple versions, use {@link Sources}
     * or set up the {@link IProguardMapping} implementation class here.
     *
     * @param proguardMapping Global proguard mapping.
     */
    public static void setProguardMapping(@Nonnull IProguardMapping proguardMapping) {
        sProguardMapping = proguardMapping;
    }

    public static IProguardMapping getProguardMapping() {
        return sProguardMapping;
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
    public static void setProxyClassLoader(@Nonnull ClassLoader classLoader) {
        sProxyClassLoader = classLoader;
    }

    /**
     * Set the {@link IProxyClassMapping} implementation class.
     *
     * @param proxyClassMapping Implementation class.
     */
    public static void setProxyClassMapping(@Nonnull IProxyClassMapping proxyClassMapping) {
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
        } catch (Throwable ignored) {
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
    public static Class<? extends IProxy> getProxyClass(@Nonnull Class<?> clazz) {
        return isProxyClass(clazz) ? (Class<? extends IProxy>) clazz : null;
    }

    /**
     * Gets the type array of the proxy parameters.
     *
     * @param args The proxy interface method's arguments.
     * @return The original parameter types.
     */
    @Nonnull
    public static Class<?>[] getProxyTypes(@Nullable Object... args) {
        Class<?>[] result = new Class<?>[0];
        if (args == null) {
            return result;
        }
        List<Class<?>> classes = new ArrayList<>();
        for (Object proxyArg : args) {
            if (proxyArg == null) {
                classes.add(Object.class);
            } else if (proxyArg instanceof IProxy) {
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
    public static void putSourceClass(@Nonnull Class<? extends IProxy> proxy,
            @Nonnull Class<?> clazz) {
        sSourceClassMap.put(proxy, clazz);
    }

    public static void removeSourceClass(@Nonnull Class<? extends IProxy> proxy) {
        sSourceClassMap.remove(proxy);
    }

    /**
     * @param proxy The proxy interface.
     * @return The class being proxied.
     */
    @Nonnull
    public static Class<?> getSourceClass(@Nonnull Class<? extends IProxy> proxy)
            throws ReflectxException {
        return getSourceClass(proxy, getProxyClassLoader());
    }

    @Nullable
    public static Class<?> getSourceClassIfExists(@Nonnull Class<? extends IProxy> proxy) {
        try {
            return getSourceClass(proxy);
        } catch (Throwable ignored) {
            return null;
        }
    }

    /**
     * @param proxy  The proxy interface.
     * @param loader The proxied class's classloader.
     * @return The class being proxied.
     */
    @Nonnull
    public static Class<?> getSourceClass(@Nonnull Class<? extends IProxy> proxy,
            @Nonnull ClassLoader loader) throws ReflectxException {
        return getSourceClass(proxy, proxy.isAnnotationPresent(Initialized.class), loader);
    }

    @Nullable
    public static Class<?> getSourceClassIfExists(@Nonnull Class<? extends IProxy> proxy,
            @Nonnull ClassLoader loader) {
        try {
            return getSourceClass(proxy, loader);
        } catch (Throwable ignored) {
            return null;
        }
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
            boolean initialize, @Nonnull ClassLoader loader) throws ReflectxException {
        if (proxy.isAnnotationPresent(SourceClass.class)) {
            return proxy.getAnnotation(SourceClass.class).value();
        }
        if (sSourceClassMap.containsKey(proxy)) {
            Class<?> clazz = sSourceClassMap.get(proxy);
            if (clazz != null && clazz.getClassLoader() == loader) {
                return clazz;
            }
        }
        String className = getSourceName(proxy);
        try {
            Class<?> sourceClass = Class.forName(className, initialize, loader);
            sSourceClassMap.put(proxy, sourceClass);
            return sourceClass;
        } catch (ClassNotFoundException e) {
            throw new ReflectxException(e);
        }
    }

    @Nullable
    public static Class<?> getSourceClassIfExists(@Nonnull Class<? extends IProxy> proxy,
            boolean initialize, @Nonnull ClassLoader loader) {
        try {
            return getSourceClass(proxy, initialize, loader);
        } catch (Throwable ignored) {
            return null;
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
        String sourceName = getSourceNameAtRuntime(element);
        if (sourceName != null) {
            return sourceName;
        }

        if (element instanceof Method) {
            return ((Method) element).getName();
        }
        if (!(element instanceof Class<?>)) {
            return null;
        }
        Class<? extends IProxy> proxyClass = getProxyClass((Class<?>) element);
        if (proxyClass == null) {
            return null;
        }
        IProxyClassMapping.Item item = getProxyClassMapping().get(proxyClass);
        if (item == null) {
            return null;
        }
        Map<Long, String> nameMapping = item.nameMapping;
        if (nameMapping == null) {
            return null;
        }
        if (nameMapping.containsKey(sCurrentVersion)) {
            return nameMapping.get(sCurrentVersion);
        }
        long latestVersion = SourceMapping.DEFAULT_VERSION;
        for (Long version : nameMapping.keySet()) {
            if (version >= latestVersion) {
                latestVersion = version;
            }
        }
        return nameMapping.get(latestVersion);
    }

    @Nullable
    private static String getSourceNameAtRuntime(@Nonnull AnnotatedElement element) {
        Sources sources = element.getAnnotation(Sources.class);
        Source source = null;
        if (sources != null) {
            long latestVersion = SourceMapping.DEFAULT_VERSION;
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
        if (source != null) {
            if (sProguardMapping == null) {
                return source.value();
            }
            String name = sProguardMapping.getSourceName(element, source.value());
            if (name != null) {
                return name;
            }
            return sProguardMapping.getSourceName(source.identifies(), source.value());
        }
        return null;
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
        IProxyClassMapping.Item item = getProxyClassMapping().get(proxy);
        if (item != null) {
            return item.proxyImplClass;
        }
        throw new IllegalArgumentException(proxy + "$Proxy was not found");
    }

    private static IProxyClassMapping getProxyClassMapping() {
        if (sProxyClassMapping == null) {
            throw new IllegalArgumentException("no proxy class mapping was found");
        }
        return sProxyClassMapping;
    }
}
