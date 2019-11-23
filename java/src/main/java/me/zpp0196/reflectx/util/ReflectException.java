package me.zpp0196.reflectx.util;

public class ReflectException extends RuntimeException {

    private static final long serialVersionUID = -5134448466865737453L;

    public ReflectException() { }

    public ReflectException(String s) {
        super(s);
    }

    public ReflectException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ReflectException(Throwable throwable) {
        super(throwable);
    }
}
