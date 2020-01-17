package reflectx;

import java.lang.reflect.Field;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import reflectx.annotations.FindField;

/**
 * @param <T> The type of field's value.
 * @author zpp0196
 * @see FindField
 */
public class FieldRef<T> {

    @Nullable
    public Field field;
    @Nonnull
    public Object object;

    public FieldRef(@Nullable Field field, @Nonnull Object object) {
        this.field = field;
        this.object = object;
    }

    public boolean exists() {
        return field != null;
    }

    @Nonnull
    public String name() {
        return Objects.requireNonNull(field).getName();
    }

    @Nonnull
    public Class<?> type() {
        return Objects.requireNonNull(field).getType();
    }

    @Nonnull
    public Class<?> declaringClass() {
        if (field != null) {
            return field.getDeclaringClass();
        }
        if (object instanceof Class<?>) {
            return (Class<?>) object;
        } else {
            return object.getClass();
        }
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public T get() throws ReflectxException {
        try {
            return (T) Objects.requireNonNull(field).get(object);
        } catch (IllegalAccessException e) {
            throw new ReflectxException(e);
        }
    }

    @Nullable
    public T getIfExists() throws ReflectxException {
        return exists() ? get() : null;
    }

    @Nullable
    public T getNoThrow() {
        try {
            return getIfExists();
        } catch (Throwable ignored) {
            return null;
        }
    }

    public void set(T value) {
        try {
            Objects.requireNonNull(field).set(object, value);
        } catch (IllegalAccessException e) {
            throw new ReflectxException(e);
        }
    }

    public void setIfExists(T value) {
        if (exists()) {
            set(value);
        }
    }

    public void setNoThrow(T value) {
        try {
            setIfExists(value);
        } catch (Throwable ignored) {
        }
    }
}
