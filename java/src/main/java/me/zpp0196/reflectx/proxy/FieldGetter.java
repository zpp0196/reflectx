package me.zpp0196.reflectx.proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface FieldGetter {

    /**
     * @return {@link Field#getType()}
     */
    Class<?> type() default IgnoreType.class;

    /**
     * @return {@link Field#getName()}
     */
    String value() default "";
}
