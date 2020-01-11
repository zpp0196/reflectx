package reflectx.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * Find the original class's {@link Field}.
 * <p>The marked method return type must be {@link Field},
 * so you can only specify the type of field in the annotation.
 *
 * @author zpp0196
 * @see GetField
 * @see SetField
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface FindField {
    /**
     * @return The original field's type.
     */
    Class<?> type() default Object.class;

    /**
     * @return The original field's name.
     */
    String value() default "";
}
