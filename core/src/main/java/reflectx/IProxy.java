package reflectx;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import reflectx.annotations.Source;

/**
 * For specification reasons, all proxy interfaces need to inherit this interface,
 * even if you already use the {@link Source}
 *
 * @author zpp0196
 */
public interface IProxy {
    /**
     * @return The original's class.
     */
    @Nullable
    Class<?> getSourceClass();

    @Nonnull
    default Class<?> requireSourceClass() {
        return Objects.requireNonNull(getSourceClass());
    }

    /**
     * @return The proxy interface's class.
     */
    @Nonnull
    Class<? extends IProxy> getProxyClass();
}
