package com.sxyangsuper.exceptionunifier.base.asserts;

import com.sxyangsuper.exceptionunifier.base.BaseException;
import com.sxyangsuper.exceptionunifier.base.fixture.TestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static com.sxyangsuper.exceptionunifier.base.fixture.TestExceptionEnum.ERROR_1;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ArrayAssertsTest {

    IArrayAsserts<BaseException> arrayAsserts;
    BaseException suppliedException;
    Object[] args;
    RuntimeException cause;
    Object data;

    @BeforeEach
    void setUp() {
        //noinspection unchecked
        arrayAsserts = Mockito.spy(IArrayAsserts.class);

        suppliedException = TestException.of(ERROR_1, "Test");
        args = new Object[]{ new Object() };
        cause = new RuntimeException();
        data = new Object();

        when(arrayAsserts.newE(args)).thenReturn(suppliedException);
        when(arrayAsserts.newEWithCause(cause, args)).thenReturn(suppliedException);
        when(arrayAsserts.newEWithData(data, args)).thenReturn(suppliedException);
        when(arrayAsserts.newEWithCauseAndData(cause, data, args)).thenReturn(suppliedException);
    }

    @Nested
    class TestAssertArrayNotEmpty {

        @Test
        void should_throw_exception_given_target_is_empty() {
            final BaseException thrownException = assertThrows(BaseException.class,
                () -> arrayAsserts.assertNotEmpty(null, args)
            );

            assertSame(suppliedException, thrownException);
            verify(arrayAsserts, times(1)).newE(args);
        }

        @Test
        void should_not_throw_exception_given_target_is_not_empty() {
            final Object[] target = { new Object() };
            final Object[] result = arrayAsserts.assertNotEmpty(target, args);

            assertSame(target, result);
            verify(arrayAsserts, times(0)).newE(any());
        }
    }

    @Nested
    class TestAssertArrayNotEmptyWithData {

        @Test
        void should_throw_exception_given_target_is_empty() {
            final BaseException thrownException = assertThrows(BaseException.class,
                () -> arrayAsserts.assertNotEmptyWithData(null, data, args)
            );

            assertSame(suppliedException, thrownException);
            verify(arrayAsserts, times(1)).newEWithData(data, args);
        }

        @Test
        void should_not_throw_exception_given_target_is_not_empty() {
            final Object[] target = { new Object() };
            final Object[] result = arrayAsserts.assertNotEmptyWithData(target, data, args);

            assertSame(target, result);
            verify(arrayAsserts, times(0)).newEWithData(any(), any());
        }
    }

    @Nested
    class TestAssertArrayNotEmptyWithDataSupplier {

        @Test
        void should_throw_exception_given_target_is_empty() {
            final BaseException thrownException = assertThrows(BaseException.class,
                () -> arrayAsserts.assertNotEmptyWithDataSupplier(null, () -> data, args)
            );

            assertSame(suppliedException, thrownException);
            verify(arrayAsserts, times(1)).newEWithData(data, args);
        }

        @Test
        void should_not_throw_exception_given_target_is_not_empty() {
            final Object[] target = { new Object() };
            final Object[] result = arrayAsserts.assertNotEmptyWithDataSupplier(target, () -> data, args);

            assertSame(target, result);
            verify(arrayAsserts, times(0)).newEWithData(any(), any());
        }
    }

    @Nested
    class TestAssertArrayEmpty {

        @Test
        void should_throw_exception_given_target_is_not_empty() {
            final BaseException thrownException = assertThrows(BaseException.class,
                () -> arrayAsserts.assertEmpty(new Object[]{ new Object() }, args)
            );

            assertSame(suppliedException, thrownException);
            verify(arrayAsserts, times(1)).newE(args);
        }

        @Test
        void should_not_throw_exception_given_target_is_empty() {
            final Object[] target = new Object[]{};
            final Object[] result = arrayAsserts.assertEmpty(target, args);

            assertSame(target, result);
            verify(arrayAsserts, times(0)).newE(any());
        }
    }

    @Nested
    class TestAssertArrayEmptyWithData {

        @Test
        void should_throw_exception_given_target_is_not_empty() {
            final BaseException thrownException = assertThrows(BaseException.class,
                () -> arrayAsserts.assertEmptyWithData(new Object[]{ new Object() }, data, args)
            );

            assertSame(suppliedException, thrownException);
            verify(arrayAsserts, times(1)).newEWithData(data, args);
        }

        @Test
        void should_not_throw_exception_given_target_is_empty() {
            final Object[] target = new Object[]{};
            final Object[] result = arrayAsserts.assertEmptyWithData(target, data, args);

            assertSame(target, result);
            verify(arrayAsserts, times(0)).newEWithData(any(), any());
        }
    }

    @Nested
    class TestAssertArrayEmptyWithDataSupplier {

        @Test
        void should_throw_exception_given_target_is_not_empty() {
            final BaseException thrownException = assertThrows(BaseException.class,
                () -> arrayAsserts.assertEmptyWithDataSupplier(new Object[]{ new Object() }, () -> data, args)
            );

            assertSame(suppliedException, thrownException);
            verify(arrayAsserts, times(1)).newEWithData(data, args);
        }

        @Test
        void should_not_throw_exception_given_target_is_empty() {
            final Object[] target = new Object[]{};
            final Object[] result = arrayAsserts.assertEmptyWithDataSupplier(target, () -> data, args);

            assertSame(target, result);
            verify(arrayAsserts, times(0)).newEWithData(any(), any());
        }
    }
}
