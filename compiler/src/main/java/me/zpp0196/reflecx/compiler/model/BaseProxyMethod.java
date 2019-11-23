package me.zpp0196.reflecx.compiler.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

/**
 * @author zpp0196
 */
public abstract class BaseProxyMethod {

    ExecutableElement mElement;

    BaseProxyMethod(Element element) {
        mElement = (ExecutableElement) element;
    }

    public MethodSpec.Builder buildMethodSpec() {
        List<? extends VariableElement> parameters = mElement.getParameters();
        MethodSpec.Builder builder = MethodSpec.methodBuilder(mElement.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class);
        for (VariableElement parameter : parameters) {
            builder.addParameter(ClassName.get(parameter.asType()),
                    parameter.getSimpleName().toString());
        }
        builder.returns(ClassName.get(mElement.getReturnType()));
        return builder;
    }
}
