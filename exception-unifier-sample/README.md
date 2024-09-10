# Exception unifier sample
## common
For all example, exception enum code will be prefixed by their own annotation `ExceptionSource` first.

Then it'll be prefixed by module level exception code prefix which is provided differently.

And the separator is `:`.

## base
In this sample, we are using compile arg to directly provide the exception code fo this module [here](base/pom.xml:30)

so that exception code for `com.sxyangsuper.exceptionunifier.sample.base.SampleExceptionEnum.TEST` will be prefixed by `SAMPLE` and be `SAMPLE:U:001`.

## remote
In this example, we are using compile arg to provide two required arg `exceptionCodeRemoteBaseUrl` [here](remote/pom.xml:31) and `exceptionCodeModuleId` [here](remote/pom.xml:32) for `com.sxyangsuper.exceptionunifier.processor.RemoteExceptionCodePrefixSupplier`.

We also set up a simple web server [here](remote/src/main/resources/exception-code-remote-server)

which serves response for request to `http//localhost:8080/prefix` and returning `SAMPLE-REMOTE` exception code prefix for moduleId `io.github.sxyang-super.exception-unifier-sample`.

so that in processor will make a request to `http//localhost:8080/prefix` with parameter moduleId `io.github.sxyang-super.exception-unifier-sample` and then get `SAMPLE-REMOTE` as the prefix of exception enum code.

Finally, code of exception enum `com.sxyangsuper.exceptionunifier.sample.remote.SampleExceptionEnum.TEST` will be prefixed by `SAMPLE-REMOTE` and be `SAMPLE-REMOTE:U:001`.


