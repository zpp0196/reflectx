package me.zpp0196.reflectx.proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zpp0196
 * @see SourceClass
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Source {
    /**
     * @return <ul>
     * <li>Class: 声明被代理类的路径，默认为当前类名</li>
     * <li>Method: 声明被代理方法的方法名，默认为当前方法名。</li>
     * </ul>
     */
    String value() default "";

    /**
     * @return signature
     */
    String signature() default "";

    /**
     * @return hashcode
     */
    long hashcode() default -1;
}
