package com.sxyangsuper.exceptionunifier.processor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ProcessedExceptionCode {
    private String code;
    private String messagePlaceholder;
}
