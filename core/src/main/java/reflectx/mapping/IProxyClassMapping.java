package reflectx.mapping;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import reflectx.BaseProxyClass;
import reflectx.IProxy;

/**
 * @author zpp0196
 */
public interface IProxyClassMapping {

    class Item {
        @Nonnull
        public Class<? extends BaseProxyClass> proxyImplClass;
        @Nullable
        public Map<Long, String> nameMapping;

        public Item(@Nonnull Class<? extends BaseProxyClass> proxyImplClass,
                @Nullable Map<Long, String> nameMapping) {
            this.proxyImplClass = proxyImplClass;
            this.nameMapping = nameMapping;
        }
    }

    @Nullable
    Item get(Class<? extends IProxy> proxy);
}
