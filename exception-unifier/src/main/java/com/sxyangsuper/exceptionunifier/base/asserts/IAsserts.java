package com.sxyangsuper.exceptionunifier.base.asserts;

import com.sxyangsuper.exceptionunifier.base.BaseException;

public interface IAsserts<E extends BaseException> extends IArrayAsserts<E>, IBaseAsserts<E>, ICharSequenceAsserts<E>, IComparisonAsserts<E>, IFunctionalAsserts<E>, IIterableAsserts<E>, IThrowAsserts<E> {
}
