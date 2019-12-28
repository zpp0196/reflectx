package me.zpp0196.reflectx.compiler.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

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

    protected TypeMirror getMirrorClass(Element element, Class<?> annotationClass, String key) {
        AnnotationMirror annotationMirror = getAnnotationMirror(element, annotationClass);
        AnnotationValue annotationValue = getAnnotationValue(annotationMirror, key);
        return annotationValue == null ? null:  (TypeMirror) annotationValue.getValue();
    }

    // TODO: 2019/12/12 0012 List<?>
    protected List getMirrorClassArray(Element element, Class<?> annotationClass, String key) {
        AnnotationMirror annotationMirror = getAnnotationMirror(element, annotationClass);
        AnnotationValue annotationValue = getAnnotationValue(annotationMirror, key);
        return annotationValue == null ? new ArrayList() : (List) annotationValue.getValue();
    }

    protected AnnotationMirror getAnnotationMirror(Element element, Class<?> clazz) {
        String clazzName = clazz.getName();
        for (AnnotationMirror m : element.getAnnotationMirrors()) {
            if (m.getAnnotationType().toString().equals(clazzName)) {
                return m;
            }
        }
        throw new IllegalArgumentException();
    }

    protected AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String key) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
                annotationMirror.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
