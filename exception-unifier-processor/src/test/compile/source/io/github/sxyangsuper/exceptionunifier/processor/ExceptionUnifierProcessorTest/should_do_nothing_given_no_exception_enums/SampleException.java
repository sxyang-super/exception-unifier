package source.io.github.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.should_do_nothing_given_no_exception_enums;

import io.github.sxyangsuper.exceptionunifier.base.BaseException;
import lombok.Getter;

@Getter
public class SampleException extends BaseException {
    SampleException() {
        super();
    }
}
