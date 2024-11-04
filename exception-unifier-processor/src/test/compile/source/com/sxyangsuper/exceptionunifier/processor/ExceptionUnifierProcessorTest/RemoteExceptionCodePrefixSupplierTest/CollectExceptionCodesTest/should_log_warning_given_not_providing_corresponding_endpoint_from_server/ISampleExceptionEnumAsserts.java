package source.com.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.RemoteExceptionCodePrefixSupplierTest.CollectExceptionCodesTest.should_log_warning_given_not_providing_corresponding_endpoint_from_server;

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
