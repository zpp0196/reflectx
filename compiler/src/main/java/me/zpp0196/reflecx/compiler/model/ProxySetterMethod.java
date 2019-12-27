package me.zpp0196.reflecx.compiler.model;

import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import me.zpp0196.reflectx.proxy.ProxySetter;

/**
 * @author zpp0196
 */
public class ProxySetterMethod extends BaseProxyMethod {

    ProxySetterMethod(Element element) {
        super(element);
    }

    @Override
    public MethodSpec.Builder buildMethodSpec() {
        MethodSpec.Builder builder = super.buildMethodSpec();
        if (mElement.getParameters().size() != 1) {
            throw new IllegalArgumentException("methods decorated by ProxySetter must only have one argument");
        }
        VariableElement value = mElement.getParameters().get(0);
        ProxySetter getter = mElement.getAnnotation(ProxySetter.class);
        String fieldName = getter.value();
        if (fieldName.isEmpty()) {
            fieldName = value.getSimpleName().toString();
        }
        builder.addStatement("set($T.class,\"$L\",$L)", value, fieldName, fieldName);
        Name qualifiedName = ((TypeElement) mElement.getEnclosingElement()).getQualifiedName();
        if (qualifiedName.contentEquals(mElement.getReturnType().toString())) {
            builder.addStatement("return this");
        }
        return builder;
    }
}
