package reflectx.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;

/**
 * Find the original class's {@link Constructor}.
 * <p>The marked method return type must be {@link Constructor}.
 *
 * @author zpp0196
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface FindConstructor {
    /**
     * @return The original constructor's parameter types.
     */
    Class[] value() default {};
}
