package reflectx.compiler;

import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Element;

import reflectx.annotations.GetField;

/**
 * @author zpp0196
 */
class GetFieldImpl extends BaseProxyMethod {

    GetFieldImpl(Element element) {
        super(element);
    }

    @Override
    MethodSpec.Builder buildMethodSpec() {
        MethodSpec.Builder builder = super.buildMethodSpec();
        GetField getField = mElement.getAnnotation(GetField.class);
        String fieldName = getField.value();
        if (fieldName.isEmpty()) {
            fieldName = mElement.getSimpleName().toString();
        }
        String name = buildSourceName(builder, fieldName);
        builder.addStatement("return get($L.class,$L)",
                getTypeString(mElement.getReturnType()), name);
        return builder;
    }
}
