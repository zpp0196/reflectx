package reflectx.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author zpp0196
 */
@SuppressWarnings("unchecked")
class ReflectUtils implements IReflectUtils {

    @Nonnull
    static IReflectUtils get() {
        return INSTANCE;
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.PARAMETER)
    private @interface Null {
    }

    private static final IReflectUtils INSTANCE = new ReflectUtils();
    private static final HashMap<String, Field> mFieldCache = new HashMap<>();
    private static final HashMap<String, Method> mMethodCache = new HashMap<>();
    private static final HashMap<String, Constructor<?>> mConstructorCache = new HashMap<>();

    private ReflectUtils() { }

    @Nonnull
    @Override
    public Field findFieldExact(@Nonnull Class<?> clazz, @Nullable Class<?> fieldType,
            @Nonnull String fieldName) throws NoSuchMemberException {
        return find(this::getFieldFullName, this::findFieldExactImpl, mFieldCache, clazz,
                fieldType, fieldName, null);
    }

    @Nonnull
    @Override
    public Method findMethodExact(@Nonnull Class<?> clazz, @Nullable Class<?> returnType,
            @Nonnull String methodName, @Nullable Class<?>... parameterTypes)
            throws NoSuchMemberException {
        return find(this::getMethodFullName, this::findMethodExactImpl, mMethodCache, clazz,
                returnType, methodName, parameterTypes);
    }

    @Nonnull
    @Override
    public <T> Constructor<T> findConstructor(@Nonnull Class<T> clazz,
            @Nullable Class<?>... parameterTypes) throws NoSuchMemberException {
        return (Constructor<T>) find(this::getConstructorFullName, this::findConstructorImpl,
                mConstructorCache, clazz, null, null, parameterTypes);
    }

    @Nullable
    private Field findFieldExactImpl(@Nonnull Class<?> clazz, @Nullable Class<?> fieldType,
            @Nonnull String fieldName, @Null Class<?>[] types) {
        Field field = null;
        try {
            field = clazz.getField(fieldName);
        } catch (NoSuchFieldException ignore) {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
            }
        }
        if (field != null && isTypeMatch(fieldType, field.getType())) {
            return field;
        }
        do {
            for (Field declaredField : clazz.getDeclaredFields()) {
                if (declaredField.getName().equals(fieldName) &&
                        isTypeMatch(fieldType, declaredField.getType())) {
                    return declaredField;
                }
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        return null;
    }

    @Nullable
    private Method findMethodExactImpl(@Nonnull Class<?> clazz, @Nullable Class<?> returnType,
            @Nonnull String methodName, @Nullable Class<?>[] parameterTypes) {
        Method method = null;
        try {
            method = clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException ignore) {
            try {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ignored) {
            }
        }
        if (method != null && isTypeMatch(returnType, method.getReturnType())) {
            return method;
        }
        do {
            for (Method declaredMethod : clazz.getDeclaredMethods()) {
                if (declaredMethod.getName().equals(methodName) &&
                        isTypeMatch(returnType, declaredMethod.getReturnType()) &&
                        isTypesMatch(parameterTypes, declaredMethod.getParameterTypes())) {
                    return declaredMethod;
                }
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        return null;
    }

    @Nullable
    private Constructor<?> findConstructorImpl(@Nonnull Class<?> clazz, @Null Class<?> type,
            @Null String name, @Nullable Class<?>[] parameterTypes) {
        try {
            return clazz.getConstructor(parameterTypes);
        } catch (NoSuchMethodException ignore) {
            try {
                return clazz.getDeclaredConstructor(parameterTypes);
            } catch (NoSuchMethodException ignored) {
            }
        }
        for (Constructor<?> declaredConstructor : clazz.getDeclaredConstructors()) {
            if (isTypesMatch(parameterTypes, declaredConstructor.getParameterTypes())) {
                return declaredConstructor;
            }
        }
        return null;
    }

    @Nonnull
    private StringBuilder getFieldFullName(@Nonnull Class<?> clazz, @Nullable Class<?> fieldType,
            @Nonnull String fieldName, @Null Class<?>[] types) {
        StringBuilder sb = new StringBuilder().append(clazz.getName())
                .append("#").append(fieldName);
        if (fieldType != null) {
            sb.append(": ").append(fieldType.getName());
        }
        return sb;
    }

    @Nonnull
    private StringBuilder getMethodFullName(@Nonnull Class<?> clazz, @Nullable Class<?> returnType,
            @Nonnull String methodName, @Nullable Class<?>[] parameterTypes) {
        StringBuilder sb = new StringBuilder().append(clazz.getName())
                .append("#").append(methodName);
        appendParameterTypes(sb, parameterTypes);
        if (returnType != null) {
            sb.append(": ").append(returnType.getName());
        }
        return sb;
    }

    @Nonnull
    private StringBuilder getConstructorFullName(@Nonnull Class<?> clazz, @Null Class<?> type,
            @Null String name, @Nullable Class<?>[] parameterTypes) {
        return appendParameterTypes(
                new StringBuilder().append(clazz.getName()).append("#"),
                parameterTypes
        );
    }

    @Nonnull
    private StringBuilder appendParameterTypes(@Nonnull StringBuilder sb,
            @Nullable Class<?>[] parameterTypes) {
        sb.append("(");
        if (parameterTypes != null && parameterTypes.length > 0) {
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameter = parameterTypes[i];
                sb.append(parameter.getName());
                if (i != parameterTypes.length - 1) {
                    sb.append(", ");
                }
            }
        }
        return sb.append(")");
    }

    private boolean isTypesMatch(@Nullable Class<?>[] expected, @Nullable Class<?>[] original) {
        if (expected == null && original == null) {
            return true;
        }
        if (expected == null || original == null || expected.length != original.length) {
            return false;
        }

        for (int i = 0; i < original.length; i++) {
            if (!isTypeMatch(expected[i], original[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean isTypeMatch(@Nullable Class<?> expected, @Nonnull Class<?> original) {
        if (expected == null) {
            return true;
        }
        return expected.isAssignableFrom(original);
    }

    private interface IMemberName {
        @Nonnull
        StringBuilder getFullName(@Nonnull Class<?> clazz, @Nullable Class<?> type,
                @Nullable String name, @Nullable Class<?>[] types);
    }

    private interface IMemberFinder<T extends AccessibleObject> {
        @Nullable
        T find(@Nonnull Class<?> clazz, @Nullable Class<?> type,
                @Nullable String name, @Nullable Class<?>[] types);
    }

    @Nonnull
    private <T extends AccessibleObject> T find(@Nonnull IMemberName memberName,
            @Nonnull IMemberFinder<T> finder, @Nonnull Map<String, T> cache,
            @Nonnull Class<?> clazz, @Nullable Class<?> type, @Nullable String name,
            @Nullable Class<?>[] types) throws NoSuchMemberException {
        String fullName = memberName.getFullName(clazz, type, name, types).toString();
        NoSuchMemberException e = new NoSuchMemberException(fullName);
        if (cache.containsKey(fullName)) {
            T t = cache.get(fullName);
            if (t == null) {
                throw e;
            }
            return t;
        }
        T t = finder.find(clazz, type, name, types);
        if (t == null) {
            cache.put(fullName, null);
            throw e;
        }
        cache.put(fullName, accessible(t));
        return t;
    }
}
