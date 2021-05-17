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

import reflectx.annotations.Source;
import reflectx.annotations.SourceName;
import reflectx.annotations.SourceNames;
import reflectx.annotations.Sources;
import reflectx.mapping.SourceMapping;

/**
 * @author zpp0196
 */
abstract class BaseProxyMethod {

    final ExecutableElement mElement;
    private final reflectx.compiler.TryCatchBlock mCatchBlock;

    BaseProxyMethod(Element element) {
        mElement = (ExecutableElement) element;
        mCatchBlock = new reflectx.compiler.TryCatchBlock(mElement);
    }

    MethodSpec.Builder buildMethodSpec() {
        MethodSpec.Builder builder = MethodSpec.overriding(mElement);
        mCatchBlock.start(builder);
        return builder;
    }

    String buildSourceName(MethodSpec.Builder builder, String defaultName) {
        Source source = mElement.getAnnotation(Source.class);
        Sources sources = mElement.getAnnotation(Sources.class);
        SourceName sourceName = mElement.getAnnotation(SourceName.class);
        SourceNames sourceNames = mElement.getAnnotation(SourceNames.class);
        String name = "name$" + System.currentTimeMillis();
        List<SourceMapping> mappingList = new ArrayList<>();
        if (sources != null) {
            for (Source s : sources.value()) {
                mappingList.add(new SourceMapping(s));
            }
        } else if (source != null) {
            mappingList.add(new SourceMapping(source));
        } else if (sourceNames != null) {
            for (SourceName sn : sourceNames.value()) {
                mappingList.add(new SourceMapping(sn));
            }
        } else if (sourceName != null) {
            mappingList.add(new SourceMapping(sourceName));
        }
        String listName = null;
        for (SourceMapping sourceMapping : mappingList) {
            if (sourceMapping == null) {
                continue;
            }
            if (listName == null) {
                listName = "mappings";
                builder.addStatement("$T<$T> $L = new $T<>()", List.class,
                        SourceMapping.class, listName, ArrayList.class);
            }
            Object identifies = sourceMapping.isDefaultIdentifies() ? null :
                    sourceMapping.identifies + "L";
            builder.addStatement("$L.add(new $T($LL, $S, $L))", listName, SourceMapping.class,
                    sourceMapping.version, sourceMapping.value, identifies);
        }
        if (listName == null) {
            builder.addStatement("$T $L = $S", String.class, name, defaultName);
        } else {
            builder.addStatement("$T $L = getSourceName($S, $L)", String.class,
                    name, defaultName, listName);
        }
        return name;
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

    List<?> getMirrorClassArray(Element element, Class<?> annotationClass, String key) {
        AnnotationMirror annotationMirror = getAnnotationMirror(element, annotationClass);
        if (annotationMirror == null) {
            return new ArrayList<>();
        }
        AnnotationValue annotationValue = getAnnotationValue(annotationMirror, key);
        return annotationValue == null ? new ArrayList<>() : (List<?>) annotationValue.getValue();
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
