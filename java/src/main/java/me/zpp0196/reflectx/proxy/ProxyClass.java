package me.zpp0196.reflectx.proxy;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import me.zpp0196.reflectx.util.ReflectException;

/**
 * @author zpp0196
 */
public class ProxyClass {

    private static ClassLoader sDefaultLoader = ProxyClass.class.getClassLoader();

    /**
     * 设置默认 ClassLoader
     *
     * @param classLoader {@link ClassLoader}
     */
    public static void setDefaultClassLoader(ClassLoader classLoader) {
        sDefaultLoader = classLoader;
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
        if (proxy == void.class) {
            return proxy;
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

    @Nonnull
    private static String getSourceName0(AnnotatedElement element) {
        String value = "";
        Source source = element.getAnnotation(Source.class);
        if (source != null) {
            value = source.value();
        }
        if (value.isEmpty()) {
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
