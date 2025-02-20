def checkResultFile = new File(basedir, String.join(File.separator, "target", "exception-unifier", "check-result.json"))
assert checkResultFile.exists()

String checkResultContent = checkResultFile.text
assert checkResultContent.contains("succeed_with_remote\\\\SampleExceptionEnum.class")
