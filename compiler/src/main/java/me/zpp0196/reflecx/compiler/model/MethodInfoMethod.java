package me.zpp0196.reflecx.compiler.model;

import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.Element;

import me.zpp0196.reflectx.proxy.IgnoreType;
import me.zpp0196.reflectx.proxy.MethodGetter;

/**
 * @author zpp0196
 */
public class MethodInfoMethod extends BaseProxyMethod {

    MethodInfoMethod(Element element) {
        super(element);
    }

    @Override
    public MethodSpec.Builder buildMethodSpec() {
        MethodGetter methodGetter = mElement.getAnnotation(MethodGetter.class);
        String types;
        StringBuilder sb = new StringBuilder().append("new Class[]{");
        List parameterTypes = getMirrorClassArray(mElement, MethodGetter.class, "parameterTypes");
        for (int i = 0; i < parameterTypes.size(); i++) {
            sb.append(parameterTypes.get(i).toString());
            if (i != parameterTypes.size() - 1) {
                sb.append(",");
            }
        }
        types = sb.append("}").toString();
        Object returnType = getMirrorClass(mElement, MethodGetter.class, "returnType");
        String methodName = methodGetter.value();
        if (methodName.isEmpty()) {
            methodName = mElement.getSimpleName().toString();
        }
        MethodSpec.Builder builder = super.buildMethodSpec();
        builder.addStatement("return exactMethod($T.class,\"$L\",$L)",
                returnType == null ? IgnoreType.class : returnType, methodName, types);
        return builder;
    }
}
