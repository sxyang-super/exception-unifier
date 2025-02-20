def logFile = new File(basedir, "build.log")
assert logFile.exists()

String logContent = logFile.text
assert logContent.contains("Duplicate exception code ABC:001 within check.fail_with_base.duplicate_in_global_scope.SampleExceptionEnum")