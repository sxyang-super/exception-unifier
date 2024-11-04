package com.sxyangsuper.exceptionunifier.sample.remote.websupport;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ExceptionCodeReportMeta {
    private String moduleId;
    private String prefix;
    private List<MetaSource> metaSources;

    @Getter
    @Setter
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class MetaSource {
        private String source;
        private List<ExceptionCode> exceptionCodes;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class ExceptionCode {
        private String code;
        private String messagePlaceholder;
    }

}
