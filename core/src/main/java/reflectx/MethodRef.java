package reflectx;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import reflectx.annotations.FindMethod;

/**
 * @param <T> The method's return type.
 * @author zpp0196
 * @see FindMethod
 */
public class MethodRef<T> {

    @Nullable
    public Method method;
    @Nonnull
    public Object object;

    public MethodRef(@Nullable Method method, @Nonnull Object object) {
        this.method = method;
        this.object = object;
    }

    public boolean exists() {
        return method != null;
    }

    @Nonnull
    public String name() {
        return Objects.requireNonNull(method).getName();
    }

    @Nonnull
    public Class<?> declaringClass() {
        if (method != null) {
            return method.getDeclaringClass();
        }
        if (object instanceof Class<?>) {
            return (Class<?>) object;
        } else {
            return object.getClass();
        }
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public T invoke(Object... args) throws ReflectxException {
        try {
            return (T) Objects.requireNonNull(method).invoke(object, args);
        } catch (IllegalAccessException e) {
            throw new ReflectxException(e);
        } catch (InvocationTargetException e) {
            Throwable t = e;
            if (e.getCause() != null) {
                t = e.getCause();
            }
            throw new ReflectxException(t);
        }
    }

    @Nullable
    public T invokeIfExists(Object... args) throws ReflectxException {
        return exists() ? invoke(args) : null;
    }

    @Nullable
    public T invokeNoThrow(Object... args) {
        try {
            return invokeIfExists(args);
        } catch (Throwable ignored) {
            return null;
        }
    }
}
