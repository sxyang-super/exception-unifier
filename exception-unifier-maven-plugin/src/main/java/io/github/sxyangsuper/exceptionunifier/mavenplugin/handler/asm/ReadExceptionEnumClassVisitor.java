package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.asm;

import cn.hutool.core.map.MapUtil;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.Consts;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.ExceptionEnum;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.ExceptionEnumVariable;
import io.github.sxyangsuper.exceptionunifier.processor.ExceptionSource;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.Consts.ASM_API_VERSION;

public abstract class ReadExceptionEnumClassVisitor extends ClassVisitor {

    private static final String EXCEPTION_SOURCE_DESCRIPTOR = Type.getDescriptor(ExceptionSource.class);
    public static final String FIELD_NAME_CODE = "code";
    public static final String FIELD_NAME_MESSAGE = "message";

    /*default*/
    final ExceptionEnum exceptionEnum = new ExceptionEnum();
    /*default*/
    final AtomicReference<Map<String, Integer>> reservedFieldNameToConstructorParametersIndexesRef = new AtomicReference<>(MapUtil.empty());
    /*default*/
    final AtomicInteger enumConstructorParameterCountRef = new AtomicInteger(0);

    public ReadExceptionEnumClassVisitor(final int api) {
        super(api);
    }


    @Override
    public void visit(final int version,
                      final int access,
                      final String name,
                      final String signature,
                      final String superName,
                      final String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        exceptionEnum.setQualifiedClassName(name.replace("/", "."));
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        if (EXCEPTION_SOURCE_DESCRIPTOR.equals(descriptor)) {
            return new AnnotationVisitor(ASM_API_VERSION) {
                @Override
                public void visit(final String name, final Object value) {
                    super.visit(name, value);
                    exceptionEnum.setSource((String) value);
                }
            };
        }
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public MethodVisitor visitMethod(final int access,
                                     final String name,
                                     final String descriptor,
                                     final String signature,
                                     final String[] exceptions) {
        if (exceptionEnum.getSource() == null) {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        if (Consts.METHOD_NAME_INIT.equals(name)) {
            return new ReadReservedFieldIndexInEnumConstructorForInitMethodNode(ASM_API_VERSION, access, name, descriptor, signature, exceptions, this.exceptionEnum.getQualifiedClassName()) {
                @Override
                protected void setReservedFieldNameToConstructorParametersIndexes(
                        final Map<String, Integer> reservedFieldNameToConstructorParametersIndexes
                ) {
                    reservedFieldNameToConstructorParametersIndexesRef.set(reservedFieldNameToConstructorParametersIndexes);
                    exceptionEnum.setCodeConstructorParametersIndex(reservedFieldNameToConstructorParametersIndexes.get(FIELD_NAME_CODE));
                    exceptionEnum.setMessageConstructorParametersIndex(reservedFieldNameToConstructorParametersIndexes.get(FIELD_NAME_MESSAGE));
                }

                @Override
                protected void setEnumConstructorParameterCount(final int enumConstructorParameterCount) {
                    enumConstructorParameterCountRef.set(enumConstructorParameterCount);
                    exceptionEnum.setTotalConstructorParameterCount(enumConstructorParameterCount);
                }
            };
        }

        if (Consts.METHOD_NAME_CLINIT.equals(name)) {
            return new ReadExceptionEnumVariablesForClinitMethodNode(
                    ASM_API_VERSION,
                    access,
                    name,
                    descriptor,
                    signature,
                    exceptions,
                    reservedFieldNameToConstructorParametersIndexesRef.get(),
                    enumConstructorParameterCountRef.get()) {
                @Override
                protected void setVariables(final List<ExceptionEnumVariable> variables) {
                    exceptionEnum.setExceptionEnumVariables(variables);
                }
            };
        }

        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();

        this.setExceptionEnum(this.exceptionEnum);
    }

    protected abstract void setExceptionEnum(ExceptionEnum exceptionEnum);
}
