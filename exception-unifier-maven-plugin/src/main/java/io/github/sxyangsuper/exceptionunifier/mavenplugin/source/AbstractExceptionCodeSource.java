package io.github.sxyangsuper.exceptionunifier.mavenplugin.source;

import cn.hutool.core.util.StrUtil;
import io.github.sxyangsuper.exceptionunifier.base.Consts;
import org.apache.maven.plugin.MojoExecutionException;

public abstract class AbstractExceptionCodeSource implements IExceptionCodeSource {

    protected void validateExceptionCodePrefix(final String exceptionCodePrefix) throws MojoExecutionException {
        if (StrUtil.isBlank(exceptionCodePrefix)) {
            throw new MojoExecutionException("exception code prefix can not be blank.");
        }

        if (exceptionCodePrefix.contains(Consts.EXCEPTION_CODE_SPLITTER)) {
            throw new MojoExecutionException(
                    String.format("exception code prefix should not contain reserved splitter: %s", Consts.EXCEPTION_CODE_SPLITTER)
            );
        }
    }
}
