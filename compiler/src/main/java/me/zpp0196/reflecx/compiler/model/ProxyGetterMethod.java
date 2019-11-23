package me.zpp0196.reflecx.compiler.model;

import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Element;

import me.zpp0196.reflectx.proxy.ProxyGetter;
import me.zpp0196.reflectx.proxy.ProxyClass;

/**
 * @author zpp0196
 */
public class ProxyGetterMethod extends BaseProxyMethod {

    ProxyGetterMethod(Element element) {
        super(element);
    }

    @Override
    public MethodSpec.Builder buildMethodSpec() {
        ProxyGetter getter = mElement.getAnnotation(ProxyGetter.class);
        String fieldName = getter.value();
        if (fieldName.isEmpty()) {
            fieldName = mElement.getSimpleName().toString();
        }
        MethodSpec.Builder builder = super.buildMethodSpec();
        builder.addStatement("return get($T.findClass($T.class),\"$L\")",
                ProxyClass.class, mElement.getReturnType(), fieldName);
        return builder;
    }
}
