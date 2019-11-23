package me.zpp0196.reflectx.proxy;

/**
 * @author zpp0196
 */
public interface IProxy {
    /**
     * 获取被代理类的 Class
     *
     * @return {@link Class}
     */
    Class<?> getSourceClass();

    /**
     * 获取代理接口
     *
     * @return ProxyInterface.class
     */
    Class<?> getProxyInterface();
}
