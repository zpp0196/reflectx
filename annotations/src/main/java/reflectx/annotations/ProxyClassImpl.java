package reflectx.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the parent class of the implementation class of the generated proxy interface.
 * <p>This class must extend {@code reflectx.BaseProxyClass}.
 *
 * @author zpp0196
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ProxyClassImpl {
}
