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
 * <li>Specify the name of the original class or method,
 * If the mark is on a method, it can only be treated as an annotation,
 * unless the method is in {@code reflectx.IProxyCallback}.</li>
 * </ul>
 *
 * @author zpp0196
 * @see SourceClass
 */
@Repeatable(Sources.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Source {
    /**
     * @return The original class or method's name.
     */
    String value() default "";

    /**
     * When you repeatedly mark a class or method, you need to specify the version here.
     *
     * @return Version code.
     */
    long version() default -1;
}
