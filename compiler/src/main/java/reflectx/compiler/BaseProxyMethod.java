package reflectx.compiler;

import com.squareup.javapoet.MethodSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

/**
 * @author zpp0196
 */
abstract class BaseProxyMethod {

    ExecutableElement mElement;
    private reflectx.compiler.TryCatchBlock mCatchBlock;

    BaseProxyMethod(Element element) {
        mElement = (ExecutableElement) element;
        mCatchBlock = new reflectx.compiler.TryCatchBlock(mElement);
    }

    MethodSpec.Builder buildMethodSpec() {
        MethodSpec.Builder builder = MethodSpec.overriding(mElement);
        mCatchBlock.start(builder);
        return builder;
    }

    MethodSpec.Builder buildMethodSpecWithCatch() {
        MethodSpec.Builder builder = buildMethodSpec();
        mCatchBlock.end(builder);
        return builder;
    }

    TypeMirror getMirrorClass(Element element, Class<?> annotationClass, String key) {
        AnnotationMirror annotationMirror = getAnnotationMirror(element, annotationClass);
        if (annotationMirror == null) {
            return null;
        }
        AnnotationValue annotationValue = getAnnotationValue(annotationMirror, key);
        return annotationValue == null ? null : (TypeMirror) annotationValue.getValue();
    }

    List getMirrorClassArray(Element element, Class<?> annotationClass, String key) {
        AnnotationMirror annotationMirror = getAnnotationMirror(element, annotationClass);
        if (annotationMirror == null) {
            return new ArrayList();
        }
        AnnotationValue annotationValue = getAnnotationValue(annotationMirror, key);
        return annotationValue == null ? new ArrayList() : (List) annotationValue.getValue();
    }

    String getTypeString(Object type) {
        if (type == null) {
            return void.class.toString();
        }
        if (type instanceof DeclaredType) {
            type = ((DeclaredType) type).asElement();
        }
        return type.toString();
    }

    private AnnotationMirror getAnnotationMirror(Element element, Class<?> clazz) {
        String clazzName = clazz.getName();
        for (AnnotationMirror m : element.getAnnotationMirrors()) {
            if (m.getAnnotationType().toString().equals(clazzName)) {
                return m;
            }
        }
        return null;
    }

    private AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String key) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
                annotationMirror.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
