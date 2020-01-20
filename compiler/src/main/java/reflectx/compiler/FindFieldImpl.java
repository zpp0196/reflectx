package reflectx.compiler;

import com.squareup.javapoet.MethodSpec;

import java.lang.reflect.Field;

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
        String name = buildSourceName(builder, fieldName);
        builder.addCode("return ");
        boolean wrapper = !mElement.getReturnType().toString().startsWith(Field.class.getName());
        if (wrapper) {
            builder.addCode("new $L(findFieldExactIfExists(requireSourceClass(),",
                    mElement.getReturnType());
        } else {
            builder.addCode("field(");
        }
        TypeMirror fieldType = getMirrorClass(mElement, FindField.class, "type");
        if (fieldType == null) {
            builder.addCode("null");
        } else {
            builder.addCode("$T.class", fieldType);
        }
        builder.addCode(",$L)", name);
        if (wrapper) {
            builder.addCode(",requireOriginal())");
        }
        return builder.addCode(";\n");
    }
}
