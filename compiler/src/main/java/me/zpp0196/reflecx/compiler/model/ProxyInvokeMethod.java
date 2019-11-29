package me.zpp0196.reflecx.compiler.model;

import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import me.zpp0196.reflectx.proxy.ProxyClass;
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
        String methodName = "";
        if (source != null) {
            methodName = source.value();
        }
        if (methodName.isEmpty()) {
            methodName = mElement.getSimpleName().toString();
        }
        TypeMirror returnType = mElement.getReturnType();
        String args = "new Object[0]";
        List<? extends VariableElement> parameters = mElement.getParameters();
        int size = parameters.size();
        if (size > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                VariableElement parameter = parameters.get(i);
                sb.append(parameter.getSimpleName().toString());
                if (i != size - 1) {
                    sb.append(",");
                }
            }
            args = sb.toString();
        }
        String statement = "call($L.class,\"$L\",$L)";
        Name qualifiedName = ((TypeElement) mElement.getEnclosingElement()).getQualifiedName();
        boolean isBuilder = qualifiedName.contentEquals(mElement.getReturnType().toString());
        if (!returnType.toString().equals("void") && !isBuilder) {
            statement = "return " + statement;
        }
        if (returnType instanceof DeclaredType) {
            Element element = ((DeclaredType) returnType).asElement();
            TypeElement typeElement = (TypeElement) element;
            builder.addStatement(statement, typeElement.getQualifiedName(), methodName, args);
        } else {
            builder.addStatement(statement, returnType, methodName, args);
        }
        if (isBuilder) {
            builder.addStatement("return this");
        }
        return builder;
    }
}
