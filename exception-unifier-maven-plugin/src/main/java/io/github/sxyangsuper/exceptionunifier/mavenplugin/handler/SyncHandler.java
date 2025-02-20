package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.asm.ModifyExceptionEnumClassVisitor;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.source.IExceptionCodeSource;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.util.MojoConfigurationUtil;
import org.apache.maven.plugin.MojoExecutionException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SyncHandler {
    private final IExceptionCodeSource exceptionSource;
    private final CheckResult checkResult;
    private final MojoConfiguration mojoConfiguration;

    public SyncHandler(final IExceptionCodeSource exceptionSource,
                       final CheckResult checkResult,
                       final MojoConfiguration mojoConfiguration) {
        this.exceptionSource = exceptionSource;
        this.checkResult = checkResult;
        this.mojoConfiguration = mojoConfiguration;
    }

    public SyncResult syncExceptionEnums() throws MojoExecutionException {
        final List<ExceptionEnum> exceptionEnums = this.checkResult.getExceptionEnums();

        if (CollUtil.isEmpty(exceptionEnums)) {
            return null;
        }

        final SyncResult syncResult = new SyncResult();
        syncResult.setModuleId(MojoConfigurationUtil.getModuleId(this.mojoConfiguration));
        final List<SyncedExceptionSource> syncedExceptionSources = new ArrayList<>(exceptionEnums.size());
        syncResult.setSyncedExceptionSources(syncedExceptionSources);

        final String exceptionCodePrefix = this.exceptionSource.getExceptionCodePrefix();

        for (final ExceptionEnum exceptionEnum : exceptionEnums) {
            syncedExceptionSources.add(this.modifyExceptionEnum(exceptionEnum, exceptionCodePrefix));
        }

        return syncResult;
    }

    private SyncedExceptionSource modifyExceptionEnum(final ExceptionEnum exceptionEnum, final String exceptionCodePrefix) throws MojoExecutionException {
        final ClassReader classReader;
        try (BufferedInputStream inputStream = FileUtil.getInputStream(exceptionEnum.getClassFilePath())) {
            classReader = new ClassReader(inputStream);
        } catch (IOException e) {
            throw new MojoExecutionException(
                    String.format("Fail create class reader for %s", exceptionEnum),
                    e
            );
        }

        final AtomicReference<SyncedExceptionSource> syncedExceptionSourceReference = new AtomicReference<>();

        final ModifyExceptionEnumClassVisitor modifyEnumClassVisitor = new ModifyExceptionEnumClassVisitor(Opcodes.ASM8, exceptionEnum, exceptionCodePrefix, classReader) {
            @Override
            protected void setSyncedExceptionSource(final SyncedExceptionSource syncedExceptionSource) {
                syncedExceptionSourceReference.set(syncedExceptionSource);
            }
        };
        classReader.accept(modifyEnumClassVisitor, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
        FileUtil.writeBytes(modifyEnumClassVisitor.toByteArray(), exceptionEnum.getClassFilePath());
        return syncedExceptionSourceReference.get();
    }
}
