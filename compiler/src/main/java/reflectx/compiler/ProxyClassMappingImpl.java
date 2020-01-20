package reflectx.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

import reflectx.Reflectx;
import reflectx.annotations.SourceName;
import reflectx.annotations.SourceNames;
import reflectx.mapping.IProxyClassMapping;

class ProxyClassMappingImpl {

    private static final Map<String, String> mProxyClassMapping = new HashMap<>();
    private static final Map<String, Element> mElementMapping = new HashMap<>();

    void addMapping(Element element, String sourceInterface, String proxyName) {
        mProxyClassMapping.put(sourceInterface, proxyName);
        mElementMapping.put(sourceInterface, element);
    }

    void write(Filer filer) {
        String className = "ProxyClassMapping";
        TypeSpec.Builder classSpec = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(IProxyClassMapping.class);

        FieldSpec mapField = FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(Class.class), ClassName.get(IProxyClassMapping.Item.class)), "m")
                .initializer("new $T<>()", HashMap.class)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .build();
        classSpec.addField(mapField);

        CodeBlock.Builder staticInit = CodeBlock.builder();
        int i = 1;
        for (String key : mProxyClassMapping.keySet()) {
            Element element = mElementMapping.get(key);
            List<SourceName> sourceNameList;
            if (element.getAnnotation(SourceNames.class) != null) {
                sourceNameList = Arrays.asList(element.getAnnotation(SourceNames.class).value());
            } else {
                sourceNameList = Collections.singletonList(element.getAnnotation(SourceName.class));
            }
            String mapName = null;
            for (SourceName sn : sourceNameList) {
                if (sn == null) {
                    continue;
                }
                if (mapName == null) {
                    mapName = "m" + i;
                    staticInit.addStatement("Map<$T,$T> $L = new $T<>()",
                            Long.class, String.class, mapName, HashMap.class);
                    i++;
                }
                staticInit.addStatement("$L.put($LL, $S)",
                        mapName, sn.version(), sn.value());
            }
            staticInit.addStatement("m.put($L.class,new $T($L.class, $L))", key,
                    IProxyClassMapping.Item.class, mProxyClassMapping.get(key), mapName);
        }
        classSpec.addStaticBlock(staticInit.build());

        MethodSpec.Builder initMethod = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addStatement("$T.setProxyClassMapping(new $L())", Reflectx.class, className);
        classSpec.addMethod(initMethod.build());

        MethodSpec getMethod = MethodSpec.methodBuilder("get")
                .addAnnotation(Override.class)
                .returns(IProxyClassMapping.Item.class)
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
