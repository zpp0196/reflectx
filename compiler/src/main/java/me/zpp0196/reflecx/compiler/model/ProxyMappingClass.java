package me.zpp0196.reflecx.compiler.model;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import me.zpp0196.reflectx.Keep;
import me.zpp0196.reflectx.proxy.ProxyClass;

public class ProxyMappingClass {

    private String mClassName;
    private static final Map<String, String> mMapping = new HashMap<>();

    public ProxyMappingClass(String proxyMapping) {
        mClassName = proxyMapping;
        System.out.println(mClassName);
    }

    public void addMapping(String sourceInterface, String proxyName) {
        mMapping.put(sourceInterface, proxyName);
    }

    public void write(Filer filer) {
        String packageName = mClassName.substring(0, mClassName.lastIndexOf("."));
        String simpleName = mClassName.substring(packageName.length() + 1);
        TypeSpec.Builder classSpec = TypeSpec.classBuilder(simpleName)
                .addAnnotation(Keep.class)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ProxyClass.IMapping.class);

        FieldSpec mapField = FieldSpec.builder(ParameterizedTypeName.get(Map.class, Class.class, Class.class), "m")
                .initializer("new $T<>()", HashMap.class)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .build();
        classSpec.addField(mapField);

        CodeBlock.Builder staticInit = CodeBlock.builder();
        for (String key : mMapping.keySet()) {
            staticInit.addStatement("m.put($L.class,$L.class)", key, mMapping.get(key));
        }
        classSpec.addStaticBlock(staticInit.build());

        MethodSpec getMethod = MethodSpec.methodBuilder("get")
                .addAnnotation(Override.class)
                .returns(Class.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Class.class, "proxy")
                .addStatement("return m.get(proxy)")
                .build();
        classSpec.addMethod(getMethod);

        try {
            JavaFile.builder(packageName, classSpec.build()).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
