package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.fixture;

import cn.hutool.core.collection.ListUtil;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.SyncResult;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.SyncedExceptionCode;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.SyncedExceptionSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SyncResultFixture {

    public static @NotNull SyncResult getSyncResult() {
        final SyncResult syncResult = Mockito.mock(SyncResult.class);
        Mockito.when(syncResult.getModuleId())
                .thenReturn("com.test");

        final SyncedExceptionSource syncedExceptionSource = Mockito.mock(SyncedExceptionSource.class);
        Mockito.when(syncResult.getSyncedExceptionSources())
                .thenReturn(ListUtil.of(
                        syncedExceptionSource
                ));

        Mockito.when(syncedExceptionSource.getSource())
                .thenReturn("ABC");

        final SyncedExceptionCode syncedExceptionCode = Mockito.mock(SyncedExceptionCode.class);
        Mockito.when(syncedExceptionSource.getSyncedExceptionCodes())
                .thenReturn(ListUtil.of(
                        syncedExceptionCode
                ));

        Mockito.when(syncedExceptionCode.getCode())
                .thenReturn("SAMPLE:ABC:001");
        Mockito.when(syncedExceptionCode.getOriginalCode())
                .thenReturn("001");
        Mockito.when(syncedExceptionCode.getName())
                .thenReturn("TEST");
        Mockito.when(syncedExceptionCode.getMessage())
                .thenReturn("Not Found");
        return syncResult;
    }
}
