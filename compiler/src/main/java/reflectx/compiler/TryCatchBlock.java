package reflectx.compiler;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;

import java.util.function.Function;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import reflectx.annotations.RunWithCatch;

class TryCatchBlock {

    private ExecutableElement mElement;
    private RunWithCatch mCatch;
    private TypeMirror mReturnType;

    TryCatchBlock(ExecutableElement element) {
        this.mElement = element;
        mCatch = element.getAnnotation(RunWithCatch.class);
        mReturnType = element.getReturnType();
    }

    void start(MethodSpec.Builder builder) {
        if (mCatch == null) {
            return;
        }
        builder.addCode(CodeBlock.builder().add("try {\n").build());
    }

    void end(MethodSpec.Builder builder) {
        if (mCatch == null) {
            return;
        }
        builder.addCode(CodeBlock.builder().add("} catch (Throwable ignored) {\n}\n").build());
        TypeMirror returnType = mElement.getReturnType();
        if (returnType.toString().equals(void.class.getName())) {
            return;
        }
        if (appendReturn(builder, byte.class.getName(), RunWithCatch::byteValue) ||
                appendReturn(builder, short.class.getName(), RunWithCatch::shortValue) ||
                appendReturn(builder, int.class.getName(), RunWithCatch::intValue) ||
                appendReturn(builder, long.class.getName(), RunWithCatch::longValue) ||
                appendReturn(builder, float.class.getName(), RunWithCatch::floatValue) ||
                appendReturn(builder, double.class.getName(), RunWithCatch::doubleValue) ||
                appendReturn(builder, boolean.class.getName(), RunWithCatch::booleanValue) ||
                appendReturn(builder, char.class.getName(), RunWithCatch::charValue)) {
            return;
        }
        if (mReturnType.toString().equals(String.class.getName())) {
            builder.addStatement("return $S", mCatch.stringValue());
            return;
        }
        Name qualifiedName = ((TypeElement) mElement.getEnclosingElement()).getQualifiedName();
        if (qualifiedName.contentEquals(mElement.getReturnType().toString())) {
            builder.addStatement("return this");
        } else {
            builder.addStatement("return null");
        }
    }

    private <R> boolean appendReturn(MethodSpec.Builder builder, String typeName,
            Function<RunWithCatch, R> function) {
        if (mReturnType.toString().equals(typeName)) {
            builder.addStatement("return $L", function.apply(mCatch));
            return true;
        }
        return false;
    }
}
