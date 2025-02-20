package io.github.sxyangsuper.exceptionunifier.base.asserts;

import io.github.sxyangsuper.exceptionunifier.base.BaseException;
import io.github.sxyangsuper.exceptionunifier.base.fixture.TestException;
import io.github.sxyangsuper.exceptionunifier.base.fixture.TestExceptionEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FunctionAssertsTest {
    IFunctionalAsserts<BaseException> functionalAsserts;
    BaseException suppliedException;
    Object[] args;
    RuntimeException cause;
    Object data;

    @BeforeEach
    void setUp() {
        //noinspection unchecked
        functionalAsserts = Mockito.spy(IFunctionalAsserts.class);

        suppliedException = TestException.of(TestExceptionEnum.ERROR_1, "Test");
        args = new Object[]{ new Object() };
        cause = new RuntimeException();
        data = new Object();

        when(functionalAsserts.newE(args)).thenReturn(suppliedException);
        when(functionalAsserts.newEWithCause(cause, args)).thenReturn(suppliedException);
        when(functionalAsserts.newEWithData(data, args)).thenReturn(suppliedException);
        when(functionalAsserts.newEWithCauseAndData(cause, data, args)).thenReturn(suppliedException);
    }


    @Nested
    class TestAssertSupplySuccessfully {

        @Test
        void should_throw_exception_given_supply_fails() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> functionalAsserts.assertSupplySuccessfully(() -> {
                throw cause;
            }, args));

            assertSame(suppliedException, thrownException);
            verify(functionalAsserts, times(1)).newEWithCause(cause, args);
        }

        @Test
        void should_return_supplied_value_given_supply_successfully() {
            final Object suppliedTarget = new Object();
            final Object result = functionalAsserts.assertSupplySuccessfully(() -> suppliedTarget, args);

            assertSame(suppliedTarget, result);
            verify(functionalAsserts, times(0)).newEWithCause(any(), any());
        }
    }

    @Nested
    class TestAssertSupplySuccessfullyWithData {

        @Test
        void should_throw_exception_given_supply_fails() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> functionalAsserts.assertSupplySuccessfullyWithData(() -> {
                throw cause;
            }, data, args));

            assertSame(suppliedException, thrownException);
            verify(functionalAsserts, times(1)).newEWithCauseAndData(cause, data, args);
        }

        @Test
        void should_return_supplied_value_given_supply_successfully() {
            final Object suppliedTarget = new Object();
            final Object result = functionalAsserts.assertSupplySuccessfullyWithData(() -> suppliedTarget, data, args);

            assertSame(suppliedTarget, result);
            verify(functionalAsserts, times(0)).newEWithCauseAndData(any(), any(), any());
        }
    }

    @Nested
    class TestAssertSupplySuccessfullyWithDataSupplier {

        @Test
        void should_throw_exception_given_supply_fails() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> functionalAsserts.assertSupplySuccessfullyWithDataSupplier(() -> {
                throw cause;
            }, () -> data, args));

            assertSame(suppliedException, thrownException);
            verify(functionalAsserts, times(1)).newEWithCauseAndData(cause, data, args);
        }

        @Test
        void should_return_supplied_value_given_supply_successfully() {
            final Object suppliedTarget = new Object();
            final Object result = functionalAsserts.assertSupplySuccessfullyWithDataSupplier(() -> suppliedTarget, () -> data, args);

            assertSame(suppliedTarget, result);
            verify(functionalAsserts, times(0)).newEWithCauseAndData(any(), any(), any());
        }
    }

    @Nested
    class TestAssertExecuteSuccessfully {

        @Test
        void should_throw_exception_given_execute_fails() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> functionalAsserts.assertExecuteSuccessfully(() -> {
                throw cause;
            }, args));

            assertSame(suppliedException, thrownException);
            verify(functionalAsserts, times(1)).newEWithCause(cause, args);
        }

        @Test
        void should_return_supplied_value_given_execute_successfully() {
            functionalAsserts.assertExecuteSuccessfully(() -> {
            }, args);

            verify(functionalAsserts, times(0)).newEWithCause(any(), any());
        }
    }

    @Nested
    class TestAssertExecuteSuccessfullyWithData {

        @Test
        void should_throw_exception_given_execute_fails() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> functionalAsserts.assertExecuteSuccessfullyWithData(() -> {
                throw cause;
            }, data, args));

            assertSame(suppliedException, thrownException);
            verify(functionalAsserts, times(1)).newEWithCauseAndData(cause, data, args);
        }

        @Test
        void should_return_supplied_value_given_execute_successfully() {
            functionalAsserts.assertExecuteSuccessfullyWithData(() -> {
            }, data, args);

            verify(functionalAsserts, times(0)).newEWithCauseAndData(any(), any(), any());
        }
    }

    @Nested
    class TestAssertExecuteSuccessfullyWithDataSupplier {

        @Test
        void should_throw_exception_given_execute_fails() {
            final BaseException thrownException = assertThrows(BaseException.class, () -> functionalAsserts.assertExecuteSuccessfullyWithDataSupplier(() -> {
                throw cause;
            }, () -> data, args));

            assertSame(suppliedException, thrownException);
            verify(functionalAsserts, times(1)).newEWithCauseAndData(cause, data, args);
        }

        @Test
        void should_return_supplied_value_given_execute_successfully() {
            functionalAsserts.assertExecuteSuccessfullyWithDataSupplier(() -> {
            }, () -> data, args);

            verify(functionalAsserts, times(0)).newEWithCauseAndData(any(), any(), any());
        }
    }
}
