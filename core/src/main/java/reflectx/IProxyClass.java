package reflectx;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author zpp0196
 * @see IProxy
 */
public interface IProxyClass extends IProxy {
    /**
     * @param proxy Sub interface of the current proxy interface.
     * @param <P>   The sub interface's type.
     * @return {@code this}
     * @see Class#cast(Object)
     */
    @Nonnull
    <P extends IProxyClass> P as(@Nonnull Class<P> proxy);

    /**
     * Unlike {@link #as(Class)}, this method checks for type matches.
     *
     * @param proxy Sub interface of the current proxy interface.
     * @param <P>   The sub interface's type.
     * @return {@code this}
     */
    @Nullable
    <P extends IProxyClass> P cast(@Nonnull Class<P> proxy);

    /**
     * @return The original object.
     */
    @Nullable
    <T> T get();

    @Nonnull
    default <T> T requireOriginal() {
        return Objects.requireNonNull(get());
    }

    /**
     * Update the original object.
     *
     * @param object The object being proxied.
     */
    <T extends IProxyClass> T set(@Nullable Object object);
}
