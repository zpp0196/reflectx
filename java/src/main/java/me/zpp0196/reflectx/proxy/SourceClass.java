package me.zpp0196.reflectx.proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明被代理类，一般用于已知 {@link Class}，优先级高于{@link Source}
 *
 * @author zpp0196
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SourceClass {

    /**
     * @return {@link Class}
     */
    Class<?> value();
}
