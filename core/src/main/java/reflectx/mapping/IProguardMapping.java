package reflectx.mapping;

import java.lang.reflect.AnnotatedElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import reflectx.annotations.Source;
import reflectx.annotations.SourceName;

/**
 * If you don't want to use {@link Source} or {@link SourceName} to mark
 * potentially confusing class or method names in your Source code,
 * you can implement the interface to handle the strings yourself.
 *
 * @author zpp0196
 * @see Source
 * @see reflectx.Reflectx#setProguardMapping(IProguardMapping)
 */
public interface IProguardMapping {
    /**
     * This method can only handle methods inside a class or proxy callback.
     *
     * @param element Class of the proxy interface or method in the proxy callback interface,
     *                and that element must be annotated by {@link Source}.
     * @param name    Name specified in {@link Source}.
     * @return Returns the source name, if {@code null} is returned,
     * {@link #getSourceName(long, String)} will be called, otherwise it is not.
     */
    @Nullable
    default String getSourceName(@Nonnull AnnotatedElement element, @Nonnull String name) {
        return name;
    }

    /**
     * Handle the confusion with a globally unique id,
     * which is used for all elements annotated with {@link Source} or {@link SourceName}
     *
     * @param identifies The globally unique id.
     * @param name       Name specified in {@link Source} or {@link SourceName}.
     * @return Returns the source name.
     */
    @Nonnull
    default String getSourceName(long identifies, @Nonnull String name) {
        return name;
    }
}
