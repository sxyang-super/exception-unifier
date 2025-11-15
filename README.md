<p style="display: flex; justify-content: center;">
  <img src="docs/images/banner.png" alt="Exception Unifier" width="332">
</p>

<p style="display: flex; justify-content: center;">
  <a href="https://central.sonatype.com/artifact/io.github.sxyang-super/exception-unifier-base">
    <img src="https://img.shields.io/maven-central/v/io.github.sxyang-super/exception-unifier-base?style=flat-square" alt="Maven Central">
  </a>
  <a href="https://github.com/sxyang-super/exception-unifier/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/sxyang-super/exception-unifier?style=flat-square" alt="License">
  </a>
  <a href="https://adoptium.net/temurin/releases/?version=8">
    <img src="https://img.shields.io/badge/Java-8+-orange?style=flat-square" alt="Java Version">
  </a>
</p>

---

## Exception Unifier

**Opinionated Java toolkit for defining, throwing, and tracking exceptions consistently across services, with enum-driven assertions, a compile-time processor, and optional centralized code management.**

- 🧩 **Consistent structure** – one base exception model for all errors.
- 📜 **Declarative definitions** – enums + annotations instead of scattered `throw new ...`.
- 🔍 **Build-time validation** – catch conflicts before runtime.
- 🔄 **Smart prefix resolution** – load from Maven config or a remote server.
- 🌐 **Optional central catalog** – keep codes unique across modules and teams.  

## How It Works in 60 Seconds

```java
// 1. Module-level BaseException
public class SampleException extends BaseException {
    SampleException() {} // package-private constructor
}

// 2. Enum asserts for fluent exception creation
public interface ISampleExceptionEnumAsserts extends IExceptionEnumAsserts<SampleException> {
    @Override
    default SampleException newE(Object... objects) {
        return BaseException.of(SampleException::new, this, objects);
    }
    @Override
    default SampleException newEWithCause(Throwable t, Object... objects) {
        return BaseException.ofWithCause(SampleException::new, this, t, objects);
    }
    @Override
    default SampleException newEWithData(Object o, Object... objects) {
        return BaseException.ofWithData(SampleException::new, this, o, objects);
    }
    @Override
    default SampleException newEWithCauseAndData(Throwable t, Object o, Object... objects) {
        return BaseException.ofWithCauseAndData(SampleException::new, this, t, o, objects);
    }
}

// 3. Define exceptions in an enum
@ExceptionSource("TEST")
public enum SampleExceptionEnum implements ISampleExceptionEnumAsserts {
    ORDER_NOT_FOUND("001", "Order {0} is not found"),
    INVALID_ORDER_STATUS("002", "Status of order {0} is {1} that is invalid.");

    private final String code;
    private final String message;
    SampleExceptionEnum(String code, String message) { this.code = code; this.message = message; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
}

public class Demo {
    public static void main(String[] args) {
        // 4. Use assertion helpers to throw exceptions
        int orderId = 1;
        Order order = getById(orderId);
        SampleExceptionEnum.ORDER_NOT_FOUND.assertNotNull(order, orderId);

        String orderStatus = order.getStatus();
        SampleExceptionEnum.INVALID_ORDER_STATUS.assertEqual(OrderStatus.CREATED, orderStatus, orderId, orderStatus);
    }
}
```