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

class CharSequenceAssertsTest {

    ICharSequenceAsserts<BaseException> charSequenceAsserts;
    BaseException suppliedException;
    Object[] args;
    RuntimeException cause;
    Object data;

    @BeforeEach
    void setUp() {
        //noinspection unchecked
        charSequenceAsserts = Mockito.spy(ICharSequenceAsserts.class);

        suppliedException = TestException.of(ERROR_1, "Test");
        args = new Object[]{ new Object() };
        cause = new RuntimeException();
        data = new Object();

        when(charSequenceAsserts.newE(args)).thenReturn(suppliedException);
        when(charSequenceAsserts.newEWithCause(cause, args)).thenReturn(suppliedException);
        when(charSequenceAsserts.newEWithData(data, args)).thenReturn(suppliedException);
        when(charSequenceAsserts.newEWithCauseAndData(cause, data, args)).thenReturn(suppliedException);
    }

    @Nested
    class TestAssertNotBlank {

        @Test
        void should_throw_exception_given_target_is_blank() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> charSequenceAsserts.assertNotBlank("  ", args));

            assertSame(suppliedException, thrownException);
            verify(charSequenceAsserts, times(1)).newE(args);
        }

        @Test
        void should_throw_exception_given_target_is_not_char_sequence() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> charSequenceAsserts.assertNotBlank(new Object(), args));

            assertSame(suppliedException, thrownException);
            verify(charSequenceAsserts, times(1)).newE(args);
        }

        @Test
        void should_not_throw_exception_given_target_is_not_blank() {
            final Object target = "A";
            final CharSequence result = charSequenceAsserts.assertNotBlank(target, args);

            assertSame(target, result);
            verify(charSequenceAsserts, times(0)).newE(any());
        }
    }

    @Nested
    class TestAssertNotBlankWithData {

        @Test
        void should_throw_exception_given_target_is_blank() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> charSequenceAsserts.assertNotBlankWithData("  ", data, args));

            assertSame(suppliedException, thrownException);
            verify(charSequenceAsserts, times(1)).newEWithData(data, args);
        }

        @Test
        void should_not_throw_exception_given_target_is_not_blank() {
            final Object target = "A";
            final CharSequence result = charSequenceAsserts.assertNotBlankWithData(target, data, args);

            assertSame(target, result);
            verify(charSequenceAsserts, times(0)).newEWithData(any(), any());
        }
    }

    @Nested
    class TestAssertNotBlankWithDataSupplier {


        @Test
        void should_throw_exception_given_target_is_blank() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> charSequenceAsserts.assertNotBlankWithDataSupplier("  ", () -> data, args));

            assertSame(suppliedException, thrownException);
            verify(charSequenceAsserts, times(1)).newEWithData(data, args);
        }

        @Test
        void should_not_throw_exception_given_target_is_not_blank() {
            final Object target = "A";
            final CharSequence result = charSequenceAsserts.assertNotBlankWithDataSupplier(target, () -> data, args);

            assertSame(target, result);
            verify(charSequenceAsserts, times(0)).newEWithData(any(), any());
        }
    }

    @Nested
    class TestAssertBlank {

        @Test
        void should_throw_exception_given_target_is_not_blank() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> charSequenceAsserts.assertBlank("A", args));

            assertSame(suppliedException, thrownException);
            verify(charSequenceAsserts, times(1)).newE(args);
        }

        @Test
        void should_throw_exception_given_target_is_not_char_sequence() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> charSequenceAsserts.assertBlank(new Object(), args));

            assertSame(suppliedException, thrownException);
            verify(charSequenceAsserts, times(1)).newE(args);
        }

        @Test
        void should_not_throw_exception_given_target_is_blank() {
            final String target = "  ";
            final CharSequence result = charSequenceAsserts.assertBlank(target, args);

            assertSame(target, result);
            verify(charSequenceAsserts, times(0)).newE(any());
        }
    }

    @Nested
    class TestAssertBlankWithData {

        @Test
        void should_throw_exception_given_target_is_not_blank() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> charSequenceAsserts.assertBlankWithData("A", data, args));

            assertSame(suppliedException, thrownException);
            verify(charSequenceAsserts, times(1)).newEWithData(data, args);
        }

        @Test
        void should_not_throw_exception_given_target_is_blank() {
            final String target = "  ";
            final CharSequence result = charSequenceAsserts.assertBlankWithData(target, data, args);

            assertSame(target, result);
            verify(charSequenceAsserts, times(0)).newEWithData(any(), any());
        }
    }

    @Nested
    class TestAssertBlankWithDataSupplier {

        @Test
        void should_throw_exception_given_target_is_not_blank() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> charSequenceAsserts.assertBlankWithDataSupplier("A", () -> data, args));

            assertSame(suppliedException, thrownException);
            verify(charSequenceAsserts, times(1)).newEWithData(data, args);
        }

        @Test
        void should_not_throw_exception_given_target_is_blank() {
            final String target = "  ";
            final CharSequence result = charSequenceAsserts.assertBlankWithDataSupplier(target, () -> data, args);

            assertSame(target, result);
            verify(charSequenceAsserts, times(0)).newEWithData(any(), any());
        }
    }
}
