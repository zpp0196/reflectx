package me.zpp0196.reflecx.compiler.model;

import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import me.zpp0196.reflectx.proxy.FieldGetter;
import me.zpp0196.reflectx.proxy.IgnoreType;

/**
 * @author zpp0196
 */
public class FieldInfoMethod extends BaseProxyMethod {

    FieldInfoMethod(Element element) {
        super(element);
    }

    @Override
    public MethodSpec.Builder buildMethodSpec() {
        FieldGetter fieldGetter = mElement.getAnnotation(FieldGetter.class);
        TypeMirror fieldType = getMirrorClass(mElement, FieldGetter.class, "type");
        String fieldName = fieldGetter.value();
        if (fieldName.isEmpty()) {
            fieldName = mElement.getSimpleName().toString();
        }
        MethodSpec.Builder builder = super.buildMethodSpec();
        builder.addStatement("return exactField($T.class,\"$L\")",
                fieldType == null ? IgnoreType.class : fieldType, fieldName);
        return builder;
    }
}
