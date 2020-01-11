package reflectx.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * Find the original class's {@link Method}.
 * <p>The marked method return type must be {@link Method}.
 *
 * @author zpp0196
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface FindMethod {
    /**
     * @return The original method's return type.
     */
    Class<?> returnType() default Object.class;

    /**
     * @return The original method's name.
     */
    String value() default "";

    /**
     * @return The original method's parameter types.
     */
    Class[] parameterTypes() default {};
}
