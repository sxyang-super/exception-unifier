package com.sxyangsuper.exceptionunifier.base.asserts;

import com.sxyangsuper.exceptionunifier.base.BaseException;
import com.sxyangsuper.exceptionunifier.base.fixture.TestException;
import com.sxyangsuper.exceptionunifier.base.fixture.TestExceptionEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PatternAssertsTest {

    IPatternAsserts<BaseException> patternAsserts;
    BaseException suppliedException;
    Object[] args;
    RuntimeException cause;
    Object data;

    @BeforeEach
    void setUp() {
        //noinspection unchecked
        patternAsserts = Mockito.spy(IPatternAsserts.class);

        suppliedException = TestException.of(TestExceptionEnum.ERROR_1, "Test");
        args = new Object[]{ new Object() };
        cause = new RuntimeException();
        data = new Object();

        when(patternAsserts.newE(args)).thenReturn(suppliedException);
        when(patternAsserts.newEWithCause(cause, args)).thenReturn(suppliedException);
        when(patternAsserts.newEWithData(data, args)).thenReturn(suppliedException);
        when(patternAsserts.newEWithCauseAndData(cause, data, args)).thenReturn(suppliedException);
    }


    @Nested
    class TestMatches {
        Pattern pattern;
        final String notMatchTarget = "B";
        final String matchTarget = "A";

        @BeforeEach
        void setUp() {
            pattern = Pattern.compile("^A$");
        }

        @Nested
        class TestAssertMatches {

            @Test
            void should_throw_exception_given_target_not_match_pattern() {
                final BaseException thrownException = assertThrows(BaseException.class, () -> patternAsserts.assertMatches(notMatchTarget, pattern, args));

                assertSame(suppliedException, thrownException);
                verify(patternAsserts, times(1)).newE(args);
            }

            @Test
            void should_throw_exception_given_pattern_is_null() {
                final BaseException thrownException = assertThrows(BaseException.class, () -> patternAsserts.assertMatches(matchTarget, null, args));

                assertSame(suppliedException, thrownException);
                verify(patternAsserts, times(1)).newE(args);
            }

            @Test
            void should_throw_exception_given_target_is_null() {
                final BaseException thrownException = assertThrows(BaseException.class, () -> patternAsserts.assertMatches(null, pattern, args));

                assertSame(suppliedException, thrownException);
                verify(patternAsserts, times(1)).newE(args);
            }

            @Test
            void should_not_throw_exception_given_target_match_pattern() {
                final CharSequence result = patternAsserts.assertMatches(matchTarget, pattern, args);

                assertSame(matchTarget, result);
                verify(patternAsserts, times(0)).newE(any());
            }
        }

        @Nested
        class TestAssertMatchesWithData {

            @Test
            void should_throw_exception_given_target_not_match_pattern() {
                final BaseException thrownException = assertThrows(BaseException.class, () -> patternAsserts.assertMatchesWithData(notMatchTarget, pattern, data, args));

                assertSame(suppliedException, thrownException);
                verify(patternAsserts, times(1)).newEWithData(data, args);
            }

            @Test
            void should_not_throw_exception_given_target_match_pattern() {
                final CharSequence result = patternAsserts.assertMatchesWithData(matchTarget, pattern, data, args);

                assertSame(matchTarget, result);
                verify(patternAsserts, times(0)).newEWithData(any());
            }
        }

        @Nested
        class TestAssertMatchesWithDataSupplier {

            @Test
            void should_throw_exception_given_target_not_match_pattern() {
                final BaseException thrownException = assertThrows(BaseException.class, () -> patternAsserts.assertMatchesWithDataSupplier(notMatchTarget, pattern, () -> data, args));

                assertSame(suppliedException, thrownException);
                verify(patternAsserts, times(1)).newEWithData(data, args);
            }

            @Test
            void should_not_throw_exception_given_target_match_pattern() {
                final CharSequence result = patternAsserts.assertMatchesWithDataSupplier(matchTarget, pattern, () -> data, args);

                assertSame(matchTarget, result);
                verify(patternAsserts, times(0)).newEWithData(any());
            }
        }

        @Nested
        class TestAssertNotMatches {

            @Test
            void should_throw_exception_given_target_match_pattern() {
                final BaseException thrownException = assertThrows(BaseException.class, () -> patternAsserts.assertNotMatches(matchTarget, pattern, args));

                assertSame(suppliedException, thrownException);
                verify(patternAsserts, times(1)).newE(args);
            }

            @Test
            void should_not_throw_exception_given_target_not_match_pattern() {
                final CharSequence result = patternAsserts.assertNotMatches(notMatchTarget, pattern, args);

                assertSame(notMatchTarget, result);
                verify(patternAsserts, times(0)).newE(any());
            }
        }

        @Nested
        class TestAssertNotMatchesWithData {

            @Test
            void should_throw_exception_given_target_match_pattern() {
                final BaseException thrownException = assertThrows(BaseException.class, () -> patternAsserts.assertNotMatchesWithData(matchTarget, pattern, data, args));

                assertSame(suppliedException, thrownException);
                verify(patternAsserts, times(1)).newEWithData(data, args);
            }

            @Test
            void should_not_throw_exception_given_target_not_match_pattern() {
                final CharSequence result = patternAsserts.assertNotMatchesWithData(notMatchTarget, pattern, data, args);

                assertSame(notMatchTarget, result);
                verify(patternAsserts, times(0)).newEWithData(any(), any());
            }
        }

        @Nested
        class TestAssertNotMatchesWithDataSupplier {

            @Test
            void should_throw_exception_given_target_match_pattern() {
                final BaseException thrownException = assertThrows(BaseException.class, () -> patternAsserts.assertNotMatchesWithDataSupplier(matchTarget, pattern, () -> data, args));

                assertSame(suppliedException, thrownException);
                verify(patternAsserts, times(1)).newEWithData(data, args);
            }

            @Test
            void should_not_throw_exception_given_target_not_match_pattern() {
                final CharSequence result = patternAsserts.assertNotMatchesWithDataSupplier(notMatchTarget, pattern, () -> data, args);

                assertSame(notMatchTarget, result);
                verify(patternAsserts, times(0)).newEWithData(any(), any());
            }
        }
    }
}
