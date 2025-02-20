package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SyncedExceptionSourceTest {

    @Nested
    class GetSyncedExceptionCodesTest {

        @Test
        void should_return_unmodifiable_list() {
            final List<SyncedExceptionCode> syncedExceptionCodes = new SyncedExceptionSource().getSyncedExceptionCodes();
            Assertions.assertThrows(UnsupportedOperationException.class, () -> syncedExceptionCodes.add(null));
        }
    }
}