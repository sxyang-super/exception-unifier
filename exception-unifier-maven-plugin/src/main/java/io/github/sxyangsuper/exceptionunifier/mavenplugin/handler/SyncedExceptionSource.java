package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class SyncedExceptionSource {
    private String source;
    private List<SyncedExceptionCode> syncedExceptionCodes = new ArrayList<>();

    public List<SyncedExceptionCode> getSyncedExceptionCodes() {
        return Collections.unmodifiableList(syncedExceptionCodes);
    }
}
