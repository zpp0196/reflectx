package me.zpp0196.reflectx.compiler;

import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.Element;

import me.zpp0196.reflectx.proxy.ConstructorGetter;

/**
 * @author zpp0196
 */
class ConstructorInfoMethod extends BaseProxyMethod {

    ConstructorInfoMethod(Element element) {
        super(element);
    }

    @Override
    public MethodSpec.Builder buildMethodSpec() {
        String types;
        StringBuilder sb = new StringBuilder().append("new Class[]{");
        List parameters = getMirrorClassArray(mElement, ConstructorGetter.class, "value");
        for (int i = 0; i < parameters.size(); i++) {
            sb.append(parameters.get(i).toString());
            if (i != parameters.size() - 1) {
                sb.append(",");
            }
        }
        types = sb.append("}").toString();
        return super.buildMethodSpec().addStatement("return findConstructor($L)", types);
    }
}
