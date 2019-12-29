package me.zpp0196.reflectx.proxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import me.zpp0196.reflectx.util.IReflectUtils;
import me.zpp0196.reflectx.util.ReflectUtils;
import me.zpp0196.reflectx.util.ReflectException;

/**
 * @author zpp0196
 */
@SuppressWarnings("unchecked")
public class BaseProxyClass implements IProxyClass {

    protected Object mOriginal;
    protected Class<?> mClass;
    protected Class<?> mProxyInterface;

    final BaseProxyClass init(@Nonnull Object original, Class<?> proxyInterface) {
        this.mOriginal = original;
        this.mProxyInterface = proxyInterface;
        setOriginalClass(original);
        return this;
    }

    @Nonnull
    public IReflectUtils getReflectUtils() {
        return ReflectUtils.get();
    }

    @Override
    public <P> P as(Class<P> clazz) {
        return clazz.cast(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get() {
        return (T) mOriginal;
    }

    @Override
    public Class<?> getSourceClass() {
        return mClass;
    }

    @Override
    public Class<?> getProxyInterface() {
        return mProxyInterface;
    }

    protected void setOriginalClass(@Nullable Object original) {
        boolean isClass = original instanceof Class<?>;
        if (original == null) {
            mClass = null;
            return;
        }
        if (isClass) {
            mClass = (Class<?>) original;
        } else {
            mClass = original.getClass();
        }
    }

    @Override
    public void set(@Nullable Object surrogate) {
        if (surrogate instanceof BaseProxyClass) {
            BaseProxyClass proxy = (BaseProxyClass) surrogate;
            mOriginal = proxy.mOriginal;
        } else {
            mOriginal = surrogate;
        }
        setOriginalClass(mOriginal);
    }

    @Override
    public void release() {
        set(null);
    }

    /**
     * @param fieldType {@link Field#getType()}
     * @param fieldName {@link Field#getName()}
     * @param value     {@link Field#set(Object, Object)}
     */
    protected final void set(Class<?> fieldType, String fieldName, Object value) {
        try {
            exactField(fieldType, fieldName).set(get(), value);
        } catch (IllegalAccessException e) {
            throw new ReflectException(e);
        }
    }

    /**
     * @param fieldType {@link Field#getType()}
     * @param fieldName {@link Field#getName()}
     * @param <T>       字段值的类型
     * @return {@link Field#get(Object)}
     */
    protected final <T> T get(Class<?> fieldType, String fieldName) {
        try {
            return (T) wrapper(exactField(fieldType, fieldName)
                    .get(get()), fieldType);
        } catch (IllegalAccessException e) {
            throw new ReflectException(e);
        }
    }

    /**
     * @param returnType     {@link Method#getReturnType()}
     * @param methodName     {@link Method#getName()}
     * @param parameterTypes {@link Method#getParameterTypes()}
     * @param args           方法参数
     * @param <T>            方法调用结果类型
     * @return {@link Method#invoke(Object, Object...)}
     */
    protected final <T> T call(Class<?> returnType, String methodName,
            @Nullable Class<?>[] parameterTypes, @Nullable Object... args) {
        try {
            Method method;
            try {
                method = exactMethod(returnType, methodName, parameterTypes);
            } catch (Throwable th) {
                if (!returnType.isAssignableFrom(getClass())) {
                    throw th;
                }
                method = exactMethod(void.class, methodName, parameterTypes);
            }
            new ProxyWrapper(parameterTypes, args).unwrap();
            return (T) wrapper(method.invoke(get(), args), returnType);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectException(e);
        }
    }

    protected Field exactField(Class<?> fieldType, String fieldName) {
        fieldType = getOriginalClass(fieldType);
        return getReflectUtils().findFieldExact(getSourceClass(), fieldType, fieldName);
    }

    protected Method exactMethod(Class<?> returnType, String methodName, Class<?>... parameterTypes) {
        unwrapParameterTypes(parameterTypes);
        returnType = getOriginalClass(returnType);
        return getReflectUtils()
                .findMethodExact(getSourceClass(), returnType, methodName, parameterTypes);
    }

    protected Constructor findConstructor(Class<?>... parameterTypes) {
        unwrapParameterTypes(parameterTypes);
        return getReflectUtils().findConstructor(getSourceClass(), parameterTypes);
    }

    private void unwrapParameterTypes(Class[] parameterTypes) {
        if (parameterTypes == null) {
            return;
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = getOriginalClass(parameterTypes[i]);
        }
    }

    protected Class<?> getOriginalClass(Class<?> proxy) {
        return ProxyClass.findClass(proxy);
    }

    /**
     * 返回值装箱
     *
     * @param result     方法返回值结果
     * @param expectType 期望的返回值
     * @return 最终的返回值
     */
    protected Object wrapper(@Nullable Object result, @Nonnull Class<?> expectType) {
        boolean wrapper = expectType.isInterface();
        if (result != null) {
            String className = ProxyClass.getSourceName(expectType);
            Class<?> resultClass = result.getClass();
            if (expectType.isAssignableFrom(resultClass) || resultClass.isPrimitive() || !wrapper) {
                return result;
            }
            try {
                Class<?> clz = resultClass.getClassLoader().loadClass(className);
                if (!clz.isAssignableFrom(resultClass)) {
                    return result;
                }
            } catch (ClassNotFoundException e) {
                throw new ReflectException(e);
            }
        } else {
            wrapper = wrapper && expectType.isAssignableFrom(getClass());
            if (expectType == void.class || !wrapper) {
                return null;
            }
            return this;
        }
        if (result instanceof Class<?>) {
            return ProxyFactory.proxyClass(expectType);
        }
        return ProxyFactory.proxyObject(expectType, result);
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
