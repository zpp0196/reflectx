package reflectx.mapping;

import java.lang.reflect.AnnotatedElement;

import javax.annotation.Nonnull;

import reflectx.annotations.Source;

/**
 * @author zpp0196
 * @see Source
 * @see reflectx.Reflectx#setProguardMapping(IProguardMapping)
 */
public interface IProguardMapping {
    @Nonnull
    String getSourceName(@Nonnull AnnotatedElement element, @Nonnull String name, long version);
}
