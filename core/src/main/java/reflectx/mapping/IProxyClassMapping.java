package reflectx.mapping;

import javax.annotation.Nullable;

import reflectx.BaseProxyClass;
import reflectx.IProxy;

/**
 * @author zpp0196
 */
public interface IProxyClassMapping {
    @Nullable
    Class<? extends BaseProxyClass> get(Class<? extends IProxy> proxy);
}
