package me.zpp0196.reflectx.proxy;

import javax.annotation.Nonnull;

/**
 * @author zpp0196
 * @see Source
 */
public interface IProguardMapping {
    String getSource(@Nonnull String name, @Nonnull String signature, long hashcode);
}
