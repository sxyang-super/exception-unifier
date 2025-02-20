def logFile = new File(basedir, "build.log")
assert logFile.exists()

String logContent = logFile.text
assert logContent.contains("Fail to get exception code prefix for module io.github.sxyang-super.it.sync-fail_with_remote-exception_code_prefix_not_found, response status is 404, message is exception source code for moduleId io.github.sxyang-super.it.sync-fail_with_remote-exception_code_prefix_not_found is not configured")
