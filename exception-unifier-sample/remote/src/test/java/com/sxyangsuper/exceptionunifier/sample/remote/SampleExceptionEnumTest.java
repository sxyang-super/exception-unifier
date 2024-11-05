package com.sxyangsuper.exceptionunifier.sample.remote;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SampleExceptionEnumTest {

    @Test
    @Disabled
    void should_get_correct_exception_code() {
        assertEquals("SAMPLE-REMOTE:U:001", SampleExceptionEnum.TEST.getCode());

        final String userDir = System.getProperty("user.dir");
        final String targetFilePath = String.join(File.separator,userDir, "target", "generated-test-sources", "exceptionCodeReportMeta.json");
        final String storedReportMetaJson = FileUtil.readString(targetFilePath, CharsetUtil.defaultCharset());
        Assertions.assertEquals("{\"moduleId\":\"io.github.sxyang-super.exception-unifier-sample\",\"prefix\":\"SAMPLE-REMOTE\",\"metaSources\":[{\"source\":\"U\",\"exceptionCodes\":[{\"code\":\"SAMPLE-REMOTE:U:001\",\"messagePlaceholder\":\"Not found {0}\"}]}]}", storedReportMetaJson);
    }
}
