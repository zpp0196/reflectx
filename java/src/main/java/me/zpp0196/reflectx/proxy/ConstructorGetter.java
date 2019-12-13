package me.zpp0196.reflectx.proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface ConstructorGetter {
    /**
     * @return {@link Constructor#getParameterTypes()}
     */
    Class[] value() default {};
}
