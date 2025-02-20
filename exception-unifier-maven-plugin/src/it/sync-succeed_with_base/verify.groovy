def checkResultFile = new File(basedir, String.join(File.separator, "target", "exception-unifier", "sync-result.json"))
assert checkResultFile.exists()

String checkResultContent = checkResultFile.text
assert checkResultContent.contains("SAMPLE:ABC:001")
