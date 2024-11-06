# Exception Unifier

*[English](README.md) ∙ [中文](README.zh.md)*

这个项目旨在帮助统一你们组织的异常模式。</br>
它将帮助你实现以下目标：
1. 强制在整个组织中使所有异常代码唯一
2. 统一维护异常的模式
3. 统一抛出异常的模式
4. 统一检查异常的模式

这是一个进行中的项目，详细信息请参见 [项目线路图](ROADMAP.zh.md)

## 项目愿景

### 核心思想

在**Exception Unifier**的核心中，**在整个组织中统一异常代码**是非常重要的。</br>

我们相信，**在当前多个模块开发模式下依赖于个别开发者来实现这一点是不可靠的**。</br>

我们的目标是**为开发者提供一种简单的方法来创建整个组织中的唯一异常代码**，</br>
同时**组织管理者可以轻松了解不同模块之间异常代码的差异**，</br>
此外，**组织管理者可以在集中式管理页面中设置每个模块的统一器**。

### 关键原则

**简洁性**：我们的项目设计以简洁性为重。**易于维护异常（代码），易于使用异常**

### 为什么这个项目很重要

目前，我们没有找到任何有用的组织级异常模式，这意味着在一个组织中，每个模块可以有自己独特的模式，这使得模式不统一。</br>
而统一的异常模式可以帮助组织更容易地维护异常代码。</br>
有了这个项目，声明异常（代码）的模式、抛出异常的方式、识别异常的方式都很简单。</br>
由于模式是统一的，我们可以更好地根据你的要求添加拦截逻辑。</br>
最重要的是，暴露给客户端的异常代码始终是唯一的。

## 目录
- [安装](#安装)
- [使用](#使用)
- [许可证](LICENSE)

## 安装

要使用这个项目，请将以下依赖项添加到你的 Maven 构建文件中。

```xml
<project>
    <dependencies>
        <dependency>
            <groupId>io.github.sxyang-super</groupId>
            <artifactId>exception-unifier</artifactId>
            <version>1.0.0</version>
        </dependency>
        <dependency>
            <groupId>io.github.sxyang-super</groupId>
            <artifactId>exception-unifier-processor</artifactId>
            <version>1.0.0</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- 默认插件 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <compilerArgs>
                        <compilerArg>-AexceptionCodePrefix=SAMPLE</compilerArg>
                    </compilerArgs>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## 使用
1. 通过编译参数 exceptionCodePrefix 指定你的模块异常代码前缀
2. 创建你的模块基础异常
```java
package com.sxyangsuper.exceptionunifier.sample;

import com.sxyangsuper.exceptionunifier.base.BaseException;

public class SampleException extends BaseException {
    SampleException() {
        super();
    }
}
```
3. 创建你的模块异常枚举断言
```java
package com.sxyangsuper.exceptionunifier.sample;

import com.sxyangsuper.exceptionunifier.base.BaseException;
import com.sxyangsuper.exceptionunifier.base.IExceptionEnumAsserts;

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
```
4. 创建你的模块异常枚举
```java
package com.sxyangsuper.exceptionunifier.sample;

import com.sxyangsuper.exceptionunifier.processor.ExceptionSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ExceptionSource("U")
public enum SampleExceptionEnum implements ISampleExceptionEnumAsserts {
    TEST("001", "Not found");
    private final String code;
    private final String message;
}
```
5. 使用 `mvn clean compile` 编译你的项目，并查看生成的 `SampleExceptionEnum` 类文件，你会发现 `TEST` 的代码是 `SAMPLE:U:001`
6. 使用IAsserts提供的方法
````java
public class Sample {
    public static void main(String[] args) {
        // will throw exception
        SampleExceptionEnum.TEST.assertNotNull(null);
    }
}
````
7. 更多例子可以参考[exception-unifier-sample](exception-unifier-sample/README.zh.md)

## 贡献

请参考 [贡献指南](./CONTRIBUTION.zh.md)