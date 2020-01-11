package reflectx.compiler;

import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.Element;

import reflectx.annotations.FindMethod;

/**
 * @author zpp0196
 */
class FindMethodImpl extends BaseProxyMethod {

    FindMethodImpl(Element element) {
        super(element);
    }

    @Override
    MethodSpec.Builder buildMethodSpec() {
        MethodSpec.Builder builder = super.buildMethodSpec();
        builder.addCode("return method(");
        Object returnType = getMirrorClass(mElement, FindMethod.class, "returnType");
        if (returnType == null) {
            builder.addCode("null");
        } else {
            builder.addCode("$T.class", returnType);
        }
        FindMethod findMethod = mElement.getAnnotation(FindMethod.class);
        String methodName = findMethod.value();
        if (methodName.isEmpty()) {
            methodName = mElement.getSimpleName().toString();
        }
        builder.addCode(",$S,new Class[]{", methodName);
        List parameterTypes = getMirrorClassArray(mElement, FindMethod.class, "parameterTypes");
        for (int i = 0; i < parameterTypes.size(); i++) {
            builder.addCode(parameterTypes.get(i).toString());
            if (i != parameterTypes.size() - 1) {
                builder.addCode(",");
            }
        }
        return builder.addCode("});\n");
    }
}
