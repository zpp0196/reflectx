package reflectx.compiler;

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

import reflectx.IProxy;
import reflectx.IProxyClass;
import reflectx.annotations.FindConstructor;
import reflectx.annotations.FindField;
import reflectx.annotations.FindMethod;
import reflectx.annotations.GetField;
import reflectx.annotations.SetField;

/**
 * @author zpp0196
 */
class ProxyClassImplImpl {

    private TypeElement mClassElement;

    private List<BaseProxyMethod> proxyMethods;

    private Elements mElements;

    ProxyClassImplImpl(Element element, Elements elements) {
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
            } else if (methodElement.getAnnotation(SetField.class) != null) {
                proxyMethods.add(new SetFieldImpl(methodElement));
            } else if (methodElement.getAnnotation(GetField.class) != null) {
                proxyMethods.add(new GetFieldImpl(methodElement));
            } else if (methodElement.getAnnotation(FindField.class) != null) {
                proxyMethods.add(new FindFieldImpl(methodElement));
            } else if (methodElement.getAnnotation(FindMethod.class) != null) {
                proxyMethods.add(new FindMethodImpl(methodElement));
            } else if (methodElement.getAnnotation(FindConstructor.class) != null) {
                proxyMethods.add(new FindConstructorImpl(methodElement));
            } else {
                proxyMethods.add(new CallMethodImpl(methodElement));
            }
        }
    }

    JavaFile generateProxy(TypeName proxyClassImpl, ProxyClassMappingImpl mappingClass) {
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
            if (parent.toString().equals(IProxyClass.class.getName()) ||
                    parent.toString().equals(IProxy.class.getName())) {
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

        mappingClass.addMapping(mClassElement, sourceInterface.toString(),
                proxyClassName.toString() + "$Proxy");
        return JavaFile.builder(packageName, proxyClass).build();
    }

    private List<MethodSpec> buildMethods() {
        List<MethodSpec> methods = new ArrayList<>();
        for (BaseProxyMethod proxyMethod : proxyMethods) {
            methods.add(proxyMethod.buildMethodSpecWithCatch().build());
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
