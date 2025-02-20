package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SyncedExceptionCode {
    private String name;
    private String code;
    private String originalCode;
    private String message;
}
