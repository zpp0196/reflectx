package me.zpp0196.reflectx.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import me.zpp0196.reflectx.util.Reflect;
import me.zpp0196.reflectx.util.ReflectException;

/**
 * @author zpp0196
 */
@SuppressWarnings("unchecked")
public class BaseProxyClass implements IProxyClass {

    protected Object mOriginal;
    protected Class<?> mClass;
    protected boolean isClass;
    protected Class<?> mProxyInterface;

    public BaseProxyClass(@Nonnull Object original, Class<?> proxyInterface) {
        this.mOriginal = original;
        this.mProxyInterface = proxyInterface;
        setOriginalClass(original);
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

    private void setOriginalClass(@Nullable Object original) {
        isClass = original instanceof Class<?>;
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
        setOriginalClass(surrogate);
    }

    @Override
    public void release() {
        set(null);
    }

    /**
     * @param type  {@link Field#getType()}
     * @param name  {@link Field#getName()}
     * @param value {@link Field#set(Object, Object)}
     */
    protected void set(Class<?> type, String name, Object value) {
        Reflect.on(mOriginal).set(type, name, value);
    }

    /**
     * @param type {@link Field#getType()}
     * @param name {@link Field#getName()}
     * @param <T>  字段值的类型
     * @return {@link Field#get(Object)}
     */
    protected <T> T get(Class<?> type, String name) {
        return (T) wrapper(Reflect.on(mOriginal).get(type, name), type);
    }

    /**
     * @param returnType {@link Method#getReturnType()}
     * @param name       {@link Method#getName()}
     * @param args       方法参数
     * @param <T>        方法调用结果类型
     * @return {@link Method#invoke(Object, Object...)}
     */
    protected <T> T call(Class<?> returnType, String name, Object... args) {
        for (Method method : mClass.getDeclaredMethods()) {
            if (!method.getName().equals(name)) {
                continue;
            }
            ProxyWrapper wrapper = new ProxyWrapper(method.getParameterTypes(), args);
            if (wrapper.unwrap()) {
                method.setAccessible(true);
                try {
                    if (isClass) {
                        return (T) method.invoke(null, args);
                    }
                    return (T) wrapper(method.invoke(mOriginal, args), returnType);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        String types = Arrays.toString(ProxyWrapper.getProxyTypes(args));
        throw new ReflectException(new NoSuchMethodException(mClass.getName() + "#" + name + "(" + types + ")"));
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
            wrapper = wrapper && className.equals(resultClass.getName());
            if (resultClass.isAssignableFrom(expectType) || resultClass.isPrimitive() || !wrapper) {
                return result;
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
