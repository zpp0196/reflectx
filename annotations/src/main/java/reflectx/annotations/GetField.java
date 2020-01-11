package reflectx.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Find and get the field's value.
 * <p>The type of the field is the return type of the marked method.
 *
 * @author zpp0196
 * @see SetField
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface GetField {
    /**
     * @return The original field's name.
     * <p>If not specified, the name of the currently marked method is used by default.
     */
    String value() default "";
}
