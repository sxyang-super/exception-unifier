package com.sxyangsuper.exceptionunifier.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

class ExceptionSupplierTest {

    IExceptionSupplier<BaseException> exceptionSupplier;
    BaseException suppliedException;
    Object[] args;
    Exception cause;
    Object data;

    @BeforeEach
    void setUp() {
        //noinspection unchecked
        exceptionSupplier = Mockito.spy(IExceptionSupplier.class);

        suppliedException = new BaseException();
        args = new Object[]{ new Object() };
        cause = new RuntimeException();
        data = new Object();

        when(exceptionSupplier.newE(args)).thenReturn(suppliedException);
        when(exceptionSupplier.newEWithCause(cause, args)).thenReturn(suppliedException);
        when(exceptionSupplier.newEWithData(data, args)).thenReturn(suppliedException);
        when(exceptionSupplier.newEWithCauseAndData(cause, data, args)).thenReturn(suppliedException);
    }

    @Test
    void newESupplier() {
        final BaseException resultException = exceptionSupplier.newESupplier(args).get();

        Assertions.assertSame(resultException, suppliedException);
        Mockito.verify(exceptionSupplier, Mockito.times(1)).newE(args);
    }

    @Test
    void newEWithCauseSupplier() {
        final BaseException resultException = exceptionSupplier.newEWithCauseSupplier(cause, args).get();

        Assertions.assertSame(resultException, suppliedException);
        Mockito.verify(exceptionSupplier, Mockito.times(1)).newEWithCauseSupplier(cause, args);
    }

    @Test
    void newEWithDataSupplier() {
        final BaseException resultException = exceptionSupplier.newEWithDataSupplier(data, args).get();

        Assertions.assertSame(resultException, suppliedException);
        Mockito.verify(exceptionSupplier, Mockito.times(1)).newEWithDataSupplier(data, args);
    }

    @Test
    void newEWithCauseAndDataSupplier() {
        final BaseException resultException = exceptionSupplier.newEWithCauseAndDataSupplier(cause, data, args).get();

        Assertions.assertSame(resultException, suppliedException);
        Mockito.verify(exceptionSupplier, Mockito.times(1)).newEWithCauseAndDataSupplier(cause, data, args);
    }
}
