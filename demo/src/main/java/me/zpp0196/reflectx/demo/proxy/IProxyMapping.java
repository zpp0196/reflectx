package me.zpp0196.reflectx.demo.proxy;

import me.zpp0196.reflectx.proxy.ProxyMapping;

@ProxyMapping(IProxyMapping.MAPPING)
public interface IProxyMapping {
    String MAPPING = "me.zpp0196.reflectx.android.ProxyMapping";
}
