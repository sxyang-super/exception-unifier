package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SyncResultTest {

    @Nested
    class GetSyncedExceptionSourcesTest {

        @Test
        void should_return_unmodifiable_list() {
            final List<SyncedExceptionSource> syncedExceptionSources = new SyncResult().getSyncedExceptionSources();
            Assertions.assertThrows(UnsupportedOperationException.class, () -> syncedExceptionSources.add(null));
        }
    }
}