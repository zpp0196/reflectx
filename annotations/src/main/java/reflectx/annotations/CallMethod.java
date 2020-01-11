package reflectx.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Find and call the original's method.
 * <p>This annotation is the default annotation for methods in the proxy interface.
 *
 * @author zpp0196
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface CallMethod {
    /**
     * @return The original method's name.
     */
    String value() default "";

    /**
     * @return The original method's parameter types.
     */
    Class[] parameterTypes() default {};
}
