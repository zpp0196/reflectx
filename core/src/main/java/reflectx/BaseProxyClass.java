package reflectx;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import reflectx.mapping.IProguardMapping;
import reflectx.mapping.SourceMapping;
import reflectx.utils.IReflectUtils;
import reflectx.utils.ProxyWrapper;

/**
 * @author zpp0196
 */
@SuppressWarnings({"unchecked", "WeakerAccess"})
public class BaseProxyClass implements IProxyClass, IReflectUtils {

    @Nullable
    protected Object mOriginal;
    @Nullable
    protected Class<?> mSourceClass;
    @SuppressWarnings("NotNullFieldNotInitialized")
    @Nonnull
    protected Class<? extends IProxy> mProxyInterface;

    final BaseProxyClass init(@Nonnull Object original,
            @Nonnull Class<? extends IProxy> proxyInterface) {
        this.mOriginal = original;
        this.mProxyInterface = proxyInterface;
        setSourceClass(original);
        return this;
    }

    @Nonnull
    protected IReflectUtils getReflectUtils() {
        return IReflectUtils.get();
    }

    @Nonnull
    @Override
    public <P extends IProxyClass> P as(@Nonnull Class<P> proxy) {
        return proxy.cast(this);
    }

    @Nullable
    @Override
    public <P extends IProxyClass> P cast(@Nonnull Class<P> proxy) {
        if (mOriginal == null) {
            return null;
        }
        Class<?> sourceClass = Reflectx.getSourceClass(proxy);
        if (mOriginal.getClass().isAssignableFrom(sourceClass)) {
            return ProxyFactory.proxy(proxy, mOriginal);
        }
        return null;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get() {
        return (T) mOriginal;
    }

    @Nullable
    @Override
    public Class<?> getSourceClass() {
        return mSourceClass;
    }

    @Nonnull
    @Override
    public Class<? extends IProxy> getProxyClass() {
        return mProxyInterface;
    }

    @Override
    public <T extends IProxyClass> T set(@Nullable Object object) {
        if (object instanceof BaseProxyClass) {
            BaseProxyClass proxy = (BaseProxyClass) object;
            mOriginal = proxy.mOriginal;
        } else {
            mOriginal = object;
        }
        setSourceClass(mOriginal);
        return (T) this;
    }

    protected void setSourceClass(@Nullable Object original) {
        if (original == null) {
            mSourceClass = null;
            return;
        }
        if (original instanceof Class<?>) {
            mSourceClass = (Class<?>) original;
        } else {
            mSourceClass = original.getClass();
        }
    }

    /**
     * @param fieldType The field's type, or {@code null} if don't care about the type.
     * @param fieldName The field's name.
     * @param value     The field's value.
     * @throws ReflectxException Equals {@link IllegalAccessException}
     */
    protected final void set(@Nullable Class<?> fieldType, @Nonnull String fieldName,
            @Nullable Object value) throws ReflectxException {
        try {
            field(fieldType, fieldName).set(get(), value);
        } catch (IllegalAccessException e) {
            throw new ReflectxException(e);
        }
    }

    /**
     * @param fieldType The field's type, or {@code null} if don't care about the type.
     * @param fieldName The field's name.
     * @param <T>       The type of field's value;
     * @return The field's value.
     */
    protected final <T> T get(@Nullable Class<?> fieldType, @Nonnull String fieldName) {
        try {
            return (T) wrapperResult(field(fieldType, fieldName).get(get()), fieldType);
        } catch (IllegalAccessException e) {
            throw new ReflectxException(e);
        }
    }

    /**
     * @param returnType     The method's return type, or {@code null}
     *                       if don't care about the return type.
     * @param methodName     The method's name.
     * @param parameterTypes The method's parameter types.
     * @param args           The method's arguments.
     * @param <T>            The type of method's returns the result.
     */
    protected final <T> T call(@Nullable Class<?> returnType, @Nonnull String methodName,
            @Nullable Class<?>[] parameterTypes, @Nullable Object... args) {
        try {
            Method method;
            try {
                method = method(returnType, methodName, parameterTypes);
            } catch (IReflectUtils.NoSuchMemberException e) {
                if (returnType == null || !returnType.isAssignableFrom(getClass())) {
                    throw e;
                }
                method = method(void.class, methodName, parameterTypes);
            }
            ProxyWrapper.unwrap(parameterTypes, args);
            return (T) wrapperResult(method.invoke(get(), args), returnType);
        } catch (IllegalAccessException e) {
            throw new ReflectxException(e);
        } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
                throw new ReflectxException(e.getCause());
            }
            throw new ReflectxException(e);
        }
    }

    protected Field field(@Nullable Class<?> fieldType, @Nonnull String fieldName) {
        return findFieldExact(requireSourceClass(), fieldType, fieldName);
    }

    protected Method method(@Nullable Class<?> returnType, @Nonnull String methodName,
            @Nullable Class<?>... parameterTypes) {
        return findMethodExact(requireSourceClass(), returnType, methodName, parameterTypes);
    }

    protected <T> Constructor<T> constructor(@Nullable Class<?>... parameterTypes) {
        return (Constructor<T>) findConstructor(requireSourceClass(), parameterTypes);
    }

    @Nonnull
    @Override
    public Field findFieldExact(@Nonnull Class<?> clazz, @Nullable Class<?> fieldType,
            @Nonnull String fieldName) throws NoSuchMemberException {
        fieldType = getSourceClass(fieldType);
        return getReflectUtils().findFieldExact(requireSourceClass(), fieldType, fieldName);
    }

    @Nonnull
    @Override
    public Method findMethodExact(@Nonnull Class<?> clazz, @Nullable Class<?> returnType,
            @Nonnull String methodName, @Nullable Class<?>... parameterTypes)
            throws NoSuchMemberException {
        unwrapParameterTypes(parameterTypes);
        returnType = getSourceClass(returnType);
        return getReflectUtils().findMethodExact(requireSourceClass(),
                returnType, methodName, parameterTypes);
    }

    @Nonnull
    @Override
    public <T> Constructor<T> findConstructor(@Nonnull Class<T> clazz,
            @Nullable Class<?>... parameterTypes) throws NoSuchMemberException {
        unwrapParameterTypes(parameterTypes);
        return (Constructor<T>) getReflectUtils()
                .findConstructor(requireSourceClass(), parameterTypes);
    }

    protected void unwrapParameterTypes(@Nullable Class<?>[] parameterTypes) {
        if (parameterTypes == null) {
            return;
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = getSourceClass(parameterTypes[i]);
        }
    }

    @Nullable
    protected Class<?> getSourceClass(@Nullable Class<?> proxy) {
        if (proxy == null) {
            return null;
        }
        Class<? extends IProxy> proxyClass = Reflectx.getProxyClass(proxy);
        return proxyClass == null ? proxy : Reflectx.getSourceClass(proxyClass);
    }

    protected String getSourceName(@Nonnull String def,
            @Nonnull List<SourceMapping> sourceMappings) {
        long latestVersion = SourceMapping.DEFAULT_VERSION;
        for (SourceMapping sourceMapping : sourceMappings) {
            if (sourceMapping.version >= latestVersion) {
                latestVersion = sourceMapping.version;
                def = sourceMapping.value;
            }
            if (sourceMapping.version == Reflectx.getProguardVersion()) {
                return sourceMapping.value;
            }
        }
        IProguardMapping proguardMapping = Reflectx.getProguardMapping();
        if (proguardMapping == null || sourceMappings.isEmpty()) {
            return def;
        }
        return proguardMapping.getSourceName(sourceMappings.get(0).identifies, def);
    }

    /**
     * Box the return value.
     *
     * @param result     The method's returns the result.
     * @param expectType The type of return value expected.
     * @return Final return value.
     */
    @Nullable
    protected Object wrapperResult(@Nullable Object result, @Nullable Class<?> expectType) {
        if (expectType == null) {
            return result;
        }
        boolean wrapper = expectType.isInterface();
        if (result == null) {
            wrapper = wrapper && expectType.isAssignableFrom(getClass());
            if (expectType == void.class || !wrapper) {
                return null;
            }
            return this;
        }
        Class<?> resultClass = result.getClass();
        if (expectType.isAssignableFrom(resultClass) || !wrapper) {
            return result;
        }
        Class<? extends IProxy> proxyClass = Reflectx.getProxyClass(expectType);
        if (proxyClass == null) {
            return result;
        }
        String className = Reflectx.getSourceName(proxyClass);
        try {
            Class<?> clazz = resultClass.getClassLoader().loadClass(className);
            if (!clazz.isAssignableFrom(resultClass)) {
                return result;
            }
        } catch (ClassNotFoundException e) {
            throw new ReflectxException(e);
        }
        if (result instanceof Class<?>) {
            return ProxyFactory.proxy(proxyClass);
        }
        return ProxyFactory.proxy(proxyClass, result);
    }

    @Override
    public boolean equals(Object o) {
        if (mOriginal == null) {
            return super.equals(o);
        }
        if (o instanceof BaseProxyClass) {
            BaseProxyClass proxy = (BaseProxyClass) o;
            return mOriginal.equals(proxy.mOriginal);
        }
        return mOriginal.equals(o);
    }

    @Override
    public int hashCode() {
        if (mOriginal == null) {
            return super.hashCode();
        }
        return mOriginal.hashCode();
    }

    @Override
    public String toString() {
        if (mOriginal == null) {
            return super.toString();
        }
        return mOriginal.toString();
    }
}
