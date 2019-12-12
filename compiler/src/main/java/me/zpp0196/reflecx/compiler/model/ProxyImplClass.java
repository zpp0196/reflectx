package me.zpp0196.reflecx.compiler.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import me.zpp0196.reflectx.proxy.IProxyClass;
import me.zpp0196.reflectx.proxy.ProxyGetter;
import me.zpp0196.reflectx.proxy.ProxySetter;

/**
 * @author zpp0196
 */
public class ProxyImplClass {

    private TypeElement mClassElement;

    private List<BaseProxyMethod> proxyMethods;

    private Elements mElements;

    public ProxyImplClass(Element element, Elements elements) {
        this.mClassElement = (TypeElement) element;
        this.proxyMethods = new LinkedList<>();
        this.mElements = elements;
        for (Element enclosedElement : element.getEnclosedElements()) {
            if (enclosedElement.getKind() != ElementKind.METHOD) {
                continue;
            }
            if (enclosedElement.getAnnotation(ProxySetter.class) != null) {
                proxyMethods.add(new ProxySetterMethod(enclosedElement));
            } else if (enclosedElement.getAnnotation(ProxyGetter.class) != null) {
                proxyMethods.add(new ProxyGetterMethod(enclosedElement));
            } else if (enclosedElement.getModifiers().contains(Modifier.STATIC)){
                //noinspection UnnecessaryContinue
                continue;
            } else {
                proxyMethods.add(new ProxyInvokeMethod(enclosedElement));
            }
        }
    }

    public JavaFile generateProxy(TypeName proxyClassImpl, ProxyMappingClass proxyMappingClass) {
        String packageName = getPackageName(mClassElement);
        String className = getClassName(mClassElement, packageName);
        ClassName proxyClassName = ClassName.get(packageName, className);
        ClassName sourceInterface = ClassName.get(packageName,
                className.replaceAll("\\$", "."));
        String proxyName = proxyClassName.simpleName() + "$Proxy";

        TypeSpec.Builder proxyClassBuilder = TypeSpec.classBuilder(proxyName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(sourceInterface);
        if (mClassElement.getInterfaces().size() > 0) {
            TypeMirror parent = mClassElement.getInterfaces().get(0);
            if (parent.toString().equals(IProxyClass.class.getName())) {
                proxyClassBuilder.superclass(proxyClassImpl);
            } else {
                String superClass = parent.toString() + "$Proxy";
                String parentPackage = superClass.substring(0, superClass.lastIndexOf("."));
                String parentClassName = superClass.substring(parentPackage.length() + 1);
                proxyClassBuilder.superclass(ClassName.get(parentPackage, parentClassName));
            }
        } else {
            proxyClassBuilder.superclass(proxyClassImpl);
        }

        proxyClassBuilder.addMethods(buildMethods());
        TypeSpec proxyClass = proxyClassBuilder.build();

        proxyMappingClass.addMapping(sourceInterface.toString(), proxyClassName.toString() + "$Proxy");
        return JavaFile.builder(packageName, proxyClass).build();
    }

    private List<MethodSpec> buildMethods() {
        List<MethodSpec> methods = new ArrayList<>();
        for (BaseProxyMethod getter : proxyMethods) {
            methods.add(getter.buildMethodSpec().build());
        }
        return methods;
    }

    private String getPackageName(TypeElement type) {
        return mElements.getPackageOf(type).getQualifiedName().toString();
    }

    private String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }
}
