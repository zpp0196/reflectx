package reflectx.compiler;

import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import reflectx.annotations.SetField;

/**
 * @author zpp0196
 */
class SetFieldImpl extends BaseProxyMethod {

    SetFieldImpl(Element element) {
        super(element);
    }

    @Override
    MethodSpec.Builder buildMethodSpec() {
        MethodSpec.Builder builder = super.buildMethodSpec();
        if (mElement.getParameters().size() != 1) {
            throw new IllegalArgumentException("methods decorated by @SetField must only have one argument");
        }
        VariableElement value = mElement.getParameters().get(0);
        SetField getter = mElement.getAnnotation(SetField.class);
        String fieldName = getter.value();
        String parameterName = value.getSimpleName().toString();
        if (fieldName.isEmpty()) {
            fieldName = parameterName;
        }
        builder.addStatement("set($T.class,$S,$L)", value, fieldName, parameterName);
        Name qualifiedName = ((TypeElement) mElement.getEnclosingElement()).getQualifiedName();
        if (qualifiedName.contentEquals(mElement.getReturnType().toString())) {
            builder.addStatement("return this");
        }
        return builder;
    }
}
