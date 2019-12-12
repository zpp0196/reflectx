package me.zpp0196.reflecx.compiler.model;

import com.squareup.javapoet.MethodSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import me.zpp0196.reflectx.proxy.MemberParameters;
import me.zpp0196.reflectx.proxy.Source;

/**
 * @author zpp0196
 */
public class ProxyInvokeMethod extends BaseProxyMethod {

    ProxyInvokeMethod(Element element) {
        super(element);
    }

    @Override
    public MethodSpec.Builder buildMethodSpec() {
        MethodSpec.Builder builder = super.buildMethodSpec();
        Source source = mElement.getAnnotation(Source.class);
        MemberParameters srcParameters = mElement.getAnnotation(MemberParameters.class);
        String methodName = "";
        if (source != null) {
            methodName = source.value();
        }
        if (methodName.isEmpty()) {
            methodName = mElement.getSimpleName().toString();
        }
        TypeMirror returnType = mElement.getReturnType();
        String types = "null";
        String args = "new Object[0]";
        List<? extends VariableElement> parameters = mElement.getParameters();
        int size = parameters.size();
        if (size > 0) {
            StringBuilder sbType = new StringBuilder().append("new Class[]{");
            StringBuilder sbName = new StringBuilder();
            for (int i = 0; i < size; i++) {
                VariableElement parameter = parameters.get(i);
                sbType.append(parameter.asType()).append(".class");
                sbName.append(parameter.getSimpleName().toString());
                if (i != size - 1) {
                    sbType.append(",");
                    sbName.append(",");
                }
            }
            types = sbType.append("}").toString();
            args = sbName.toString();
        }
        String statement = "call($L.class,\"$L\",$L,$L)";
        if (!returnType.toString().equals("void")) {
            statement = "return " + statement;
        }
        if (srcParameters != null) {
            StringBuilder sb = new StringBuilder().append("new Class[]{");
            List memberParameters = getMemberParameters(mElement);
            for (int i = 0; i < memberParameters.size(); i++) {
                sb.append(memberParameters.get(i).toString());
                if (i != memberParameters.size() - 1) {
                    sb.append(",");
                }
            }
            types = sb.append("}").toString();
        }
        if (returnType instanceof DeclaredType) {
            Element element = ((DeclaredType) returnType).asElement();
            TypeElement typeElement = (TypeElement) element;
            builder.addStatement(statement, typeElement.getQualifiedName(), methodName, types, args);
        } else {
            builder.addStatement(statement, returnType, methodName, types, args);
        }
        return builder;
    }

    private List getMemberParameters(Element element) {
        List typeMirrors = new ArrayList<>();
        try {
            AnnotationMirror annotationMirror = getAnnotationMirror(element, MemberParameters.class);
            AnnotationValue value = getAnnotationValue(annotationMirror, "value");
            typeMirrors = (List) value.getValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return typeMirrors;
    }

    private AnnotationMirror getAnnotationMirror(Element element, Class<?> clazz) {
        String clazzName = clazz.getName();
        for (AnnotationMirror m : element.getAnnotationMirrors()) {
            if (m.getAnnotationType().toString().equals(clazzName)) {
                return m;
            }
        }
        throw new IllegalArgumentException();
    }

    private AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String key) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
                annotationMirror.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals(key)) {
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException();
    }
}
