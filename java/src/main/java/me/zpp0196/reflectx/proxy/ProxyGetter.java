package me.zpp0196.reflectx.proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * 声明该方法用于获取被代理类的字段
 *
 * @author zpp0196
 * @see ProxySetter
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface ProxyGetter {
    /**
     * 被代理字段的 name
     *
     * @return {@link Field#getName()}
     * <p>默认为当前方法名
     */
    String value() default "";
}
