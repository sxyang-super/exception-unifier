# Exception Unifier 的基本用法

## 介绍
基本用法的核心思想是通过 Maven 编译参数来设置全局异常代码前缀。

这种方式可以让我们更方便地体验异常代码的生成过程。

对于单一项目或团队可以为每个项目适当地分配前缀的多个项目，我们推荐以这种方式集成 Exception Unifier。

## 如何验证

### 验证步骤
1. 切换到本 README 文件所在的目录。
2. 运行命令行（假设你已在本地机器上安装了 [exception-unifier](https://github.com/sxyang-super/exception-unifier)）。
   > mvn clean verify
3. 检查源代码和编译后的代码：
   > 源代码 001: com/sxyangsuper/exceptionunifier/sample/base/SampleExceptionEnum.java:11  
   > 编译代码 SAMPLE:U:001: exception-unifier-sample/base/target/classes/com/sxyangsuper/exceptionunifier/sample/base/SampleExceptionEnum.class:11
4. 检查测试：
   > com/sxyangsuper/exceptionunifier/sample/base/SampleExceptionEnumTest.java:13

#### 说明
如果还未阅读过，请参考[通用知识](../README.zh.md:6)。

`001` 是详细的异常代码。

`U` 是来自注解 `ExceptionSource` 的异常来源。

`SAMPLE` 是通过编译参数 `pom.xml:30` 设置的模块异常代码前缀。

由于模块异常代码前缀是直接设置的，因此对应的供应类被称为 **Configurable**（可配置的）。
