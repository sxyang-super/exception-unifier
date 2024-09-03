package source.com.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.RemoteExceptionCodePrefixSupplierTest.should_throw_exception_given_get_exception_code_prefix_from_wrong_remote;

import com.sxyangsuper.exceptionunifier.base.BaseException;
import com.sxyangsuper.exceptionunifier.base.IExceptionEnumAsserts;

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
