package io.github.sxyangsuper.exceptionunifier.base.asserts;

import io.github.sxyangsuper.exceptionunifier.base.BaseException;
import io.github.sxyangsuper.exceptionunifier.base.fixture.TestException;
import io.github.sxyangsuper.exceptionunifier.base.fixture.TestExceptionEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IterableAssertsTest {

    IIterableAsserts<BaseException> iterableAsserts;
    BaseException suppliedException;
    Object[] args;
    RuntimeException cause;
    Object data;

    @BeforeEach
    void setUp() {
        //noinspection unchecked
        iterableAsserts = Mockito.spy(IIterableAsserts.class);

        suppliedException = TestException.of(TestExceptionEnum.ERROR_1, "Test");
        args = new Object[]{ new Object() };
        cause = new RuntimeException();
        data = new Object();

        when(iterableAsserts.newE(args)).thenReturn(suppliedException);
        when(iterableAsserts.newEWithCause(cause, args)).thenReturn(suppliedException);
        when(iterableAsserts.newEWithData(data, args)).thenReturn(suppliedException);
        when(iterableAsserts.newEWithCauseAndData(cause, data, args)).thenReturn(suppliedException);
    }

    @Nested
    class TestAssertIterableNotEmpty {

        @Test
        void should_throw_exception_given_target_is_empty() {
            final BaseException thrownException = assertThrows(BaseException.class,
                () -> iterableAsserts.assertNotEmpty(null, args)
            );

            assertSame(suppliedException, thrownException);
            verify(iterableAsserts, times(1)).newE(args);
        }

        @Test
        void should_not_throw_exception_given_target_is_not_empty() {
            final List<Object> target = Collections.singletonList(new Object());
            final Iterable<?> result = iterableAsserts.assertNotEmpty(target, args);

            assertSame(target, result);
            verify(iterableAsserts, times(0)).newE(any());
        }
    }

    @Nested
    class TestAssertIterableNotEmptyWithData {

        @Test
        void should_throw_exception_given_target_is_empty() {
            final BaseException thrownException = assertThrows(BaseException.class,
                () -> iterableAsserts.assertNotEmptyWithData(null, data, args)
            );

            assertSame(suppliedException, thrownException);
            verify(iterableAsserts, times(1)).newEWithData(data, args);
        }

        @Test
        void should_not_throw_exception_given_target_is_not_empty() {
            final List<Object> target = Collections.singletonList(new Object());
            final Iterable<?> result = iterableAsserts.assertNotEmptyWithData(target, data, args);

            assertSame(target, result);
            verify(iterableAsserts, times(0)).newE(any(), any());
        }
    }

    @Nested
    class TestAssertIterableNotEmptyWithDataSupplier {

        @Test
        void should_throw_exception_given_target_is_empty() {
            final BaseException thrownException = assertThrows(BaseException.class,
                () -> iterableAsserts.assertNotEmptyWithDataSupplier(null, () -> data, args)
            );

            assertSame(suppliedException, thrownException);
            verify(iterableAsserts, times(1)).newEWithData(data, args);
        }

        @Test
        void should_not_throw_exception_given_target_is_not_empty() {
            final List<Object> target = Collections.singletonList(new Object());
            final Iterable<?> result = iterableAsserts.assertNotEmptyWithDataSupplier(target, () -> data, args);

            assertSame(target, result);
            verify(iterableAsserts, times(0)).newE(any(), any());
        }
    }

    @Nested
    class TestAssertIterableEmpty {

        @Test
        void should_throw_exception_given_target_is_not_empty() {
            final BaseException thrownException = assertThrows(BaseException.class,
                () -> iterableAsserts.assertEmpty(Collections.singletonList(new Object()), args)
            );

            assertSame(suppliedException, thrownException);
            verify(iterableAsserts, times(1)).newE(args);
        }

        @Test
        void should_not_throw_exception_given_target_is_empty() {
            assertDoesNotThrow(() -> iterableAsserts.assertEmpty(null, args));

            verify(iterableAsserts, times(0)).newE(any());
        }
    }

    @Nested
    class TestAssertIterableEmptyWithData {

        @Test
        void should_throw_exception_given_target_is_not_empty() {
            final BaseException thrownException = assertThrows(BaseException.class,
                () -> iterableAsserts.assertEmptyWithData(Collections.singletonList(new Object()), data, args)
            );

            assertSame(suppliedException, thrownException);
            verify(iterableAsserts, times(1)).newEWithData(data, args);
        }

        @Test
        void should_not_throw_exception_given_target_is_empty() {
            assertDoesNotThrow(() -> iterableAsserts.assertEmptyWithData(null, data, args));

            verify(iterableAsserts, times(0)).newEWithData(any(), any());
        }
    }

    @Nested
    class TestAssertIterableEmptyWithDataSupplier {

        @Test
        void should_throw_exception_given_target_is_not_empty() {
            final BaseException thrownException = assertThrows(BaseException.class,
                () -> iterableAsserts.assertEmptyWithDataSupplier(Collections.singletonList(new Object()), () -> data, args)
            );

            assertSame(suppliedException, thrownException);
            verify(iterableAsserts, times(1)).newEWithData(data, args);
        }

        @Test
        void should_not_throw_exception_given_target_is_empty() {
            assertDoesNotThrow(() -> iterableAsserts.assertEmptyWithDataSupplier(null, () -> data, args));

            verify(iterableAsserts, times(0)).newEWithData(any(), any());
        }
    }
}
