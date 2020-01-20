package reflectx.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation serves two purposes:
 * <ul>
 * <li>Declare that the marked interface is a proxy interface.</li>
 * <li>Specify the name of the original class, field, or method.</li>
 * </ul>
 *
 * @author zpp0196
 * @see SourceClass
 * @see SourceName
 */
@Repeatable(Sources.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Source {

    long DEFAULT_VERSION = Long.MIN_VALUE;
    long DEFAULT_IDENTIFIES = Long.MIN_VALUE;

    /**
     * @return The original class, field, or method's name.
     */
    String value() default "";

    /**
     * When you repeatedly mark a class or method, you need to specify the version here.
     *
     * @return Version code.
     */
    long version() default DEFAULT_VERSION;

    /**
     * @return Globally unique identity.
     */
    long identifies() default DEFAULT_IDENTIFIES;
}
