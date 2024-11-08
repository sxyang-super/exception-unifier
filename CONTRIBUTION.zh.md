# 贡献

## 准备工作

### 准备 GPG

1. 下载 GPG 到你的机器上 [下载页面](https://gnupg.org/download/index.html#sec-1-2)
2. 按照[指南](https://central.sonatype.org/publish/requirements/gpg/#installing-gnupg)为名称 `exception-unifier-maven-gpg` 添加密钥

### 准备 JDK

目前仅支持 JDK 1.8。

### 准备 Maven

Maven 版本应为 3.6 或更高。

### 准备 Node

如果需要修改 exms（异常管理服务器），则需要准备 Node 环境。

### 如何直接使用 IDEA 运行测试

以下模块的测试可以直接通过 IDEA 运行：

1. exception-unifier
2. exception-unifier-processor

以下模块的测试无法直接通过 IDEA 运行：

1. exception-unifier-sample
   > 需要在另一个 IDEA 窗口中打开该模块，将其作为单独的项目。