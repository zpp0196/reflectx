package me.zpp0196.reflectx.proxy;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import me.zpp0196.reflectx.util.ReflectException;

/**
 * @author zpp0196
 */
public class ProxyClass {

    public interface IClassMapping {
        Class<? extends BaseProxyClass> get(Class<?> proxy);
    }

    public static final String DEFAULT_CLASS_MAPPING = "me.zpp0196.reflectx.proxy.ProxyClass$ClassMapping";
    private static ClassLoader sDefaultLoader = ProxyClass.class.getClassLoader();
    private static Set<IClassMapping> sClassMappingSet = new HashSet<>();
    private static Set<IProguardMapping> sProguardMappingSet = new HashSet<>();

    static {
        try {
            addClassMapping(DEFAULT_CLASS_MAPPING);
        } catch (Throwable ignore) {
        }
    }

    /**
     * 设置默认 ClassLoader
     *
     * @param classLoader {@link ClassLoader}
     */
    public static void setDefaultClassLoader(ClassLoader classLoader) {
        sDefaultLoader = classLoader;
    }

    /**
     * 添加映射实现类
     *
     * @param mappingClass 映射类
     * @see ProxyClassMapping
     */
    public static void addClassMapping(String... mappingClass) {
        try {
            for (String clazz : mappingClass) {
                Class<?> clz = Class.forName(clazz);
                sClassMappingSet.add((IClassMapping) clz.newInstance());
            }
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    public static void addProguardMapping(IProguardMapping... proguardMappings) {
        sProguardMappingSet.addAll(Arrays.asList(proguardMappings));
    }

    /**
     * 获取默认的 ClassLoader
     *
     * @return {@link ClassLoader}
     */
    @Nonnull
    public static ClassLoader getClassLoader() {
        if (sDefaultLoader == null) {
            sDefaultLoader = Thread.currentThread().getContextClassLoader();
        }
        if (sDefaultLoader == null) {
            sDefaultLoader = ProxyClass.class.getClassLoader();
        }
        return sDefaultLoader;
    }

    /**
     * @param proxy 代理类
     * @return 被代理类
     */
    @Nonnull
    public static Class<?> findClass(Class<?> proxy) {
        return findClass(proxy, true, getClassLoader());
    }

    /**
     * @param proxy  代理类
     * @param loader 被代理类的 ClassLoader
     * @return 被代理类
     */
    @Nonnull
    public static Class<?> findClass(Class<?> proxy, ClassLoader loader) {
        return findClass(proxy, true, loader);
    }

    /**
     * @param proxy      代理类
     * @param initialize 加载被代理的类时是否初始化
     * @param loader     被代理类的 ClassLoader
     * @return 被代理类
     * @see Class#forName(String, boolean, ClassLoader)
     */
    @Nonnull
    public static Class<?> findClass(Class<?> proxy, boolean initialize, ClassLoader loader) {
        if (proxy == void.class || proxy.isPrimitive()) {
            return proxy;
        }
        if (proxy.isAnnotationPresent(SourceClass.class)) {
            return proxy.getAnnotation(SourceClass.class).value();
        }
        String className = getSourceName(proxy);
        Class<?> clazz;
        try {
            clazz = Class.forName(className, initialize, loader);
        } catch (ClassNotFoundException e) {
            throw new ReflectException(e);
        }
        return clazz;
    }

    @Nonnull
    public static String getSourceName(@Nonnull Method method) {
        return getSourceName0(method);
    }

    @Nonnull
    public static String getSourceName(@Nonnull Class<?> clazz) {
        return getSourceName0(clazz);
    }

    /**
     * 获取代理接口的实现类
     *
     * @param proxy 代理接口
     * @return 代理接口实现类
     */
    public static Class<? extends BaseProxyClass> getProxyImpl(@Nonnull Class<?> proxy) {
        for (IClassMapping mapping : getClassMapping()) {
            if (mapping.get(proxy) != null) {
                return mapping.get(proxy);
            }
        }
        throw new IllegalArgumentException(proxy + "$Proxy was not found");
    }

    private static Set<IClassMapping> getClassMapping() {
        if (sClassMappingSet.isEmpty()) {
            throw new IllegalArgumentException("no mapping class was found");
        }
        return sClassMappingSet;
    }

    @Nonnull
    private static String getSourceName0(AnnotatedElement element) {
        String value = null;
        Source source = element.getAnnotation(Source.class);
        if (source != null) {
            for (IProguardMapping mapping : sProguardMappingSet) {
                if (mapping == null) {
                    continue;
                }
                value = mapping.getSource(source.value(), source.signature(), source.hashcode());
                if (value != null && !value.isEmpty()) {
                    break;
                }
            }
            if (sProguardMappingSet.isEmpty()) {
                value = source.value();
            }
        }
        if (value == null || value.isEmpty()) {
            if (element instanceof Class<?>) {
                value = ((Class) element).getName();
            } else if (element instanceof Method) {
                value = ((Method) element).getName();
            } else {
                throw new IllegalArgumentException("Unsupported elements!");
            }
        }
        return value;
    }
}
