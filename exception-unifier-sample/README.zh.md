# 异常统一化示例
## 通用规则
对于所有的示例，异常枚举代码会首先由其自己的注解 `ExceptionSource` 进行前缀化。

然后，会被模块级别的异常代码前缀进一步前缀化，这个前缀是通过不同的方式提供的。

分隔符为 `:`。

## 基础模块
在这个示例中，我们使用编译参数直接为该模块提供异常代码 [这里](base/pom.xml:30)。

因此，`com.sxyangsuper.exceptionunifier.sample.base.SampleExceptionEnum.TEST` 的异常代码会被前缀为 `SAMPLE`，最终为 `SAMPLE:U:001`。

## 远程模块
在这个示例中，我们使用编译参数提供两个必需的参数 `exceptionCodeRemoteBaseUrl` [这里](remote/pom.xml:31) 和 `exceptionCodeModuleId` [这里](remote/pom.xml:32)，以便于 `com.sxyangsuper.exceptionunifier.processor.RemoteExceptionCodePrefixSupplier` 使用。

我们还在 [这里](remote/src/main/resources/exception-code-remote-server) 设置了一个简单的 Web 服务器，

该服务器为请求 `http://localhost:8080/prefix` 提供服务，并返回模块ID `io.github.sxyang-super.exception-unifier-sample` 对应的 `SAMPLE-REMOTE` 异常代码前缀。

因此，处理器将向 `http://localhost:8080/prefix` 发出请求，参数为模块ID `io.github.sxyang-super.exception-unifier-sample`，然后返回 `SAMPLE-REMOTE` 作为异常枚举代码的前缀。

最后，异常枚举 `com.sxyangsuper.exceptionunifier.sample.remote.SampleExceptionEnum.TEST` 的代码将被前缀为 `SAMPLE-REMOTE`，最终为 `SAMPLE-REMOTE:U:001`。
