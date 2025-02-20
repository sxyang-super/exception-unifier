# Exception unifier

[中文](README.zh.md)

## Introduction

Exception unifier project is intend for implementing a pattern for constructing exception, throw exception, defining exception code.

Base on the pattern, this project also provide convenient dependency, processor, plugin and simple central exception code manage server that can help you build the pattern quickly.

### How this pattern is formed

#### Existing problems of points to be improved

##### No unified usage of exception
Author experienced many projects and found usage of exception is different among different projects, modules, teams or even people.

Those differences are mainly reflected on following

* Different fields inside exception

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

* Different value style or value construction style of same field, exception code, exception message, for example

````java
new SampleException("001", "This is a sample message");
new SampleException(ExceptionCodeEnum.SAMPLE_CODE, String.format("This is a sample message with %s", someVaraible));
new SampleException(ExceptionCodeEnum.SAMPLE_CODE, "This is a sample message", someDataVaraible)
````

##### Points that can be improved
Base on experienced projects, author found there are many common pattern when using exception, like they way of throwing an exception, categorizing exceptions.

So author finally get some points that can be improved

* Throw exception statement always follows an if statement

````java
if (someFlag) {
    throw new SampleException();
}
````
 
* Using super class to categorize exceptions

````java
class SampleException extends PublicSafeException {
    
}

class AnotherSampleException extends PublicNotSafeException {
    
}
````

* Too many exception classes
Different exception classes are declared for each of the cases

##### Others
Author is considering exceptions as key information of unhappy business path. So all the exceptions project has mean how many different branches are being considered during implementation.

So exceptions information needs to be centralized visualized and able to be managed somewhere.

In exception unifier, it's implementing

<ul>
    <li>Push local exceptions</li>
    <li>Update exception code by appending module exception code prefix and exception source during compile phase</li>
</ul>

#### How points above are solved
* unify exception to contains 4 fields by introducing a base exception class, basically it'll satisfy all requirements.

````java
// represent exception code and it's the identifier of an exception
protected IExceptionEnum exceptionEnum;
// exception description, with string template, like: This is a error message from {0}
protected String message;
// extra data along with the exception
protected Object data;
// message args to render the message template
protected Object[] args;
````

* by changing exception class only no args constructor to package level and using tool methods from exception enum class, exception construction will be hidden

````java
@Getter
@RequiredArgsConstructor
// this will group the exception by source
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

// this will forbid developer using constructor to create exception
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SampleException extends BaseException {
}

// set module exception code prefix with BASE source type or REMOTE source type
// <exceptionCodePrefix>SAMPLE</exceptionCodePrefix>

SAMPLE_EXCEPTION("001", "This is an error caused by {0}");

// with this, you'll get a thrown exception 
// with code: SAMPLE_ABC_001
// message: This is an error caused by test
// data: someData
// args: [test]

// this will also reduce the if statement before throwing an exception
SAMPLE_EXCEPTION.assertNotBlank("", "test", someData);

````

* with a maven plugin mojo **sync**, **push**, exception code can be updated and exceptions information can be pushed to remote server.
  1. exception-unifier:sync
  2. exception-unifier:push


## How to use

### What is source type

In exception unifier, module level exception code prefix can be got from either configuration of exception-unifier-maven-plugin or remote source whose url is set in plugin configuration.

When you want to set module level exception code directly through pom, then you are using BASE source type.

When you want your module level exception code to be maintained somewhere else, centralized manage server for example, then you are using REMOTE source type.

#### With BASE source type

BASE source type is good for quick hands on and single module/team project.

Mojo **check** and **sync** are working in this source type.

* Introduce dependencies and plugin

````xml
<groupId>com.sample</groupId>
<artifactId>exception-unifier-setup</artifactId>

<!-- this one provides base exception, assertion ability -->
<dependency>
    <groupId>io.github.sxyang-super</groupId>
    <artifactId>exception-unifier-base</artifactId>
    <version>2.0.0</version>
</dependency>

<!-- this one help apply basic checks against your exceptions during compile inflight -->
<dependency>
    <groupId>io.github.sxyang-super</groupId>
    <artifactId>exception-unifier-processor</artifactId>
    <version>2.0.0</version>
    <scope>provided</scope>
</dependency>

