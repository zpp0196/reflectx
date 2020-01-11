package reflectx;

public class ReflectxException extends RuntimeException {

    private static final long serialVersionUID = -5134448466865737453L;

    public ReflectxException() { }

    public ReflectxException(String s) {
        super(s);
    }

    public ReflectxException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ReflectxException(Throwable throwable) {
        super(throwable);
    }
}
