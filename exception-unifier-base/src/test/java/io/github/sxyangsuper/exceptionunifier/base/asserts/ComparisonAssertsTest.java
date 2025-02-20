package io.github.sxyangsuper.exceptionunifier.base.asserts;

import io.github.sxyangsuper.exceptionunifier.base.BaseException;
import io.github.sxyangsuper.exceptionunifier.base.fixture.TestException;
import io.github.sxyangsuper.exceptionunifier.base.fixture.TestExceptionEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ComparisonAssertsTest {
    IComparisonAsserts<BaseException> comparisonAsserts;
    BaseException suppliedException;
    Object[] args;
    RuntimeException cause;
    Object data;

    @BeforeEach
    void setUp() {
        //noinspection unchecked
        comparisonAsserts = Mockito.spy(IComparisonAsserts.class);

        suppliedException = TestException.of(TestExceptionEnum.ERROR_1, "Test");
        args = new Object[]{ new Object() };
        cause = new RuntimeException();
        data = new Object();

        when(comparisonAsserts.newE(args)).thenReturn(suppliedException);
        when(comparisonAsserts.newEWithCause(cause, args)).thenReturn(suppliedException);
        when(comparisonAsserts.newEWithData(data, args)).thenReturn(suppliedException);
        when(comparisonAsserts.newEWithCauseAndData(cause, data, args)).thenReturn(suppliedException);
    }


    @Nested
    class TestAssertEqual {

        @Test
        void should_throw_exception_given_source_not_equal_target() {
            final int source = 0;
            final int target = 1;

            final BaseException thrownException = assertThrows(BaseException.class, () -> comparisonAsserts.assertEqual(source, target, args));

            assertSame(suppliedException, thrownException);
            verify(comparisonAsserts, times(1)).newE(args);
        }

        @Test
        void should_not_throw_exception_given_source_equal_target() {
            final int source = 0;
            final int target = 0;

            assertDoesNotThrow(() -> comparisonAsserts.assertEqual(source, target, args));

            verify(comparisonAsserts, times(0)).newE(any());
        }
    }

    @Nested
    class TestAssertEqualWithData {

        @Test
        void should_throw_exception_given_source_not_equal_target() {
            final int source = 0;
            final int target = 1;

            final BaseException thrownException = assertThrows(BaseException.class, () -> comparisonAsserts.assertEqualWithData(source, target, data, args));

            assertSame(suppliedException, thrownException);
            verify(comparisonAsserts, times(1)).newEWithData(data, args);
        }

        @Test
        void should_not_throw_exception_given_source_equal_target() {
            final int source = 0;
            final int target = 0;

            assertDoesNotThrow(() -> comparisonAsserts.assertEqualWithData(source, target, data, args));

            verify(comparisonAsserts, times(0)).newEWithData(any(), any());
        }
    }

    @Nested
    class TestAssertNotEqual {

        @Test
        void should_throw_exception_given_source_equal_target() {
            final int source = 0;
            final int target = 0;

            final BaseException thrownException = assertThrows(BaseException.class, () -> comparisonAsserts.assertNotEqual(source, target, args));

            assertSame(suppliedException, thrownException);
            verify(comparisonAsserts, times(1)).newE(args);
        }

        @Test
        void should_not_throw_exception_given_source_not_equal_target() {
            final int source = 0;
            final int target = 1;

            assertDoesNotThrow(() -> comparisonAsserts.assertNotEqual(source, target, args));

            verify(comparisonAsserts, times(0)).newE(any());
        }
    }

    @Nested
    class TestAssertNotEqualWithData {

        @Test
        void should_throw_exception_given_source_equal_target() {
            final int source = 0;
            final int target = 0;

            final BaseException thrownException = assertThrows(BaseException.class, () -> comparisonAsserts.assertNotEqualWithData(source, target, data, args));

            assertSame(suppliedException, thrownException);
            verify(comparisonAsserts, times(1)).newEWithData(data, args);
        }

        @Test
        void should_not_throw_exception_given_source_not_equal_target() {
            final int source = 0;
            final int target = 1;

            assertDoesNotThrow(() -> comparisonAsserts.assertNotEqualWithData(source, target, data, args));

            verify(comparisonAsserts, times(0)).newEWithData(any(), any());
        }
    }
}
