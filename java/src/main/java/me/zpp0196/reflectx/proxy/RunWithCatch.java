package me.zpp0196.reflectx.proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 捕获代理接口实现类方法中可能会出现的异常，如果方法有返回值则返回默认值或 {@code null}
 * <p>需要配合以下注解使用：
 * <ul>
 * <li>{@link Source}</li>
 * <li>{@link ProxySetter}</li>
 * <li>{@link ProxyGetter}</li>
 * <li>{@link FieldGetter}</li>
 * <li>{@link MethodGetter}</li>
 * <li>{@link ConstructorGetter}
 * </ul>
 *
 * @author zpp0196
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface RunWithCatch {
    byte byteValue() default Byte.MAX_VALUE;

    short shortValue() default Short.MIN_VALUE;

    int intValue() default Integer.MIN_VALUE;

    long longValue() default Long.MIN_VALUE;

    float floatValue() default Float.MIN_VALUE;

    double doubleValue() default Double.MIN_VALUE;

    boolean booleanValue() default false;

    char charValue() default Character.MIN_VALUE;

    String stringValue() default "";
}
