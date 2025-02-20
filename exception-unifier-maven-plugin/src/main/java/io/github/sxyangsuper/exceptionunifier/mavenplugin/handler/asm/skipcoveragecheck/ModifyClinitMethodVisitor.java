package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.asm.skipcoveragecheck;

import cn.hutool.core.collection.CollUtil;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.ExceptionEnum;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.SyncedExceptionCode;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.github.sxyangsuper.exceptionunifier.base.Consts.EXCEPTION_CODE_SPLITTER;
import static io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.Consts.METHOD_NAME_INIT;

public abstract class ModifyClinitMethodVisitor extends MethodVisitor {
    private final List<Runnable> stashedExecutors = new ArrayList<>();
    private final List<Object> stashedExecutorCorrespondingLDCValues = new ArrayList<>();

    private final ExceptionEnum exceptionEnum;
    private final String exceptionCodePrefix;
    private final List<SyncedExceptionCode> syncedExceptionCodes;

    public ModifyClinitMethodVisitor(final int api, final MethodVisitor methodVisitor, final ExceptionEnum exceptionEnum, final String exceptionCodePrefix) {
        super(api, methodVisitor);
        this.exceptionEnum = exceptionEnum;
        this.exceptionCodePrefix = exceptionCodePrefix;
        this.syncedExceptionCodes = new ArrayList<>(exceptionEnum.getExceptionEnumVariables().size());
    }

    private void applyExecutors() {
        if (CollUtil.isNotEmpty(stashedExecutors)) {
            stashedExecutors.forEach(Runnable::run);
            CollUtil.clear(stashedExecutors);
            CollUtil.clear(stashedExecutorCorrespondingLDCValues);
        }
    }

    @Override
    public void visitInsn(final int opcode) {
        if (opcode == Opcodes.INVOKESPECIAL) {
            this.applyExecutors();
            this.mv.visitInsn(opcode);
        } else {
            stashedExecutors.add(() -> this.mv.visitInsn(opcode));
            stashedExecutorCorrespondingLDCValues.add(null);
        }
    }

    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        this.applyExecutors();
        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitVarInsn(final int opcode, final int varIndex) {
        this.applyExecutors();
        super.visitVarInsn(opcode, varIndex);
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        this.applyExecutors();
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String descriptor) {
        this.applyExecutors();
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitInvokeDynamicInsn(final String name, final String descriptor, final Handle bootstrapMethodHandle, final Object... bootstrapMethodArguments) {
        this.applyExecutors();
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }

    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        this.applyExecutors();
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(final Label label) {
        this.applyExecutors();
        super.visitLabel(label);
    }

    @Override
    public void visitIincInsn(final int varIndex, final int increment) {
        this.applyExecutors();
        super.visitIincInsn(varIndex, increment);
    }

    @Override
    public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label... labels) {
        this.applyExecutors();
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
        this.applyExecutors();
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(final String descriptor, final int numDimensions) {
        this.applyExecutors();
        super.visitMultiANewArrayInsn(descriptor, numDimensions);
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String descriptor, final boolean isInterface) {
        if (METHOD_NAME_INIT.equals(name)) {
            final int loadCodeIntoOperandStackExecutorIndex = stashedExecutors.size() - 1 - (exceptionEnum.getTotalConstructorParameterCount() - exceptionEnum.getCodeConstructorParametersIndex());
            final String originalCodeValue = (String) stashedExecutorCorrespondingLDCValues.get(loadCodeIntoOperandStackExecutorIndex);


            final int loadNameIntoOperandStackExecutorIndex = stashedExecutors.size() - 1 - (exceptionEnum.getTotalConstructorParameterCount() - 1);
            final String exceptionCodeName = (String) stashedExecutorCorrespondingLDCValues.get(loadNameIntoOperandStackExecutorIndex);
            final int loadMessageIntoOperandStackExecutorIndex = stashedExecutors.size() - 1 - (exceptionEnum.getTotalConstructorParameterCount() - exceptionEnum.getMessageConstructorParametersIndex());
            final String exceptionCodeMessage = (String) stashedExecutorCorrespondingLDCValues.get(loadMessageIntoOperandStackExecutorIndex);

            final SyncedExceptionCode syncedExceptionCode = new SyncedExceptionCode()
                    .setName(exceptionCodeName)
                    .setMessage(exceptionCodeMessage);

            final boolean isInSync = originalCodeValue.contains(EXCEPTION_CODE_SPLITTER);
            if (!isInSync) {
                final String code = String.join(EXCEPTION_CODE_SPLITTER,
                        exceptionCodePrefix,
                        exceptionEnum.getSource(),
                        originalCodeValue);
                stashedExecutors.set(loadCodeIntoOperandStackExecutorIndex, () -> mv.visitLdcInsn(code));
                syncedExceptionCode.setCode(code);
                syncedExceptionCode.setOriginalCode(originalCodeValue);
            } else {
                syncedExceptionCode.setCode(originalCodeValue);
                syncedExceptionCode.setOriginalCode(originalCodeValue.split(EXCEPTION_CODE_SPLITTER)[2]);
            }

            this.syncedExceptionCodes.add(syncedExceptionCode);
        }

        this.applyExecutors();
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitLdcInsn(final Object value) {
        stashedExecutors.add(() -> this.mv.visitLdcInsn(value));
        stashedExecutorCorrespondingLDCValues.add(value);
    }

    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        this.applyExecutors();
        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        this.setSyncedExceptionCodes(Collections.unmodifiableList(this.syncedExceptionCodes));
    }

    abstract protected void setSyncedExceptionCodes(List<SyncedExceptionCode> syncedExceptionCodes);
}
