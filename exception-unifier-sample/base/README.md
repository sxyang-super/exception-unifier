# Base usage of exception unifier
## Introduction
Thought of base usage is to set global exception code prefix through maven compile arg.

It's easy to experience the exception code generation process.

For single project or multiple projects but team can assign prefix appropriately for each project, we recommend integrating with exception unifier in this way.
## How to verify
### Verify steps
1. cd to the directory of this README file
2. run commandline (suppose you already have [exception-unifier](https://github.com/sxyang-super/exception-unifier) in your local machine)
> ..\..\mvnw(.cmd) clean verify
3. check source code and compiled code
> source code 001: com/sxyangsuper/exceptionunifier/sample/base/SampleExceptionEnum.java:11
> compiled code SAMPLE:U:001: exception-unifier-sample/base/target/classes/com/sxyangsuper/exceptionunifier/sample/base/SampleExceptionEnum.class:11
4. check test 
> com/sxyangsuper/exceptionunifier/sample/base/SampleExceptionEnumTest.java:13
#### Explanation
read [common knowledge](../READNE.md:6) if you haven't

`001` is detail exception code.

`U` is exception source that comes from annot annotation `ExceptionSource`

`SAMPLE` is module exception code prefix that is set through compile arg `pom.xml:30`

Since module exception code prefix is set directly, that's the reason corresponding supplier is called **Configurable**
