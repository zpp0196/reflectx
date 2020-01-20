package reflectx.compiler;

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

import reflectx.BaseProxyClass;
import reflectx.IProxyCallback;
import reflectx.annotations.ProxyClassImpl;
import reflectx.annotations.Source;
import reflectx.annotations.SourceClass;
import reflectx.annotations.SourceName;
import reflectx.annotations.SourceNames;
import reflectx.annotations.Sources;

public final class ReflectxProcessor extends AbstractProcessor {

    private Elements mElements;
    private Filer mFiler;
    private ProxyClassMappingImpl mProxyClassMappingImpl;

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
        types.add(Source.class.getCanonicalName());
        types.add(Sources.class.getCanonicalName());
        types.add(SourceName.class.getCanonicalName());
        types.add(SourceNames.class.getCanonicalName());
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
            if (mProxyClassMappingImpl == null) {
                mProxyClassMappingImpl = new ProxyClassMappingImpl();
            }
            if (processProxyClass(roundEnvironment, proxyClassImpl)) {
                mProxyClassMappingImpl.write(mFiler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private TypeName getProxyClassImpl(RoundEnvironment env) {
        TypeName className = ClassName.get(BaseProxyClass.class);
        Set<? extends Element> elements = env.getElementsAnnotatedWith(ProxyClassImpl.class);
        if (elements.size() > 0) {
            for (Element element : elements) {
                TypeElement typeElement = (TypeElement) element;
                className = ClassName.get(typeElement);
            }
        }
        return className;
    }

    private boolean processProxyClass(RoundEnvironment env, TypeName proxyClassImpl)
            throws IOException {
        Set<Element> elements = new HashSet<>();
        elements.addAll(env.getElementsAnnotatedWith(Source.class));
        elements.addAll(env.getElementsAnnotatedWith(Sources.class));
        elements.addAll(env.getElementsAnnotatedWith(SourceName.class));
        elements.addAll(env.getElementsAnnotatedWith(SourceNames.class));
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
            new ProxyClassImplImpl(element, mElements)
                    .generateProxy(proxyClassImpl, mProxyClassMappingImpl)
                    .writeTo(mFiler);
        }
        return elements.size() > 0;
    }
}
