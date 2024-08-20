# Exception Unifier

*[English](README.md) ∙ [中文](README.zh.md)*

This project intends to help unify the exception pattern of your organization.</br>
It will help you by achieving following things:
1. force making all exception code unique through your organization
2. unified pattern in maintaining exceptions
3. unified pattern in throwing exceptions
4. unified pattern in checking exceptions

And this is an ongoing project, details can be seen in [roadmap](ROADMAP.md)

## Project Vision

### Core Thought

At the heart of **Exception Unifier**, **Unified exception code through your whole organization** is very important.</br>

We believe that **relying on individual developer to achieve that is not reliable in current multiple modules developing mode**.</br>

The goal is to **Providing an easy way for developer to create unique exception code through whole organization**,</br>
also **organization manager can easily know the differences in exception code between different module**,
also the **organization manager can set unifier in exception code for each module in a centralized admin page**.

### Key Principles

**Simplicity**: Our project is designed with simplicity in mind. **Easy to maintain exception(code), Easy to use exception**

### Why This Project Matters

Currently, we don't find any useful exception pattern in an organization level which means in an organization, each module can have their own pattern which makes it non-unified.</br>
And unified exception pattern can help organization maintain exception code more easily.</br>
With this project, the pattern of declaring exception(code), way of throwing an exception, way of identifying an exception are all simple.</br>
Because the pattern is unified, we can better add intercepting logic due to your requirements.</br>
The most important is exception code exposed to client is always unique.

## Table of Contents
- [Installation](#Installation)
- [Usage](#Usage)
- [License](LICENSE)

## Installation

To use this project, add the following dependency to your Maven build file.

```xml
<project>
    <dependencies>
        <dependency>
            <groupId>com.github.sxyang-super</groupId>
            <artifactId>exception-unifier</artifactId>
            <version>1.0.0</version>
        </dependency>
        <dependency>
            <groupId>com.github.sxyang-super</groupId>
            <artifactId>exception-unifier-processor</artifactId>
            <version>1.0.0</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- default plugins -->
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

## Usage
1. specify your module exception code prefix by compile arg `exceptionCodePrefix`
2. create your module base exception
```java
package com.sxyangsuper.exceptionunifier.sample;

import com.sxyangsuper.exceptionunifier.base.BaseException;

public class SampleException extends BaseException {
    SampleException() {
        super();
    }
}
```
3. create your module exception enum asserts
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
4. create your module exception enum
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
5. compile your project with `mvn cleain compile` and go to your generated class file for SampleExceptionEnum, you'll find that the code of `TEST` is `SAMPLE:U:001`
6. more example can be seen in [exception-unifier-sample](https://github.com/sxyang-super/exception-unifier/tree/master/exception-unifier-sample)
