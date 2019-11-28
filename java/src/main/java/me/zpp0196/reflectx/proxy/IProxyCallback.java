package me.zpp0196.reflectx.proxy;

/**
 * 代理回调接口
 *
 * @author zpp0196
 */
public interface IProxyCallback extends IProxy {
    /**
     * 创建代理回调接口实现类
     *
     * @return 动态代理生成的实现类
     */
    default Object proxy() {
        return ProxyFactory.callback(getProxyInterface(), this);
    }

    @Override
    default Class<?> getSourceClass() {
        return ProxyClass.findClass(getProxyInterface());
    }

    @Override
    default Class<?> getProxyInterface() {
        return getClass().getInterfaces()[0];
    }
}
