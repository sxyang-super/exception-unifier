package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.fixture.CheckHandlerTest.CheckExceptionEnumsTest.should_succeed_given_no_duplicate_code_within_all_exception_enums;

import io.github.sxyangsuper.exceptionunifier.base.BaseException;
import io.github.sxyangsuper.exceptionunifier.base.IExceptionEnumAsserts;

public interface ISampleExceptionEnumAsserts extends IExceptionEnumAsserts<SampleException> {

    @Override
    default SampleException newE(Object... objects) {
        return BaseException.of(SampleException::new, this, objects);
    }

    @Override
    default SampleException newEWithCause(Throwable throwable, Object... objects) {
        return BaseException.ofWithCause(SampleException::new, this, throwable, objects);
    }

    @Override
    default SampleException newEWithData(Object o, Object... objects) {
        return BaseException.ofWithData(SampleException::new, this, o, objects);
    }

    @Override
    default SampleException newEWithCauseAndData(Throwable throwable, Object o, Object... objects) {
        return BaseException.ofWithCauseAndData(SampleException::new, this, throwable, o, objects);
    }
}