<plugin>
    <groupId>io.github.sxyang-super</groupId>
    <artifactId>exception-unifier-maven-plugin</artifactId>
    <version>2.0.0</version>
    <executions>
      
      <execution>
        <id>check</id>
        <goals>
          <!-- apply final check agains your exceptions and cache check result -->
          <goal>check</goal><!-- bound to COMPILE phase by default -->
          <!-- using configured module level exception code prefix to update exception codes -->
          <goal>sync</goal><!-- bound to COMPILE phase by default -->
        </goals>
      </execution>
    </executions>
    <configuration>
      <!-- source type, options list: BASE, REMOTE  -->
      <sourceType>BASE</sourceType>
      <!-- module level exception code prefix  -->
      <exceptionCodePrefix>SAMPLE</exceptionCodePrefix>
    </configuration>
</plugin>
````

* Create package for exceptions related classes
  * create package

````text
Recommand putting all exceptions codes to be in one package, com.example.exception for example
````

  * create module level base exception

````java
package com.example.exception;

import io.github.sxyangsuper.exceptionunifier.base.BaseException;

public class SampleException extends BaseException {
    /* default */
    SampleException() {
        // package accessiblity, avoiding developer use constructor to create exception out side of this package
    }
}
````

  * create exception enum asserts

````java
package com.example.exception;

import io.github.sxyangsuper.exceptionunifier.base.BaseException;
import io.github.sxyangsuper.exceptionunifier.base.IExceptionEnumAsserts;

/**
 * going to override newExxx method to create exception instance of your module level base exception
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

  * create exception enum

````java
package com.example.exception;

import io.github.sxyangsuper.exceptionunifier.processor.ExceptionSource;

// all exceptions within this enum is sourced by TEST
// exception code will be updated to be prefixed by TEST, with : as the separator
@ExceptionSource("TEST")
public enum SampleExceptionEnum implements ISampleExceptionEnumAsserts {
  ORDER_NOT_FOUND("001", "Order {0} is not found"),
  INVALID_ORDER_STATUS("002", "Status of order {0} is {1} that is invalid.");
  // ... define more exceptions here
  
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

  * sample of different usage

````java
int orderId = 1;
Order order = getById(orderId)
// if order is null, an exception with following fields will be thrown
// code: SAMPLE_TEST_001 ({module exception code prefix}_{exception source}_{exception code})
// message: Order 1 is not found
// data: null
// args: [1]
ORDER_NOT_FOUND.assertNotNull(order, orderId);
// if order status is not CREATED, an exception with following fields will be thrown
// code: SAMPLE_TEST_002 ({module exception code prefix}_{exception source}_{exception code})
// message: Status of order 1 is SUBMITTED that is invalid.
// data: null
// args: [1, SUBMITTED]
String orderStatus = order.getStatus();
INVALID_ORDER_STATUS.assertEqual(OrderStatus.CREATED, orderStatus, orderId, orderStatus);
````

#### With REMOTE source type

REMOTE source type is better for multiple modules/teams project that require exception code to be unique in some kind of level, like module level

so that centralized exception code prefix maintenance will be connected and affect the exception codes of each module correctly.

Base steps are the same as BASE source type, only updated steps are listed here.

* Introduce dependencies and plugin

````xml
<!-- ... two same dependencies as BASE source type -->
<plugin>
  <groupId>io.github.sxyang-super</groupId>
  <artifactId>exception-unifier-maven-plugin</artifactId>
  <version>2.0.0</version>
  <executions>

    <execution>
      <id>check</id>
      <goals>
        <!-- apply final check agains your exceptions and cache check result -->
        <goal>check</goal><!-- bound to COMPILE phase by default -->
        <!-- using configured module level exception code prefix to update exception codes -->
        <goal>sync</goal><!-- bound to COMPILE phase by default -->
        <!-- push your exceptions information to remote server -->
        <goal>push</goal><!-- bound to INSTALL phase by default -->
      </goals>
    </execution>
  </executions>
  <configuration>
    <!-- source type, options list: BASE, REMOTE  -->
    <sourceType>REMOTE</sourceType>
    <!-- remote server base url, accessed endpoints are listed below  -->
    <remoteBaseURL>http://localhost:8080</remoteBaseURL>
  </configuration>
</plugin>
````

##### Accessed endpoints of remote exception code manage server

````http request
### get exception code prefix
# trigger mojo: sync
# retry policy: max 4 times with retry interval 1 second
# set by plugin configuration: exceptionunifier.remoteBaseURL
@remoteBaseURL=http://localhost:8080
# auto fetched from pom: {project.groupId}.{project.artifactId}
@moduleId=com.sample.exception-unifier-setup
# set by plugin configuration: exceptionunifier.remoteQuery
@query=""
GET {{remoteBaseURL}}/prefix/{{moduleId}}?{{query}}

# success response:
# http status code: 200
# response body: a simple non-blank string representing target module exception code prefix

# fail response, except success response, all others are fail cases

### push exception
# trigger mojo: push
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
````