package me.zpp0196.reflectx.util;

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

    @Nonnull
    Field findFieldExact(@Nonnull Class<?> target, @Nonnull Class<?> fieldType,
            @Nonnull String fieldName);

    @Nonnull
    Method findMethodExact(@Nonnull Class<?> target, @Nonnull Class<?> returnType,
            @Nonnull String methodName, @Nullable Class<?>... parameterTypes);

    @Nonnull
    <C> Constructor<?> findConstructor(@Nonnull Class<C> target,
            @Nullable Class<?>... parameterTypes);

    @Nonnull
    <T extends AccessibleObject> T accessible(@Nonnull T accessible);
}
