package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

import cn.hutool.core.collection.ListUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class CheckResult {
    private List<ExceptionEnum> exceptionEnums = ListUtil.empty();

    public @Unmodifiable List<ExceptionEnum> getExceptionEnums() {
        return ListUtil.unmodifiable(exceptionEnums);
    }
}
