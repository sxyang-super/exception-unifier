package io.github.sxyangsuper.exceptionunifier.base.asserts;

import io.github.sxyangsuper.exceptionunifier.base.BaseException;
import io.github.sxyangsuper.exceptionunifier.base.fixture.TestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.github.sxyangsuper.exceptionunifier.base.fixture.TestExceptionEnum.ERROR_1;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ThrowAssertsTest {
    IThrowAsserts<BaseException> throwAsserts;
    BaseException suppliedException;
    Object[] args;
    RuntimeException cause;
    Object data;

    @BeforeEach
    void setUp() {
        //noinspection unchecked
        throwAsserts = Mockito.spy(IThrowAsserts.class);

        suppliedException = TestException.of(ERROR_1, "Test");
        args = new Object[]{ new Object() };
        cause = new RuntimeException();
        data = new Object();

        when(throwAsserts.newE(args)).thenReturn(suppliedException);
        when(throwAsserts.newEWithCause(cause, args)).thenReturn(suppliedException);
        when(throwAsserts.newEWithData(data, args)).thenReturn(suppliedException);
        when(throwAsserts.newEWithCauseAndData(cause, data, args)).thenReturn(suppliedException);
    }

    @Test
    void throwE() {
        final BaseException thrownException = assertThrows(BaseException.class, () -> throwAsserts.throwE(args));

        assertSame(suppliedException, thrownException);
        verify(throwAsserts, times(1)).newE(args);
    }

    @Test
    void throwEWithCause() {
        final BaseException thrownException = assertThrows(BaseException.class, () -> throwAsserts.throwEWithCause(cause, args));

        assertSame(suppliedException, thrownException);
        verify(throwAsserts, times(1)).throwEWithCause(cause, args);
    }

    @Test
    void throwEWithData() {
        final BaseException thrownException = assertThrows(BaseException.class, () -> throwAsserts.throwEWithData(data, args));

        assertSame(suppliedException, thrownException);
        verify(throwAsserts, times(1)).throwEWithData(data, args);
    }

    @Test
    void throwEWithCauseAndData() {
        final BaseException thrownException = assertThrows(BaseException.class, () -> throwAsserts.throwEWithCauseAndData(cause, data, args));

        assertSame(suppliedException, thrownException);
        verify(throwAsserts, times(1)).throwEWithCauseAndData(cause, data, args);
    }
}
