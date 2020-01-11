package reflectx.utils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author zpp0196
 */
public interface IReflectUtils {

    class NoSuchMemberException extends RuntimeException {
        @SuppressWarnings("WeakerAccess")
        public NoSuchMemberException(String s) {
            super(s);
        }
    }

    @Nonnull
    Field findFieldExact(@Nonnull Class<?> clazz, @Nullable Class<?> fieldType,
            @Nonnull String fieldName) throws NoSuchMemberException;

    @Nonnull
    Method findMethodExact(@Nonnull Class<?> clazz, @Nullable Class<?> returnType,
            @Nonnull String methodName, @Nullable Class<?>... parameterTypes)
            throws NoSuchMemberException;

    @Nonnull
    <T> Constructor<?> findConstructor(@Nonnull Class<T> clazz,
            @Nullable Class<?>... parameterTypes) throws NoSuchMemberException;

    @Nullable
    default Field findFieldExactIfExists(@Nonnull Class<?> clazz, @Nullable Class<?> fieldType,
            @Nonnull String fieldName) {
        try {
            return findFieldExact(clazz, fieldType, fieldName);
        } catch (NoSuchMemberException e) {
            return null;
        }
    }

    @Nullable
    default Method findMethodExactIfExists(@Nonnull Class<?> clazz, @Nullable Class<?> returnType,
            @Nonnull String methodName, @Nullable Class<?>... parameterTypes) {
        try {
            return findMethodExact(clazz, returnType, methodName, parameterTypes);
        } catch (NoSuchMemberException e) {
            return null;
        }
    }

    @Nullable
    default <T> Constructor<?> findConstructorIfExists(@Nonnull Class<T> clazz,
            @Nullable Class<?>... parameterTypes) {
        try {
            return findConstructor(clazz, parameterTypes);
        } catch (NoSuchMemberException e) {
            return null;
        }
    }

    @Nonnull
    <T extends AccessibleObject> T accessible(@Nonnull T accessible);

    @Nonnull
    static IReflectUtils get() {
        return ReflectUtils.get();
    }
}
