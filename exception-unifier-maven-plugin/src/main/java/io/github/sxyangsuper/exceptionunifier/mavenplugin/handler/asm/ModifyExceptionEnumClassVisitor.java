package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.asm;

import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.ExceptionEnum;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.SyncedExceptionCode;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.SyncedExceptionSource;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.asm.skipcoveragecheck.ModifyClinitMethodVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;

import static io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.Consts.ASM_API_VERSION;
import static io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.Consts.METHOD_NAME_CLINIT;

public abstract class ModifyExceptionEnumClassVisitor extends ClassVisitor {
    /*default*/
    final SyncedExceptionSource syncedExceptionSource = new SyncedExceptionSource();

    private final ExceptionEnum exceptionEnum;
    private final String exceptionCodePrefix;

    public ModifyExceptionEnumClassVisitor(final int api, final ExceptionEnum exceptionEnum, final String exceptionCodePrefix, final ClassReader classReader) {
        super(api);
        this.cv = new ClassWriter(classReader, ASM_API_VERSION);
        this.exceptionEnum = exceptionEnum;
        this.exceptionCodePrefix = exceptionCodePrefix;
        this.syncedExceptionSource.setSource(exceptionEnum.getSource());
    }


    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String descriptor, final String signature, final String[] exceptions) {
        final MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (METHOD_NAME_CLINIT.equals(name)) {
            return new ModifyClinitMethodVisitor(Opcodes.ASM8, methodVisitor, this.exceptionEnum, this.exceptionCodePrefix) {
                @Override
                protected void setSyncedExceptionCodes(final List<SyncedExceptionCode> syncedExceptionCodes) {
                    syncedExceptionSource.setSyncedExceptionCodes(syncedExceptionCodes);
                }
            };
        }
        return methodVisitor;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();

        this.setSyncedExceptionSource(this.syncedExceptionSource);
    }

    public byte[] toByteArray() {
        return ((ClassWriter) this.cv).toByteArray();
    }

    abstract protected void setSyncedExceptionSource(SyncedExceptionSource syncedExceptionSource);
}
