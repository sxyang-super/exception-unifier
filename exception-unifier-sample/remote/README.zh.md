# Exception Unifier 的远程用法

## 介绍
远程用法的核心思想是为当前模块设置 `moduleId`，并从远程服务器获取相应的模块异常代码前缀。

通过这种方式，对于多个项目，它们的异常代码前缀会在中央服务器中统一维护，以确保无重复。

这也是 Exception Unifier 的最初设计意图。

我们推荐使用此方式。

## 如何验证

### 预设
这是一个内置的[远程服务器](src/main/resources/exception-code-remote-server)，用于作为异常代码的远程服务器。

该服务器将在 Maven 生命周期阶段 `process-resources` 后自动启动，并在 `compile` 阶段结束后停止，

因此在编译阶段，对异常代码服务器的请求将被相应处理。

### 验证步骤
1. 切换到本 README 文件所在的目录。
2. 运行命令行（假设你已在本地机器上安装了 [exception-unifier](https://github.com/sxyang-super/exception-unifier)）。
   > ../../mvnw(.cmd) clean verify
3. 检查源代码和编译后的代码：
   > 源代码 001: com.sxyangsuper.exceptionunifier.sample.remote.SampleExceptionEnum.TEST  
   > 编译代码 SAMPLE-REMOTE:U:001: com.sxyangsuper.exceptionunifier.sample.remote.SampleExceptionEnum.TEST  
   > 生成的 JSON 文件记录了异常枚举的报告数据：exception-unifier-sample/remote/target/generated-test-sources/exceptionCodeReportMeta.json
4. 检查测试：
   > com.sxyangsuper.exceptionunifier.sample.remote.SampleExceptionEnumTest.should_get_correct_exception_code
