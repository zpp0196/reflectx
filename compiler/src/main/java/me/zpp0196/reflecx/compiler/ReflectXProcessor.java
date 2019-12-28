package me.zpp0196.reflecx.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import me.zpp0196.reflectx.proxy.BaseProxyClass;
import me.zpp0196.reflectx.proxy.IProxyCallback;
import me.zpp0196.reflectx.proxy.ProxyClass;
import me.zpp0196.reflectx.proxy.ProxyClassImpl;
import me.zpp0196.reflectx.proxy.ProxyClassMapping;
import me.zpp0196.reflectx.proxy.Source;
import me.zpp0196.reflectx.proxy.SourceClass;
import me.zpp0196.reflecx.compiler.model.ProxyImplClass;
import me.zpp0196.reflecx.compiler.model.ProxyClassMappingClass;

public class ReflectXProcessor extends AbstractProcessor {

    private Elements mElements;
    private Filer mFiler;
    private ProxyClassMappingClass mProxyClassMappingClass;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElements = processingEnvironment.getElementUtils();
        mFiler = processingEnvironment.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> types = new LinkedHashSet<>();
        types.add(ProxyClassImpl.class.getCanonicalName());
        types.add(ProxyClassMapping.class.getCanonicalName());
        types.add(Source.class.getCanonicalName());
        types.add(SourceClass.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try {
            TypeName proxyClassImpl = getProxyClassImpl(roundEnvironment);
            if (mProxyClassMappingClass == null) {
                String proxyMapping = getProxyMapping(roundEnvironment);
                mProxyClassMappingClass = new ProxyClassMappingClass(proxyMapping);
            }
            if (processProxyClass(roundEnvironment, proxyClassImpl)) {
                mProxyClassMappingClass.write(mFiler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private TypeName getProxyClassImpl(RoundEnvironment env) {
        TypeName defImpl = ClassName.get(BaseProxyClass.class);
        Set<? extends Element> elements = env.getElementsAnnotatedWith(ProxyClassImpl.class);
        if (elements.size() > 0) {
            for (Element element : elements) {
                if (element.getSimpleName().contentEquals(defImpl.toString())) {
                    continue;
                }
                TypeElement typeElement = (TypeElement) element;
                defImpl = ClassName.get(typeElement);
            }
        }
        return defImpl;
    }

    private String getProxyMapping(RoundEnvironment env) {
        String defImpl = ProxyClass.DEFAULT_MAPPING;
        Set<? extends Element> elements = env.getElementsAnnotatedWith(ProxyClassMapping.class);
        if (elements.size() > 0) {
            for (Element element : elements) {
                if (element.getSimpleName().contentEquals(defImpl)) {
                    continue;
                }
                defImpl = element.getAnnotation(ProxyClassMapping.class).value();
            }
        }
        return defImpl;
    }

    private boolean processProxyClass(RoundEnvironment env, TypeName proxyClassImpl) throws IOException {
        Set<Element> elements = new HashSet<>();
        elements.addAll(env.getElementsAnnotatedWith(Source.class));
        elements.addAll(env.getElementsAnnotatedWith(SourceClass.class));
        proxyClass:
        for (Element element : elements) {
            if (element.getKind() == ElementKind.METHOD) {
                continue;
            }
            TypeElement typeElement = (TypeElement) element;
            List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
            if (interfaces.size() != 0) {
                for (TypeMirror anInterface : interfaces) {
                    if (anInterface.toString().equals(IProxyCallback.class.getCanonicalName())) {
                        continue proxyClass;
                    }
                }
            }
            new ProxyImplClass(element, mElements)
                    .generateProxy(proxyClassImpl, mProxyClassMappingClass)
                    .writeTo(mFiler);
        }
        return elements.size() > 0;
    }
}
