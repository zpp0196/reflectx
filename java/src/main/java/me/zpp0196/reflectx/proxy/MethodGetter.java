package me.zpp0196.reflectx.proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * @author zpp0196
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface MethodGetter {

    /**
     * @return {@link Method#getReturnType()}}
     */
    Class<?> returnType() default IgnoreType.class;

    /**
     * @return {@link Method#getName()}
     */
    String value() default "";

    /**
     * @return {@link Method#getParameterTypes()}
     */
    Class[] parameterTypes() default {};
}
