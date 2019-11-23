package me.zpp0196.reflectx.proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * 声明该方法用于设置被代理类的字段
 * <ul>
 * <li>被该注解修饰的方法只能有一个参数，即 field.value </li>
 * <li>被该注解修饰的方法返回值必须是 void.class 或 this(Builder)</li>
 * </ul>
 *
 * @author zpp0196
 * @see ProxyGetter
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface ProxySetter {
    /**
     * 被代理字段的 name
     *
     * @return {@link Field#getName()}
     * <p>默认为当前方法名
     */
    String value() default "";
}
