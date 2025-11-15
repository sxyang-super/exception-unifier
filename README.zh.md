# Exception unifier

[English](README.md)

## 介绍

Exception unifier 项目旨在实现一种构建异常、抛出异常、定义异常代码的模式。

基于该模式，本项目还提供了方便的依赖、处理器、插件以及简单的中央异常代码管理服务器，帮助你快速构建该模式。

### 该模式是如何形成的

#### 现有问题及待改进点

##### 异常使用不统一
作者在多个项目中发现，不同项目、模块、团队甚至个人在使用异常时存在差异。

这些差异主要体现在以下方面

* 异常内部字段不同

````java
class SampleException {
    String code;
    int code;
    
    String message;
    
    Object data;
    
    SomeBean someBeanField;
    
    // ...
}
````

* 相同字段（异常代码、异常消息）的取值风格或构造方式不同，例如

````java
new SampleException("001", "This is a sample message");
new SampleException(ExceptionCodeEnum.SAMPLE_CODE, String.format("This is a sample message with %s", someVaraible));
new SampleException(ExceptionCodeEnum.SAMPLE_CODE, "This is a sample message", someDataVaraible)
````

##### 待改进点
基于实际项目经验，作者发现在使用异常时存在许多共性模式，比如抛出异常的方式、异常的分类。

因此作者最终总结出一些待改进点

* 抛出异常语句总是跟在 if 语句之后

````java
if (someFlag) {
    throw new SampleException();
}
````

* 使用超类对异常进行分类

````java
class SampleException extends PublicSafeException {
    
}

class AnotherSampleException extends PublicNotSafeException {
    
}
````

* 异常类过多  
  针对每种情况声明不同的异常类

##### 其他
作者认为异常是描述不良业务路径的关键信息。所以，一个项目中存在多少种异常，就意味着在实现过程中考虑了多少种不同的分支。

因此，异常信息需要集中化可视化，并能在某处进行管理。

在 exception unifier 中，它实现了

<ul>
    <li>推送本地异常</li>
    <li>在编译阶段通过追加模块异常代码前缀和异常来源来更新异常代码</li>
</ul>

#### 上述问题的解决方案
* 通过引入一个基础异常类，将异常统一包含为 4 个字段，基本上可以满足所有需求。

````java
// 表示异常代码，同时也是异常的标识符
protected IExceptionEnum exceptionEnum;
// 异常描述，带有字符串模板，如：This is a error message from {0}
protected String message;
// 随异常一起的额外数据
protected Object data;
// 用于渲染消息模板的消息参数
protected Object[] args;
````

* 通过将异常类仅无参构造函数设为包级访问，并使用异常枚举类中的工具方法，隐藏异常的构造过程

````java
@Getter
@RequiredArgsConstructor
// 这将按照来源对异常进行分组
@ExceptionSource("ABC")
public enum SampleExceptionEnum implements ISampleExceptionEnumAsserts {
    TEST("001", "Not found");
    private final String code;
    private final String message;
}

public interface ISampleExceptionEnumAsserts extends IExceptionEnumAsserts<SampleException> {

    @Override
    default SampleException newE(Object... objects) {
        return BaseException.of(SampleException::new, this, objects);
    }

    @Override
    default SampleException newEWithCause(Throwable throwable, Object... objects) {
        return BaseException.ofWithCause(SampleException::new, this, throwable, objects);
    }

    @Override
    default SampleException newEWithData(Object o, Object... objects) {
        return BaseException.ofWithData(SampleException::new, this, o, objects);
    }

    @Override
    default SampleException newEWithCauseAndData(Throwable throwable, Object o, Object... objects) {
        return BaseException.ofWithCauseAndData(SampleException::new, this, throwable, o, objects);
    }
}

// 这将禁止开发者使用构造函数在包外创建异常
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SampleException extends BaseException {
}

// 使用 BASE 源类型或 REMOTE 源类型设置模块异常代码前缀
// <exceptionCodePrefix>SAMPLE</exceptionCodePrefix>

SAMPLE_EXCEPTION("001", "This is an error caused by {0}");

// 通过这种方式，你将得到一个抛出的异常 
// code：SAMPLE_ABC_001
// message：This is an error caused by test
// data：someData
// args：[test]

// 这也将减少在抛出异常前的 if 语句
SAMPLE_EXCEPTION.assertNotBlank("", "test", someData);
````

