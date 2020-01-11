package reflectx.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Catch exceptions that might occur in the proxy interface implementation class method,
 * return the default value if the method has a return value or {@code null}.
 *
 * @author zpp0196
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface RunWithCatch {
    byte byteValue() default Byte.MAX_VALUE;

    short shortValue() default Short.MIN_VALUE;

    int intValue() default Integer.MIN_VALUE;

    long longValue() default Long.MIN_VALUE;

    float floatValue() default Float.MIN_VALUE;

    double doubleValue() default Double.MIN_VALUE;

    boolean booleanValue() default false;

    char charValue() default Character.MIN_VALUE;

    String stringValue() default "";
}
