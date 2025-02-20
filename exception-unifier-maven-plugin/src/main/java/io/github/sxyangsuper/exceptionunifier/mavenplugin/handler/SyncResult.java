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
public class SyncResult {
    private String moduleId;
    private List<SyncedExceptionSource> syncedExceptionSources = new ArrayList<>();

    public List<SyncedExceptionSource> getSyncedExceptionSources() {
        return Collections.unmodifiableList(syncedExceptionSources);
    }
}