* 通过 Maven 插件 Mojo **sync**、**push**，可以更新异常代码并将异常信息推送到远程服务器。
    1. exception-unifier:sync
    2. exception-unifier:push

## 使用方法

### 什么是 source type

在 exception unifier 中，模块级异常代码前缀可以通过 exception-unifier-maven-plugin 的配置或插件配置中设置的远程源 URL 获取。

当你希望直接通过 pom 设置模块级异常代码时，即使用 BASE 源类型。

当你希望模块级异常代码由其他地方维护，例如集中管理服务器时，即使用 REMOTE 源类型。

#### 使用 BASE 源类型

BASE 源类型适用于快速上手以及单模块/单团队项目。

Mojo **check** 和 **sync** 在此源类型下工作。

* 引入依赖和插件

````xml
<groupId>com.sample</groupId>
<artifactId>exception-unifier-setup</artifactId>

<!-- 该依赖提供基础异常和断言功能 -->
<dependency>
    <groupId>io.github.sxyang-super</groupId>
    <artifactId>exception-unifier-base</artifactId>
    <version>2.1.0</version>
</dependency>

<!-- 该依赖帮助在编译期间对你的异常进行基本检查 -->
<dependency>
    <groupId>io.github.sxyang-super</groupId>
    <artifactId>exception-unifier-processor</artifactId>
    <version>2.1.0</version>
    <scope>provided</scope>
</dependency>

<plugin>
    <groupId>io.github.sxyang-super</groupId>
    <artifactId>exception-unifier-maven-plugin</artifactId>
    <version>2.1.0</version>
    <executions>
      
      <execution>
        <id>check</id>
        <goals>
          <!-- 对你的异常进行最终检查并缓存检查结果 -->
          <goal>check</goal><!-- 默认绑定到 COMPILE 阶段 -->
          <!-- 使用配置的模块级异常代码前缀来更新异常代码 -->
          <goal>sync</goal><!-- 默认绑定到 COMPILE 阶段 -->
        </goals>
      </execution>
    </executions>
    <configuration>
      <!-- 源类型，可选值列表：BASE, REMOTE  -->
      <sourceType>BASE</sourceType>
      <!-- 模块级异常代码前缀  -->
      <exceptionCodePrefix>SAMPLE</exceptionCodePrefix>
    </configuration>
</plugin>
````

* 为异常相关的类创建包
    * 创建包

````text
建议将所有异常代码放在一个包中，例如 com.example.exception
````

* 创建模块级基础异常

````java
package com.example.exception;

import io.github.sxyangsuper.exceptionunifier.base.BaseException;

public class SampleException extends BaseException {
    /* default */
    SampleException() {
        // 包级访问，避免开发者在包外使用构造函数创建异常
    }
}
````

* 创建异常枚举断言接口

````java
package com.example.exception;

import io.github.sxyangsuper.exceptionunifier.base.BaseException;
import io.github.sxyangsuper.exceptionunifier.base.IExceptionEnumAsserts;

/**
 * 将覆盖 newExxx 方法以创建你的模块级基础异常实例
 */
public interface ISampleExceptionEnumAsserts extends IExceptionEnumAsserts<SampleException> {

    @Override
    default SampleException newE(Object... objects) {
        return BaseException.of(SampleException::new, this, objects);
    }

    @Override
    default SampleException newEWithCause(Throwable throwable, Object... objects) {
        return BaseException.ofWithCause(SampleException::new, this, throwable, objects);
    }

    @Override
    default SampleException newEWithData(Object o, Object... objects) {
        return BaseException.ofWithData(SampleException::new, this, o, objects);
    }

    @Override
    default SampleException newEWithCauseAndData(Throwable throwable, Object o, Object... objects) {
        return BaseException.ofWithCauseAndData(SampleException::new, this, throwable, o, objects);
    }
}
````

* 创建异常枚举

````java
package com.example.exception;

import io.github.sxyangsuper.exceptionunifier.processor.ExceptionSource;

// 此枚举中的所有异常均来源于 TEST
// 异常代码将被更新为以 TEST 为前缀，以 : 作为分隔符
@ExceptionSource("TEST")
public enum SampleExceptionEnum implements ISampleExceptionEnumAsserts {
  ORDER_NOT_FOUND("001", "Order {0} is not found"),
  INVALID_ORDER_STATUS("002", "Status of order {0} is {1} that is invalid.");
  // ... 在此处定义更多异常
  
