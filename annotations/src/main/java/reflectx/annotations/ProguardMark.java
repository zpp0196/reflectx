package reflectx.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark class or method names that might be confused, currently only used as comments.
 *
 * @author zpp0196
 */
@Repeatable(ProguardMarks.class)
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface ProguardMark {
    String type() default "";

    String value() default "";
}
