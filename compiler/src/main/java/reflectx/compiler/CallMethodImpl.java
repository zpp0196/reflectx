package reflectx.compiler;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;

import reflectx.annotations.CallMethod;

/**
 * @author zpp0196
 */
class CallMethodImpl extends BaseProxyMethod {

    CallMethodImpl(Element element) {
        super(element);
    }

    @Override
    MethodSpec.Builder buildMethodSpec() {
        MethodSpec.Builder builder = super.buildMethodSpec();
        CallMethod annotation = mElement.getAnnotation(CallMethod.class);
        String methodName;
        if (annotation != null && !annotation.value().isEmpty()) {
            methodName = annotation.value();
        } else {
            methodName = mElement.getSimpleName().toString();
        }
        Object returnType = mElement.getReturnType();

        if (returnType.toString().equals("void")) {
            builder.addCode("call(null");
        } else {
            if (returnType instanceof DeclaredType) {
                returnType = ((DeclaredType) returnType).asElement();
            }
            builder.addCode("return call(").addCode(CodeBlock.of("$L.class", returnType.toString()));
        }

        builder.addCode(",$S,new Class[]{", methodName);
        List parameterTypes = getMirrorClassArray(mElement, CallMethod.class, "parameterTypes");
        if (parameterTypes.size() > 0) {
            for (int i = 0; i < parameterTypes.size(); i++) {
                builder.addCode(parameterTypes.get(i).toString());
                if (i != parameterTypes.size() - 1) {
                    builder.addCode(",");
                }
            }
        } else {
            List<? extends VariableElement> parameters = mElement.getParameters();
            for (int i = 0; i < parameters.size(); i++) {
                VariableElement parameter = parameters.get(i);
                builder.addCode("$T.class", parameter.asType());
                if (i != parameters.size() - 1) {
                    builder.addCode(",");
                }
            }
        }
        builder.addCode("}");
        List<? extends VariableElement> parameters = mElement.getParameters();
        if (parameters.size() > 0) {
            for (int i = 0; i < parameters.size(); i++) {
                builder.addCode(",");
                VariableElement parameter = parameters.get(i);
                builder.addCode(parameter.toString());
            }
        }
        return builder.addCode(");\n");
    }
}
