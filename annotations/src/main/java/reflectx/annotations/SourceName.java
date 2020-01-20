package reflectx.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The only difference from {@link Source} is that this is a source-level annotation.
 *
 * @author zpp0196
 * @see Source
 */
@Repeatable(SourceNames.class)
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface SourceName {

    long DEFAULT_VERSION = Source.DEFAULT_VERSION;
    long DEFAULT_IDENTIFIES = Source.DEFAULT_IDENTIFIES;

    /**
     * @return {@link Source#value()}
     */
    String value() default "";

    /**
     * @return {@link Source#version()}
     */
    long version() default DEFAULT_VERSION;

    /**
     * @return {@link Source#identifies()}
     */
    long identifies() default DEFAULT_IDENTIFIES;
}
