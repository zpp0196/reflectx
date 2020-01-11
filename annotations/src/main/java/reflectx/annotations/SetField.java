package reflectx.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * Find and set the field's value.
 * <p>It should be noted that:
 * <ul>
 * <li>The marked method can have only one argument, that is field's value.</li>
 * <li>The marked method must return {@code void.class} or this class.</li>
 * </ul>
 *
 * @author zpp0196
 * @see GetField
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface SetField {
    /**
     * 被代理字段的 name
     *
     * @return {@link Field#getName()}
     * <p>默认为当前方法名
     */
    String value() default "";
}