  private final String code;
  private final String message;

  public SampleExceptionEnum(final String code, final String message) {
      this.code = code;
      this.message = message;
  }
  
  public String getMessage() {
    return message;
  }

  public String getCode() {
    return code;
  }
}
````

* 不同用法示例

````java
int orderId = 1;
Order order = getById(orderId)
// 如果 order 为 null，则会抛出以下字段的异常
// code：SAMPLE_TEST_001 （{模块异常代码前缀}_{异常来源}_{异常代码}）
// message：Order 1 is not found
// data：null
// args：[1]
ORDER_NOT_FOUND.assertNotNull(order, orderId);
// 如果 order 的状态不是 CREATED，则会抛出以下字段的异常
// code：SAMPLE_TEST_002 （{模块异常代码前缀}_{异常来源}_{异常代码}）
// message：Status of order 1 is SUBMITTED that is invalid.
// data：null
// args：[1, SUBMITTED]
String orderStatus = order.getStatus();
INVALID_ORDER_STATUS.assertEqual(OrderStatus.CREATED, orderStatus, orderId, orderStatus);
````

#### 使用 REMOTE 源类型

REMOTE 源类型更适用于多个模块/团队的项目，这类项目要求异常代码在某种层面上是唯一的，比如模块级别。

这样集中管理异常代码前缀的维护将会连接并正确影响每个模块的异常代码。

基本步骤与 BASE 源类型相同，仅更新步骤如下。

* 引入依赖和插件

````xml
<!-- 与 BASE 源类型相同的两个依赖 -->
<plugin>
  <groupId>io.github.sxyang-super</groupId>
  <artifactId>exception-unifier-maven-plugin</artifactId>
  <version>2.1.0</version>
  <executions>

    <execution>
      <id>check</id>
      <goals>
        <!-- 对你的异常进行最终检查并缓存检查结果 -->
        <goal>check</goal><!-- 默认绑定到 COMPILE 阶段 -->
        <!-- 使用配置的模块级异常代码前缀来更新异常代码 -->
        <goal>sync</goal><!-- 默认绑定到 COMPILE 阶段 -->
        <!-- 将你的异常信息推送到远程服务器 -->
        <goal>push</goal><!-- 默认绑定到 INSTALL 阶段 -->
      </goals>
    </execution>
  </executions>
  <configuration>
    <!-- 源类型，可选值列表：BASE, REMOTE  -->
    <sourceType>REMOTE</sourceType>
    <!-- 远程服务器的基础 URL，访问的端点如下所列  -->
    <remoteBaseURL>http://localhost:8080</remoteBaseURL>
  </configuration>
</plugin>
````

##### 远程异常代码管理服务器的访问端点

````http request
### 获取异常代码前缀
# 触发 Mojo: sync
# 重试策略：最多 4 次，每次间隔 1 秒
# 由插件配置设置：exceptionunifier.remoteBaseURL
@remoteBaseURL=http://localhost:8080
# 从 pom 自动获取：{project.groupId}.{project.artifactId}
@moduleId=com.sample.exception-unifier-setup
# 由插件配置设置：exceptionunifier.remoteQuery
@query=""
GET {{remoteBaseURL}}/prefix/{{moduleId}}?{{query}}

# 成功响应：
# HTTP 状态码：200
# 响应体：一个简单的非空字符串，表示目标模块异常代码前缀

# 失败响应：除成功响应之外，所有其他情况均视为失败

### 推送异常
# 触发 Mojo: push
POST {{remoteBaseURL}}/exception-enum/bulk?{{query}}
Content-Type: application/json

{
  "moduleId": "{{moduleId}}",
  "syncedExceptionSources": [
    {
      "source": "TEST",
      "syncedExceptionCodes": [
        {
          "name": "ORDER_NOT_FOUND",
          "code": "SAMPLE_TEST_001",
          "originalCode": "001",
          "message": "Order {0} is not found"
        },
        {
          "name": "INVALID_ORDER_STATUS",
          "code": "SAMPLE_TEST_002",
          "originalCode": "002",
          "message": "Status of order {0} is {1} that is invalid."
        }
      ]
    }
  ]
}
