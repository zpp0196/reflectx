package reflectx.compiler;

import com.squareup.javapoet.MethodSpec;

import java.lang.reflect.Constructor;
import java.util.List;

import javax.lang.model.element.Element;

import reflectx.annotations.FindConstructor;

/**
 * @author zpp0196
 */
class FindConstructorImpl extends BaseProxyMethod {

    FindConstructorImpl(Element element) {
        super(element);
    }

    @Override
    MethodSpec.Builder buildMethodSpec() {
        MethodSpec.Builder builder = super.buildMethodSpec();
        builder.addCode("return ");
        boolean wrapper = !mElement.getReturnType().toString()
                .startsWith(Constructor.class.getName());
        if (wrapper) {
            builder.addCode("new $L(findConstructorIfExists(requireSourceClass()," +
                    "new Class[]{", mElement.getReturnType());
        } else {
            builder.addCode("constructor(new Class[]{");
        }
        List parameterTypes = getMirrorClassArray(mElement, FindConstructor.class, "value");
        for (int i = 0; i < parameterTypes.size(); i++) {
            builder.addCode(parameterTypes.get(i).toString());
            if (i != parameterTypes.size() - 1) {
                builder.addCode(",");
            }
        }
        builder.addCode("})");
        if (wrapper) {
            builder.addCode(")");
        }
        return builder.addCode(";\n");
    }
}
