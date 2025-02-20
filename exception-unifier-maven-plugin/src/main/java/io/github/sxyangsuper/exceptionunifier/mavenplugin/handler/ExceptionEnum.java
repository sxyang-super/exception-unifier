package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

import cn.hutool.core.collection.ListUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ExceptionEnum {
    private String qualifiedClassName;
    private String source;
    private @Unmodifiable List<ExceptionEnumVariable> exceptionEnumVariables = ListUtil.empty();
    private String classFilePath;
    private Integer totalConstructorParameterCount;
    private Integer codeConstructorParametersIndex;
    private Integer messageConstructorParametersIndex;

    public @Unmodifiable List<ExceptionEnumVariable> getExceptionEnumVariables() {
        return exceptionEnumVariables;
    }

    @Override
    public boolean equals(final Object target) {
        if (this == target) {
            return true;
        }
        if (target == null || getClass() != target.getClass()) {
            return false;
        }
        final ExceptionEnum that = (ExceptionEnum) target;
        return Objects.equals(qualifiedClassName, that.qualifiedClassName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(qualifiedClassName);
    }
}
