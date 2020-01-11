package reflectx;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * If you need to create a proxy callback interface, you must inherit the interface.
 *
 * @author zpp0196
 */
public interface IProxyCallback extends IProxy {
    /**
     * @return A proxy callback instance
     */
    default Object proxy() {
        return ProxyFactory.callback(getProxyClass(), this);
    }

    @Nullable
    @Override
    default Class<?> getSourceClass() {
        return Reflectx.getSourceClass(getProxyClass());
    }

    @Nonnull
    @Override
    default Class<?> requireSourceClass() {
        return Reflectx.getSourceClass(getProxyClass());
    }

    @Nonnull
    @Override
    default Class<? extends IProxy> getProxyClass() {
        return Objects.requireNonNull(Reflectx.getProxyClass(getClass().getInterfaces()[0]));
    }
}
