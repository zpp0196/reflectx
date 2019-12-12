package me.zpp0196.reflectx.proxy;

/**
 * 提供代理接口的一些默认实现
 *
 * @author zpp0196
 */
public interface IProxyClass extends IProxy {

    /**
     * 转换为其他代理接口
     *
     * @param clazz 需要转换的接口
     * @param <P>   需要转换的接口类型
     * @return {@link BaseProxyClass#as(Class)}
     */
    <P> P as(Class<P> clazz);

    /**
     * 获取被代理的对象
     *
     * @param <T> 返回值类型
     * @return {@link BaseProxyClass#get()}
     */
    <T> T get();

    /**
     * 设置被代理对象
     *
     * @param surrogate 新的被代理对象
     * @see BaseProxyClass#set(Object)
     */
    void set(Object surrogate);

    /**
     * 释放被代理对象的引用
     *
     * @see BaseProxyClass#release()
     */
    void release();
}
