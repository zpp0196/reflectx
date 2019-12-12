package me.zpp0196.reflectx.util;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings({"StringBufferReplaceableByString", "unchecked"})
public class ReflectUtils implements IReflectUtils {

    public static class NoSuchMemberException extends RuntimeException {
        private NoSuchMemberException(String s) {
            super(s);
        }
    }

    private static IReflectUtils sReflectUtils;
    private static final HashMap<String, Field> mFieldCache = new HashMap<>();
    private static final HashMap<String, Method> mMethodCache = new HashMap<>();
    private static final HashMap<String, Constructor<?>> mConstructorCache = new HashMap<>();

    private ReflectUtils() {
    }

    public static void setImpl(@Nonnull IReflectUtils reflectUtils) {
        sReflectUtils = reflectUtils;
    }

    @Nonnull
    public static IReflectUtils get() {
        if (sReflectUtils == null) {
            sReflectUtils = new ReflectUtils();
        }
        return sReflectUtils;
    }

    @Nonnull
    @Override
    public Field findFieldExact(@Nonnull Class<?> target, @Nonnull Class<?> fieldType,
            @Nonnull String fieldName) {
        return find(this::getFieldFullName, this::findFieldExactImpl, mFieldCache, target,
                fieldType, fieldName, null);
    }

    @Nonnull
    @Override
    public Method findMethodExact(@Nonnull Class<?> target, @Nonnull Class<?> returnType,
            @Nonnull String methodName, @Nullable Class<?>... parameterTypes) {
        return find(this::getMethodFullName, this::findMethodExactImpl, mMethodCache, target,
                returnType, methodName, parameterTypes);
    }

    @Nonnull
    @Override
    public <C> Constructor<C> findConstructor(@Nonnull Class<C> target,
            @Nullable Class<?>... parameterTypes) {
        return (Constructor<C>) find(this::getConstructorFullName, this::findConstructorImpl,
                mConstructorCache, target, null, null, parameterTypes);
    }

    @Override
    @Nonnull
    public <T extends AccessibleObject> T accessible(@Nonnull T accessible) {
        if (accessible instanceof Member) {
            Member member = (Member) accessible;
            if (Modifier.isPublic(member.getModifiers()) &&
                    Modifier.isPublic(member.getDeclaringClass().getModifiers())) {
                return accessible;
            }
        }
        if (!accessible.isAccessible()) {
            accessible.setAccessible(true);
        }
        return accessible;
    }

    private Field findFieldExactImpl(Class<?> target, Class<?> fieldType, String fieldName,
            Class[] types) {
        Field field = null;
        try {
            field = target.getField(fieldName);
        } catch (NoSuchFieldException ignore) {
            try {
                field = target.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
            }
        }
        if (field != null && fieldType.isAssignableFrom(field.getType())) {
            return field;
        }
        while (true) {
            target = target.getSuperclass();
            if (target == null || target.equals(Object.class)) {
                break;
            }
            try {
                field = target.getDeclaredField(fieldName);
                if (fieldType.isAssignableFrom(field.getType())) {
                    return field;
                }
            } catch (NoSuchFieldException ignored) {
            }
        }
        return null;
    }

    private Method findMethodExactImpl(Class<?> target, Class<?> returnType, String methodName,
            @Nullable Class<?>[] parameterTypes) {
        Method method = null;
        try {
            method = target.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException ignore) {
            try {
                method = target.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ignored) {
            }
        }
        if (method != null && returnType.isAssignableFrom(method.getReturnType())) {
            return method;
        }
        while (true) {
            target = target.getSuperclass();
            if (target == null || target.equals(Object.class)) {
                break;
            }
            try {
                method = target.getDeclaredMethod(methodName, parameterTypes);
                if (returnType.isAssignableFrom(method.getReturnType())) {
                    return method;
                }
            } catch (NoSuchMethodException ignored) {
            }
        }
        return null;
    }

    private Constructor<?> findConstructorImpl(Class<?> target, Class<?> type, String name,
            @Nullable Class<?>[] parameterTypes) {
        try {
            return target.getConstructor(parameterTypes);
        } catch (NoSuchMethodException ignore) {
            try {
                return target.getDeclaredConstructor(parameterTypes);
            } catch (NoSuchMethodException ignored) {
                return null;
            }
        }
    }

    private String getFieldFullName(Class<?> target, Class<?> fieldType, String fieldName,
            Class<?>[] types) {
        return new StringBuilder()
                .append(target.getCanonicalName())
                .append("#")
                .append(fieldName)
                .append(":")
                .append(fieldType.getCanonicalName())
                .toString();
    }

    private String getMethodFullName(Class<?> target, Class<?> returnType, String methodName,
            Class<?>[] parameterTypes) {
        StringBuilder sb = new StringBuilder()
                .append(target.getCanonicalName())
                .append("#")
                .append(methodName)
                .append("(");
        if (parameterTypes != null && parameterTypes.length > 0) {
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameter = parameterTypes[i];
                sb.append(parameter.getCanonicalName());
                if (i != parameterTypes.length - 1) {
                    sb.append(",");
                }
            }
        }
        sb.append(")")
                .append(":")
                .append(returnType.getCanonicalName());
        return sb.toString();
    }

    private String getConstructorFullName(Class<?> target, Class<?> type, String name,
            Class<?>[] parameterTypes) {
        StringBuilder sb = new StringBuilder()
                .append(target.getCanonicalName())
                .append("#")
                .append("(");
        if (parameterTypes != null && parameterTypes.length > 0) {
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameter = parameterTypes[i];
                sb.append(parameter.getCanonicalName());
                if (i != parameterTypes.length - 1) {
                    sb.append(",");
                }
            }
        }
        return sb.append(")").toString();
    }

    private interface IMemberName {
        String getFullName(Class<?> target, Class<?> type, String name, Class<?>[] types);
    }

    private interface IMemberFinderImpl<T extends AccessibleObject> {
        T find(Class<?> target, Class<?> type, String name, Class<?>[] types);
    }

    @Nonnull
    private <T extends AccessibleObject> T find(IMemberName memberName, IMemberFinderImpl<T> finder,
            Map<String, T> cache, Class<?> target, Class<?> type, String name, Class<?>[] types) {
        String fullName = memberName.getFullName(target, type, name, types);
        NoSuchMemberException e = new NoSuchMemberException(fullName);
        if (cache.containsKey(fullName)) {
            T t = cache.get(fullName);
            if (t == null) {
                throw e;
            }
            return t;
        }
        T t = finder.find(target, type, name, types);
        if (t == null) {
            cache.put(fullName, null);
            throw e;
        }
        cache.put(fullName, accessible(t));
        return t;
    }
}
