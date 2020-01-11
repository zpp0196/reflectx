package reflectx.compiler;

import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import reflectx.annotations.FindField;

/**
 * @author zpp0196
 */
class FindFieldImpl extends BaseProxyMethod {

    FindFieldImpl(Element element) {
        super(element);
    }

    @Override
    MethodSpec.Builder buildMethodSpec() {
        MethodSpec.Builder builder = super.buildMethodSpec();
        FindField findField = mElement.getAnnotation(FindField.class);
        String fieldName = findField.value();
        if (fieldName.isEmpty()) {
            fieldName = mElement.getSimpleName().toString();
        }
        TypeMirror fieldType = getMirrorClass(mElement, FindField.class, "type");
        builder.addCode("return field(");
        if (fieldType == null) {
            builder.addCode("null");
        } else {
            builder.addCode("$T.class", fieldType);
        }
        builder.addCode(",$S);\n", fieldName);
        return builder;
    }
}
