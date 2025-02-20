package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class ResultHandler {

    private static final String RESULT_DIRECTORY = "exception-unifier";
    private static final String CHECK_RESULT_FILE_NAME = "check-result.json";
    private static final String SYNC_RESULT_FILE_NAME = "sync-result.json";

    private final MojoConfiguration mojoConfiguration;

    public void storeCheckResult(final CheckResult checkResult) {
        FileUtil.writeUtf8String(JSONUtil.toJsonStr(checkResult), this.getResultFilePath(CHECK_RESULT_FILE_NAME));
    }

    public void clearCheckResult() {
        final String checkResultFilePath = this.getResultFilePath(CHECK_RESULT_FILE_NAME);
        FileUtil.del(checkResultFilePath);
    }

    private String getResultFilePath(final String resultFileName) {
        return String.join(
                File.separator,
                this.mojoConfiguration.getMavenProject().getBuild().getDirectory(),
                RESULT_DIRECTORY,
                resultFileName
        );
    }

    @Nullable
    public CheckResult getCheckResult() {
        final File checkResultFile = new File(this.getResultFilePath(CHECK_RESULT_FILE_NAME));
        if (!checkResultFile.exists()) {
            return null;
        }
        return JSONUtil.toBean(FileUtil.readUtf8String(checkResultFile), CheckResult.class);
    }

    public void storeSyncResult(final SyncResult syncResult) {
        FileUtil.writeUtf8String(JSONUtil.toJsonStr(syncResult), this.getResultFilePath(SYNC_RESULT_FILE_NAME));
    }

    public SyncResult getSyncResult() {
        final File checkResultFile = new File(this.getResultFilePath(SYNC_RESULT_FILE_NAME));
        if (!checkResultFile.exists()) {
            return null;
        }
        return JSONUtil.toBean(FileUtil.readUtf8String(checkResultFile), SyncResult.class);
    }
}
