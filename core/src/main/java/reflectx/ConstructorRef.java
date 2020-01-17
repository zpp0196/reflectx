package reflectx;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import reflectx.annotations.FindConstructor;

/**
 * @param <T> The type of constructor's instance.
 * @author zpp0196
 * @see FindConstructor
 */
public class ConstructorRef<T> {

    @Nullable
    public Constructor<?> ctor;

    public ConstructorRef(@Nullable Constructor<?> ctor) {
        this.ctor = ctor;
    }

    public boolean exists() {
        return ctor != null;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public T create(Object... args) {
        try {
            return (T) Objects.requireNonNull(ctor).newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ReflectxException(e);
        }
    }

    @Nullable
    public T createIfExists(Object... args) {
        return exists() ? create(args) : null;
    }

    @Nullable
    public T createNoThrow(Object... args) {
        try {
            return createIfExists(args);
        } catch (Throwable th) {
            return null;
        }
    }
}
