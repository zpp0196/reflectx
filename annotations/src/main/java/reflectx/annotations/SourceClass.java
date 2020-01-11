package reflectx.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The same as {@link Source}, except that it is generally used to mark certain system classes.
 * <p>Priority is higher than {@link Source}.
 *
 * @author zpp0196
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SourceClass {
    /**
     * @return The original class.
     */
    Class<?> value();
}
