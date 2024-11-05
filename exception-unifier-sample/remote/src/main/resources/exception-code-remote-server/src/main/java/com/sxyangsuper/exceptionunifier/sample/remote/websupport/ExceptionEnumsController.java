package com.sxyangsuper.exceptionunifier.sample.remote.websupport;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RequestMapping("exception-enums")
@RestController
public class ExceptionEnumsController {

    @PostMapping("bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public void bulk(@RequestBody ExceptionCodeReportMeta reportMeta) throws IOException {
        final String userDir = System.getProperty("user.dir");
        final String targetFilePath = String.join(File.separator,userDir, "..", "..", "..", "..", "target", "generated-test-sources", "exceptionCodeReportMeta.json");
        FileUtil.mkParentDirs(targetFilePath);

        final File file = FileUtil.newFile(targetFilePath);
        if (file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();

        FileUtil.writeString(JSONUtil.toJsonStr(reportMeta), targetFilePath, CharsetUtil.defaultCharset());
    }
}
