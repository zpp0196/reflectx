package me.zpp0196.reflectx.compiler;

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

import me.zpp0196.reflectx.proxy.ConstructorGetter;
import me.zpp0196.reflectx.proxy.FieldGetter;
import me.zpp0196.reflectx.proxy.IProxyClass;
import me.zpp0196.reflectx.proxy.MethodGetter;
import me.zpp0196.reflectx.proxy.ProxyGetter;
import me.zpp0196.reflectx.proxy.ProxySetter;

/**
 * @author zpp0196
 */
class ProxyImplClass {

    private TypeElement mClassElement;

    private List<BaseProxyMethod> proxyMethods;

    private Elements mElements;

    ProxyImplClass(Element element, Elements elements) {
        this.mClassElement = (TypeElement) element;
        this.proxyMethods = new LinkedList<>();
        this.mElements = elements;
        for (Element methodElement : element.getEnclosedElements()) {
            if (methodElement.getKind() != ElementKind.METHOD) {
                continue;
            }
            if (methodElement.getModifiers().contains(Modifier.DEFAULT) ||
                    methodElement.getModifiers().contains(Modifier.STATIC)) {
                //noinspection UnnecessaryContinue
                continue;
            } else if (methodElement.getAnnotation(ProxySetter.class) != null) {
                proxyMethods.add(new ProxySetterMethod(methodElement));
            } else if (methodElement.getAnnotation(ProxyGetter.class) != null) {
                proxyMethods.add(new ProxyGetterMethod(methodElement));
            } else if (methodElement.getAnnotation(FieldGetter.class) != null) {
                proxyMethods.add(new FieldInfoMethod(methodElement));
            } else if (methodElement.getAnnotation(MethodGetter.class) != null) {
                proxyMethods.add(new MethodInfoMethod(methodElement));
            } else if (methodElement.getAnnotation(ConstructorGetter.class) != null) {
                proxyMethods.add(new ConstructorInfoMethod(methodElement));
            } else {
                proxyMethods.add(new ProxyInvokeMethod(methodElement));
            }
        }
    }

    JavaFile generateProxy(TypeName proxyClassImpl, ProxyClassMappingClass mappingClass) {
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

        mappingClass.addMapping(sourceInterface.toString(),
                proxyClassName.toString() + "$Proxy");
        return JavaFile.builder(packageName, proxyClass).build();
    }

    private List<MethodSpec> buildMethods() {
        List<MethodSpec> methods = new ArrayList<>();
        for (BaseProxyMethod getter : proxyMethods) {
            methods.add(getter.buildMethodSpecWithCatch().build());
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
