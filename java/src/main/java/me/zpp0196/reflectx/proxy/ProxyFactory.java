package me.zpp0196.reflectx.proxy;

import javax.annotation.Nonnull;

/**
 * 代理工厂类，用于生成代理对象
 *
 * @author zpp0196
 * @see ProxyBuilder
 */
public class ProxyFactory {

    /**
     * 创建一个代理回调接口对象
     *
     * @param proxy 回调接口
     * @param impl  回调接口实现
     * @return 代理回调
     * @see ProxyBuilder#callback()
     */
    public static Object callback(@Nonnull Class<?> proxy, @Nonnull Object impl) {
        return ProxyBuilder.proxy(proxy).original(impl).callback();
    }

    /**
     * 创建一个代理对象
     *
     * @param <P>   代理接口类型
     * @param proxy 代理接口
     * @param args  构造参数
     * @return 代理对象
     * @see ProxyBuilder#instance(Object...)
     */
    public static <P> P create(@Nonnull Class<P> proxy, Object... args) {
        return create(proxy, proxy.getClassLoader(), args);
    }

    /**
     * 创建一个代理对象
     *
     * @param <P>    代理对象接口
     * @param proxy  代理接口
     * @param loader 代理对象的 ClassLoader
     * @param args   构造参数
     * @return 代理对象
     * @see ProxyBuilder#instance(Object...)
     */
    public static <P> P create(@Nonnull Class<P> proxy, ClassLoader loader, Object... args) {
        return ProxyBuilder.proxy(proxy).loader(loader).instance(args);
    }

    /**
     * 创建代理静态工具类
     *
     * @param <P>   代理接口类型
     * @param proxy 代理接口
     * @return 代理工具类
     */
    public static <P> P proxyClass(@Nonnull Class<P> proxy) {
        return proxyClass(proxy, proxy.getClassLoader());
    }

    /**
     * 创建代理静态工具类
     *
     * @param <P>    代理接口类型
     * @param proxy  代理接口
     * @param loader 代理类的 ClassLoader
     * @return 代理工具类
     * @see ProxyBuilder#clazz()
     */
    public static <P> P proxyClass(@Nonnull Class<P> proxy, ClassLoader loader) {
        return ProxyBuilder.proxy(proxy).loader(loader).clazz();
    }

    /**
     * 创建指定对象的代理对象，缓存级别：持久
     *
     * @param <P>      代理接口类型
     * @param proxy    代理接口
     * @param original 被代理对象
     * @return 代理对象
     * @see ProxyBuilder#object()
     */
    public static <P> P proxyObject(@Nonnull Class<P> proxy, @Nonnull Object original) {
        return ProxyBuilder.proxy(proxy).original(original).object();
    }
}
