package sync.succeed_with_base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SampleExceptionEnumTest {

    @Test
    void should_sync_successfully() {
        Assertions.assertEquals("SAMPLE:ABC:001", SampleExceptionEnum.TEST.getCode());
    }
}
