package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import io.github.sxyangsuper.exceptionunifier.base.Consts;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.asm.ReadExceptionEnumClassVisitor;
import org.apache.maven.plugin.MojoExecutionException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CheckHandler {
    private final MojoConfiguration config;

    public CheckHandler(final MojoConfiguration config) {
        this.config = config;
    }

    public CheckResult checkExceptionEnums() throws MojoExecutionException {
        final String outputDirectory = this.config.getMavenProject().getBuild().getOutputDirectory();

        final List<ExceptionEnum> exceptionEnumClassFilePaths = new ArrayList<>();
        final Map<String, String> exceptionSourceConcatCodeToOwnerQualifiedNames = new HashMap<>();
        for (final File classFile : FileUtil.loopFiles(outputDirectory)) {
            final ExceptionEnum exceptionEnum = this.parse(classFile);
            if (exceptionEnum == null) {
                continue;
            }

            exceptionEnum.setClassFilePath(classFile.getAbsolutePath());

            for (final ExceptionEnumVariable exceptionEnumVariable : exceptionEnum.getExceptionEnumVariables()) {
                final String exceptionSourceConcatCode = String.join(Consts.EXCEPTION_CODE_SPLITTER, exceptionEnum.getSource(), exceptionEnumVariable.getCode());
                if (exceptionSourceConcatCodeToOwnerQualifiedNames.containsKey(exceptionSourceConcatCode)) {
                    final String errorMessage = StrUtil.equals(exceptionEnum.getQualifiedClassName(),
                            exceptionSourceConcatCodeToOwnerQualifiedNames.get(exceptionSourceConcatCode)) ? String.format(
                            "Duplicate exception code %s within %s",
                            exceptionSourceConcatCode,
                            exceptionEnum.getQualifiedClassName()
                    ) : String.format(
                            "Duplicate exception code %s between %s and %s",
                            exceptionSourceConcatCode,
                            exceptionEnum.getQualifiedClassName(),
                            exceptionSourceConcatCodeToOwnerQualifiedNames.get(exceptionSourceConcatCode)
                    );
                    throw new MojoExecutionException(
                            errorMessage
                    );
                }
                exceptionSourceConcatCodeToOwnerQualifiedNames.put(exceptionSourceConcatCode, exceptionEnum.getQualifiedClassName());
            }

            exceptionEnumClassFilePaths.add(exceptionEnum);
        }

        return new CheckResult()
                .setExceptionEnums(exceptionEnumClassFilePaths);
    }

    private ExceptionEnum parse(final File classFile) throws MojoExecutionException {
        final ClassReader classReader;
        try {
            classReader = new ClassReader(FileUtil.getInputStream(classFile));
        } catch (Exception e) {
            throw new MojoExecutionException("Fail to get input stream of file " + classFile.getAbsolutePath(), e);
        }

        final AtomicReference<ExceptionEnum> exceptionEnumReference = new AtomicReference<>();
        final ClassVisitor classVisitor = new ReadExceptionEnumClassVisitor(io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.Consts.ASM_API_VERSION) {
            @Override
            protected void setExceptionEnum(final ExceptionEnum exceptionEnum) {
                exceptionEnumReference.set(exceptionEnum);
            }
        };
        classReader.accept(classVisitor, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);

        final ExceptionEnum exceptionEnum = exceptionEnumReference.get();

        final boolean isExceptionEnum = exceptionEnum.getSource() != null;

        return isExceptionEnum ? exceptionEnum : null;
    }
}
