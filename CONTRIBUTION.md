# Contribution

## Preparation

### Get GPG ready

1. Download GPG to your machine [download page](https://gnupg.org/download/index.html#sec-1-2)
2. Following [guide](https://central.sonatype.org/publish/requirements/gpg/#installing-gnupg) to add key for name `exception-unifier-maven-gpg` 

### Get JDK ready

only jkd1.8 is supported now.

### Get maven ready

maven version should be >= 3.6

### Get node ready

If we need to modify exms(exception manage server), we need to get node ready.

### How to run test with IDEA directly

Tests of following modules can be run though IDEA directly

1. exception-unifier
2. exception-unifier-processor

Tests of following modules can not be run though IDEA directly

1. exception-unifier-sample
> we need to open this module in another IDEA window to let it as a single project
