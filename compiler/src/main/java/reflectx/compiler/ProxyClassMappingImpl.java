package reflectx.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import reflectx.BaseProxyClass;
import reflectx.Reflectx;
import reflectx.mapping.IProxyClassMapping;

class ProxyClassMappingImpl {

    private static final Map<String, String> mMapping = new HashMap<>();

    void addMapping(String sourceInterface, String proxyName) {
        mMapping.put(sourceInterface, proxyName);
    }

    void write(Filer filer) {
        String className = "ProxyClassMapping";
        TypeSpec.Builder classSpec = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(IProxyClassMapping.class);

        ParameterizedTypeName baseProxyClass = ParameterizedTypeName.get(ClassName.get(Class.class),
                WildcardTypeName.subtypeOf(TypeName.get(BaseProxyClass.class)));

        FieldSpec mapField = FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(Class.class), baseProxyClass), "m")
                .initializer("new $T<>()", HashMap.class)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .build();
        classSpec.addField(mapField);

        CodeBlock.Builder staticInit = CodeBlock.builder();
        for (String key : mMapping.keySet()) {
            staticInit.addStatement("m.put($L.class,$L.class)", key, mMapping.get(key));
        }
        classSpec.addStaticBlock(staticInit.build());

        MethodSpec.Builder initMethod = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addStatement("$T.setProxyClassMapping(new $L())", Reflectx.class, className);
        classSpec.addMethod(initMethod.build());

        MethodSpec getMethod = MethodSpec.methodBuilder("get")
                .addAnnotation(Override.class)
                .returns(baseProxyClass)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Class.class, "proxy")
                .addStatement("return m.get(proxy)")
                .build();
        classSpec.addMethod(getMethod);

        try {
            JavaFile.builder("reflectx.mapping", classSpec.build()).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
