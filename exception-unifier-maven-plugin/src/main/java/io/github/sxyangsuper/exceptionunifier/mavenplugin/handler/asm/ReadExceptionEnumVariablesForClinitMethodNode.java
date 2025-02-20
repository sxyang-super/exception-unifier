package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.asm;

import cn.hutool.core.collection.ListUtil;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.ExceptionEnumVariable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.asm.ReadExceptionEnumClassVisitor.FIELD_NAME_CODE;
import static io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.asm.ReadExceptionEnumClassVisitor.FIELD_NAME_MESSAGE;

public abstract class ReadExceptionEnumVariablesForClinitMethodNode extends MethodNode {
    // index starts from 1
    private static final int VARIABLE_NAME_CONSTRUCTOR_PARAMETER_INDEX = 1;
    private final Map<String, Integer> reservedFieldNameToConstructorParametersIndexes;
    private final int enumConstructorParameterCount;

    public ReadExceptionEnumVariablesForClinitMethodNode(
            final int api,
            final int access,
            final String name,
            final String descriptor,
            final String signature,
            final String[] exceptions,
            final Map<String, Integer> reservedFieldNameToConstructorParametersIndexes,
            final int enumConstructorParameterCount
    ) {
        super(api, access, name, descriptor, signature, exceptions);
        this.reservedFieldNameToConstructorParametersIndexes = reservedFieldNameToConstructorParametersIndexes;
        this.enumConstructorParameterCount = enumConstructorParameterCount;
    }

    protected abstract void setVariables(List<ExceptionEnumVariable> variables);

    @Override
    public void visitEnd() {
        super.visitEnd();

        final List<ExceptionEnumVariable> variables = new ArrayList<>();

        this.instructions.forEach(instruction -> {
            if (instruction instanceof MethodInsnNode) {
                final MethodInsnNode methodInsnNode = (MethodInsnNode) instruction;
                final ExceptionEnumVariable exceptionEnumVariable = new ExceptionEnumVariable();
                this.fillEnumVariable(methodInsnNode, VARIABLE_NAME_CONSTRUCTOR_PARAMETER_INDEX, exceptionEnumVariable::setName);
                this.fillEnumVariable(methodInsnNode, reservedFieldNameToConstructorParametersIndexes.get(FIELD_NAME_CODE), exceptionEnumVariable::setCode);
                this.fillEnumVariable(methodInsnNode, reservedFieldNameToConstructorParametersIndexes.get(FIELD_NAME_MESSAGE), exceptionEnumVariable::setMessage);
                variables.add(exceptionEnumVariable);
            }
        });

        this.setVariables(ListUtil.unmodifiable(variables));
    }

    private void fillEnumVariable(final MethodInsnNode initMethodInsnNode,
                                  final int fieldCorrespondingConstructorParameterIndex,
                                  final Consumer<String> setValue) {
        AbstractInsnNode abstractInsnNode = initMethodInsnNode;
        for (int goPreviousStep = enumConstructorParameterCount - fieldCorrespondingConstructorParameterIndex + 1; goPreviousStep > 0; goPreviousStep--) {
            abstractInsnNode = abstractInsnNode.getPrevious();
        }

        final LdcInsnNode ldcInsnNode = (LdcInsnNode) abstractInsnNode;
        setValue.accept((String) ldcInsnNode.cst);
    }
}
