package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.asm;

import cn.hutool.core.map.MapUtil;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.IllegalConstructorImplementationException;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.InvalidExceptionEnumException;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.asm.ReadExceptionEnumClassVisitor.FIELD_NAME_CODE;
import static io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.asm.ReadExceptionEnumClassVisitor.FIELD_NAME_MESSAGE;

public abstract class ReadReservedFieldIndexInEnumConstructorForInitMethodNode extends MethodNode {
    private static final List<String> RESERVED_FIELD_NAMES = Arrays.asList(FIELD_NAME_CODE, FIELD_NAME_MESSAGE);
    private final String sourceClassQualifiedName;

    public ReadReservedFieldIndexInEnumConstructorForInitMethodNode(final int api,
                                                                    final int access,
                                                                    final String name,
                                                                    final String descriptor,
                                                                    final String signature,
                                                                    final String[] exceptions,
                                                                    final String sourceClassQualifiedName) {
        super(api, access, name, descriptor, signature, exceptions);
        this.sourceClassQualifiedName = sourceClassQualifiedName;
    }

    abstract protected void setReservedFieldNameToConstructorParametersIndexes(Map<String, Integer> reservedFieldNameToConstructorParametersIndexes);

    abstract protected void setEnumConstructorParameterCount(int enumConstructorParameterCount);

    @Override
    public void visitEnd() {
        super.visitEnd();

        final Map<String, Integer> reservedFieldNameToConstructorParametersIndexes = new HashMap<>(RESERVED_FIELD_NAMES.size());
        final int[] totalConstructorParameterCount = {0};
        totalConstructorParameterCount[0] = Type.getArgumentCount(this.desc);
        for (final AbstractInsnNode instruction : this.instructions) {
            if (instruction instanceof FieldInsnNode && instruction.getOpcode() == Opcodes.PUTFIELD) {
                final FieldInsnNode putFieldInsnNode = (FieldInsnNode) instruction;

                if (!RESERVED_FIELD_NAMES.contains(putFieldInsnNode.name)) {
                    continue;
                }

                final VarInsnNode previousVarInsnNode = getVarInsnNode(instruction, putFieldInsnNode);
                // enum name and index take the first two index
                reservedFieldNameToConstructorParametersIndexes.put(putFieldInsnNode.name, previousVarInsnNode.var);
            }
        }
        
        if (reservedFieldNameToConstructorParametersIndexes.get(FIELD_NAME_CODE) == null) {
            throw new InvalidExceptionEnumException(
                    String.format("Invalid exception enum, value of reserved field %s should be set by constructor", FIELD_NAME_CODE)
            );
        }

        if (reservedFieldNameToConstructorParametersIndexes.get(FIELD_NAME_MESSAGE) == null) {
            throw new InvalidExceptionEnumException(
                    String.format("Invalid exception enum, value of reserved field %s should be set by constructor", FIELD_NAME_MESSAGE)
            );
        }

        this.setReservedFieldNameToConstructorParametersIndexes(MapUtil.unmodifiable(reservedFieldNameToConstructorParametersIndexes));
        this.setEnumConstructorParameterCount(totalConstructorParameterCount[0]);
    }

    private @NotNull VarInsnNode getVarInsnNode(final AbstractInsnNode instruction, final FieldInsnNode putFieldInsnNode) {
        final AbstractInsnNode previous = instruction.getPrevious();
        if (!(previous instanceof VarInsnNode)) {
            throw new IllegalConstructorImplementationException(
                    String.format(
                            "Illegal constructor for class %s, %s value should be one of constructor argument",
                            this.sourceClassQualifiedName,
                            putFieldInsnNode.name
                    )
            );
        }
        return (VarInsnNode) previous;
    }
}
