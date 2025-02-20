package io.github.sxyangsuper.exceptionunifier.base.asserts;

import io.github.sxyangsuper.exceptionunifier.base.BaseException;
import io.github.sxyangsuper.exceptionunifier.base.fixture.TestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.github.sxyangsuper.exceptionunifier.base.fixture.TestExceptionEnum.ERROR_1;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BaseAssertsTest {

    IBaseAsserts<BaseException> asserts;
    BaseException suppliedException;
    Object[] args;
    RuntimeException cause;
    Object data;

    @BeforeEach
    void setUp() {
        //noinspection unchecked
        asserts = Mockito.spy(IBaseAsserts.class);

        suppliedException = TestException.of(ERROR_1, "Test");
        args = new Object[]{ new Object() };
        cause = new RuntimeException();
        data = new Object();

        when(asserts.newE(args)).thenReturn(suppliedException);
        when(asserts.newEWithCause(cause, args)).thenReturn(suppliedException);
        when(asserts.newEWithData(data, args)).thenReturn(suppliedException);
        when(asserts.newEWithCauseAndData(cause, data, args)).thenReturn(suppliedException);
    }

    @Nested
    class TestAssertNotNullAndReturn {

        @Test
        void should_throw_exception_given_target_is_null() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> asserts.assertNotNull(null, args));

            assertSame(suppliedException, thrownException);
            verify(asserts, times(1)).newE(args);
        }

        @Test
        void should_not_throw_exception_given_target_is_not_null() {
            final Object target = new Object();
            final Object result = asserts.assertNotNull(target, args);

            assertSame(target, result);
            verify(asserts, times(0)).newE(any());
        }
    }

    @Nested
    class TestAssertNotNullWithData {

        @Test
        void should_throw_exception_given_target_is_null() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> asserts.assertNotNullWithData(null, data, args));

            assertSame(suppliedException, thrownException);
            verify(asserts, times(1)).newEWithData(data, args);
        }

        @Test
        void should_not_throw_exception_given_target_is_not_null() {
            final Object target = new Object();
            final Object result = asserts.assertNotNullWithData(target, data, args);

            assertSame(target, result);
            verify(asserts, times(0)).newEWithData(any(), any(), any());
        }
    }

    @Nested
    class TestAssertNotNullWithDataSupplier {

        @Test
        void should_throw_exception_given_target_is_null() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> asserts.assertNotNullWithDataSupplier(null, () -> data, args));

            assertSame(suppliedException, thrownException);
            verify(asserts, times(1)).newEWithData(data, args);
        }

        @Test
        void should_not_throw_exception_given_target_is_not_null() {
            final Object target = new Object();
            final Object result = asserts.assertNotNullWithDataSupplier(target, () -> data, args);

            assertSame(target, result);
            verify(asserts, times(0)).newEWithData(any(), any(), any());
        }
    }

    @Nested
    class TestAssertNull {

        @Test
        void should_throw_exception_given_target_is_not_null() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> asserts.assertNull(new Object(), args));

            assertSame(suppliedException, thrownException);
            verify(asserts, times(1)).newE(args);
        }

        @Test
        void should_not_throw_exception_given_target_is_null() {
            assertDoesNotThrow(() -> asserts.assertNull(null, args));

            verify(asserts, times(0)).newE(any());
        }
    }

    @Nested
    class TestAssertNullWithData {

        @Test
        void should_throw_exception_given_target_is_not_null() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> asserts.assertNullWithData(new Object(), data, args));

            assertSame(suppliedException, thrownException);
            verify(asserts, times(1)).newEWithData(data, args);
        }

        @Test
        void should_not_throw_exception_given_target_is_null() {
            assertDoesNotThrow(() -> asserts.assertNullWithData(null, data, args));

            verify(asserts, times(0)).newEWithData(any(), any());
        }
    }

    @Nested
    class TestAssertTrue {

        @Test
        void should_throw_exception_given_target_false() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> asserts.assertTrue(false, args));

            assertSame(suppliedException, thrownException);
            verify(asserts, times(1)).newE(args);
        }

        @Test
        void should_not_throw_exception_given_target_is_true() {
            assertDoesNotThrow(() -> asserts.assertTrue(true, args));

            verify(asserts, times(0)).newE(any());
        }
    }

    @Nested
    class TestAssertTrueWithData {

        @Test
        void should_throw_exception_given_target_false() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> asserts.assertTrueWithData(false, data, args));

            assertSame(suppliedException, thrownException);
            verify(asserts, times(1)).newEWithData(data, args);
        }

        @Test
        void should_not_throw_exception_given_target_is_true() {
            assertDoesNotThrow(() -> asserts.assertTrueWithData(true, data, args));

            verify(asserts, times(0)).newEWithData(any(), any());
        }
    }

    @Nested
    class TestAssertFalse {

        @Test
        void should_throw_exception_given_target_true() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> asserts.assertFalse(true, args));

            assertSame(suppliedException, thrownException);
            verify(asserts, times(1)).newE(args);
        }

        @Test
        void should_not_throw_exception_given_target_is_false() {
            assertDoesNotThrow(() -> asserts.assertFalse(false, args));

            verify(asserts, times(0)).newE(any());
        }
    }

    @Nested
    class TestAssertFalseWithData {

        @Test
        void should_throw_exception_given_target_true() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> asserts.assertFalseWithData(true, data, args));

            assertSame(suppliedException, thrownException);
            verify(asserts, times(1)).newEWithData(data, args);
        }

        @Test
        void should_not_throw_exception_given_target_is_false() {
            assertDoesNotThrow(() -> asserts.assertFalseWithData(false, data, args));

            verify(asserts, times(0)).newEWithData(any(), any());
        }
    }
}
