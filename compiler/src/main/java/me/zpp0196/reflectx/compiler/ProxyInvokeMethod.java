package me.zpp0196.reflectx.compiler;

import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import me.zpp0196.reflectx.proxy.MethodParameters;
import me.zpp0196.reflectx.proxy.Source;

/**
 * @author zpp0196
 */
class ProxyInvokeMethod extends BaseProxyMethod {

    ProxyInvokeMethod(Element element) {
        super(element);
    }

    @Override
    public MethodSpec.Builder buildMethodSpec() {
        MethodSpec.Builder builder = super.buildMethodSpec();
        Source source = mElement.getAnnotation(Source.class);
        MethodParameters srcParameters = mElement.getAnnotation(MethodParameters.class);
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
            List memberParameters = getMirrorClassArray(mElement, MethodParameters.class, "value");
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
}
